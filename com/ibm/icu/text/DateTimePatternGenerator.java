package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.PatternTokenizer;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DateTimePatternGenerator implements Freezable, Cloneable {
   private static final boolean DEBUG = false;
   public static final int ERA = 0;
   public static final int YEAR = 1;
   public static final int QUARTER = 2;
   public static final int MONTH = 3;
   public static final int WEEK_OF_YEAR = 4;
   public static final int WEEK_OF_MONTH = 5;
   public static final int WEEKDAY = 6;
   public static final int DAY = 7;
   public static final int DAY_OF_YEAR = 8;
   public static final int DAY_OF_WEEK_IN_MONTH = 9;
   public static final int DAYPERIOD = 10;
   public static final int HOUR = 11;
   public static final int MINUTE = 12;
   public static final int SECOND = 13;
   public static final int FRACTIONAL_SECOND = 14;
   public static final int ZONE = 15;
   public static final int TYPE_LIMIT = 16;
   public static final int MATCH_NO_OPTIONS = 0;
   public static final int MATCH_HOUR_FIELD_LENGTH = 2048;
   /** @deprecated */
   public static final int MATCH_MINUTE_FIELD_LENGTH = 4096;
   /** @deprecated */
   public static final int MATCH_SECOND_FIELD_LENGTH = 8192;
   public static final int MATCH_ALL_FIELDS_LENGTH = 65535;
   private TreeMap skeleton2pattern = new TreeMap();
   private TreeMap basePattern_pattern = new TreeMap();
   private String decimal = "?";
   private String dateTimeFormat = "{1} {0}";
   private String[] appendItemFormats = new String[16];
   private String[] appendItemNames = new String[16];
   private char defaultHourFormatChar;
   private boolean frozen;
   private transient DateTimePatternGenerator.DateTimeMatcher current;
   private transient DateTimePatternGenerator.FormatParser fp;
   private transient DateTimePatternGenerator.DistanceInfo _distanceInfo;
   private static final int FRACTIONAL_MASK = 16384;
   private static final int SECOND_AND_FRACTIONAL_MASK = 24576;
   private static ICUCache DTPNG_CACHE = new SimpleCache();
   private static final String[] CLDR_FIELD_APPEND = new String[]{"Era", "Year", "Quarter", "Month", "Week", "*", "Day-Of-Week", "Day", "*", "*", "*", "Hour", "Minute", "Second", "*", "Timezone"};
   private static final String[] CLDR_FIELD_NAME = new String[]{"era", "year", "*", "month", "week", "*", "weekday", "day", "*", "*", "dayperiod", "hour", "minute", "second", "*", "zone"};
   private static final String[] FIELD_NAME = new String[]{"Era", "Year", "Quarter", "Month", "Week_in_Year", "Week_in_Month", "Weekday", "Day", "Day_Of_Year", "Day_of_Week_in_Month", "Dayperiod", "Hour", "Minute", "Second", "Fractional_Second", "Zone"};
   private static final String[] CANONICAL_ITEMS = new String[]{"G", "y", "Q", "M", "w", "W", "E", "d", "D", "F", "H", "m", "s", "S", "v"};
   private static final Set CANONICAL_SET = new HashSet(Arrays.asList(CANONICAL_ITEMS));
   private Set cldrAvailableFormatKeys;
   private static final int DATE_MASK = 1023;
   private static final int TIME_MASK = 64512;
   private static final int DELTA = 16;
   private static final int NUMERIC = 256;
   private static final int NONE = 0;
   private static final int NARROW = -257;
   private static final int SHORT = -258;
   private static final int LONG = -259;
   private static final int EXTRA_FIELD = 65536;
   private static final int MISSING_FIELD = 4096;
   private static final int[][] types = new int[][]{{71, 0, -258, 1, 3}, {71, 0, -259, 4}, {121, 1, 256, 1, 20}, {89, 1, 272, 1, 20}, {117, 1, 288, 1, 20}, {85, 1, -258, 1, 3}, {85, 1, -259, 4}, {85, 1, -257, 5}, {81, 2, 256, 1, 2}, {81, 2, -258, 3}, {81, 2, -259, 4}, {113, 2, 272, 1, 2}, {113, 2, -242, 3}, {113, 2, -243, 4}, {77, 3, 256, 1, 2}, {77, 3, -258, 3}, {77, 3, -259, 4}, {77, 3, -257, 5}, {76, 3, 272, 1, 2}, {76, 3, -274, 3}, {76, 3, -275, 4}, {76, 3, -273, 5}, {108, 3, 272, 1, 1}, {119, 4, 256, 1, 2}, {87, 5, 272, 1}, {69, 6, -258, 1, 3}, {69, 6, -259, 4}, {69, 6, -257, 5}, {99, 6, 288, 1, 2}, {99, 6, -290, 3}, {99, 6, -291, 4}, {99, 6, -289, 5}, {101, 6, 272, 1, 2}, {101, 6, -274, 3}, {101, 6, -275, 4}, {101, 6, -273, 5}, {100, 7, 256, 1, 2}, {68, 8, 272, 1, 3}, {70, 9, 288, 1}, {103, 7, 304, 1, 20}, {97, 10, -258, 1}, {72, 11, 416, 1, 2}, {107, 11, 432, 1, 2}, {104, 11, 256, 1, 2}, {75, 11, 272, 1, 2}, {109, 12, 256, 1, 2}, {115, 13, 256, 1, 2}, {83, 14, 272, 1, 1000}, {65, 13, 288, 1, 1000}, {118, 15, -290, 1}, {118, 15, -291, 4}, {122, 15, -258, 1, 3}, {122, 15, -259, 4}, {90, 15, -274, 1, 3}, {90, 15, -275, 4}, {86, 15, -274, 1, 3}, {86, 15, -275, 4}};

   public static DateTimePatternGenerator getEmptyInstance() {
      return new DateTimePatternGenerator();
   }

   protected DateTimePatternGenerator() {
      for(int i = 0; i < 16; ++i) {
         this.appendItemFormats[i] = "{0} ├{2}: {1}┤";
         this.appendItemNames[i] = "F" + i;
      }

      this.defaultHourFormatChar = 72;
      this.frozen = false;
      this.current = new DateTimePatternGenerator.DateTimeMatcher();
      this.fp = new DateTimePatternGenerator.FormatParser();
      this._distanceInfo = new DateTimePatternGenerator.DistanceInfo();
      this.complete();
      this.cldrAvailableFormatKeys = new HashSet(20);
   }

   public static DateTimePatternGenerator getInstance() {
      return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public static DateTimePatternGenerator getInstance(ULocale uLocale) {
      return getFrozenInstance(uLocale).cloneAsThawed();
   }

   /** @deprecated */
   public static DateTimePatternGenerator getFrozenInstance(ULocale uLocale) {
      String localeKey = uLocale.toString();
      DateTimePatternGenerator result = (DateTimePatternGenerator)DTPNG_CACHE.get(localeKey);
      if(result != null) {
         return result;
      } else {
         result = new DateTimePatternGenerator();
         DateTimePatternGenerator.PatternInfo returnInfo = new DateTimePatternGenerator.PatternInfo();
         String shortTimePattern = null;

         for(int i = 0; i <= 3; ++i) {
            SimpleDateFormat df = (SimpleDateFormat)DateFormat.getDateInstance(i, uLocale);
            result.addPattern(df.toPattern(), false, returnInfo);
            df = (SimpleDateFormat)DateFormat.getTimeInstance(i, uLocale);
            result.addPattern(df.toPattern(), false, returnInfo);
            if(i == 3) {
               shortTimePattern = df.toPattern();
               DateTimePatternGenerator.FormatParser fp = new DateTimePatternGenerator.FormatParser();
               fp.set(shortTimePattern);
               List<Object> items = fp.getItems();

               for(int idx = 0; idx < items.size(); ++idx) {
                  Object item = items.get(idx);
                  if(item instanceof DateTimePatternGenerator.VariableField) {
                     DateTimePatternGenerator.VariableField fld = (DateTimePatternGenerator.VariableField)item;
                     if(fld.getType() == 11) {
                        result.defaultHourFormatChar = fld.toString().charAt(0);
                        break;
                     }
                  }
               }
            }
         }

         ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", uLocale);
         String calendarTypeToUse = uLocale.getKeywordValue("calendar");
         if(calendarTypeToUse == null) {
            String[] preferredCalendarTypes = Calendar.getKeywordValuesForLocale("calendar", uLocale, true);
            calendarTypeToUse = preferredCalendarTypes[0];
         }

         if(calendarTypeToUse == null) {
            calendarTypeToUse = "gregorian";
         }

         try {
            ICUResourceBundle itemBundle = rb.getWithFallback("calendar/" + calendarTypeToUse + "/appendItems");

            for(int i = 0; i < itemBundle.getSize(); ++i) {
               ICUResourceBundle formatBundle = (ICUResourceBundle)itemBundle.get(i);
               String formatName = itemBundle.get(i).getKey();
               String value = formatBundle.getString();
               result.setAppendItemFormat(getAppendFormatNumber(formatName), value);
            }
         } catch (MissingResourceException var15) {
            ;
         }

         try {
            ICUResourceBundle itemBundle = rb.getWithFallback("fields");

            for(int i = 0; i < 16; ++i) {
               if(isCLDRFieldName(i)) {
                  ICUResourceBundle fieldBundle = itemBundle.getWithFallback(CLDR_FIELD_NAME[i]);
                  ICUResourceBundle dnBundle = fieldBundle.getWithFallback("dn");
                  String value = dnBundle.getString();
                  result.setAppendItemName(i, value);
               }
            }
         } catch (MissingResourceException var14) {
            ;
         }

         ICUResourceBundle availFormatsBundle = null;

         try {
            availFormatsBundle = rb.getWithFallback("calendar/" + calendarTypeToUse + "/availableFormats");
         } catch (MissingResourceException var13) {
            ;
         }

         boolean override = true;

         while(availFormatsBundle != null) {
            for(int i = 0; i < availFormatsBundle.getSize(); ++i) {
               String formatKey = availFormatsBundle.get(i).getKey();
               if(!result.isAvailableFormatSet(formatKey)) {
                  result.setAvailableFormat(formatKey);
                  String formatValue = availFormatsBundle.get(i).getString();
                  result.addPatternWithSkeleton(formatValue, formatKey, override, returnInfo);
               }
            }

            ICUResourceBundle pbundle = (ICUResourceBundle)availFormatsBundle.getParent();
            if(pbundle == null) {
               break;
            }

            try {
               availFormatsBundle = pbundle.getWithFallback("calendar/" + calendarTypeToUse + "/availableFormats");
            } catch (MissingResourceException var12) {
               availFormatsBundle = null;
            }

            if(availFormatsBundle != null && pbundle.getULocale().getBaseName().equals("root")) {
               override = false;
            }
         }

         if(shortTimePattern != null) {
            hackTimes(result, returnInfo, shortTimePattern);
         }

         result.setDateTimeFormat(Calendar.getDateTimePattern(Calendar.getInstance(uLocale), uLocale, 2));
         DecimalFormatSymbols dfs = new DecimalFormatSymbols(uLocale);
         result.setDecimal(String.valueOf(dfs.getDecimalSeparator()));
         result.freeze();
         DTPNG_CACHE.put(localeKey, result);
         return result;
      }
   }

   /** @deprecated */
   public char getDefaultHourFormatChar() {
      return this.defaultHourFormatChar;
   }

   /** @deprecated */
   public void setDefaultHourFormatChar(char defaultHourFormatChar) {
      this.defaultHourFormatChar = defaultHourFormatChar;
   }

   private static void hackTimes(DateTimePatternGenerator result, DateTimePatternGenerator.PatternInfo returnInfo, String hackPattern) {
      result.fp.set(hackPattern);
      StringBuilder mmss = new StringBuilder();
      boolean gotMm = false;

      for(int i = 0; i < result.fp.items.size(); ++i) {
         Object item = result.fp.items.get(i);
         if(item instanceof String) {
            if(gotMm) {
               mmss.append(result.fp.quoteLiteral(item.toString()));
            }
         } else {
            char ch = item.toString().charAt(0);
            if(ch == 109) {
               gotMm = true;
               mmss.append(item);
            } else {
               if(ch == 115) {
                  if(gotMm) {
                     mmss.append(item);
                     result.addPattern(mmss.toString(), false, returnInfo);
                  }
                  break;
               }

               if(gotMm || ch == 122 || ch == 90 || ch == 118 || ch == 86) {
                  break;
               }
            }
         }
      }

      BitSet variables = new BitSet();
      BitSet nuke = new BitSet();

      for(int i = 0; i < result.fp.items.size(); ++i) {
         Object item = result.fp.items.get(i);
         if(item instanceof DateTimePatternGenerator.VariableField) {
            variables.set(i);
            char ch = item.toString().charAt(0);
            if(ch == 115 || ch == 83) {
               nuke.set(i);

               for(int j = i - 1; j >= 0 && !variables.get(j); ++j) {
                  nuke.set(i);
               }
            }
         }
      }

      String hhmm = getFilteredPattern(result.fp, nuke);
      result.addPattern(hhmm, false, returnInfo);
   }

   private static String getFilteredPattern(DateTimePatternGenerator.FormatParser fp, BitSet nuke) {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < fp.items.size(); ++i) {
         if(!nuke.get(i)) {
            Object item = fp.items.get(i);
            if(item instanceof String) {
               result.append(fp.quoteLiteral(item.toString()));
            } else {
               result.append(item.toString());
            }
         }
      }

      return result.toString();
   }

   /** @deprecated */
   public static int getAppendFormatNumber(String string) {
      for(int i = 0; i < CLDR_FIELD_APPEND.length; ++i) {
         if(CLDR_FIELD_APPEND[i].equals(string)) {
            return i;
         }
      }

      return -1;
   }

   private static boolean isCLDRFieldName(int index) {
      return index < 0 && index >= 16?false:CLDR_FIELD_NAME[index].charAt(0) != 42;
   }

   public String getBestPattern(String skeleton) {
      return this.getBestPattern(skeleton, (DateTimePatternGenerator.DateTimeMatcher)null, 0);
   }

   public String getBestPattern(String skeleton, int options) {
      return this.getBestPattern(skeleton, (DateTimePatternGenerator.DateTimeMatcher)null, options);
   }

   private String getBestPattern(String skeleton, DateTimePatternGenerator.DateTimeMatcher skipMatcher, int options) {
      skeleton = skeleton.replaceAll("j", String.valueOf(this.defaultHourFormatChar));
      String datePattern;
      String timePattern;
      synchronized(this) {
         this.current.set(skeleton, this.fp, false);
         DateTimePatternGenerator.PatternWithMatcher bestWithMatcher = this.getBestRaw(this.current, -1, this._distanceInfo, skipMatcher);
         if(this._distanceInfo.missingFieldMask == 0 && this._distanceInfo.extraFieldMask == 0) {
            return this.adjustFieldTypes(bestWithMatcher, this.current, false, options);
         }

         int neededFields = this.current.getFieldMask();
         datePattern = this.getBestAppending(this.current, neededFields & 1023, this._distanceInfo, skipMatcher, options);
         timePattern = this.getBestAppending(this.current, neededFields & 'ﰀ', this._distanceInfo, skipMatcher, options);
      }

      return datePattern == null?(timePattern == null?"":timePattern):(timePattern == null?datePattern:MessageFormat.format(this.getDateTimeFormat(), new Object[]{timePattern, datePattern}));
   }

   public DateTimePatternGenerator addPattern(String pattern, boolean override, DateTimePatternGenerator.PatternInfo returnInfo) {
      return this.addPatternWithSkeleton(pattern, (String)null, override, returnInfo);
   }

   /** @deprecated */
   public DateTimePatternGenerator addPatternWithSkeleton(String pattern, String skeletonToUse, boolean override, DateTimePatternGenerator.PatternInfo returnInfo) {
      this.checkFrozen();
      DateTimePatternGenerator.DateTimeMatcher matcher;
      if(skeletonToUse == null) {
         matcher = (new DateTimePatternGenerator.DateTimeMatcher()).set(pattern, this.fp, false);
      } else {
         matcher = (new DateTimePatternGenerator.DateTimeMatcher()).set(skeletonToUse, this.fp, false);
      }

      String basePattern = matcher.getBasePattern();
      DateTimePatternGenerator.PatternWithSkeletonFlag previousPatternWithSameBase = (DateTimePatternGenerator.PatternWithSkeletonFlag)this.basePattern_pattern.get(basePattern);
      if(previousPatternWithSameBase != null && (!previousPatternWithSameBase.skeletonWasSpecified || skeletonToUse != null && !override)) {
         returnInfo.status = 1;
         returnInfo.conflictingPattern = previousPatternWithSameBase.pattern;
         if(!override) {
            return this;
         }
      }

      DateTimePatternGenerator.PatternWithSkeletonFlag previousValue = (DateTimePatternGenerator.PatternWithSkeletonFlag)this.skeleton2pattern.get(matcher);
      if(previousValue != null) {
         returnInfo.status = 2;
         returnInfo.conflictingPattern = previousValue.pattern;
         if(!override || skeletonToUse != null && previousValue.skeletonWasSpecified) {
            return this;
         }
      }

      returnInfo.status = 0;
      returnInfo.conflictingPattern = "";
      DateTimePatternGenerator.PatternWithSkeletonFlag patWithSkelFlag = new DateTimePatternGenerator.PatternWithSkeletonFlag(pattern, skeletonToUse != null);
      this.skeleton2pattern.put(matcher, patWithSkelFlag);
      this.basePattern_pattern.put(basePattern, patWithSkelFlag);
      return this;
   }

   public String getSkeleton(String pattern) {
      synchronized(this) {
         this.current.set(pattern, this.fp, false);
         return this.current.toString();
      }
   }

   /** @deprecated */
   public String getSkeletonAllowingDuplicates(String pattern) {
      synchronized(this) {
         this.current.set(pattern, this.fp, true);
         return this.current.toString();
      }
   }

   /** @deprecated */
   public String getCanonicalSkeletonAllowingDuplicates(String pattern) {
      synchronized(this) {
         this.current.set(pattern, this.fp, true);
         return this.current.toCanonicalString();
      }
   }

   public String getBaseSkeleton(String pattern) {
      synchronized(this) {
         this.current.set(pattern, this.fp, false);
         return this.current.getBasePattern();
      }
   }

   public Map getSkeletons(Map result) {
      if(result == null) {
         result = new LinkedHashMap();
      }

      for(DateTimePatternGenerator.DateTimeMatcher item : this.skeleton2pattern.keySet()) {
         DateTimePatternGenerator.PatternWithSkeletonFlag patternWithSkelFlag = (DateTimePatternGenerator.PatternWithSkeletonFlag)this.skeleton2pattern.get(item);
         String pattern = patternWithSkelFlag.pattern;
         if(!CANONICAL_SET.contains(pattern)) {
            ((Map)result).put(item.toString(), pattern);
         }
      }

      return (Map)result;
   }

   public Set getBaseSkeletons(Set result) {
      if(result == null) {
         result = new HashSet();
      }

      ((Set)result).addAll(this.basePattern_pattern.keySet());
      return (Set)result;
   }

   public String replaceFieldTypes(String pattern, String skeleton) {
      return this.replaceFieldTypes(pattern, skeleton, 0);
   }

   public String replaceFieldTypes(String pattern, String skeleton, int options) {
      synchronized(this) {
         DateTimePatternGenerator.PatternWithMatcher patternNoMatcher = new DateTimePatternGenerator.PatternWithMatcher(pattern, (DateTimePatternGenerator.DateTimeMatcher)null);
         return this.adjustFieldTypes(patternNoMatcher, this.current.set(skeleton, this.fp, false), false, options);
      }
   }

   public void setDateTimeFormat(String dateTimeFormat) {
      this.checkFrozen();
      this.dateTimeFormat = dateTimeFormat;
   }

   public String getDateTimeFormat() {
      return this.dateTimeFormat;
   }

   public void setDecimal(String decimal) {
      this.checkFrozen();
      this.decimal = decimal;
   }

   public String getDecimal() {
      return this.decimal;
   }

   /** @deprecated */
   public Collection getRedundants(Collection output) {
      synchronized(this) {
         if(output == null) {
            output = new LinkedHashSet();
         }

         for(DateTimePatternGenerator.DateTimeMatcher cur : this.skeleton2pattern.keySet()) {
            DateTimePatternGenerator.PatternWithSkeletonFlag patternWithSkelFlag = (DateTimePatternGenerator.PatternWithSkeletonFlag)this.skeleton2pattern.get(cur);
            String pattern = patternWithSkelFlag.pattern;
            if(!CANONICAL_SET.contains(pattern)) {
               String trial = this.getBestPattern(cur.toString(), cur, 0);
               if(trial.equals(pattern)) {
                  ((Collection)output).add(pattern);
               }
            }
         }

         return (Collection)output;
      }
   }

   public void setAppendItemFormat(int field, String value) {
      this.checkFrozen();
      this.appendItemFormats[field] = value;
   }

   public String getAppendItemFormat(int field) {
      return this.appendItemFormats[field];
   }

   public void setAppendItemName(int field, String value) {
      this.checkFrozen();
      this.appendItemNames[field] = value;
   }

   public String getAppendItemName(int field) {
      return this.appendItemNames[field];
   }

   /** @deprecated */
   public static boolean isSingleField(String skeleton) {
      char first = skeleton.charAt(0);

      for(int i = 1; i < skeleton.length(); ++i) {
         if(skeleton.charAt(i) != first) {
            return false;
         }
      }

      return true;
   }

   private void setAvailableFormat(String key) {
      this.checkFrozen();
      this.cldrAvailableFormatKeys.add(key);
   }

   private boolean isAvailableFormatSet(String key) {
      return this.cldrAvailableFormatKeys.contains(key);
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public DateTimePatternGenerator freeze() {
      this.frozen = true;
      return this;
   }

   public DateTimePatternGenerator cloneAsThawed() {
      DateTimePatternGenerator result = (DateTimePatternGenerator)((DateTimePatternGenerator)this.clone());
      this.frozen = false;
      return result;
   }

   public Object clone() {
      try {
         DateTimePatternGenerator result = (DateTimePatternGenerator)((DateTimePatternGenerator)super.clone());
         result.skeleton2pattern = (TreeMap)this.skeleton2pattern.clone();
         result.basePattern_pattern = (TreeMap)this.basePattern_pattern.clone();
         result.appendItemFormats = (String[])this.appendItemFormats.clone();
         result.appendItemNames = (String[])this.appendItemNames.clone();
         result.current = new DateTimePatternGenerator.DateTimeMatcher();
         result.fp = new DateTimePatternGenerator.FormatParser();
         result._distanceInfo = new DateTimePatternGenerator.DistanceInfo();
         result.frozen = false;
         return result;
      } catch (CloneNotSupportedException var2) {
         throw new IllegalArgumentException("Internal Error");
      }
   }

   /** @deprecated */
   public boolean skeletonsAreSimilar(String id, String skeleton) {
      if(id.equals(skeleton)) {
         return true;
      } else {
         TreeSet<String> parser1 = this.getSet(id);
         TreeSet<String> parser2 = this.getSet(skeleton);
         if(parser1.size() != parser2.size()) {
            return false;
         } else {
            Iterator<String> it2 = parser2.iterator();

            for(String item : parser1) {
               int index1 = getCanonicalIndex(item, false);
               String item2 = (String)it2.next();
               int index2 = getCanonicalIndex(item2, false);
               if(types[index1][1] != types[index2][1]) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   private TreeSet getSet(String id) {
      List<Object> items = this.fp.set(id).getItems();
      TreeSet<String> result = new TreeSet();

      for(Object obj : items) {
         String item = obj.toString();
         if(!item.startsWith("G") && !item.startsWith("a")) {
            result.add(item);
         }
      }

      return result;
   }

   private void checkFrozen() {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify frozen object");
      }
   }

   private String getBestAppending(DateTimePatternGenerator.DateTimeMatcher source, int missingFields, DateTimePatternGenerator.DistanceInfo distInfo, DateTimePatternGenerator.DateTimeMatcher skipMatcher, int options) {
      String resultPattern = null;
      if(missingFields != 0) {
         DateTimePatternGenerator.PatternWithMatcher resultPatternWithMatcher = this.getBestRaw(source, missingFields, distInfo, skipMatcher);
         resultPattern = this.adjustFieldTypes(resultPatternWithMatcher, source, false, options);

         while(distInfo.missingFieldMask != 0) {
            if((distInfo.missingFieldMask & 24576) == 16384 && (missingFields & 24576) == 24576) {
               resultPatternWithMatcher.pattern = resultPattern;
               resultPattern = this.adjustFieldTypes(resultPatternWithMatcher, source, true, options);
               distInfo.missingFieldMask &= -16385;
            } else {
               int startingMask = distInfo.missingFieldMask;
               DateTimePatternGenerator.PatternWithMatcher tempWithMatcher = this.getBestRaw(source, distInfo.missingFieldMask, distInfo, skipMatcher);
               String temp = this.adjustFieldTypes(tempWithMatcher, source, false, options);
               int foundMask = startingMask & ~distInfo.missingFieldMask;
               int topField = this.getTopBitNumber(foundMask);
               resultPattern = MessageFormat.format(this.getAppendFormat(topField), new Object[]{resultPattern, temp, this.getAppendName(topField)});
            }
         }
      }

      return resultPattern;
   }

   private String getAppendName(int foundMask) {
      return "\'" + this.appendItemNames[foundMask] + "\'";
   }

   private String getAppendFormat(int foundMask) {
      return this.appendItemFormats[foundMask];
   }

   private int getTopBitNumber(int foundMask) {
      int i;
      for(i = 0; foundMask != 0; ++i) {
         foundMask >>>= 1;
      }

      return i - 1;
   }

   private void complete() {
      DateTimePatternGenerator.PatternInfo patternInfo = new DateTimePatternGenerator.PatternInfo();

      for(int i = 0; i < CANONICAL_ITEMS.length; ++i) {
         this.addPattern(String.valueOf(CANONICAL_ITEMS[i]), false, patternInfo);
      }

   }

   private DateTimePatternGenerator.PatternWithMatcher getBestRaw(DateTimePatternGenerator.DateTimeMatcher source, int includeMask, DateTimePatternGenerator.DistanceInfo missingFields, DateTimePatternGenerator.DateTimeMatcher skipMatcher) {
      int bestDistance = Integer.MAX_VALUE;
      DateTimePatternGenerator.PatternWithMatcher bestPatternWithMatcher = new DateTimePatternGenerator.PatternWithMatcher("", (DateTimePatternGenerator.DateTimeMatcher)null);
      DateTimePatternGenerator.DistanceInfo tempInfo = new DateTimePatternGenerator.DistanceInfo();

      for(DateTimePatternGenerator.DateTimeMatcher trial : this.skeleton2pattern.keySet()) {
         if(!trial.equals(skipMatcher)) {
            int distance = source.getDistance(trial, includeMask, tempInfo);
            if(distance < bestDistance) {
               bestDistance = distance;
               DateTimePatternGenerator.PatternWithSkeletonFlag patternWithSkelFlag = (DateTimePatternGenerator.PatternWithSkeletonFlag)this.skeleton2pattern.get(trial);
               bestPatternWithMatcher.pattern = patternWithSkelFlag.pattern;
               if(patternWithSkelFlag.skeletonWasSpecified) {
                  bestPatternWithMatcher.matcherWithSkeleton = trial;
               } else {
                  bestPatternWithMatcher.matcherWithSkeleton = null;
               }

               missingFields.setTo(tempInfo);
               if(distance == 0) {
                  break;
               }
            }
         }
      }

      return bestPatternWithMatcher;
   }

   private String adjustFieldTypes(DateTimePatternGenerator.PatternWithMatcher patternWithMatcher, DateTimePatternGenerator.DateTimeMatcher inputRequest, boolean fixFractionalSeconds, int options) {
      this.fp.set(patternWithMatcher.pattern);
      StringBuilder newPattern = new StringBuilder();

      for(Object item : this.fp.getItems()) {
         if(item instanceof String) {
            newPattern.append(this.fp.quoteLiteral((String)item));
         } else {
            DateTimePatternGenerator.VariableField variableField = (DateTimePatternGenerator.VariableField)item;
            StringBuilder fieldBuilder = new StringBuilder(variableField.toString());
            int type = variableField.getType();
            if(fixFractionalSeconds && type == 13) {
               String newField = inputRequest.original[14];
               fieldBuilder.append(this.decimal);
               fieldBuilder.append(newField);
            } else if(inputRequest.type[type] != 0) {
               String reqField = inputRequest.original[type];
               int reqFieldLen = reqField.length();
               if(reqField.charAt(0) == 69 && reqFieldLen < 3) {
                  reqFieldLen = 3;
               }

               int adjFieldLen = reqFieldLen;
               DateTimePatternGenerator.DateTimeMatcher matcherWithSkeleton = patternWithMatcher.matcherWithSkeleton;
               if((type != 11 || (options & 2048) != 0) && (type != 12 || (options & 4096) != 0) && (type != 13 || (options & 8192) != 0)) {
                  if(matcherWithSkeleton != null) {
                     String skelField = matcherWithSkeleton.origStringForField(type);
                     int skelFieldLen = skelField.length();
                     boolean patFieldIsNumeric = variableField.isNumeric();
                     boolean skelFieldIsNumeric = matcherWithSkeleton.fieldIsNumeric(type);
                     if(skelFieldLen == reqFieldLen || patFieldIsNumeric && !skelFieldIsNumeric || skelFieldIsNumeric && !patFieldIsNumeric) {
                        adjFieldLen = fieldBuilder.length();
                     }
                  }
               } else {
                  adjFieldLen = fieldBuilder.length();
               }

               char c = type != 11 && type != 3 && type != 6 && type != 1?reqField.charAt(0):fieldBuilder.charAt(0);
               fieldBuilder = new StringBuilder();

               for(int i = adjFieldLen; i > 0; --i) {
                  fieldBuilder.append(c);
               }
            }

            newPattern.append(fieldBuilder);
         }
      }

      return newPattern.toString();
   }

   /** @deprecated */
   public String getFields(String pattern) {
      this.fp.set(pattern);
      StringBuilder newPattern = new StringBuilder();

      for(Object item : this.fp.getItems()) {
         if(item instanceof String) {
            newPattern.append(this.fp.quoteLiteral((String)item));
         } else {
            newPattern.append("{" + getName(item.toString()) + "}");
         }
      }

      return newPattern.toString();
   }

   private static String showMask(int mask) {
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < 16; ++i) {
         if((mask & 1 << i) != 0) {
            if(result.length() != 0) {
               result.append(" | ");
            }

            result.append(FIELD_NAME[i]);
            result.append(" ");
         }
      }

      return result.toString();
   }

   private static String getName(String s) {
      int i = getCanonicalIndex(s, true);
      String name = FIELD_NAME[types[i][1]];
      int subtype = types[i][2];
      boolean string = subtype < 0;
      if(string) {
         subtype = -subtype;
      }

      if(subtype < 0) {
         name = name + ":S";
      } else {
         name = name + ":N";
      }

      return name;
   }

   private static int getCanonicalIndex(String s, boolean strict) {
      int len = s.length();
      if(len == 0) {
         return -1;
      } else {
         int ch = s.charAt(0);

         for(int i = 1; i < len; ++i) {
            if(s.charAt(i) != ch) {
               return -1;
            }
         }

         int bestRow = -1;

         for(int i = 0; i < types.length; ++i) {
            int[] row = types[i];
            if(row[0] == ch) {
               bestRow = i;
               if(row[3] <= len && row[row.length - 1] >= len) {
                  return i;
               }
            }
         }

         return strict?-1:bestRow;
      }
   }

   private static class DateTimeMatcher implements Comparable {
      private int[] type;
      private String[] original;
      private String[] baseOriginal;

      private DateTimeMatcher() {
         this.type = new int[16];
         this.original = new String[16];
         this.baseOriginal = new String[16];
      }

      public String origStringForField(int field) {
         return this.original[field];
      }

      public boolean fieldIsNumeric(int field) {
         return this.type[field] > 0;
      }

      public String toString() {
         StringBuilder result = new StringBuilder();

         for(int i = 0; i < 16; ++i) {
            if(this.original[i].length() != 0) {
               result.append(this.original[i]);
            }
         }

         return result.toString();
      }

      public String toCanonicalString() {
         StringBuilder result = new StringBuilder();

         for(int i = 0; i < 16; ++i) {
            if(this.original[i].length() != 0) {
               for(int j = 0; j < DateTimePatternGenerator.types.length; ++j) {
                  int[] row = DateTimePatternGenerator.types[j];
                  if(row[1] == i) {
                     char originalChar = this.original[i].charAt(0);
                     char repeatChar = originalChar != 104 && originalChar != 75?(char)row[0]:104;
                     result.append(Utility.repeat(String.valueOf(repeatChar), this.original[i].length()));
                     break;
                  }
               }
            }
         }

         return result.toString();
      }

      String getBasePattern() {
         StringBuilder result = new StringBuilder();

         for(int i = 0; i < 16; ++i) {
            if(this.baseOriginal[i].length() != 0) {
               result.append(this.baseOriginal[i]);
            }
         }

         return result.toString();
      }

      DateTimePatternGenerator.DateTimeMatcher set(String pattern, DateTimePatternGenerator.FormatParser fp, boolean allowDuplicateFields) {
         for(int i = 0; i < 16; ++i) {
            this.type[i] = 0;
            this.original[i] = "";
            this.baseOriginal[i] = "";
         }

         fp.set(pattern);

         for(Object obj : fp.getItems()) {
            if(obj instanceof DateTimePatternGenerator.VariableField) {
               DateTimePatternGenerator.VariableField item = (DateTimePatternGenerator.VariableField)obj;
               String field = item.toString();
               if(field.charAt(0) != 97) {
                  int canonicalIndex = item.getCanonicalIndex();
                  int[] row = DateTimePatternGenerator.types[canonicalIndex];
                  int typeValue = row[1];
                  if(this.original[typeValue].length() != 0) {
                     if(!allowDuplicateFields) {
                        throw new IllegalArgumentException("Conflicting fields:\t" + this.original[typeValue] + ", " + field + "\t in " + pattern);
                     }
                  } else {
                     this.original[typeValue] = field;
                     char repeatChar = (char)row[0];
                     int repeatCount = row[3];
                     if("GEzvQ".indexOf(repeatChar) >= 0) {
                        repeatCount = 1;
                     }

                     this.baseOriginal[typeValue] = Utility.repeat(String.valueOf(repeatChar), repeatCount);
                     int subTypeValue = row[2];
                     if(subTypeValue > 0) {
                        subTypeValue += field.length();
                     }

                     this.type[typeValue] = subTypeValue;
                  }
               }
            }
         }

         return this;
      }

      int getFieldMask() {
         int result = 0;

         for(int i = 0; i < this.type.length; ++i) {
            if(this.type[i] != 0) {
               result |= 1 << i;
            }
         }

         return result;
      }

      void extractFrom(DateTimePatternGenerator.DateTimeMatcher source, int fieldMask) {
         for(int i = 0; i < this.type.length; ++i) {
            if((fieldMask & 1 << i) != 0) {
               this.type[i] = source.type[i];
               this.original[i] = source.original[i];
            } else {
               this.type[i] = 0;
               this.original[i] = "";
            }
         }

      }

      int getDistance(DateTimePatternGenerator.DateTimeMatcher other, int includeMask, DateTimePatternGenerator.DistanceInfo distanceInfo) {
         int result = 0;
         distanceInfo.clear();

         for(int i = 0; i < this.type.length; ++i) {
            int myType = (includeMask & 1 << i) == 0?0:this.type[i];
            int otherType = other.type[i];
            if(myType != otherType) {
               if(myType == 0) {
                  result += 65536;
                  distanceInfo.addExtra(i);
               } else if(otherType == 0) {
                  result += 4096;
                  distanceInfo.addMissing(i);
               } else {
                  result += Math.abs(myType - otherType);
               }
            }
         }

         return result;
      }

      public int compareTo(DateTimePatternGenerator.DateTimeMatcher that) {
         for(int i = 0; i < this.original.length; ++i) {
            int comp = this.original[i].compareTo(that.original[i]);
            if(comp != 0) {
               return -comp;
            }
         }

         return 0;
      }

      public boolean equals(Object other) {
         if(!(other instanceof DateTimePatternGenerator.DateTimeMatcher)) {
            return false;
         } else {
            DateTimePatternGenerator.DateTimeMatcher that = (DateTimePatternGenerator.DateTimeMatcher)other;

            for(int i = 0; i < this.original.length; ++i) {
               if(!this.original[i].equals(that.original[i])) {
                  return false;
               }
            }

            return true;
         }
      }

      public int hashCode() {
         int result = 0;

         for(int i = 0; i < this.original.length; ++i) {
            result ^= this.original[i].hashCode();
         }

         return result;
      }
   }

   private static class DistanceInfo {
      int missingFieldMask;
      int extraFieldMask;

      private DistanceInfo() {
      }

      void clear() {
         this.missingFieldMask = this.extraFieldMask = 0;
      }

      void setTo(DateTimePatternGenerator.DistanceInfo other) {
         this.missingFieldMask = other.missingFieldMask;
         this.extraFieldMask = other.extraFieldMask;
      }

      void addMissing(int field) {
         this.missingFieldMask |= 1 << field;
      }

      void addExtra(int field) {
         this.extraFieldMask |= 1 << field;
      }

      public String toString() {
         return "missingFieldMask: " + DateTimePatternGenerator.showMask(this.missingFieldMask) + ", extraFieldMask: " + DateTimePatternGenerator.showMask(this.extraFieldMask);
      }
   }

   /** @deprecated */
   public static class FormatParser {
      private transient PatternTokenizer tokenizer = (new PatternTokenizer()).setSyntaxCharacters(new UnicodeSet("[a-zA-Z]")).setExtraQuotingCharacters(new UnicodeSet("[[[:script=Latn:][:script=Cyrl:]]&[[:L:][:M:]]]")).setUsingQuote(true);
      private List items = new ArrayList();

      /** @deprecated */
      public final DateTimePatternGenerator.FormatParser set(String string) {
         return this.set(string, false);
      }

      /** @deprecated */
      public DateTimePatternGenerator.FormatParser set(String string, boolean strict) {
         this.items.clear();
         if(string.length() == 0) {
            return this;
         } else {
            this.tokenizer.setPattern(string);
            StringBuffer buffer = new StringBuffer();
            StringBuffer variable = new StringBuffer();

            while(true) {
               buffer.setLength(0);
               int status = this.tokenizer.next(buffer);
               if(status == 0) {
                  this.addVariable(variable, false);
                  return this;
               }

               if(status == 1) {
                  if(variable.length() != 0 && buffer.charAt(0) != variable.charAt(0)) {
                     this.addVariable(variable, false);
                  }

                  variable.append(buffer);
               } else {
                  this.addVariable(variable, false);
                  this.items.add(buffer.toString());
               }
            }
         }
      }

      private void addVariable(StringBuffer variable, boolean strict) {
         if(variable.length() != 0) {
            this.items.add(new DateTimePatternGenerator.VariableField(variable.toString(), strict));
            variable.setLength(0);
         }

      }

      /** @deprecated */
      public List getItems() {
         return this.items;
      }

      /** @deprecated */
      public String toString() {
         return this.toString(0, this.items.size());
      }

      /** @deprecated */
      public String toString(int start, int limit) {
         StringBuilder result = new StringBuilder();

         for(int i = start; i < limit; ++i) {
            Object item = this.items.get(i);
            if(item instanceof String) {
               String itemString = (String)item;
               result.append(this.tokenizer.quoteLiteral(itemString));
            } else {
               result.append(this.items.get(i).toString());
            }
         }

         return result.toString();
      }

      /** @deprecated */
      public boolean hasDateAndTimeFields() {
         int foundMask = 0;

         for(Object item : this.items) {
            if(item instanceof DateTimePatternGenerator.VariableField) {
               int type = ((DateTimePatternGenerator.VariableField)item).getType();
               foundMask |= 1 << type;
            }
         }

         boolean isDate = (foundMask & 1023) != 0;
         boolean isTime = (foundMask & 'ﰀ') != 0;
         return isDate && isTime;
      }

      /** @deprecated */
      public Object quoteLiteral(String string) {
         return this.tokenizer.quoteLiteral(string);
      }
   }

   public static final class PatternInfo {
      public static final int OK = 0;
      public static final int BASE_CONFLICT = 1;
      public static final int CONFLICT = 2;
      public int status;
      public String conflictingPattern;
   }

   private static class PatternWithMatcher {
      public String pattern;
      public DateTimePatternGenerator.DateTimeMatcher matcherWithSkeleton;

      public PatternWithMatcher(String pat, DateTimePatternGenerator.DateTimeMatcher matcher) {
         this.pattern = pat;
         this.matcherWithSkeleton = matcher;
      }
   }

   private static class PatternWithSkeletonFlag {
      public String pattern;
      public boolean skeletonWasSpecified;

      public PatternWithSkeletonFlag(String pat, boolean skelSpecified) {
         this.pattern = pat;
         this.skeletonWasSpecified = skelSpecified;
      }

      public String toString() {
         return this.pattern + "," + this.skeletonWasSpecified;
      }
   }

   /** @deprecated */
   public static class VariableField {
      private final String string;
      private final int canonicalIndex;

      /** @deprecated */
      public VariableField(String string) {
         this(string, false);
      }

      /** @deprecated */
      public VariableField(String string, boolean strict) {
         this.canonicalIndex = DateTimePatternGenerator.getCanonicalIndex(string, strict);
         if(this.canonicalIndex < 0) {
            throw new IllegalArgumentException("Illegal datetime field:\t" + string);
         } else {
            this.string = string;
         }
      }

      /** @deprecated */
      public int getType() {
         return DateTimePatternGenerator.types[this.canonicalIndex][1];
      }

      /** @deprecated */
      public static String getCanonicalCode(int type) {
         try {
            return DateTimePatternGenerator.CANONICAL_ITEMS[type];
         } catch (Exception var2) {
            return String.valueOf(type);
         }
      }

      /** @deprecated */
      public boolean isNumeric() {
         return DateTimePatternGenerator.types[this.canonicalIndex][2] > 0;
      }

      private int getCanonicalIndex() {
         return this.canonicalIndex;
      }

      /** @deprecated */
      public String toString() {
         return this.string;
      }
   }
}
