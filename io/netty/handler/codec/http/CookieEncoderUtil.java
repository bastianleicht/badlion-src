package io.netty.handler.codec.http;

import io.netty.util.internal.InternalThreadLocalMap;

final class CookieEncoderUtil {
   static StringBuilder stringBuilder() {
      return InternalThreadLocalMap.get().stringBuilder();
   }

   static String stripTrailingSeparator(StringBuilder buf) {
      if(buf.length() > 0) {
         buf.setLength(buf.length() - 2);
      }

      return buf.toString();
   }

   static void add(StringBuilder param0, String param1, String param2) {
      // $FF: Couldn't be decompiled
   }

   static void addUnquoted(StringBuilder sb, String name, String val) {
      sb.append(name);
      sb.append('=');
      sb.append(val);
      sb.append(';');
      sb.append(' ');
   }

   static void addQuoted(StringBuilder sb, String name, String val) {
      if(val == null) {
         val = "";
      }

      sb.append(name);
      sb.append('=');
      sb.append('\"');
      sb.append(val.replace("\\", "\\\\").replace("\"", "\\\""));
      sb.append('\"');
      sb.append(';');
      sb.append(' ');
   }

   static void add(StringBuilder sb, String name, long val) {
      sb.append(name);
      sb.append('=');
      sb.append(val);
      sb.append(';');
      sb.append(' ');
   }
}
