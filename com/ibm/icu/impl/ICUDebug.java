package com.ibm.icu.impl;

import com.ibm.icu.util.VersionInfo;

public final class ICUDebug {
   private static String params;
   private static boolean debug;
   private static boolean help;
   public static final String javaVersionString;
   public static final boolean isJDK14OrHigher;
   public static final VersionInfo javaVersion;

   public static VersionInfo getInstanceLenient(String s) {
      int[] ver = new int[4];
      boolean numeric = false;
      int i = 0;
      int vidx = 0;

      while(i < s.length()) {
         char c = s.charAt(i++);
         if(c >= 48 && c <= 57) {
            if(numeric) {
               ver[vidx] = ver[vidx] * 10 + (c - 48);
               if(ver[vidx] > 255) {
                  ver[vidx] = 0;
                  break;
               }
            } else {
               numeric = true;
               ver[vidx] = c - 48;
            }
         } else if(numeric) {
            if(vidx == 3) {
               break;
            }

            numeric = false;
            ++vidx;
         }
      }

      return VersionInfo.getInstance(ver[0], ver[1], ver[2], ver[3]);
   }

   public static boolean enabled() {
      return debug;
   }

   public static boolean enabled(String arg) {
      if(debug) {
         boolean result = params.indexOf(arg) != -1;
         if(help) {
            System.out.println("\nICUDebug.enabled(" + arg + ") = " + result);
         }

         return result;
      } else {
         return false;
      }
   }

   public static String value(String arg) {
      String result = "false";
      if(debug) {
         int index = params.indexOf(arg);
         if(index != -1) {
            index = index + arg.length();
            if(params.length() > index && params.charAt(index) == 61) {
               ++index;
               int limit = params.indexOf(",", index);
               result = params.substring(index, limit == -1?params.length():limit);
            } else {
               result = "true";
            }
         }

         if(help) {
            System.out.println("\nICUDebug.value(" + arg + ") = " + result);
         }
      }

      return result;
   }

   static {
      try {
         params = System.getProperty("ICUDebug");
      } catch (SecurityException var1) {
         ;
      }

      debug = params != null;
      help = debug && (params.equals("") || params.indexOf("help") != -1);
      if(debug) {
         System.out.println("\nICUDebug=" + params);
      }

      javaVersionString = System.getProperty("java.version", "0");
      javaVersion = getInstanceLenient(javaVersionString);
      VersionInfo java14Version = VersionInfo.getInstance("1.4.0");
      isJDK14OrHigher = javaVersion.compareTo(java14Version) >= 0;
   }
}
