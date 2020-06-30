package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;

public class HolidayBundle_it extends ListResourceBundle {
   private static final Object[][] fContents = new Object[][]{{"All Saints\' Day", "Ognissanti"}, {"Armistice Day", "armistizio"}, {"Ascension", "ascensione"}, {"Ash Wednesday", "mercoledì delle ceneri"}, {"Boxing Day", "Santo Stefano"}, {"Christmas", "natale"}, {"Easter Sunday", "pasqua"}, {"Epiphany", "Epifania"}, {"Good Friday", "venerdì santo"}, {"Halloween", "vigilia di Ognissanti"}, {"Maundy Thursday", "giovedì santo"}, {"New Year\'s Day", "anno nuovo"}, {"Palm Sunday", "domenica delle palme"}, {"Pentecost", "di Pentecoste"}, {"Shrove Tuesday", "martedi grasso"}, {"St. Stephen\'s Day", "Santo Stefano"}, {"Thanksgiving", "Giorno del Ringraziamento"}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
