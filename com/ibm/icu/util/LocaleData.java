package com.ibm.icu.util;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.VersionInfo;
import java.util.MissingResourceException;

public final class LocaleData {
   private static final String MEASUREMENT_SYSTEM = "MeasurementSystem";
   private static final String PAPER_SIZE = "PaperSize";
   private static final String LOCALE_DISPLAY_PATTERN = "localeDisplayPattern";
   private static final String PATTERN = "pattern";
   private static final String SEPARATOR = "separator";
   private boolean noSubstitute;
   private ICUResourceBundle bundle;
   private ICUResourceBundle langBundle;
   public static final int ES_STANDARD = 0;
   public static final int ES_AUXILIARY = 1;
   public static final int ES_INDEX = 2;
   /** @deprecated */
   public static final int ES_CURRENCY = 3;
   public static final int ES_PUNCTUATION = 4;
   public static final int ES_COUNT = 5;
   public static final int QUOTATION_START = 0;
   public static final int QUOTATION_END = 1;
   public static final int ALT_QUOTATION_START = 2;
   public static final int ALT_QUOTATION_END = 3;
   public static final int DELIMITER_COUNT = 4;
   private static final String[] DELIMITER_TYPES = new String[]{"quotationStart", "quotationEnd", "alternateQuotationStart", "alternateQuotationEnd"};
   private static VersionInfo gCLDRVersion = null;

   public static UnicodeSet getExemplarSet(ULocale locale, int options) {
      return getInstance(locale).getExemplarSet(options, 0);
   }

   public static UnicodeSet getExemplarSet(ULocale locale, int options, int extype) {
      return getInstance(locale).getExemplarSet(options, extype);
   }

   public UnicodeSet getExemplarSet(int options, int extype) {
      String[] exemplarSetTypes = new String[]{"ExemplarCharacters", "AuxExemplarCharacters", "ExemplarCharactersIndex", "ExemplarCharactersCurrency", "ExemplarCharactersPunctuation"};
      if(extype == 3) {
         return new UnicodeSet();
      } else {
         try {
            ICUResourceBundle stringBundle = (ICUResourceBundle)this.bundle.get(exemplarSetTypes[extype]);
            if(this.noSubstitute && stringBundle.getLoadingStatus() == 2) {
               return null;
            } else {
               String unicodeSetPattern = stringBundle.getString();
               if(extype == 4) {
                  try {
                     return new UnicodeSet(unicodeSetPattern, 1 | options);
                  } catch (IllegalArgumentException var7) {
                     throw new IllegalArgumentException("Can\'t create exemplars for " + exemplarSetTypes[extype] + " in " + this.bundle.getLocale(), var7);
                  }
               } else {
                  return new UnicodeSet(unicodeSetPattern, 1 | options);
               }
            }
         } catch (MissingResourceException var8) {
            if(extype == 1) {
               return new UnicodeSet();
            } else if(extype == 2) {
               return null;
            } else {
               throw var8;
            }
         }
      }
   }

   public static final LocaleData getInstance(ULocale locale) {
      LocaleData ld = new LocaleData();
      ld.bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      ld.langBundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/lang", locale);
      ld.noSubstitute = false;
      return ld;
   }

   public static final LocaleData getInstance() {
      return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public void setNoSubstitute(boolean setting) {
      this.noSubstitute = setting;
   }

   public boolean getNoSubstitute() {
      return this.noSubstitute;
   }

   public String getDelimiter(int type) {
      ICUResourceBundle delimitersBundle = (ICUResourceBundle)this.bundle.get("delimiters");
      ICUResourceBundle stringBundle = delimitersBundle.getWithFallback(DELIMITER_TYPES[type]);
      return this.noSubstitute && stringBundle.getLoadingStatus() == 2?null:stringBundle.getString();
   }

   public static final LocaleData.MeasurementSystem getMeasurementSystem(ULocale locale) {
      UResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      UResourceBundle sysBundle = bundle.get("MeasurementSystem");
      int system = sysBundle.getInt();
      return LocaleData.MeasurementSystem.US.equals(system)?LocaleData.MeasurementSystem.US:(LocaleData.MeasurementSystem.SI.equals(system)?LocaleData.MeasurementSystem.SI:null);
   }

   public static final LocaleData.PaperSize getPaperSize(ULocale locale) {
      UResourceBundle bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      UResourceBundle obj = bundle.get("PaperSize");
      int[] size = obj.getIntVector();
      return new LocaleData.PaperSize(size[0], size[1]);
   }

   public String getLocaleDisplayPattern() {
      ICUResourceBundle locDispBundle = (ICUResourceBundle)this.langBundle.get("localeDisplayPattern");
      String localeDisplayPattern = locDispBundle.getStringWithFallback("pattern");
      return localeDisplayPattern;
   }

   public String getLocaleSeparator() {
      ICUResourceBundle locDispBundle = (ICUResourceBundle)this.langBundle.get("localeDisplayPattern");
      String localeSeparator = locDispBundle.getStringWithFallback("separator");
      return localeSeparator;
   }

   public static VersionInfo getCLDRVersion() {
      if(gCLDRVersion == null) {
         UResourceBundle supplementalDataBundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         UResourceBundle cldrVersionBundle = supplementalDataBundle.get("cldrVersion");
         gCLDRVersion = VersionInfo.getInstance(cldrVersionBundle.getString());
      }

      return gCLDRVersion;
   }

   public static final class MeasurementSystem {
      public static final LocaleData.MeasurementSystem SI = new LocaleData.MeasurementSystem(0);
      public static final LocaleData.MeasurementSystem US = new LocaleData.MeasurementSystem(1);
      private int systemID;

      private MeasurementSystem(int id) {
         this.systemID = id;
      }

      private boolean equals(int id) {
         return this.systemID == id;
      }
   }

   public static final class PaperSize {
      private int height;
      private int width;

      private PaperSize(int h, int w) {
         this.height = h;
         this.width = w;
      }

      public int getHeight() {
         return this.height;
      }

      public int getWidth() {
         return this.width;
      }
   }
}
