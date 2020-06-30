package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarAstronomer;
import com.ibm.icu.impl.CalendarCache;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.util.Date;
import java.util.Locale;

public class IslamicCalendar extends Calendar {
   private static final long serialVersionUID = -6253365474073869325L;
   public static final int MUHARRAM = 0;
   public static final int SAFAR = 1;
   public static final int RABI_1 = 2;
   public static final int RABI_2 = 3;
   public static final int JUMADA_1 = 4;
   public static final int JUMADA_2 = 5;
   public static final int RAJAB = 6;
   public static final int SHABAN = 7;
   public static final int RAMADAN = 8;
   public static final int SHAWWAL = 9;
   public static final int DHU_AL_QIDAH = 10;
   public static final int DHU_AL_HIJJAH = 11;
   private static final long HIJRA_MILLIS = -42521587200000L;
   private static final int[][] LIMITS = new int[][]{{0, 0, 0, 0}, {1, 1, 5000000, 5000000}, {0, 0, 11, 11}, {1, 1, 50, 51}, new int[0], {1, 1, 29, 30}, {1, 1, 354, 355}, new int[0], {-1, -1, 5, 5}, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], {1, 1, 5000000, 5000000}, new int[0], {1, 1, 5000000, 5000000}, new int[0], new int[0]};
   private static CalendarAstronomer astro = new CalendarAstronomer();
   private static CalendarCache cache = new CalendarCache();
   private boolean civil;

   public IslamicCalendar() {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public IslamicCalendar(TimeZone zone) {
      this(zone, ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public IslamicCalendar(Locale aLocale) {
      this(TimeZone.getDefault(), aLocale);
   }

   public IslamicCalendar(ULocale locale) {
      this(TimeZone.getDefault(), locale);
   }

   public IslamicCalendar(TimeZone zone, Locale aLocale) {
      super(zone, aLocale);
      this.civil = true;
      this.setTimeInMillis(System.currentTimeMillis());
   }

   public IslamicCalendar(TimeZone zone, ULocale locale) {
      super(zone, locale);
      this.civil = true;
      this.setTimeInMillis(System.currentTimeMillis());
   }

   public IslamicCalendar(Date date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.civil = true;
      this.setTime(date);
   }

   public IslamicCalendar(int year, int month, int date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.civil = true;
      this.set(1, year);
      this.set(2, month);
      this.set(5, date);
   }

   public IslamicCalendar(int year, int month, int date, int hour, int minute, int second) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.civil = true;
      this.set(1, year);
      this.set(2, month);
      this.set(5, date);
      this.set(11, hour);
      this.set(12, minute);
      this.set(13, second);
   }

   public void setCivil(boolean beCivil) {
      if(this.civil != beCivil) {
         long m = this.getTimeInMillis();
         this.civil = beCivil;
         this.clear();
         this.setTimeInMillis(m);
      }

   }

   public boolean isCivil() {
      return this.civil;
   }

   protected int handleGetLimit(int field, int limitType) {
      return LIMITS[field][limitType];
   }

   private static final boolean civilLeapYear(int year) {
      return (14 + 11 * year) % 30 < 11;
   }

   private long yearStart(int year) {
      return this.civil?(long)((year - 1) * 354) + (long)Math.floor((double)(3 + 11 * year) / 30.0D):trueMonthStart((long)(12 * (year - 1)));
   }

   private long monthStart(int year, int month) {
      int realYear = year + month / 12;
      int realMonth = month % 12;
      return this.civil?(long)Math.ceil(29.5D * (double)realMonth) + (long)((realYear - 1) * 354) + (long)Math.floor((double)(3 + 11 * realYear) / 30.0D):trueMonthStart((long)(12 * (realYear - 1) + realMonth));
   }

   private static final long trueMonthStart(long month) {
      long start = cache.get(month);
      if(start == CalendarCache.EMPTY) {
         long origin = -42521587200000L + (long)Math.floor((double)month * 29.530588853D) * 86400000L;
         double age = moonAge(origin);
         if(moonAge(origin) >= 0.0D) {
            while(true) {
               origin -= 86400000L;
               age = moonAge(origin);
               if(age < 0.0D) {
                  break;
               }
            }
         } else {
            while(true) {
               origin += 86400000L;
               age = moonAge(origin);
               if(age >= 0.0D) {
                  break;
               }
            }
         }

         start = (origin - -42521587200000L) / 86400000L + 1L;
         cache.put(month, start);
      }

      return start;
   }

   static final double moonAge(long time) {
      double age = 0.0D;
      synchronized(astro) {
         astro.setTime(time);
         age = astro.getMoonAge();
      }

      age = age * 180.0D / 3.141592653589793D;
      if(age > 180.0D) {
         age -= 360.0D;
      }

      return age;
   }

   protected int handleGetMonthLength(int extendedYear, int month) {
      int length = 0;
      if(this.civil) {
         length = 29 + (month + 1) % 2;
         if(month == 11 && civilLeapYear(extendedYear)) {
            ++length;
         }
      } else {
         month = 12 * (extendedYear - 1) + month;
         length = (int)(trueMonthStart((long)(month + 1)) - trueMonthStart((long)month));
      }

      return length;
   }

   protected int handleGetYearLength(int extendedYear) {
      if(this.civil) {
         return 354 + (civilLeapYear(extendedYear)?1:0);
      } else {
         int month = 12 * (extendedYear - 1);
         return (int)(trueMonthStart((long)(month + 12)) - trueMonthStart((long)month));
      }
   }

   protected int handleComputeMonthStart(int eyear, int month, boolean useMonth) {
      return (int)this.monthStart(eyear, month) + 1948439;
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

   protected void handleComputeFields(int julianDay) {
      long days = (long)(julianDay - 1948440);
      int year;
      int month;
      if(this.civil) {
         year = (int)Math.floor((double)(30L * days + 10646L) / 10631.0D);
         month = (int)Math.ceil((double)(days - 29L - this.yearStart(year)) / 29.5D);
         month = Math.min(month, 11);
      } else {
         int months = (int)Math.floor((double)days / 29.530588853D);
         long monthStart = (long)Math.floor((double)months * 29.530588853D - 1.0D);
         if(days - monthStart >= 25L && moonAge(this.internalGetTimeInMillis()) > 0.0D) {
            ++months;
         }

         while(trueMonthStart((long)months) > days) {
            --months;
         }

         year = months / 12 + 1;
         month = months % 12;
      }

      int dayOfMonth = (int)(days - this.monthStart(year, month)) + 1;
      int dayOfYear = (int)(days - this.monthStart(year, 0) + 1L);
      this.internalSet(0, 0);
      this.internalSet(1, year);
      this.internalSet(19, year);
      this.internalSet(2, month);
      this.internalSet(5, dayOfMonth);
      this.internalSet(6, dayOfYear);
   }

   public String getType() {
      return this.civil?"islamic-civil":"islamic";
   }
}
