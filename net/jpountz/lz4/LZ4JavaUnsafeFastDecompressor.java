package net.jpountz.lz4;

import java.nio.ByteBuffer;
import net.jpountz.lz4.LZ4ByteBufferUtils;
import net.jpountz.lz4.LZ4Exception;
import net.jpountz.lz4.LZ4FastDecompressor;
import net.jpountz.lz4.LZ4UnsafeUtils;
import net.jpountz.util.ByteBufferUtils;
import net.jpountz.util.UnsafeUtils;

final class LZ4JavaUnsafeFastDecompressor extends LZ4FastDecompressor {
   public static final LZ4FastDecompressor INSTANCE = new LZ4JavaUnsafeFastDecompressor();

   public int decompress(byte[] src, int srcOff, byte[] dest, int destOff, int destLen) {
      UnsafeUtils.checkRange(src, srcOff);
      UnsafeUtils.checkRange(dest, destOff, destLen);
      if(destLen == 0) {
         if(UnsafeUtils.readByte(src, srcOff) != 0) {
            throw new LZ4Exception("Malformed input at " + srcOff);
         } else {
            return 1;
         }
      } else {
         int destEnd = destOff + destLen;
         int sOff = srcOff;
         int dOff = destOff;

         while(true) {
            int token = UnsafeUtils.readByte(src, sOff) & 255;
            ++sOff;
            int literalLen = token >>> 4;
            if(literalLen == 15) {
               byte literalCopyEnd;
               for(len = -1; (literalCopyEnd = UnsafeUtils.readByte(src, sOff++)) == -1; literalLen += 255) {
                  ;
               }

               literalLen += literalCopyEnd & 255;
            }

            int literalCopyEnd = dOff + literalLen;
            if(literalCopyEnd > destEnd - 8) {
               if(literalCopyEnd != destEnd) {
                  throw new LZ4Exception("Malformed input at " + sOff);
               } else {
                  LZ4UnsafeUtils.safeArraycopy(src, sOff, dest, dOff, literalLen);
                  sOff = sOff + literalLen;
                  return sOff - srcOff;
               }
            }

            LZ4UnsafeUtils.wildArraycopy(src, sOff, dest, dOff, literalLen);
            sOff = sOff + literalLen;
            int matchDec = UnsafeUtils.readShortLE(src, sOff);
            sOff = sOff + 2;
            int matchOff = literalCopyEnd - matchDec;
            if(matchOff < destOff) {
               throw new LZ4Exception("Malformed input at " + sOff);
            }

            int matchLen = token & 15;
            if(matchLen == 15) {
               byte matchCopyEnd;
               for(len = -1; (matchCopyEnd = UnsafeUtils.readByte(src, sOff++)) == -1; matchLen += 255) {
                  ;
               }

               matchLen += matchCopyEnd & 255;
            }

            matchLen = matchLen + 4;
            int matchCopyEnd = literalCopyEnd + matchLen;
            if(matchCopyEnd > destEnd - 8) {
               if(matchCopyEnd > destEnd) {
                  throw new LZ4Exception("Malformed input at " + sOff);
               }

               LZ4UnsafeUtils.safeIncrementalCopy(dest, matchOff, literalCopyEnd, matchLen);
            } else {
               LZ4UnsafeUtils.wildIncrementalCopy(dest, matchOff, literalCopyEnd, matchCopyEnd);
            }

            dOff = matchCopyEnd;
         }
      }
   }

   public int decompress(ByteBuffer src, int srcOff, ByteBuffer dest, int destOff, int destLen) {
      if(src.hasArray() && dest.hasArray()) {
         return this.decompress(src.array(), srcOff, dest.array(), destOff, destLen);
      } else {
         src = ByteBufferUtils.inNativeByteOrder(src);
         dest = ByteBufferUtils.inNativeByteOrder(dest);
         ByteBufferUtils.checkRange(src, srcOff);
         ByteBufferUtils.checkRange(dest, destOff, destLen);
         if(destLen == 0) {
            if(ByteBufferUtils.readByte(src, srcOff) != 0) {
               throw new LZ4Exception("Malformed input at " + srcOff);
            } else {
               return 1;
            }
         } else {
            int destEnd = destOff + destLen;
            int sOff = srcOff;
            int dOff = destOff;

            while(true) {
               int token = ByteBufferUtils.readByte(src, sOff) & 255;
               ++sOff;
               int literalLen = token >>> 4;
               if(literalLen == 15) {
                  byte literalCopyEnd;
                  for(len = -1; (literalCopyEnd = ByteBufferUtils.readByte(src, sOff++)) == -1; literalLen += 255) {
                     ;
                  }

                  literalLen += literalCopyEnd & 255;
               }

               int literalCopyEnd = dOff + literalLen;
               if(literalCopyEnd > destEnd - 8) {
                  if(literalCopyEnd != destEnd) {
                     throw new LZ4Exception("Malformed input at " + sOff);
                  } else {
                     LZ4ByteBufferUtils.safeArraycopy(src, sOff, dest, dOff, literalLen);
                     sOff = sOff + literalLen;
                     return sOff - srcOff;
                  }
               }

               LZ4ByteBufferUtils.wildArraycopy(src, sOff, dest, dOff, literalLen);
               sOff = sOff + literalLen;
               int matchDec = ByteBufferUtils.readShortLE(src, sOff);
               sOff = sOff + 2;
               int matchOff = literalCopyEnd - matchDec;
               if(matchOff < destOff) {
                  throw new LZ4Exception("Malformed input at " + sOff);
               }

               int matchLen = token & 15;
               if(matchLen == 15) {
                  byte matchCopyEnd;
                  for(len = -1; (matchCopyEnd = ByteBufferUtils.readByte(src, sOff++)) == -1; matchLen += 255) {
                     ;
                  }

                  matchLen += matchCopyEnd & 255;
               }

               matchLen = matchLen + 4;
               int matchCopyEnd = literalCopyEnd + matchLen;
               if(matchCopyEnd > destEnd - 8) {
                  if(matchCopyEnd > destEnd) {
                     throw new LZ4Exception("Malformed input at " + sOff);
                  }

                  LZ4ByteBufferUtils.safeIncrementalCopy(dest, matchOff, literalCopyEnd, matchLen);
               } else {
                  LZ4ByteBufferUtils.wildIncrementalCopy(dest, matchOff, literalCopyEnd, matchCopyEnd);
               }

               dOff = matchCopyEnd;
            }
         }
      }
   }
}
