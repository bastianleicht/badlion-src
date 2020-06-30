package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarAstronomer;
import com.ibm.icu.impl.CalendarCache;
import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.Locale;

public class ChineseCalendar extends Calendar {
   private static final long serialVersionUID = 7312110751940929420L;
   private int epochYear;
   private TimeZone zoneAstro;
   private transient CalendarAstronomer astro;
   private transient CalendarCache winterSolsticeCache;
   private transient CalendarCache newYearCache;
   private transient boolean isLeapYear;
   private static final int[][] LIMITS = new int[][]{{1, 1, 83333, 83333}, {1, 1, 60, 60}, {0, 0, 11, 11}, {1, 1, 50, 55}, new int[0], {1, 1, 29, 30}, {1, 1, 353, 385}, new int[0], {-1, -1, 5, 5}, new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], {-5000000, -5000000, 5000000, 5000000}, new int[0], new int[0], {0, 0, 1, 1}};
   static final int[][][] CHINESE_DATE_PRECEDENCE = new int[][][]{{{5}, {3, 7}, {4, 7}, {8, 7}, {3, 18}, {4, 18}, {8, 18}, {6}, {37, 22}}, {{3}, {4}, {8}, {40, 7}, {40, 18}}};
   private static final int CHINESE_EPOCH_YEAR = -2636;
   private static final TimeZone CHINA_ZONE = (new SimpleTimeZone(28800000, "CHINA_ZONE")).freeze();
   private static final int SYNODIC_GAP = 25;

   public ChineseCalendar() {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT), -2636, CHINA_ZONE);
   }

   public ChineseCalendar(Date date) {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT), -2636, CHINA_ZONE);
      this.setTime(date);
   }

   public ChineseCalendar(int year, int month, int isLeapMonth, int date) {
      this(year, month, isLeapMonth, date, 0, 0, 0);
   }

   public ChineseCalendar(int year, int month, int isLeapMonth, int date, int hour, int minute, int second) {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT), -2636, CHINA_ZONE);
      this.set(14, 0);
      this.set(1, year);
      this.set(2, month);
      this.set(22, isLeapMonth);
      this.set(5, date);
      this.set(11, hour);
      this.set(12, minute);
      this.set(13, second);
   }

   public ChineseCalendar(int era, int year, int month, int isLeapMonth, int date) {
      this(era, year, month, isLeapMonth, 0, 0, 0);
   }

   public ChineseCalendar(int era, int year, int month, int isLeapMonth, int date, int hour, int minute, int second) {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT), -2636, CHINA_ZONE);
      this.set(14, 0);
      this.set(0, era);
      this.set(1, year);
      this.set(2, month);
      this.set(22, isLeapMonth);
      this.set(5, date);
      this.set(11, hour);
      this.set(12, minute);
      this.set(13, second);
   }

   public ChineseCalendar(Locale aLocale) {
      this(TimeZone.getDefault(), ULocale.forLocale(aLocale), -2636, CHINA_ZONE);
   }

   public ChineseCalendar(TimeZone zone) {
      this(zone, ULocale.getDefault(ULocale.Category.FORMAT), -2636, CHINA_ZONE);
   }

   public ChineseCalendar(TimeZone zone, Locale aLocale) {
      this(zone, ULocale.forLocale(aLocale), -2636, CHINA_ZONE);
   }

   public ChineseCalendar(ULocale locale) {
      this(TimeZone.getDefault(), locale, -2636, CHINA_ZONE);
   }

   public ChineseCalendar(TimeZone zone, ULocale locale) {
      this(zone, locale, -2636, CHINA_ZONE);
   }

   /** @deprecated */
   protected ChineseCalendar(TimeZone zone, ULocale locale, int epochYear, TimeZone zoneAstroCalc) {
      super(zone, locale);
      this.astro = new CalendarAstronomer();
      this.winterSolsticeCache = new CalendarCache();
      this.newYearCache = new CalendarCache();
      this.epochYear = epochYear;
      this.zoneAstro = zoneAstroCalc;
      this.setTimeInMillis(System.currentTimeMillis());
   }

   protected int handleGetLimit(int field, int limitType) {
      return LIMITS[field][limitType];
   }

   protected int handleGetExtendedYear() {
      int year;
      if(this.newestStamp(0, 1, 0) <= this.getStamp(19)) {
         year = this.internalGet(19, 1);
      } else {
         int cycle = this.internalGet(0, 1) - 1;
         year = cycle * 60 + this.internalGet(1, 1) - (this.epochYear - -2636);
      }

      return year;
   }

   protected int handleGetMonthLength(int extendedYear, int month) {
      int thisStart = this.handleComputeMonthStart(extendedYear, month, true) - 2440588 + 1;
      int nextStart = this.newMoonNear(thisStart + 25, true);
      return nextStart - thisStart;
   }

   protected DateFormat handleGetDateFormat(String pattern, String override, ULocale locale) {
      return super.handleGetDateFormat(pattern, override, locale);
   }

   protected int[][][] getFieldResolutionTable() {
      return CHINESE_DATE_PRECEDENCE;
   }

   private void offsetMonth(int newMoon, int dom, int delta) {
      newMoon = newMoon + (int)(29.530588853D * ((double)delta - 0.5D));
      newMoon = this.newMoonNear(newMoon, true);
      int jd = newMoon + 2440588 - 1 + dom;
      if(dom > 29) {
         this.set(20, jd - 1);
         this.complete();
         if(this.getActualMaximum(5) >= dom) {
            this.set(20, jd);
         }
      } else {
         this.set(20, jd);
      }

   }

   public void add(int field, int amount) {
      switch(field) {
      case 2:
         if(amount != 0) {
            int dom = this.get(5);
            int day = this.get(20) - 2440588;
            int moon = day - dom + 1;
            this.offsetMonth(moon, dom, amount);
         }
         break;
      default:
         super.add(field, amount);
      }

   }

   public void roll(int field, int amount) {
      switch(field) {
      case 2:
         if(amount != 0) {
            int dom = this.get(5);
            int day = this.get(20) - 2440588;
            int moon = day - dom + 1;
            int m = this.get(2);
            if(this.isLeapYear) {
               if(this.get(22) == 1) {
                  ++m;
               } else {
                  int moon1 = moon - (int)(29.530588853D * ((double)m - 0.5D));
                  moon1 = this.newMoonNear(moon1, true);
                  if(this.isLeapMonthBetween(moon1, moon)) {
                     ++m;
                  }
               }
            }

            int n = this.isLeapYear?13:12;
            int newM = (m + amount) % n;
            if(newM < 0) {
               newM += n;
            }

            if(newM != m) {
               this.offsetMonth(moon, dom, newM - m);
            }
         }
         break;
      default:
         super.roll(field, amount);
      }

   }

   private final long daysToMillis(int days) {
      long millis = (long)days * 86400000L;
      return millis - (long)this.zoneAstro.getOffset(millis);
   }

   private final int millisToDays(long millis) {
      return (int)floorDivide(millis + (long)this.zoneAstro.getOffset(millis), 86400000L);
   }

   private int winterSolstice(int gyear) {
      long cacheValue = this.winterSolsticeCache.get((long)gyear);
      if(cacheValue == CalendarCache.EMPTY) {
         long ms = this.daysToMillis(this.computeGregorianMonthStart(gyear, 11) + 1 - 2440588);
         this.astro.setTime(ms);
         long solarLong = this.astro.getSunTime(CalendarAstronomer.WINTER_SOLSTICE, true);
         cacheValue = (long)this.millisToDays(solarLong);
         this.winterSolsticeCache.put((long)gyear, cacheValue);
      }

      return (int)cacheValue;
   }

   private int newMoonNear(int days, boolean after) {
      this.astro.setTime(this.daysToMillis(days));
      long newMoon = this.astro.getMoonTime(CalendarAstronomer.NEW_MOON, after);
      return this.millisToDays(newMoon);
   }

   private int synodicMonthsBetween(int day1, int day2) {
      return (int)Math.round((double)(day2 - day1) / 29.530588853D);
   }

   private int majorSolarTerm(int days) {
      this.astro.setTime(this.daysToMillis(days));
      int term = ((int)Math.floor(6.0D * this.astro.getSunLongitude() / 3.141592653589793D) + 2) % 12;
      if(term < 1) {
         term += 12;
      }

      return term;
   }

   private boolean hasNoMajorSolarTerm(int newMoon) {
      int mst = this.majorSolarTerm(newMoon);
      int nmn = this.newMoonNear(newMoon + 25, true);
      int mstt = this.majorSolarTerm(nmn);
      return mst == mstt;
   }

   private boolean isLeapMonthBetween(int newMoon1, int newMoon2) {
      if(this.synodicMonthsBetween(newMoon1, newMoon2) >= 50) {
         throw new IllegalArgumentException("isLeapMonthBetween(" + newMoon1 + ", " + newMoon2 + "): Invalid parameters");
      } else {
         return newMoon2 >= newMoon1 && (this.isLeapMonthBetween(newMoon1, this.newMoonNear(newMoon2 - 25, false)) || this.hasNoMajorSolarTerm(newMoon2));
      }
   }

   protected void handleComputeFields(int julianDay) {
      this.computeChineseFields(julianDay - 2440588, this.getGregorianYear(), this.getGregorianMonth(), true);
   }

   private void computeChineseFields(int days, int gyear, int gmonth, boolean setAllFields) {
      int solsticeAfter = this.winterSolstice(gyear);
      int solsticeBefore;
      if(days < solsticeAfter) {
         solsticeBefore = this.winterSolstice(gyear - 1);
      } else {
         solsticeBefore = solsticeAfter;
         solsticeAfter = this.winterSolstice(gyear + 1);
      }

      int firstMoon = this.newMoonNear(solsticeBefore + 1, true);
      int lastMoon = this.newMoonNear(solsticeAfter + 1, false);
      int thisMoon = this.newMoonNear(days + 1, false);
      this.isLeapYear = this.synodicMonthsBetween(firstMoon, lastMoon) == 12;
      int month = this.synodicMonthsBetween(firstMoon, thisMoon);
      if(this.isLeapYear && this.isLeapMonthBetween(firstMoon, thisMoon)) {
         --month;
      }

      if(month < 1) {
         month += 12;
      }

      boolean isLeapMonth = this.isLeapYear && this.hasNoMajorSolarTerm(thisMoon) && !this.isLeapMonthBetween(firstMoon, this.newMoonNear(thisMoon - 25, false));
      this.internalSet(2, month - 1);
      this.internalSet(22, isLeapMonth?1:0);
      if(setAllFields) {
         int extended_year = gyear - this.epochYear;
         int cycle_year = gyear - -2636;
         if(month < 11 || gmonth >= 6) {
            ++extended_year;
            ++cycle_year;
         }

         int dayOfMonth = days - thisMoon + 1;
         this.internalSet(19, extended_year);
         int[] yearOfCycle = new int[1];
         int cycle = floorDivide(cycle_year - 1, 60, yearOfCycle);
         this.internalSet(0, cycle + 1);
         this.internalSet(1, yearOfCycle[0] + 1);
         this.internalSet(5, dayOfMonth);
         int newYear = this.newYear(gyear);
         if(days < newYear) {
            newYear = this.newYear(gyear - 1);
         }

         this.internalSet(6, days - newYear + 1);
      }

   }

   private int newYear(int gyear) {
      long cacheValue = this.newYearCache.get((long)gyear);
      if(cacheValue == CalendarCache.EMPTY) {
         int solsticeBefore = this.winterSolstice(gyear - 1);
         int solsticeAfter = this.winterSolstice(gyear);
         int newMoon1 = this.newMoonNear(solsticeBefore + 1, true);
         int newMoon2 = this.newMoonNear(newMoon1 + 25, true);
         int newMoon11 = this.newMoonNear(solsticeAfter + 1, false);
         if(this.synodicMonthsBetween(newMoon1, newMoon11) != 12 || !this.hasNoMajorSolarTerm(newMoon1) && !this.hasNoMajorSolarTerm(newMoon2)) {
            cacheValue = (long)newMoon2;
         } else {
            cacheValue = (long)this.newMoonNear(newMoon2 + 25, true);
         }

         this.newYearCache.put((long)gyear, cacheValue);
      }

      return (int)cacheValue;
   }

   protected int handleComputeMonthStart(int eyear, int month, boolean useMonth) {
      if(month < 0 || month > 11) {
         int[] rem = new int[1];
         eyear += floorDivide(month, 12, rem);
         month = rem[0];
      }

      int gyear = eyear + this.epochYear - 1;
      int newYear = this.newYear(gyear);
      int newMoon = this.newMoonNear(newYear + month * 29, true);
      int julianDay = newMoon + 2440588;
      int saveMonth = this.internalGet(2);
      int saveIsLeapMonth = this.internalGet(22);
      int isLeapMonth = useMonth?saveIsLeapMonth:0;
      this.computeGregorianFields(julianDay);
      this.computeChineseFields(newMoon, this.getGregorianYear(), this.getGregorianMonth(), false);
      if(month != this.internalGet(2) || isLeapMonth != this.internalGet(22)) {
         newMoon = this.newMoonNear(newMoon + 25, true);
         julianDay = newMoon + 2440588;
      }

      this.internalSet(2, saveMonth);
      this.internalSet(22, saveIsLeapMonth);
      return julianDay - 1;
   }

   public String getType() {
      return "chinese";
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      this.epochYear = -2636;
      this.zoneAstro = CHINA_ZONE;
      stream.defaultReadObject();
      this.astro = new CalendarAstronomer();
      this.winterSolsticeCache = new CalendarCache();
      this.newYearCache = new CalendarCache();
   }
}
