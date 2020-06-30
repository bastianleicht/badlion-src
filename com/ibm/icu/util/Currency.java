package com.ibm.icu.util;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.TextTrieMap;
import com.ibm.icu.text.CurrencyDisplayNames;
import com.ibm.icu.text.CurrencyMetaInfo;
import com.ibm.icu.util.MeasureUnit;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.Serializable;
import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Map.Entry;

public class Currency extends MeasureUnit implements Serializable {
   private static final long serialVersionUID = -5839973855554750484L;
   private static final boolean DEBUG = ICUDebug.enabled("currency");
   private static ICUCache CURRENCY_NAME_CACHE = new SimpleCache();
   private String isoCode;
   public static final int SYMBOL_NAME = 0;
   public static final int LONG_NAME = 1;
   public static final int PLURAL_LONG_NAME = 2;
   private static Currency.ServiceShim shim;
   private static final String EUR_STR = "EUR";
   private static final ICUCache currencyCodeCache = new SimpleCache();
   private static final ULocale UND = new ULocale("und");
   private static final String[] EMPTY_STRING_ARRAY = new String[0];
   private static final int[] POW10 = new int[]{1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000};
   private static SoftReference ALL_TENDER_CODES;
   private static SoftReference ALL_CODES_AS_SET;

   private static Currency.ServiceShim getShim() {
      if(shim == null) {
         try {
            Class<?> cls = Class.forName("com.ibm.icu.util.CurrencyServiceShim");
            shim = (Currency.ServiceShim)cls.newInstance();
         } catch (Exception var1) {
            if(DEBUG) {
               var1.printStackTrace();
            }

            throw new RuntimeException(var1.getMessage());
         }
      }

      return shim;
   }

   public static Currency getInstance(Locale locale) {
      return getInstance(ULocale.forLocale(locale));
   }

   public static Currency getInstance(ULocale locale) {
      String currency = locale.getKeywordValue("currency");
      return currency != null?getInstance(currency):(shim == null?createCurrency(locale):shim.createInstance(locale));
   }

   public static String[] getAvailableCurrencyCodes(ULocale loc, Date d) {
      CurrencyMetaInfo.CurrencyFilter filter = CurrencyMetaInfo.CurrencyFilter.onDate(d).withRegion(loc.getCountry());
      List<String> list = getTenderCurrencies(filter);
      return list.isEmpty()?null:(String[])list.toArray(new String[list.size()]);
   }

   public static Set getAvailableCurrencies() {
      CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
      List<String> list = info.currencies(CurrencyMetaInfo.CurrencyFilter.all());
      HashSet<Currency> resultSet = new HashSet(list.size());

      for(String code : list) {
         resultSet.add(new Currency(code));
      }

      return resultSet;
   }

   static Currency createCurrency(ULocale loc) {
      String variant = loc.getVariant();
      if("EURO".equals(variant)) {
         return new Currency("EUR");
      } else {
         String code = (String)currencyCodeCache.get(loc);
         if(code == null) {
            String country = loc.getCountry();
            CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
            List<String> list = info.currencies(CurrencyMetaInfo.CurrencyFilter.onRegion(country));
            if(list.size() <= 0) {
               return null;
            }

            code = (String)list.get(0);
            boolean isPreEuro = "PREEURO".equals(variant);
            if(isPreEuro && "EUR".equals(code)) {
               if(list.size() < 2) {
                  return null;
               }

               code = (String)list.get(1);
            }

            currencyCodeCache.put(loc, code);
         }

         return new Currency(code);
      }
   }

   public static Currency getInstance(String theISOCode) {
      if(theISOCode == null) {
         throw new NullPointerException("The input currency code is null.");
      } else if(!isAlpha3Code(theISOCode)) {
         throw new IllegalArgumentException("The input currency code is not 3-letter alphabetic code.");
      } else {
         return new Currency(theISOCode.toUpperCase(Locale.ENGLISH));
      }
   }

   private static boolean isAlpha3Code(String code) {
      if(code.length() != 3) {
         return false;
      } else {
         for(int i = 0; i < 3; ++i) {
            char ch = code.charAt(i);
            if(ch < 65 || ch > 90 && ch < 97 || ch > 122) {
               return false;
            }
         }

         return true;
      }
   }

   public static Object registerInstance(Currency currency, ULocale locale) {
      return getShim().registerInstance(currency, locale);
   }

   public static boolean unregister(Object registryKey) {
      if(registryKey == null) {
         throw new IllegalArgumentException("registryKey must not be null");
      } else {
         return shim == null?false:shim.unregister(registryKey);
      }
   }

   public static Locale[] getAvailableLocales() {
      return shim == null?ICUResourceBundle.getAvailableLocales():shim.getAvailableLocales();
   }

   public static ULocale[] getAvailableULocales() {
      return shim == null?ICUResourceBundle.getAvailableULocales():shim.getAvailableULocales();
   }

   public static final String[] getKeywordValuesForLocale(String key, ULocale locale, boolean commonlyUsed) {
      if(!"currency".equals(key)) {
         return EMPTY_STRING_ARRAY;
      } else if(!commonlyUsed) {
         return (String[])getAllTenderCurrencies().toArray(new String[0]);
      } else {
         String prefRegion = locale.getCountry();
         if(prefRegion.length() == 0) {
            if(UND.equals(locale)) {
               return EMPTY_STRING_ARRAY;
            }

            ULocale loc = ULocale.addLikelySubtags(locale);
            prefRegion = loc.getCountry();
         }

         CurrencyMetaInfo.CurrencyFilter filter = CurrencyMetaInfo.CurrencyFilter.now().withRegion(prefRegion);
         List<String> result = getTenderCurrencies(filter);
         return result.size() == 0?EMPTY_STRING_ARRAY:(String[])result.toArray(new String[result.size()]);
      }
   }

   public int hashCode() {
      return this.isoCode.hashCode();
   }

   public boolean equals(Object rhs) {
      if(rhs == null) {
         return false;
      } else if(rhs == this) {
         return true;
      } else {
         try {
            Currency c = (Currency)rhs;
            return this.isoCode.equals(c.isoCode);
         } catch (ClassCastException var3) {
            return false;
         }
      }
   }

   public String getCurrencyCode() {
      return this.isoCode;
   }

   public int getNumericCode() {
      int code = 0;

      try {
         UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "currencyNumericCodes", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         UResourceBundle codeMap = bundle.get("codeMap");
         UResourceBundle numCode = codeMap.get(this.isoCode);
         code = numCode.getInt();
      } catch (MissingResourceException var5) {
         ;
      }

      return code;
   }

   public String getSymbol() {
      return this.getSymbol(ULocale.getDefault(ULocale.Category.DISPLAY));
   }

   public String getSymbol(Locale loc) {
      return this.getSymbol(ULocale.forLocale(loc));
   }

   public String getSymbol(ULocale uloc) {
      return this.getName((ULocale)uloc, 0, new boolean[1]);
   }

   public String getName(Locale locale, int nameStyle, boolean[] isChoiceFormat) {
      return this.getName(ULocale.forLocale(locale), nameStyle, isChoiceFormat);
   }

   public String getName(ULocale locale, int nameStyle, boolean[] isChoiceFormat) {
      if(nameStyle != 0 && nameStyle != 1) {
         throw new IllegalArgumentException("bad name style: " + nameStyle);
      } else {
         if(isChoiceFormat != null) {
            isChoiceFormat[0] = false;
         }

         CurrencyDisplayNames names = CurrencyDisplayNames.getInstance(locale);
         return nameStyle == 0?names.getSymbol(this.isoCode):names.getName(this.isoCode);
      }
   }

   public String getName(Locale locale, int nameStyle, String pluralCount, boolean[] isChoiceFormat) {
      return this.getName(ULocale.forLocale(locale), nameStyle, pluralCount, isChoiceFormat);
   }

   public String getName(ULocale locale, int nameStyle, String pluralCount, boolean[] isChoiceFormat) {
      if(nameStyle != 2) {
         return this.getName(locale, nameStyle, isChoiceFormat);
      } else {
         if(isChoiceFormat != null) {
            isChoiceFormat[0] = false;
         }

         CurrencyDisplayNames names = CurrencyDisplayNames.getInstance(locale);
         return names.getPluralName(this.isoCode, pluralCount);
      }
   }

   public String getDisplayName() {
      return this.getName((Locale)Locale.getDefault(), 1, (boolean[])null);
   }

   public String getDisplayName(Locale locale) {
      return this.getName((Locale)locale, 1, (boolean[])null);
   }

   /** @deprecated */
   public static String parse(ULocale locale, String text, int type, ParsePosition pos) {
      List<TextTrieMap<Currency.CurrencyStringInfo>> currencyTrieVec = (List)CURRENCY_NAME_CACHE.get(locale);
      if(currencyTrieVec == null) {
         TextTrieMap<Currency.CurrencyStringInfo> currencyNameTrie = new TextTrieMap(true);
         TextTrieMap<Currency.CurrencyStringInfo> currencySymbolTrie = new TextTrieMap(false);
         currencyTrieVec = new ArrayList();
         currencyTrieVec.add(currencySymbolTrie);
         currencyTrieVec.add(currencyNameTrie);
         setupCurrencyTrieVec(locale, currencyTrieVec);
         CURRENCY_NAME_CACHE.put(locale, currencyTrieVec);
      }

      int maxLength = 0;
      String isoResult = null;
      TextTrieMap<Currency.CurrencyStringInfo> currencyNameTrie = (TextTrieMap)currencyTrieVec.get(1);
      Currency.CurrencyNameResultHandler handler = new Currency.CurrencyNameResultHandler();
      currencyNameTrie.find(text, pos.getIndex(), handler);
      List<Currency.CurrencyStringInfo> list = handler.getMatchedCurrencyNames();
      if(list != null && list.size() != 0) {
         for(Currency.CurrencyStringInfo info : list) {
            String isoCode = info.getISOCode();
            String currencyString = info.getCurrencyString();
            if(currencyString.length() > maxLength) {
               maxLength = currencyString.length();
               isoResult = isoCode;
            }
         }
      }

      if(type != 1) {
         TextTrieMap<Currency.CurrencyStringInfo> currencySymbolTrie = (TextTrieMap)currencyTrieVec.get(0);
         handler = new Currency.CurrencyNameResultHandler();
         currencySymbolTrie.find(text, pos.getIndex(), handler);
         list = handler.getMatchedCurrencyNames();
         if(list != null && list.size() != 0) {
            for(Currency.CurrencyStringInfo info : list) {
               String isoCode = info.getISOCode();
               String currencyString = info.getCurrencyString();
               if(currencyString.length() > maxLength) {
                  maxLength = currencyString.length();
                  isoResult = isoCode;
               }
            }
         }
      }

      int start = pos.getIndex();
      pos.setIndex(start + maxLength);
      return isoResult;
   }

   private static void setupCurrencyTrieVec(ULocale locale, List trieVec) {
      TextTrieMap<Currency.CurrencyStringInfo> symTrie = (TextTrieMap)trieVec.get(0);
      TextTrieMap<Currency.CurrencyStringInfo> trie = (TextTrieMap)trieVec.get(1);
      CurrencyDisplayNames names = CurrencyDisplayNames.getInstance(locale);

      for(Entry<String, String> e : names.symbolMap().entrySet()) {
         String symbol = (String)e.getKey();
         String isoCode = (String)e.getValue();
         symTrie.put(symbol, new Currency.CurrencyStringInfo(isoCode, symbol));
      }

      for(Entry<String, String> e : names.nameMap().entrySet()) {
         String name = (String)e.getKey();
         String isoCode = (String)e.getValue();
         trie.put(name, new Currency.CurrencyStringInfo(isoCode, name));
      }

   }

   public int getDefaultFractionDigits() {
      CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
      CurrencyMetaInfo.CurrencyDigits digits = info.currencyDigits(this.isoCode);
      return digits.fractionDigits;
   }

   public double getRoundingIncrement() {
      CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
      CurrencyMetaInfo.CurrencyDigits digits = info.currencyDigits(this.isoCode);
      int data1 = digits.roundingIncrement;
      if(data1 == 0) {
         return 0.0D;
      } else {
         int data0 = digits.fractionDigits;
         return data0 >= 0 && data0 < POW10.length?(double)data1 / (double)POW10[data0]:0.0D;
      }
   }

   public String toString() {
      return this.isoCode;
   }

   protected Currency(String theISOCode) {
      this.isoCode = theISOCode;
   }

   private static synchronized List getAllTenderCurrencies() {
      List<String> all = ALL_TENDER_CODES == null?null:(List)ALL_TENDER_CODES.get();
      if(all == null) {
         CurrencyMetaInfo.CurrencyFilter filter = CurrencyMetaInfo.CurrencyFilter.all();
         all = Collections.unmodifiableList(getTenderCurrencies(filter));
         ALL_TENDER_CODES = new SoftReference(all);
      }

      return all;
   }

   private static synchronized Set getAllCurrenciesAsSet() {
      Set<String> all = ALL_CODES_AS_SET == null?null:(Set)ALL_CODES_AS_SET.get();
      if(all == null) {
         CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
         all = Collections.unmodifiableSet(new HashSet(info.currencies(CurrencyMetaInfo.CurrencyFilter.all())));
         ALL_CODES_AS_SET = new SoftReference(all);
      }

      return all;
   }

   public static boolean isAvailable(String code, Date from, Date to) {
      if(!isAlpha3Code(code)) {
         return false;
      } else if(from != null && to != null && from.after(to)) {
         throw new IllegalArgumentException("To is before from");
      } else {
         code = code.toUpperCase(Locale.ENGLISH);
         boolean isKnown = getAllCurrenciesAsSet().contains(code);
         if(!isKnown) {
            return false;
         } else if(from == null && to == null) {
            return true;
         } else {
            CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
            List<String> allActive = info.currencies(CurrencyMetaInfo.CurrencyFilter.onDateRange(from, to).withCurrency(code));
            return allActive.contains(code);
         }
      }
   }

   private static List getTenderCurrencies(CurrencyMetaInfo.CurrencyFilter filter) {
      CurrencyMetaInfo info = CurrencyMetaInfo.getInstance();
      return info.currencies(filter.withTender());
   }

   private static class CurrencyNameResultHandler implements TextTrieMap.ResultHandler {
      private ArrayList resultList;

      private CurrencyNameResultHandler() {
      }

      public boolean handlePrefixMatch(int matchLength, Iterator values) {
         if(this.resultList == null) {
            this.resultList = new ArrayList();
         }

         while(values.hasNext()) {
            Currency.CurrencyStringInfo item = (Currency.CurrencyStringInfo)values.next();
            if(item == null) {
               break;
            }

            int i;
            for(i = 0; i < this.resultList.size(); ++i) {
               Currency.CurrencyStringInfo tmp = (Currency.CurrencyStringInfo)this.resultList.get(i);
               if(item.getISOCode().equals(tmp.getISOCode())) {
                  if(matchLength > tmp.getCurrencyString().length()) {
                     this.resultList.set(i, item);
                  }
                  break;
               }
            }

            if(i == this.resultList.size()) {
               this.resultList.add(item);
            }
         }

         return true;
      }

      List getMatchedCurrencyNames() {
         return this.resultList != null && this.resultList.size() != 0?this.resultList:null;
      }
   }

   private static final class CurrencyStringInfo {
      private String isoCode;
      private String currencyString;

      public CurrencyStringInfo(String isoCode, String currencyString) {
         this.isoCode = isoCode;
         this.currencyString = currencyString;
      }

      private String getISOCode() {
         return this.isoCode;
      }

      private String getCurrencyString() {
         return this.currencyString;
      }
   }

   abstract static class ServiceShim {
      abstract ULocale[] getAvailableULocales();

      abstract Locale[] getAvailableLocales();

      abstract Currency createInstance(ULocale var1);

      abstract Object registerInstance(Currency var1, ULocale var2);

      abstract boolean unregister(Object var1);
   }
}
