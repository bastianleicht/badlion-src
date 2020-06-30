package org.apache.commons.compress.archivers.ar;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.utils.ArchiveUtils;
import org.apache.commons.compress.utils.IOUtils;

public class ArArchiveInputStream extends ArchiveInputStream {
   private final InputStream input;
   private long offset = 0L;
   private boolean closed;
   private ArArchiveEntry currentEntry = null;
   private byte[] namebuffer = null;
   private long entryOffset = -1L;
   private final byte[] NAME_BUF = new byte[16];
   private final byte[] LAST_MODIFIED_BUF = new byte[12];
   private final byte[] ID_BUF = new byte[6];
   private final byte[] FILE_MODE_BUF = new byte[8];
   private final byte[] LENGTH_BUF = new byte[10];
   static final String BSD_LONGNAME_PREFIX = "#1/";
   private static final int BSD_LONGNAME_PREFIX_LEN = "#1/".length();
   private static final String BSD_LONGNAME_PATTERN = "^#1/\\d+";
   private static final String GNU_STRING_TABLE_NAME = "//";
   private static final String GNU_LONGNAME_PATTERN = "^/\\d+";

   public ArArchiveInputStream(InputStream pInput) {
      this.input = pInput;
      this.closed = false;
   }

   public ArArchiveEntry getNextArEntry() throws IOException {
      if(this.currentEntry != null) {
         long entryEnd = this.entryOffset + this.currentEntry.getLength();
         IOUtils.skip(this, entryEnd - this.offset);
         this.currentEntry = null;
      }

      if(this.offset == 0L) {
         byte[] expected = ArchiveUtils.toAsciiBytes("!<arch>\n");
         byte[] realized = new byte[expected.length];
         int read = IOUtils.readFully(this, realized);
         if(read != expected.length) {
            throw new IOException("failed to read header. Occured at byte: " + this.getBytesRead());
         }

         for(int i = 0; i < expected.length; ++i) {
            if(expected[i] != realized[i]) {
               throw new IOException("invalid header " + ArchiveUtils.toAsciiString(realized));
            }
         }
      }

      if(this.offset % 2L != 0L && this.read() < 0) {
         return null;
      } else if(this.input.available() == 0) {
         return null;
      } else {
         IOUtils.readFully(this, this.NAME_BUF);
         IOUtils.readFully(this, this.LAST_MODIFIED_BUF);
         IOUtils.readFully(this, this.ID_BUF);
         int userId = this.asInt(this.ID_BUF, true);
         IOUtils.readFully(this, this.ID_BUF);
         IOUtils.readFully(this, this.FILE_MODE_BUF);
         IOUtils.readFully(this, this.LENGTH_BUF);
         byte[] expected = ArchiveUtils.toAsciiBytes("`\n");
         byte[] realized = new byte[expected.length];
         int read = IOUtils.readFully(this, realized);
         if(read != expected.length) {
            throw new IOException("failed to read entry trailer. Occured at byte: " + this.getBytesRead());
         } else {
            for(int i = 0; i < expected.length; ++i) {
               if(expected[i] != realized[i]) {
                  throw new IOException("invalid entry trailer. not read the content? Occured at byte: " + this.getBytesRead());
               }
            }

            this.entryOffset = this.offset;
            String temp = ArchiveUtils.toAsciiString(this.NAME_BUF).trim();
            if(isGNUStringTable(temp)) {
               this.currentEntry = this.readGNUStringTable(this.LENGTH_BUF);
               return this.getNextArEntry();
            } else {
               long len = this.asLong(this.LENGTH_BUF);
               if(temp.endsWith("/")) {
                  temp = temp.substring(0, temp.length() - 1);
               } else if(this.isGNULongName(temp)) {
                  int off = Integer.parseInt(temp.substring(1));
                  temp = this.getExtendedName(off);
               } else if(isBSDLongName(temp)) {
                  temp = this.getBSDLongName(temp);
                  int nameLen = temp.length();
                  len -= (long)nameLen;
                  this.entryOffset += (long)nameLen;
               }

               this.currentEntry = new ArArchiveEntry(temp, len, userId, this.asInt(this.ID_BUF, true), this.asInt(this.FILE_MODE_BUF, 8), this.asLong(this.LAST_MODIFIED_BUF));
               return this.currentEntry;
            }
         }
      }
   }

   private String getExtendedName(int offset) throws IOException {
      if(this.namebuffer == null) {
         throw new IOException("Cannot process GNU long filename as no // record was found");
      } else {
         for(int i = offset; i < this.namebuffer.length; ++i) {
            if(this.namebuffer[i] == 10) {
               if(this.namebuffer[i - 1] == 47) {
                  --i;
               }

               return ArchiveUtils.toAsciiString(this.namebuffer, offset, i - offset);
            }
         }

         throw new IOException("Failed to read entry: " + offset);
      }
   }

   private long asLong(byte[] input) {
      return Long.parseLong(ArchiveUtils.toAsciiString(input).trim());
   }

   private int asInt(byte[] input) {
      return this.asInt(input, 10, false);
   }

   private int asInt(byte[] input, boolean treatBlankAsZero) {
      return this.asInt(input, 10, treatBlankAsZero);
   }

   private int asInt(byte[] input, int base) {
      return this.asInt(input, base, false);
   }

   private int asInt(byte[] input, int base, boolean treatBlankAsZero) {
      String string = ArchiveUtils.toAsciiString(input).trim();
      return string.length() == 0 && treatBlankAsZero?0:Integer.parseInt(string, base);
   }

   public ArchiveEntry getNextEntry() throws IOException {
      return this.getNextArEntry();
   }

   public void close() throws IOException {
      if(!this.closed) {
         this.closed = true;
         this.input.close();
      }

      this.currentEntry = null;
   }

   public int read(byte[] b, int off, int len) throws IOException {
      int toRead = len;
      if(this.currentEntry != null) {
         long entryEnd = this.entryOffset + this.currentEntry.getLength();
         if(len <= 0 || entryEnd <= this.offset) {
            return -1;
         }

         toRead = (int)Math.min((long)len, entryEnd - this.offset);
      }

      int ret = this.input.read(b, off, toRead);
      this.count(ret);
      this.offset += ret > 0?(long)ret:0L;
      return ret;
   }

   public static boolean matches(byte[] signature, int length) {
      return length < 8?false:(signature[0] != 33?false:(signature[1] != 60?false:(signature[2] != 97?false:(signature[3] != 114?false:(signature[4] != 99?false:(signature[5] != 104?false:(signature[6] != 62?false:signature[7] == 10)))))));
   }

   private static boolean isBSDLongName(String name) {
      return name != null && name.matches("^#1/\\d+");
   }

   private String getBSDLongName(String bsdLongName) throws IOException {
      int nameLen = Integer.parseInt(bsdLongName.substring(BSD_LONGNAME_PREFIX_LEN));
      byte[] name = new byte[nameLen];
      int read = IOUtils.readFully(this.input, name);
      this.count(read);
      if(read != nameLen) {
         throw new EOFException();
      } else {
         return ArchiveUtils.toAsciiString(name);
      }
   }

   private static boolean isGNUStringTable(String name) {
      return "//".equals(name);
   }

   private ArArchiveEntry readGNUStringTable(byte[] length) throws IOException {
      int bufflen = this.asInt(length);
      this.namebuffer = new byte[bufflen];
      int read = IOUtils.readFully(this, this.namebuffer, 0, bufflen);
      if(read != bufflen) {
         throw new IOException("Failed to read complete // record: expected=" + bufflen + " read=" + read);
      } else {
         return new ArArchiveEntry("//", (long)bufflen);
      }
   }

   private boolean isGNULongName(String name) {
      return name != null && name.matches("^/\\d+");
   }
}
