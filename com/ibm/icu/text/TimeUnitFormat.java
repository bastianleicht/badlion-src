package com.ibm.icu.text;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.MeasureFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.TimeUnit;
import com.ibm.icu.util.TimeUnitAmount;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

public class TimeUnitFormat extends MeasureFormat {
   public static final int FULL_NAME = 0;
   public static final int ABBREVIATED_NAME = 1;
   private static final int TOTAL_STYLES = 2;
   private static final long serialVersionUID = -3707773153184971529L;
   private static final String DEFAULT_PATTERN_FOR_SECOND = "{0} s";
   private static final String DEFAULT_PATTERN_FOR_MINUTE = "{0} min";
   private static final String DEFAULT_PATTERN_FOR_HOUR = "{0} h";
   private static final String DEFAULT_PATTERN_FOR_DAY = "{0} d";
   private static final String DEFAULT_PATTERN_FOR_WEEK = "{0} w";
   private static final String DEFAULT_PATTERN_FOR_MONTH = "{0} m";
   private static final String DEFAULT_PATTERN_FOR_YEAR = "{0} y";
   private NumberFormat format;
   private ULocale locale;
   private transient Map timeUnitToCountToPatterns;
   private transient PluralRules pluralRules;
   private transient boolean isReady;
   private int style;

   public TimeUnitFormat() {
      this.isReady = false;
      this.style = 0;
   }

   public TimeUnitFormat(ULocale locale) {
      this((ULocale)locale, 0);
   }

   public TimeUnitFormat(Locale locale) {
      this((Locale)locale, 0);
   }

   public TimeUnitFormat(ULocale locale, int style) {
      if(style >= 0 && style < 2) {
         this.style = style;
         this.locale = locale;
         this.isReady = false;
      } else {
         throw new IllegalArgumentException("style should be either FULL_NAME or ABBREVIATED_NAME style");
      }
   }

   public TimeUnitFormat(Locale locale, int style) {
      this(ULocale.forLocale(locale), style);
   }

   public TimeUnitFormat setLocale(ULocale locale) {
      if(locale != this.locale) {
         this.locale = locale;
         this.isReady = false;
      }

      return this;
   }

   public TimeUnitFormat setLocale(Locale locale) {
      return this.setLocale(ULocale.forLocale(locale));
   }

   public TimeUnitFormat setNumberFormat(NumberFormat format) {
      if(format == this.format) {
         return this;
      } else {
         if(format == null) {
            if(this.locale == null) {
               this.isReady = false;
               return this;
            }

            this.format = NumberFormat.getNumberInstance(this.locale);
         } else {
            this.format = format;
         }

         if(!this.isReady) {
            return this;
         } else {
            for(Map<String, Object[]> countToPattern : this.timeUnitToCountToPatterns.values()) {
               for(Object[] pair : countToPattern.values()) {
                  MessageFormat pattern = (MessageFormat)pair[0];
                  pattern.setFormatByArgumentIndex(0, format);
                  pattern = (MessageFormat)pair[1];
                  pattern.setFormatByArgumentIndex(0, format);
               }
            }

            return this;
         }
      }
   }

   public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
      if(!(obj instanceof TimeUnitAmount)) {
         throw new IllegalArgumentException("can not format non TimeUnitAmount object");
      } else {
         if(!this.isReady) {
            this.setup();
         }

         TimeUnitAmount amount = (TimeUnitAmount)obj;
         Map<String, Object[]> countToPattern = (Map)this.timeUnitToCountToPatterns.get(amount.getTimeUnit());
         double number = amount.getNumber().doubleValue();
         String count = this.pluralRules.select(number);
         MessageFormat pattern = (MessageFormat)((Object[])countToPattern.get(count))[this.style];
         return pattern.format(new Object[]{amount.getNumber()}, toAppendTo, pos);
      }
   }

   public Object parseObject(String source, ParsePosition pos) {
      if(!this.isReady) {
         this.setup();
      }

      Number resultNumber = null;
      TimeUnit resultTimeUnit = null;
      int oldPos = pos.getIndex();
      int newPos = -1;
      int longestParseDistance = 0;
      String countOfLongestMatch = null;

      for(TimeUnit timeUnit : this.timeUnitToCountToPatterns.keySet()) {
         Map<String, Object[]> countToPattern = (Map)this.timeUnitToCountToPatterns.get(timeUnit);

         for(Entry<String, Object[]> patternEntry : countToPattern.entrySet()) {
            String count = (String)patternEntry.getKey();

            for(int styl = 0; styl < 2; ++styl) {
               MessageFormat pattern = (MessageFormat)((Object[])patternEntry.getValue())[styl];
               pos.setErrorIndex(-1);
               pos.setIndex(oldPos);
               Object parsed = pattern.parseObject(source, pos);
               if(pos.getErrorIndex() == -1 && pos.getIndex() != oldPos) {
                  Number temp = null;
                  if(((Object[])((Object[])parsed)).length != 0) {
                     temp = (Number)((Object[])((Object[])parsed))[0];
                     String select = this.pluralRules.select(temp.doubleValue());
                     if(!count.equals(select)) {
                        continue;
                     }
                  }

                  int parseDistance = pos.getIndex() - oldPos;
                  if(parseDistance > longestParseDistance) {
                     resultNumber = temp;
                     resultTimeUnit = timeUnit;
                     newPos = pos.getIndex();
                     longestParseDistance = parseDistance;
                     countOfLongestMatch = count;
                  }
               }
            }
         }
      }

      if(resultNumber == null && longestParseDistance != 0) {
         if(countOfLongestMatch.equals("zero")) {
            resultNumber = Integer.valueOf(0);
         } else if(countOfLongestMatch.equals("one")) {
            resultNumber = Integer.valueOf(1);
         } else if(countOfLongestMatch.equals("two")) {
            resultNumber = Integer.valueOf(2);
         } else {
            resultNumber = Integer.valueOf(3);
         }
      }

      if(longestParseDistance == 0) {
         pos.setIndex(oldPos);
         pos.setErrorIndex(0);
         return null;
      } else {
         pos.setIndex(newPos);
         pos.setErrorIndex(-1);
         return new TimeUnitAmount(resultNumber, resultTimeUnit);
      }
   }

   private void setup() {
      if(this.locale == null) {
         if(this.format != null) {
            this.locale = this.format.getLocale((ULocale.Type)null);
         } else {
            this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
         }
      }

      if(this.format == null) {
         this.format = NumberFormat.getNumberInstance(this.locale);
      }

      this.pluralRules = PluralRules.forLocale(this.locale);
      this.timeUnitToCountToPatterns = new HashMap();
      Set<String> pluralKeywords = this.pluralRules.getKeywords();
      this.setup("units", this.timeUnitToCountToPatterns, 0, pluralKeywords);
      this.setup("unitsShort", this.timeUnitToCountToPatterns, 1, pluralKeywords);
      this.isReady = true;
   }

   private void setup(String resourceKey, Map timeUnitToCountToPatterns, int style, Set pluralKeywords) {
      try {
         ICUResourceBundle resource = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", this.locale);
         ICUResourceBundle unitsRes = resource.getWithFallback(resourceKey);
         int size = unitsRes.getSize();

         for(int index = 0; index < size; ++index) {
            String timeUnitName = unitsRes.get(index).getKey();
            TimeUnit timeUnit = null;
            if(timeUnitName.equals("year")) {
               timeUnit = TimeUnit.YEAR;
            } else if(timeUnitName.equals("month")) {
               timeUnit = TimeUnit.MONTH;
            } else if(timeUnitName.equals("day")) {
               timeUnit = TimeUnit.DAY;
            } else if(timeUnitName.equals("hour")) {
               timeUnit = TimeUnit.HOUR;
            } else if(timeUnitName.equals("minute")) {
               timeUnit = TimeUnit.MINUTE;
            } else if(timeUnitName.equals("second")) {
               timeUnit = TimeUnit.SECOND;
            } else {
               if(!timeUnitName.equals("week")) {
                  continue;
               }

               timeUnit = TimeUnit.WEEK;
            }

            ICUResourceBundle oneUnitRes = unitsRes.getWithFallback(timeUnitName);
            int count = oneUnitRes.getSize();
            Map<String, Object[]> countToPatterns = (Map)timeUnitToCountToPatterns.get(timeUnit);
            if(countToPatterns == null) {
               countToPatterns = new TreeMap();
               timeUnitToCountToPatterns.put(timeUnit, countToPatterns);
            }

            for(int pluralIndex = 0; pluralIndex < count; ++pluralIndex) {
               String pluralCount = oneUnitRes.get(pluralIndex).getKey();
               if(pluralKeywords.contains(pluralCount)) {
                  String pattern = oneUnitRes.get(pluralIndex).getString();
                  MessageFormat messageFormat = new MessageFormat(pattern, this.locale);
                  if(this.format != null) {
                     messageFormat.setFormatByArgumentIndex(0, this.format);
                  }

                  Object[] pair = (Object[])countToPatterns.get(pluralCount);
                  if(pair == null) {
                     pair = new Object[2];
                     countToPatterns.put(pluralCount, pair);
                  }

                  pair[style] = messageFormat;
               }
            }
         }
      } catch (MissingResourceException var19) {
         ;
      }

      TimeUnit[] timeUnits = TimeUnit.values();
      Set<String> keywords = this.pluralRules.getKeywords();

      for(int i = 0; i < timeUnits.length; ++i) {
         TimeUnit timeUnit = timeUnits[i];
         Map<String, Object[]> countToPatterns = (Map)timeUnitToCountToPatterns.get(timeUnit);
         if(countToPatterns == null) {
            countToPatterns = new TreeMap();
            timeUnitToCountToPatterns.put(timeUnit, countToPatterns);
         }

         for(String pluralCount : keywords) {
            if(countToPatterns.get(pluralCount) == null || ((Object[])countToPatterns.get(pluralCount))[style] == null) {
               this.searchInTree(resourceKey, style, timeUnit, pluralCount, pluralCount, countToPatterns);
            }
         }
      }

   }

   private void searchInTree(String resourceKey, int styl, TimeUnit timeUnit, String srcPluralCount, String searchPluralCount, Map countToPatterns) {
      ULocale parentLocale = this.locale;

      for(String srcTimeUnitName = timeUnit.toString(); parentLocale != null; parentLocale = parentLocale.getFallback()) {
         try {
            ICUResourceBundle unitsRes = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", parentLocale);
            unitsRes = unitsRes.getWithFallback(resourceKey);
            ICUResourceBundle oneUnitRes = unitsRes.getWithFallback(srcTimeUnitName);
            String pattern = oneUnitRes.getStringWithFallback(searchPluralCount);
            MessageFormat messageFormat = new MessageFormat(pattern, this.locale);
            if(this.format != null) {
               messageFormat.setFormatByArgumentIndex(0, this.format);
            }

            Object[] pair = (Object[])countToPatterns.get(srcPluralCount);
            if(pair == null) {
               pair = new Object[2];
               countToPatterns.put(srcPluralCount, pair);
            }

            pair[styl] = messageFormat;
            return;
         }
      }

      if(parentLocale == null && resourceKey.equals("unitsShort")) {
         this.searchInTree("units", styl, timeUnit, srcPluralCount, searchPluralCount, countToPatterns);
         if(countToPatterns != null && countToPatterns.get(srcPluralCount) != null && ((Object[])countToPatterns.get(srcPluralCount))[styl] != null) {
            return;
         }
      }

      if(searchPluralCount.equals("other")) {
         MessageFormat messageFormat = null;
         if(timeUnit == TimeUnit.SECOND) {
            messageFormat = new MessageFormat("{0} s", this.locale);
         } else if(timeUnit == TimeUnit.MINUTE) {
            messageFormat = new MessageFormat("{0} min", this.locale);
         } else if(timeUnit == TimeUnit.HOUR) {
            messageFormat = new MessageFormat("{0} h", this.locale);
         } else if(timeUnit == TimeUnit.WEEK) {
            messageFormat = new MessageFormat("{0} w", this.locale);
         } else if(timeUnit == TimeUnit.DAY) {
            messageFormat = new MessageFormat("{0} d", this.locale);
         } else if(timeUnit == TimeUnit.MONTH) {
            messageFormat = new MessageFormat("{0} m", this.locale);
         } else if(timeUnit == TimeUnit.YEAR) {
            messageFormat = new MessageFormat("{0} y", this.locale);
         }

         if(this.format != null && messageFormat != null) {
            messageFormat.setFormatByArgumentIndex(0, this.format);
         }

         Object[] pair = (Object[])countToPatterns.get(srcPluralCount);
         if(pair == null) {
            pair = new Object[2];
            countToPatterns.put(srcPluralCount, pair);
         }

         pair[styl] = messageFormat;
      } else {
         this.searchInTree(resourceKey, styl, timeUnit, srcPluralCount, "other", countToPatterns);
      }

   }
}
