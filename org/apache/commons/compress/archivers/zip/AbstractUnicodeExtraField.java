package org.apache.commons.compress.archivers.zip;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.zip.ZipException;
import org.apache.commons.compress.archivers.zip.ZipExtraField;
import org.apache.commons.compress.archivers.zip.ZipLong;
import org.apache.commons.compress.archivers.zip.ZipShort;

public abstract class AbstractUnicodeExtraField implements ZipExtraField {
   private long nameCRC32;
   private byte[] unicodeName;
   private byte[] data;

   protected AbstractUnicodeExtraField() {
   }

   protected AbstractUnicodeExtraField(String text, byte[] bytes, int off, int len) {
      CRC32 crc32 = new CRC32();
      crc32.update(bytes, off, len);
      this.nameCRC32 = crc32.getValue();

      try {
         this.unicodeName = text.getBytes("UTF-8");
      } catch (UnsupportedEncodingException var7) {
         throw new RuntimeException("FATAL: UTF-8 encoding not supported.", var7);
      }
   }

   protected AbstractUnicodeExtraField(String text, byte[] bytes) {
      this(text, bytes, 0, bytes.length);
   }

   private void assembleData() {
      if(this.unicodeName != null) {
         this.data = new byte[5 + this.unicodeName.length];
         this.data[0] = 1;
         System.arraycopy(ZipLong.getBytes(this.nameCRC32), 0, this.data, 1, 4);
         System.arraycopy(this.unicodeName, 0, this.data, 5, this.unicodeName.length);
      }
   }

   public long getNameCRC32() {
      return this.nameCRC32;
   }

   public void setNameCRC32(long nameCRC32) {
      this.nameCRC32 = nameCRC32;
      this.data = null;
   }

   public byte[] getUnicodeName() {
      byte[] b = null;
      if(this.unicodeName != null) {
         b = new byte[this.unicodeName.length];
         System.arraycopy(this.unicodeName, 0, b, 0, b.length);
      }

      return b;
   }

   public void setUnicodeName(byte[] unicodeName) {
      if(unicodeName != null) {
         this.unicodeName = new byte[unicodeName.length];
         System.arraycopy(unicodeName, 0, this.unicodeName, 0, unicodeName.length);
      } else {
         this.unicodeName = null;
      }

      this.data = null;
   }

   public byte[] getCentralDirectoryData() {
      if(this.data == null) {
         this.assembleData();
      }

      byte[] b = null;
      if(this.data != null) {
         b = new byte[this.data.length];
         System.arraycopy(this.data, 0, b, 0, b.length);
      }

      return b;
   }

   public ZipShort getCentralDirectoryLength() {
      if(this.data == null) {
         this.assembleData();
      }

      return new ZipShort(this.data != null?this.data.length:0);
   }

   public byte[] getLocalFileDataData() {
      return this.getCentralDirectoryData();
   }

   public ZipShort getLocalFileDataLength() {
      return this.getCentralDirectoryLength();
   }

   public void parseFromLocalFileData(byte[] buffer, int offset, int length) throws ZipException {
      if(length < 5) {
         throw new ZipException("UniCode path extra data must have at least 5 bytes.");
      } else {
         int version = buffer[offset];
         if(version != 1) {
            throw new ZipException("Unsupported version [" + version + "] for UniCode path extra data.");
         } else {
            this.nameCRC32 = ZipLong.getValue(buffer, offset + 1);
            this.unicodeName = new byte[length - 5];
            System.arraycopy(buffer, offset + 5, this.unicodeName, 0, length - 5);
            this.data = null;
         }
      }
   }

   public void parseFromCentralDirectoryData(byte[] buffer, int offset, int length) throws ZipException {
      this.parseFromLocalFileData(buffer, offset, length);
   }
}
