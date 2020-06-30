package com.ibm.icu.util;

import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.util.Date;
import java.util.Locale;

abstract class CECalendar extends Calendar {
   private static final long serialVersionUID = -999547623066414271L;
   private static final int[][] LIMITS = new int[][]{{0, 0, 1, 1}, {1, 1, 5000000, 5000000}, {0, 0, 12, 12}, {1, 1, 52, 53}, new int[0], {1, 1, 5, 30}, {1, 1, 365, 366}, new int[0], {-1, -1, 1, 5}, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], new int[0]};

   protected CECalendar() {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
   }

   protected CECalendar(TimeZone zone) {
      this(zone, ULocale.getDefault(ULocale.Category.FORMAT));
   }

   protected CECalendar(Locale aLocale) {
      this(TimeZone.getDefault(), aLocale);
   }

   protected CECalendar(ULocale locale) {
      this(TimeZone.getDefault(), locale);
   }

   protected CECalendar(TimeZone zone, Locale aLocale) {
      super(zone, aLocale);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   protected CECalendar(TimeZone zone, ULocale locale) {
      super(zone, locale);
      this.setTimeInMillis(System.currentTimeMillis());
   }

   protected CECalendar(int year, int month, int date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.set(year, month, date);
   }

   protected CECalendar(Date date) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.setTime(date);
   }

   protected CECalendar(int year, int month, int date, int hour, int minute, int second) {
      super(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.set(year, month, date, hour, minute, second);
   }

   protected abstract int getJDEpochOffset();

   protected int handleComputeMonthStart(int eyear, int emonth, boolean useMonth) {
      return ceToJD((long)eyear, emonth, 0, this.getJDEpochOffset());
   }

   protected int handleGetLimit(int field, int limitType) {
      return LIMITS[field][limitType];
   }

   protected int handleGetMonthLength(int extendedYear, int month) {
      return (month + 1) % 13 != 0?30:extendedYear % 4 / 3 + 5;
   }

   public static int ceToJD(long year, int month, int day, int jdEpochOffset) {
      if(month >= 0) {
         year = year + (long)(month / 13);
         month = month % 13;
      } else {
         ++month;
         year = year + (long)(month / 13 - 1);
         month = month % 13 + 12;
      }

      return (int)((long)jdEpochOffset + 365L * year + floorDivide(year, 4L) + (long)(30 * month) + (long)day - 1L);
   }

   public static void jdToCE(int julianDay, int jdEpochOffset, int[] fields) {
      int[] r4 = new int[1];
      int c4 = floorDivide(julianDay - jdEpochOffset, 1461, r4);
      fields[0] = 4 * c4 + (r4[0] / 365 - r4[0] / 1460);
      int doy = r4[0] == 1460?365:r4[0] % 365;
      fields[1] = doy / 30;
      fields[2] = doy % 30 + 1;
   }
}
