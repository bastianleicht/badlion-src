package com.jcraft.jogg;

import com.jcraft.jogg.Page;

public class SyncState {
   public byte[] data;
   int storage;
   int fill;
   int returned;
   int unsynced;
   int headerbytes;
   int bodybytes;
   private Page pageseek = new Page();
   private byte[] chksum = new byte[4];

   public int clear() {
      this.data = null;
      return 0;
   }

   public int buffer(int size) {
      if(this.returned != 0) {
         this.fill -= this.returned;
         if(this.fill > 0) {
            System.arraycopy(this.data, this.returned, this.data, 0, this.fill);
         }

         this.returned = 0;
      }

      if(size > this.storage - this.fill) {
         int newsize = size + this.fill + 4096;
         if(this.data != null) {
            byte[] foo = new byte[newsize];
            System.arraycopy(this.data, 0, foo, 0, this.data.length);
            this.data = foo;
         } else {
            this.data = new byte[newsize];
         }

         this.storage = newsize;
      }

      return this.fill;
   }

   public int wrote(int bytes) {
      if(this.fill + bytes > this.storage) {
         return -1;
      } else {
         this.fill += bytes;
         return 0;
      }
   }

   public int pageseek(Page og) {
      int page = this.returned;
      int bytes = this.fill - this.returned;
      if(this.headerbytes == 0) {
         if(bytes < 27) {
            return 0;
         }

         if(this.data[page] != 79 || this.data[page + 1] != 103 || this.data[page + 2] != 103 || this.data[page + 3] != 83) {
            this.headerbytes = 0;
            this.bodybytes = 0;
            int next = 0;

            for(int ii = 0; ii < bytes - 1; ++ii) {
               if(this.data[page + 1 + ii] == 79) {
                  next = page + 1 + ii;
                  break;
               }
            }

            if(next == 0) {
               next = this.fill;
            }

            this.returned = next;
            return -(next - page);
         }

         int _headerbytes = (this.data[page + 26] & 255) + 27;
         if(bytes < _headerbytes) {
            return 0;
         }

         for(int i = 0; i < (this.data[page + 26] & 255); ++i) {
            this.bodybytes += this.data[page + 27 + i] & 255;
         }

         this.headerbytes = _headerbytes;
      }

      if(this.bodybytes + this.headerbytes > bytes) {
         return 0;
      } else {
         synchronized(this.chksum) {
            System.arraycopy(this.data, page + 22, this.chksum, 0, 4);
            this.data[page + 22] = 0;
            this.data[page + 23] = 0;
            this.data[page + 24] = 0;
            this.data[page + 25] = 0;
            Page log = this.pageseek;
            log.header_base = this.data;
            log.header = page;
            log.header_len = this.headerbytes;
            log.body_base = this.data;
            log.body = page + this.headerbytes;
            log.body_len = this.bodybytes;
            log.checksum();
            if(this.chksum[0] != this.data[page + 22] || this.chksum[1] != this.data[page + 23] || this.chksum[2] != this.data[page + 24] || this.chksum[3] != this.data[page + 25]) {
               System.arraycopy(this.chksum, 0, this.data, page + 22, 4);
               this.headerbytes = 0;
               this.bodybytes = 0;
               int next = 0;

               for(int ii = 0; ii < bytes - 1; ++ii) {
                  if(this.data[page + 1 + ii] == 79) {
                     next = page + 1 + ii;
                     break;
                  }
               }

               if(next == 0) {
                  next = this.fill;
               }

               this.returned = next;
               return -(next - page);
            }
         }

         page = this.returned;
         if(og != null) {
            og.header_base = this.data;
            og.header = page;
            og.header_len = this.headerbytes;
            og.body_base = this.data;
            og.body = page + this.headerbytes;
            og.body_len = this.bodybytes;
         }

         this.unsynced = 0;
         this.returned += bytes = this.headerbytes + this.bodybytes;
         this.headerbytes = 0;
         this.bodybytes = 0;
         return bytes;
      }
   }

   public int pageout(Page og) {
      while(true) {
         int ret = this.pageseek(og);
         if(ret > 0) {
            return 1;
         }

         if(ret == 0) {
            return 0;
         }

         if(this.unsynced == 0) {
            break;
         }
      }

      this.unsynced = 1;
      return -1;
   }

   public int reset() {
      this.fill = 0;
      this.returned = 0;
      this.unsynced = 0;
      this.headerbytes = 0;
      this.bodybytes = 0;
      return 0;
   }

   public void init() {
   }

   public int getDataOffset() {
      return this.returned;
   }

   public int getBufferOffset() {
      return this.fill;
   }
}
