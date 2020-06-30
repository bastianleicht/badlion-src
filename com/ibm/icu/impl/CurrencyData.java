package com.ibm.icu.impl;

import com.ibm.icu.text.CurrencyDisplayNames;
import com.ibm.icu.util.ULocale;
import java.util.Collections;
import java.util.Map;

public class CurrencyData {
   public static final CurrencyData.CurrencyDisplayInfoProvider provider;

   static {
      CurrencyData.CurrencyDisplayInfoProvider temp = null;

      try {
         Class<?> clzz = Class.forName("com.ibm.icu.impl.ICUCurrencyDisplayInfoProvider");
         temp = (CurrencyData.CurrencyDisplayInfoProvider)clzz.newInstance();
      } catch (Throwable var2) {
         temp = new CurrencyData.CurrencyDisplayInfoProvider() {
            public CurrencyData.CurrencyDisplayInfo getInstance(ULocale locale, boolean withFallback) {
               return CurrencyData.DefaultInfo.getWithFallback(withFallback);
            }

            public boolean hasData() {
               return false;
            }
         };
      }

      provider = temp;
   }

   public abstract static class CurrencyDisplayInfo extends CurrencyDisplayNames {
      public abstract Map getUnitPatterns();

      public abstract CurrencyData.CurrencyFormatInfo getFormatInfo(String var1);

      public abstract CurrencyData.CurrencySpacingInfo getSpacingInfo();
   }

   public interface CurrencyDisplayInfoProvider {
      CurrencyData.CurrencyDisplayInfo getInstance(ULocale var1, boolean var2);

      boolean hasData();
   }

   public static final class CurrencyFormatInfo {
      public final String currencyPattern;
      public final char monetarySeparator;
      public final char monetaryGroupingSeparator;

      public CurrencyFormatInfo(String currencyPattern, char monetarySeparator, char monetaryGroupingSeparator) {
         this.currencyPattern = currencyPattern;
         this.monetarySeparator = monetarySeparator;
         this.monetaryGroupingSeparator = monetaryGroupingSeparator;
      }
   }

   public static final class CurrencySpacingInfo {
      public final String beforeCurrencyMatch;
      public final String beforeContextMatch;
      public final String beforeInsert;
      public final String afterCurrencyMatch;
      public final String afterContextMatch;
      public final String afterInsert;
      private static final String DEFAULT_CUR_MATCH = "[:letter:]";
      private static final String DEFAULT_CTX_MATCH = "[:digit:]";
      private static final String DEFAULT_INSERT = " ";
      public static final CurrencyData.CurrencySpacingInfo DEFAULT = new CurrencyData.CurrencySpacingInfo("[:letter:]", "[:digit:]", " ", "[:letter:]", "[:digit:]", " ");

      public CurrencySpacingInfo(String beforeCurrencyMatch, String beforeContextMatch, String beforeInsert, String afterCurrencyMatch, String afterContextMatch, String afterInsert) {
         this.beforeCurrencyMatch = beforeCurrencyMatch;
         this.beforeContextMatch = beforeContextMatch;
         this.beforeInsert = beforeInsert;
         this.afterCurrencyMatch = afterCurrencyMatch;
         this.afterContextMatch = afterContextMatch;
         this.afterInsert = afterInsert;
      }
   }

   public static class DefaultInfo extends CurrencyData.CurrencyDisplayInfo {
      private final boolean fallback;
      private static final CurrencyData.CurrencyDisplayInfo FALLBACK_INSTANCE = new CurrencyData.DefaultInfo(true);
      private static final CurrencyData.CurrencyDisplayInfo NO_FALLBACK_INSTANCE = new CurrencyData.DefaultInfo(false);

      private DefaultInfo(boolean fallback) {
         this.fallback = fallback;
      }

      public static final CurrencyData.CurrencyDisplayInfo getWithFallback(boolean fallback) {
         return fallback?FALLBACK_INSTANCE:NO_FALLBACK_INSTANCE;
      }

      public String getName(String isoCode) {
         return this.fallback?isoCode:null;
      }

      public String getPluralName(String isoCode, String pluralType) {
         return this.fallback?isoCode:null;
      }

      public String getSymbol(String isoCode) {
         return this.fallback?isoCode:null;
      }

      public Map symbolMap() {
         return Collections.emptyMap();
      }

      public Map nameMap() {
         return Collections.emptyMap();
      }

      public ULocale getULocale() {
         return ULocale.ROOT;
      }

      public Map getUnitPatterns() {
         return this.fallback?Collections.emptyMap():null;
      }

      public CurrencyData.CurrencyFormatInfo getFormatInfo(String isoCode) {
         return null;
      }

      public CurrencyData.CurrencySpacingInfo getSpacingInfo() {
         return this.fallback?CurrencyData.CurrencySpacingInfo.DEFAULT:null;
      }
   }
}
