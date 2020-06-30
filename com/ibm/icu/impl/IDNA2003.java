package com.ibm.icu.impl;

import com.ibm.icu.impl.Punycode;
import com.ibm.icu.text.StringPrep;
import com.ibm.icu.text.StringPrepParseException;
import com.ibm.icu.text.UCharacterIterator;

public final class IDNA2003 {
   private static char[] ACE_PREFIX = new char[]{'x', 'n', '-', '-'};
   private static final int MAX_LABEL_LENGTH = 63;
   private static final int HYPHEN = 45;
   private static final int CAPITAL_A = 65;
   private static final int CAPITAL_Z = 90;
   private static final int LOWER_CASE_DELTA = 32;
   private static final int FULL_STOP = 46;
   private static final int MAX_DOMAIN_NAME_LENGTH = 255;
   private static final StringPrep namePrep = StringPrep.getInstance(0);

   private static boolean startsWithPrefix(StringBuffer src) {
      boolean startsWithPrefix = true;
      if(src.length() < ACE_PREFIX.length) {
         return false;
      } else {
         for(int i = 0; i < ACE_PREFIX.length; ++i) {
            if(toASCIILower(src.charAt(i)) != ACE_PREFIX[i]) {
               startsWithPrefix = false;
            }
         }

         return startsWithPrefix;
      }
   }

   private static char toASCIILower(char ch) {
      return 65 <= ch && ch <= 90?(char)(ch + 32):ch;
   }

   private static StringBuffer toASCIILower(CharSequence src) {
      StringBuffer dest = new StringBuffer();

      for(int i = 0; i < src.length(); ++i) {
         dest.append(toASCIILower(src.charAt(i)));
      }

      return dest;
   }

   private static int compareCaseInsensitiveASCII(StringBuffer s1, StringBuffer s2) {
      for(int i = 0; i != s1.length(); ++i) {
         char c1 = s1.charAt(i);
         char c2 = s2.charAt(i);
         if(c1 != c2) {
            int rc = toASCIILower(c1) - toASCIILower(c2);
            if(rc != 0) {
               return rc;
            }
         }
      }

      return 0;
   }

   private static int getSeparatorIndex(char[] src, int start, int limit) {
      while(start < limit) {
         if(isLabelSeparator(src[start])) {
            return start;
         }

         ++start;
      }

      return start;
   }

   private static boolean isLDHChar(int ch) {
      return ch > 122?false:ch == 45 || 48 <= ch && ch <= 57 || 65 <= ch && ch <= 90 || 97 <= ch && ch <= 122;
   }

   private static boolean isLabelSeparator(int ch) {
      switch(ch) {
      case 46:
      case 12290:
      case 65294:
      case 65377:
         return true;
      default:
         return false;
      }
   }

   public static StringBuffer convertToASCII(UCharacterIterator src, int options) throws StringPrepParseException {
      boolean[] caseFlags = null;
      boolean srcIsASCII = true;
      boolean srcIsLDH = true;
      boolean useSTD3ASCIIRules = (options & 2) != 0;

      int ch;
      while((ch = src.next()) != -1) {
         if(ch > 127) {
            srcIsASCII = false;
         }
      }

      int failPos = -1;
      src.setToStart();
      StringBuffer processOut = null;
      if(!srcIsASCII) {
         processOut = namePrep.prepare(src, options);
      } else {
         processOut = new StringBuffer(src.getText());
      }

      int poLen = processOut.length();
      if(poLen == 0) {
         throw new StringPrepParseException("Found zero length lable after NamePrep.", 10);
      } else {
         StringBuffer dest = new StringBuffer();
         srcIsASCII = true;

         for(int j = 0; j < poLen; ++j) {
            ch = processOut.charAt(j);
            if(ch > 127) {
               srcIsASCII = false;
            } else if(!isLDHChar(ch)) {
               srcIsLDH = false;
               failPos = j;
            }
         }

         if(!useSTD3ASCIIRules || srcIsLDH && processOut.charAt(0) != 45 && processOut.charAt(processOut.length() - 1) != 45) {
            if(srcIsASCII) {
               dest = processOut;
            } else {
               if(startsWithPrefix(processOut)) {
                  throw new StringPrepParseException("The input does not start with the ACE Prefix.", 6, processOut.toString(), 0);
               }

               caseFlags = new boolean[poLen];
               StringBuilder punyout = Punycode.encode(processOut, caseFlags);
               StringBuffer lowerOut = toASCIILower(punyout);
               dest.append(ACE_PREFIX, 0, ACE_PREFIX.length);
               dest.append(lowerOut);
            }

            if(dest.length() > 63) {
               throw new StringPrepParseException("The labels in the input are too long. Length > 63.", 8, dest.toString(), 0);
            } else {
               return dest;
            }
         } else if(!srcIsLDH) {
            throw new StringPrepParseException("The input does not conform to the STD 3 ASCII rules", 5, processOut.toString(), failPos > 0?failPos - 1:failPos);
         } else if(processOut.charAt(0) == 45) {
            throw new StringPrepParseException("The input does not conform to the STD 3 ASCII rules", 5, processOut.toString(), 0);
         } else {
            throw new StringPrepParseException("The input does not conform to the STD 3 ASCII rules", 5, processOut.toString(), poLen > 0?poLen - 1:poLen);
         }
      }
   }

   public static StringBuffer convertIDNToASCII(String src, int options) throws StringPrepParseException {
      char[] srcArr = src.toCharArray();
      StringBuffer result = new StringBuffer();
      int sepIndex = 0;
      int oldSepIndex = 0;

      while(true) {
         sepIndex = getSeparatorIndex(srcArr, sepIndex, srcArr.length);
         String label = new String(srcArr, oldSepIndex, sepIndex - oldSepIndex);
         if(label.length() != 0 || sepIndex != srcArr.length) {
            UCharacterIterator iter = UCharacterIterator.getInstance(label);
            result.append(convertToASCII(iter, options));
         }

         if(sepIndex == srcArr.length) {
            if(result.length() > 255) {
               throw new StringPrepParseException("The output exceed the max allowed length.", 11);
            }

            return result;
         }

         ++sepIndex;
         oldSepIndex = sepIndex;
         result.append('.');
      }
   }

   public static StringBuffer convertToUnicode(UCharacterIterator src, int options) throws StringPrepParseException {
      boolean[] caseFlags = null;
      boolean srcIsASCII = true;
      int saveIndex = src.getIndex();

      int ch;
      while((ch = src.next()) != -1) {
         if(ch > 127) {
            srcIsASCII = false;
         }
      }

      StringBuffer processOut;
      if(!srcIsASCII) {
         try {
            src.setIndex(saveIndex);
            processOut = namePrep.prepare(src, options);
         } catch (StringPrepParseException var11) {
            return new StringBuffer(src.getText());
         }
      } else {
         processOut = new StringBuffer(src.getText());
      }

      if(startsWithPrefix(processOut)) {
         StringBuffer decodeOut = null;
         String temp = processOut.substring(ACE_PREFIX.length, processOut.length());

         try {
            decodeOut = new StringBuffer(Punycode.decode(temp, caseFlags));
         } catch (StringPrepParseException var10) {
            decodeOut = null;
         }

         if(decodeOut != null) {
            StringBuffer toASCIIOut = convertToASCII(UCharacterIterator.getInstance(decodeOut), options);
            if(compareCaseInsensitiveASCII(processOut, toASCIIOut) != 0) {
               decodeOut = null;
            }
         }

         if(decodeOut != null) {
            return decodeOut;
         }
      }

      return new StringBuffer(src.getText());
   }

   public static StringBuffer convertIDNToUnicode(String src, int options) throws StringPrepParseException {
      char[] srcArr = src.toCharArray();
      StringBuffer result = new StringBuffer();
      int sepIndex = 0;
      int oldSepIndex = 0;

      while(true) {
         sepIndex = getSeparatorIndex(srcArr, sepIndex, srcArr.length);
         String label = new String(srcArr, oldSepIndex, sepIndex - oldSepIndex);
         if(label.length() == 0 && sepIndex != srcArr.length) {
            throw new StringPrepParseException("Found zero length lable after NamePrep.", 10);
         }

         UCharacterIterator iter = UCharacterIterator.getInstance(label);
         result.append(convertToUnicode(iter, options));
         if(sepIndex == srcArr.length) {
            if(result.length() > 255) {
               throw new StringPrepParseException("The output exceed the max allowed length.", 11);
            }

            return result;
         }

         result.append(srcArr[sepIndex]);
         ++sepIndex;
         oldSepIndex = sepIndex;
      }
   }

   public static int compare(String s1, String s2, int options) throws StringPrepParseException {
      StringBuffer s1Out = convertIDNToASCII(s1, options);
      StringBuffer s2Out = convertIDNToASCII(s2, options);
      return compareCaseInsensitiveASCII(s1Out, s2Out);
   }
}
