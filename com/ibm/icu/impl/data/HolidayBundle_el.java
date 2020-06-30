package com.ibm.icu.impl.data;

import java.util.ListResourceBundle;

public class HolidayBundle_el extends ListResourceBundle {
   private static final Object[][] fContents = new Object[][]{{"Assumption", "15 Αύγούστου"}, {"Boxing Day", "Δεύτερη μέρα τών Χριστουγέννων"}, {"Christmas", "Χριστούγεννα"}, {"Clean Monday", "Καθαρή Δευτέρα"}, {"Easter Monday", "Δεύτερη μέρα τού Πάσχα"}, {"Epiphany", "Έπιφάνεια"}, {"Good Friday", "Μεγάλη Παρασκευή"}, {"May Day", "Πρωτομαγιά"}, {"New Year\'s Day", "Πρωτοχρονιά"}, {"Ochi Day", "28 Όκτωβρίου"}, {"Whit Monday", "Δεύτερη μέρα τού Πεντηκοστή"}};

   public synchronized Object[][] getContents() {
      return fContents;
   }
}
