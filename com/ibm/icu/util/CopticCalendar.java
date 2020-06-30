package com.ibm.icu.util;

import com.ibm.icu.util.CECalendar;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import java.util.Date;
import java.util.Locale;

public final class CopticCalendar extends CECalendar {
   private static final long serialVersionUID = 5903818751846742911L;
   public static final int TOUT = 0;
   public static final int BABA = 1;
   public static final int HATOR = 2;
   public static final int KIAHK = 3;
   public static final int TOBA = 4;
   public static final int AMSHIR = 5;
   public static final int BARAMHAT = 6;
   public static final int BARAMOUDA = 7;
   public static final int BASHANS = 8;
   public static final int PAONA = 9;
   public static final int EPEP = 10;
   public static final int MESRA = 11;
   public static final int NASIE = 12;
   private static final int JD_EPOCH_OFFSET = 1824665;
   private static final int BCE = 0;
   private static final int CE = 1;

   public CopticCalendar() {
   }

   public CopticCalendar(TimeZone zone) {
      super(zone);
   }

   public CopticCalendar(Locale aLocale) {
      super(aLocale);
   }

   public CopticCalendar(ULocale locale) {
      super(locale);
   }

   public CopticCalendar(TimeZone zone, Locale aLocale) {
      super(zone, aLocale);
   }

   public CopticCalendar(TimeZone zone, ULocale locale) {
      super(zone, locale);
   }

   public CopticCalendar(int year, int month, int date) {
      super(year, month, date);
   }

   public CopticCalendar(Date date) {
      super(date);
   }

   public CopticCalendar(int year, int month, int date, int hour, int minute, int second) {
      super(year, month, date, hour, minute, second);
   }

   public String getType() {
      return "coptic";
   }

   /** @deprecated */
   protected int handleGetExtendedYear() {
      int eyear;
      if(this.newerField(19, 1) == 19) {
         eyear = this.internalGet(19, 1);
      } else {
         int era = this.internalGet(0, 1);
         if(era == 0) {
            eyear = 1 - this.internalGet(1, 1);
         } else {
            eyear = this.internalGet(1, 1);
         }
      }

      return eyear;
   }

   /** @deprecated */
   protected void handleComputeFields(int julianDay) {
      int[] fields = new int[3];
      jdToCE(julianDay, this.getJDEpochOffset(), fields);
      int era;
      int year;
      if(fields[0] <= 0) {
         era = 0;
         year = 1 - fields[0];
      } else {
         era = 1;
         year = fields[0];
      }

      this.internalSet(19, fields[0]);
      this.internalSet(0, era);
      this.internalSet(1, year);
      this.internalSet(2, fields[1]);
      this.internalSet(5, fields[2]);
      this.internalSet(6, 30 * fields[1] + fields[2]);
   }

   /** @deprecated */
   protected int getJDEpochOffset() {
      return 1824665;
   }

   public static int copticToJD(long year, int month, int date) {
      return ceToJD(year, month, date, 1824665);
   }
}
