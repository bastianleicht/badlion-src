package com.ibm.icu.impl;

public final class UCharacterUtility {
   private static final int NON_CHARACTER_SUFFIX_MIN_3_0_ = 65534;
   private static final int NON_CHARACTER_MIN_3_1_ = 64976;
   private static final int NON_CHARACTER_MAX_3_1_ = 65007;

   public static boolean isNonCharacter(int ch) {
      return (ch & '\ufffe') == '\ufffe'?true:ch >= '\ufdd0' && ch <= '\ufdef';
   }

   static int toInt(char msc, char lsc) {
      return msc << 16 | lsc;
   }

   static int getNullTermByteSubString(StringBuffer str, byte[] array, int index) {
      for(byte b = 1; b != 0; ++index) {
         b = array[index];
         if(b != 0) {
            str.append((char)(b & 255));
         }
      }

      return index;
   }

   static int compareNullTermByteSubString(String str, byte[] array, int strindex, int aindex) {
      byte b = 1;
      int length = str.length();

      while(true) {
         if(b != 0) {
            b = array[aindex];
            ++aindex;
            if(b != 0) {
               if(strindex != length && str.charAt(strindex) == (char)(b & 255)) {
                  ++strindex;
                  continue;
               }

               return -1;
            }
         }

         return strindex;
      }
   }

   static int skipNullTermByteSubString(byte[] array, int index, int skipcount) {
      for(int i = 0; i < skipcount; ++i) {
         for(byte b = 1; b != 0; ++index) {
            b = array[index];
         }
      }

      return index;
   }

   static int skipByteSubString(byte[] array, int index, int length, byte skipend) {
      int result;
      for(result = 0; result < length; ++result) {
         byte b = array[index + result];
         if(b == skipend) {
            ++result;
            break;
         }
      }

      return result;
   }
}
