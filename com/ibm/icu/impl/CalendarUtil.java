package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.MissingResourceException;

public class CalendarUtil {
   private static ICUCache CALTYPE_CACHE = new SimpleCache();
   private static final String CALKEY = "calendar";
   private static final String DEFCAL = "gregorian";

   public static String getCalendarType(ULocale loc) {
      String calType = null;
      calType = loc.getKeywordValue("calendar");
      if(calType != null) {
         return calType;
      } else {
         String baseLoc = loc.getBaseName();
         calType = (String)CALTYPE_CACHE.get(baseLoc);
         if(calType != null) {
            return calType;
         } else {
            ULocale canonical = ULocale.createCanonical(loc.toString());
            calType = canonical.getKeywordValue("calendar");
            if(calType == null) {
               String region = canonical.getCountry();
               if(region.length() == 0) {
                  ULocale fullLoc = ULocale.addLikelySubtags(canonical);
                  region = fullLoc.getCountry();
               }

               try {
                  UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
                  UResourceBundle calPref = rb.get("calendarPreferenceData");
                  UResourceBundle order = null;

                  try {
                     order = calPref.get(region);
                  } catch (MissingResourceException var9) {
                     order = calPref.get("001");
                  }

                  calType = order.getString(0);
               } catch (MissingResourceException var10) {
                  ;
               }

               if(calType == null) {
                  calType = "gregorian";
               }
            }

            CALTYPE_CACHE.put(baseLoc, calType);
            return calType;
         }
      }
   }
}
