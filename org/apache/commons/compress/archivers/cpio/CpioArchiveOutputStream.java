package org.apache.commons.compress.archivers.cpio;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.cpio.CpioArchiveEntry;
import org.apache.commons.compress.archivers.cpio.CpioConstants;
import org.apache.commons.compress.archivers.cpio.CpioUtil;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.utils.ArchiveUtils;

public class CpioArchiveOutputStream extends ArchiveOutputStream implements CpioConstants {
   private CpioArchiveEntry entry;
   private boolean closed;
   private boolean finished;
   private final short entryFormat;
   private final HashMap names;
   private long crc;
   private long written;
   private final OutputStream out;
   private final int blockSize;
   private long nextArtificalDeviceAndInode;
   private final ZipEncoding encoding;

   public CpioArchiveOutputStream(OutputStream out, short format) {
      this(out, format, 512, "US-ASCII");
   }

   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize) {
      this(out, format, blockSize, "US-ASCII");
   }

   public CpioArchiveOutputStream(OutputStream out, short format, int blockSize, String encoding) {
      this.closed = false;
      this.names = new HashMap();
      this.crc = 0L;
      this.nextArtificalDeviceAndInode = 1L;
      this.out = out;
      switch(format) {
      case 1:
      case 2:
      case 4:
      case 8:
         this.entryFormat = format;
         this.blockSize = blockSize;
         this.encoding = ZipEncodingHelper.getZipEncoding(encoding);
         return;
      case 3:
      case 5:
      case 6:
      case 7:
      default:
         throw new IllegalArgumentException("Unknown format: " + format);
      }
   }

   public CpioArchiveOutputStream(OutputStream out) {
      this(out, (short)1);
   }

   public CpioArchiveOutputStream(OutputStream out, String encoding) {
      this(out, (short)1, 512, encoding);
   }

   private void ensureOpen() throws IOException {
      if(this.closed) {
         throw new IOException("Stream closed");
      }
   }

   public void putArchiveEntry(ArchiveEntry entry) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         CpioArchiveEntry e = (CpioArchiveEntry)entry;
         this.ensureOpen();
         if(this.entry != null) {
            this.closeArchiveEntry();
         }

         if(e.getTime() == -1L) {
            e.setTime(System.currentTimeMillis() / 1000L);
         }

         short format = e.getFormat();
         if(format != this.entryFormat) {
            throw new IOException("Header format: " + format + " does not match existing format: " + this.entryFormat);
         } else if(this.names.put(e.getName(), e) != null) {
            throw new IOException("duplicate entry: " + e.getName());
         } else {
            this.writeHeader(e);
            this.entry = e;
            this.written = 0L;
         }
      }
   }

   private void writeHeader(CpioArchiveEntry e) throws IOException {
      switch(e.getFormat()) {
      case 1:
         this.out.write(ArchiveUtils.toAsciiBytes("070701"));
         this.count(6);
         this.writeNewEntry(e);
         break;
      case 2:
         this.out.write(ArchiveUtils.toAsciiBytes("070702"));
         this.count(6);
         this.writeNewEntry(e);
         break;
      case 3:
      case 5:
      case 6:
      case 7:
      default:
         throw new IOException("unknown format " + e.getFormat());
      case 4:
         this.out.write(ArchiveUtils.toAsciiBytes("070707"));
         this.count(6);
         this.writeOldAsciiEntry(e);
         break;
      case 8:
         boolean swapHalfWord = true;
         this.writeBinaryLong(29127L, 2, swapHalfWord);
         this.writeOldBinaryEntry(e, swapHalfWord);
      }

   }

   private void writeNewEntry(CpioArchiveEntry entry) throws IOException {
      long inode = entry.getInode();
      long devMin = entry.getDeviceMin();
      if("TRAILER!!!".equals(entry.getName())) {
         devMin = 0L;
         inode = 0L;
      } else if(inode == 0L && devMin == 0L) {
         inode = this.nextArtificalDeviceAndInode & -1L;
         devMin = this.nextArtificalDeviceAndInode++ >> 32 & -1L;
      } else {
         this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 4294967296L * devMin) + 1L;
      }

      this.writeAsciiLong(inode, 8, 16);
      this.writeAsciiLong(entry.getMode(), 8, 16);
      this.writeAsciiLong(entry.getUID(), 8, 16);
      this.writeAsciiLong(entry.getGID(), 8, 16);
      this.writeAsciiLong(entry.getNumberOfLinks(), 8, 16);
      this.writeAsciiLong(entry.getTime(), 8, 16);
      this.writeAsciiLong(entry.getSize(), 8, 16);
      this.writeAsciiLong(entry.getDeviceMaj(), 8, 16);
      this.writeAsciiLong(devMin, 8, 16);
      this.writeAsciiLong(entry.getRemoteDeviceMaj(), 8, 16);
      this.writeAsciiLong(entry.getRemoteDeviceMin(), 8, 16);
      this.writeAsciiLong((long)(entry.getName().length() + 1), 8, 16);
      this.writeAsciiLong(entry.getChksum(), 8, 16);
      this.writeCString(entry.getName());
      this.pad(entry.getHeaderPadCount());
   }

   private void writeOldAsciiEntry(CpioArchiveEntry entry) throws IOException {
      long inode = entry.getInode();
      long device = entry.getDevice();
      if("TRAILER!!!".equals(entry.getName())) {
         device = 0L;
         inode = 0L;
      } else if(inode == 0L && device == 0L) {
         inode = this.nextArtificalDeviceAndInode & 262143L;
         device = this.nextArtificalDeviceAndInode++ >> 18 & 262143L;
      } else {
         this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 262144L * device) + 1L;
      }

      this.writeAsciiLong(device, 6, 8);
      this.writeAsciiLong(inode, 6, 8);
      this.writeAsciiLong(entry.getMode(), 6, 8);
      this.writeAsciiLong(entry.getUID(), 6, 8);
      this.writeAsciiLong(entry.getGID(), 6, 8);
      this.writeAsciiLong(entry.getNumberOfLinks(), 6, 8);
      this.writeAsciiLong(entry.getRemoteDevice(), 6, 8);
      this.writeAsciiLong(entry.getTime(), 11, 8);
      this.writeAsciiLong((long)(entry.getName().length() + 1), 6, 8);
      this.writeAsciiLong(entry.getSize(), 11, 8);
      this.writeCString(entry.getName());
   }

   private void writeOldBinaryEntry(CpioArchiveEntry entry, boolean swapHalfWord) throws IOException {
      long inode = entry.getInode();
      long device = entry.getDevice();
      if("TRAILER!!!".equals(entry.getName())) {
         device = 0L;
         inode = 0L;
      } else if(inode == 0L && device == 0L) {
         inode = this.nextArtificalDeviceAndInode & 65535L;
         device = this.nextArtificalDeviceAndInode++ >> 16 & 65535L;
      } else {
         this.nextArtificalDeviceAndInode = Math.max(this.nextArtificalDeviceAndInode, inode + 65536L * device) + 1L;
      }

      this.writeBinaryLong(device, 2, swapHalfWord);
      this.writeBinaryLong(inode, 2, swapHalfWord);
      this.writeBinaryLong(entry.getMode(), 2, swapHalfWord);
      this.writeBinaryLong(entry.getUID(), 2, swapHalfWord);
      this.writeBinaryLong(entry.getGID(), 2, swapHalfWord);
      this.writeBinaryLong(entry.getNumberOfLinks(), 2, swapHalfWord);
      this.writeBinaryLong(entry.getRemoteDevice(), 2, swapHalfWord);
      this.writeBinaryLong(entry.getTime(), 4, swapHalfWord);
      this.writeBinaryLong((long)(entry.getName().length() + 1), 2, swapHalfWord);
      this.writeBinaryLong(entry.getSize(), 4, swapHalfWord);
      this.writeCString(entry.getName());
      this.pad(entry.getHeaderPadCount());
   }

   public void closeArchiveEntry() throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         this.ensureOpen();
         if(this.entry == null) {
            throw new IOException("Trying to close non-existent entry");
         } else if(this.entry.getSize() != this.written) {
            throw new IOException("invalid entry size (expected " + this.entry.getSize() + " but got " + this.written + " bytes)");
         } else {
            this.pad(this.entry.getDataPadCount());
            if(this.entry.getFormat() == 2 && this.crc != this.entry.getChksum()) {
               throw new IOException("CRC Error");
            } else {
               this.entry = null;
               this.crc = 0L;
               this.written = 0L;
            }
         }
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.ensureOpen();
      if(off >= 0 && len >= 0 && off <= b.length - len) {
         if(len != 0) {
            if(this.entry == null) {
               throw new IOException("no current CPIO entry");
            } else if(this.written + (long)len > this.entry.getSize()) {
               throw new IOException("attempt to write past end of STORED entry");
            } else {
               this.out.write(b, off, len);
               this.written += (long)len;
               if(this.entry.getFormat() == 2) {
                  for(int pos = 0; pos < len; ++pos) {
                     this.crc += (long)(b[pos] & 255);
                  }
               }

               this.count(len);
            }
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   public void finish() throws IOException {
      this.ensureOpen();
      if(this.finished) {
         throw new IOException("This archive has already been finished");
      } else if(this.entry != null) {
         throw new IOException("This archive contains unclosed entries.");
      } else {
         this.entry = new CpioArchiveEntry(this.entryFormat);
         this.entry.setName("TRAILER!!!");
         this.entry.setNumberOfLinks(1L);
         this.writeHeader(this.entry);
         this.closeArchiveEntry();
         int lengthOfLastBlock = (int)(this.getBytesWritten() % (long)this.blockSize);
         if(lengthOfLastBlock != 0) {
            this.pad(this.blockSize - lengthOfLastBlock);
         }

         this.finished = true;
      }
   }

   public void close() throws IOException {
      if(!this.finished) {
         this.finish();
      }

      if(!this.closed) {
         this.out.close();
         this.closed = true;
      }

   }

   private void pad(int count) throws IOException {
      if(count > 0) {
         byte[] buff = new byte[count];
         this.out.write(buff);
         this.count(count);
      }

   }

   private void writeBinaryLong(long number, int length, boolean swapHalfWord) throws IOException {
      byte[] tmp = CpioUtil.long2byteArray(number, length, swapHalfWord);
      this.out.write(tmp);
      this.count(tmp.length);
   }

   private void writeAsciiLong(long number, int length, int radix) throws IOException {
      StringBuilder tmp = new StringBuilder();
      if(radix == 16) {
         tmp.append(Long.toHexString(number));
      } else if(radix == 8) {
         tmp.append(Long.toOctalString(number));
      } else {
         tmp.append(Long.toString(number));
      }

      String tmpStr;
      if(tmp.length() <= length) {
         long insertLength = (long)(length - tmp.length());

         for(int pos = 0; (long)pos < insertLength; ++pos) {
            tmp.insert(0, "0");
         }

         tmpStr = tmp.toString();
      } else {
         tmpStr = tmp.substring(tmp.length() - length);
      }

      byte[] b = ArchiveUtils.toAsciiBytes(tmpStr);
      this.out.write(b);
      this.count(b.length);
   }

   private void writeCString(String str) throws IOException {
      ByteBuffer buf = this.encoding.encode(str);
      int len = buf.limit() - buf.position();
      this.out.write(buf.array(), buf.arrayOffset(), len);
      this.out.write(0);
      this.count(len + 1);
   }

   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         return new CpioArchiveEntry(inputFile, entryName);
      }
   }
}
