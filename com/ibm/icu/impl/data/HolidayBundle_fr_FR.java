package com.ibm.icu.impl.data;

import com.ibm.icu.util.EasterHoliday;
import com.ibm.icu.util.Holiday;
import com.ibm.icu.util.SimpleHoliday;
import java.util.ListResourceBundle;

public class HolidayBundle_fr_FR extends ListResourceBundle {
   private static final Holiday[] fHolidays = new Holiday[]{SimpleHoliday.NEW_YEARS_DAY, new SimpleHoliday(4, 1, 0, "Labor Day"), new SimpleHoliday(4, 8, 0, "Victory Day"), new SimpleHoliday(6, 14, 0, "Bastille Day"), SimpleHoliday.ASSUMPTION, SimpleHoliday.ALL_SAINTS_DAY, new SimpleHoliday(10, 11, 0, "Armistice Day"), SimpleHoliday.CHRISTMAS, EasterHoliday.EASTER_SUNDAY, EasterHoliday.EASTER_MONDAY, EasterHoliday.ASCENSION, EasterHoliday.WHIT_SUNDAY, EasterHoliday.WHIT_MONDAY};
   private static final Object[][] fContents = new Object[][]{{"holidays", fHolidays}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
