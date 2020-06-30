package com.ibm.icu.util;

import com.ibm.icu.impl.CalendarUtil;
import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

class CalendarServiceShim extends Calendar.CalendarShim {
   private static ICULocaleService service = new CalendarServiceShim.CalService();

   Locale[] getAvailableLocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableLocales():service.getAvailableLocales();
   }

   ULocale[] getAvailableULocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableULocales():service.getAvailableULocales();
   }

   Calendar createInstance(ULocale desiredLocale) {
      ULocale[] actualLoc = new ULocale[1];
      if(desiredLocale.equals(ULocale.ROOT)) {
         desiredLocale = ULocale.ROOT;
      }

      ULocale useLocale;
      if(desiredLocale.getKeywordValue("calendar") == null) {
         String calType = CalendarUtil.getCalendarType(desiredLocale);
         useLocale = desiredLocale.setKeywordValue("calendar", calType);
      } else {
         useLocale = desiredLocale;
      }

      Calendar cal = (Calendar)service.get(useLocale, actualLoc);
      if(cal == null) {
         throw new MissingResourceException("Unable to construct Calendar", "", "");
      } else {
         cal = (Calendar)cal.clone();
         return cal;
      }
   }

   Object registerFactory(Calendar.CalendarFactory factory) {
      return service.registerFactory(new CalendarServiceShim.CalFactory(factory));
   }

   boolean unregister(Object k) {
      return service.unregisterFactory((ICUService.Factory)k);
   }

   private static final class CalFactory extends ICULocaleService.LocaleKeyFactory {
      private Calendar.CalendarFactory delegate;

      CalFactory(Calendar.CalendarFactory delegate) {
         super(delegate.visible());
         this.delegate = delegate;
      }

      public Object create(ICUService.Key key, ICUService srvc) {
         if(this.handlesKey(key) && key instanceof ICULocaleService.LocaleKey) {
            ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
            Object result = this.delegate.createCalendar(lkey.canonicalLocale());
            if(result == null) {
               result = srvc.getKey(key, (String[])null, this);
            }

            return result;
         } else {
            return null;
         }
      }

      protected Set getSupportedIDs() {
         return this.delegate.getSupportedLocaleNames();
      }
   }

   private static class CalService extends ICULocaleService {
      CalService() {
         super("Calendar");
         class RBCalendarFactory extends ICULocaleService.ICUResourceBundleFactory {
            protected Object handleCreate(ULocale loc, int kind, ICUService sercice) {
               return Calendar.createInstance(loc);
            }
         }

         this.registerFactory(new RBCalendarFactory());
         this.markDefault();
      }
   }
}
