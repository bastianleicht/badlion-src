package org.apache.commons.compress.archivers.zip;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ExplodingInputStream;
import org.apache.commons.compress.archivers.zip.GeneralPurposeBit;
import org.apache.commons.compress.archivers.zip.UnshrinkingInputStream;
import org.apache.commons.compress.archivers.zip.UnsupportedZipFeatureException;
import org.apache.commons.compress.archivers.zip.Zip64ExtendedInformationExtraField;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipEightByteInteger;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipMethod;
import org.apache.commons.compress.archivers.zip.ZipShort;
import org.apache.commons.compress.archivers.zip.ZipUtil;
import org.apache.commons.compress.utils.IOUtils;

public class ZipArchiveInputStream extends ArchiveInputStream {
   private final ZipEncoding zipEncoding;
   private final boolean useUnicodeExtraFields;
   private final InputStream in;
   private final Inflater inf;
   private final ByteBuffer buf;
   private ZipArchiveInputStream.CurrentEntry current;
   private boolean closed;
   private boolean hitCentralDirectory;
   private ByteArrayInputStream lastStoredEntry;
   private boolean allowStoredEntriesWithDataDescriptor;
   private static final int LFH_LEN = 30;
   private static final int CFH_LEN = 46;
   private static final long TWO_EXP_32 = 4294967296L;
   private final byte[] LFH_BUF;
   private final byte[] SKIP_BUF;
   private final byte[] SHORT_BUF;
   private final byte[] WORD_BUF;
   private final byte[] TWO_DWORD_BUF;
   private int entriesRead;
   private static final byte[] LFH = ZipLong.LFH_SIG.getBytes();
   private static final byte[] CFH = ZipLong.CFH_SIG.getBytes();
   private static final byte[] DD = ZipLong.DD_SIG.getBytes();

   public ZipArchiveInputStream(InputStream inputStream) {
      this(inputStream, "UTF8");
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding) {
      this(inputStream, encoding, true);
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields) {
      this(inputStream, encoding, useUnicodeExtraFields, false);
   }

   public ZipArchiveInputStream(InputStream inputStream, String encoding, boolean useUnicodeExtraFields, boolean allowStoredEntriesWithDataDescriptor) {
      this.inf = new Inflater(true);
      this.buf = ByteBuffer.allocate(512);
      this.current = null;
      this.closed = false;
      this.hitCentralDirectory = false;
      this.lastStoredEntry = null;
      this.allowStoredEntriesWithDataDescriptor = false;
      this.LFH_BUF = new byte[30];
      this.SKIP_BUF = new byte[1024];
      this.SHORT_BUF = new byte[2];
      this.WORD_BUF = new byte[4];
      this.TWO_DWORD_BUF = new byte[16];
      this.entriesRead = 0;
      this.zipEncoding = ZipEncodingHelper.getZipEncoding(encoding);
      this.useUnicodeExtraFields = useUnicodeExtraFields;
      this.in = new PushbackInputStream(inputStream, this.buf.capacity());
      this.allowStoredEntriesWithDataDescriptor = allowStoredEntriesWithDataDescriptor;
      this.buf.limit(0);
   }

   public ZipArchiveEntry getNextZipEntry() throws IOException {
      boolean firstEntry = true;
      if(!this.closed && !this.hitCentralDirectory) {
         if(this.current != null) {
            this.closeEntry();
            firstEntry = false;
         }

         try {
            if(firstEntry) {
               this.readFirstLocalFileHeader(this.LFH_BUF);
            } else {
               this.readFully(this.LFH_BUF);
            }
         } catch (EOFException var16) {
            return null;
         }

         ZipLong sig = new ZipLong(this.LFH_BUF);
         if(sig.equals(ZipLong.CFH_SIG) || sig.equals(ZipLong.AED_SIG)) {
            this.hitCentralDirectory = true;
            this.skipRemainderOfArchive();
         }

         if(!sig.equals(ZipLong.LFH_SIG)) {
            return null;
         } else {
            int off = 4;
            this.current = new ZipArchiveInputStream.CurrentEntry();
            int versionMadeBy = ZipShort.getValue(this.LFH_BUF, off);
            off = off + 2;
            this.current.entry.setPlatform(versionMadeBy >> 8 & 15);
            GeneralPurposeBit gpFlag = GeneralPurposeBit.parse(this.LFH_BUF, off);
            boolean hasUTF8Flag = gpFlag.usesUTF8ForNames();
            ZipEncoding entryEncoding = hasUTF8Flag?ZipEncodingHelper.UTF8_ZIP_ENCODING:this.zipEncoding;
            this.current.hasDataDescriptor = gpFlag.usesDataDescriptor();
            this.current.entry.setGeneralPurposeBit(gpFlag);
            off = off + 2;
            this.current.entry.setMethod(ZipShort.getValue(this.LFH_BUF, off));
            off = off + 2;
            long time = ZipUtil.dosToJavaTime(ZipLong.getValue(this.LFH_BUF, off));
            this.current.entry.setTime(time);
            off = off + 4;
            ZipLong size = null;
            ZipLong cSize = null;
            if(!this.current.hasDataDescriptor) {
               this.current.entry.setCrc(ZipLong.getValue(this.LFH_BUF, off));
               off = off + 4;
               cSize = new ZipLong(this.LFH_BUF, off);
               off = off + 4;
               size = new ZipLong(this.LFH_BUF, off);
               off = off + 4;
            } else {
               off = off + 12;
            }

            int fileNameLen = ZipShort.getValue(this.LFH_BUF, off);
            off = off + 2;
            int extraLen = ZipShort.getValue(this.LFH_BUF, off);
            off = off + 2;
            byte[] fileName = new byte[fileNameLen];
            this.readFully(fileName);
            this.current.entry.setName(entryEncoding.decode(fileName), fileName);
            byte[] extraData = new byte[extraLen];
            this.readFully(extraData);
            this.current.entry.setExtra(extraData);
            if(!hasUTF8Flag && this.useUnicodeExtraFields) {
               ZipUtil.setNameAndCommentFromExtraFields(this.current.entry, fileName, (byte[])null);
            }

            this.processZip64Extra(size, cSize);
            if(this.current.entry.getCompressedSize() != -1L) {
               if(this.current.entry.getMethod() == ZipMethod.UNSHRINKING.getCode()) {
                  this.current.in = new UnshrinkingInputStream(new ZipArchiveInputStream.BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
               } else if(this.current.entry.getMethod() == ZipMethod.IMPLODING.getCode()) {
                  this.current.in = new ExplodingInputStream(this.current.entry.getGeneralPurposeBit().getSlidingDictionarySize(), this.current.entry.getGeneralPurposeBit().getNumberOfShannonFanoTrees(), new ZipArchiveInputStream.BoundedInputStream(this.in, this.current.entry.getCompressedSize()));
               }
            }

            ++this.entriesRead;
            return this.current.entry;
         }
      } else {
         return null;
      }
   }

   private void readFirstLocalFileHeader(byte[] lfh) throws IOException {
      this.readFully(lfh);
      ZipLong sig = new ZipLong(lfh);
      if(sig.equals(ZipLong.DD_SIG)) {
         throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.SPLITTING);
      } else {
         if(sig.equals(ZipLong.SINGLE_SEGMENT_SPLIT_MARKER)) {
            byte[] missedLfhBytes = new byte[4];
            this.readFully(missedLfhBytes);
            System.arraycopy(lfh, 4, lfh, 0, 26);
            System.arraycopy(missedLfhBytes, 0, lfh, 26, 4);
         }

      }
   }

   private void processZip64Extra(ZipLong size, ZipLong cSize) {
      Zip64ExtendedInformationExtraField z64 = (Zip64ExtendedInformationExtraField)this.current.entry.getExtraField(Zip64ExtendedInformationExtraField.HEADER_ID);
      this.current.usesZip64 = z64 != null;
      if(!this.current.hasDataDescriptor) {
         if(z64 == null || !cSize.equals(ZipLong.ZIP64_MAGIC) && !size.equals(ZipLong.ZIP64_MAGIC)) {
            this.current.entry.setCompressedSize(cSize.getValue());
            this.current.entry.setSize(size.getValue());
         } else {
            this.current.entry.setCompressedSize(z64.getCompressedSize().getLongValue());
            this.current.entry.setSize(z64.getSize().getLongValue());
         }
      }

   }

   public ArchiveEntry getNextEntry() throws IOException {
      return this.getNextZipEntry();
   }

   public boolean canReadEntryData(ArchiveEntry ae) {
      if(!(ae instanceof ZipArchiveEntry)) {
         return false;
      } else {
         ZipArchiveEntry ze = (ZipArchiveEntry)ae;
         return ZipUtil.canHandleEntryData(ze) && this.supportsDataDescriptorFor(ze);
      }
   }

   public int read(byte[] buffer, int offset, int length) throws IOException {
      if(this.closed) {
         throw new IOException("The stream is closed");
      } else if(this.current == null) {
         return -1;
      } else if(offset <= buffer.length && length >= 0 && offset >= 0 && buffer.length - offset >= length) {
         ZipUtil.checkRequestedFeatures(this.current.entry);
         if(!this.supportsDataDescriptorFor(this.current.entry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.DATA_DESCRIPTOR, this.current.entry);
         } else {
            int read;
            if(this.current.entry.getMethod() == 0) {
               read = this.readStored(buffer, offset, length);
            } else if(this.current.entry.getMethod() == 8) {
               read = this.readDeflated(buffer, offset, length);
            } else {
               if(this.current.entry.getMethod() != ZipMethod.UNSHRINKING.getCode() && this.current.entry.getMethod() != ZipMethod.IMPLODING.getCode()) {
                  throw new UnsupportedZipFeatureException(ZipMethod.getMethodByCode(this.current.entry.getMethod()), this.current.entry);
               }

               read = this.current.in.read(buffer, offset, length);
            }

            if(read >= 0) {
               this.current.crc.update(buffer, offset, read);
            }

            return read;
         }
      } else {
         throw new ArrayIndexOutOfBoundsException();
      }
   }

   private int readStored(byte[] buffer, int offset, int length) throws IOException {
      if(this.current.hasDataDescriptor) {
         if(this.lastStoredEntry == null) {
            this.readStoredEntry();
         }

         return this.lastStoredEntry.read(buffer, offset, length);
      } else {
         long csize = this.current.entry.getSize();
         if(this.current.bytesRead >= csize) {
            return -1;
         } else {
            if(this.buf.position() >= this.buf.limit()) {
               this.buf.position(0);
               int l = this.in.read(this.buf.array());
               if(l == -1) {
                  return -1;
               }

               this.buf.limit(l);
               this.count(l);
               this.current.bytesReadFromStream = (long)l;
            }

            int toRead = Math.min(this.buf.remaining(), length);
            if(csize - this.current.bytesRead < (long)toRead) {
               toRead = (int)(csize - this.current.bytesRead);
            }

            this.buf.get(buffer, offset, toRead);
            this.current.bytesRead = (long)toRead;
            return toRead;
         }
      }
   }

   private int readDeflated(byte[] buffer, int offset, int length) throws IOException {
      int read = this.readFromInflater(buffer, offset, length);
      if(read <= 0) {
         if(this.inf.finished()) {
            return -1;
         }

         if(this.inf.needsDictionary()) {
            throw new ZipException("This archive needs a preset dictionary which is not supported by Commons Compress.");
         }

         if(read == -1) {
            throw new IOException("Truncated ZIP file");
         }
      }

      return read;
   }

   private int readFromInflater(byte[] buffer, int offset, int length) throws IOException {
      int read = 0;

      while(true) {
         if(this.inf.needsInput()) {
            int l = this.fill();
            if(l <= 0) {
               if(l == -1) {
                  return -1;
               }
               break;
            }

            this.current.bytesReadFromStream = (long)this.buf.limit();
         }

         try {
            read = this.inf.inflate(buffer, offset, length);
         } catch (DataFormatException var6) {
            throw (IOException)(new ZipException(var6.getMessage())).initCause(var6);
         }

         if(read != 0 || !this.inf.needsInput()) {
            break;
         }
      }

      return read;
   }

   public void close() throws IOException {
      if(!this.closed) {
         this.closed = true;
         this.in.close();
         this.inf.end();
      }

   }

   public long skip(long value) throws IOException {
      if(value >= 0L) {
         long skipped;
         int x;
         for(skipped = 0L; skipped < value; skipped += (long)x) {
            long rem = value - skipped;
            x = this.read(this.SKIP_BUF, 0, (int)((long)this.SKIP_BUF.length > rem?rem:(long)this.SKIP_BUF.length));
            if(x == -1) {
               return skipped;
            }
         }

         return skipped;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static boolean matches(byte[] signature, int length) {
      return length < ZipArchiveOutputStream.LFH_SIG.length?false:checksig(signature, ZipArchiveOutputStream.LFH_SIG) || checksig(signature, ZipArchiveOutputStream.EOCD_SIG) || checksig(signature, ZipArchiveOutputStream.DD_SIG) || checksig(signature, ZipLong.SINGLE_SEGMENT_SPLIT_MARKER.getBytes());
   }

   private static boolean checksig(byte[] signature, byte[] expected) {
      for(int i = 0; i < expected.length; ++i) {
         if(signature[i] != expected[i]) {
            return false;
         }
      }

      return true;
   }

   private void closeEntry() throws IOException {
      if(this.closed) {
         throw new IOException("The stream is closed");
      } else if(this.current != null) {
         if(this.current.bytesReadFromStream <= this.current.entry.getCompressedSize() && !this.current.hasDataDescriptor) {
            this.drainCurrentEntryData();
         } else {
            this.skip(Long.MAX_VALUE);
            long inB = this.current.entry.getMethod() == 8?this.getBytesInflated():this.current.bytesRead;
            int diff = (int)(this.current.bytesReadFromStream - inB);
            if(diff > 0) {
               this.pushback(this.buf.array(), this.buf.limit() - diff, diff);
            }
         }

         if(this.lastStoredEntry == null && this.current.hasDataDescriptor) {
            this.readDataDescriptor();
         }

         this.inf.reset();
         this.buf.clear().flip();
         this.current = null;
         this.lastStoredEntry = null;
      }
   }

   private void drainCurrentEntryData() throws IOException {
      long n;
      for(long remaining = this.current.entry.getCompressedSize() - this.current.bytesReadFromStream; remaining > 0L; remaining -= n) {
         n = (long)this.in.read(this.buf.array(), 0, (int)Math.min((long)this.buf.capacity(), remaining));
         if(n < 0L) {
            throw new EOFException("Truncated ZIP entry: " + this.current.entry.getName());
         }

         this.count(n);
      }

   }

   private long getBytesInflated() {
      long inB = this.inf.getBytesRead();
      if(this.current.bytesReadFromStream >= 4294967296L) {
         while(inB + 4294967296L <= this.current.bytesReadFromStream) {
            inB += 4294967296L;
         }
      }

      return inB;
   }

   private int fill() throws IOException {
      if(this.closed) {
         throw new IOException("The stream is closed");
      } else {
         int length = this.in.read(this.buf.array());
         if(length > 0) {
            this.buf.limit(length);
            this.count(this.buf.limit());
            this.inf.setInput(this.buf.array(), 0, this.buf.limit());
         }

         return length;
      }
   }

   private void readFully(byte[] b) throws IOException {
      int count = IOUtils.readFully(this.in, b);
      this.count(count);
      if(count < b.length) {
         throw new EOFException();
      }
   }

   private void readDataDescriptor() throws IOException {
      this.readFully(this.WORD_BUF);
      ZipLong val = new ZipLong(this.WORD_BUF);
      if(ZipLong.DD_SIG.equals(val)) {
         this.readFully(this.WORD_BUF);
         val = new ZipLong(this.WORD_BUF);
      }

      this.current.entry.setCrc(val.getValue());
      this.readFully(this.TWO_DWORD_BUF);
      ZipLong potentialSig = new ZipLong(this.TWO_DWORD_BUF, 8);
      if(!potentialSig.equals(ZipLong.CFH_SIG) && !potentialSig.equals(ZipLong.LFH_SIG)) {
         this.current.entry.setCompressedSize(ZipEightByteInteger.getLongValue(this.TWO_DWORD_BUF));
         this.current.entry.setSize(ZipEightByteInteger.getLongValue(this.TWO_DWORD_BUF, 8));
      } else {
         this.pushback(this.TWO_DWORD_BUF, 8, 8);
         this.current.entry.setCompressedSize(ZipLong.getValue(this.TWO_DWORD_BUF));
         this.current.entry.setSize(ZipLong.getValue(this.TWO_DWORD_BUF, 4));
      }

   }

   private boolean supportsDataDescriptorFor(ZipArchiveEntry entry) {
      return !entry.getGeneralPurposeBit().usesDataDescriptor() || this.allowStoredEntriesWithDataDescriptor && entry.getMethod() == 0 || entry.getMethod() == 8;
   }

   private void readStoredEntry() throws IOException {
      ByteArrayOutputStream bos = new ByteArrayOutputStream();
      int off = 0;
      boolean done = false;
      int ddLen = this.current.usesZip64?20:12;

      while(!done) {
         int r = this.in.read(this.buf.array(), off, 512 - off);
         if(r <= 0) {
            throw new IOException("Truncated ZIP file");
         }

         if(r + off < 4) {
            off += r;
         } else {
            done = this.bufferContainsSignature(bos, off, r, ddLen);
            if(!done) {
               off = this.cacheBytesRead(bos, off, r, ddLen);
            }
         }
      }

      byte[] b = bos.toByteArray();
      this.lastStoredEntry = new ByteArrayInputStream(b);
   }

   private boolean bufferContainsSignature(ByteArrayOutputStream bos, int offset, int lastRead, int expectedDDLen) throws IOException {
      boolean done = false;
      int readTooMuch = 0;

      for(int i = 0; !done && i < lastRead - 4; ++i) {
         if(this.buf.array()[i] == LFH[0] && this.buf.array()[i + 1] == LFH[1]) {
            if((this.buf.array()[i + 2] != LFH[2] || this.buf.array()[i + 3] != LFH[3]) && (this.buf.array()[i] != CFH[2] || this.buf.array()[i + 3] != CFH[3])) {
               if(this.buf.array()[i + 2] == DD[2] && this.buf.array()[i + 3] == DD[3]) {
                  readTooMuch = offset + lastRead - i;
                  done = true;
               }
            } else {
               readTooMuch = offset + lastRead - i - expectedDDLen;
               done = true;
            }

            if(done) {
               this.pushback(this.buf.array(), offset + lastRead - readTooMuch, readTooMuch);
               bos.write(this.buf.array(), 0, i);
               this.readDataDescriptor();
            }
         }
      }

      return done;
   }

   private int cacheBytesRead(ByteArrayOutputStream bos, int offset, int lastRead, int expecteDDLen) {
      int cacheable = offset + lastRead - expecteDDLen - 3;
      if(cacheable > 0) {
         bos.write(this.buf.array(), 0, cacheable);
         System.arraycopy(this.buf.array(), cacheable, this.buf.array(), 0, expecteDDLen + 3);
         offset = expecteDDLen + 3;
      } else {
         offset = offset + lastRead;
      }

      return offset;
   }

   private void pushback(byte[] buf, int offset, int length) throws IOException {
      ((PushbackInputStream)this.in).unread(buf, offset, length);
      this.pushedBackBytes((long)length);
   }

   private void skipRemainderOfArchive() throws IOException {
      this.realSkip((long)(this.entriesRead * 46 - 30));
      this.findEocdRecord();
      this.realSkip(16L);
      this.readFully(this.SHORT_BUF);
      this.realSkip((long)ZipShort.getValue(this.SHORT_BUF));
   }

   private void findEocdRecord() throws IOException {
      int currentByte = -1;
      boolean skipReadCall = false;

      while(skipReadCall || (currentByte = this.readOneByte()) > -1) {
         skipReadCall = false;
         if(this.isFirstByteOfEocdSig(currentByte)) {
            currentByte = this.readOneByte();
            if(currentByte != ZipArchiveOutputStream.EOCD_SIG[1]) {
               if(currentByte == -1) {
                  break;
               }

               skipReadCall = this.isFirstByteOfEocdSig(currentByte);
            } else {
               currentByte = this.readOneByte();
               if(currentByte != ZipArchiveOutputStream.EOCD_SIG[2]) {
                  if(currentByte == -1) {
                     break;
                  }

                  skipReadCall = this.isFirstByteOfEocdSig(currentByte);
               } else {
                  currentByte = this.readOneByte();
                  if(currentByte == -1 || currentByte == ZipArchiveOutputStream.EOCD_SIG[3]) {
                     break;
                  }

                  skipReadCall = this.isFirstByteOfEocdSig(currentByte);
               }
            }
         }
      }

   }

   private void realSkip(long value) throws IOException {
      if(value >= 0L) {
         int x;
         for(long skipped = 0L; skipped < value; skipped += (long)x) {
            long rem = value - skipped;
            x = this.in.read(this.SKIP_BUF, 0, (int)((long)this.SKIP_BUF.length > rem?rem:(long)this.SKIP_BUF.length));
            if(x == -1) {
               return;
            }

            this.count(x);
         }

      } else {
         throw new IllegalArgumentException();
      }
   }

   private int readOneByte() throws IOException {
      int b = this.in.read();
      if(b != -1) {
         this.count(1);
      }

      return b;
   }

   private boolean isFirstByteOfEocdSig(int b) {
      return b == ZipArchiveOutputStream.EOCD_SIG[0];
   }

   private class BoundedInputStream extends InputStream {
      private final InputStream in;
      private final long max;
      private long pos = 0L;

      public BoundedInputStream(InputStream in, long size) {
         this.max = size;
         this.in = in;
      }

      public int read() throws IOException {
         if(this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            int result = this.in.read();
            ++this.pos;
            ZipArchiveInputStream.this.count(1);
            ZipArchiveInputStream.this.current.bytesReadFromStream++;
            return result;
         }
      }

      public int read(byte[] b) throws IOException {
         return this.read(b, 0, b.length);
      }

      public int read(byte[] b, int off, int len) throws IOException {
         if(this.max >= 0L && this.pos >= this.max) {
            return -1;
         } else {
            long maxRead = this.max >= 0L?Math.min((long)len, this.max - this.pos):(long)len;
            int bytesRead = this.in.read(b, off, (int)maxRead);
            if(bytesRead == -1) {
               return -1;
            } else {
               this.pos += (long)bytesRead;
               ZipArchiveInputStream.this.count(bytesRead);
               ZipArchiveInputStream.this.current.bytesReadFromStream = (long)bytesRead;
               return bytesRead;
            }
         }
      }

      public long skip(long n) throws IOException {
         long toSkip = this.max >= 0L?Math.min(n, this.max - this.pos):n;
         long skippedBytes = this.in.skip(toSkip);
         this.pos += skippedBytes;
         return skippedBytes;
      }

      public int available() throws IOException {
         return this.max >= 0L && this.pos >= this.max?0:this.in.available();
      }
   }

   private static final class CurrentEntry {
      private final ZipArchiveEntry entry;
      private boolean hasDataDescriptor;
      private boolean usesZip64;
      private long bytesRead;
      private long bytesReadFromStream;
      private final CRC32 crc;
      private InputStream in;

      private CurrentEntry() {
         this.entry = new ZipArchiveEntry();
         this.crc = new CRC32();
      }
   }
}
