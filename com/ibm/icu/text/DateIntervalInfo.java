package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.DateIntervalFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.Freezable;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.Map.Entry;

public class DateIntervalInfo implements Cloneable, Freezable, Serializable {
   static final int currentSerialVersion = 1;
   static final String[] CALENDAR_FIELD_TO_PATTERN_LETTER = new String[]{"G", "y", "M", "w", "W", "d", "D", "E", "F", "a", "h", "H", "m"};
   private static final long serialVersionUID = 1L;
   private static final int MINIMUM_SUPPORTED_CALENDAR_FIELD = 12;
   private static String FALLBACK_STRING = "fallback";
   private static String LATEST_FIRST_PREFIX = "latestFirst:";
   private static String EARLIEST_FIRST_PREFIX = "earliestFirst:";
   private static final ICUCache DIICACHE = new SimpleCache();
   private String fFallbackIntervalPattern;
   private boolean fFirstDateInPtnIsLaterDate = false;
   private Map fIntervalPatterns = null;
   private transient boolean frozen = false;
   private transient boolean fIntervalPatternsReadOnly = false;

   /** @deprecated */
   public DateIntervalInfo() {
      this.fIntervalPatterns = new HashMap();
      this.fFallbackIntervalPattern = "{0} – {1}";
   }

   public DateIntervalInfo(ULocale locale) {
      this.initializeData(locale);
   }

   private void initializeData(ULocale locale) {
      String key = locale.toString();
      DateIntervalInfo dii = (DateIntervalInfo)DIICACHE.get(key);
      if(dii == null) {
         this.setup(locale);
         this.fIntervalPatternsReadOnly = true;
         DIICACHE.put(key, ((DateIntervalInfo)this.clone()).freeze());
      } else {
         this.initializeFromReadOnlyPatterns(dii);
      }

   }

   private void initializeFromReadOnlyPatterns(DateIntervalInfo dii) {
      this.fFallbackIntervalPattern = dii.fFallbackIntervalPattern;
      this.fFirstDateInPtnIsLaterDate = dii.fFirstDateInPtnIsLaterDate;
      this.fIntervalPatterns = dii.fIntervalPatterns;
      this.fIntervalPatternsReadOnly = true;
   }

   private void setup(ULocale locale) {
      int DEFAULT_HASH_SIZE = 19;
      this.fIntervalPatterns = new HashMap(DEFAULT_HASH_SIZE);
      this.fFallbackIntervalPattern = "{0} – {1}";
      HashSet<String> skeletonSet = new HashSet();

      try {
         ULocale currentLocale = locale;
         String calendarTypeToUse = locale.getKeywordValue("calendar");
         if(calendarTypeToUse == null) {
            String[] preferredCalendarTypes = Calendar.getKeywordValuesForLocale("calendar", locale, true);
            calendarTypeToUse = preferredCalendarTypes[0];
         }

         if(calendarTypeToUse == null) {
            calendarTypeToUse = "gregorian";
         }

         while(true) {
            String name = currentLocale.getName();
            if(name.length() == 0) {
               break;
            }

            ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", currentLocale);
            ICUResourceBundle itvDtPtnResource = rb.getWithFallback("calendar/" + calendarTypeToUse + "/intervalFormats");
            String fallback = itvDtPtnResource.getStringWithFallback(FALLBACK_STRING);
            this.setFallbackIntervalPattern(fallback);
            int size = itvDtPtnResource.getSize();

            for(int index = 0; index < size; ++index) {
               String skeleton = itvDtPtnResource.get(index).getKey();
               if(!skeletonSet.contains(skeleton)) {
                  skeletonSet.add(skeleton);
                  if(skeleton.compareTo(FALLBACK_STRING) != 0) {
                     ICUResourceBundle intervalPatterns = (ICUResourceBundle)itvDtPtnResource.get(skeleton);
                     int ptnNum = intervalPatterns.getSize();

                     for(int ptnIndex = 0; ptnIndex < ptnNum; ++ptnIndex) {
                        String key = intervalPatterns.get(ptnIndex).getKey();
                        String pattern = intervalPatterns.get(ptnIndex).getString();
                        int calendarField = -1;
                        if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[1]) == 0) {
                           calendarField = 1;
                        } else if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[2]) == 0) {
                           calendarField = 2;
                        } else if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[5]) == 0) {
                           calendarField = 5;
                        } else if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[9]) == 0) {
                           calendarField = 9;
                        } else if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[10]) == 0) {
                           calendarField = 10;
                        } else if(key.compareTo(CALENDAR_FIELD_TO_PATTERN_LETTER[12]) == 0) {
                           calendarField = 12;
                        }

                        if(calendarField != -1) {
                           this.setIntervalPatternInternally(skeleton, key, pattern);
                        }
                     }
                  }
               }
            }

            try {
               UResourceBundle parentNameBundle = rb.get("%%Parent");
               currentLocale = new ULocale(parentNameBundle.getString());
            } catch (MissingResourceException var19) {
               currentLocale = currentLocale.getFallback();
            }

            if(currentLocale == null || currentLocale.getBaseName().equals("root")) {
               break;
            }
         }
      } catch (MissingResourceException var20) {
         ;
      }

   }

   private static int splitPatternInto2Part(String intervalPattern) {
      boolean inQuote = false;
      char prevCh = 0;
      int count = 0;
      int[] patternRepeated = new int[58];
      int PATTERN_CHAR_BASE = 65;
      boolean foundRepetition = false;

      int i;
      for(i = 0; i < intervalPattern.length(); ++i) {
         char ch = intervalPattern.charAt(i);
         if(ch != prevCh && count > 0) {
            int repeated = patternRepeated[prevCh - PATTERN_CHAR_BASE];
            if(repeated != 0) {
               foundRepetition = true;
               break;
            }

            patternRepeated[prevCh - PATTERN_CHAR_BASE] = 1;
            count = 0;
         }

         if(ch == 39) {
            if(i + 1 < intervalPattern.length() && intervalPattern.charAt(i + 1) == 39) {
               ++i;
            } else {
               inQuote = !inQuote;
            }
         } else if(!inQuote && (ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90)) {
            prevCh = ch;
            ++count;
         }
      }

      if(count > 0 && !foundRepetition && patternRepeated[prevCh - PATTERN_CHAR_BASE] == 0) {
         count = 0;
      }

      return i - count;
   }

   public void setIntervalPattern(String skeleton, int lrgDiffCalUnit, String intervalPattern) {
      if(this.frozen) {
         throw new UnsupportedOperationException("no modification is allowed after DII is frozen");
      } else if(lrgDiffCalUnit > 12) {
         throw new IllegalArgumentException("calendar field is larger than MINIMUM_SUPPORTED_CALENDAR_FIELD");
      } else {
         if(this.fIntervalPatternsReadOnly) {
            this.fIntervalPatterns = cloneIntervalPatterns(this.fIntervalPatterns);
            this.fIntervalPatternsReadOnly = false;
         }

         DateIntervalInfo.PatternInfo ptnInfo = this.setIntervalPatternInternally(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[lrgDiffCalUnit], intervalPattern);
         if(lrgDiffCalUnit == 11) {
            this.setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[9], ptnInfo);
            this.setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[10], ptnInfo);
         } else if(lrgDiffCalUnit == 5 || lrgDiffCalUnit == 7) {
            this.setIntervalPattern(skeleton, CALENDAR_FIELD_TO_PATTERN_LETTER[5], ptnInfo);
         }

      }
   }

   private DateIntervalInfo.PatternInfo setIntervalPatternInternally(String skeleton, String lrgDiffCalUnit, String intervalPattern) {
      Map<String, DateIntervalInfo.PatternInfo> patternsOfOneSkeleton = (Map)this.fIntervalPatterns.get(skeleton);
      boolean emptyHash = false;
      if(patternsOfOneSkeleton == null) {
         patternsOfOneSkeleton = new HashMap();
         emptyHash = true;
      }

      boolean order = this.fFirstDateInPtnIsLaterDate;
      if(intervalPattern.startsWith(LATEST_FIRST_PREFIX)) {
         order = true;
         int prefixLength = LATEST_FIRST_PREFIX.length();
         intervalPattern = intervalPattern.substring(prefixLength, intervalPattern.length());
      } else if(intervalPattern.startsWith(EARLIEST_FIRST_PREFIX)) {
         order = false;
         int earliestFirstLength = EARLIEST_FIRST_PREFIX.length();
         intervalPattern = intervalPattern.substring(earliestFirstLength, intervalPattern.length());
      }

      DateIntervalInfo.PatternInfo itvPtnInfo = genPatternInfo(intervalPattern, order);
      patternsOfOneSkeleton.put(lrgDiffCalUnit, itvPtnInfo);
      if(emptyHash) {
         this.fIntervalPatterns.put(skeleton, patternsOfOneSkeleton);
      }

      return itvPtnInfo;
   }

   private void setIntervalPattern(String skeleton, String lrgDiffCalUnit, DateIntervalInfo.PatternInfo ptnInfo) {
      Map<String, DateIntervalInfo.PatternInfo> patternsOfOneSkeleton = (Map)this.fIntervalPatterns.get(skeleton);
      patternsOfOneSkeleton.put(lrgDiffCalUnit, ptnInfo);
   }

   static DateIntervalInfo.PatternInfo genPatternInfo(String intervalPattern, boolean laterDateFirst) {
      int splitPoint = splitPatternInto2Part(intervalPattern);
      String firstPart = intervalPattern.substring(0, splitPoint);
      String secondPart = null;
      if(splitPoint < intervalPattern.length()) {
         secondPart = intervalPattern.substring(splitPoint, intervalPattern.length());
      }

      return new DateIntervalInfo.PatternInfo(firstPart, secondPart, laterDateFirst);
   }

   public DateIntervalInfo.PatternInfo getIntervalPattern(String skeleton, int field) {
      if(field > 12) {
         throw new IllegalArgumentException("no support for field less than MINUTE");
      } else {
         Map<String, DateIntervalInfo.PatternInfo> patternsOfOneSkeleton = (Map)this.fIntervalPatterns.get(skeleton);
         if(patternsOfOneSkeleton != null) {
            DateIntervalInfo.PatternInfo intervalPattern = (DateIntervalInfo.PatternInfo)patternsOfOneSkeleton.get(CALENDAR_FIELD_TO_PATTERN_LETTER[field]);
            if(intervalPattern != null) {
               return intervalPattern;
            }
         }

         return null;
      }
   }

   public String getFallbackIntervalPattern() {
      return this.fFallbackIntervalPattern;
   }

   public void setFallbackIntervalPattern(String fallbackPattern) {
      if(this.frozen) {
         throw new UnsupportedOperationException("no modification is allowed after DII is frozen");
      } else {
         int firstPatternIndex = fallbackPattern.indexOf("{0}");
         int secondPatternIndex = fallbackPattern.indexOf("{1}");
         if(firstPatternIndex != -1 && secondPatternIndex != -1) {
            if(firstPatternIndex > secondPatternIndex) {
               this.fFirstDateInPtnIsLaterDate = true;
            }

            this.fFallbackIntervalPattern = fallbackPattern;
         } else {
            throw new IllegalArgumentException("no pattern {0} or pattern {1} in fallbackPattern");
         }
      }
   }

   public boolean getDefaultOrder() {
      return this.fFirstDateInPtnIsLaterDate;
   }

   public Object clone() {
      return this.frozen?this:this.cloneUnfrozenDII();
   }

   private Object cloneUnfrozenDII() {
      try {
         DateIntervalInfo other = (DateIntervalInfo)super.clone();
         other.fFallbackIntervalPattern = this.fFallbackIntervalPattern;
         other.fFirstDateInPtnIsLaterDate = this.fFirstDateInPtnIsLaterDate;
         if(this.fIntervalPatternsReadOnly) {
            other.fIntervalPatterns = this.fIntervalPatterns;
            other.fIntervalPatternsReadOnly = true;
         } else {
            other.fIntervalPatterns = cloneIntervalPatterns(this.fIntervalPatterns);
            other.fIntervalPatternsReadOnly = false;
         }

         other.frozen = false;
         return other;
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException("clone is not supported");
      }
   }

   private static Map cloneIntervalPatterns(Map patterns) {
      Map<String, Map<String, DateIntervalInfo.PatternInfo>> result = new HashMap();

      for(Entry<String, Map<String, DateIntervalInfo.PatternInfo>> skeletonEntry : patterns.entrySet()) {
         String skeleton = (String)skeletonEntry.getKey();
         Map<String, DateIntervalInfo.PatternInfo> patternsOfOneSkeleton = (Map)skeletonEntry.getValue();
         Map<String, DateIntervalInfo.PatternInfo> oneSetPtn = new HashMap();

         for(Entry<String, DateIntervalInfo.PatternInfo> calEntry : patternsOfOneSkeleton.entrySet()) {
            String calField = (String)calEntry.getKey();
            DateIntervalInfo.PatternInfo value = (DateIntervalInfo.PatternInfo)calEntry.getValue();
            oneSetPtn.put(calField, value);
         }

         result.put(skeleton, oneSetPtn);
      }

      return result;
   }

   public boolean isFrozen() {
      return this.frozen;
   }

   public DateIntervalInfo freeze() {
      this.frozen = true;
      this.fIntervalPatternsReadOnly = true;
      return this;
   }

   public DateIntervalInfo cloneAsThawed() {
      DateIntervalInfo result = (DateIntervalInfo)((DateIntervalInfo)this.cloneUnfrozenDII());
      return result;
   }

   static void parseSkeleton(String skeleton, int[] skeletonFieldWidth) {
      int PATTERN_CHAR_BASE = 65;

      for(int i = 0; i < skeleton.length(); ++i) {
         ++skeletonFieldWidth[skeleton.charAt(i) - PATTERN_CHAR_BASE];
      }

   }

   private static boolean stringNumeric(int fieldWidth, int anotherFieldWidth, char patternLetter) {
      return patternLetter == 77 && (fieldWidth <= 2 && anotherFieldWidth > 2 || fieldWidth > 2 && anotherFieldWidth <= 2);
   }

   DateIntervalFormat.BestMatchInfo getBestSkeleton(String inputSkeleton) {
      String bestSkeleton = inputSkeleton;
      int[] inputSkeletonFieldWidth = new int[58];
      int[] skeletonFieldWidth = new int[58];
      int DIFFERENT_FIELD = 4096;
      int STRING_NUMERIC_DIFFERENCE = 256;
      int BASE = 65;
      boolean replaceZWithV = false;
      if(inputSkeleton.indexOf(122) != -1) {
         inputSkeleton = inputSkeleton.replace('z', 'v');
         replaceZWithV = true;
      }

      parseSkeleton(inputSkeleton, inputSkeletonFieldWidth);
      int bestDistance = Integer.MAX_VALUE;
      int bestFieldDifference = 0;

      for(String skeleton : this.fIntervalPatterns.keySet()) {
         for(int i = 0; i < skeletonFieldWidth.length; ++i) {
            skeletonFieldWidth[i] = 0;
         }

         parseSkeleton(skeleton, skeletonFieldWidth);
         int distance = 0;
         int fieldDifference = 1;

         for(int i = 0; i < inputSkeletonFieldWidth.length; ++i) {
            int inputFieldWidth = inputSkeletonFieldWidth[i];
            int fieldWidth = skeletonFieldWidth[i];
            if(inputFieldWidth != fieldWidth) {
               if(inputFieldWidth == 0) {
                  fieldDifference = -1;
                  distance += 4096;
               } else if(fieldWidth == 0) {
                  fieldDifference = -1;
                  distance += 4096;
               } else if(stringNumeric(inputFieldWidth, fieldWidth, (char)(i + 65))) {
                  distance += 256;
               } else {
                  distance += Math.abs(inputFieldWidth - fieldWidth);
               }
            }
         }

         if(distance < bestDistance) {
            bestSkeleton = skeleton;
            bestDistance = distance;
            bestFieldDifference = fieldDifference;
         }

         if(distance == 0) {
            bestFieldDifference = 0;
            break;
         }
      }

      if(replaceZWithV && bestFieldDifference != -1) {
         bestFieldDifference = 2;
      }

      return new DateIntervalFormat.BestMatchInfo(bestSkeleton, bestFieldDifference);
   }

   public boolean equals(Object a) {
      if(a instanceof DateIntervalInfo) {
         DateIntervalInfo dtInfo = (DateIntervalInfo)a;
         return this.fIntervalPatterns.equals(dtInfo.fIntervalPatterns);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.fIntervalPatterns.hashCode();
   }

   /** @deprecated */
   public Map getPatterns() {
      LinkedHashMap<String, Set<String>> result = new LinkedHashMap();

      for(Entry<String, Map<String, DateIntervalInfo.PatternInfo>> entry : this.fIntervalPatterns.entrySet()) {
         result.put(entry.getKey(), new LinkedHashSet(((Map)entry.getValue()).keySet()));
      }

      return result;
   }

   public static final class PatternInfo implements Cloneable, Serializable {
      static final int currentSerialVersion = 1;
      private static final long serialVersionUID = 1L;
      private final String fIntervalPatternFirstPart;
      private final String fIntervalPatternSecondPart;
      private final boolean fFirstDateInPtnIsLaterDate;

      public PatternInfo(String firstPart, String secondPart, boolean firstDateInPtnIsLaterDate) {
         this.fIntervalPatternFirstPart = firstPart;
         this.fIntervalPatternSecondPart = secondPart;
         this.fFirstDateInPtnIsLaterDate = firstDateInPtnIsLaterDate;
      }

      public String getFirstPart() {
         return this.fIntervalPatternFirstPart;
      }

      public String getSecondPart() {
         return this.fIntervalPatternSecondPart;
      }

      public boolean firstDateInPtnIsLaterDate() {
         return this.fFirstDateInPtnIsLaterDate;
      }

      public boolean equals(Object a) {
         if(!(a instanceof DateIntervalInfo.PatternInfo)) {
            return false;
         } else {
            DateIntervalInfo.PatternInfo patternInfo = (DateIntervalInfo.PatternInfo)a;
            return Utility.objectEquals(this.fIntervalPatternFirstPart, patternInfo.fIntervalPatternFirstPart) && Utility.objectEquals(this.fIntervalPatternSecondPart, this.fIntervalPatternSecondPart) && this.fFirstDateInPtnIsLaterDate == patternInfo.fFirstDateInPtnIsLaterDate;
         }
      }

      public int hashCode() {
         int hash = this.fIntervalPatternFirstPart != null?this.fIntervalPatternFirstPart.hashCode():0;
         if(this.fIntervalPatternSecondPart != null) {
            hash ^= this.fIntervalPatternSecondPart.hashCode();
         }

         if(this.fFirstDateInPtnIsLaterDate) {
            hash = ~hash;
         }

         return hash;
      }
   }
}
