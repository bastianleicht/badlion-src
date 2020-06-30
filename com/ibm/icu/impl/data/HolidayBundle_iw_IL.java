package com.ibm.icu.impl.data;

import com.ibm.icu.util.HebrewHoliday;
import com.ibm.icu.util.Holiday;
import java.util.ListResourceBundle;

public class HolidayBundle_iw_IL extends ListResourceBundle {
   private static final Holiday[] fHolidays = new Holiday[]{HebrewHoliday.ROSH_HASHANAH, HebrewHoliday.YOM_KIPPUR, HebrewHoliday.HANUKKAH, HebrewHoliday.PURIM, HebrewHoliday.PASSOVER, HebrewHoliday.SHAVUOT, HebrewHoliday.SELIHOT};
   private static final Object[][] fContents = new Object[][]{{"holidays", fHolidays}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
