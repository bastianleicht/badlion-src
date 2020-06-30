package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;

public class HolidayBundle_da extends ListResourceBundle {
   private static final Object[][] fContents = new Object[][]{{"Armistice Day", "våbenhvile"}, {"Ascension", "himmelfart"}, {"Boxing Day", "anden juledag"}, {"Christmas Eve", "juleaften"}, {"Easter", "påske"}, {"Epiphany", "helligtrekongersdag"}, {"Good Friday", "langfredag"}, {"Halloween", "allehelgensaften"}, {"Maundy Thursday", "skærtorsdag"}, {"Palm Sunday", "palmesøndag"}, {"Pentecost", "pinse"}, {"Shrove Tuesday", "hvidetirsdag"}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
