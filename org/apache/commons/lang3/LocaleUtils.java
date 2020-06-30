package org.apache.commons.lang3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.commons.lang3.StringUtils;

public class LocaleUtils {
   private static final ConcurrentMap cLanguagesByCountry = new ConcurrentHashMap();
   private static final ConcurrentMap cCountriesByLanguage = new ConcurrentHashMap();

   public static Locale toLocale(String str) {
      if(str == null) {
         return null;
      } else if(str.isEmpty()) {
         return new Locale("", "");
      } else if(str.contains("#")) {
         throw new IllegalArgumentException("Invalid locale format: " + str);
      } else {
         int len = str.length();
         if(len < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
         } else {
            char ch0 = str.charAt(0);
            if(ch0 == 95) {
               if(len < 3) {
                  throw new IllegalArgumentException("Invalid locale format: " + str);
               } else {
                  char ch1 = str.charAt(1);
                  char ch2 = str.charAt(2);
                  if(Character.isUpperCase(ch1) && Character.isUpperCase(ch2)) {
                     if(len == 3) {
                        return new Locale("", str.substring(1, 3));
                     } else if(len < 5) {
                        throw new IllegalArgumentException("Invalid locale format: " + str);
                     } else if(str.charAt(3) != 95) {
                        throw new IllegalArgumentException("Invalid locale format: " + str);
                     } else {
                        return new Locale("", str.substring(1, 3), str.substring(4));
                     }
                  } else {
                     throw new IllegalArgumentException("Invalid locale format: " + str);
                  }
               }
            } else {
               String[] split = str.split("_", -1);
               int occurrences = split.length - 1;
               switch(occurrences) {
               case 0:
                  if(!StringUtils.isAllLowerCase(str) || len != 2 && len != 3) {
                     throw new IllegalArgumentException("Invalid locale format: " + str);
                  }

                  return new Locale(str);
               case 1:
                  if(StringUtils.isAllLowerCase(split[0]) && (split[0].length() == 2 || split[0].length() == 3) && split[1].length() == 2 && StringUtils.isAllUpperCase(split[1])) {
                     return new Locale(split[0], split[1]);
                  }

                  throw new IllegalArgumentException("Invalid locale format: " + str);
               case 2:
                  if(StringUtils.isAllLowerCase(split[0]) && (split[0].length() == 2 || split[0].length() == 3) && (split[1].length() == 0 || split[1].length() == 2 && StringUtils.isAllUpperCase(split[1])) && split[2].length() > 0) {
                     return new Locale(split[0], split[1], split[2]);
                  }
               default:
                  throw new IllegalArgumentException("Invalid locale format: " + str);
               }
            }
         }
      }
   }

   public static List localeLookupList(Locale locale) {
      return localeLookupList(locale, locale);
   }

   public static List localeLookupList(Locale locale, Locale defaultLocale) {
      List<Locale> list = new ArrayList(4);
      if(locale != null) {
         list.add(locale);
         if(locale.getVariant().length() > 0) {
            list.add(new Locale(locale.getLanguage(), locale.getCountry()));
         }

         if(locale.getCountry().length() > 0) {
            list.add(new Locale(locale.getLanguage(), ""));
         }

         if(!list.contains(defaultLocale)) {
            list.add(defaultLocale);
         }
      }

      return Collections.unmodifiableList(list);
   }

   public static List availableLocaleList() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_LIST;
   }

   public static Set availableLocaleSet() {
      return LocaleUtils.SyncAvoid.AVAILABLE_LOCALE_SET;
   }

   public static boolean isAvailableLocale(Locale locale) {
      return availableLocaleList().contains(locale);
   }

   public static List languagesByCountry(String countryCode) {
      if(countryCode == null) {
         return Collections.emptyList();
      } else {
         List<Locale> langs = (List)cLanguagesByCountry.get(countryCode);
         if(langs == null) {
            ArrayList var5 = new ArrayList();
            List<Locale> locales = availableLocaleList();

            for(int i = 0; i < locales.size(); ++i) {
               Locale locale = (Locale)locales.get(i);
               if(countryCode.equals(locale.getCountry()) && locale.getVariant().isEmpty()) {
                  var5.add(locale);
               }
            }

            List var6 = Collections.unmodifiableList(var5);
            cLanguagesByCountry.putIfAbsent(countryCode, var6);
            langs = (List)cLanguagesByCountry.get(countryCode);
         }

         return langs;
      }
   }

   public static List countriesByLanguage(String languageCode) {
      if(languageCode == null) {
         return Collections.emptyList();
      } else {
         List<Locale> countries = (List)cCountriesByLanguage.get(languageCode);
         if(countries == null) {
            ArrayList var5 = new ArrayList();
            List<Locale> locales = availableLocaleList();

            for(int i = 0; i < locales.size(); ++i) {
               Locale locale = (Locale)locales.get(i);
               if(languageCode.equals(locale.getLanguage()) && locale.getCountry().length() != 0 && locale.getVariant().isEmpty()) {
                  var5.add(locale);
               }
            }

            List var6 = Collections.unmodifiableList(var5);
            cCountriesByLanguage.putIfAbsent(languageCode, var6);
            countries = (List)cCountriesByLanguage.get(languageCode);
         }

         return countries;
      }
   }

   static class SyncAvoid {
      private static final List AVAILABLE_LOCALE_LIST;
      private static final Set AVAILABLE_LOCALE_SET;

      static {
         List<Locale> list = new ArrayList(Arrays.asList(Locale.getAvailableLocales()));
         AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(list);
         AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet(list));
      }
   }
}
