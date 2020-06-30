package org.apache.commons.compress.archivers.dump;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.Map.Entry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.dump.Dirent;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.archivers.dump.DumpArchiveEntry;
import org.apache.commons.compress.archivers.dump.DumpArchiveSummary;
import org.apache.commons.compress.archivers.dump.DumpArchiveUtil;
import org.apache.commons.compress.archivers.dump.InvalidFormatException;
import org.apache.commons.compress.archivers.dump.TapeInputStream;
import org.apache.commons.compress.archivers.dump.UnrecognizedFormatException;
import org.apache.commons.compress.archivers.zip.ZipEncoding;
import org.apache.commons.compress.archivers.zip.ZipEncodingHelper;

public class DumpArchiveInputStream extends ArchiveInputStream {
   private DumpArchiveSummary summary;
   private DumpArchiveEntry active;
   private boolean isClosed;
   private boolean hasHitEOF;
   private long entrySize;
   private long entryOffset;
   private int readIdx;
   private final byte[] readBuf;
   private byte[] blockBuffer;
   private int recordOffset;
   private long filepos;
   protected TapeInputStream raw;
   private final Map names;
   private final Map pending;
   private Queue queue;
   private final ZipEncoding encoding;

   public DumpArchiveInputStream(InputStream is) throws ArchiveException {
      this(is, (String)null);
   }

   public DumpArchiveInputStream(InputStream is, String encoding) throws ArchiveException {
      this.readBuf = new byte[1024];
      this.names = new HashMap();
      this.pending = new HashMap();
      this.raw = new TapeInputStream(is);
      this.hasHitEOF = false;
      this.encoding = ZipEncodingHelper.getZipEncoding(encoding);

      try {
         byte[] headerBytes = this.raw.readRecord();
         if(!DumpArchiveUtil.verify(headerBytes)) {
            throw new UnrecognizedFormatException();
         }

         this.summary = new DumpArchiveSummary(headerBytes, this.encoding);
         this.raw.resetBlockSize(this.summary.getNTRec(), this.summary.isCompressed());
         this.blockBuffer = new byte[4096];
         this.readCLRI();
         this.readBITS();
      } catch (IOException var4) {
         throw new ArchiveException(var4.getMessage(), var4);
      }

      Dirent root = new Dirent(2, 2, 4, ".");
      this.names.put(Integer.valueOf(2), root);
      this.queue = new PriorityQueue(10, new Comparator() {
         public int compare(DumpArchiveEntry p, DumpArchiveEntry q) {
            return p.getOriginalName() != null && q.getOriginalName() != null?p.getOriginalName().compareTo(q.getOriginalName()):Integer.MAX_VALUE;
         }
      });
   }

   /** @deprecated */
   @Deprecated
   public int getCount() {
      return (int)this.getBytesRead();
   }

   public long getBytesRead() {
      return this.raw.getBytesRead();
   }

   public DumpArchiveSummary getSummary() {
      return this.summary;
   }

   private void readCLRI() throws IOException {
      byte[] buffer = this.raw.readRecord();
      if(!DumpArchiveUtil.verify(buffer)) {
         throw new InvalidFormatException();
      } else {
         this.active = DumpArchiveEntry.parse(buffer);
         if(DumpArchiveConstants.SEGMENT_TYPE.CLRI != this.active.getHeaderType()) {
            throw new InvalidFormatException();
         } else if(this.raw.skip((long)(1024 * this.active.getHeaderCount())) == -1L) {
            throw new EOFException();
         } else {
            this.readIdx = this.active.getHeaderCount();
         }
      }
   }

   private void readBITS() throws IOException {
      byte[] buffer = this.raw.readRecord();
      if(!DumpArchiveUtil.verify(buffer)) {
         throw new InvalidFormatException();
      } else {
         this.active = DumpArchiveEntry.parse(buffer);
         if(DumpArchiveConstants.SEGMENT_TYPE.BITS != this.active.getHeaderType()) {
            throw new InvalidFormatException();
         } else if(this.raw.skip((long)(1024 * this.active.getHeaderCount())) == -1L) {
            throw new EOFException();
         } else {
            this.readIdx = this.active.getHeaderCount();
         }
      }
   }

   public DumpArchiveEntry getNextDumpEntry() throws IOException {
      return this.getNextEntry();
   }

   public DumpArchiveEntry getNextEntry() throws IOException {
      DumpArchiveEntry entry = null;
      String path = null;
      if(!this.queue.isEmpty()) {
         return (DumpArchiveEntry)this.queue.remove();
      } else {
         while(entry == null) {
            if(this.hasHitEOF) {
               return null;
            }

            while(this.readIdx < this.active.getHeaderCount()) {
               if(!this.active.isSparseRecord(this.readIdx++) && this.raw.skip(1024L) == -1L) {
                  throw new EOFException();
               }
            }

            this.readIdx = 0;
            this.filepos = this.raw.getBytesRead();
            byte[] headerBytes = this.raw.readRecord();
            if(!DumpArchiveUtil.verify(headerBytes)) {
               throw new InvalidFormatException();
            }

            for(this.active = DumpArchiveEntry.parse(headerBytes); DumpArchiveConstants.SEGMENT_TYPE.ADDR == this.active.getHeaderType(); this.active = DumpArchiveEntry.parse(headerBytes)) {
               if(this.raw.skip((long)(1024 * (this.active.getHeaderCount() - this.active.getHeaderHoles()))) == -1L) {
                  throw new EOFException();
               }

               this.filepos = this.raw.getBytesRead();
               headerBytes = this.raw.readRecord();
               if(!DumpArchiveUtil.verify(headerBytes)) {
                  throw new InvalidFormatException();
               }
            }

            if(DumpArchiveConstants.SEGMENT_TYPE.END == this.active.getHeaderType()) {
               this.hasHitEOF = true;
               return null;
            }

            entry = this.active;
            if(entry.isDirectory()) {
               this.readDirectoryEntry(this.active);
               this.entryOffset = 0L;
               this.entrySize = 0L;
               this.readIdx = this.active.getHeaderCount();
            } else {
               this.entryOffset = 0L;
               this.entrySize = this.active.getEntrySize();
               this.readIdx = 0;
            }

            this.recordOffset = this.readBuf.length;
            path = this.getPath(entry);
            if(path == null) {
               entry = null;
            }
         }

         entry.setName(path);
         entry.setSimpleName(((Dirent)this.names.get(Integer.valueOf(entry.getIno()))).getName());
         entry.setOffset(this.filepos);
         return entry;
      }
   }

   private void readDirectoryEntry(DumpArchiveEntry entry) throws IOException {
      long size = entry.getEntrySize();

      for(boolean first = true; first || DumpArchiveConstants.SEGMENT_TYPE.ADDR == entry.getHeaderType(); size -= 1024L) {
         if(!first) {
            this.raw.readRecord();
         }

         if(!this.names.containsKey(Integer.valueOf(entry.getIno())) && DumpArchiveConstants.SEGMENT_TYPE.INODE == entry.getHeaderType()) {
            this.pending.put(Integer.valueOf(entry.getIno()), entry);
         }

         int datalen = 1024 * entry.getHeaderCount();
         if(this.blockBuffer.length < datalen) {
            this.blockBuffer = new byte[datalen];
         }

         if(this.raw.read(this.blockBuffer, 0, datalen) != datalen) {
            throw new EOFException();
         }

         int reclen = 0;

         for(int i = 0; i < datalen - 8 && (long)i < size - 8L; i += reclen) {
            int ino = DumpArchiveUtil.convert32(this.blockBuffer, i);
            reclen = DumpArchiveUtil.convert16(this.blockBuffer, i + 4);
            byte type = this.blockBuffer[i + 6];
            String name = DumpArchiveUtil.decode(this.encoding, this.blockBuffer, i + 8, this.blockBuffer[i + 7]);
            if(!".".equals(name) && !"..".equals(name)) {
               Dirent d = new Dirent(ino, entry.getIno(), type, name);
               this.names.put(Integer.valueOf(ino), d);

               for(Entry<Integer, DumpArchiveEntry> e : this.pending.entrySet()) {
                  String path = this.getPath((DumpArchiveEntry)e.getValue());
                  if(path != null) {
                     ((DumpArchiveEntry)e.getValue()).setName(path);
                     ((DumpArchiveEntry)e.getValue()).setSimpleName(((Dirent)this.names.get(e.getKey())).getName());
                     this.queue.add(e.getValue());
                  }
               }

               for(DumpArchiveEntry e : this.queue) {
                  this.pending.remove(Integer.valueOf(e.getIno()));
               }
            }
         }

         byte[] peekBytes = this.raw.peek();
         if(!DumpArchiveUtil.verify(peekBytes)) {
            throw new InvalidFormatException();
         }

         entry = DumpArchiveEntry.parse(peekBytes);
         first = false;
      }

   }

   private String getPath(DumpArchiveEntry entry) {
      Stack<String> elements = new Stack();
      Dirent dirent = null;
      int i = entry.getIno();

      while(true) {
         if(!this.names.containsKey(Integer.valueOf(i))) {
            elements.clear();
            break;
         }

         dirent = (Dirent)this.names.get(Integer.valueOf(i));
         elements.push(dirent.getName());
         if(dirent.getIno() == dirent.getParentIno()) {
            break;
         }

         i = dirent.getParentIno();
      }

      if(elements.isEmpty()) {
         this.pending.put(Integer.valueOf(entry.getIno()), entry);
         return null;
      } else {
         StringBuilder sb = new StringBuilder((String)elements.pop());

         while(!elements.isEmpty()) {
            sb.append('/');
            sb.append((String)elements.pop());
         }

         return sb.toString();
      }
   }

   public int read(byte[] buf, int off, int len) throws IOException {
      int totalRead = 0;
      if(!this.hasHitEOF && !this.isClosed && this.entryOffset < this.entrySize) {
         if(this.active == null) {
            throw new IllegalStateException("No current dump entry");
         } else {
            if((long)len + this.entryOffset > this.entrySize) {
               len = (int)(this.entrySize - this.entryOffset);
            }

            while(len > 0) {
               int sz = len > this.readBuf.length - this.recordOffset?this.readBuf.length - this.recordOffset:len;
               if(this.recordOffset + sz <= this.readBuf.length) {
                  System.arraycopy(this.readBuf, this.recordOffset, buf, off, sz);
                  totalRead += sz;
                  this.recordOffset += sz;
                  len -= sz;
                  off += sz;
               }

               if(len > 0) {
                  if(this.readIdx >= 512) {
                     byte[] headerBytes = this.raw.readRecord();
                     if(!DumpArchiveUtil.verify(headerBytes)) {
                        throw new InvalidFormatException();
                     }

                     this.active = DumpArchiveEntry.parse(headerBytes);
                     this.readIdx = 0;
                  }

                  if(!this.active.isSparseRecord(this.readIdx++)) {
                     int r = this.raw.read(this.readBuf, 0, this.readBuf.length);
                     if(r != this.readBuf.length) {
                        throw new EOFException();
                     }
                  } else {
                     Arrays.fill(this.readBuf, (byte)0);
                  }

                  this.recordOffset = 0;
               }
            }

            this.entryOffset += (long)totalRead;
            return totalRead;
         }
      } else {
         return -1;
      }
   }

   public void close() throws IOException {
      if(!this.isClosed) {
         this.isClosed = true;
         this.raw.close();
      }

   }

   public static boolean matches(byte[] buffer, int length) {
      return length < 32?false:(length >= 1024?DumpArchiveUtil.verify(buffer):'\uea6c' == DumpArchiveUtil.convert32(buffer, 24));
   }
}
