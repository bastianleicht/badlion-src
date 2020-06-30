package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarCache;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.util.Date;
import java.util.Locale;

public class HebrewCalendar extends Calendar {
   private static final long serialVersionUID = -1952524560588825816L;
   public static final int TISHRI = 0;
   public static final int HESHVAN = 1;
   public static final int KISLEV = 2;
   public static final int TEVET = 3;
   public static final int SHEVAT = 4;
   public static final int ADAR_1 = 5;
   public static final int ADAR = 6;
   public static final int NISAN = 7;
   public static final int IYAR = 8;
   public static final int SIVAN = 9;
   public static final int TAMUZ = 10;
   public static final int AV = 11;
   public static final int ELUL = 12;
   private static final int[][] LIMITS = new int[][]{{0, 0, 0, 0}, {-5000000, -5000000, 5000000, 5000000}, {0, 0, 12, 12}, {1, 1, 51, 56}, new int[0], {1, 1, 29, 30}, {1, 1, 353, 385}, new int[0], {-1, -1, 5, 5}, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], new int[0]};
   private static final int[][] MONTH_LENGTH = new int[][]{{30, 30, 30}, {29, 29, 30}, {29, 30, 30}, {29, 29, 29}, {30, 30, 30}, {30, 30, 30}, {29, 29, 29}, {30, 30, 30}, {29, 29, 29}, {30, 30, 30}, {29, 29, 29}, {30, 30, 30}, {29, 29, 29}};
   private static final int[][] MONTH_START = new int[][]{{0, 0, 0}, {30, 30, 30}, {59, 59, 60}, {88, 89, 90}, {117, 118, 119}, {147, 148, 149}, {147, 148, 149}, {176, 177, 178}, {206, 207, 208}, {235, 236, 237}, {265, 266, 267}, {294, 295, 296}, {324, 325, 326}, {353, 354, 355}};
   private static final int[][] LEAP_MONTH_START = new int[][]{{0, 0, 0}, {30, 30, 30}, {59, 59, 60}, {88, 89, 90}, {117, 118, 119}, {147, 148, 149}, {177, 178, 179}, {206, 207, 208}, {236, 237, 238}, {265, 266, 267}, {295, 296, 297}, {324, 325, 326}, {354, 355, 356}, {383, 384, 385}};
   private static CalendarCache cache = new CalendarCache();
   private static final long HOUR_PARTS = 1080L;
   private static final long DAY_PARTS = 25920L;
   private static final int MONTH_DAYS = 29;
   private static final long MONTH_FRACT = 13753L;
   private static final long MONTH_PARTS = 765433L;
   private static final long BAHARAD = 12084L;

   public HebrewCalendar() {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public HebrewCalendar(TimeZone zone) {
      this(zone, ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public HebrewCalendar(Locale aLocale) {
      this(TimeZone.getDefault(), aLocale);
   }

   public HebrewCalendar(ULocale locale) {
      this(TimeZone.getDefault(), locale);
   }

   public HebrewCalendar(TimeZone zone, Locale aLocale) {
      super(zone, aLocale);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   public HebrewCalendar(TimeZone zone, ULocale locale) {
      super(zone, locale);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   public HebrewCalendar(int year, int month, int date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.set(1, year);
      this.set(2, month);
      this.set(5, date);
   }

   public HebrewCalendar(Date date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.setTime(date);
   }

   public HebrewCalendar(int year, int month, int date, int hour, int minute, int second) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.set(1, year);
      this.set(2, month);
      this.set(5, date);
      this.set(11, hour);
      this.set(12, minute);
      this.set(13, second);
   }

   public void add(int field, int amount) {
      switch(field) {
      case 2:
         int month = this.get(2);
         int year = this.get(1);
         if(amount > 0) {
            boolean acrossAdar1 = month < 5;
            month = month + amount;

            while(true) {
               if(acrossAdar1 && month >= 5 && !isLeapYear(year)) {
                  ++month;
               }

               if(month <= 12) {
                  break;
               }

               month -= 13;
               ++year;
               acrossAdar1 = true;
            }
         } else {
            boolean acrossAdar1 = month > 5;
            month = month + amount;

            while(true) {
               if(acrossAdar1 && month <= 5 && !isLeapYear(year)) {
                  --month;
               }

               if(month >= 0) {
                  break;
               }

               month += 13;
               --year;
               acrossAdar1 = true;
            }
         }

         this.set(2, month);
         this.set(1, year);
         this.pinField(5);
         break;
      default:
         super.add(field, amount);
      }

   }

   public void roll(int field, int amount) {
      switch(field) {
      case 2:
         int month = this.get(2);
         int year = this.get(1);
         boolean leapYear = isLeapYear(year);
         int yearLength = monthsInYear(year);
         int newMonth = month + amount % yearLength;
         if(!leapYear) {
            if(amount > 0 && month < 5 && newMonth >= 5) {
               ++newMonth;
            } else if(amount < 0 && month > 5 && newMonth <= 5) {
               --newMonth;
            }
         }

         this.set(2, (newMonth + 13) % 13);
         this.pinField(5);
         return;
      default:
         super.roll(field, amount);
      }
   }

   private static long startOfYear(int year) {
      long day = cache.get((long)year);
      if(day == CalendarCache.EMPTY) {
         int months = (235 * year - 234) / 19;
         long frac = (long)months * 13753L + 12084L;
         day = (long)(months * 29) + frac / 25920L;
         frac = frac % 25920L;
         int wd = (int)(day % 7L);
         if(wd == 2 || wd == 4 || wd == 6) {
            ++day;
            wd = (int)(day % 7L);
         }

         if(wd == 1 && frac > 16404L && !isLeapYear(year)) {
            day += 2L;
         } else if(wd == 0 && frac > 23269L && isLeapYear(year - 1)) {
            ++day;
         }

         cache.put((long)year, day);
      }

      return day;
   }

   private final int yearType(int year) {
      int yearLength = this.handleGetYearLength(year);
      if(yearLength > 380) {
         yearLength -= 30;
      }

      int type = 0;
      switch(yearLength) {
      case 353:
         type = 0;
         break;
      case 354:
         type = 1;
         break;
      case 355:
         type = 2;
         break;
      default:
         throw new IllegalArgumentException("Illegal year length " + yearLength + " in year " + year);
      }

      return type;
   }

   /** @deprecated */
   public static boolean isLeapYear(int year) {
      int x = (year * 12 + 17) % 19;
      return x >= (x < 0?-7:12);
   }

   private static int monthsInYear(int year) {
      return isLeapYear(year)?13:12;
   }

   protected int handleGetLimit(int field, int limitType) {
      return LIMITS[field][limitType];
   }

   protected int handleGetMonthLength(int extendedYear, int month) {
      while(month < 0) {
         --extendedYear;
         month += monthsInYear(extendedYear);
      }

      while(month > 12) {
         month -= monthsInYear(extendedYear++);
      }

      switch(month) {
      case 1:
      case 2:
         return MONTH_LENGTH[month][this.yearType(extendedYear)];
      default:
         return MONTH_LENGTH[month][0];
      }
   }

   protected int handleGetYearLength(int eyear) {
      return (int)(startOfYear(eyear + 1) - startOfYear(eyear));
   }

   protected void handleComputeFields(int julianDay) {
      long d = (long)(julianDay - 347997);
      long m = d * 25920L / 765433L;
      int year = (int)((19L * m + 234L) / 235L) + 1;
      long ys = startOfYear(year);

      int dayOfYear;
      for(dayOfYear = (int)(d - ys); dayOfYear < 1; dayOfYear = (int)(d - ys)) {
         --year;
         ys = startOfYear(year);
      }

      int yearType = this.yearType(year);
      int[][] monthStart = isLeapYear(year)?LEAP_MONTH_START:MONTH_START;

      int month;
      for(month = 0; dayOfYear > monthStart[month][yearType]; ++month) {
         ;
      }

      --month;
      int dayOfMonth = dayOfYear - monthStart[month][yearType];
      this.internalSet(0, 0);
      this.internalSet(1, year);
      this.internalSet(19, year);
      this.internalSet(2, month);
      this.internalSet(5, dayOfMonth);
      this.internalSet(6, dayOfYear);
   }

   protected int handleGetExtendedYear() {
      int year;
      if(this.newerField(19, 1) == 19) {
         year = this.internalGet(19, 1);
      } else {
         year = this.internalGet(1, 1);
      }

      return year;
   }

   protected int handleComputeMonthStart(int eyear, int month, boolean useMonth) {
      while(month < 0) {
         --eyear;
         month += monthsInYear(eyear);
      }

      while(month > 12) {
         month -= monthsInYear(eyear++);
      }

      long day = startOfYear(eyear);
      if(month != 0) {
         if(isLeapYear(eyear)) {
            day += (long)LEAP_MONTH_START[month][this.yearType(eyear)];
         } else {
            day += (long)MONTH_START[month][this.yearType(eyear)];
         }
      }

      return (int)(day + 347997L);
   }

   public String getType() {
      return "hebrew";
   }
}
