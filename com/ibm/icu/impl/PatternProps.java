package com.ibm.icu.impl;

public final class PatternProps {
   private static final byte[] latin1 = new byte[]{(byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)5, (byte)5, (byte)5, (byte)5, (byte)5, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)5, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)3, (byte)3, (byte)3, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)3, (byte)3, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)5, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)3, (byte)0, (byte)3, (byte)0, (byte)3, (byte)3, (byte)0, (byte)3, (byte)0, (byte)3, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)3, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0};
   private static final byte[] index2000 = new byte[]{(byte)2, (byte)3, (byte)4, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)5, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)6, (byte)7, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)1, (byte)1, (byte)1, (byte)1, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)8, (byte)9};
   private static final int[] syntax2000 = new int[]{0, -1, -65536, 2147418367, 2146435070, -65536, 4194303, -1048576, -242, 65537};
   private static final int[] syntaxOrWhiteSpace2000 = new int[]{0, -1, -16384, 2147419135, 2146435070, -65536, 4194303, -1048576, -242, 65537};

   public static boolean isSyntax(int c) {
      if(c < 0) {
         return false;
      } else if(c <= 255) {
         return latin1[c] == 3;
      } else if(c < 8208) {
         return false;
      } else if(c <= 12336) {
         int bits = syntax2000[index2000[c - 8192 >> 5]];
         return (bits >> (c & 31) & 1) != 0;
      } else {
         return '﴾' <= c && c <= '﹆'?c <= '﴿' || '﹅' <= c:false;
      }
   }

   public static boolean isSyntaxOrWhiteSpace(int c) {
      if(c < 0) {
         return false;
      } else if(c <= 255) {
         return latin1[c] != 0;
      } else if(c < 8206) {
         return false;
      } else if(c <= 12336) {
         int bits = syntaxOrWhiteSpace2000[index2000[c - 8192 >> 5]];
         return (bits >> (c & 31) & 1) != 0;
      } else {
         return '﴾' <= c && c <= '﹆'?c <= '﴿' || '﹅' <= c:false;
      }
   }

   public static boolean isWhiteSpace(int c) {
      return c < 0?false:(c <= 255?latin1[c] == 5:(8206 <= c && c <= 8233?c <= 8207 || 8232 <= c:false));
   }

   public static int skipWhiteSpace(CharSequence s, int i) {
      while(i < s.length() && isWhiteSpace(s.charAt(i))) {
         ++i;
      }

      return i;
   }

   public static String trimWhiteSpace(String s) {
      if(s.length() != 0 && (isWhiteSpace(s.charAt(0)) || isWhiteSpace(s.charAt(s.length() - 1)))) {
         int start = 0;

         int limit;
         for(limit = s.length(); start < limit && isWhiteSpace(s.charAt(start)); ++start) {
            ;
         }

         if(start < limit) {
            while(isWhiteSpace(s.charAt(limit - 1))) {
               --limit;
            }
         }

         return s.substring(start, limit);
      } else {
         return s;
      }
   }

   public static boolean isIdentifier(CharSequence s) {
      int limit = s.length();
      if(limit == 0) {
         return false;
      } else {
         int start = 0;

         while(!isSyntaxOrWhiteSpace(s.charAt(start++))) {
            if(start >= limit) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean isIdentifier(CharSequence s, int start, int limit) {
      if(start >= limit) {
         return false;
      } else {
         while(!isSyntaxOrWhiteSpace(s.charAt(start++))) {
            if(start >= limit) {
               return true;
            }
         }

         return false;
      }
   }

   public static int skipIdentifier(CharSequence s, int i) {
      while(i < s.length() && !isSyntaxOrWhiteSpace(s.charAt(i))) {
         ++i;
      }

      return i;
   }
}
