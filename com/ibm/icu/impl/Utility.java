package com.ibm.icu.impl;

import com.ibm.icu.impl.IllegalIcuArgumentException;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.UCharacterProperty;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeMatcher;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Utility {
   private static final char APOSTROPHE = '\'';
   private static final char BACKSLASH = '\\';
   private static final int MAGIC_UNSIGNED = Integer.MIN_VALUE;
   private static final char ESCAPE = 'ꖥ';
   static final byte ESCAPE_BYTE = -91;
   public static String LINE_SEPARATOR = System.getProperty("line.separator");
   static final char[] HEX_DIGIT = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
   private static final char[] UNESCAPE_MAP = new char[]{'a', '\u0007', 'b', '\b', 'e', '\u001b', 'f', '\f', 'n', '\n', 'r', '\r', 't', '\t', 'v', '\u000b'};
   static final char[] DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

   public static final boolean arrayEquals(Object[] source, Object target) {
      if(source == null) {
         return target == null;
      } else if(!(target instanceof Object[])) {
         return false;
      } else {
         Object[] targ = (Object[])((Object[])target);
         return source.length == targ.length && arrayRegionMatches((Object[])source, 0, (Object[])targ, 0, source.length);
      }
   }

   public static final boolean arrayEquals(int[] source, Object target) {
      if(source == null) {
         return target == null;
      } else if(!(target instanceof int[])) {
         return false;
      } else {
         int[] targ = (int[])((int[])target);
         return source.length == targ.length && arrayRegionMatches((int[])source, 0, (int[])targ, 0, source.length);
      }
   }

   public static final boolean arrayEquals(double[] source, Object target) {
      if(source == null) {
         return target == null;
      } else if(!(target instanceof double[])) {
         return false;
      } else {
         double[] targ = (double[])((double[])target);
         return source.length == targ.length && arrayRegionMatches((double[])source, 0, (double[])targ, 0, source.length);
      }
   }

   public static final boolean arrayEquals(byte[] source, Object target) {
      if(source == null) {
         return target == null;
      } else if(!(target instanceof byte[])) {
         return false;
      } else {
         byte[] targ = (byte[])((byte[])target);
         return source.length == targ.length && arrayRegionMatches((byte[])source, 0, (byte[])targ, 0, source.length);
      }
   }

   public static final boolean arrayEquals(Object source, Object target) {
      return source == null?target == null:(source instanceof Object[]?arrayEquals((Object[])((Object[])source), target):(source instanceof int[]?arrayEquals((int[])((int[])source), target):(source instanceof double[]?arrayEquals((double[])((double[])source), target):(source instanceof byte[]?arrayEquals((byte[])((byte[])source), target):source.equals(target)))));
   }

   public static final boolean arrayRegionMatches(Object[] source, int sourceStart, Object[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(!arrayEquals(source[i], target[i + delta])) {
            return false;
         }
      }

      return true;
   }

   public static final boolean arrayRegionMatches(char[] source, int sourceStart, char[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(source[i] != target[i + delta]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean arrayRegionMatches(int[] source, int sourceStart, int[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(source[i] != target[i + delta]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean arrayRegionMatches(double[] source, int sourceStart, double[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(source[i] != target[i + delta]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean arrayRegionMatches(byte[] source, int sourceStart, byte[] target, int targetStart, int len) {
      int sourceEnd = sourceStart + len;
      int delta = targetStart - sourceStart;

      for(int i = sourceStart; i < sourceEnd; ++i) {
         if(source[i] != target[i + delta]) {
            return false;
         }
      }

      return true;
   }

   public static final boolean objectEquals(Object a, Object b) {
      return a == null?b == null:(b == null?false:a.equals(b));
   }

   public static int checkCompare(Comparable a, Comparable b) {
      return a == null?(b == null?0:-1):(b == null?1:a.compareTo(b));
   }

   public static int checkHash(Object a) {
      return a == null?0:a.hashCode();
   }

   public static final String arrayToRLEString(int[] a) {
      StringBuilder buffer = new StringBuilder();
      appendInt(buffer, a.length);
      int runValue = a[0];
      int runLength = 1;

      for(int i = 1; i < a.length; ++i) {
         int s = a[i];
         if(s == runValue && runLength < '\uffff') {
            ++runLength;
         } else {
            encodeRun(buffer, (int)runValue, runLength);
            runValue = s;
            runLength = 1;
         }
      }

      encodeRun(buffer, (int)runValue, runLength);
      return buffer.toString();
   }

   public static final String arrayToRLEString(short[] a) {
      StringBuilder buffer = new StringBuilder();
      buffer.append((char)(a.length >> 16));
      buffer.append((char)a.length);
      short runValue = a[0];
      int runLength = 1;

      for(int i = 1; i < a.length; ++i) {
         short s = a[i];
         if(s == runValue && runLength < '\uffff') {
            ++runLength;
         } else {
            encodeRun(buffer, (short)runValue, runLength);
            runValue = s;
            runLength = 1;
         }
      }

      encodeRun(buffer, (short)runValue, runLength);
      return buffer.toString();
   }

   public static final String arrayToRLEString(char[] a) {
      StringBuilder buffer = new StringBuilder();
      buffer.append((char)(a.length >> 16));
      buffer.append((char)a.length);
      char runValue = a[0];
      int runLength = 1;

      for(int i = 1; i < a.length; ++i) {
         char s = a[i];
         if(s == runValue && runLength < '\uffff') {
            ++runLength;
         } else {
            encodeRun(buffer, (short)((short)runValue), runLength);
            runValue = s;
            runLength = 1;
         }
      }

      encodeRun(buffer, (short)((short)runValue), runLength);
      return buffer.toString();
   }

   public static final String arrayToRLEString(byte[] a) {
      StringBuilder buffer = new StringBuilder();
      buffer.append((char)(a.length >> 16));
      buffer.append((char)a.length);
      byte runValue = a[0];
      int runLength = 1;
      byte[] state = new byte[2];

      for(int i = 1; i < a.length; ++i) {
         byte b = a[i];
         if(b == runValue && runLength < 255) {
            ++runLength;
         } else {
            encodeRun(buffer, runValue, runLength, state);
            runValue = b;
            runLength = 1;
         }
      }

      encodeRun(buffer, runValue, runLength, state);
      if(state[0] != 0) {
         appendEncodedByte(buffer, (byte)0, state);
      }

      return buffer.toString();
   }

   private static final void encodeRun(Appendable buffer, int value, int length) {
      if(length < 4) {
         for(int j = 0; j < length; ++j) {
            if(value == 'ꖥ') {
               appendInt(buffer, value);
            }

            appendInt(buffer, value);
         }
      } else {
         if(length == 'ꖥ') {
            if(value == 'ꖥ') {
               appendInt(buffer, 'ꖥ');
            }

            appendInt(buffer, value);
            --length;
         }

         appendInt(buffer, 'ꖥ');
         appendInt(buffer, length);
         appendInt(buffer, value);
      }

   }

   private static final void appendInt(Appendable buffer, int value) {
      try {
         buffer.append((char)(value >>> 16));
         buffer.append((char)(value & '\uffff'));
      } catch (IOException var3) {
         throw new IllegalIcuArgumentException(var3);
      }
   }

   private static final void encodeRun(Appendable buffer, short value, int length) {
      try {
         if(length < 4) {
            for(int j = 0; j < length; ++j) {
               if(value == 'ꖥ') {
                  buffer.append('ꖥ');
               }

               buffer.append((char)value);
            }
         } else {
            if(length == 'ꖥ') {
               if(value == 'ꖥ') {
                  buffer.append('ꖥ');
               }

               buffer.append((char)value);
               --length;
            }

            buffer.append('ꖥ');
            buffer.append((char)length);
            buffer.append((char)value);
         }

      } catch (IOException var4) {
         throw new IllegalIcuArgumentException(var4);
      }
   }

   private static final void encodeRun(Appendable buffer, byte value, int length, byte[] state) {
      if(length < 4) {
         for(int j = 0; j < length; ++j) {
            if(value == -91) {
               appendEncodedByte(buffer, (byte)-91, state);
            }

            appendEncodedByte(buffer, value, state);
         }
      } else {
         if(length == -91) {
            if(value == -91) {
               appendEncodedByte(buffer, (byte)-91, state);
            }

            appendEncodedByte(buffer, value, state);
            --length;
         }

         appendEncodedByte(buffer, (byte)-91, state);
         appendEncodedByte(buffer, (byte)length, state);
         appendEncodedByte(buffer, value, state);
      }

   }

   private static final void appendEncodedByte(Appendable buffer, byte value, byte[] state) {
      try {
         if(state[0] != 0) {
            char c = (char)(state[1] << 8 | value & 255);
            buffer.append(c);
            state[0] = 0;
         } else {
            state[0] = 1;
            state[1] = value;
         }

      } catch (IOException var4) {
         throw new IllegalIcuArgumentException(var4);
      }
   }

   public static final int[] RLEStringToIntArray(String s) {
      int length = getInt(s, 0);
      int[] array = new int[length];
      int ai = 0;
      int i = 1;
      int maxI = s.length() / 2;

      while(ai < length && i < maxI) {
         int c = getInt(s, i++);
         if(c == 'ꖥ') {
            c = getInt(s, i++);
            if(c == 'ꖥ') {
               array[ai++] = c;
            } else {
               int runLength = c;
               int runValue = getInt(s, i++);

               for(int j = 0; j < runLength; ++j) {
                  array[ai++] = runValue;
               }
            }
         } else {
            array[ai++] = c;
         }
      }

      if(ai == length && i == maxI) {
         return array;
      } else {
         throw new IllegalStateException("Bad run-length encoded int array");
      }
   }

   static final int getInt(String s, int i) {
      return s.charAt(2 * i) << 16 | s.charAt(2 * i + 1);
   }

   public static final short[] RLEStringToShortArray(String s) {
      int length = s.charAt(0) << 16 | s.charAt(1);
      short[] array = new short[length];
      int ai = 0;

      for(int i = 2; i < s.length(); ++i) {
         char c = s.charAt(i);
         if(c == 'ꖥ') {
            ++i;
            c = s.charAt(i);
            if(c == 'ꖥ') {
               array[ai++] = (short)c;
            } else {
               int runLength = c;
               ++i;
               short runValue = (short)s.charAt(i);

               for(int j = 0; j < runLength; ++j) {
                  array[ai++] = runValue;
               }
            }
         } else {
            array[ai++] = (short)c;
         }
      }

      if(ai != length) {
         throw new IllegalStateException("Bad run-length encoded short array");
      } else {
         return array;
      }
   }

   public static final char[] RLEStringToCharArray(String s) {
      int length = s.charAt(0) << 16 | s.charAt(1);
      char[] array = new char[length];
      int ai = 0;

      for(int i = 2; i < s.length(); ++i) {
         char c = s.charAt(i);
         if(c == 'ꖥ') {
            ++i;
            c = s.charAt(i);
            if(c == 'ꖥ') {
               array[ai++] = c;
            } else {
               int runLength = c;
               ++i;
               char runValue = s.charAt(i);

               for(int j = 0; j < runLength; ++j) {
                  array[ai++] = runValue;
               }
            }
         } else {
            array[ai++] = c;
         }
      }

      if(ai != length) {
         throw new IllegalStateException("Bad run-length encoded short array");
      } else {
         return array;
      }
   }

   public static final byte[] RLEStringToByteArray(String s) {
      int length = s.charAt(0) << 16 | s.charAt(1);
      byte[] array = new byte[length];
      boolean nextChar = true;
      char c = 0;
      int node = 0;
      int runLength = 0;
      int i = 2;
      int ai = 0;

      while(ai < length) {
         byte b;
         if(nextChar) {
            c = s.charAt(i++);
            b = (byte)(c >> 8);
            nextChar = false;
         } else {
            b = (byte)(c & 255);
            nextChar = true;
         }

         switch(node) {
         case 0:
            if(b == -91) {
               node = 1;
            } else {
               array[ai++] = b;
            }
            break;
         case 1:
            if(b == -91) {
               array[ai++] = -91;
               node = 0;
            } else {
               runLength = b;
               if(b < 0) {
                  runLength = b + 256;
               }

               node = 2;
            }
            break;
         case 2:
            for(int j = 0; j < runLength; ++j) {
               array[ai++] = b;
            }

            node = 0;
         }
      }

      if(node != 0) {
         throw new IllegalStateException("Bad run-length encoded byte array");
      } else if(i != s.length()) {
         throw new IllegalStateException("Excess data in RLE byte array string");
      } else {
         return array;
      }
   }

   public static final String formatForSource(String s) {
      StringBuilder buffer = new StringBuilder();

      for(int i = 0; i < s.length(); buffer.append('\"')) {
         if(i > 0) {
            buffer.append('+').append(LINE_SEPARATOR);
         }

         buffer.append("        \"");
         int count = 11;

         while(i < s.length() && count < 80) {
            char c = s.charAt(i++);
            if(c >= 32 && c != 34 && c != 92) {
               if(c <= 126) {
                  buffer.append(c);
                  ++count;
               } else {
                  buffer.append("\\u");
                  buffer.append(HEX_DIGIT[(c & '\uf000') >> 12]);
                  buffer.append(HEX_DIGIT[(c & 3840) >> 8]);
                  buffer.append(HEX_DIGIT[(c & 240) >> 4]);
                  buffer.append(HEX_DIGIT[c & 15]);
                  count += 6;
               }
            } else if(c == 10) {
               buffer.append("\\n");
               count += 2;
            } else if(c == 9) {
               buffer.append("\\t");
               count += 2;
            } else if(c == 13) {
               buffer.append("\\r");
               count += 2;
            } else {
               buffer.append('\\');
               buffer.append(HEX_DIGIT[(c & 448) >> 6]);
               buffer.append(HEX_DIGIT[(c & 56) >> 3]);
               buffer.append(HEX_DIGIT[c & 7]);
               count += 4;
            }
         }
      }

      return buffer.toString();
   }

   public static final String format1ForSource(String s) {
      StringBuilder buffer = new StringBuilder();
      buffer.append("\"");
      int i = 0;

      while(i < s.length()) {
         char c = s.charAt(i++);
         if(c >= 32 && c != 34 && c != 92) {
            if(c <= 126) {
               buffer.append(c);
            } else {
               buffer.append("\\u");
               buffer.append(HEX_DIGIT[(c & '\uf000') >> 12]);
               buffer.append(HEX_DIGIT[(c & 3840) >> 8]);
               buffer.append(HEX_DIGIT[(c & 240) >> 4]);
               buffer.append(HEX_DIGIT[c & 15]);
            }
         } else if(c == 10) {
            buffer.append("\\n");
         } else if(c == 9) {
            buffer.append("\\t");
         } else if(c == 13) {
            buffer.append("\\r");
         } else {
            buffer.append('\\');
            buffer.append(HEX_DIGIT[(c & 448) >> 6]);
            buffer.append(HEX_DIGIT[(c & 56) >> 3]);
            buffer.append(HEX_DIGIT[c & 7]);
         }
      }

      buffer.append('\"');
      return buffer.toString();
   }

   public static final String escape(String s) {
      StringBuilder buf = new StringBuilder();
      int i = 0;

      while(i < s.length()) {
         int c = Character.codePointAt(s, i);
         i += UTF16.getCharCount(c);
         if(c >= 32 && c <= 127) {
            if(c == 92) {
               buf.append("\\\\");
            } else {
               buf.append((char)c);
            }
         } else {
            boolean four = c <= '\uffff';
            buf.append(four?"\\u":"\\U");
            buf.append(hex((long)c, four?4:8));
         }
      }

      return buf.toString();
   }

   public static int unescapeAt(String s, int[] offset16) {
      int result = 0;
      int n = 0;
      int minDig = 0;
      int maxDig = 0;
      int bitsPerDigit = 4;
      boolean braces = false;
      int offset = offset16[0];
      int length = s.length();
      if(offset >= 0 && offset < length) {
         int c = Character.codePointAt(s, offset);
         offset = offset + UTF16.getCharCount(c);
         switch(c) {
         case 85:
            maxDig = 8;
            minDig = 8;
            break;
         case 117:
            maxDig = 4;
            minDig = 4;
            break;
         case 120:
            minDig = 1;
            if(offset < length && UTF16.charAt(s, offset) == 123) {
               ++offset;
               braces = true;
               maxDig = 8;
            } else {
               maxDig = 2;
            }
            break;
         default:
            int dig = UCharacter.digit(c, 8);
            if(dig >= 0) {
               minDig = 1;
               maxDig = 3;
               n = 1;
               bitsPerDigit = 3;
               result = dig;
            }
         }

         if(minDig != 0) {
            while(offset < length && n < maxDig) {
               c = UTF16.charAt(s, offset);
               int dig = UCharacter.digit(c, bitsPerDigit == 3?8:16);
               if(dig < 0) {
                  break;
               }

               result = result << bitsPerDigit | dig;
               offset += UTF16.getCharCount(c);
               ++n;
            }

            if(n < minDig) {
               return -1;
            } else {
               if(braces) {
                  if(c != 125) {
                     return -1;
                  }

                  ++offset;
               }

               if(result >= 0 && result < 1114112) {
                  if(offset < length && UTF16.isLeadSurrogate((char)result)) {
                     int ahead = offset + 1;
                     c = s.charAt(offset);
                     if(c == 92 && ahead < length) {
                        int[] o = new int[]{ahead};
                        c = unescapeAt(s, o);
                        ahead = o[0];
                     }

                     if(UTF16.isTrailSurrogate((char)c)) {
                        offset = ahead;
                        result = UCharacterProperty.getRawSupplementary((char)result, (char)c);
                     }
                  }

                  offset16[0] = offset;
                  return result;
               } else {
                  return -1;
               }
            }
         } else {
            for(int i = 0; i < UNESCAPE_MAP.length; i += 2) {
               if(c == UNESCAPE_MAP[i]) {
                  offset16[0] = offset;
                  return UNESCAPE_MAP[i + 1];
               }

               if(c < UNESCAPE_MAP[i]) {
                  break;
               }
            }

            if(c == 99 && offset < length) {
               c = UTF16.charAt(s, offset);
               offset16[0] = offset + UTF16.getCharCount(c);
               return 31 & c;
            } else {
               offset16[0] = offset;
               return c;
            }
         }
      } else {
         return -1;
      }
   }

   public static String unescape(String s) {
      StringBuilder buf = new StringBuilder();
      int[] pos = new int[1];
      int i = 0;

      while(i < s.length()) {
         char c = s.charAt(i++);
         if(c == 92) {
            pos[0] = i;
            int e = unescapeAt(s, pos);
            if(e < 0) {
               throw new IllegalArgumentException("Invalid escape sequence " + s.substring(i - 1, Math.min(i + 8, s.length())));
            }

            buf.appendCodePoint(e);
            i = pos[0];
         } else {
            buf.append(c);
         }
      }

      return buf.toString();
   }

   public static String unescapeLeniently(String s) {
      StringBuilder buf = new StringBuilder();
      int[] pos = new int[1];
      int i = 0;

      while(i < s.length()) {
         char c = s.charAt(i++);
         if(c == 92) {
            pos[0] = i;
            int e = unescapeAt(s, pos);
            if(e < 0) {
               buf.append(c);
            } else {
               buf.appendCodePoint(e);
               i = pos[0];
            }
         } else {
            buf.append(c);
         }
      }

      return buf.toString();
   }

   public static String hex(long ch) {
      return hex(ch, 4);
   }

   public static String hex(long i, int places) {
      if(i == Long.MIN_VALUE) {
         return "-8000000000000000";
      } else {
         boolean negative = i < 0L;
         if(negative) {
            i = -i;
         }

         String result = Long.toString(i, 16).toUpperCase(Locale.ENGLISH);
         if(result.length() < places) {
            result = "0000000000000000".substring(result.length(), places) + result;
         }

         return negative?'-' + result:result;
      }
   }

   public static String hex(CharSequence s) {
      return ((StringBuilder)hex(s, 4, ",", true, new StringBuilder())).toString();
   }

   public static Appendable hex(CharSequence s, int width, CharSequence separator, boolean useCodePoints, Appendable result) {
      try {
         int cp;
         if(useCodePoints) {
            for(int i = 0; i < s.length(); i += UTF16.getCharCount(cp)) {
               cp = Character.codePointAt(s, i);
               if(i != 0) {
                  result.append(separator);
               }

               result.append(hex((long)cp, width));
            }
         } else {
            for(cp = 0; cp < s.length(); ++cp) {
               if(cp != 0) {
                  result.append(separator);
               }

               result.append(hex((long)s.charAt(cp), width));
            }
         }

         return result;
      } catch (IOException var7) {
         throw new IllegalIcuArgumentException(var7);
      }
   }

   public static String hex(byte[] o, int start, int end, String separator) {
      StringBuilder result = new StringBuilder();

      for(int i = start; i < end; ++i) {
         if(i != 0) {
            result.append(separator);
         }

         result.append(hex((long)o[i]));
      }

      return result.toString();
   }

   public static String hex(CharSequence s, int width, CharSequence separator) {
      return ((StringBuilder)hex(s, width, separator, true, new StringBuilder())).toString();
   }

   public static void split(String s, char divider, String[] output) {
      int last = 0;
      int current = 0;

      int i;
      for(i = 0; i < s.length(); ++i) {
         if(s.charAt(i) == divider) {
            output[current++] = s.substring(last, i);
            last = i + 1;
         }
      }

      for(output[current++] = s.substring(last, i); current < output.length; output[current++] = "") {
         ;
      }

   }

   public static String[] split(String s, char divider) {
      int last = 0;
      ArrayList<String> output = new ArrayList();

      int i;
      for(i = 0; i < s.length(); ++i) {
         if(s.charAt(i) == divider) {
            output.add(s.substring(last, i));
            last = i + 1;
         }
      }

      output.add(s.substring(last, i));
      return (String[])output.toArray(new String[output.size()]);
   }

   public static int lookup(String source, String[] target) {
      for(int i = 0; i < target.length; ++i) {
         if(source.equals(target[i])) {
            return i;
         }
      }

      return -1;
   }

   public static boolean parseChar(String id, int[] pos, char ch) {
      int start = pos[0];
      pos[0] = PatternProps.skipWhiteSpace(id, pos[0]);
      if(pos[0] != id.length() && id.charAt(pos[0]) == ch) {
         ++pos[0];
         return true;
      } else {
         pos[0] = start;
         return false;
      }
   }

   public static int parsePattern(String rule, int pos, int limit, String pattern, int[] parsedInts) {
      int[] p = new int[1];
      int intCount = 0;

      for(int i = 0; i < pattern.length(); ++i) {
         char cpat = pattern.charAt(i);
         switch(cpat) {
         case ' ':
            if(pos >= limit) {
               return -1;
            }

            char c = rule.charAt(pos++);
            if(!PatternProps.isWhiteSpace(c)) {
               return -1;
            }
         case '~':
            pos = PatternProps.skipWhiteSpace(rule, pos);
            break;
         case '#':
            p[0] = pos;
            parsedInts[intCount++] = parseInteger(rule, p, limit);
            if(p[0] == pos) {
               return -1;
            }

            pos = p[0];
            break;
         default:
            if(pos >= limit) {
               return -1;
            }

            char var10 = (char)UCharacter.toLowerCase(rule.charAt(pos++));
            if(var10 != cpat) {
               return -1;
            }
         }
      }

      return pos;
   }

   public static int parsePattern(String pat, Replaceable text, int index, int limit) {
      int ipat = 0;
      if(ipat == pat.length()) {
         return index;
      } else {
         int cpat = Character.codePointAt(pat, ipat);

         while(index < limit) {
            int c = text.char32At(index);
            if(cpat == 126) {
               if(PatternProps.isWhiteSpace(c)) {
                  index += UTF16.getCharCount(c);
                  continue;
               }

               ++ipat;
               if(ipat == pat.length()) {
                  return index;
               }
            } else {
               if(c != cpat) {
                  return -1;
               }

               int n = UTF16.getCharCount(c);
               index += n;
               ipat += n;
               if(ipat == pat.length()) {
                  return index;
               }
            }

            cpat = UTF16.charAt(pat, ipat);
         }

         return -1;
      }
   }

   public static int parseInteger(String rule, int[] pos, int limit) {
      int count = 0;
      int value = 0;
      int p = pos[0];
      int radix = 10;
      if(rule.regionMatches(true, p, "0x", 0, 2)) {
         p += 2;
         radix = 16;
      } else if(p < limit && rule.charAt(p) == 48) {
         ++p;
         count = 1;
         radix = 8;
      }

      while(p < limit) {
         int d = UCharacter.digit(rule.charAt(p++), radix);
         if(d < 0) {
            --p;
            break;
         }

         ++count;
         int v = value * radix + d;
         if(v <= value) {
            return 0;
         }

         value = v;
      }

      if(count > 0) {
         pos[0] = p;
      }

      return value;
   }

   public static String parseUnicodeIdentifier(String str, int[] pos) {
      StringBuilder buf = new StringBuilder();

      int p;
      int ch;
      for(p = pos[0]; p < str.length(); p += UTF16.getCharCount(ch)) {
         ch = Character.codePointAt(str, p);
         if(buf.length() == 0) {
            if(!UCharacter.isUnicodeIdentifierStart(ch)) {
               return null;
            }

            buf.appendCodePoint(ch);
         } else {
            if(!UCharacter.isUnicodeIdentifierPart(ch)) {
               break;
            }

            buf.appendCodePoint(ch);
         }
      }

      pos[0] = p;
      return buf.toString();
   }

   private static void recursiveAppendNumber(Appendable result, int n, int radix, int minDigits) {
      try {
         int digit = n % radix;
         if(n >= radix || minDigits > 1) {
            recursiveAppendNumber(result, n / radix, radix, minDigits - 1);
         }

         result.append(DIGITS[digit]);
      } catch (IOException var5) {
         throw new IllegalIcuArgumentException(var5);
      }
   }

   public static Appendable appendNumber(Appendable result, int n, int radix, int minDigits) {
      try {
         if(radix >= 2 && radix <= 36) {
            int abs = n;
            if(n < 0) {
               abs = -n;
               result.append("-");
            }

            recursiveAppendNumber(result, abs, radix, minDigits);
            return result;
         } else {
            throw new IllegalArgumentException("Illegal radix " + radix);
         }
      } catch (IOException var5) {
         throw new IllegalIcuArgumentException(var5);
      }
   }

   public static int parseNumber(String text, int[] pos, int radix) {
      int n = 0;

      int p;
      for(p = pos[0]; p < text.length(); ++p) {
         int ch = Character.codePointAt(text, p);
         int d = UCharacter.digit(ch, radix);
         if(d < 0) {
            break;
         }

         n = radix * n + d;
         if(n < 0) {
            return -1;
         }
      }

      if(p == pos[0]) {
         return -1;
      } else {
         pos[0] = p;
         return n;
      }
   }

   public static boolean isUnprintable(int c) {
      return c < 32 || c > 126;
   }

   public static boolean escapeUnprintable(Appendable result, int c) {
      try {
         if(isUnprintable(c)) {
            result.append('\\');
            if((c & -65536) != 0) {
               result.append('U');
               result.append(DIGITS[15 & c >> 28]);
               result.append(DIGITS[15 & c >> 24]);
               result.append(DIGITS[15 & c >> 20]);
               result.append(DIGITS[15 & c >> 16]);
            } else {
               result.append('u');
            }

            result.append(DIGITS[15 & c >> 12]);
            result.append(DIGITS[15 & c >> 8]);
            result.append(DIGITS[15 & c >> 4]);
            result.append(DIGITS[15 & c]);
            return true;
         } else {
            return false;
         }
      } catch (IOException var3) {
         throw new IllegalIcuArgumentException(var3);
      }
   }

   public static int quotedIndexOf(String text, int start, int limit, String setOfChars) {
      for(int i = start; i < limit; ++i) {
         char c = text.charAt(i);
         if(c == 92) {
            ++i;
         } else if(c == 39) {
            while(true) {
               ++i;
               if(i >= limit || text.charAt(i) == 39) {
                  break;
               }
            }
         } else if(setOfChars.indexOf(c) >= 0) {
            return i;
         }
      }

      return -1;
   }

   public static void appendToRule(StringBuffer rule, int c, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf) {
      if(isLiteral || escapeUnprintable && isUnprintable(c)) {
         if(quoteBuf.length() > 0) {
            while(quoteBuf.length() >= 2 && quoteBuf.charAt(0) == 39 && quoteBuf.charAt(1) == 39) {
               rule.append('\\').append('\'');
               quoteBuf.delete(0, 2);
            }

            int trailingCount;
            for(trailingCount = 0; quoteBuf.length() >= 2 && quoteBuf.charAt(quoteBuf.length() - 2) == 39 && quoteBuf.charAt(quoteBuf.length() - 1) == 39; ++trailingCount) {
               quoteBuf.setLength(quoteBuf.length() - 2);
            }

            if(quoteBuf.length() > 0) {
               rule.append('\'');
               rule.append(quoteBuf);
               rule.append('\'');
               quoteBuf.setLength(0);
            }

            while(trailingCount-- > 0) {
               rule.append('\\').append('\'');
            }
         }

         if(c != -1) {
            if(c == 32) {
               int len = rule.length();
               if(len > 0 && rule.charAt(len - 1) != 32) {
                  rule.append(' ');
               }
            } else if(!escapeUnprintable || !escapeUnprintable(rule, c)) {
               rule.appendCodePoint(c);
            }
         }
      } else if(quoteBuf.length() != 0 || c != 39 && c != 92) {
         if(quoteBuf.length() <= 0 && (c < 33 || c > 126 || c >= 48 && c <= 57 || c >= 65 && c <= 90 || c >= 97 && c <= 122) && !PatternProps.isWhiteSpace(c)) {
            rule.appendCodePoint(c);
         } else {
            quoteBuf.appendCodePoint(c);
            if(c == 39) {
               quoteBuf.append((char)c);
            }
         }
      } else {
         rule.append('\\').append((char)c);
      }

   }

   public static void appendToRule(StringBuffer rule, String text, boolean isLiteral, boolean escapeUnprintable, StringBuffer quoteBuf) {
      for(int i = 0; i < text.length(); ++i) {
         appendToRule(rule, text.charAt(i), isLiteral, escapeUnprintable, quoteBuf);
      }

   }

   public static void appendToRule(StringBuffer rule, UnicodeMatcher matcher, boolean escapeUnprintable, StringBuffer quoteBuf) {
      if(matcher != null) {
         appendToRule(rule, matcher.toPattern(escapeUnprintable), true, escapeUnprintable, quoteBuf);
      }

   }

   public static final int compareUnsigned(int source, int target) {
      source = source - Integer.MIN_VALUE;
      target = target - Integer.MIN_VALUE;
      return source < target?-1:(source > target?1:0);
   }

   public static final byte highBit(int n) {
      if(n <= 0) {
         return (byte)-1;
      } else {
         byte bit = 0;
         if(n >= 65536) {
            n >>= 16;
            bit = (byte)(bit + 16);
         }

         if(n >= 256) {
            n >>= 8;
            bit = (byte)(bit + 8);
         }

         if(n >= 16) {
            n >>= 4;
            bit = (byte)(bit + 4);
         }

         if(n >= 4) {
            n >>= 2;
            bit = (byte)(bit + 2);
         }

         if(n >= 2) {
            n = n >> 1;
            ++bit;
         }

         return bit;
      }
   }

   public static String valueOf(int[] source) {
      StringBuilder result = new StringBuilder(source.length);

      for(int i = 0; i < source.length; ++i) {
         result.appendCodePoint(source[i]);
      }

      return result.toString();
   }

   public static String repeat(String s, int count) {
      if(count <= 0) {
         return "";
      } else if(count == 1) {
         return s;
      } else {
         StringBuilder result = new StringBuilder();

         for(int i = 0; i < count; ++i) {
            result.append(s);
         }

         return result.toString();
      }
   }

   public static String[] splitString(String src, String target) {
      return src.split("\\Q" + target + "\\E");
   }

   public static String[] splitWhitespace(String src) {
      return src.split("\\s+");
   }

   public static String fromHex(String string, int minLength, String separator) {
      return fromHex(string, minLength, Pattern.compile(separator != null?separator:"\\s+"));
   }

   public static String fromHex(String string, int minLength, Pattern separator) {
      StringBuilder buffer = new StringBuilder();
      String[] parts = separator.split(string);

      for(String part : parts) {
         if(part.length() < minLength) {
            throw new IllegalArgumentException("code point too short: " + part);
         }

         int cp = Integer.parseInt(part, 16);
         buffer.appendCodePoint(cp);
      }

      return buffer.toString();
   }

   public static ClassLoader getFallbackClassLoader() {
      ClassLoader cl = Thread.currentThread().getContextClassLoader();
      if(cl == null) {
         cl = ClassLoader.getSystemClassLoader();
         if(cl == null) {
            throw new RuntimeException("No accessible class loader is available.");
         }
      }

      return cl;
   }
}
