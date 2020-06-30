package net.jpountz.xxhash;

import java.nio.ByteBuffer;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.SafeUtils;
import net.jpountz.xxhash.XXHash32;

final class XXHash32JavaSafe extends XXHash32 {
   public static final XXHash32 INSTANCE = new XXHash32JavaSafe();

   public int hash(byte[] buf, int off, int len, int seed) {
      SafeUtils.checkRange(buf, off, len);
      int end = off + len;
      int h32;
      if(len >= 16) {
         int limit = end - 16;
         int v1 = seed + -1640531535 + -2048144777;
         int v2 = seed + -2048144777;
         int v3 = seed + 0;
         int v4 = seed - -1640531535;

         while(true) {
            v1 = v1 + SafeUtils.readIntLE(buf, off) * -2048144777;
            v1 = Integer.rotateLeft(v1, 13);
            v1 = v1 * -1640531535;
            off = off + 4;
            v2 = v2 + SafeUtils.readIntLE(buf, off) * -2048144777;
            v2 = Integer.rotateLeft(v2, 13);
            v2 = v2 * -1640531535;
            off = off + 4;
            v3 = v3 + SafeUtils.readIntLE(buf, off) * -2048144777;
            v3 = Integer.rotateLeft(v3, 13);
            v3 = v3 * -1640531535;
            off = off + 4;
            v4 = v4 + SafeUtils.readIntLE(buf, off) * -2048144777;
            v4 = Integer.rotateLeft(v4, 13);
            v4 = v4 * -1640531535;
            off = off + 4;
            if(off > limit) {
               break;
            }
         }

         h32 = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7) + Integer.rotateLeft(v3, 12) + Integer.rotateLeft(v4, 18);
      } else {
         h32 = seed + 374761393;
      }

      for(h32 = h32 + len; off <= end - 4; off += 4) {
         h32 = h32 + SafeUtils.readIntLE(buf, off) * -1028477379;
         h32 = Integer.rotateLeft(h32, 17) * 668265263;
      }

      while(off < end) {
         h32 = h32 + (SafeUtils.readByte(buf, off) & 255) * 374761393;
         h32 = Integer.rotateLeft(h32, 11) * -1640531535;
         ++off;
      }

      h32 = h32 ^ h32 >>> 15;
      h32 = h32 * -2048144777;
      h32 = h32 ^ h32 >>> 13;
      h32 = h32 * -1028477379;
      h32 = h32 ^ h32 >>> 16;
      return h32;
   }

   public int hash(ByteBuffer buf, int off, int len, int seed) {
      if(buf.hasArray()) {
         return this.hash(buf.array(), off, len, seed);
      } else {
         ByteBufferUtils.checkRange(buf, off, len);
         buf = ByteBufferUtils.inLittleEndianOrder(buf);
         int end = off + len;
         int h32;
         if(len >= 16) {
            int limit = end - 16;
            int v1 = seed + -1640531535 + -2048144777;
            int v2 = seed + -2048144777;
            int v3 = seed + 0;
            int v4 = seed - -1640531535;

            while(true) {
               v1 = v1 + ByteBufferUtils.readIntLE(buf, off) * -2048144777;
               v1 = Integer.rotateLeft(v1, 13);
               v1 = v1 * -1640531535;
               off = off + 4;
               v2 = v2 + ByteBufferUtils.readIntLE(buf, off) * -2048144777;
               v2 = Integer.rotateLeft(v2, 13);
               v2 = v2 * -1640531535;
               off = off + 4;
               v3 = v3 + ByteBufferUtils.readIntLE(buf, off) * -2048144777;
               v3 = Integer.rotateLeft(v3, 13);
               v3 = v3 * -1640531535;
               off = off + 4;
               v4 = v4 + ByteBufferUtils.readIntLE(buf, off) * -2048144777;
               v4 = Integer.rotateLeft(v4, 13);
               v4 = v4 * -1640531535;
               off = off + 4;
               if(off > limit) {
                  break;
               }
            }

            h32 = Integer.rotateLeft(v1, 1) + Integer.rotateLeft(v2, 7) + Integer.rotateLeft(v3, 12) + Integer.rotateLeft(v4, 18);
         } else {
            h32 = seed + 374761393;
         }

         for(h32 = h32 + len; off <= end - 4; off += 4) {
            h32 = h32 + ByteBufferUtils.readIntLE(buf, off) * -1028477379;
            h32 = Integer.rotateLeft(h32, 17) * 668265263;
         }

         while(off < end) {
            h32 = h32 + (ByteBufferUtils.readByte(buf, off) & 255) * 374761393;
            h32 = Integer.rotateLeft(h32, 11) * -1640531535;
            ++off;
         }

         h32 = h32 ^ h32 >>> 15;
         h32 = h32 * -2048144777;
         h32 = h32 ^ h32 >>> 13;
         h32 = h32 * -1028477379;
         h32 = h32 ^ h32 >>> 16;
         return h32;
      }
   }
}
