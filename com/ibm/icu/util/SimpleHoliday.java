package com.ibm.icu.util;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.DateRule;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.Holiday;
import com.ibm.icu.util.RangeDateRule;
import com.ibm.icu.util.SimpleDateRule;
import java.util.Date;

public class SimpleHoliday extends Holiday {
   public static final SimpleHoliday NEW_YEARS_DAY = new SimpleHoliday(0, 1, "New Year\'s Day");
   public static final SimpleHoliday EPIPHANY = new SimpleHoliday(0, 6, "Epiphany");
   public static final SimpleHoliday MAY_DAY = new SimpleHoliday(4, 1, "May Day");
   public static final SimpleHoliday ASSUMPTION = new SimpleHoliday(7, 15, "Assumption");
   public static final SimpleHoliday ALL_SAINTS_DAY = new SimpleHoliday(10, 1, "All Saints\' Day");
   public static final SimpleHoliday ALL_SOULS_DAY = new SimpleHoliday(10, 2, "All Souls\' Day");
   public static final SimpleHoliday IMMACULATE_CONCEPTION = new SimpleHoliday(11, 8, "Immaculate Conception");
   public static final SimpleHoliday CHRISTMAS_EVE = new SimpleHoliday(11, 24, "Christmas Eve");
   public static final SimpleHoliday CHRISTMAS = new SimpleHoliday(11, 25, "Christmas");
   public static final SimpleHoliday BOXING_DAY = new SimpleHoliday(11, 26, "Boxing Day");
   public static final SimpleHoliday ST_STEPHENS_DAY = new SimpleHoliday(11, 26, "St. Stephen\'s Day");
   public static final SimpleHoliday NEW_YEARS_EVE = new SimpleHoliday(11, 31, "New Year\'s Eve");

   public SimpleHoliday(int month, int dayOfMonth, String name) {
      super(name, new SimpleDateRule(month, dayOfMonth));
   }

   public SimpleHoliday(int month, int dayOfMonth, String name, int startYear) {
      super(name, rangeRule(startYear, 0, new SimpleDateRule(month, dayOfMonth)));
   }

   public SimpleHoliday(int month, int dayOfMonth, String name, int startYear, int endYear) {
      super(name, rangeRule(startYear, endYear, new SimpleDateRule(month, dayOfMonth)));
   }

   public SimpleHoliday(int month, int dayOfMonth, int dayOfWeek, String name) {
      super(name, new SimpleDateRule(month, dayOfMonth, dayOfWeek > 0?dayOfWeek:-dayOfWeek, dayOfWeek > 0));
   }

   public SimpleHoliday(int month, int dayOfMonth, int dayOfWeek, String name, int startYear) {
      super(name, rangeRule(startYear, 0, new SimpleDateRule(month, dayOfMonth, dayOfWeek > 0?dayOfWeek:-dayOfWeek, dayOfWeek > 0)));
   }

   public SimpleHoliday(int month, int dayOfMonth, int dayOfWeek, String name, int startYear, int endYear) {
      super(name, rangeRule(startYear, endYear, new SimpleDateRule(month, dayOfMonth, dayOfWeek > 0?dayOfWeek:-dayOfWeek, dayOfWeek > 0)));
   }

   private static DateRule rangeRule(int startYear, int endYear, DateRule rule) {
      if(startYear == 0 && endYear == 0) {
         return rule;
      } else {
         RangeDateRule rangeRule = new RangeDateRule();
         if(startYear != 0) {
            Calendar start = new GregorianCalendar(startYear, 0, 1);
            rangeRule.add(start.getTime(), rule);
         } else {
            rangeRule.add(rule);
         }

         if(endYear != 0) {
            Date end = (new GregorianCalendar(endYear, 11, 31)).getTime();
            rangeRule.add(end, (DateRule)null);
         }

         return rangeRule;
      }
   }
}
