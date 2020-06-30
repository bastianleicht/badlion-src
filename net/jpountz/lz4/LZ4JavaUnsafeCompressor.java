package net.jpountz.lz4;

import java.nio.ByteBuffer;
import java.util.Arrays;
import net.jpountz.lz4.LZ4ByteBufferUtils;
import net.jpountz.lz4.LZ4Compressor;
import net.jpountz.lz4.LZ4Constants;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4UnsafeUtils;
import net.jpountz.lz4.LZ4Utils;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.UnsafeUtils;

final class LZ4JavaUnsafeCompressor extends LZ4Compressor {
   public static final LZ4Compressor INSTANCE = new LZ4JavaUnsafeCompressor();

   static int compress64k(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int destEnd) {
      int srcEnd = srcOff + srcLen;
      int srcLimit = srcEnd - 5;
      int mflimit = srcEnd - 12;
      int dOff = destOff;
      int anchor = srcOff;
      if(srcLen >= 13) {
         short[] hashTable = new short[8192];
         int var23 = srcOff + 1;

         label45:
         while(true) {
            int forwardOff = var23;
            int step = 1;
            int searchMatchNb = 1 << LZ4Constants.SKIP_STRENGTH;

            int ref;
            while(true) {
               var23 = forwardOff;
               forwardOff += step;
               step = searchMatchNb++ >>> LZ4Constants.SKIP_STRENGTH;
               if(forwardOff > mflimit) {
                  break label45;
               }

               int h = LZ4Utils.hash64k(UnsafeUtils.readInt(src, var23));
               ref = srcOff + UnsafeUtils.readShort(hashTable, h);
               UnsafeUtils.writeShort(hashTable, h, var23 - srcOff);
               if(LZ4UnsafeUtils.readIntEquals(src, ref, var23)) {
                  break;
               }
            }

            int excess = LZ4UnsafeUtils.commonBytesBackward(src, ref, var23, srcOff, anchor);
            var23 = var23 - excess;
            ref = ref - excess;
            int runLen = var23 - anchor;
            int tokenOff = dOff++;
            if(dOff + runLen + 8 + (runLen >>> 8) > destEnd) {
               throw new LZ4Exception("maxDestLen is too small");
            }

            if(runLen >= 15) {
               UnsafeUtils.writeByte(dest, tokenOff, (int)240);
               dOff = LZ4UnsafeUtils.writeLen(runLen - 15, dest, dOff);
            } else {
               UnsafeUtils.writeByte(dest, tokenOff, runLen << 4);
            }

            LZ4UnsafeUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
            dOff += runLen;

            while(true) {
               UnsafeUtils.writeShortLE(dest, dOff, (short)(var23 - ref));
               dOff += 2;
               var23 = var23 + 4;
               ref = ref + 4;
               int matchLen = LZ4UnsafeUtils.commonBytes(src, ref, var23, srcLimit);
               if(dOff + 6 + (matchLen >>> 8) > destEnd) {
                  throw new LZ4Exception("maxDestLen is too small");
               }

               var23 = var23 + matchLen;
               if(matchLen >= 15) {
                  UnsafeUtils.writeByte(dest, tokenOff, UnsafeUtils.readByte(dest, tokenOff) | 15);
                  dOff = LZ4UnsafeUtils.writeLen(matchLen - 15, dest, dOff);
               } else {
                  UnsafeUtils.writeByte(dest, tokenOff, UnsafeUtils.readByte(dest, tokenOff) | matchLen);
               }

               if(var23 > mflimit) {
                  anchor = var23;
                  break label45;
               }

               UnsafeUtils.writeShort(hashTable, LZ4Utils.hash64k(UnsafeUtils.readInt(src, var23 - 2)), var23 - 2 - srcOff);
               int h = LZ4Utils.hash64k(UnsafeUtils.readInt(src, var23));
               ref = srcOff + UnsafeUtils.readShort(hashTable, h);
               UnsafeUtils.writeShort(hashTable, h, var23 - srcOff);
               if(!LZ4UnsafeUtils.readIntEquals(src, var23, ref)) {
                  anchor = var23++;
                  break;
               }

               tokenOff = dOff++;
               UnsafeUtils.writeByte(dest, tokenOff, (int)0);
            }
         }
      }

      dOff = LZ4UnsafeUtils.lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
      return dOff - destOff;
   }

   public int compress(byte[] src, int srcOff, int srcLen, byte[] dest, int destOff, int maxDestLen) {
      UnsafeUtils.checkRange(src, srcOff, srcLen);
      UnsafeUtils.checkRange(dest, destOff, maxDestLen);
      int destEnd = destOff + maxDestLen;
      if(srcLen < 65547) {
         return compress64k(src, srcOff, srcLen, dest, destOff, destEnd);
      } else {
         int srcEnd = srcOff + srcLen;
         int srcLimit = srcEnd - 5;
         int mflimit = srcEnd - 12;
         int dOff = destOff;
         int var26 = srcOff + 1;
         int anchor = srcOff;
         int[] hashTable = new int[4096];
         Arrays.fill(hashTable, srcOff);

         label87:
         while(true) {
            int forwardOff = var26;
            int step = 1;
            int searchMatchNb = 1 << LZ4Constants.SKIP_STRENGTH;

            while(true) {
               var26 = forwardOff;
               forwardOff += step;
               step = searchMatchNb++ >>> LZ4Constants.SKIP_STRENGTH;
               if(forwardOff <= mflimit) {
                  int h = LZ4Utils.hash(UnsafeUtils.readInt(src, var26));
                  int ref = UnsafeUtils.readInt(hashTable, h);
                  int back = var26 - ref;
                  UnsafeUtils.writeInt(hashTable, h, var26);
                  if(back >= 65536 || !LZ4UnsafeUtils.readIntEquals(src, ref, var26)) {
                     continue;
                  }

                  h = LZ4UnsafeUtils.commonBytesBackward(src, ref, var26, srcOff, anchor);
                  var26 = var26 - h;
                  ref = ref - h;
                  int runLen = var26 - anchor;
                  int tokenOff = dOff++;
                  if(dOff + runLen + 8 + (runLen >>> 8) > destEnd) {
                     throw new LZ4Exception("maxDestLen is too small");
                  }

                  if(runLen >= 15) {
                     UnsafeUtils.writeByte(dest, tokenOff, (int)240);
                     dOff = LZ4UnsafeUtils.writeLen(runLen - 15, dest, dOff);
                  } else {
                     UnsafeUtils.writeByte(dest, tokenOff, runLen << 4);
                  }

                  LZ4UnsafeUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
                  dOff += runLen;

                  while(true) {
                     UnsafeUtils.writeShortLE(dest, dOff, back);
                     dOff += 2;
                     var26 = var26 + 4;
                     int matchLen = LZ4UnsafeUtils.commonBytes(src, ref + 4, var26, srcLimit);
                     if(dOff + 6 + (matchLen >>> 8) > destEnd) {
                        throw new LZ4Exception("maxDestLen is too small");
                     }

                     var26 = var26 + matchLen;
                     if(matchLen >= 15) {
                        UnsafeUtils.writeByte(dest, tokenOff, UnsafeUtils.readByte(dest, tokenOff) | 15);
                        dOff = LZ4UnsafeUtils.writeLen(matchLen - 15, dest, dOff);
                     } else {
                        UnsafeUtils.writeByte(dest, tokenOff, UnsafeUtils.readByte(dest, tokenOff) | matchLen);
                     }

                     if(var26 > mflimit) {
                        anchor = var26;
                        break;
                     }

                     UnsafeUtils.writeInt(hashTable, LZ4Utils.hash(UnsafeUtils.readInt(src, var26 - 2)), var26 - 2);
                     int h = LZ4Utils.hash(UnsafeUtils.readInt(src, var26));
                     ref = UnsafeUtils.readInt(hashTable, h);
                     UnsafeUtils.writeInt(hashTable, h, var26);
                     back = var26 - ref;
                     if(back >= 65536 || !LZ4UnsafeUtils.readIntEquals(src, ref, var26)) {
                        anchor = var26++;
                        continue label87;
                     }

                     tokenOff = dOff++;
                     UnsafeUtils.writeByte(dest, tokenOff, (int)0);
                  }
               }

               dOff = LZ4UnsafeUtils.lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
               return dOff - destOff;
            }
         }
      }
   }

   static int compress64k(ByteBuffer src, int srcOff, int srcLen, ByteBuffer dest, int destOff, int destEnd) {
      int srcEnd = srcOff + srcLen;
      int srcLimit = srcEnd - 5;
      int mflimit = srcEnd - 12;
      int dOff = destOff;
      int anchor = srcOff;
      if(srcLen >= 13) {
         short[] hashTable = new short[8192];
         int var23 = srcOff + 1;

         label45:
         while(true) {
            int forwardOff = var23;
            int step = 1;
            int searchMatchNb = 1 << LZ4Constants.SKIP_STRENGTH;

            int ref;
            while(true) {
               var23 = forwardOff;
               forwardOff += step;
               step = searchMatchNb++ >>> LZ4Constants.SKIP_STRENGTH;
               if(forwardOff > mflimit) {
                  break label45;
               }

               int h = LZ4Utils.hash64k(ByteBufferUtils.readInt(src, var23));
               ref = srcOff + UnsafeUtils.readShort(hashTable, h);
               UnsafeUtils.writeShort(hashTable, h, var23 - srcOff);
               if(LZ4ByteBufferUtils.readIntEquals(src, ref, var23)) {
                  break;
               }
            }

            int excess = LZ4ByteBufferUtils.commonBytesBackward(src, ref, var23, srcOff, anchor);
            var23 = var23 - excess;
            ref = ref - excess;
            int runLen = var23 - anchor;
            int tokenOff = dOff++;
            if(dOff + runLen + 8 + (runLen >>> 8) > destEnd) {
               throw new LZ4Exception("maxDestLen is too small");
            }

            if(runLen >= 15) {
               ByteBufferUtils.writeByte(dest, tokenOff, 240);
               dOff = LZ4ByteBufferUtils.writeLen(runLen - 15, dest, dOff);
            } else {
               ByteBufferUtils.writeByte(dest, tokenOff, runLen << 4);
            }

            LZ4ByteBufferUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
            dOff += runLen;

            while(true) {
               ByteBufferUtils.writeShortLE(dest, dOff, (short)(var23 - ref));
               dOff += 2;
               var23 = var23 + 4;
               ref = ref + 4;
               int matchLen = LZ4ByteBufferUtils.commonBytes(src, ref, var23, srcLimit);
               if(dOff + 6 + (matchLen >>> 8) > destEnd) {
                  throw new LZ4Exception("maxDestLen is too small");
               }

               var23 = var23 + matchLen;
               if(matchLen >= 15) {
                  ByteBufferUtils.writeByte(dest, tokenOff, ByteBufferUtils.readByte(dest, tokenOff) | 15);
                  dOff = LZ4ByteBufferUtils.writeLen(matchLen - 15, dest, dOff);
               } else {
                  ByteBufferUtils.writeByte(dest, tokenOff, ByteBufferUtils.readByte(dest, tokenOff) | matchLen);
               }

               if(var23 > mflimit) {
                  anchor = var23;
                  break label45;
               }

               UnsafeUtils.writeShort(hashTable, LZ4Utils.hash64k(ByteBufferUtils.readInt(src, var23 - 2)), var23 - 2 - srcOff);
               int h = LZ4Utils.hash64k(ByteBufferUtils.readInt(src, var23));
               ref = srcOff + UnsafeUtils.readShort(hashTable, h);
               UnsafeUtils.writeShort(hashTable, h, var23 - srcOff);
               if(!LZ4ByteBufferUtils.readIntEquals(src, var23, ref)) {
                  anchor = var23++;
                  break;
               }

               tokenOff = dOff++;
               ByteBufferUtils.writeByte(dest, tokenOff, 0);
            }
         }
      }

      dOff = LZ4ByteBufferUtils.lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
      return dOff - destOff;
   }

   public int compress(ByteBuffer src, int srcOff, int srcLen, ByteBuffer dest, int destOff, int maxDestLen) {
      if(src.hasArray() && dest.hasArray()) {
         return this.compress(src.array(), srcOff, srcLen, dest.array(), destOff, maxDestLen);
      } else {
         src = ByteBufferUtils.inNativeByteOrder(src);
         dest = ByteBufferUtils.inNativeByteOrder(dest);
         ByteBufferUtils.checkRange(src, srcOff, srcLen);
         ByteBufferUtils.checkRange(dest, destOff, maxDestLen);
         int destEnd = destOff + maxDestLen;
         if(srcLen < 65547) {
            return compress64k(src, srcOff, srcLen, dest, destOff, destEnd);
         } else {
            int srcEnd = srcOff + srcLen;
            int srcLimit = srcEnd - 5;
            int mflimit = srcEnd - 12;
            int dOff = destOff;
            int var28 = srcOff + 1;
            int anchor = srcOff;
            int[] hashTable = new int[4096];
            Arrays.fill(hashTable, srcOff);

            label134:
            while(true) {
               int forwardOff = var28;
               int step = 1;
               int searchMatchNb = 1 << LZ4Constants.SKIP_STRENGTH;

               while(true) {
                  var28 = forwardOff;
                  forwardOff += step;
                  step = searchMatchNb++ >>> LZ4Constants.SKIP_STRENGTH;
                  if(forwardOff <= mflimit) {
                     int h = LZ4Utils.hash(ByteBufferUtils.readInt(src, var28));
                     int ref = UnsafeUtils.readInt(hashTable, h);
                     int back = var28 - ref;
                     UnsafeUtils.writeInt(hashTable, h, var28);
                     if(back >= 65536 || !LZ4ByteBufferUtils.readIntEquals(src, ref, var28)) {
                        continue;
                     }

                     h = LZ4ByteBufferUtils.commonBytesBackward(src, ref, var28, srcOff, anchor);
                     var28 = var28 - h;
                     ref = ref - h;
                     int runLen = var28 - anchor;
                     int tokenOff = dOff++;
                     if(dOff + runLen + 8 + (runLen >>> 8) > destEnd) {
                        throw new LZ4Exception("maxDestLen is too small");
                     }

                     if(runLen >= 15) {
                        ByteBufferUtils.writeByte(dest, tokenOff, 240);
                        dOff = LZ4ByteBufferUtils.writeLen(runLen - 15, dest, dOff);
                     } else {
                        ByteBufferUtils.writeByte(dest, tokenOff, runLen << 4);
                     }

                     LZ4ByteBufferUtils.wildArraycopy(src, anchor, dest, dOff, runLen);
                     dOff += runLen;

                     while(true) {
                        ByteBufferUtils.writeShortLE(dest, dOff, back);
                        dOff += 2;
                        var28 = var28 + 4;
                        int matchLen = LZ4ByteBufferUtils.commonBytes(src, ref + 4, var28, srcLimit);
                        if(dOff + 6 + (matchLen >>> 8) > destEnd) {
                           throw new LZ4Exception("maxDestLen is too small");
                        }

                        var28 = var28 + matchLen;
                        if(matchLen >= 15) {
                           ByteBufferUtils.writeByte(dest, tokenOff, ByteBufferUtils.readByte(dest, tokenOff) | 15);
                           dOff = LZ4ByteBufferUtils.writeLen(matchLen - 15, dest, dOff);
                        } else {
                           ByteBufferUtils.writeByte(dest, tokenOff, ByteBufferUtils.readByte(dest, tokenOff) | matchLen);
                        }

                        if(var28 > mflimit) {
                           anchor = var28;
                           break;
                        }

                        UnsafeUtils.writeInt(hashTable, LZ4Utils.hash(ByteBufferUtils.readInt(src, var28 - 2)), var28 - 2);
                        int h = LZ4Utils.hash(ByteBufferUtils.readInt(src, var28));
                        ref = UnsafeUtils.readInt(hashTable, h);
                        UnsafeUtils.writeInt(hashTable, h, var28);
                        back = var28 - ref;
                        if(back >= 65536 || !LZ4ByteBufferUtils.readIntEquals(src, ref, var28)) {
                           anchor = var28++;
                           continue label134;
                        }

                        tokenOff = dOff++;
                        ByteBufferUtils.writeByte(dest, tokenOff, 0);
                     }
                  }

                  dOff = LZ4ByteBufferUtils.lastLiterals(src, anchor, srcEnd - anchor, dest, dOff, destEnd);
                  return dOff - destOff;
               }
            }
         }
      }
   }
}
