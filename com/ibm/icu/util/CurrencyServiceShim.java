package com.ibm.icu.util;

import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;
import java.util.Locale;

final class CurrencyServiceShim extends Currency.ServiceShim {
   static final ICULocaleService service = new CurrencyServiceShim.CFService();

   Locale[] getAvailableLocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableLocales():service.getAvailableLocales();
   }

   ULocale[] getAvailableULocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableULocales():service.getAvailableULocales();
   }

   Currency createInstance(ULocale loc) {
      if(service.isDefault()) {
         return Currency.createCurrency(loc);
      } else {
         Currency curr = (Currency)service.get(loc);
         return curr;
      }
   }

   Object registerInstance(Currency currency, ULocale locale) {
      return service.registerObject(currency, locale);
   }

   boolean unregister(Object registryKey) {
      return service.unregisterFactory((ICUService.Factory)registryKey);
   }

   private static class CFService extends ICULocaleService {
      CFService() {
         super("Currency");
         class CurrencyFactory extends ICULocaleService.ICUResourceBundleFactory {
            protected Object handleCreate(ULocale loc, int kind, ICUService srvc) {
               return Currency.createCurrency(loc);
            }
         }

         this.registerFactory(new CurrencyFactory());
         this.markDefault();
      }
   }
}
