package org.apache.logging.log4j.core.helpers;

import org.apache.logging.log4j.core.helpers.Strings;

public final class Transform {
   private static final String CDATA_START = "<![CDATA[";
   private static final String CDATA_END = "]]>";
   private static final String CDATA_PSEUDO_END = "]]&gt;";
   private static final String CDATA_EMBEDED_END = "]]>]]&gt;<![CDATA[";
   private static final int CDATA_END_LEN = "]]>".length();

   public static String escapeHtmlTags(String input) {
      if(!Strings.isEmpty(input) && (input.indexOf(34) != -1 || input.indexOf(38) != -1 || input.indexOf(60) != -1 || input.indexOf(62) != -1)) {
         StringBuilder buf = new StringBuilder(input.length() + 6);
         char ch = ' ';
         int len = input.length();

         for(int i = 0; i < len; ++i) {
            ch = input.charAt(i);
            if(ch > 62) {
               buf.append(ch);
            } else if(ch == 60) {
               buf.append("&lt;");
            } else if(ch == 62) {
               buf.append("&gt;");
            } else if(ch == 38) {
               buf.append("&amp;");
            } else if(ch == 34) {
               buf.append("&quot;");
            } else {
               buf.append(ch);
            }
         }

         return buf.toString();
      } else {
         return input;
      }
   }

   public static void appendEscapingCDATA(StringBuilder buf, String str) {
      if(str != null) {
         int end = str.indexOf("]]>");
         if(end < 0) {
            buf.append(str);
         } else {
            int start;
            for(start = 0; end > -1; end = str.indexOf("]]>", start)) {
               buf.append(str.substring(start, end));
               buf.append("]]>]]&gt;<![CDATA[");
               start = end + CDATA_END_LEN;
               if(start >= str.length()) {
                  return;
               }
            }

            buf.append(str.substring(start));
         }
      }

   }

   public static String escapeJsonControlCharacters(String input) {
      if(!Strings.isEmpty(input) && (input.indexOf(34) != -1 || input.indexOf(92) != -1 || input.indexOf(47) != -1 || input.indexOf(8) != -1 || input.indexOf(12) != -1 || input.indexOf(10) != -1 || input.indexOf(13) != -1 || input.indexOf(9) != -1)) {
         StringBuilder buf = new StringBuilder(input.length() + 6);
         int len = input.length();

         for(int i = 0; i < len; ++i) {
            char ch = input.charAt(i);
            String escBs = "\\\\";
            switch(ch) {
            case '\b':
               buf.append("\\\\");
               buf.append('b');
               break;
            case '\t':
               buf.append("\\\\");
               buf.append('t');
               break;
            case '\n':
               buf.append("\\\\");
               buf.append('n');
               break;
            case '\f':
               buf.append("\\\\");
               buf.append('f');
               break;
            case '\r':
               buf.append("\\\\");
               buf.append('r');
               break;
            case '\"':
               buf.append("\\\\");
               buf.append(ch);
               break;
            case '/':
               buf.append("\\\\");
               buf.append(ch);
               break;
            case '\\':
               buf.append("\\\\");
               buf.append(ch);
               break;
            default:
               buf.append(ch);
            }
         }

         return buf.toString();
      } else {
         return input;
      }
   }
}
