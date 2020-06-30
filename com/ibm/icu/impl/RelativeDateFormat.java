package com.ibm.icu.impl;

import com.ibm.icu.impl.CalendarData;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.text.MessageFormat;
import com.ibm.icu.text.SimpleDateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import com.ibm.icu.util.UResourceBundleIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Comparator;
import java.util.Date;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

public class RelativeDateFormat extends DateFormat {
   private static final long serialVersionUID = 1131984966440549435L;
   private DateFormat fDateFormat;
   private DateFormat fTimeFormat;
   private MessageFormat fCombinedFormat;
   private SimpleDateFormat fDateTimeFormat = null;
   private String fDatePattern = null;
   private String fTimePattern = null;
   int fDateStyle;
   int fTimeStyle;
   ULocale fLocale;
   private transient RelativeDateFormat.URelativeString[] fDates = null;

   public RelativeDateFormat(int timeStyle, int dateStyle, ULocale locale) {
      this.fLocale = locale;
      this.fTimeStyle = timeStyle;
      this.fDateStyle = dateStyle;
      if(this.fDateStyle != -1) {
         int newStyle = this.fDateStyle & -129;
         DateFormat df = DateFormat.getDateInstance(newStyle, locale);
         if(!(df instanceof SimpleDateFormat)) {
            throw new IllegalArgumentException("Can\'t create SimpleDateFormat for date style");
         }

         this.fDateTimeFormat = (SimpleDateFormat)df;
         this.fDatePattern = this.fDateTimeFormat.toPattern();
         if(this.fTimeStyle != -1) {
            newStyle = this.fTimeStyle & -129;
            df = DateFormat.getTimeInstance(newStyle, locale);
            if(df instanceof SimpleDateFormat) {
               this.fTimePattern = ((SimpleDateFormat)df).toPattern();
            }
         }
      } else {
         int newStyle = this.fTimeStyle & -129;
         DateFormat df = DateFormat.getTimeInstance(newStyle, locale);
         if(!(df instanceof SimpleDateFormat)) {
            throw new IllegalArgumentException("Can\'t create SimpleDateFormat for time style");
         }

         this.fDateTimeFormat = (SimpleDateFormat)df;
         this.fTimePattern = this.fDateTimeFormat.toPattern();
      }

      this.initializeCalendar((TimeZone)null, this.fLocale);
      this.loadDates();
      this.initializeCombinedFormat(this.calendar, this.fLocale);
   }

   public StringBuffer format(Calendar cal, StringBuffer toAppendTo, FieldPosition fieldPosition) {
      String relativeDayString = null;
      if(this.fDateStyle != -1) {
         int dayDiff = dayDifference(cal);
         relativeDayString = this.getStringForDay(dayDiff);
      }

      if(this.fDateTimeFormat != null && (this.fDatePattern != null || this.fTimePattern != null)) {
         if(this.fDatePattern == null) {
            this.fDateTimeFormat.applyPattern(this.fTimePattern);
            this.fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
         } else if(this.fTimePattern == null) {
            if(relativeDayString != null) {
               toAppendTo.append(relativeDayString);
            } else {
               this.fDateTimeFormat.applyPattern(this.fDatePattern);
               this.fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
            }
         } else {
            String datePattern = this.fDatePattern;
            if(relativeDayString != null) {
               datePattern = "\'" + relativeDayString.replace("\'", "\'\'") + "\'";
            }

            StringBuffer combinedPattern = new StringBuffer("");
            this.fCombinedFormat.format(new Object[]{this.fTimePattern, datePattern}, combinedPattern, new FieldPosition(0));
            this.fDateTimeFormat.applyPattern(combinedPattern.toString());
            this.fDateTimeFormat.format(cal, toAppendTo, fieldPosition);
         }
      } else if(this.fDateFormat != null) {
         if(relativeDayString != null) {
            toAppendTo.append(relativeDayString);
         } else {
            this.fDateFormat.format(cal, toAppendTo, fieldPosition);
         }
      }

      return toAppendTo;
   }

   public void parse(String text, Calendar cal, ParsePosition pos) {
      throw new UnsupportedOperationException("Relative Date parse is not implemented yet");
   }

   private String getStringForDay(int day) {
      if(this.fDates == null) {
         this.loadDates();
      }

      for(int i = 0; i < this.fDates.length; ++i) {
         if(this.fDates[i].offset == day) {
            return this.fDates[i].string;
         }
      }

      return null;
   }

   private synchronized void loadDates() {
      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", this.fLocale);
      ICUResourceBundle rdb = rb.getWithFallback("fields/day/relative");
      Set<RelativeDateFormat.URelativeString> datesSet = new TreeSet(new Comparator() {
         public int compare(RelativeDateFormat.URelativeString r1, RelativeDateFormat.URelativeString r2) {
            return r1.offset == r2.offset?0:(r1.offset < r2.offset?-1:1);
         }
      });
      UResourceBundleIterator i = rdb.getIterator();

      while(i.hasNext()) {
         UResourceBundle line = i.next();
         String k = line.getKey();
         String v = line.getString();
         RelativeDateFormat.URelativeString rs = new RelativeDateFormat.URelativeString(k, v);
         datesSet.add(rs);
      }

      this.fDates = (RelativeDateFormat.URelativeString[])datesSet.toArray(new RelativeDateFormat.URelativeString[0]);
   }

   private static int dayDifference(Calendar until) {
      Calendar nowCal = (Calendar)until.clone();
      Date nowDate = new Date(System.currentTimeMillis());
      nowCal.clear();
      nowCal.setTime(nowDate);
      int dayDiff = until.get(20) - nowCal.get(20);
      return dayDiff;
   }

   private Calendar initializeCalendar(TimeZone zone, ULocale locale) {
      if(this.calendar == null) {
         if(zone == null) {
            this.calendar = Calendar.getInstance(locale);
         } else {
            this.calendar = Calendar.getInstance(zone, locale);
         }
      }

      return this.calendar;
   }

   private MessageFormat initializeCombinedFormat(Calendar cal, ULocale locale) {
      String pattern = "{1} {0}";

      try {
         CalendarData calData = new CalendarData(locale, cal.getType());
         String[] patterns = calData.getDateTimePatterns();
         if(patterns != null && patterns.length >= 9) {
            int glueIndex = 8;
            if(patterns.length >= 13) {
               switch(this.fDateStyle) {
               case 0:
               case 128:
                  ++glueIndex;
                  break;
               case 1:
               case 129:
                  glueIndex += 2;
                  break;
               case 2:
               case 130:
                  glueIndex += 3;
                  break;
               case 3:
               case 131:
                  glueIndex += 4;
               }
            }

            pattern = patterns[glueIndex];
         }
      } catch (MissingResourceException var7) {
         ;
      }

      this.fCombinedFormat = new MessageFormat(pattern, locale);
      return this.fCombinedFormat;
   }

   public static class URelativeString {
      public int offset;
      public String string;

      URelativeString(int offset, String string) {
         this.offset = offset;
         this.string = string;
      }

      URelativeString(String offset, String string) {
         this.offset = Integer.parseInt(offset);
         this.string = string;
      }
   }
}
