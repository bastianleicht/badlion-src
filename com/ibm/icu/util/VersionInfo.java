package com.ibm.icu.util;

import com.ibm.icu.util.LocaleData;
import com.ibm.icu.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

public final class VersionInfo implements Comparable {
   public static final VersionInfo UNICODE_1_0 = getInstance(1, 0, 0, 0);
   public static final VersionInfo UNICODE_1_0_1 = getInstance(1, 0, 1, 0);
   public static final VersionInfo UNICODE_1_1_0 = getInstance(1, 1, 0, 0);
   public static final VersionInfo UNICODE_1_1_5 = getInstance(1, 1, 5, 0);
   public static final VersionInfo UNICODE_2_0 = getInstance(2, 0, 0, 0);
   public static final VersionInfo UNICODE_2_1_2 = getInstance(2, 1, 2, 0);
   public static final VersionInfo UNICODE_2_1_5 = getInstance(2, 1, 5, 0);
   public static final VersionInfo UNICODE_2_1_8 = getInstance(2, 1, 8, 0);
   public static final VersionInfo UNICODE_2_1_9 = getInstance(2, 1, 9, 0);
   public static final VersionInfo UNICODE_3_0 = getInstance(3, 0, 0, 0);
   public static final VersionInfo UNICODE_3_0_1 = getInstance(3, 0, 1, 0);
   public static final VersionInfo UNICODE_3_1_0 = getInstance(3, 1, 0, 0);
   public static final VersionInfo UNICODE_3_1_1 = getInstance(3, 1, 1, 0);
   public static final VersionInfo UNICODE_3_2 = getInstance(3, 2, 0, 0);
   public static final VersionInfo UNICODE_4_0 = getInstance(4, 0, 0, 0);
   public static final VersionInfo UNICODE_4_0_1 = getInstance(4, 0, 1, 0);
   public static final VersionInfo UNICODE_4_1 = getInstance(4, 1, 0, 0);
   public static final VersionInfo UNICODE_5_0 = getInstance(5, 0, 0, 0);
   public static final VersionInfo UNICODE_5_1 = getInstance(5, 1, 0, 0);
   public static final VersionInfo UNICODE_5_2 = getInstance(5, 2, 0, 0);
   public static final VersionInfo UNICODE_6_0 = getInstance(6, 0, 0, 0);
   public static final VersionInfo UNICODE_6_1 = getInstance(6, 1, 0, 0);
   public static final VersionInfo UNICODE_6_2 = getInstance(6, 2, 0, 0);
   public static final VersionInfo ICU_VERSION = getInstance(51, 2, 0, 0);
   /** @deprecated */
   public static final String ICU_DATA_VERSION_PATH = "51b";
   /** @deprecated */
   public static final VersionInfo ICU_DATA_VERSION = getInstance(51, 2, 0, 0);
   public static final VersionInfo UCOL_RUNTIME_VERSION = getInstance(7);
   public static final VersionInfo UCOL_BUILDER_VERSION = getInstance(8);
   public static final VersionInfo UCOL_TAILORINGS_VERSION = getInstance(1);
   private static volatile VersionInfo javaVersion;
   private static final VersionInfo UNICODE_VERSION = UNICODE_6_2;
   private int m_version_;
   private static final ConcurrentHashMap MAP_ = new ConcurrentHashMap();
   private static final int LAST_BYTE_MASK_ = 255;
   private static final String INVALID_VERSION_NUMBER_ = "Invalid version number: Version number may be negative or greater than 255";

   public static VersionInfo getInstance(String version) {
      int length = version.length();
      int[] array = new int[]{0, 0, 0, 0};
      int count = 0;

      int index;
      for(index = 0; count < 4 && index < length; ++index) {
         char c = version.charAt(index);
         if(c == 46) {
            ++count;
         } else {
            c = (char)(c - 48);
            if(c < 0 || c > 9) {
               throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
            }

            array[count] *= 10;
            array[count] += c;
         }
      }

      if(index != length) {
         throw new IllegalArgumentException("Invalid version number: String \'" + version + "\' exceeds version format");
      } else {
         for(int i = 0; i < 4; ++i) {
            if(array[i] < 0 || array[i] > 255) {
               throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
            }
         }

         return getInstance(array[0], array[1], array[2], array[3]);
      }
   }

   public static VersionInfo getInstance(int major, int minor, int milli, int micro) {
      if(major >= 0 && major <= 255 && minor >= 0 && minor <= 255 && milli >= 0 && milli <= 255 && micro >= 0 && micro <= 255) {
         int version = getInt(major, minor, milli, micro);
         Integer key = Integer.valueOf(version);
         VersionInfo result = (VersionInfo)MAP_.get(key);
         if(result == null) {
            result = new VersionInfo(version);
            VersionInfo tmpvi = (VersionInfo)MAP_.putIfAbsent(key, result);
            if(tmpvi != null) {
               result = tmpvi;
            }
         }

         return result;
      } else {
         throw new IllegalArgumentException("Invalid version number: Version number may be negative or greater than 255");
      }
   }

   public static VersionInfo getInstance(int major, int minor, int milli) {
      return getInstance(major, minor, milli, 0);
   }

   public static VersionInfo getInstance(int major, int minor) {
      return getInstance(major, minor, 0, 0);
   }

   public static VersionInfo getInstance(int major) {
      return getInstance(major, 0, 0, 0);
   }

   /** @deprecated */
   public static VersionInfo javaVersion() {
      if(javaVersion == null) {
         synchronized(VersionInfo.class) {
            if(javaVersion == null) {
               String s = System.getProperty("java.version");
               char[] chars = s.toCharArray();
               int r = 0;
               int w = 0;
               int count = 0;
               boolean numeric = false;

               while(r < chars.length) {
                  char c = chars[r++];
                  if(c >= 48 && c <= 57) {
                     numeric = true;
                     chars[w++] = c;
                  } else if(numeric) {
                     if(count == 3) {
                        break;
                     }

                     numeric = false;
                     chars[w++] = 46;
                     ++count;
                  }
               }

               while(w > 0 && chars[w - 1] == 46) {
                  --w;
               }

               String vs = new String(chars, 0, w);
               javaVersion = getInstance(vs);
            }
         }
      }

      return javaVersion;
   }

   public String toString() {
      StringBuilder result = new StringBuilder(7);
      result.append(this.getMajor());
      result.append('.');
      result.append(this.getMinor());
      result.append('.');
      result.append(this.getMilli());
      result.append('.');
      result.append(this.getMicro());
      return result.toString();
   }

   public int getMajor() {
      return this.m_version_ >> 24 & 255;
   }

   public int getMinor() {
      return this.m_version_ >> 16 & 255;
   }

   public int getMilli() {
      return this.m_version_ >> 8 & 255;
   }

   public int getMicro() {
      return this.m_version_ & 255;
   }

   public boolean equals(Object other) {
      return other == this;
   }

   public int compareTo(VersionInfo other) {
      return this.m_version_ - other.m_version_;
   }

   private VersionInfo(int compactversion) {
      this.m_version_ = compactversion;
   }

   private static int getInt(int major, int minor, int milli, int micro) {
      return major << 24 | minor << 16 | milli << 8 | micro;
   }

   public static void main(String[] args) {
      String icuApiVer;
      if(ICU_VERSION.getMajor() <= 4) {
         if(ICU_VERSION.getMinor() % 2 != 0) {
            int major = ICU_VERSION.getMajor();
            int minor = ICU_VERSION.getMinor() + 1;
            if(minor >= 10) {
               minor -= 10;
               ++major;
            }

            icuApiVer = "" + major + "." + minor + "M" + ICU_VERSION.getMilli();
         } else {
            icuApiVer = ICU_VERSION.getVersionString(2, 2);
         }
      } else if(ICU_VERSION.getMinor() == 0) {
         icuApiVer = "" + ICU_VERSION.getMajor() + "M" + ICU_VERSION.getMilli();
      } else {
         icuApiVer = ICU_VERSION.getVersionString(2, 2);
      }

      System.out.println("International Components for Unicode for Java " + icuApiVer);
      System.out.println("");
      System.out.println("Implementation Version: " + ICU_VERSION.getVersionString(2, 4));
      System.out.println("Unicode Data Version:   " + UNICODE_VERSION.getVersionString(2, 4));
      System.out.println("CLDR Data Version:      " + LocaleData.getCLDRVersion().getVersionString(2, 4));
      System.out.println("Time Zone Data Version: " + TimeZone.getTZDataVersion());
   }

   private String getVersionString(int minDigits, int maxDigits) {
      if(minDigits >= 1 && maxDigits >= 1 && minDigits <= 4 && maxDigits <= 4 && minDigits <= maxDigits) {
         int[] digits = new int[]{this.getMajor(), this.getMinor(), this.getMilli(), this.getMicro()};

         int numDigits;
         for(numDigits = maxDigits; numDigits > minDigits && digits[numDigits - 1] == 0; --numDigits) {
            ;
         }

         StringBuilder verStr = new StringBuilder(7);
         verStr.append(digits[0]);

         for(int i = 1; i < numDigits; ++i) {
            verStr.append(".");
            verStr.append(digits[i]);
         }

         return verStr.toString();
      } else {
         throw new IllegalArgumentException("Invalid min/maxDigits range");
      }
   }
}
