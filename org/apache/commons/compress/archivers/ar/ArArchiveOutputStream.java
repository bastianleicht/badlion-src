package org.apache.commons.compress.archivers.ar;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.utils.ArchiveUtils;

public class ArArchiveOutputStream extends ArchiveOutputStream {
   public static final int LONGFILE_ERROR = 0;
   public static final int LONGFILE_BSD = 1;
   private final OutputStream out;
   private long entryOffset = 0L;
   private ArArchiveEntry prevEntry;
   private boolean haveUnclosedEntry = false;
   private int longFileMode = 0;
   private boolean finished = false;

   public ArArchiveOutputStream(OutputStream pOut) {
      this.out = pOut;
   }

   public void setLongFileMode(int longFileMode) {
      this.longFileMode = longFileMode;
   }

   private long writeArchiveHeader() throws IOException {
      byte[] header = ArchiveUtils.toAsciiBytes("!<arch>\n");
      this.out.write(header);
      return (long)header.length;
   }

   public void closeArchiveEntry() throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else if(this.prevEntry != null && this.haveUnclosedEntry) {
         if(this.entryOffset % 2L != 0L) {
            this.out.write(10);
         }

         this.haveUnclosedEntry = false;
      } else {
         throw new IOException("No current entry to close");
      }
   }

   public void putArchiveEntry(ArchiveEntry pEntry) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         ArArchiveEntry pArEntry = (ArArchiveEntry)pEntry;
         if(this.prevEntry == null) {
            this.writeArchiveHeader();
         } else {
            if(this.prevEntry.getLength() != this.entryOffset) {
               throw new IOException("length does not match entry (" + this.prevEntry.getLength() + " != " + this.entryOffset);
            }

            if(this.haveUnclosedEntry) {
               this.closeArchiveEntry();
            }
         }

         this.prevEntry = pArEntry;
         this.writeEntryHeader(pArEntry);
         this.entryOffset = 0L;
         this.haveUnclosedEntry = true;
      }
   }

   private long fill(long pOffset, long pNewOffset, char pFill) throws IOException {
      long diff = pNewOffset - pOffset;
      if(diff > 0L) {
         for(int i = 0; (long)i < diff; ++i) {
            this.write(pFill);
         }
      }

      return pNewOffset;
   }

   private long write(String data) throws IOException {
      byte[] bytes = data.getBytes("ascii");
      this.write(bytes);
      return (long)bytes.length;
   }

   private long writeEntryHeader(ArArchiveEntry pEntry) throws IOException {
      long offset = 0L;
      boolean mustAppendName = false;
      String n = pEntry.getName();
      if(0 == this.longFileMode && n.length() > 16) {
         throw new IOException("filename too long, > 16 chars: " + n);
      } else {
         if(1 != this.longFileMode || n.length() <= 16 && n.indexOf(" ") <= -1) {
            offset = offset + this.write(n);
         } else {
            mustAppendName = true;
            offset = offset + this.write("#1/" + String.valueOf(n.length()));
         }

         offset = this.fill(offset, 16L, ' ');
         String m = "" + pEntry.getLastModified();
         if(m.length() > 12) {
            throw new IOException("modified too long");
         } else {
            offset = offset + this.write(m);
            offset = this.fill(offset, 28L, ' ');
            String u = "" + pEntry.getUserId();
            if(u.length() > 6) {
               throw new IOException("userid too long");
            } else {
               offset = offset + this.write(u);
               offset = this.fill(offset, 34L, ' ');
               String g = "" + pEntry.getGroupId();
               if(g.length() > 6) {
                  throw new IOException("groupid too long");
               } else {
                  offset = offset + this.write(g);
                  offset = this.fill(offset, 40L, ' ');
                  String fm = "" + Integer.toString(pEntry.getMode(), 8);
                  if(fm.length() > 8) {
                     throw new IOException("filemode too long");
                  } else {
                     offset = offset + this.write(fm);
                     offset = this.fill(offset, 48L, ' ');
                     String s = String.valueOf(pEntry.getLength() + (long)(mustAppendName?n.length():0));
                     if(s.length() > 10) {
                        throw new IOException("size too long");
                     } else {
                        offset = offset + this.write(s);
                        offset = this.fill(offset, 58L, ' ');
                        offset = offset + this.write("`\n");
                        if(mustAppendName) {
                           offset += this.write(n);
                        }

                        return offset;
                     }
                  }
               }
            }
         }
      }
   }

   public void write(byte[] b, int off, int len) throws IOException {
      this.out.write(b, off, len);
      this.count(len);
      this.entryOffset += (long)len;
   }

   public void close() throws IOException {
      if(!this.finished) {
         this.finish();
      }

      this.out.close();
      this.prevEntry = null;
   }

   public ArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
      if(this.finished) {
         throw new IOException("Stream has already been finished");
      } else {
         return new ArArchiveEntry(inputFile, entryName);
      }
   }

   public void finish() throws IOException {
      if(this.haveUnclosedEntry) {
         throw new IOException("This archive contains unclosed entries.");
      } else if(this.finished) {
         throw new IOException("This archive has already been finished");
      } else {
         this.finished = true;
      }
   }
}
