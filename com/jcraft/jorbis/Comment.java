package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;

public class Comment {
   private static byte[] _vorbis = "vorbis".getBytes();
   private static byte[] _vendor = "Xiphophorus libVorbis I 20000508".getBytes();
   private static final int OV_EIMPL = -130;
   public byte[][] user_comments;
   public int[] comment_lengths;
   public int comments;
   public byte[] vendor;

   public void init() {
      this.user_comments = (byte[][])null;
      this.comments = 0;
      this.vendor = null;
   }

   public void add(String comment) {
      this.add(comment.getBytes());
   }

   private void add(byte[] comment) {
      byte[][] foo = new byte[this.comments + 2][];
      if(this.user_comments != null) {
         System.arraycopy(this.user_comments, 0, foo, 0, this.comments);
      }

      this.user_comments = foo;
      int[] goo = new int[this.comments + 2];
      if(this.comment_lengths != null) {
         System.arraycopy(this.comment_lengths, 0, goo, 0, this.comments);
      }

      this.comment_lengths = goo;
      byte[] bar = new byte[comment.length + 1];
      System.arraycopy(comment, 0, bar, 0, comment.length);
      this.user_comments[this.comments] = bar;
      this.comment_lengths[this.comments] = comment.length;
      ++this.comments;
      this.user_comments[this.comments] = null;
   }

   public void add_tag(String tag, String contents) {
      if(contents == null) {
         contents = "";
      }

      this.add(tag + "=" + contents);
   }

   static boolean tagcompare(byte[] s1, byte[] s2, int n) {
      for(int c = 0; c < n; ++c) {
         byte u1 = s1[c];
         byte u2 = s2[c];
         if(90 >= u1 && u1 >= 65) {
            u1 = (byte)(u1 - 65 + 97);
         }

         if(90 >= u2 && u2 >= 65) {
            u2 = (byte)(u2 - 65 + 97);
         }

         if(u1 != u2) {
            return false;
         }
      }

      return true;
   }

   public String query(String tag) {
      return this.query((String)tag, 0);
   }

   public String query(String tag, int count) {
      int foo = this.query(tag.getBytes(), count);
      if(foo == -1) {
         return null;
      } else {
         byte[] comment = this.user_comments[foo];

         for(int i = 0; i < this.comment_lengths[foo]; ++i) {
            if(comment[i] == 61) {
               return new String(comment, i + 1, this.comment_lengths[foo] - (i + 1));
            }
         }

         return null;
      }
   }

   private int query(byte[] tag, int count) {
      int i = 0;
      int found = 0;
      int fulltaglen = tag.length + 1;
      byte[] fulltag = new byte[fulltaglen];
      System.arraycopy(tag, 0, fulltag, 0, tag.length);
      fulltag[tag.length] = 61;

      for(i = 0; i < this.comments; ++i) {
         if(tagcompare(this.user_comments[i], fulltag, fulltaglen)) {
            if(count == found) {
               return i;
            }

            ++found;
         }
      }

      return -1;
   }

   int unpack(Buffer opb) {
      int vendorlen = opb.read(32);
      if(vendorlen < 0) {
         this.clear();
         return -1;
      } else {
         this.vendor = new byte[vendorlen + 1];
         opb.read(this.vendor, vendorlen);
         this.comments = opb.read(32);
         if(this.comments < 0) {
            this.clear();
            return -1;
         } else {
            this.user_comments = new byte[this.comments + 1][];
            this.comment_lengths = new int[this.comments + 1];

            for(int i = 0; i < this.comments; ++i) {
               int len = opb.read(32);
               if(len < 0) {
                  this.clear();
                  return -1;
               }

               this.comment_lengths[i] = len;
               this.user_comments[i] = new byte[len + 1];
               opb.read(this.user_comments[i], len);
            }

            if(opb.read(1) != 1) {
               this.clear();
               return -1;
            } else {
               return 0;
            }
         }
      }
   }

   int pack(Buffer opb) {
      opb.write(3, 8);
      opb.write(_vorbis);
      opb.write(_vendor.length, 32);
      opb.write(_vendor);
      opb.write(this.comments, 32);
      if(this.comments != 0) {
         for(int i = 0; i < this.comments; ++i) {
            if(this.user_comments[i] != null) {
               opb.write(this.comment_lengths[i], 32);
               opb.write(this.user_comments[i]);
            } else {
               opb.write(0, 32);
            }
         }
      }

      opb.write(1, 1);
      return 0;
   }

   public int header_out(Packet op) {
      Buffer opb = new Buffer();
      opb.writeinit();
      if(this.pack(opb) != 0) {
         return -130;
      } else {
         op.packet_base = new byte[opb.bytes()];
         op.packet = 0;
         op.bytes = opb.bytes();
         System.arraycopy(opb.buffer(), 0, op.packet_base, 0, op.bytes);
         op.b_o_s = 0;
         op.e_o_s = 0;
         op.granulepos = 0L;
         return 0;
      }
   }

   void clear() {
      for(int i = 0; i < this.comments; ++i) {
         this.user_comments[i] = null;
      }

      this.user_comments = (byte[][])null;
      this.vendor = null;
   }

   public String getVendor() {
      return new String(this.vendor, 0, this.vendor.length - 1);
   }

   public String getComment(int i) {
      return this.comments <= i?null:new String(this.user_comments[i], 0, this.user_comments[i].length - 1);
   }

   public String toString() {
      String foo = "Vendor: " + new String(this.vendor, 0, this.vendor.length - 1);

      for(int i = 0; i < this.comments; ++i) {
         foo = foo + "\nComment: " + new String(this.user_comments[i], 0, this.user_comments[i].length - 1);
      }

      foo = foo + "\n";
      return foo;
   }
}
