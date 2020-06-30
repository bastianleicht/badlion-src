package com.ibm.icu.impl.locale;

public final class AsciiUtil {
   public static boolean caseIgnoreMatch(String s1, String s2) {
      if(s1 == s2) {
         return true;
      } else {
         int len = s1.length();
         if(len != s2.length()) {
            return false;
         } else {
            int i;
            for(i = 0; i < len; ++i) {
               char c1 = s1.charAt(i);
               char c2 = s2.charAt(i);
               if(c1 != c2 && toLower(c1) != toLower(c2)) {
                  break;
               }
            }

            return i == len;
         }
      }
   }

   public static int caseIgnoreCompare(String s1, String s2) {
      return s1 == s2?0:toLowerString(s1).compareTo(toLowerString(s2));
   }

   public static char toUpper(char c) {
      if(c >= 97 && c <= 122) {
         c = (char)(c - 32);
      }

      return c;
   }

   public static char toLower(char c) {
      if(c >= 65 && c <= 90) {
         c = (char)(c + 32);
      }

      return c;
   }

   public static String toLowerString(String s) {
      int idx;
      for(idx = 0; idx < s.length(); ++idx) {
         char c = s.charAt(idx);
         if(c >= 65 && c <= 90) {
            break;
         }
      }

      if(idx == s.length()) {
         return s;
      } else {
         StringBuilder buf;
         for(buf = new StringBuilder(s.substring(0, idx)); idx < s.length(); ++idx) {
            buf.append(toLower(s.charAt(idx)));
         }

         return buf.toString();
      }
   }

   public static String toUpperString(String s) {
      int idx;
      for(idx = 0; idx < s.length(); ++idx) {
         char c = s.charAt(idx);
         if(c >= 97 && c <= 122) {
            break;
         }
      }

      if(idx == s.length()) {
         return s;
      } else {
         StringBuilder buf;
         for(buf = new StringBuilder(s.substring(0, idx)); idx < s.length(); ++idx) {
            buf.append(toUpper(s.charAt(idx)));
         }

         return buf.toString();
      }
   }

   public static String toTitleString(String s) {
      if(s.length() == 0) {
         return s;
      } else {
         int idx = 0;
         char c = s.charAt(idx);
         if(c < 97 || c > 122) {
            for(idx = 1; idx < s.length() && (c < 65 || c > 90); ++idx) {
               ;
            }
         }

         if(idx == s.length()) {
            return s;
         } else {
            StringBuilder buf = new StringBuilder(s.substring(0, idx));
            if(idx == 0) {
               buf.append(toUpper(s.charAt(idx)));
               ++idx;
            }

            while(idx < s.length()) {
               buf.append(toLower(s.charAt(idx)));
               ++idx;
            }

            return buf.toString();
         }
      }
   }

   public static boolean isAlpha(char c) {
      return c >= 65 && c <= 90 || c >= 97 && c <= 122;
   }

   public static boolean isAlphaString(String s) {
      boolean b = true;

      for(int i = 0; i < s.length(); ++i) {
         if(!isAlpha(s.charAt(i))) {
            b = false;
            break;
         }
      }

      return b;
   }

   public static boolean isNumeric(char c) {
      return c >= 48 && c <= 57;
   }

   public static boolean isNumericString(String s) {
      boolean b = true;

      for(int i = 0; i < s.length(); ++i) {
         if(!isNumeric(s.charAt(i))) {
            b = false;
            break;
         }
      }

      return b;
   }

   public static boolean isAlphaNumeric(char c) {
      return isAlpha(c) || isNumeric(c);
   }

   public static boolean isAlphaNumericString(String s) {
      boolean b = true;

      for(int i = 0; i < s.length(); ++i) {
         if(!isAlphaNumeric(s.charAt(i))) {
            b = false;
            break;
         }
      }

      return b;
   }

   public static class CaseInsensitiveKey {
      private String _key;
      private int _hash;

      public CaseInsensitiveKey(String key) {
         this._key = key;
         this._hash = AsciiUtil.toLowerString(key).hashCode();
      }

      public boolean equals(Object o) {
         return this == o?true:(o instanceof AsciiUtil.CaseInsensitiveKey?AsciiUtil.caseIgnoreMatch(this._key, ((AsciiUtil.CaseInsensitiveKey)o)._key):false);
      }

      public int hashCode() {
         return this._hash;
      }
   }
}
