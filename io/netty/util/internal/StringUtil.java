package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;

public final class StringUtil {
   public static final String NEWLINE;
   private static final String[] BYTE2HEX_PAD = new String[256];
   private static final String[] BYTE2HEX_NOPAD = new String[256];
   private static final String EMPTY_STRING = "";

   public static String[] split(String value, char delim) {
      int end = value.length();
      List<String> res = new ArrayList();
      int start = 0;

      for(int i = 0; i < end; ++i) {
         if(value.charAt(i) == delim) {
            if(start == i) {
               res.add("");
            } else {
               res.add(value.substring(start, i));
            }

            start = i + 1;
         }
      }

      if(start == 0) {
         res.add(value);
      } else if(start != end) {
         res.add(value.substring(start, end));
      } else {
         for(int i = res.size() - 1; i >= 0 && ((String)((List)res).get(i)).isEmpty(); --i) {
            res.remove(i);
         }
      }

      return (String[])res.toArray(new String[res.size()]);
   }

   public static String byteToHexStringPadded(int value) {
      return BYTE2HEX_PAD[value & 255];
   }

   public static Appendable byteToHexStringPadded(Appendable buf, int value) {
      try {
         buf.append(byteToHexStringPadded(value));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return buf;
   }

   public static String toHexStringPadded(byte[] src) {
      return toHexStringPadded(src, 0, src.length);
   }

   public static String toHexStringPadded(byte[] src, int offset, int length) {
      return ((StringBuilder)toHexStringPadded(new StringBuilder(length << 1), src, offset, length)).toString();
   }

   public static Appendable toHexStringPadded(Appendable dst, byte[] src) {
      return toHexStringPadded(dst, src, 0, src.length);
   }

   public static Appendable toHexStringPadded(Appendable dst, byte[] src, int offset, int length) {
      int end = offset + length;

      for(int i = offset; i < end; ++i) {
         byteToHexStringPadded(dst, src[i]);
      }

      return dst;
   }

   public static String byteToHexString(int value) {
      return BYTE2HEX_NOPAD[value & 255];
   }

   public static Appendable byteToHexString(Appendable buf, int value) {
      try {
         buf.append(byteToHexString(value));
      } catch (IOException var3) {
         PlatformDependent.throwException(var3);
      }

      return buf;
   }

   public static String toHexString(byte[] src) {
      return toHexString(src, 0, src.length);
   }

   public static String toHexString(byte[] src, int offset, int length) {
      return ((StringBuilder)toHexString(new StringBuilder(length << 1), src, offset, length)).toString();
   }

   public static Appendable toHexString(Appendable dst, byte[] src) {
      return toHexString(dst, src, 0, src.length);
   }

   public static Appendable toHexString(Appendable dst, byte[] src, int offset, int length) {
      assert length >= 0;

      if(length == 0) {
         return dst;
      } else {
         int end = offset + length;
         int endMinusOne = end - 1;

         int i;
         for(i = offset; i < endMinusOne && src[i] == 0; ++i) {
            ;
         }

         byteToHexString(dst, src[i++]);
         int remaining = end - i;
         toHexStringPadded(dst, src, i, remaining);
         return dst;
      }
   }

   public static String simpleClassName(Object o) {
      return o == null?"null_object":simpleClassName(o.getClass());
   }

   public static String simpleClassName(Class clazz) {
      if(clazz == null) {
         return "null_class";
      } else {
         Package pkg = clazz.getPackage();
         return pkg != null?clazz.getName().substring(pkg.getName().length() + 1):clazz.getName();
      }
   }

   static {
      String newLine;
      try {
         newLine = (new Formatter()).format("%n", new Object[0]).toString();
      } catch (Exception var4) {
         newLine = "\n";
      }

      NEWLINE = newLine;

      int i;
      for(i = 0; i < 10; ++i) {
         StringBuilder buf = new StringBuilder(2);
         buf.append('0');
         buf.append(i);
         BYTE2HEX_PAD[i] = buf.toString();
         BYTE2HEX_NOPAD[i] = String.valueOf(i);
      }

      while(i < 16) {
         StringBuilder buf = new StringBuilder(2);
         char c = (char)(97 + i - 10);
         buf.append('0');
         buf.append(c);
         BYTE2HEX_PAD[i] = buf.toString();
         BYTE2HEX_NOPAD[i] = String.valueOf(c);
         ++i;
      }

      while(i < BYTE2HEX_PAD.length) {
         StringBuilder buf = new StringBuilder(2);
         buf.append(Integer.toHexString(i));
         String str = buf.toString();
         BYTE2HEX_PAD[i] = str;
         BYTE2HEX_NOPAD[i] = str;
         ++i;
      }

   }
}
