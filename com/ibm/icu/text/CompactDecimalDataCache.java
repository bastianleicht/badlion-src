package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;

class CompactDecimalDataCache {
   private static final String SHORT_STYLE = "short";
   private static final String LONG_STYLE = "long";
   private static final String NUMBER_ELEMENTS = "NumberElements";
   private static final String PATTERN_LONG_PATH = "patternsLong/decimalFormat";
   private static final String PATTERNS_SHORT_PATH = "patternsShort/decimalFormat";
   static final String OTHER = "other";
   static final int MAX_DIGITS = 15;
   private static final String LATIN_NUMBERING_SYSTEM = "latn";
   private final ICUCache cache = new SimpleCache();

   CompactDecimalDataCache.DataBundle get(ULocale locale) {
      CompactDecimalDataCache.DataBundle result = (CompactDecimalDataCache.DataBundle)this.cache.get(locale);
      if(result == null) {
         result = load(locale);
         this.cache.put(locale, result);
      }

      return result;
   }

   private static CompactDecimalDataCache.DataBundle load(ULocale ulocale) {
      NumberingSystem ns = NumberingSystem.getInstance(ulocale);
      ICUResourceBundle r = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", ulocale);
      r = r.getWithFallback("NumberElements");
      String numberingSystemName = ns.getName();
      ICUResourceBundle shortDataBundle = null;
      ICUResourceBundle longDataBundle = null;
      if(!"latn".equals(numberingSystemName)) {
         ICUResourceBundle bundle = findWithFallback(r, numberingSystemName, CompactDecimalDataCache.UResFlags.NOT_ROOT);
         shortDataBundle = findWithFallback(bundle, "patternsShort/decimalFormat", CompactDecimalDataCache.UResFlags.NOT_ROOT);
         longDataBundle = findWithFallback(bundle, "patternsLong/decimalFormat", CompactDecimalDataCache.UResFlags.NOT_ROOT);
      }

      if(shortDataBundle == null) {
         ICUResourceBundle bundle = getWithFallback(r, "latn", CompactDecimalDataCache.UResFlags.ANY);
         shortDataBundle = getWithFallback(bundle, "patternsShort/decimalFormat", CompactDecimalDataCache.UResFlags.ANY);
         if(longDataBundle == null) {
            longDataBundle = findWithFallback(bundle, "patternsLong/decimalFormat", CompactDecimalDataCache.UResFlags.ANY);
            if(longDataBundle != null && isRoot(longDataBundle) && !isRoot(shortDataBundle)) {
               longDataBundle = null;
            }
         }
      }

      CompactDecimalDataCache.Data shortData = loadStyle(shortDataBundle, ulocale, "short");
      CompactDecimalDataCache.Data longData;
      if(longDataBundle == null) {
         longData = shortData;
      } else {
         longData = loadStyle(longDataBundle, ulocale, "long");
      }

      return new CompactDecimalDataCache.DataBundle(shortData, longData);
   }

   private static ICUResourceBundle findWithFallback(ICUResourceBundle r, String path, CompactDecimalDataCache.UResFlags flags) {
      if(r == null) {
         return null;
      } else {
         ICUResourceBundle result = r.findWithFallback(path);
         if(result == null) {
            return null;
         } else {
            switch(flags) {
            case NOT_ROOT:
               return isRoot(result)?null:result;
            case ANY:
               return result;
            default:
               throw new IllegalArgumentException();
            }
         }
      }
   }

   private static ICUResourceBundle getWithFallback(ICUResourceBundle r, String path, CompactDecimalDataCache.UResFlags flags) {
      ICUResourceBundle result = findWithFallback(r, path, flags);
      if(result == null) {
         throw new MissingResourceException("Cannot find " + path, ICUResourceBundle.class.getName(), path);
      } else {
         return result;
      }
   }

   private static boolean isRoot(ICUResourceBundle r) {
      ULocale bundleLocale = r.getULocale();
      return bundleLocale.equals(ULocale.ROOT) || bundleLocale.toString().equals("root");
   }

   private static CompactDecimalDataCache.Data loadStyle(ICUResourceBundle r, ULocale locale, String style) {
      int size = r.getSize();
      CompactDecimalDataCache.Data result = new CompactDecimalDataCache.Data(new long[15], new HashMap());

      for(int i = 0; i < size; ++i) {
         populateData(r.get(i), locale, style, result);
      }

      fillInMissing(result);
      return result;
   }

   private static void populateData(UResourceBundle divisorData, ULocale locale, String style, CompactDecimalDataCache.Data result) {
      long magnitude = Long.parseLong(divisorData.getKey());
      int thisIndex = (int)Math.log10((double)magnitude);
      if(thisIndex < 15) {
         int size = divisorData.getSize();
         int numZeros = 0;
         boolean otherVariantDefined = false;

         for(int i = 0; i < size; ++i) {
            UResourceBundle pluralVariantData = divisorData.get(i);
            String pluralVariant = pluralVariantData.getKey();
            String template = pluralVariantData.getString();
            if(pluralVariant.equals("other")) {
               otherVariantDefined = true;
            }

            int nz = populatePrefixSuffix(pluralVariant, thisIndex, template, locale, style, result);
            if(nz != numZeros) {
               if(numZeros != 0) {
                  throw new IllegalArgumentException("Plural variant \'" + pluralVariant + "\' template \'" + template + "\' for 10^" + thisIndex + " has wrong number of zeros in " + localeAndStyle(locale, style));
               }

               numZeros = nz;
            }
         }

         if(!otherVariantDefined) {
            throw new IllegalArgumentException("No \'other\' plural variant defined for 10^" + thisIndex + "in " + localeAndStyle(locale, style));
         } else {
            long divisor = magnitude;

            for(int i = 1; i < numZeros; ++i) {
               divisor /= 10L;
            }

            result.divisors[thisIndex] = divisor;
         }
      }
   }

   private static int populatePrefixSuffix(String pluralVariant, int idx, String template, ULocale locale, String style, CompactDecimalDataCache.Data result) {
      int firstIdx = template.indexOf("0");
      int lastIdx = template.lastIndexOf("0");
      if(firstIdx == -1) {
         throw new IllegalArgumentException("Expect at least one zero in template \'" + template + "\' for variant \'" + pluralVariant + "\' for 10^" + idx + " in " + localeAndStyle(locale, style));
      } else {
         String prefix = fixQuotes(template.substring(0, firstIdx));
         String suffix = fixQuotes(template.substring(lastIdx + 1));
         saveUnit(new DecimalFormat.Unit(prefix, suffix), pluralVariant, idx, result.units);
         if(prefix.trim().length() == 0 && suffix.trim().length() == 0) {
            return idx + 1;
         } else {
            int i;
            for(i = firstIdx + 1; i <= lastIdx && template.charAt(i) == 48; ++i) {
               ;
            }

            return i - firstIdx;
         }
      }
   }

   private static String fixQuotes(String prefixOrSuffix) {
      StringBuilder result = new StringBuilder();
      int len = prefixOrSuffix.length();
      CompactDecimalDataCache.QuoteState state = CompactDecimalDataCache.QuoteState.OUTSIDE;

      for(int idx = 0; idx < len; ++idx) {
         char ch = prefixOrSuffix.charAt(idx);
         if(ch == 39) {
            if(state == CompactDecimalDataCache.QuoteState.INSIDE_EMPTY) {
               result.append('\'');
            }
         } else {
            result.append(ch);
         }

         switch(state) {
         case OUTSIDE:
            state = ch == 39?CompactDecimalDataCache.QuoteState.INSIDE_EMPTY:CompactDecimalDataCache.QuoteState.OUTSIDE;
            break;
         case INSIDE_EMPTY:
         case INSIDE_FULL:
            state = ch == 39?CompactDecimalDataCache.QuoteState.OUTSIDE:CompactDecimalDataCache.QuoteState.INSIDE_FULL;
            break;
         default:
            throw new IllegalStateException();
         }
      }

      return result.toString();
   }

   private static String localeAndStyle(ULocale locale, String style) {
      return "locale \'" + locale + "\' style \'" + style + "\'";
   }

   private static void fillInMissing(CompactDecimalDataCache.Data result) {
      long lastDivisor = 1L;

      for(int i = 0; i < result.divisors.length; ++i) {
         if(((DecimalFormat.Unit[])result.units.get("other"))[i] == null) {
            result.divisors[i] = lastDivisor;
            copyFromPreviousIndex(i, result.units);
         } else {
            lastDivisor = result.divisors[i];
            propagateOtherToMissing(i, result.units);
         }
      }

   }

   private static void propagateOtherToMissing(int idx, Map units) {
      DecimalFormat.Unit otherVariantValue = ((DecimalFormat.Unit[])units.get("other"))[idx];

      for(DecimalFormat.Unit[] byBase : units.values()) {
         if(byBase[idx] == null) {
            byBase[idx] = otherVariantValue;
         }
      }

   }

   private static void copyFromPreviousIndex(int idx, Map units) {
      for(DecimalFormat.Unit[] byBase : units.values()) {
         if(idx == 0) {
            byBase[idx] = DecimalFormat.NULL_UNIT;
         } else {
            byBase[idx] = byBase[idx - 1];
         }
      }

   }

   private static void saveUnit(DecimalFormat.Unit unit, String pluralVariant, int idx, Map units) {
      DecimalFormat.Unit[] byBase = (DecimalFormat.Unit[])units.get(pluralVariant);
      if(byBase == null) {
         byBase = new DecimalFormat.Unit[15];
         units.put(pluralVariant, byBase);
      }

      byBase[idx] = unit;
   }

   static DecimalFormat.Unit getUnit(Map units, String variant, int base) {
      DecimalFormat.Unit[] byBase = (DecimalFormat.Unit[])units.get(variant);
      if(byBase == null) {
         byBase = (DecimalFormat.Unit[])units.get("other");
      }

      return byBase[base];
   }

   static class Data {
      long[] divisors;
      Map units;

      Data(long[] divisors, Map units) {
         this.divisors = divisors;
         this.units = units;
      }
   }

   static class DataBundle {
      CompactDecimalDataCache.Data shortData;
      CompactDecimalDataCache.Data longData;

      DataBundle(CompactDecimalDataCache.Data shortData, CompactDecimalDataCache.Data longData) {
         this.shortData = shortData;
         this.longData = longData;
      }
   }

   private static enum QuoteState {
      OUTSIDE,
      INSIDE_EMPTY,
      INSIDE_FULL;
   }

   private static enum UResFlags {
      ANY,
      NOT_ROOT;
   }
}
