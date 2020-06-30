package com.ibm.icu.util;

import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.util.Date;
import java.util.Locale;

public class BuddhistCalendar extends GregorianCalendar {
   private static final long serialVersionUID = 2583005278132380631L;
   public static final int BE = 0;
   private static final int BUDDHIST_ERA_START = -543;
   private static final int GREGORIAN_EPOCH = 1970;

   public BuddhistCalendar() {
   }

   public BuddhistCalendar(TimeZone zone) {
      super(zone);
   }

   public BuddhistCalendar(Locale aLocale) {
      super(aLocale);
   }

   public BuddhistCalendar(ULocale locale) {
      super(locale);
   }

   public BuddhistCalendar(TimeZone zone, Locale aLocale) {
      super(zone, aLocale);
   }

   public BuddhistCalendar(TimeZone zone, ULocale locale) {
      super(zone, locale);
   }

   public BuddhistCalendar(Date date) {
      this();
      this.setTime(date);
   }

   public BuddhistCalendar(int year, int month, int date) {
      super(year, month, date);
   }

   public BuddhistCalendar(int year, int month, int date, int hour, int minute, int second) {
      super(year, month, date, hour, minute, second);
   }

   protected int handleGetExtendedYear() {
      int year;
      if(this.newerField(19, 1) == 19) {
         year = this.internalGet(19, 1970);
      } else {
         year = this.internalGet(1, 2513) + -543;
      }

      return year;
   }

   protected int handleComputeMonthStart(int eyear, int month, boolean useMonth) {
      return super.handleComputeMonthStart(eyear, month, useMonth);
   }

   protected void handleComputeFields(int julianDay) {
      super.handleComputeFields(julianDay);
      int y = this.internalGet(19) - -543;
      this.internalSet(0, 0);
      this.internalSet(1, y);
   }

   protected int handleGetLimit(int field, int limitType) {
      return field == 0?0:super.handleGetLimit(field, limitType);
   }

   public String getType() {
      return "buddhist";
   }
}
