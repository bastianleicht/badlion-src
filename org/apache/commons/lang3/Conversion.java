package org.apache.commons.lang3;

import java.util.UUID;

public class Conversion {
   public static int hexDigitToInt(char hexDigit) {
      int digit = Character.digit(hexDigit, 16);
      if(digit < 0) {
         throw new IllegalArgumentException("Cannot interpret \'" + hexDigit + "\' as a hexadecimal digit");
      } else {
         return digit;
      }
   }

   public static int hexDigitMsb0ToInt(char hexDigit) {
      switch(hexDigit) {
      case '0':
         return 0;
      case '1':
         return 8;
      case '2':
         return 4;
      case '3':
         return 12;
      case '4':
         return 2;
      case '5':
         return 10;
      case '6':
         return 6;
      case '7':
         return 14;
      case '8':
         return 1;
      case '9':
         return 9;
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret \'" + hexDigit + "\' as a hexadecimal digit");
      case 'A':
      case 'a':
         return 5;
      case 'B':
      case 'b':
         return 13;
      case 'C':
      case 'c':
         return 3;
      case 'D':
      case 'd':
         return 11;
      case 'E':
      case 'e':
         return 7;
      case 'F':
      case 'f':
         return 15;
      }
   }

   public static boolean[] hexDigitToBinary(char hexDigit) {
      switch(hexDigit) {
      case '0':
         return new boolean[]{false, false, false, false};
      case '1':
         return new boolean[]{true, false, false, false};
      case '2':
         return new boolean[]{false, true, false, false};
      case '3':
         return new boolean[]{true, true, false, false};
      case '4':
         return new boolean[]{false, false, true, false};
      case '5':
         return new boolean[]{true, false, true, false};
      case '6':
         return new boolean[]{false, true, true, false};
      case '7':
         return new boolean[]{true, true, true, false};
      case '8':
         return new boolean[]{false, false, false, true};
      case '9':
         return new boolean[]{true, false, false, true};
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret \'" + hexDigit + "\' as a hexadecimal digit");
      case 'A':
      case 'a':
         return new boolean[]{false, true, false, true};
      case 'B':
      case 'b':
         return new boolean[]{true, true, false, true};
      case 'C':
      case 'c':
         return new boolean[]{false, false, true, true};
      case 'D':
      case 'd':
         return new boolean[]{true, false, true, true};
      case 'E':
      case 'e':
         return new boolean[]{false, true, true, true};
      case 'F':
      case 'f':
         return new boolean[]{true, true, true, true};
      }
   }

   public static boolean[] hexDigitMsb0ToBinary(char hexDigit) {
      switch(hexDigit) {
      case '0':
         return new boolean[]{false, false, false, false};
      case '1':
         return new boolean[]{false, false, false, true};
      case '2':
         return new boolean[]{false, false, true, false};
      case '3':
         return new boolean[]{false, false, true, true};
      case '4':
         return new boolean[]{false, true, false, false};
      case '5':
         return new boolean[]{false, true, false, true};
      case '6':
         return new boolean[]{false, true, true, false};
      case '7':
         return new boolean[]{false, true, true, true};
      case '8':
         return new boolean[]{true, false, false, false};
      case '9':
         return new boolean[]{true, false, false, true};
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'G':
      case 'H':
      case 'I':
      case 'J':
      case 'K':
      case 'L':
      case 'M':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'S':
      case 'T':
      case 'U':
      case 'V':
      case 'W':
      case 'X':
      case 'Y':
      case 'Z':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      default:
         throw new IllegalArgumentException("Cannot interpret \'" + hexDigit + "\' as a hexadecimal digit");
      case 'A':
      case 'a':
         return new boolean[]{true, false, true, false};
      case 'B':
      case 'b':
         return new boolean[]{true, false, true, true};
      case 'C':
      case 'c':
         return new boolean[]{true, true, false, false};
      case 'D':
      case 'd':
         return new boolean[]{true, true, false, true};
      case 'E':
      case 'e':
         return new boolean[]{true, true, true, false};
      case 'F':
      case 'f':
         return new boolean[]{true, true, true, true};
      }
   }

   public static char binaryToHexDigit(boolean[] src) {
      return binaryToHexDigit(src, 0);
   }

   public static char binaryToHexDigit(boolean[] src, int srcPos) {
      if(src.length == 0) {
         throw new IllegalArgumentException("Cannot convert an empty array.");
      } else {
         return (char)(src.length > srcPos + 3 && src[srcPos + 3]?(src.length > srcPos + 2 && src[srcPos + 2]?(src.length > srcPos + 1 && src[srcPos + 1]?(src[srcPos]?'f':'e'):(src[srcPos]?'d':'c')):(src.length > srcPos + 1 && src[srcPos + 1]?(src[srcPos]?'b':'a'):(src[srcPos]?'9':'8'))):(src.length > srcPos + 2 && src[srcPos + 2]?(src.length > srcPos + 1 && src[srcPos + 1]?(src[srcPos]?'7':'6'):(src[srcPos]?'5':'4')):(src.length > srcPos + 1 && src[srcPos + 1]?(src[srcPos]?'3':'2'):(src[srcPos]?'1':'0'))));
      }
   }

   public static char binaryToHexDigitMsb0_4bits(boolean[] src) {
      return binaryToHexDigitMsb0_4bits(src, 0);
   }

   public static char binaryToHexDigitMsb0_4bits(boolean[] src, int srcPos) {
      if(src.length > 8) {
         throw new IllegalArgumentException("src.length>8: src.length=" + src.length);
      } else if(src.length - srcPos < 4) {
         throw new IllegalArgumentException("src.length-srcPos<4: src.length=" + src.length + ", srcPos=" + srcPos);
      } else {
         return (char)(src[srcPos + 3]?(src[srcPos + 2]?(src[srcPos + 1]?(src[srcPos]?'f':'7'):(src[srcPos]?'b':'3')):(src[srcPos + 1]?(src[srcPos]?'d':'5'):(src[srcPos]?'9':'1'))):(src[srcPos + 2]?(src[srcPos + 1]?(src[srcPos]?'e':'6'):(src[srcPos]?'a':'2')):(src[srcPos + 1]?(src[srcPos]?'c':'4'):(src[srcPos]?'8':'0'))));
      }
   }

   public static char binaryBeMsb0ToHexDigit(boolean[] src) {
      return binaryBeMsb0ToHexDigit(src, 0);
   }

   public static char binaryBeMsb0ToHexDigit(boolean[] src, int srcPos) {
      if(src.length == 0) {
         throw new IllegalArgumentException("Cannot convert an empty array.");
      } else {
         int beSrcPos = src.length - 1 - srcPos;
         int srcLen = Math.min(4, beSrcPos + 1);
         boolean[] paddedSrc = new boolean[4];
         System.arraycopy(src, beSrcPos + 1 - srcLen, paddedSrc, 4 - srcLen, srcLen);
         srcPos = 0;
         return (char)(paddedSrc[srcPos]?(paddedSrc.length > srcPos + 1 && paddedSrc[srcPos + 1]?(paddedSrc.length > srcPos + 2 && paddedSrc[srcPos + 2]?(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'f':'e'):(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'d':'c')):(paddedSrc.length > srcPos + 2 && paddedSrc[srcPos + 2]?(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'b':'a'):(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'9':'8'))):(paddedSrc.length > srcPos + 1 && paddedSrc[srcPos + 1]?(paddedSrc.length > srcPos + 2 && paddedSrc[srcPos + 2]?(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'7':'6'):(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'5':'4')):(paddedSrc.length > srcPos + 2 && paddedSrc[srcPos + 2]?(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'3':'2'):(paddedSrc.length > srcPos + 3 && paddedSrc[srcPos + 3]?'1':'0'))));
      }
   }

   public static char intToHexDigit(int nibble) {
      char c = Character.forDigit(nibble, 16);
      if(c == 0) {
         throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
      } else {
         return c;
      }
   }

   public static char intToHexDigitMsb0(int nibble) {
      switch(nibble) {
      case 0:
         return '0';
      case 1:
         return '8';
      case 2:
         return '4';
      case 3:
         return 'c';
      case 4:
         return '2';
      case 5:
         return 'a';
      case 6:
         return '6';
      case 7:
         return 'e';
      case 8:
         return '1';
      case 9:
         return '9';
      case 10:
         return '5';
      case 11:
         return 'd';
      case 12:
         return '3';
      case 13:
         return 'b';
      case 14:
         return '7';
      case 15:
         return 'f';
      default:
         throw new IllegalArgumentException("nibble value not between 0 and 15: " + nibble);
      }
   }

   public static long intArrayToLong(int[] src, int srcPos, long dstInit, int dstPos, int nInts) {
      if((src.length != 0 || srcPos != 0) && 0 != nInts) {
         if((nInts - 1) * 32 + dstPos >= 64) {
            throw new IllegalArgumentException("(nInts-1)*32+dstPos is greather or equal to than 64");
         } else {
            long out = dstInit;
            int shift = 0;

            for(int i = 0; i < nInts; ++i) {
               shift = i * 32 + dstPos;
               long bits = (4294967295L & (long)src[i + srcPos]) << shift;
               long mask = 4294967295L << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static long shortArrayToLong(short[] src, int srcPos, long dstInit, int dstPos, int nShorts) {
      if((src.length != 0 || srcPos != 0) && 0 != nShorts) {
         if((nShorts - 1) * 16 + dstPos >= 64) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 64");
         } else {
            long out = dstInit;
            int shift = 0;

            for(int i = 0; i < nShorts; ++i) {
               shift = i * 16 + dstPos;
               long bits = (65535L & (long)src[i + srcPos]) << shift;
               long mask = 65535L << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static int shortArrayToInt(short[] src, int srcPos, int dstInit, int dstPos, int nShorts) {
      if((src.length != 0 || srcPos != 0) && 0 != nShorts) {
         if((nShorts - 1) * 16 + dstPos >= 32) {
            throw new IllegalArgumentException("(nShorts-1)*16+dstPos is greather or equal to than 32");
         } else {
            int out = dstInit;
            int shift = 0;

            for(int i = 0; i < nShorts; ++i) {
               shift = i * 16 + dstPos;
               int bits = ('\uffff' & src[i + srcPos]) << shift;
               int mask = '\uffff' << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static long byteArrayToLong(byte[] src, int srcPos, long dstInit, int dstPos, int nBytes) {
      if((src.length != 0 || srcPos != 0) && 0 != nBytes) {
         if((nBytes - 1) * 8 + dstPos >= 64) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 64");
         } else {
            long out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBytes; ++i) {
               shift = i * 8 + dstPos;
               long bits = (255L & (long)src[i + srcPos]) << shift;
               long mask = 255L << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static int byteArrayToInt(byte[] src, int srcPos, int dstInit, int dstPos, int nBytes) {
      if((src.length != 0 || srcPos != 0) && 0 != nBytes) {
         if((nBytes - 1) * 8 + dstPos >= 32) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 32");
         } else {
            int out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBytes; ++i) {
               shift = i * 8 + dstPos;
               int bits = (255 & src[i + srcPos]) << shift;
               int mask = 255 << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static short byteArrayToShort(byte[] src, int srcPos, short dstInit, int dstPos, int nBytes) {
      if((src.length != 0 || srcPos != 0) && 0 != nBytes) {
         if((nBytes - 1) * 8 + dstPos >= 16) {
            throw new IllegalArgumentException("(nBytes-1)*8+dstPos is greather or equal to than 16");
         } else {
            short out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBytes; ++i) {
               shift = i * 8 + dstPos;
               int bits = (255 & src[i + srcPos]) << shift;
               int mask = 255 << shift;
               out = (short)(out & ~mask | bits);
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static long hexToLong(String src, int srcPos, long dstInit, int dstPos, int nHex) {
      if(0 == nHex) {
         return dstInit;
      } else if((nHex - 1) * 4 + dstPos >= 64) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 64");
      } else {
         long out = dstInit;
         int shift = 0;

         for(int i = 0; i < nHex; ++i) {
            shift = i * 4 + dstPos;
            long bits = (15L & (long)hexDigitToInt(src.charAt(i + srcPos))) << shift;
            long mask = 15L << shift;
            out = out & ~mask | bits;
         }

         return out;
      }
   }

   public static int hexToInt(String src, int srcPos, int dstInit, int dstPos, int nHex) {
      if(0 == nHex) {
         return dstInit;
      } else if((nHex - 1) * 4 + dstPos >= 32) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 32");
      } else {
         int out = dstInit;
         int shift = 0;

         for(int i = 0; i < nHex; ++i) {
            shift = i * 4 + dstPos;
            int bits = (15 & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            int mask = 15 << shift;
            out = out & ~mask | bits;
         }

         return out;
      }
   }

   public static short hexToShort(String src, int srcPos, short dstInit, int dstPos, int nHex) {
      if(0 == nHex) {
         return dstInit;
      } else if((nHex - 1) * 4 + dstPos >= 16) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 16");
      } else {
         short out = dstInit;
         int shift = 0;

         for(int i = 0; i < nHex; ++i) {
            shift = i * 4 + dstPos;
            int bits = (15 & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            int mask = 15 << shift;
            out = (short)(out & ~mask | bits);
         }

         return out;
      }
   }

   public static byte hexToByte(String src, int srcPos, byte dstInit, int dstPos, int nHex) {
      if(0 == nHex) {
         return dstInit;
      } else if((nHex - 1) * 4 + dstPos >= 8) {
         throw new IllegalArgumentException("(nHexs-1)*4+dstPos is greather or equal to than 8");
      } else {
         byte out = dstInit;
         int shift = 0;

         for(int i = 0; i < nHex; ++i) {
            shift = i * 4 + dstPos;
            int bits = (15 & hexDigitToInt(src.charAt(i + srcPos))) << shift;
            int mask = 15 << shift;
            out = (byte)(out & ~mask | bits);
         }

         return out;
      }
   }

   public static long binaryToLong(boolean[] src, int srcPos, long dstInit, int dstPos, int nBools) {
      if((src.length != 0 || srcPos != 0) && 0 != nBools) {
         if(nBools - 1 + dstPos >= 64) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 64");
         } else {
            long out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBools; ++i) {
               shift = i * 1 + dstPos;
               long bits = (src[i + srcPos]?1L:0L) << shift;
               long mask = 1L << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static int binaryToInt(boolean[] src, int srcPos, int dstInit, int dstPos, int nBools) {
      if((src.length != 0 || srcPos != 0) && 0 != nBools) {
         if(nBools - 1 + dstPos >= 32) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 32");
         } else {
            int out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBools; ++i) {
               shift = i * 1 + dstPos;
               int bits = (src[i + srcPos]?1:0) << shift;
               int mask = 1 << shift;
               out = out & ~mask | bits;
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static short binaryToShort(boolean[] src, int srcPos, short dstInit, int dstPos, int nBools) {
      if((src.length != 0 || srcPos != 0) && 0 != nBools) {
         if(nBools - 1 + dstPos >= 16) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 16");
         } else {
            short out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBools; ++i) {
               shift = i * 1 + dstPos;
               int bits = (src[i + srcPos]?1:0) << shift;
               int mask = 1 << shift;
               out = (short)(out & ~mask | bits);
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static byte binaryToByte(boolean[] src, int srcPos, byte dstInit, int dstPos, int nBools) {
      if((src.length != 0 || srcPos != 0) && 0 != nBools) {
         if(nBools - 1 + dstPos >= 8) {
            throw new IllegalArgumentException("nBools-1+dstPos is greather or equal to than 8");
         } else {
            byte out = dstInit;
            int shift = 0;

            for(int i = 0; i < nBools; ++i) {
               shift = i * 1 + dstPos;
               int bits = (src[i + srcPos]?1:0) << shift;
               int mask = 1 << shift;
               out = (byte)(out & ~mask | bits);
            }

            return out;
         }
      } else {
         return dstInit;
      }
   }

   public static int[] longToIntArray(long src, int srcPos, int[] dst, int dstPos, int nInts) {
      if(0 == nInts) {
         return dst;
      } else if((nInts - 1) * 32 + srcPos >= 64) {
         throw new IllegalArgumentException("(nInts-1)*32+srcPos is greather or equal to than 64");
      } else {
         int shift = 0;

         for(int i = 0; i < nInts; ++i) {
            shift = i * 32 + srcPos;
            dst[dstPos + i] = (int)(-1L & src >> shift);
         }

         return dst;
      }
   }

   public static short[] longToShortArray(long src, int srcPos, short[] dst, int dstPos, int nShorts) {
      if(0 == nShorts) {
         return dst;
      } else if((nShorts - 1) * 16 + srcPos >= 64) {
         throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 64");
      } else {
         int shift = 0;

         for(int i = 0; i < nShorts; ++i) {
            shift = i * 16 + srcPos;
            dst[dstPos + i] = (short)((int)(65535L & src >> shift));
         }

         return dst;
      }
   }

   public static short[] intToShortArray(int src, int srcPos, short[] dst, int dstPos, int nShorts) {
      if(0 == nShorts) {
         return dst;
      } else if((nShorts - 1) * 16 + srcPos >= 32) {
         throw new IllegalArgumentException("(nShorts-1)*16+srcPos is greather or equal to than 32");
      } else {
         int shift = 0;

         for(int i = 0; i < nShorts; ++i) {
            shift = i * 16 + srcPos;
            dst[dstPos + i] = (short)('\uffff' & src >> shift);
         }

         return dst;
      }
   }

   public static byte[] longToByteArray(long src, int srcPos, byte[] dst, int dstPos, int nBytes) {
      if(0 == nBytes) {
         return dst;
      } else if((nBytes - 1) * 8 + srcPos >= 64) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 64");
      } else {
         int shift = 0;

         for(int i = 0; i < nBytes; ++i) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)((int)(255L & src >> shift));
         }

         return dst;
      }
   }

   public static byte[] intToByteArray(int src, int srcPos, byte[] dst, int dstPos, int nBytes) {
      if(0 == nBytes) {
         return dst;
      } else if((nBytes - 1) * 8 + srcPos >= 32) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 32");
      } else {
         int shift = 0;

         for(int i = 0; i < nBytes; ++i) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)(255 & src >> shift);
         }

         return dst;
      }
   }

   public static byte[] shortToByteArray(short src, int srcPos, byte[] dst, int dstPos, int nBytes) {
      if(0 == nBytes) {
         return dst;
      } else if((nBytes - 1) * 8 + srcPos >= 16) {
         throw new IllegalArgumentException("(nBytes-1)*8+srcPos is greather or equal to than 16");
      } else {
         int shift = 0;

         for(int i = 0; i < nBytes; ++i) {
            shift = i * 8 + srcPos;
            dst[dstPos + i] = (byte)(255 & src >> shift);
         }

         return dst;
      }
   }

   public static String longToHex(long src, int srcPos, String dstInit, int dstPos, int nHexs) {
      if(0 == nHexs) {
         return dstInit;
      } else if((nHexs - 1) * 4 + srcPos >= 64) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 64");
      } else {
         StringBuilder sb = new StringBuilder(dstInit);
         int shift = 0;
         int append = sb.length();

         for(int i = 0; i < nHexs; ++i) {
            shift = i * 4 + srcPos;
            int bits = (int)(15L & src >> shift);
            if(dstPos + i == append) {
               ++append;
               sb.append(intToHexDigit(bits));
            } else {
               sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
         }

         return sb.toString();
      }
   }

   public static String intToHex(int src, int srcPos, String dstInit, int dstPos, int nHexs) {
      if(0 == nHexs) {
         return dstInit;
      } else if((nHexs - 1) * 4 + srcPos >= 32) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 32");
      } else {
         StringBuilder sb = new StringBuilder(dstInit);
         int shift = 0;
         int append = sb.length();

         for(int i = 0; i < nHexs; ++i) {
            shift = i * 4 + srcPos;
            int bits = 15 & src >> shift;
            if(dstPos + i == append) {
               ++append;
               sb.append(intToHexDigit(bits));
            } else {
               sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
         }

         return sb.toString();
      }
   }

   public static String shortToHex(short src, int srcPos, String dstInit, int dstPos, int nHexs) {
      if(0 == nHexs) {
         return dstInit;
      } else if((nHexs - 1) * 4 + srcPos >= 16) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 16");
      } else {
         StringBuilder sb = new StringBuilder(dstInit);
         int shift = 0;
         int append = sb.length();

         for(int i = 0; i < nHexs; ++i) {
            shift = i * 4 + srcPos;
            int bits = 15 & src >> shift;
            if(dstPos + i == append) {
               ++append;
               sb.append(intToHexDigit(bits));
            } else {
               sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
         }

         return sb.toString();
      }
   }

   public static String byteToHex(byte src, int srcPos, String dstInit, int dstPos, int nHexs) {
      if(0 == nHexs) {
         return dstInit;
      } else if((nHexs - 1) * 4 + srcPos >= 8) {
         throw new IllegalArgumentException("(nHexs-1)*4+srcPos is greather or equal to than 8");
      } else {
         StringBuilder sb = new StringBuilder(dstInit);
         int shift = 0;
         int append = sb.length();

         for(int i = 0; i < nHexs; ++i) {
            shift = i * 4 + srcPos;
            int bits = 15 & src >> shift;
            if(dstPos + i == append) {
               ++append;
               sb.append(intToHexDigit(bits));
            } else {
               sb.setCharAt(dstPos + i, intToHexDigit(bits));
            }
         }

         return sb.toString();
      }
   }

   public static boolean[] longToBinary(long src, int srcPos, boolean[] dst, int dstPos, int nBools) {
      if(0 == nBools) {
         return dst;
      } else if(nBools - 1 + srcPos >= 64) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 64");
      } else {
         int shift = 0;

         for(int i = 0; i < nBools; ++i) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = (1L & src >> shift) != 0L;
         }

         return dst;
      }
   }

   public static boolean[] intToBinary(int src, int srcPos, boolean[] dst, int dstPos, int nBools) {
      if(0 == nBools) {
         return dst;
      } else if(nBools - 1 + srcPos >= 32) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 32");
      } else {
         int shift = 0;

         for(int i = 0; i < nBools; ++i) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = (1 & src >> shift) != 0;
         }

         return dst;
      }
   }

   public static boolean[] shortToBinary(short src, int srcPos, boolean[] dst, int dstPos, int nBools) {
      if(0 == nBools) {
         return dst;
      } else if(nBools - 1 + srcPos >= 16) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 16");
      } else {
         int shift = 0;

         assert (nBools - 1) * 1 < 16 - srcPos;

         for(int i = 0; i < nBools; ++i) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = (1 & src >> shift) != 0;
         }

         return dst;
      }
   }

   public static boolean[] byteToBinary(byte src, int srcPos, boolean[] dst, int dstPos, int nBools) {
      if(0 == nBools) {
         return dst;
      } else if(nBools - 1 + srcPos >= 8) {
         throw new IllegalArgumentException("nBools-1+srcPos is greather or equal to than 8");
      } else {
         int shift = 0;

         for(int i = 0; i < nBools; ++i) {
            shift = i * 1 + srcPos;
            dst[dstPos + i] = (1 & src >> shift) != 0;
         }

         return dst;
      }
   }

   public static byte[] uuidToByteArray(UUID src, byte[] dst, int dstPos, int nBytes) {
      if(0 == nBytes) {
         return dst;
      } else if(nBytes > 16) {
         throw new IllegalArgumentException("nBytes is greather than 16");
      } else {
         longToByteArray(src.getMostSignificantBits(), 0, dst, dstPos, nBytes > 8?8:nBytes);
         if(nBytes >= 8) {
            longToByteArray(src.getLeastSignificantBits(), 0, dst, dstPos + 8, nBytes - 8);
         }

         return dst;
      }
   }

   public static UUID byteArrayToUuid(byte[] src, int srcPos) {
      if(src.length - srcPos < 16) {
         throw new IllegalArgumentException("Need at least 16 bytes for UUID");
      } else {
         return new UUID(byteArrayToLong(src, srcPos, 0L, 0, 8), byteArrayToLong(src, srcPos + 8, 0L, 0, 8));
      }
   }
}
