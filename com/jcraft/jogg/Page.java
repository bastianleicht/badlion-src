package com.jcraft.jogg;

public class Page {
   private static int[] crc_lookup = new int[256];
   public byte[] header_base;
   public int header;
   public int header_len;
   public byte[] body_base;
   public int body;
   public int body_len;

   private static int crc_entry(int index) {
      int r = index << 24;

      for(int i = 0; i < 8; ++i) {
         if((r & Integer.MIN_VALUE) != 0) {
            r = r << 1 ^ 79764919;
         } else {
            r <<= 1;
         }
      }

      return r & -1;
   }

   int version() {
      return this.header_base[this.header + 4] & 255;
   }

   int continued() {
      return this.header_base[this.header + 5] & 1;
   }

   public int bos() {
      return this.header_base[this.header + 5] & 2;
   }

   public int eos() {
      return this.header_base[this.header + 5] & 4;
   }

   public long granulepos() {
      long foo = (long)(this.header_base[this.header + 13] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 12] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 11] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 10] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 9] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 8] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 7] & 255);
      foo = foo << 8 | (long)(this.header_base[this.header + 6] & 255);
      return foo;
   }

   public int serialno() {
      return this.header_base[this.header + 14] & 255 | (this.header_base[this.header + 15] & 255) << 8 | (this.header_base[this.header + 16] & 255) << 16 | (this.header_base[this.header + 17] & 255) << 24;
   }

   int pageno() {
      return this.header_base[this.header + 18] & 255 | (this.header_base[this.header + 19] & 255) << 8 | (this.header_base[this.header + 20] & 255) << 16 | (this.header_base[this.header + 21] & 255) << 24;
   }

   void checksum() {
      int crc_reg = 0;

      for(int i = 0; i < this.header_len; ++i) {
         crc_reg = crc_reg << 8 ^ crc_lookup[crc_reg >>> 24 & 255 ^ this.header_base[this.header + i] & 255];
      }

      for(int i = 0; i < this.body_len; ++i) {
         crc_reg = crc_reg << 8 ^ crc_lookup[crc_reg >>> 24 & 255 ^ this.body_base[this.body + i] & 255];
      }

      this.header_base[this.header + 22] = (byte)crc_reg;
      this.header_base[this.header + 23] = (byte)(crc_reg >>> 8);
      this.header_base[this.header + 24] = (byte)(crc_reg >>> 16);
      this.header_base[this.header + 25] = (byte)(crc_reg >>> 24);
   }

   public Page copy() {
      return this.copy(new Page());
   }

   public Page copy(Page p) {
      byte[] tmp = new byte[this.header_len];
      System.arraycopy(this.header_base, this.header, tmp, 0, this.header_len);
      p.header_len = this.header_len;
      p.header_base = tmp;
      p.header = 0;
      tmp = new byte[this.body_len];
      System.arraycopy(this.body_base, this.body, tmp, 0, this.body_len);
      p.body_len = this.body_len;
      p.body_base = tmp;
      p.body = 0;
      return p;
   }

   static {
      for(int i = 0; i < crc_lookup.length; ++i) {
         crc_lookup[i] = crc_entry(i);
      }

   }
}
