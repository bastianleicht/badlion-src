package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeMap;

public class PluralRulesLoader {
   private final Map rulesIdToRules = new HashMap();
   private Map localeIdToCardinalRulesId;
   private Map localeIdToOrdinalRulesId;
   private Map rulesIdToEquivalentULocale;
   public static final PluralRulesLoader loader = new PluralRulesLoader();

   public ULocale[] getAvailableULocales() {
      Set<String> keys = this.getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL).keySet();
      ULocale[] locales = new ULocale[keys.size()];
      int n = 0;

      for(Iterator<String> iter = keys.iterator(); iter.hasNext(); locales[n++] = ULocale.createCanonical((String)iter.next())) {
         ;
      }

      return locales;
   }

   public ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable) {
      if(isAvailable != null && isAvailable.length > 0) {
         String localeId = ULocale.canonicalize(locale.getBaseName());
         Map<String, String> idMap = this.getLocaleIdToRulesIdMap(PluralRules.PluralType.CARDINAL);
         isAvailable[0] = idMap.containsKey(localeId);
      }

      String rulesId = this.getRulesIdForLocale(locale, PluralRules.PluralType.CARDINAL);
      if(rulesId != null && rulesId.trim().length() != 0) {
         ULocale result = (ULocale)this.getRulesIdToEquivalentULocaleMap().get(rulesId);
         return result == null?ULocale.ROOT:result;
      } else {
         return ULocale.ROOT;
      }
   }

   private Map getLocaleIdToRulesIdMap(PluralRules.PluralType type) {
      this.checkBuildRulesIdMaps();
      return type == PluralRules.PluralType.CARDINAL?this.localeIdToCardinalRulesId:this.localeIdToOrdinalRulesId;
   }

   private Map getRulesIdToEquivalentULocaleMap() {
      this.checkBuildRulesIdMaps();
      return this.rulesIdToEquivalentULocale;
   }

   private void checkBuildRulesIdMaps() {
      boolean haveMap;
      synchronized(this) {
         haveMap = this.localeIdToCardinalRulesId != null;
      }

      if(!haveMap) {
         Map<String, String> tempLocaleIdToCardinalRulesId;
         Map<String, String> tempLocaleIdToOrdinalRulesId;
         Map<String, ULocale> tempRulesIdToEquivalentULocale;
         try {
            UResourceBundle pluralb = this.getPluralBundle();
            UResourceBundle localeb = pluralb.get("locales");
            tempLocaleIdToCardinalRulesId = new TreeMap();
            tempRulesIdToEquivalentULocale = new HashMap();

            for(int i = 0; i < localeb.getSize(); ++i) {
               UResourceBundle b = localeb.get(i);
               String id = b.getKey();
               String value = b.getString().intern();
               tempLocaleIdToCardinalRulesId.put(id, value);
               if(!tempRulesIdToEquivalentULocale.containsKey(value)) {
                  tempRulesIdToEquivalentULocale.put(value, new ULocale(id));
               }
            }

            localeb = pluralb.get("locales_ordinals");
            tempLocaleIdToOrdinalRulesId = new TreeMap();

            for(int i = 0; i < localeb.getSize(); ++i) {
               UResourceBundle b = localeb.get(i);
               String id = b.getKey();
               String value = b.getString().intern();
               tempLocaleIdToOrdinalRulesId.put(id, value);
            }
         } catch (MissingResourceException var14) {
            tempLocaleIdToCardinalRulesId = Collections.emptyMap();
            tempLocaleIdToOrdinalRulesId = Collections.emptyMap();
            tempRulesIdToEquivalentULocale = Collections.emptyMap();
         }

         synchronized(this) {
            if(this.localeIdToCardinalRulesId == null) {
               this.localeIdToCardinalRulesId = tempLocaleIdToCardinalRulesId;
               this.localeIdToOrdinalRulesId = tempLocaleIdToOrdinalRulesId;
               this.rulesIdToEquivalentULocale = tempRulesIdToEquivalentULocale;
            }
         }
      }

   }

   public String getRulesIdForLocale(ULocale locale, PluralRules.PluralType type) {
      Map<String, String> idMap = this.getLocaleIdToRulesIdMap(type);
      String localeId = ULocale.canonicalize(locale.getBaseName());

      int ix;
      String var7;
      for(rulesId = null; null == (var7 = (String)idMap.get(localeId)); localeId = localeId.substring(0, ix)) {
         ix = localeId.lastIndexOf("_");
         if(ix == -1) {
            break;
         }
      }

      return var7;
   }

   public PluralRules getRulesForRulesId(String rulesId) {
      PluralRules rules = null;
      boolean hasRules;
      synchronized(this.rulesIdToRules) {
         hasRules = this.rulesIdToRules.containsKey(rulesId);
         if(hasRules) {
            rules = (PluralRules)this.rulesIdToRules.get(rulesId);
         }
      }

      if(!hasRules) {
         try {
            UResourceBundle pluralb = this.getPluralBundle();
            UResourceBundle rulesb = pluralb.get("rules");
            UResourceBundle setb = rulesb.get(rulesId);
            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < setb.getSize(); ++i) {
               UResourceBundle b = setb.get(i);
               if(i > 0) {
                  sb.append("; ");
               }

               sb.append(b.getKey());
               sb.append(": ");
               sb.append(b.getString());
            }

            rules = PluralRules.parseDescription(sb.toString());
         } catch (ParseException var12) {
            ;
         } catch (MissingResourceException var13) {
            ;
         }

         synchronized(this.rulesIdToRules) {
            if(this.rulesIdToRules.containsKey(rulesId)) {
               rules = (PluralRules)this.rulesIdToRules.get(rulesId);
            } else {
               this.rulesIdToRules.put(rulesId, rules);
            }
         }
      }

      return rules;
   }

   public UResourceBundle getPluralBundle() throws MissingResourceException {
      return ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "plurals", ICUResourceBundle.ICU_DATA_CLASS_LOADER, true);
   }

   public PluralRules forLocale(ULocale locale, PluralRules.PluralType type) {
      String rulesId = this.getRulesIdForLocale(locale, type);
      if(rulesId != null && rulesId.trim().length() != 0) {
         PluralRules rules = this.getRulesForRulesId(rulesId);
         if(rules == null) {
            rules = PluralRules.DEFAULT;
         }

         return rules;
      } else {
         return PluralRules.DEFAULT;
      }
   }
}
