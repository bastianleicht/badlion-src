package com.ibm.icu.text;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public final class ListFormatter {
   private final String two;
   private final String start;
   private final String middle;
   private final String end;
   static Map localeToData = new HashMap();
   static ListFormatter.Cache cache = new ListFormatter.Cache();

   public ListFormatter(String two, String start, String middle, String end) {
      this.two = two;
      this.start = start;
      this.middle = middle;
      this.end = end;
   }

   public static ListFormatter getInstance(ULocale locale) {
      return cache.get(locale);
   }

   public static ListFormatter getInstance(Locale locale) {
      return getInstance(ULocale.forLocale(locale));
   }

   public static ListFormatter getInstance() {
      return getInstance(ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public String format(Object... items) {
      return this.format((Collection)Arrays.asList(items));
   }

   public String format(Collection items) {
      Iterator<?> it = items.iterator();
      int count = items.size();
      switch(count) {
      case 0:
         return "";
      case 1:
         return it.next().toString();
      case 2:
         return this.format2(this.two, it.next(), it.next());
      default:
         String result = it.next().toString();
         result = this.format2(this.start, result, it.next());

         for(count = count - 3; count > 0; --count) {
            result = this.format2(this.middle, result, it.next());
         }

         return this.format2(this.end, result, it.next());
      }
   }

   private String format2(String pattern, Object a, Object b) {
      int i0 = pattern.indexOf("{0}");
      int i1 = pattern.indexOf("{1}");
      if(i0 >= 0 && i1 >= 0) {
         return i0 < i1?pattern.substring(0, i0) + a + pattern.substring(i0 + 3, i1) + b + pattern.substring(i1 + 3):pattern.substring(0, i1) + b + pattern.substring(i1 + 3, i0) + a + pattern.substring(i0 + 3);
      } else {
         throw new IllegalArgumentException("Missing {0} or {1} in pattern " + pattern);
      }
   }

   static void add(String locale, String... data) {
      localeToData.put(new ULocale(locale), new ListFormatter(data[0], data[1], data[2], data[3]));
   }

   private static class Cache {
      private final ICUCache cache;

      private Cache() {
         this.cache = new SimpleCache();
      }

      public ListFormatter get(ULocale locale) {
         ListFormatter result = (ListFormatter)this.cache.get(locale);
         if(result == null) {
            result = load(locale);
            this.cache.put(locale, result);
         }

         return result;
      }

      private static ListFormatter load(ULocale ulocale) {
         ICUResourceBundle r = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", ulocale);
         r = r.getWithFallback("listPattern/standard");
         return new ListFormatter(r.getWithFallback("2").getString(), r.getWithFallback("start").getString(), r.getWithFallback("middle").getString(), r.getWithFallback("end").getString());
      }
   }
}
