package org.apache.commons.io;

import java.io.Serializable;

public class ByteOrderMark implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final ByteOrderMark UTF_8 = new ByteOrderMark("UTF-8", new int[]{239, 187, 191});
   public static final ByteOrderMark UTF_16BE = new ByteOrderMark("UTF-16BE", new int[]{254, 255});
   public static final ByteOrderMark UTF_16LE = new ByteOrderMark("UTF-16LE", new int[]{255, 254});
   public static final ByteOrderMark UTF_32BE = new ByteOrderMark("UTF-32BE", new int[]{0, 0, 254, 255});
   public static final ByteOrderMark UTF_32LE = new ByteOrderMark("UTF-32LE", new int[]{255, 254, 0, 0});
   private final String charsetName;
   private final int[] bytes;

   public ByteOrderMark(String charsetName, int... bytes) {
      if(charsetName != null && charsetName.length() != 0) {
         if(bytes != null && bytes.length != 0) {
            this.charsetName = charsetName;
            this.bytes = new int[bytes.length];
            System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
         } else {
            throw new IllegalArgumentException("No bytes specified");
         }
      } else {
         throw new IllegalArgumentException("No charsetName specified");
      }
   }

   public String getCharsetName() {
      return this.charsetName;
   }

   public int length() {
      return this.bytes.length;
   }

   public int get(int pos) {
      return this.bytes[pos];
   }

   public byte[] getBytes() {
      byte[] copy = new byte[this.bytes.length];

      for(int i = 0; i < this.bytes.length; ++i) {
         copy[i] = (byte)this.bytes[i];
      }

      return copy;
   }

   public boolean equals(Object obj) {
      if(!(obj instanceof ByteOrderMark)) {
         return false;
      } else {
         ByteOrderMark bom = (ByteOrderMark)obj;
         if(this.bytes.length != bom.length()) {
            return false;
         } else {
            for(int i = 0; i < this.bytes.length; ++i) {
               if(this.bytes[i] != bom.get(i)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      int hashCode = this.getClass().hashCode();

      for(int b : this.bytes) {
         hashCode += b;
      }

      return hashCode;
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getClass().getSimpleName());
      builder.append('[');
      builder.append(this.charsetName);
      builder.append(": ");

      for(int i = 0; i < this.bytes.length; ++i) {
         if(i > 0) {
            builder.append(",");
         }

         builder.append("0x");
         builder.append(Integer.toHexString(255 & this.bytes[i]).toUpperCase());
      }

      builder.append(']');
      return builder.toString();
   }
}
