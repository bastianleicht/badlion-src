package com.ibm.icu.text;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.DateNumberFormat;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.DateFormatSymbols;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.DisplayContext;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.text.TimeZoneFormat;
import com.ibm.icu.text.UTF16;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.HebrewCalendar;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneTransition;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.Format.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

public class SimpleDateFormat extends DateFormat {
   private static final long serialVersionUID = 4774881970558875024L;
   static final int currentSerialVersion = 2;
   static boolean DelayedHebrewMonthCheck = false;
   private static final int[] CALENDAR_FIELD_TO_LEVEL = new int[]{0, 10, 20, 20, 30, 30, 20, 30, 30, 40, 50, 50, 60, 70, 80, 0, 0, 10, 30, 10, 0, 40};
   private static final int[] PATTERN_CHAR_TO_LEVEL = new int[]{-1, 40, -1, -1, 20, 30, 30, 0, 50, -1, -1, 50, 20, 20, -1, 0, -1, 20, -1, 80, -1, 10, 0, 30, 0, 10, 0, -1, -1, -1, -1, -1, -1, 40, -1, 30, 30, 30, -1, 0, 50, -1, -1, 50, -1, 60, -1, -1, -1, 20, -1, 70, -1, 10, 0, 20, 0, 10, 0, -1, -1, -1, -1, -1};
   private static final int HEBREW_CAL_CUR_MILLENIUM_START_YEAR = 5000;
   private static final int HEBREW_CAL_CUR_MILLENIUM_END_YEAR = 6000;
   private int serialVersionOnStream;
   private String pattern;
   private String override;
   private HashMap numberFormatters;
   private HashMap overrideMap;
   private DateFormatSymbols formatData;
   private transient ULocale locale;
   private Date defaultCenturyStart;
   private transient int defaultCenturyStartYear;
   private transient long defaultCenturyBase;
   private transient TimeZoneFormat.TimeType tztype;
   private static final int millisPerHour = 3600000;
   private static final int ISOSpecialEra = -32000;
   private static final String SUPPRESS_NEGATIVE_PREFIX = "\uab00";
   private transient boolean useFastFormat;
   private volatile TimeZoneFormat tzFormat;
   private transient DisplayContext capitalizationSetting;
   private static ULocale cachedDefaultLocale = null;
   private static String cachedDefaultPattern = null;
   private static final String FALLBACKPATTERN = "yy/MM/dd HH:mm";
   private static final int PATTERN_CHAR_BASE = 64;
   private static final int[] PATTERN_CHAR_TO_INDEX = new int[]{-1, 22, -1, -1, 10, 9, 11, 0, 5, -1, -1, 16, 26, 2, -1, 31, -1, 27, -1, 8, -1, 30, 29, 13, 32, 18, 23, -1, -1, -1, -1, -1, -1, 14, -1, 25, 3, 19, -1, 21, 15, -1, -1, 4, -1, 6, -1, -1, -1, 28, -1, 7, -1, 20, 24, 12, 33, 1, 17, -1, -1, -1, -1, -1};
   private static final int[] PATTERN_INDEX_TO_CALENDAR_FIELD = new int[]{0, 1, 2, 5, 11, 11, 12, 13, 14, 7, 6, 8, 3, 4, 9, 10, 10, 15, 17, 18, 19, 20, 21, 15, 15, 18, 2, 2, 2, 15, 1, 15, 15, 15};
   private static final int[] PATTERN_INDEX_TO_DATE_FORMAT_FIELD = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};
   private static final DateFormat.Field[] PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE = new DateFormat.Field[]{DateFormat.Field.ERA, DateFormat.Field.YEAR, DateFormat.Field.MONTH, DateFormat.Field.DAY_OF_MONTH, DateFormat.Field.HOUR_OF_DAY1, DateFormat.Field.HOUR_OF_DAY0, DateFormat.Field.MINUTE, DateFormat.Field.SECOND, DateFormat.Field.MILLISECOND, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.DAY_OF_YEAR, DateFormat.Field.DAY_OF_WEEK_IN_MONTH, DateFormat.Field.WEEK_OF_YEAR, DateFormat.Field.WEEK_OF_MONTH, DateFormat.Field.AM_PM, DateFormat.Field.HOUR1, DateFormat.Field.HOUR0, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR_WOY, DateFormat.Field.DOW_LOCAL, DateFormat.Field.EXTENDED_YEAR, DateFormat.Field.JULIAN_DAY, DateFormat.Field.MILLISECONDS_IN_DAY, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.DAY_OF_WEEK, DateFormat.Field.MONTH, DateFormat.Field.QUARTER, DateFormat.Field.QUARTER, DateFormat.Field.TIME_ZONE, DateFormat.Field.YEAR, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE, DateFormat.Field.TIME_ZONE};
   private static ICUCache PARSED_PATTERN_CACHE = new SimpleCache();
   private transient Object[] patternItems;
   private transient boolean useLocalZeroPaddingNumberFormat;
   private transient char[] decDigits;
   private transient char[] decimalBuf;
   private static final String NUMERIC_FORMAT_CHARS = "MYyudehHmsSDFwWkK";
   static final UnicodeSet DATE_PATTERN_TYPE = (new UnicodeSet("[GyYuUQqMLlwWd]")).freeze();

   public SimpleDateFormat() {
      this(getDefaultPattern(), (DateFormatSymbols)null, (Calendar)null, (NumberFormat)null, (ULocale)null, true, (String)null);
   }

   public SimpleDateFormat(String pattern) {
      this(pattern, (DateFormatSymbols)null, (Calendar)null, (NumberFormat)null, (ULocale)null, true, (String)null);
   }

   public SimpleDateFormat(String pattern, Locale loc) {
      this(pattern, (DateFormatSymbols)null, (Calendar)null, (NumberFormat)null, ULocale.forLocale(loc), true, (String)null);
   }

   public SimpleDateFormat(String pattern, ULocale loc) {
      this(pattern, (DateFormatSymbols)null, (Calendar)null, (NumberFormat)null, loc, true, (String)null);
   }

   public SimpleDateFormat(String pattern, String override, ULocale loc) {
      this(pattern, (DateFormatSymbols)null, (Calendar)null, (NumberFormat)null, loc, false, override);
   }

   public SimpleDateFormat(String pattern, DateFormatSymbols formatData) {
      this(pattern, (DateFormatSymbols)formatData.clone(), (Calendar)null, (NumberFormat)null, (ULocale)null, true, (String)null);
   }

   /** @deprecated */
   public SimpleDateFormat(String pattern, DateFormatSymbols formatData, ULocale loc) {
      this(pattern, (DateFormatSymbols)formatData.clone(), (Calendar)null, (NumberFormat)null, loc, true, (String)null);
   }

   SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, ULocale locale, boolean useFastFormat, String override) {
      this(pattern, (DateFormatSymbols)formatData.clone(), (Calendar)calendar.clone(), (NumberFormat)null, locale, useFastFormat, override);
   }

   private SimpleDateFormat(String pattern, DateFormatSymbols formatData, Calendar calendar, NumberFormat numberFormat, ULocale locale, boolean useFastFormat, String override) {
      this.serialVersionOnStream = 2;
      this.tztype = TimeZoneFormat.TimeType.UNKNOWN;
      this.pattern = pattern;
      this.formatData = formatData;
      this.calendar = calendar;
      this.numberFormat = numberFormat;
      this.locale = locale;
      this.useFastFormat = useFastFormat;
      this.override = override;
      this.initialize();
   }

   /** @deprecated */
   public static SimpleDateFormat getInstance(Calendar.FormatConfiguration formatConfig) {
      String ostr = formatConfig.getOverrideString();
      boolean useFast = ostr != null && ostr.length() > 0;
      return new SimpleDateFormat(formatConfig.getPatternString(), formatConfig.getDateFormatSymbols(), formatConfig.getCalendar(), (NumberFormat)null, formatConfig.getLocale(), useFast, formatConfig.getOverrideString());
   }

   private void initialize() {
      if(this.locale == null) {
         this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
      }

      if(this.formatData == null) {
         this.formatData = new DateFormatSymbols(this.locale);
      }

      if(this.calendar == null) {
         this.calendar = Calendar.getInstance(this.locale);
      }

      if(this.numberFormat == null) {
         NumberingSystem ns = NumberingSystem.getInstance(this.locale);
         if(ns.isAlgorithmic()) {
            this.numberFormat = NumberFormat.getInstance(this.locale);
         } else {
            String digitString = ns.getDescription();
            String nsName = ns.getName();
            this.numberFormat = new DateNumberFormat(this.locale, digitString, nsName);
         }
      }

      this.defaultCenturyBase = System.currentTimeMillis();
      this.setLocale(this.calendar.getLocale(ULocale.VALID_LOCALE), this.calendar.getLocale(ULocale.ACTUAL_LOCALE));
      this.initLocalZeroPaddingNumberFormat();
      if(this.override != null) {
         this.initNumberFormatters(this.locale);
      }

      this.capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
   }

   private synchronized void initializeTimeZoneFormat(boolean bForceUpdate) {
      if(bForceUpdate || this.tzFormat == null) {
         this.tzFormat = TimeZoneFormat.getInstance(this.locale);
         String digits = null;
         if(this.numberFormat instanceof DecimalFormat) {
            DecimalFormatSymbols decsym = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols();
            digits = new String(decsym.getDigits());
         } else if(this.numberFormat instanceof DateNumberFormat) {
            digits = new String(((DateNumberFormat)this.numberFormat).getDigits());
         }

         if(digits != null && !this.tzFormat.getGMTOffsetDigits().equals(digits)) {
            if(this.tzFormat.isFrozen()) {
               this.tzFormat = this.tzFormat.cloneAsThawed();
            }

            this.tzFormat.setGMTOffsetDigits(digits);
         }
      }

   }

   private TimeZoneFormat tzFormat() {
      if(this.tzFormat == null) {
         this.initializeTimeZoneFormat(false);
      }

      return this.tzFormat;
   }

   private static synchronized String getDefaultPattern() {
      ULocale defaultLocale = ULocale.getDefault(ULocale.Category.FORMAT);
      if(!defaultLocale.equals(cachedDefaultLocale)) {
         cachedDefaultLocale = defaultLocale;
         Calendar cal = Calendar.getInstance(cachedDefaultLocale);

         try {
            CalendarData calData = new CalendarData(cachedDefaultLocale, cal.getType());
            String[] dateTimePatterns = calData.getDateTimePatterns();
            int glueIndex = 8;
            if(dateTimePatterns.length >= 13) {
               glueIndex += 4;
            }

            cachedDefaultPattern = MessageFormat.format(dateTimePatterns[glueIndex], new Object[]{dateTimePatterns[3], dateTimePatterns[7]});
         } catch (MissingResourceException var5) {
            cachedDefaultPattern = "yy/MM/dd HH:mm";
         }
      }

      return cachedDefaultPattern;
   }

   private void parseAmbiguousDatesAsAfter(Date startDate) {
      this.defaultCenturyStart = startDate;
      this.calendar.setTime(startDate);
      this.defaultCenturyStartYear = this.calendar.get(1);
   }

   private void initializeDefaultCenturyStart(long baseTime) {
      this.defaultCenturyBase = baseTime;
      Calendar tmpCal = (Calendar)this.calendar.clone();
      tmpCal.setTimeInMillis(baseTime);
      tmpCal.add(1, -80);
      this.defaultCenturyStart = tmpCal.getTime();
      this.defaultCenturyStartYear = tmpCal.get(1);
   }

   private Date getDefaultCenturyStart() {
      if(this.defaultCenturyStart == null) {
         this.initializeDefaultCenturyStart(this.defaultCenturyBase);
      }

      return this.defaultCenturyStart;
   }

   private int getDefaultCenturyStartYear() {
      if(this.defaultCenturyStart == null) {
         this.initializeDefaultCenturyStart(this.defaultCenturyBase);
      }

      return this.defaultCenturyStartYear;
   }

   public void set2DigitYearStart(Date startDate) {
      this.parseAmbiguousDatesAsAfter(startDate);
   }

   public Date get2DigitYearStart() {
      return this.getDefaultCenturyStart();
   }

   public StringBuffer format(Calendar cal, StringBuffer toAppendTo, FieldPosition pos) {
      TimeZone backupTZ = null;
      if(cal != this.calendar && !cal.getType().equals(this.calendar.getType())) {
         this.calendar.setTimeInMillis(cal.getTimeInMillis());
         backupTZ = this.calendar.getTimeZone();
         this.calendar.setTimeZone(cal.getTimeZone());
         cal = this.calendar;
      }

      StringBuffer result = this.format(cal, this.capitalizationSetting, toAppendTo, pos, (List)null);
      if(backupTZ != null) {
         this.calendar.setTimeZone(backupTZ);
      }

      return result;
   }

   private StringBuffer format(Calendar cal, DisplayContext capitalizationContext, StringBuffer toAppendTo, FieldPosition pos, List attributes) {
      pos.setBeginIndex(0);
      pos.setEndIndex(0);
      Object[] items = this.getPatternItems();

      for(int i = 0; i < items.length; ++i) {
         if(items[i] instanceof String) {
            toAppendTo.append((String)items[i]);
         } else {
            SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
            int start = 0;
            if(attributes != null) {
               start = toAppendTo.length();
            }

            if(this.useFastFormat) {
               this.subFormat(toAppendTo, item.type, item.length, toAppendTo.length(), i, capitalizationContext, pos, cal);
            } else {
               toAppendTo.append(this.subFormat(item.type, item.length, toAppendTo.length(), i, capitalizationContext, pos, cal));
            }

            if(attributes != null) {
               int end = toAppendTo.length();
               if(end - start > 0) {
                  DateFormat.Field attr = this.patternCharToDateFormatField(item.type);
                  FieldPosition fp = new FieldPosition(attr);
                  fp.setBeginIndex(start);
                  fp.setEndIndex(end);
                  attributes.add(fp);
               }
            }
         }
      }

      return toAppendTo;
   }

   protected DateFormat.Field patternCharToDateFormatField(char ch) {
      int patternCharIndex = -1;
      if(65 <= ch && ch <= 122) {
         patternCharIndex = PATTERN_CHAR_TO_INDEX[ch - 64];
      }

      return patternCharIndex != -1?PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex]:null;
   }

   protected String subFormat(char ch, int count, int beginOffset, FieldPosition pos, DateFormatSymbols fmtData, Calendar cal) throws IllegalArgumentException {
      return this.subFormat(ch, count, beginOffset, 0, DisplayContext.CAPITALIZATION_NONE, pos, cal);
   }

   /** @deprecated */
   protected String subFormat(char ch, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal) {
      StringBuffer buf = new StringBuffer();
      this.subFormat(buf, ch, count, beginOffset, fieldNum, capitalizationContext, pos, cal);
      return buf.toString();
   }

   /** @deprecated */
   protected void subFormat(StringBuffer buf, char ch, int count, int beginOffset, int fieldNum, DisplayContext capitalizationContext, FieldPosition pos, Calendar cal) {
      int maxIntCount = Integer.MAX_VALUE;
      int bufstart = buf.length();
      TimeZone tz = cal.getTimeZone();
      long date = cal.getTimeInMillis();
      String result = null;
      int patternCharIndex = -1;
      if(65 <= ch && ch <= 122) {
         patternCharIndex = PATTERN_CHAR_TO_INDEX[ch - 64];
      }

      if(patternCharIndex == -1) {
         if(ch != 108) {
            throw new IllegalArgumentException("Illegal pattern character \'" + ch + "\' in \"" + this.pattern + '\"');
         }
      } else {
         int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
         int value = cal.get(field);
         NumberFormat currentNumberFormat = this.getNumberFormat(ch);
         DateFormatSymbols.CapitalizationContextUsage capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.OTHER;
         switch(patternCharIndex) {
         case 0:
            if(cal.getType().equals("chinese")) {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, 1, 9);
            } else if(count == 5) {
               safeAppend(this.formatData.narrowEras, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_NARROW;
            } else if(count == 4) {
               safeAppend(this.formatData.eraNames, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_WIDE;
            } else {
               safeAppend(this.formatData.eras, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ERA_ABBREV;
            }
            break;
         case 2:
         case 26:
            if(cal.getType().equals("hebrew")) {
               boolean isLeap = HebrewCalendar.isLeapYear(cal.get(1));
               if(isLeap && value == 6 && count >= 3) {
                  value = 13;
               }

               if(!isLeap && value >= 6 && count < 3) {
                  --value;
               }
            }

            int isLeapMonth = this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7?cal.get(22):0;
            if(count == 5) {
               if(patternCharIndex == 2) {
                  safeAppendWithMonthPattern(this.formatData.narrowMonths, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[2]:null);
               } else {
                  safeAppendWithMonthPattern(this.formatData.standaloneNarrowMonths, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[5]:null);
               }

               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_NARROW;
            } else if(count == 4) {
               if(patternCharIndex == 2) {
                  safeAppendWithMonthPattern(this.formatData.months, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[0]:null);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
               } else {
                  safeAppendWithMonthPattern(this.formatData.standaloneMonths, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[3]:null);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
               }
            } else if(count == 3) {
               if(patternCharIndex == 2) {
                  safeAppendWithMonthPattern(this.formatData.shortMonths, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[1]:null);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_FORMAT;
               } else {
                  safeAppendWithMonthPattern(this.formatData.standaloneShortMonths, value, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[4]:null);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.MONTH_STANDALONE;
               }
            } else {
               StringBuffer monthNumber = new StringBuffer();
               this.zeroPaddingNumber(currentNumberFormat, monthNumber, value + 1, count, Integer.MAX_VALUE);
               String[] monthNumberStrings = new String[]{monthNumber.toString()};
               safeAppendWithMonthPattern(monthNumberStrings, 0, buf, isLeapMonth != 0?this.formatData.leapMonthPatterns[6]:null);
            }
            break;
         case 3:
         case 5:
         case 6:
         case 7:
         case 10:
         case 11:
         case 12:
         case 13:
         case 16:
         case 20:
         case 21:
         case 22:
         default:
            this.zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
            break;
         case 4:
            if(value == 0) {
               this.zeroPaddingNumber(currentNumberFormat, buf, cal.getMaximum(11) + 1, count, Integer.MAX_VALUE);
            } else {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
            }
            break;
         case 8:
            this.numberFormat.setMinimumIntegerDigits(Math.min(3, count));
            this.numberFormat.setMaximumIntegerDigits(Integer.MAX_VALUE);
            if(count == 1) {
               value /= 100;
            } else if(count == 2) {
               value /= 10;
            }

            FieldPosition p = new FieldPosition(-1);
            this.numberFormat.format((long)value, buf, p);
            if(count > 3) {
               this.numberFormat.setMinimumIntegerDigits(count - 3);
               this.numberFormat.format(0L, buf, p);
            }
            break;
         case 14:
            safeAppend(this.formatData.ampms, value, buf);
            break;
         case 15:
            if(value == 0) {
               this.zeroPaddingNumber(currentNumberFormat, buf, cal.getLeastMaximum(10) + 1, count, Integer.MAX_VALUE);
            } else {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
            }
            break;
         case 17:
            if(count < 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.SPECIFIC_SHORT, tz, date);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
            } else {
               result = this.tzFormat().format(TimeZoneFormat.Style.SPECIFIC_LONG, tz, date);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
            }

            buf.append(result);
            break;
         case 19:
            if(count < 3) {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
               break;
            } else {
               value = cal.get(7);
            }
         case 9:
            if(count == 5) {
               safeAppend(this.formatData.narrowWeekdays, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
            } else if(count == 4) {
               safeAppend(this.formatData.weekdays, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
            } else if(count == 6 && this.formatData.shorterWeekdays != null) {
               safeAppend(this.formatData.shorterWeekdays, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
            } else {
               safeAppend(this.formatData.shortWeekdays, value, buf);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_FORMAT;
            }
            break;
         case 23:
            if(count < 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz, date);
            } else if(count == 5) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz, date);
            } else {
               result = this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz, date);
            }

            buf.append(result);
            break;
         case 24:
            if(count == 1) {
               result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_SHORT, tz, date);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_SHORT;
            } else if(count == 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_LONG, tz, date);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.METAZONE_LONG;
            }

            buf.append(result);
            break;
         case 25:
            if(count < 3) {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, 1, Integer.MAX_VALUE);
            } else {
               value = cal.get(7);
               if(count == 5) {
                  safeAppend(this.formatData.standaloneNarrowWeekdays, value, buf);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_NARROW;
               } else if(count == 4) {
                  safeAppend(this.formatData.standaloneWeekdays, value, buf);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
               } else if(count == 6 && this.formatData.standaloneShorterWeekdays != null) {
                  safeAppend(this.formatData.standaloneShorterWeekdays, value, buf);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
               } else {
                  safeAppend(this.formatData.standaloneShortWeekdays, value, buf);
                  capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.DAY_STANDALONE;
               }
            }
            break;
         case 27:
            if(count >= 4) {
               safeAppend(this.formatData.quarters, value / 3, buf);
            } else if(count == 3) {
               safeAppend(this.formatData.shortQuarters, value / 3, buf);
            } else {
               this.zeroPaddingNumber(currentNumberFormat, buf, value / 3 + 1, count, Integer.MAX_VALUE);
            }
            break;
         case 28:
            if(count >= 4) {
               safeAppend(this.formatData.standaloneQuarters, value / 3, buf);
            } else if(count == 3) {
               safeAppend(this.formatData.standaloneShortQuarters, value / 3, buf);
            } else {
               this.zeroPaddingNumber(currentNumberFormat, buf, value / 3 + 1, count, Integer.MAX_VALUE);
            }
            break;
         case 29:
            if(count == 1) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ZONE_ID_SHORT, tz, date);
            } else if(count == 2) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ZONE_ID, tz, date);
            } else if(count == 3) {
               result = this.tzFormat().format(TimeZoneFormat.Style.EXEMPLAR_LOCATION, tz, date);
            } else if(count == 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.GENERIC_LOCATION, tz, date);
               capContextUsageType = DateFormatSymbols.CapitalizationContextUsage.ZONE_LONG;
            }

            buf.append(result);
            break;
         case 30:
            if(this.formatData.shortYearNames != null && value <= this.formatData.shortYearNames.length) {
               safeAppend(this.formatData.shortYearNames, value - 1, buf);
               break;
            }
         case 1:
         case 18:
            if(this.override != null && (this.override.compareTo("hebr") == 0 || this.override.indexOf("y=hebr") >= 0) && value > 5000 && value < 6000) {
               value -= 5000;
            }

            if(count == 2) {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, 2, 2);
            } else {
               this.zeroPaddingNumber(currentNumberFormat, buf, value, count, Integer.MAX_VALUE);
            }
            break;
         case 31:
            if(count == 1) {
               result = this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT_SHORT, tz, date);
            } else if(count == 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.LOCALIZED_GMT, tz, date);
            }

            buf.append(result);
            break;
         case 32:
            if(count == 1) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_SHORT, tz, date);
            } else if(count == 2) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FIXED, tz, date);
            } else if(count == 3) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FIXED, tz, date);
            } else if(count == 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_FULL, tz, date);
            } else if(count == 5) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_FULL, tz, date);
            }

            buf.append(result);
            break;
         case 33:
            if(count == 1) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT, tz, date);
            } else if(count == 2) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED, tz, date);
            } else if(count == 3) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED, tz, date);
            } else if(count == 4) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL, tz, date);
            } else if(count == 5) {
               result = this.tzFormat().format(TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL, tz, date);
            }

            buf.append(result);
         }

         if(fieldNum == 0) {
            boolean titlecase = false;
            if(capitalizationContext != null) {
               switch(capitalizationContext) {
               case CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE:
                  titlecase = true;
                  break;
               case CAPITALIZATION_FOR_UI_LIST_OR_MENU:
               case CAPITALIZATION_FOR_STANDALONE:
                  if(this.formatData.capitalization != null) {
                     boolean[] transforms = (boolean[])this.formatData.capitalization.get(capContextUsageType);
                     titlecase = capitalizationContext == DisplayContext.CAPITALIZATION_FOR_UI_LIST_OR_MENU?transforms[0]:transforms[1];
                  }
               }
            }

            if(titlecase) {
               String firstField = buf.substring(bufstart);
               String firstFieldTitleCase = UCharacter.toTitleCase(this.locale, firstField, (BreakIterator)null, 768);
               buf.replace(bufstart, buf.length(), firstFieldTitleCase);
            }
         }

         if(pos.getBeginIndex() == pos.getEndIndex()) {
            if(pos.getField() == PATTERN_INDEX_TO_DATE_FORMAT_FIELD[patternCharIndex]) {
               pos.setBeginIndex(beginOffset);
               pos.setEndIndex(beginOffset + buf.length() - bufstart);
            } else if(pos.getFieldAttribute() == PATTERN_INDEX_TO_DATE_FORMAT_ATTRIBUTE[patternCharIndex]) {
               pos.setBeginIndex(beginOffset);
               pos.setEndIndex(beginOffset + buf.length() - bufstart);
            }
         }

      }
   }

   private static void safeAppend(String[] array, int value, StringBuffer appendTo) {
      if(array != null && value >= 0 && value < array.length) {
         appendTo.append(array[value]);
      }

   }

   private static void safeAppendWithMonthPattern(String[] array, int value, StringBuffer appendTo, String monthPattern) {
      if(array != null && value >= 0 && value < array.length) {
         if(monthPattern == null) {
            appendTo.append(array[value]);
         } else {
            appendTo.append(MessageFormat.format(monthPattern, new Object[]{array[value]}));
         }
      }

   }

   private Object[] getPatternItems() {
      if(this.patternItems != null) {
         return this.patternItems;
      } else {
         this.patternItems = (Object[])PARSED_PATTERN_CACHE.get(this.pattern);
         if(this.patternItems != null) {
            return this.patternItems;
         } else {
            boolean isPrevQuote = false;
            boolean inQuote = false;
            StringBuilder text = new StringBuilder();
            char itemType = 0;
            int itemLength = 1;
            List<Object> items = new ArrayList();

            for(int i = 0; i < this.pattern.length(); ++i) {
               char ch = this.pattern.charAt(i);
               if(ch == 39) {
                  if(isPrevQuote) {
                     text.append('\'');
                     isPrevQuote = false;
                  } else {
                     isPrevQuote = true;
                     if(itemType != 0) {
                        items.add(new SimpleDateFormat.PatternItem(itemType, itemLength));
                        itemType = 0;
                     }
                  }

                  inQuote = !inQuote;
               } else {
                  isPrevQuote = false;
                  if(inQuote) {
                     text.append(ch);
                  } else if(ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90) {
                     if(ch == itemType) {
                        ++itemLength;
                     } else {
                        if(itemType == 0) {
                           if(text.length() > 0) {
                              items.add(text.toString());
                              text.setLength(0);
                           }
                        } else {
                           items.add(new SimpleDateFormat.PatternItem(itemType, itemLength));
                        }

                        itemType = ch;
                        itemLength = 1;
                     }
                  } else {
                     if(itemType != 0) {
                        items.add(new SimpleDateFormat.PatternItem(itemType, itemLength));
                        itemType = 0;
                     }

                     text.append(ch);
                  }
               }
            }

            if(itemType == 0) {
               if(text.length() > 0) {
                  items.add(text.toString());
                  text.setLength(0);
               }
            } else {
               items.add(new SimpleDateFormat.PatternItem(itemType, itemLength));
            }

            this.patternItems = items.toArray(new Object[items.size()]);
            PARSED_PATTERN_CACHE.put(this.pattern, this.patternItems);
            return this.patternItems;
         }
      }
   }

   /** @deprecated */
   protected void zeroPaddingNumber(NumberFormat nf, StringBuffer buf, int value, int minDigits, int maxDigits) {
      if(this.useLocalZeroPaddingNumberFormat && value >= 0) {
         this.fastZeroPaddingNumber(buf, value, minDigits, maxDigits);
      } else {
         nf.setMinimumIntegerDigits(minDigits);
         nf.setMaximumIntegerDigits(maxDigits);
         nf.format((long)value, buf, new FieldPosition(-1));
      }

   }

   public void setNumberFormat(NumberFormat newNumberFormat) {
      super.setNumberFormat(newNumberFormat);
      this.initLocalZeroPaddingNumberFormat();
      this.initializeTimeZoneFormat(true);
   }

   private void initLocalZeroPaddingNumberFormat() {
      if(this.numberFormat instanceof DecimalFormat) {
         this.decDigits = ((DecimalFormat)this.numberFormat).getDecimalFormatSymbols().getDigits();
         this.useLocalZeroPaddingNumberFormat = true;
      } else if(this.numberFormat instanceof DateNumberFormat) {
         this.decDigits = ((DateNumberFormat)this.numberFormat).getDigits();
         this.useLocalZeroPaddingNumberFormat = true;
      } else {
         this.useLocalZeroPaddingNumberFormat = false;
      }

      if(this.useLocalZeroPaddingNumberFormat) {
         this.decimalBuf = new char[10];
      }

   }

   private void fastZeroPaddingNumber(StringBuffer buf, int value, int minDigits, int maxDigits) {
      int limit = this.decimalBuf.length < maxDigits?this.decimalBuf.length:maxDigits;
      int index = limit - 1;

      while(true) {
         this.decimalBuf[index] = this.decDigits[value % 10];
         value /= 10;
         if(index == 0 || value == 0) {
            int padding;
            for(padding = minDigits - (limit - index); padding > 0 && index > 0; --padding) {
               --index;
               this.decimalBuf[index] = this.decDigits[0];
            }

            while(padding > 0) {
               buf.append(this.decDigits[0]);
               --padding;
            }

            buf.append(this.decimalBuf, index, limit - index);
            return;
         }

         --index;
      }
   }

   protected String zeroPaddingNumber(long value, int minDigits, int maxDigits) {
      this.numberFormat.setMinimumIntegerDigits(minDigits);
      this.numberFormat.setMaximumIntegerDigits(maxDigits);
      return this.numberFormat.format(value);
   }

   private static final boolean isNumeric(char formatChar, int count) {
      int i = "MYyudehHmsSDFwWkK".indexOf(formatChar);
      return i > 0 || i == 0 && count < 3;
   }

   public void parse(String text, Calendar cal, ParsePosition parsePos) {
      TimeZone backupTZ = null;
      Calendar resultCal = null;
      if(cal != this.calendar && !cal.getType().equals(this.calendar.getType())) {
         this.calendar.setTimeInMillis(cal.getTimeInMillis());
         backupTZ = this.calendar.getTimeZone();
         this.calendar.setTimeZone(cal.getTimeZone());
         resultCal = cal;
         cal = this.calendar;
      }

      int pos = parsePos.getIndex();
      int start = pos;
      this.tztype = TimeZoneFormat.TimeType.UNKNOWN;
      boolean[] ambiguousYear = new boolean[]{false};
      int numericFieldStart = -1;
      int numericFieldLength = 0;
      int numericStartPos = 0;
      MessageFormat numericLeapMonthFormatter = null;
      if(this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7) {
         numericLeapMonthFormatter = new MessageFormat(this.formatData.leapMonthPatterns[6], this.locale);
      }

      Object[] items = this.getPatternItems();
      int i = 0;

      while(i < items.length) {
         if(items[i] instanceof SimpleDateFormat.PatternItem) {
            SimpleDateFormat.PatternItem field = (SimpleDateFormat.PatternItem)items[i];
            if(field.isNumeric && numericFieldStart == -1 && i + 1 < items.length && items[i + 1] instanceof SimpleDateFormat.PatternItem && ((SimpleDateFormat.PatternItem)items[i + 1]).isNumeric) {
               numericFieldStart = i;
               numericFieldLength = field.length;
               numericStartPos = pos;
            }

            if(numericFieldStart != -1) {
               int len = field.length;
               if(numericFieldStart == i) {
                  len = numericFieldLength;
               }

               pos = this.subParse(text, pos, field.type, len, true, false, ambiguousYear, cal, numericLeapMonthFormatter);
               if(pos < 0) {
                  --numericFieldLength;
                  if(numericFieldLength == 0) {
                     parsePos.setIndex(start);
                     parsePos.setErrorIndex(pos);
                     if(backupTZ != null) {
                        this.calendar.setTimeZone(backupTZ);
                     }

                     return;
                  }

                  i = numericFieldStart;
                  pos = numericStartPos;
                  continue;
               }
            } else if(field.type != 108) {
               numericFieldStart = -1;
               int s = pos;
               pos = this.subParse(text, pos, field.type, field.length, false, true, ambiguousYear, cal, numericLeapMonthFormatter);
               if(pos < 0) {
                  if(pos != -32000) {
                     parsePos.setIndex(start);
                     parsePos.setErrorIndex(s);
                     if(backupTZ != null) {
                        this.calendar.setTimeZone(backupTZ);
                     }

                     return;
                  }

                  pos = s;
                  if(i + 1 < items.length) {
                     String patl = null;

                     try {
                        patl = (String)items[i + 1];
                     } catch (ClassCastException var32) {
                        parsePos.setIndex(start);
                        parsePos.setErrorIndex(s);
                        if(backupTZ != null) {
                           this.calendar.setTimeZone(backupTZ);
                        }

                        return;
                     }

                     if(patl == null) {
                        patl = (String)items[i + 1];
                     }

                     int plen = patl.length();

                     int idx;
                     for(idx = 0; idx < plen; ++idx) {
                        char pch = patl.charAt(idx);
                        if(!PatternProps.isWhiteSpace(pch)) {
                           break;
                        }
                     }

                     if(idx == plen) {
                        ++i;
                     }
                  }
               }
            }
         } else {
            numericFieldStart = -1;
            boolean[] complete = new boolean[1];
            pos = this.matchLiteral(text, pos, items, i, complete);
            if(!complete[0]) {
               parsePos.setIndex(start);
               parsePos.setErrorIndex(pos);
               if(backupTZ != null) {
                  this.calendar.setTimeZone(backupTZ);
               }

               return;
            }
         }

         ++i;
      }

      if(pos < text.length()) {
         char extra = text.charAt(pos);
         if(extra == 46 && this.isLenient() && items.length != 0) {
            Object lastItem = items[items.length - 1];
            if(lastItem instanceof SimpleDateFormat.PatternItem && !((SimpleDateFormat.PatternItem)lastItem).isNumeric) {
               ++pos;
            }
         }
      }

      parsePos.setIndex(pos);

      try {
         if(ambiguousYear[0] || this.tztype != TimeZoneFormat.TimeType.UNKNOWN) {
            if(ambiguousYear[0]) {
               Calendar copy = (Calendar)cal.clone();
               Date parsedDate = copy.getTime();
               if(parsedDate.before(this.getDefaultCenturyStart())) {
                  cal.set(1, this.getDefaultCenturyStartYear() + 100);
               }
            }

            if(this.tztype != TimeZoneFormat.TimeType.UNKNOWN) {
               Calendar copy = (Calendar)cal.clone();
               TimeZone tz = copy.getTimeZone();
               BasicTimeZone btz = null;
               if(tz instanceof BasicTimeZone) {
                  btz = (BasicTimeZone)tz;
               }

               copy.set(15, 0);
               copy.set(16, 0);
               long localMillis = copy.getTimeInMillis();
               int[] offsets = new int[2];
               if(btz != null) {
                  if(this.tztype == TimeZoneFormat.TimeType.STANDARD) {
                     btz.getOffsetFromLocal(localMillis, 1, 1, offsets);
                  } else {
                     btz.getOffsetFromLocal(localMillis, 3, 3, offsets);
                  }
               } else {
                  tz.getOffset(localMillis, true, offsets);
                  if(this.tztype == TimeZoneFormat.TimeType.STANDARD && offsets[1] != 0 || this.tztype == TimeZoneFormat.TimeType.DAYLIGHT && offsets[1] == 0) {
                     tz.getOffset(localMillis - 86400000L, true, offsets);
                  }
               }

               int resolvedSavings = offsets[1];
               if(this.tztype == TimeZoneFormat.TimeType.STANDARD) {
                  if(offsets[1] != 0) {
                     resolvedSavings = 0;
                  }
               } else if(offsets[1] == 0) {
                  if(btz != null) {
                     long time = localMillis + (long)offsets[0];
                     long beforeT = time;
                     long afterT = time;
                     int beforeSav = 0;
                     int afterSav = 0;

                     TimeZoneTransition beforeTrs;
                     while(true) {
                        beforeTrs = btz.getPreviousTransition(beforeT, true);
                        if(beforeTrs == null) {
                           break;
                        }

                        beforeT = beforeTrs.getTime() - 1L;
                        beforeSav = beforeTrs.getFrom().getDSTSavings();
                        if(beforeSav != 0) {
                           break;
                        }
                     }

                     TimeZoneTransition afterTrs;
                     while(true) {
                        afterTrs = btz.getNextTransition(afterT, false);
                        if(afterTrs == null) {
                           break;
                        }

                        afterT = afterTrs.getTime();
                        afterSav = afterTrs.getTo().getDSTSavings();
                        if(afterSav != 0) {
                           break;
                        }
                     }

                     if(beforeTrs != null && afterTrs != null) {
                        if(time - beforeT > afterT - time) {
                           resolvedSavings = afterSav;
                        } else {
                           resolvedSavings = beforeSav;
                        }
                     } else if(beforeTrs != null && beforeSav != 0) {
                        resolvedSavings = beforeSav;
                     } else if(afterTrs != null && afterSav != 0) {
                        resolvedSavings = afterSav;
                     } else {
                        resolvedSavings = btz.getDSTSavings();
                     }
                  } else {
                     resolvedSavings = tz.getDSTSavings();
                  }

                  if(resolvedSavings == 0) {
                     resolvedSavings = 3600000;
                  }
               }

               cal.set(15, offsets[0]);
               cal.set(16, resolvedSavings);
            }
         }
      } catch (IllegalArgumentException var33) {
         parsePos.setErrorIndex(pos);
         parsePos.setIndex(start);
         if(backupTZ != null) {
            this.calendar.setTimeZone(backupTZ);
         }

         return;
      }

      if(resultCal != null) {
         resultCal.setTimeZone(cal.getTimeZone());
         resultCal.setTimeInMillis(cal.getTimeInMillis());
      }

      if(backupTZ != null) {
         this.calendar.setTimeZone(backupTZ);
      }

   }

   private int matchLiteral(String text, int pos, Object[] items, int itemIndex, boolean[] complete) {
      int originalPos = pos;
      String patternLiteral = (String)items[itemIndex];
      int plen = patternLiteral.length();
      int tlen = text.length();
      int idx = 0;

      while(idx < plen && pos < tlen) {
         char pch = patternLiteral.charAt(idx);
         char ich = text.charAt(pos);
         if(PatternProps.isWhiteSpace(pch) && PatternProps.isWhiteSpace(ich)) {
            while(idx + 1 < plen && PatternProps.isWhiteSpace(patternLiteral.charAt(idx + 1))) {
               ++idx;
            }

            while(pos + 1 < tlen && PatternProps.isWhiteSpace(text.charAt(pos + 1))) {
               ++pos;
            }
         } else if(pch != ich) {
            if(ich != 46 || pos != originalPos || 0 >= itemIndex || !this.isLenient()) {
               break;
            }

            Object before = items[itemIndex - 1];
            if(!(before instanceof SimpleDateFormat.PatternItem)) {
               break;
            }

            boolean isNumeric = ((SimpleDateFormat.PatternItem)before).isNumeric;
            if(isNumeric) {
               break;
            }

            ++pos;
            continue;
         }

         ++idx;
         ++pos;
      }

      complete[0] = idx == plen;
      if(!complete[0] && this.isLenient() && 0 < itemIndex && itemIndex < items.length - 1 && originalPos < tlen) {
         Object before = items[itemIndex - 1];
         Object after = items[itemIndex + 1];
         if(before instanceof SimpleDateFormat.PatternItem && after instanceof SimpleDateFormat.PatternItem) {
            char beforeType = ((SimpleDateFormat.PatternItem)before).type;
            char afterType = ((SimpleDateFormat.PatternItem)after).type;
            if(DATE_PATTERN_TYPE.contains(beforeType) != DATE_PATTERN_TYPE.contains(afterType)) {
               int newPos = originalPos;

               while(true) {
                  char ich = text.charAt(newPos);
                  if(!PatternProps.isWhiteSpace(ich)) {
                     complete[0] = newPos > originalPos;
                     pos = newPos;
                     break;
                  }

                  ++newPos;
               }
            }
         }
      }

      return pos;
   }

   protected int matchString(String text, int start, int field, String[] data, Calendar cal) {
      return this.matchString(text, start, field, data, (String)null, cal);
   }

   protected int matchString(String text, int start, int field, String[] data, String monthPattern, Calendar cal) {
      int i = 0;
      int count = data.length;
      if(field == 7) {
         i = 1;
      }

      int bestMatchLength = 0;
      int bestMatch = -1;
      int isLeapMonth = 0;

      for(int matchLength = 0; i < count; ++i) {
         int length = data[i].length();
         if(length > bestMatchLength && (matchLength = this.regionMatchesWithOptionalDot(text, start, data[i], length)) >= 0) {
            bestMatch = i;
            bestMatchLength = matchLength;
            isLeapMonth = 0;
         }

         if(monthPattern != null) {
            String leapMonthName = MessageFormat.format(monthPattern, new Object[]{data[i]});
            length = leapMonthName.length();
            if(length > bestMatchLength && (matchLength = this.regionMatchesWithOptionalDot(text, start, leapMonthName, length)) >= 0) {
               bestMatch = i;
               bestMatchLength = matchLength;
               isLeapMonth = 1;
            }
         }
      }

      if(bestMatch >= 0) {
         if(field == 1) {
            ++bestMatch;
         }

         cal.set(field, bestMatch);
         if(monthPattern != null) {
            cal.set(22, isLeapMonth);
         }

         return start + bestMatchLength;
      } else {
         return -start;
      }
   }

   private int regionMatchesWithOptionalDot(String text, int start, String data, int length) {
      boolean matches = text.regionMatches(true, start, data, 0, length);
      return matches?length:(data.length() > 0 && data.charAt(data.length() - 1) == 46 && text.regionMatches(true, start, data, 0, length - 1)?length - 1:-1);
   }

   protected int matchQuarterString(String text, int start, int field, String[] data, Calendar cal) {
      int i = 0;
      int count = data.length;
      int bestMatchLength = 0;
      int bestMatch = -1;

      for(int matchLength = 0; i < count; ++i) {
         int length = data[i].length();
         if(length > bestMatchLength && (matchLength = this.regionMatchesWithOptionalDot(text, start, data[i], length)) >= 0) {
            bestMatch = i;
            bestMatchLength = matchLength;
         }
      }

      if(bestMatch >= 0) {
         cal.set(field, bestMatch * 3);
         return start + bestMatchLength;
      } else {
         return -start;
      }
   }

   protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal) {
      return this.subParse(text, start, ch, count, obeyCount, allowNegative, ambiguousYear, cal, (MessageFormat)null);
   }

   protected int subParse(String text, int start, char ch, int count, boolean obeyCount, boolean allowNegative, boolean[] ambiguousYear, Calendar cal, MessageFormat numericLeapMonthFormatter) {
      Number number = null;
      NumberFormat currentNumberFormat = null;
      int value = 0;
      ParsePosition pos = new ParsePosition(0);
      boolean lenient = this.isLenient();
      int patternCharIndex = -1;
      if(65 <= ch && ch <= 122) {
         patternCharIndex = PATTERN_CHAR_TO_INDEX[ch - 64];
      }

      if(patternCharIndex == -1) {
         return -start;
      } else {
         currentNumberFormat = this.getNumberFormat(ch);
         int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
         if(numericLeapMonthFormatter != null) {
            numericLeapMonthFormatter.setFormatByArgumentIndex(0, currentNumberFormat);
         }

         while(start < text.length()) {
            int c = UTF16.charAt(text, start);
            if(!UCharacter.isUWhiteSpace(c) || !PatternProps.isWhiteSpace(c)) {
               pos.setIndex(start);
               if(patternCharIndex == 4 || patternCharIndex == 15 || patternCharIndex == 2 && count <= 2 || patternCharIndex == 26 && count <= 2 || patternCharIndex == 1 || patternCharIndex == 18 || patternCharIndex == 30 || patternCharIndex == 0 && cal.getType().equals("chinese") || patternCharIndex == 8) {
                  c = 0;
                  if(numericLeapMonthFormatter != null && (patternCharIndex == 2 || patternCharIndex == 26)) {
                     Object[] args = numericLeapMonthFormatter.parse(text, pos);
                     if(args != null && pos.getIndex() > start && args[0] instanceof Number) {
                        c = 1;
                        number = (Number)args[0];
                        cal.set(22, 1);
                     } else {
                        pos.setIndex(start);
                        cal.set(22, 0);
                     }
                  }

                  if(!c) {
                     if(obeyCount) {
                        if(start + count > text.length()) {
                           return -start;
                        }

                        number = this.parseInt(text, count, pos, allowNegative, currentNumberFormat);
                     } else {
                        number = this.parseInt(text, pos, allowNegative, currentNumberFormat);
                     }

                     if(number == null && patternCharIndex != 30) {
                        return -start;
                     }
                  }

                  if(number != null) {
                     value = number.intValue();
                  }
               }

               switch(patternCharIndex) {
               case 0:
                  if(cal.getType().equals("chinese")) {
                     cal.set(0, value);
                     return pos.getIndex();
                  }

                  c = 0;
                  if(count == 5) {
                     c = this.matchString(text, start, 0, this.formatData.narrowEras, (String)null, cal);
                  } else if(count == 4) {
                     c = this.matchString(text, start, 0, this.formatData.eraNames, (String)null, cal);
                  } else {
                     c = this.matchString(text, start, 0, this.formatData.eras, (String)null, cal);
                  }

                  if(c == -start) {
                     c = -32000;
                  }

                  return c;
               case 1:
               case 18:
                  if(this.override != null && (this.override.compareTo("hebr") == 0 || this.override.indexOf("y=hebr") >= 0) && value < 1000) {
                     value += 5000;
                  } else if(count == 2 && pos.getIndex() - start == 2 && !cal.getType().equals("chinese") && UCharacter.isDigit(text.charAt(start)) && UCharacter.isDigit(text.charAt(start + 1))) {
                     int ambiguousTwoDigitYear = this.getDefaultCenturyStartYear() % 100;
                     ambiguousYear[0] = value == ambiguousTwoDigitYear;
                     value += this.getDefaultCenturyStartYear() / 100 * 100 + (value < ambiguousTwoDigitYear?100:0);
                  }

                  cal.set(field, value);
                  if(DelayedHebrewMonthCheck) {
                     if(!HebrewCalendar.isLeapYear(value)) {
                        cal.add(2, 1);
                     }

                     DelayedHebrewMonthCheck = false;
                  }

                  return pos.getIndex();
               case 2:
               case 26:
                  if(count <= 2) {
                     cal.set(2, value - 1);
                     if(cal.getType().equals("hebrew") && value >= 6) {
                        if(cal.isSet(1)) {
                           if(!HebrewCalendar.isLeapYear(cal.get(1))) {
                              cal.set(2, value);
                           }
                        } else {
                           DelayedHebrewMonthCheck = true;
                        }
                     }

                     return pos.getIndex();
                  }

                  boolean haveMonthPat = this.formatData.leapMonthPatterns != null && this.formatData.leapMonthPatterns.length >= 7;
                  int newStart = patternCharIndex == 2?this.matchString(text, start, 2, this.formatData.months, haveMonthPat?this.formatData.leapMonthPatterns[0]:null, cal):this.matchString(text, start, 2, this.formatData.standaloneMonths, haveMonthPat?this.formatData.leapMonthPatterns[3]:null, cal);
                  if(newStart > 0) {
                     return newStart;
                  }

                  return patternCharIndex == 2?this.matchString(text, start, 2, this.formatData.shortMonths, haveMonthPat?this.formatData.leapMonthPatterns[1]:null, cal):this.matchString(text, start, 2, this.formatData.standaloneShortMonths, haveMonthPat?this.formatData.leapMonthPatterns[4]:null, cal);
               case 3:
               case 5:
               case 6:
               case 7:
               case 10:
               case 11:
               case 12:
               case 13:
               case 16:
               case 19:
               case 20:
               case 21:
               case 22:
               default:
                  if(obeyCount) {
                     if(start + count > text.length()) {
                        return -start;
                     }

                     number = this.parseInt(text, count, pos, allowNegative, currentNumberFormat);
                  } else {
                     number = this.parseInt(text, pos, allowNegative, currentNumberFormat);
                  }

                  if(number != null) {
                     cal.set(field, number.intValue());
                     return pos.getIndex();
                  }

                  return -start;
               case 4:
                  if(value == cal.getMaximum(11) + 1) {
                     value = 0;
                  }

                  cal.set(11, value);
                  return pos.getIndex();
               case 8:
                  int i = pos.getIndex() - start;
                  if(i < 3) {
                     while(i < 3) {
                        value *= 10;
                        ++i;
                     }
                  } else {
                     int a;
                     for(a = 1; i > 3; --i) {
                        a *= 10;
                     }

                     value /= a;
                  }

                  cal.set(14, value);
                  return pos.getIndex();
               case 9:
                  int newStart = this.matchString(text, start, 7, this.formatData.weekdays, (String)null, cal);
                  if(newStart > 0) {
                     return newStart;
                  }

                  if((newStart = this.matchString(text, start, 7, this.formatData.shortWeekdays, (String)null, cal)) > 0) {
                     return newStart;
                  }

                  if(this.formatData.shorterWeekdays != null) {
                     return this.matchString(text, start, 7, this.formatData.shorterWeekdays, (String)null, cal);
                  }

                  return newStart;
               case 14:
                  return this.matchString(text, start, 9, this.formatData.ampms, (String)null, cal);
               case 15:
                  if(value == cal.getLeastMaximum(10) + 1) {
                     value = 0;
                  }

                  cal.set(10, value);
                  return pos.getIndex();
               case 17:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style = count < 4?TimeZoneFormat.Style.SPECIFIC_SHORT:TimeZoneFormat.Style.SPECIFIC_LONG;
                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 23:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style = count < 4?TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL:(count == 5?TimeZoneFormat.Style.ISO_EXTENDED_FULL:TimeZoneFormat.Style.LOCALIZED_GMT);
                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 24:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style = count < 4?TimeZoneFormat.Style.GENERIC_SHORT:TimeZoneFormat.Style.GENERIC_LONG;
                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 25:
                  int newStart = this.matchString(text, start, 7, this.formatData.standaloneWeekdays, (String)null, cal);
                  if(newStart > 0) {
                     return newStart;
                  }

                  if((newStart = this.matchString(text, start, 7, this.formatData.standaloneShortWeekdays, (String)null, cal)) > 0) {
                     return newStart;
                  }

                  if(this.formatData.standaloneShorterWeekdays != null) {
                     return this.matchString(text, start, 7, this.formatData.standaloneShorterWeekdays, (String)null, cal);
                  }

                  return newStart;
               case 27:
                  if(count <= 2) {
                     cal.set(2, (value - 1) * 3);
                     return pos.getIndex();
                  }

                  int newStart = this.matchQuarterString(text, start, 2, this.formatData.quarters, cal);
                  if(newStart > 0) {
                     return newStart;
                  }

                  return this.matchQuarterString(text, start, 2, this.formatData.shortQuarters, cal);
               case 28:
                  if(count <= 2) {
                     cal.set(2, (value - 1) * 3);
                     return pos.getIndex();
                  }

                  int newStart = this.matchQuarterString(text, start, 2, this.formatData.standaloneQuarters, cal);
                  if(newStart > 0) {
                     return newStart;
                  }

                  return this.matchQuarterString(text, start, 2, this.formatData.standaloneShortQuarters, cal);
               case 29:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style = null;
                  switch(count) {
                  case 1:
                     style = TimeZoneFormat.Style.ZONE_ID_SHORT;
                     break;
                  case 2:
                     style = TimeZoneFormat.Style.ZONE_ID;
                     break;
                  case 3:
                     style = TimeZoneFormat.Style.EXEMPLAR_LOCATION;
                     break;
                  default:
                     style = TimeZoneFormat.Style.GENERIC_LOCATION;
                  }

                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 30:
                  if(this.formatData.shortYearNames != null) {
                     int newStart = this.matchString(text, start, 1, this.formatData.shortYearNames, (String)null, cal);
                     if(newStart > 0) {
                        return newStart;
                     }
                  }

                  if(number == null || !lenient && this.formatData.shortYearNames != null && value <= this.formatData.shortYearNames.length) {
                     return -start;
                  }

                  cal.set(1, value);
                  return pos.getIndex();
               case 31:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style = count < 4?TimeZoneFormat.Style.LOCALIZED_GMT_SHORT:TimeZoneFormat.Style.LOCALIZED_GMT;
                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 32:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style;
                  switch(count) {
                  case 1:
                     style = TimeZoneFormat.Style.ISO_BASIC_SHORT;
                     break;
                  case 2:
                     style = TimeZoneFormat.Style.ISO_BASIC_FIXED;
                     break;
                  case 3:
                     style = TimeZoneFormat.Style.ISO_EXTENDED_FIXED;
                     break;
                  case 4:
                     style = TimeZoneFormat.Style.ISO_BASIC_FULL;
                     break;
                  default:
                     style = TimeZoneFormat.Style.ISO_EXTENDED_FULL;
                  }

                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               case 33:
                  Output<TimeZoneFormat.TimeType> tzTimeType = new Output();
                  TimeZoneFormat.Style style;
                  switch(count) {
                  case 1:
                     style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_SHORT;
                     break;
                  case 2:
                     style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FIXED;
                     break;
                  case 3:
                     style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FIXED;
                     break;
                  case 4:
                     style = TimeZoneFormat.Style.ISO_BASIC_LOCAL_FULL;
                     break;
                  default:
                     style = TimeZoneFormat.Style.ISO_EXTENDED_LOCAL_FULL;
                  }

                  TimeZone tz = this.tzFormat().parse(style, text, pos, tzTimeType);
                  if(tz != null) {
                     this.tztype = (TimeZoneFormat.TimeType)tzTimeType.value;
                     cal.setTimeZone(tz);
                     return pos.getIndex();
                  }

                  return -start;
               }
            }

            start += UTF16.getCharCount(c);
         }

         return -start;
      }
   }

   private Number parseInt(String text, ParsePosition pos, boolean allowNegative, NumberFormat fmt) {
      return this.parseInt(text, -1, pos, allowNegative, fmt);
   }

   private Number parseInt(String text, int maxDigits, ParsePosition pos, boolean allowNegative, NumberFormat fmt) {
      int oldPos = pos.getIndex();
      Number number;
      if(allowNegative) {
         number = fmt.parse(text, pos);
      } else if(fmt instanceof DecimalFormat) {
         String oldPrefix = ((DecimalFormat)fmt).getNegativePrefix();
         ((DecimalFormat)fmt).setNegativePrefix("\uab00");
         number = fmt.parse(text, pos);
         ((DecimalFormat)fmt).setNegativePrefix(oldPrefix);
      } else {
         boolean dateNumberFormat = fmt instanceof DateNumberFormat;
         if(dateNumberFormat) {
            ((DateNumberFormat)fmt).setParsePositiveOnly(true);
         }

         number = fmt.parse(text, pos);
         if(dateNumberFormat) {
            ((DateNumberFormat)fmt).setParsePositiveOnly(false);
         }
      }

      if(maxDigits > 0) {
         int nDigits = pos.getIndex() - oldPos;
         if(nDigits > maxDigits) {
            double val = number.doubleValue();

            for(nDigits = nDigits - maxDigits; nDigits > 0; --nDigits) {
               val /= 10.0D;
            }

            pos.setIndex(oldPos + maxDigits);
            number = Integer.valueOf((int)val);
         }
      }

      return number;
   }

   private String translatePattern(String pat, String from, String to) {
      StringBuilder result = new StringBuilder();
      boolean inQuote = false;

      for(int i = 0; i < pat.length(); ++i) {
         char c = pat.charAt(i);
         if(inQuote) {
            if(c == 39) {
               inQuote = false;
            }
         } else if(c == 39) {
            inQuote = true;
         } else if(c >= 97 && c <= 122 || c >= 65 && c <= 90) {
            int ci = from.indexOf(c);
            if(ci != -1) {
               c = to.charAt(ci);
            }
         }

         result.append(c);
      }

      if(inQuote) {
         throw new IllegalArgumentException("Unfinished quote in pattern");
      } else {
         return result.toString();
      }
   }

   public String toPattern() {
      return this.pattern;
   }

   public String toLocalizedPattern() {
      return this.translatePattern(this.pattern, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx", this.formatData.localPatternChars);
   }

   public void applyPattern(String pat) {
      this.pattern = pat;
      this.setLocale((ULocale)null, (ULocale)null);
      this.patternItems = null;
   }

   public void applyLocalizedPattern(String pat) {
      this.pattern = this.translatePattern(pat, this.formatData.localPatternChars, "GyMdkHmsSEDFwWahKzYeugAZvcLQqVUOXx");
      this.setLocale((ULocale)null, (ULocale)null);
   }

   public DateFormatSymbols getDateFormatSymbols() {
      return (DateFormatSymbols)this.formatData.clone();
   }

   public void setDateFormatSymbols(DateFormatSymbols newFormatSymbols) {
      this.formatData = (DateFormatSymbols)newFormatSymbols.clone();
   }

   protected DateFormatSymbols getSymbols() {
      return this.formatData;
   }

   public TimeZoneFormat getTimeZoneFormat() {
      return this.tzFormat().freeze();
   }

   public void setTimeZoneFormat(TimeZoneFormat tzfmt) {
      if(tzfmt.isFrozen()) {
         this.tzFormat = tzfmt;
      } else {
         this.tzFormat = tzfmt.cloneAsThawed().freeze();
      }

   }

   public void setContext(DisplayContext context) {
      if(context.type() == DisplayContext.Type.CAPITALIZATION) {
         this.capitalizationSetting = context;
      }

   }

   public DisplayContext getContext(DisplayContext.Type type) {
      return type == DisplayContext.Type.CAPITALIZATION && this.capitalizationSetting != null?this.capitalizationSetting:DisplayContext.CAPITALIZATION_NONE;
   }

   public Object clone() {
      SimpleDateFormat other = (SimpleDateFormat)super.clone();
      other.formatData = (DateFormatSymbols)this.formatData.clone();
      return other;
   }

   public int hashCode() {
      return this.pattern.hashCode();
   }

   public boolean equals(Object obj) {
      if(!super.equals(obj)) {
         return false;
      } else {
         SimpleDateFormat that = (SimpleDateFormat)obj;
         return this.pattern.equals(that.pattern) && this.formatData.equals(that.formatData);
      }
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      if(this.defaultCenturyStart == null) {
         this.initializeDefaultCenturyStart(this.defaultCenturyBase);
      }

      this.initializeTimeZoneFormat(false);
      stream.defaultWriteObject();
      stream.writeInt(this.capitalizationSetting.value());
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      int capitalizationSettingValue = this.serialVersionOnStream > 1?stream.readInt():-1;
      if(this.serialVersionOnStream < 1) {
         this.defaultCenturyBase = System.currentTimeMillis();
      } else {
         this.parseAmbiguousDatesAsAfter(this.defaultCenturyStart);
      }

      this.serialVersionOnStream = 2;
      this.locale = this.getLocale(ULocale.VALID_LOCALE);
      if(this.locale == null) {
         this.locale = ULocale.getDefault(ULocale.Category.FORMAT);
      }

      this.initLocalZeroPaddingNumberFormat();
      this.capitalizationSetting = DisplayContext.CAPITALIZATION_NONE;
      if(capitalizationSettingValue >= 0) {
         for(DisplayContext context : DisplayContext.values()) {
            if(context.value() == capitalizationSettingValue) {
               this.capitalizationSetting = context;
               break;
            }
         }
      }

   }

   public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
      Calendar cal = this.calendar;
      if(obj instanceof Calendar) {
         cal = (Calendar)obj;
      } else if(obj instanceof Date) {
         this.calendar.setTime((Date)obj);
      } else {
         if(!(obj instanceof Number)) {
            throw new IllegalArgumentException("Cannot format given Object as a Date");
         }

         this.calendar.setTimeInMillis(((Number)obj).longValue());
      }

      StringBuffer toAppendTo = new StringBuffer();
      FieldPosition pos = new FieldPosition(0);
      List<FieldPosition> attributes = new ArrayList();
      this.format(cal, this.capitalizationSetting, toAppendTo, pos, attributes);
      AttributedString as = new AttributedString(toAppendTo.toString());

      for(int i = 0; i < ((List)attributes).size(); ++i) {
         FieldPosition fp = (FieldPosition)attributes.get(i);
         Field attribute = fp.getFieldAttribute();
         as.addAttribute(attribute, attribute, fp.getBeginIndex(), fp.getEndIndex());
      }

      return as.getIterator();
   }

   ULocale getLocale() {
      return this.locale;
   }

   boolean isFieldUnitIgnored(int field) {
      return isFieldUnitIgnored(this.pattern, field);
   }

   static boolean isFieldUnitIgnored(String pattern, int field) {
      int fieldLevel = CALENDAR_FIELD_TO_LEVEL[field];
      boolean inQuote = false;
      char prevCh = 0;
      int count = 0;

      for(int i = 0; i < pattern.length(); ++i) {
         char ch = pattern.charAt(i);
         if(ch != prevCh && count > 0) {
            int level = PATTERN_CHAR_TO_LEVEL[prevCh - 64];
            if(fieldLevel <= level) {
               return false;
            }

            count = 0;
         }

         if(ch == 39) {
            if(i + 1 < pattern.length() && pattern.charAt(i + 1) == 39) {
               ++i;
            } else {
               inQuote = !inQuote;
            }
         } else if(!inQuote && (ch >= 97 && ch <= 122 || ch >= 65 && ch <= 90)) {
            prevCh = ch;
            ++count;
         }
      }

      if(count > 0) {
         int level = PATTERN_CHAR_TO_LEVEL[prevCh - 64];
         if(fieldLevel <= level) {
            return false;
         }
      }

      return true;
   }

   /** @deprecated */
   public final StringBuffer intervalFormatByAlgorithm(Calendar fromCalendar, Calendar toCalendar, StringBuffer appendTo, FieldPosition pos) throws IllegalArgumentException {
      if(!fromCalendar.isEquivalentTo(toCalendar)) {
         throw new IllegalArgumentException("can not format on two different calendars");
      } else {
         Object[] items = this.getPatternItems();
         int diffBegin = -1;
         int diffEnd = -1;

         try {
            for(int i = 0; i < items.length; ++i) {
               if(this.diffCalFieldValue(fromCalendar, toCalendar, items, i)) {
                  diffBegin = i;
                  break;
               }
            }

            if(diffBegin == -1) {
               return this.format(fromCalendar, appendTo, pos);
            }

            for(int i = items.length - 1; i >= diffBegin; --i) {
               if(this.diffCalFieldValue(fromCalendar, toCalendar, items, i)) {
                  diffEnd = i;
                  break;
               }
            }
         } catch (IllegalArgumentException var14) {
            throw new IllegalArgumentException(var14.toString());
         }

         if(diffBegin == 0 && diffEnd == items.length - 1) {
            this.format(fromCalendar, appendTo, pos);
            appendTo.append(" – ");
            this.format(toCalendar, appendTo, pos);
            return appendTo;
         } else {
            int highestLevel = 1000;

            for(int i = diffBegin; i <= diffEnd; ++i) {
               if(!(items[i] instanceof String)) {
                  SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
                  char ch = item.type;
                  int patternCharIndex = -1;
                  if(65 <= ch && ch <= 122) {
                     patternCharIndex = PATTERN_CHAR_TO_LEVEL[ch - 64];
                  }

                  if(patternCharIndex == -1) {
                     throw new IllegalArgumentException("Illegal pattern character \'" + ch + "\' in \"" + this.pattern + '\"');
                  }

                  if(patternCharIndex < highestLevel) {
                     highestLevel = patternCharIndex;
                  }
               }
            }

            try {
               for(int i = 0; i < diffBegin; ++i) {
                  if(this.lowerLevel(items, i, highestLevel)) {
                     diffBegin = i;
                     break;
                  }
               }

               for(int i = items.length - 1; i > diffEnd; --i) {
                  if(this.lowerLevel(items, i, highestLevel)) {
                     diffEnd = i;
                     break;
                  }
               }
            } catch (IllegalArgumentException var13) {
               throw new IllegalArgumentException(var13.toString());
            }

            if(diffBegin == 0 && diffEnd == items.length - 1) {
               this.format(fromCalendar, appendTo, pos);
               appendTo.append(" – ");
               this.format(toCalendar, appendTo, pos);
               return appendTo;
            } else {
               pos.setBeginIndex(0);
               pos.setEndIndex(0);

               for(int i = 0; i <= diffEnd; ++i) {
                  if(items[i] instanceof String) {
                     appendTo.append((String)items[i]);
                  } else {
                     SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
                     if(this.useFastFormat) {
                        this.subFormat(appendTo, item.type, item.length, appendTo.length(), i, this.capitalizationSetting, pos, fromCalendar);
                     } else {
                        appendTo.append(this.subFormat(item.type, item.length, appendTo.length(), i, this.capitalizationSetting, pos, fromCalendar));
                     }
                  }
               }

               appendTo.append(" – ");

               for(int i = diffBegin; i < items.length; ++i) {
                  if(items[i] instanceof String) {
                     appendTo.append((String)items[i]);
                  } else {
                     SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
                     if(this.useFastFormat) {
                        this.subFormat(appendTo, item.type, item.length, appendTo.length(), i, this.capitalizationSetting, pos, toCalendar);
                     } else {
                        appendTo.append(this.subFormat(item.type, item.length, appendTo.length(), i, this.capitalizationSetting, pos, toCalendar));
                     }
                  }
               }

               return appendTo;
            }
         }
      }
   }

   private boolean diffCalFieldValue(Calendar fromCalendar, Calendar toCalendar, Object[] items, int i) throws IllegalArgumentException {
      if(items[i] instanceof String) {
         return false;
      } else {
         SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
         char ch = item.type;
         int patternCharIndex = -1;
         if(65 <= ch && ch <= 122) {
            patternCharIndex = PATTERN_CHAR_TO_INDEX[ch - 64];
         }

         if(patternCharIndex == -1) {
            throw new IllegalArgumentException("Illegal pattern character \'" + ch + "\' in \"" + this.pattern + '\"');
         } else {
            int field = PATTERN_INDEX_TO_CALENDAR_FIELD[patternCharIndex];
            int value = fromCalendar.get(field);
            int value_2 = toCalendar.get(field);
            return value != value_2;
         }
      }
   }

   private boolean lowerLevel(Object[] items, int i, int level) throws IllegalArgumentException {
      if(items[i] instanceof String) {
         return false;
      } else {
         SimpleDateFormat.PatternItem item = (SimpleDateFormat.PatternItem)items[i];
         char ch = item.type;
         int patternCharIndex = -1;
         if(65 <= ch && ch <= 122) {
            patternCharIndex = PATTERN_CHAR_TO_LEVEL[ch - 64];
         }

         if(patternCharIndex == -1) {
            throw new IllegalArgumentException("Illegal pattern character \'" + ch + "\' in \"" + this.pattern + '\"');
         } else {
            return patternCharIndex >= level;
         }
      }
   }

   /** @deprecated */
   protected NumberFormat getNumberFormat(char ch) {
      Character ovrField = Character.valueOf(ch);
      if(this.overrideMap != null && this.overrideMap.containsKey(ovrField)) {
         String nsName = ((String)this.overrideMap.get(ovrField)).toString();
         NumberFormat nf = (NumberFormat)this.numberFormatters.get(nsName);
         return nf;
      } else {
         return this.numberFormat;
      }
   }

   private void initNumberFormatters(ULocale loc) {
      this.numberFormatters = new HashMap();
      this.overrideMap = new HashMap();
      this.processOverrideString(loc, this.override);
   }

   private void processOverrideString(ULocale loc, String str) {
      if(str != null && str.length() != 0) {
         int start = 0;

         int delimiterPosition;
         for(boolean moreToProcess = true; moreToProcess; start = delimiterPosition + 1) {
            delimiterPosition = str.indexOf(";", start);
            int end;
            if(delimiterPosition == -1) {
               moreToProcess = false;
               end = str.length();
            } else {
               end = delimiterPosition;
            }

            String currentString = str.substring(start, end);
            int equalSignPosition = currentString.indexOf("=");
            String nsName;
            boolean fullOverride;
            if(equalSignPosition == -1) {
               nsName = currentString;
               fullOverride = true;
            } else {
               nsName = currentString.substring(equalSignPosition + 1);
               Character ovrField = Character.valueOf(currentString.charAt(0));
               this.overrideMap.put(ovrField, nsName);
               fullOverride = false;
            }

            ULocale ovrLoc = new ULocale(loc.getBaseName() + "@numbers=" + nsName);
            NumberFormat nf = NumberFormat.createInstance(ovrLoc, 0);
            nf.setGroupingUsed(false);
            if(fullOverride) {
               this.setNumberFormat(nf);
            } else {
               this.useLocalZeroPaddingNumberFormat = false;
            }

            if(!this.numberFormatters.containsKey(nsName)) {
               this.numberFormatters.put(nsName, nf);
            }
         }

      }
   }

   private static enum ContextValue {
      UNKNOWN,
      CAPITALIZATION_FOR_MIDDLE_OF_SENTENCE,
      CAPITALIZATION_FOR_BEGINNING_OF_SENTENCE,
      CAPITALIZATION_FOR_UI_LIST_OR_MENU,
      CAPITALIZATION_FOR_STANDALONE;
   }

   private static class PatternItem {
      final char type;
      final int length;
      final boolean isNumeric;

      PatternItem(char type, int length) {
         this.type = type;
         this.length = length;
         this.isNumeric = SimpleDateFormat.isNumeric(type, length);
      }
   }
}
