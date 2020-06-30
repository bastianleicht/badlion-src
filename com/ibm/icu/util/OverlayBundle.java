package com.ibm.icu.util;

import java.util.Enumeration;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/** @deprecated */
public class OverlayBundle extends ResourceBundle {
   private String[] baseNames;
   private Locale locale;
   private ResourceBundle[] bundles;

   /** @deprecated */
   public OverlayBundle(String[] baseNames, Locale locale) {
      this.baseNames = baseNames;
      this.locale = locale;
      this.bundles = new ResourceBundle[baseNames.length];
   }

   /** @deprecated */
   protected Object handleGetObject(String key) throws MissingResourceException {
      Object o = null;

      for(int i = 0; i < this.bundles.length; ++i) {
         this.load(i);

         try {
            o = this.bundles[i].getObject(key);
         } catch (MissingResourceException var5) {
            if(i == this.bundles.length - 1) {
               throw var5;
            }
         }

         if(o != null) {
            break;
         }
      }

      return o;
   }

   /** @deprecated */
   public Enumeration getKeys() {
      int i = this.bundles.length - 1;
      this.load(i);
      return this.bundles[i].getKeys();
   }

   private void load(int i) throws MissingResourceException {
      if(this.bundles[i] == null) {
         boolean tryWildcard = false;

         try {
            this.bundles[i] = ResourceBundle.getBundle(this.baseNames[i], this.locale);
            if(this.bundles[i].getLocale().equals(this.locale)) {
               return;
            }

            if(this.locale.getCountry().length() != 0 && i != this.bundles.length - 1) {
               tryWildcard = true;
            }
         } catch (MissingResourceException var6) {
            if(i == this.bundles.length - 1) {
               throw var6;
            }

            tryWildcard = true;
         }

         if(tryWildcard) {
            Locale wildcard = new Locale("xx", this.locale.getCountry(), this.locale.getVariant());

            try {
               this.bundles[i] = ResourceBundle.getBundle(this.baseNames[i], wildcard);
            } catch (MissingResourceException var5) {
               if(this.bundles[i] == null) {
                  throw var5;
               }
            }
         }
      }

   }
}
