package com.ibm.icu.text;

import com.ibm.icu.impl.ICULocaleService;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.ICUService;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

class NumberFormatServiceShim extends NumberFormat.NumberFormatShim {
   private static ICULocaleService service = new NumberFormatServiceShim.NFService();

   Locale[] getAvailableLocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableLocales():service.getAvailableLocales();
   }

   ULocale[] getAvailableULocales() {
      return service.isDefault()?ICUResourceBundle.getAvailableULocales():service.getAvailableULocales();
   }

   Object registerFactory(NumberFormat.NumberFormatFactory factory) {
      return service.registerFactory(new NumberFormatServiceShim.NFFactory(factory));
   }

   boolean unregister(Object registryKey) {
      return service.unregisterFactory((ICUService.Factory)registryKey);
   }

   NumberFormat createInstance(ULocale desiredLocale, int choice) {
      ULocale[] actualLoc = new ULocale[1];
      NumberFormat fmt = (NumberFormat)service.get(desiredLocale, choice, actualLoc);
      if(fmt == null) {
         throw new MissingResourceException("Unable to construct NumberFormat", "", "");
      } else {
         fmt = (NumberFormat)fmt.clone();
         if(choice == 1 || choice == 5 || choice == 6) {
            fmt.setCurrency(Currency.getInstance(desiredLocale));
         }

         ULocale uloc = actualLoc[0];
         fmt.setLocale(uloc, uloc);
         return fmt;
      }
   }

   private static final class NFFactory extends ICULocaleService.LocaleKeyFactory {
      private NumberFormat.NumberFormatFactory delegate;

      NFFactory(NumberFormat.NumberFormatFactory delegate) {
         super(delegate.visible());
         this.delegate = delegate;
      }

      public Object create(ICUService.Key key, ICUService srvc) {
         if(this.handlesKey(key) && key instanceof ICULocaleService.LocaleKey) {
            ICULocaleService.LocaleKey lkey = (ICULocaleService.LocaleKey)key;
            Object result = this.delegate.createFormat(lkey.canonicalLocale(), lkey.kind());
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

   private static class NFService extends ICULocaleService {
      NFService() {
         super("NumberFormat");
         class RBNumberFormatFactory extends ICULocaleService.ICUResourceBundleFactory {
            protected Object handleCreate(ULocale loc, int kind, ICUService srvc) {
               return NumberFormat.createInstance(loc, kind);
            }
         }

         this.registerFactory(new RBNumberFormatFactory());
         this.markDefault();
      }
   }
}
