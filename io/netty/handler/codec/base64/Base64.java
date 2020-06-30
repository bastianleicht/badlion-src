package io.netty.handler.codec.base64;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64Dialect;

public final class Base64 {
   private static final int MAX_LINE_LENGTH = 76;
   private static final byte EQUALS_SIGN = 61;
   private static final byte NEW_LINE = 10;
   private static final byte WHITE_SPACE_ENC = -5;
   private static final byte EQUALS_SIGN_ENC = -1;

   private static byte[] alphabet(Base64Dialect dialect) {
      if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         return dialect.alphabet;
      }
   }

   private static byte[] decodabet(Base64Dialect dialect) {
      if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         return dialect.decodabet;
      }
   }

   private static boolean breakLines(Base64Dialect dialect) {
      if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         return dialect.breakLinesByDefault;
      }
   }

   public static ByteBuf encode(ByteBuf src) {
      return encode(src, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, Base64Dialect dialect) {
      return encode(src, breakLines(dialect), dialect);
   }

   public static ByteBuf encode(ByteBuf src, boolean breakLines) {
      return encode(src, breakLines, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, boolean breakLines, Base64Dialect dialect) {
      if(src == null) {
         throw new NullPointerException("src");
      } else {
         ByteBuf dest = encode(src, src.readerIndex(), src.readableBytes(), breakLines, dialect);
         src.readerIndex(src.writerIndex());
         return dest;
      }
   }

   public static ByteBuf encode(ByteBuf src, int off, int len) {
      return encode(src, off, len, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, Base64Dialect dialect) {
      return encode(src, off, len, breakLines(dialect), dialect);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines) {
      return encode(src, off, len, breakLines, Base64Dialect.STANDARD);
   }

   public static ByteBuf encode(ByteBuf src, int off, int len, boolean breakLines, Base64Dialect dialect) {
      if(src == null) {
         throw new NullPointerException("src");
      } else if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         int len43 = len * 4 / 3;
         ByteBuf dest = Unpooled.buffer(len43 + (len % 3 > 0?4:0) + (breakLines?len43 / 76:0)).order(src.order());
         int d = 0;
         int e = 0;
         int len2 = len - 2;

         for(int lineLength = 0; d < len2; e += 4) {
            encode3to4(src, d + off, 3, dest, e, dialect);
            lineLength += 4;
            if(breakLines && lineLength == 76) {
               dest.setByte(e + 4, 10);
               ++e;
               lineLength = 0;
            }

            d += 3;
         }

         if(d < len) {
            encode3to4(src, d + off, len - d, dest, e, dialect);
            e += 4;
         }

         return dest.slice(0, e);
      }
   }

   private static void encode3to4(ByteBuf src, int srcOffset, int numSigBytes, ByteBuf dest, int destOffset, Base64Dialect dialect) {
      byte[] ALPHABET = alphabet(dialect);
      int inBuff = (numSigBytes > 0?src.getByte(srcOffset) << 24 >>> 8:0) | (numSigBytes > 1?src.getByte(srcOffset + 1) << 24 >>> 16:0) | (numSigBytes > 2?src.getByte(srcOffset + 2) << 24 >>> 24:0);
      switch(numSigBytes) {
      case 1:
         dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
         dest.setByte(destOffset + 1, ALPHABET[inBuff >>> 12 & 63]);
         dest.setByte(destOffset + 2, 61);
         dest.setByte(destOffset + 3, 61);
         break;
      case 2:
         dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
         dest.setByte(destOffset + 1, ALPHABET[inBuff >>> 12 & 63]);
         dest.setByte(destOffset + 2, ALPHABET[inBuff >>> 6 & 63]);
         dest.setByte(destOffset + 3, 61);
         break;
      case 3:
         dest.setByte(destOffset, ALPHABET[inBuff >>> 18]);
         dest.setByte(destOffset + 1, ALPHABET[inBuff >>> 12 & 63]);
         dest.setByte(destOffset + 2, ALPHABET[inBuff >>> 6 & 63]);
         dest.setByte(destOffset + 3, ALPHABET[inBuff & 63]);
      }

   }

   public static ByteBuf decode(ByteBuf src) {
      return decode(src, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf src, Base64Dialect dialect) {
      if(src == null) {
         throw new NullPointerException("src");
      } else {
         ByteBuf dest = decode(src, src.readerIndex(), src.readableBytes(), dialect);
         src.readerIndex(src.writerIndex());
         return dest;
      }
   }

   public static ByteBuf decode(ByteBuf src, int off, int len) {
      return decode(src, off, len, Base64Dialect.STANDARD);
   }

   public static ByteBuf decode(ByteBuf src, int off, int len, Base64Dialect dialect) {
      if(src == null) {
         throw new NullPointerException("src");
      } else if(dialect == null) {
         throw new NullPointerException("dialect");
      } else {
         byte[] DECODABET = decodabet(dialect);
         int len34 = len * 3 / 4;
         ByteBuf dest = src.alloc().buffer(len34).order(src.order());
         int outBuffPosn = 0;
         byte[] b4 = new byte[4];
         int b4Posn = 0;

         for(int i = off; i < off + len; ++i) {
            byte sbiCrop = (byte)(src.getByte(i) & 127);
            byte sbiDecode = DECODABET[sbiCrop];
            if(sbiDecode < -5) {
               throw new IllegalArgumentException("bad Base64 input character at " + i + ": " + src.getUnsignedByte(i) + " (decimal)");
            }

            if(sbiDecode >= -1) {
               b4[b4Posn++] = sbiCrop;
               if(b4Posn > 3) {
                  outBuffPosn += decode4to3(b4, 0, dest, outBuffPosn, dialect);
                  b4Posn = 0;
                  if(sbiCrop == 61) {
                     break;
                  }
               }
            }
         }

         return dest.slice(0, outBuffPosn);
      }
   }

   private static int decode4to3(byte[] src, int srcOffset, ByteBuf dest, int destOffset, Base64Dialect dialect) {
      byte[] DECODABET = decodabet(dialect);
      if(src[srcOffset + 2] == 61) {
         int outBuff = (DECODABET[src[srcOffset]] & 255) << 18 | (DECODABET[src[srcOffset + 1]] & 255) << 12;
         dest.setByte(destOffset, (byte)(outBuff >>> 16));
         return 1;
      } else if(src[srcOffset + 3] == 61) {
         int outBuff = (DECODABET[src[srcOffset]] & 255) << 18 | (DECODABET[src[srcOffset + 1]] & 255) << 12 | (DECODABET[src[srcOffset + 2]] & 255) << 6;
         dest.setByte(destOffset, (byte)(outBuff >>> 16));
         dest.setByte(destOffset + 1, (byte)(outBuff >>> 8));
         return 2;
      } else {
         int outBuff;
         try {
            outBuff = (DECODABET[src[srcOffset]] & 255) << 18 | (DECODABET[src[srcOffset + 1]] & 255) << 12 | (DECODABET[src[srcOffset + 2]] & 255) << 6 | DECODABET[src[srcOffset + 3]] & 255;
         } catch (IndexOutOfBoundsException var8) {
            throw new IllegalArgumentException("not encoded in Base64");
         }

         dest.setByte(destOffset, (byte)(outBuff >> 16));
         dest.setByte(destOffset + 1, (byte)(outBuff >> 8));
         dest.setByte(destOffset + 2, (byte)outBuff);
         return 3;
      }
   }
}
