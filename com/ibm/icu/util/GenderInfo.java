package com.ibm.icu.util;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

public class GenderInfo {
   private final GenderInfo.ListGenderStyle style;
   private static GenderInfo neutral = new GenderInfo(GenderInfo.ListGenderStyle.NEUTRAL);
   private static GenderInfo.Cache genderInfoCache = new GenderInfo.Cache();

   public static GenderInfo getInstance(ULocale uLocale) {
      return genderInfoCache.get(uLocale);
   }

   public static GenderInfo getInstance(Locale locale) {
      return getInstance(ULocale.forLocale(locale));
   }

   public GenderInfo.Gender getListGender(GenderInfo.Gender... genders) {
      return this.getListGender(Arrays.asList(genders));
   }

   public GenderInfo.Gender getListGender(List genders) {
      if(genders.size() == 0) {
         return GenderInfo.Gender.OTHER;
      } else if(genders.size() == 1) {
         return (GenderInfo.Gender)genders.get(0);
      } else {
         switch(this.style) {
         case NEUTRAL:
            return GenderInfo.Gender.OTHER;
         case MIXED_NEUTRAL:
            boolean hasFemale = false;
            boolean hasMale = false;

            for(GenderInfo.Gender gender : genders) {
               switch(gender) {
               case FEMALE:
                  if(hasMale) {
                     return GenderInfo.Gender.OTHER;
                  }

                  hasFemale = true;
                  break;
               case MALE:
                  if(hasFemale) {
                     return GenderInfo.Gender.OTHER;
                  }

                  hasMale = true;
                  break;
               case OTHER:
                  return GenderInfo.Gender.OTHER;
               }
            }

            return hasMale?GenderInfo.Gender.MALE:GenderInfo.Gender.FEMALE;
         case MALE_TAINTS:
            for(GenderInfo.Gender gender : genders) {
               if(gender != GenderInfo.Gender.FEMALE) {
                  return GenderInfo.Gender.MALE;
               }
            }

            return GenderInfo.Gender.FEMALE;
         default:
            return GenderInfo.Gender.OTHER;
         }
      }
   }

   public GenderInfo(GenderInfo.ListGenderStyle genderStyle) {
      this.style = genderStyle;
   }

   private static class Cache {
      private final ICUCache cache;

      private Cache() {
         this.cache = new SimpleCache();
      }

      public GenderInfo get(ULocale locale) {
         GenderInfo result = (GenderInfo)this.cache.get(locale);
         if(result == null) {
            result = load(locale);
            if(result == null) {
               ULocale fallback = locale.getFallback();
               result = fallback == null?GenderInfo.neutral:this.get(fallback);
            }

            this.cache.put(locale, result);
         }

         return result;
      }

      private static GenderInfo load(ULocale ulocale) {
         UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "genderList", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
         UResourceBundle genderList = rb.get("genderList");

         try {
            return new GenderInfo(GenderInfo.ListGenderStyle.fromName(genderList.getString(ulocale.toString())));
         } catch (MissingResourceException var4) {
            return null;
         }
      }
   }

   public static enum Gender {
      MALE,
      FEMALE,
      OTHER;
   }

   public static enum ListGenderStyle {
      NEUTRAL,
      MIXED_NEUTRAL,
      MALE_TAINTS;

      private static Map fromNameMap = new HashMap(3);

      public static GenderInfo.ListGenderStyle fromName(String name) {
         GenderInfo.ListGenderStyle result = (GenderInfo.ListGenderStyle)fromNameMap.get(name);
         if(result == null) {
            throw new IllegalArgumentException("Unknown gender style name: " + name);
         } else {
            return result;
         }
      }

      static {
         fromNameMap.put("neutral", NEUTRAL);
         fromNameMap.put("maleTaints", MALE_TAINTS);
         fromNameMap.put("mixedNeutral", MIXED_NEUTRAL);
      }
   }
}
