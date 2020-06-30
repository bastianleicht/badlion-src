package com.ibm.icu.impl.data;

import com.ibm.icu.util.EasterHoliday;
import com.ibm.icu.util.Holiday;
import com.ibm.icu.util.SimpleHoliday;
import java.util.ListResourceBundle;

public class HolidayBundle_de_DE extends ListResourceBundle {
   private static final Holiday[] fHolidays = new Holiday[]{SimpleHoliday.NEW_YEARS_DAY, SimpleHoliday.MAY_DAY, new SimpleHoliday(5, 15, 4, "Memorial Day"), new SimpleHoliday(9, 3, 0, "Unity Day"), SimpleHoliday.ALL_SAINTS_DAY, new SimpleHoliday(10, 18, 0, "Day of Prayer and Repentance"), SimpleHoliday.CHRISTMAS, SimpleHoliday.BOXING_DAY, EasterHoliday.GOOD_FRIDAY, EasterHoliday.EASTER_SUNDAY, EasterHoliday.EASTER_MONDAY, EasterHoliday.ASCENSION, EasterHoliday.WHIT_SUNDAY, EasterHoliday.WHIT_MONDAY};
   private static final Object[][] fContents = new Object[][]{{"holidays", fHolidays}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
