package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUResourceTableAccess;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.DisplayContext;
import com.ibm.icu.text.LocaleDisplayNames;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

public class LocaleDisplayNamesImpl extends LocaleDisplayNames {
   private final ULocale locale;
   private final LocaleDisplayNames.DialectHandling dialectHandling;
   private final DisplayContext capitalization;
   private final LocaleDisplayNamesImpl.DataTable langData;
   private final LocaleDisplayNamesImpl.DataTable regionData;
   private final LocaleDisplayNamesImpl.Appender appender;
   private final MessageFormat format;
   private final MessageFormat keyTypeFormat;
   private static final LocaleDisplayNamesImpl.Cache cache = new LocaleDisplayNamesImpl.Cache();
   private Map capitalizationUsage;
   private static final Map contextUsageTypeMap = new HashMap();

   public static LocaleDisplayNames getInstance(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) {
      synchronized(cache) {
         return cache.get(locale, dialectHandling);
      }
   }

   public static LocaleDisplayNames getInstance(ULocale locale, DisplayContext... contexts) {
      synchronized(cache) {
         return cache.get(locale, contexts);
      }
   }

   public LocaleDisplayNamesImpl(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) {
      this(locale, new DisplayContext[]{dialectHandling == LocaleDisplayNames.DialectHandling.STANDARD_NAMES?DisplayContext.STANDARD_NAMES:DisplayContext.DIALECT_NAMES, DisplayContext.CAPITALIZATION_NONE});
   }

   public LocaleDisplayNamesImpl(ULocale locale, DisplayContext... contexts) {
      this.capitalizationUsage = null;
      LocaleDisplayNames.DialectHandling dialectHandling = LocaleDisplayNames.DialectHandling.STANDARD_NAMES;
      DisplayContext capitalization = DisplayContext.CAPITALIZATION_NONE;

      for(DisplayContext contextItem : contexts) {
         switch(contextItem.type()) {
         case DIALECT_HANDLING:
            dialectHandling = contextItem.value() == DisplayContext.STANDARD_NAMES.value()?LocaleDisplayNames.DialectHandling.STANDARD_NAMES:LocaleDisplayNames.DialectHandling.DIALECT_NAMES;
            break;
         case CAPITALIZATION:
            capitalization = contextItem;
         }
      }

      this.dialectHandling = dialectHandling;
      this.capitalization = capitalization;
      this.langData = LocaleDisplayNamesImpl.LangDataTables.impl.get(locale);
      this.regionData = LocaleDisplayNamesImpl.RegionDataTables.impl.get(locale);
      this.locale = ULocale.ROOT.equals(this.langData.getLocale())?this.regionData.getLocale():this.langData.getLocale();
      String sep = this.langData.get("localeDisplayPattern", "separator");
      if("separator".equals(sep)) {
         sep = ", ";
      }

      this.appender = new LocaleDisplayNamesImpl.Appender(sep);
      String pattern = this.langData.get("localeDisplayPattern", "pattern");
      if("pattern".equals(pattern)) {
         pattern = "{0} ({1})";
      }

      this.format = new MessageFormat(pattern);
      String keyTypePattern = this.langData.get("localeDisplayPattern", "keyTypePattern");
      if("keyTypePattern".equals(keyTypePattern)) {
         keyTypePattern = "{0}={1}";
      }

      this.keyTypeFormat = new MessageFormat(keyTypePattern);
      if(capitalization == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU || capitalization == DisplayContext.CAPITALIZATION_FOR_STANDALONE) {
         this.capitalizationUsage = new HashMap();
         boolean[] noTransforms = new boolean[]{false, false};
         LocaleDisplayNamesImpl.CapitalizationContextUsage[] allUsages = LocaleDisplayNamesImpl.CapitalizationContextUsage.values();

         for(LocaleDisplayNamesImpl.CapitalizationContextUsage usage : allUsages) {
            this.capitalizationUsage.put(usage, noTransforms);
         }

         ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
         UResourceBundle contextTransformsBundle = null;

         try {
            var25 = rb.getWithFallback("contextTransforms");
         } catch (MissingResourceException var18) {
            var25 = null;
         }

         if(var25 != null) {
            UResourceBundleIterator ctIterator = var25.getIterator();

            while(ctIterator.hasNext()) {
               UResourceBundle contextTransformUsage = ctIterator.next();
               int[] intVector = contextTransformUsage.getIntVector();
               if(intVector.length >= 2) {
                  String usageKey = contextTransformUsage.getKey();
                  LocaleDisplayNamesImpl.CapitalizationContextUsage usage = (LocaleDisplayNamesImpl.CapitalizationContextUsage)contextUsageTypeMap.get(usageKey);
                  if(usage != null) {
                     boolean[] transforms = new boolean[]{intVector[0] != 0, intVector[1] != 0};
                     this.capitalizationUsage.put(usage, transforms);
                  }
               }
            }
         }
      }

   }

   public ULocale getLocale() {
      return this.locale;
   }

   public LocaleDisplayNames.DialectHandling getDialectHandling() {
      return this.dialectHandling;
   }

   public DisplayContext getContext(DisplayContext.Type type) {
      DisplayContext result;
      switch(type) {
      case DIALECT_HANDLING:
         result = this.dialectHandling == LocaleDisplayNames.DialectHandling.STANDARD_NAMES?DisplayContext.STANDARD_NAMES:DisplayContext.DIALECT_NAMES;
         break;
      case CAPITALIZATION:
         result = this.capitalization;
         break;
      default:
         result = DisplayContext.STANDARD_NAMES;
      }

      return result;
   }

   private String adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage usage, String name) {
      String result = name;
      boolean titlecase = false;
      switch(this.capitalization) {
      case CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE:
         titlecase = true;
         break;
      case CAPITALIZATION_FOR_UI_LIST_OR_MENU:
      case CAPITALIZATION_FOR_STANDALONE:
         if(this.capitalizationUsage != null) {
            boolean[] transforms = (boolean[])this.capitalizationUsage.get(usage);
            titlecase = this.capitalization == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU?transforms[0]:transforms[1];
         }
      }

      if(titlecase) {
         int stopPosLimit = 8;
         int len = name.length();
         if(stopPosLimit > len) {
            stopPosLimit = len;
         }

         int stopPos;
         for(stopPos = 0; stopPos < stopPosLimit; ++stopPos) {
            int ch = name.codePointAt(stopPos);
            if(ch < 65 || ch > 90 && ch < 97 || ch > 122 && ch < 192) {
               break;
            }

            if(ch >= 65536) {
               ++stopPos;
            }
         }

         if(stopPos > 0 && stopPos < len) {
            String firstWord = name.substring(0, stopPos);
            String firstWordTitleCase = UCharacter.toTitleCase(this.locale, firstWord, (BreakIterator)null, 768);
            result = firstWordTitleCase.concat(name.substring(stopPos));
         } else {
            result = UCharacter.toTitleCase(this.locale, name, (BreakIterator)null, 768);
         }
      }

      return result;
   }

   public String localeDisplayName(ULocale locale) {
      return this.localeDisplayNameInternal(locale);
   }

   public String localeDisplayName(Locale locale) {
      return this.localeDisplayNameInternal(ULocale.forLocale(locale));
   }

   public String localeDisplayName(String localeId) {
      return this.localeDisplayNameInternal(new ULocale(localeId));
   }

   private String localeDisplayNameInternal(ULocale locale) {
      String resultName = null;
      String lang = locale.getLanguage();
      if(locale.getBaseName().length() == 0) {
         lang = "root";
      }

      String script = locale.getScript();
      String country = locale.getCountry();
      String variant = locale.getVariant();
      boolean hasScript = script.length() > 0;
      boolean hasCountry = country.length() > 0;
      boolean hasVariant = variant.length() > 0;
      if(this.dialectHandling == LocaleDisplayNames.DialectHandling.DIALECT_NAMES) {
         label93: {
            if(hasScript && hasCountry) {
               String langScriptCountry = lang + '_' + script + '_' + country;
               String result = this.localeIdName(langScriptCountry);
               if(!result.equals(langScriptCountry)) {
                  resultName = result;
                  hasScript = false;
                  hasCountry = false;
                  break label93;
               }
            }

            if(hasScript) {
               String langScript = lang + '_' + script;
               String result = this.localeIdName(langScript);
               if(!result.equals(langScript)) {
                  resultName = result;
                  hasScript = false;
                  break label93;
               }
            }

            if(hasCountry) {
               String langCountry = lang + '_' + country;
               String result = this.localeIdName(langCountry);
               if(!result.equals(langCountry)) {
                  resultName = result;
                  hasCountry = false;
               }
            }
         }
      }

      if(resultName == null) {
         resultName = this.localeIdName(lang);
      }

      StringBuilder buf = new StringBuilder();
      if(hasScript) {
         buf.append(this.scriptDisplayNameInContext(script));
      }

      if(hasCountry) {
         this.appender.append(this.regionDisplayName(country), buf);
      }

      if(hasVariant) {
         this.appender.append(this.variantDisplayName(variant), buf);
      }

      Iterator<String> keys = locale.getKeywords();
      if(keys != null) {
         while(keys.hasNext()) {
            String key = (String)keys.next();
            String value = locale.getKeywordValue(key);
            String keyDisplayName = this.keyDisplayName(key);
            String valueDisplayName = this.keyValueDisplayName(key, value);
            if(!valueDisplayName.equals(value)) {
               this.appender.append(valueDisplayName, buf);
            } else if(!key.equals(keyDisplayName)) {
               String keyValue = this.keyTypeFormat.format(new String[]{keyDisplayName, valueDisplayName});
               this.appender.append(keyValue, buf);
            } else {
               this.appender.append(keyDisplayName, buf).append("=").append(valueDisplayName);
            }
         }
      }

      String resultRemainder = null;
      if(buf.length() > 0) {
         resultRemainder = buf.toString();
      }

      if(resultRemainder != null) {
         resultName = this.format.format(new Object[]{resultName, resultRemainder});
      }

      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.LANGUAGE, resultName);
   }

   private String localeIdName(String localeId) {
      return this.langData.get("Languages", localeId);
   }

   public String languageDisplayName(String lang) {
      return !lang.equals("root") && lang.indexOf(95) == -1?this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.LANGUAGE, this.langData.get("Languages", lang)):lang;
   }

   public String scriptDisplayName(String script) {
      String str = this.langData.get("Scripts%stand-alone", script);
      if(str.equals(script)) {
         str = this.langData.get("Scripts", script);
      }

      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.SCRIPT, str);
   }

   public String scriptDisplayNameInContext(String script) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.SCRIPT, this.langData.get("Scripts", script));
   }

   public String scriptDisplayName(int scriptCode) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.SCRIPT, this.scriptDisplayName(UScript.getShortName(scriptCode)));
   }

   public String regionDisplayName(String region) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.TERRITORY, this.regionData.get("Countries", region));
   }

   public String variantDisplayName(String variant) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.VARIANT, this.langData.get("Variants", variant));
   }

   public String keyDisplayName(String key) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.KEY, this.langData.get("Keys", key));
   }

   public String keyValueDisplayName(String key, String value) {
      return this.adjustForUsageAndContext(LocaleDisplayNamesImpl.CapitalizationContextUsage.TYPE, this.langData.get("Types", key, value));
   }

   public static boolean haveData(LocaleDisplayNamesImpl.DataTableType type) {
      switch(type) {
      case LANG:
         return LocaleDisplayNamesImpl.LangDataTables.impl instanceof LocaleDisplayNamesImpl.ICUDataTables;
      case REGION:
         return LocaleDisplayNamesImpl.RegionDataTables.impl instanceof LocaleDisplayNamesImpl.ICUDataTables;
      default:
         throw new IllegalArgumentException("unknown type: " + type);
      }
   }

   static {
      contextUsageTypeMap.put("languages", LocaleDisplayNamesImpl.CapitalizationContextUsage.LANGUAGE);
      contextUsageTypeMap.put("script", LocaleDisplayNamesImpl.CapitalizationContextUsage.SCRIPT);
      contextUsageTypeMap.put("territory", LocaleDisplayNamesImpl.CapitalizationContextUsage.TERRITORY);
      contextUsageTypeMap.put("variant", LocaleDisplayNamesImpl.CapitalizationContextUsage.VARIANT);
      contextUsageTypeMap.put("key", LocaleDisplayNamesImpl.CapitalizationContextUsage.KEY);
      contextUsageTypeMap.put("type", LocaleDisplayNamesImpl.CapitalizationContextUsage.TYPE);
   }

   static class Appender {
      private final String sep;

      Appender(String sep) {
         this.sep = sep;
      }

      StringBuilder append(String s, StringBuilder b) {
         if(b.length() > 0) {
            b.append(this.sep);
         }

         b.append(s);
         return b;
      }
   }

   private static class Cache {
      private ULocale locale;
      private LocaleDisplayNames.DialectHandling dialectHandling;
      private DisplayContext capitalization;
      private LocaleDisplayNames cache;

      private Cache() {
      }

      public LocaleDisplayNames get(ULocale locale, LocaleDisplayNames.DialectHandling dialectHandling) {
         if(dialectHandling != this.dialectHandling || DisplayContext.CAPITALIZATION_NONE != this.capitalization || !locale.equals(this.locale)) {
            this.locale = locale;
            this.dialectHandling = dialectHandling;
            this.capitalization = DisplayContext.CAPITALIZATION_NONE;
            this.cache = new LocaleDisplayNamesImpl(locale, dialectHandling);
         }

         return this.cache;
      }

      public LocaleDisplayNames get(ULocale locale, DisplayContext... contexts) {
         LocaleDisplayNames.DialectHandling dialectHandlingIn = LocaleDisplayNames.DialectHandling.STANDARD_NAMES;
         DisplayContext capitalizationIn = DisplayContext.CAPITALIZATION_NONE;

         for(DisplayContext contextItem : contexts) {
            switch(contextItem.type()) {
            case DIALECT_HANDLING:
               dialectHandlingIn = contextItem.value() == DisplayContext.STANDARD_NAMES.value()?LocaleDisplayNames.DialectHandling.STANDARD_NAMES:LocaleDisplayNames.DialectHandling.DIALECT_NAMES;
               break;
            case CAPITALIZATION:
               capitalizationIn = contextItem;
            }
         }

         if(dialectHandlingIn != this.dialectHandling || capitalizationIn != this.capitalization || !locale.equals(this.locale)) {
            this.locale = locale;
            this.dialectHandling = dialectHandlingIn;
            this.capitalization = capitalizationIn;
            this.cache = new LocaleDisplayNamesImpl(locale, contexts);
         }

         return this.cache;
      }
   }

   private static enum CapitalizationContextUsage {
      LANGUAGE,
      SCRIPT,
      TERRITORY,
      VARIANT,
      KEY,
      TYPE;
   }

   public static class DataTable {
      ULocale getLocale() {
         return ULocale.ROOT;
      }

      String get(String tableName, String code) {
         return this.get(tableName, (String)null, code);
      }

      String get(String tableName, String subTableName, String code) {
         return code;
      }
   }

   public static enum DataTableType {
      LANG,
      REGION;
   }

   abstract static class DataTables {
      public abstract LocaleDisplayNamesImpl.DataTable get(ULocale var1);

      public static LocaleDisplayNamesImpl.DataTables load(String className) {
         try {
            return (LocaleDisplayNamesImpl.DataTables)Class.forName(className).newInstance();
         } catch (Throwable var3) {
            final LocaleDisplayNamesImpl.DataTable NO_OP = new LocaleDisplayNamesImpl.DataTable();
            return new LocaleDisplayNamesImpl.DataTables() {
               public LocaleDisplayNamesImpl.DataTable get(ULocale locale) {
                  return NO_OP;
               }
            };
         }
      }
   }

   static class ICUDataTable extends LocaleDisplayNamesImpl.DataTable {
      private final ICUResourceBundle bundle;

      public ICUDataTable(String path, ULocale locale) {
         this.bundle = (ICUResourceBundle)UResourceBundle.getBundleInstance(path, locale.getBaseName());
      }

      public ULocale getLocale() {
         return this.bundle.getULocale();
      }

      public String get(String tableName, String subTableName, String code) {
         return ICUResourceTableAccess.getTableString(this.bundle, tableName, subTableName, code);
      }
   }

   abstract static class ICUDataTables extends LocaleDisplayNamesImpl.DataTables {
      private final String path;

      protected ICUDataTables(String path) {
         this.path = path;
      }

      public LocaleDisplayNamesImpl.DataTable get(ULocale locale) {
         return new LocaleDisplayNamesImpl.ICUDataTable(this.path, locale);
      }
   }

   static class LangDataTables {
      static final LocaleDisplayNamesImpl.DataTables impl = LocaleDisplayNamesImpl.DataTables.load("com.ibm.icu.impl.ICULangDataTables");
   }

   static class RegionDataTables {
      static final LocaleDisplayNamesImpl.DataTables impl = LocaleDisplayNamesImpl.DataTables.load("com.ibm.icu.impl.ICURegionDataTables");
   }
}
