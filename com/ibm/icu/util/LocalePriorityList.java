package com.ibm.icu.util;

import com.ibm.icu.util.ULocale;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocalePriorityList implements Iterable {
   private static final double D0 = 0.0D;
   private static final Double D1 = Double.valueOf(1.0D);
   private static final Pattern languageSplitter = Pattern.compile("\\s*,\\s*");
   private static final Pattern weightSplitter = Pattern.compile("\\s*(\\S*)\\s*;\\s*q\\s*=\\s*(\\S*)");
   private final Map languagesAndWeights;
   private static Comparator myDescendingDouble = new Comparator() {
      public int compare(Double o1, Double o2) {
         return -o1.compareTo(o2);
      }
   };

   public static LocalePriorityList.Builder add(ULocale languageCode) {
      return (new LocalePriorityList.Builder()).add(languageCode);
   }

   public static LocalePriorityList.Builder add(ULocale languageCode, double weight) {
      return (new LocalePriorityList.Builder()).add(languageCode, weight);
   }

   public static LocalePriorityList.Builder add(LocalePriorityList languagePriorityList) {
      return (new LocalePriorityList.Builder()).add(languagePriorityList);
   }

   public static LocalePriorityList.Builder add(String acceptLanguageString) {
      return (new LocalePriorityList.Builder()).add(acceptLanguageString);
   }

   public Double getWeight(ULocale language) {
      return (Double)this.languagesAndWeights.get(language);
   }

   public String toString() {
      StringBuilder result = new StringBuilder();

      for(ULocale language : this.languagesAndWeights.keySet()) {
         if(result.length() != 0) {
            result.append(", ");
         }

         result.append(language);
         double weight = ((Double)this.languagesAndWeights.get(language)).doubleValue();
         if(weight != D1.doubleValue()) {
            result.append(";q=").append(weight);
         }
      }

      return result.toString();
   }

   public Iterator iterator() {
      return this.languagesAndWeights.keySet().iterator();
   }

   public boolean equals(Object o) {
      if(o == null) {
         return false;
      } else if(this == o) {
         return true;
      } else {
         try {
            LocalePriorityList that = (LocalePriorityList)o;
            return this.languagesAndWeights.equals(that.languagesAndWeights);
         } catch (RuntimeException var3) {
            return false;
         }
      }
   }

   public int hashCode() {
      return this.languagesAndWeights.hashCode();
   }

   private LocalePriorityList(Map languageToWeight) {
      this.languagesAndWeights = languageToWeight;
   }

   public static class Builder {
      private final Map languageToWeight;

      private Builder() {
         this.languageToWeight = new LinkedHashMap();
      }

      public LocalePriorityList build() {
         return this.build(false);
      }

      public LocalePriorityList build(boolean preserveWeights) {
         Map<Double, Set<ULocale>> doubleCheck = new TreeMap(LocalePriorityList.myDescendingDouble);

         for(ULocale lang : this.languageToWeight.keySet()) {
            Double weight = (Double)this.languageToWeight.get(lang);
            Set<ULocale> s = (Set)doubleCheck.get(weight);
            if(s == null) {
               doubleCheck.put(weight, s = new LinkedHashSet());
            }

            s.add(lang);
         }

         Map<ULocale, Double> temp = new LinkedHashMap();

         for(Entry<Double, Set<ULocale>> langEntry : doubleCheck.entrySet()) {
            Double weight = (Double)langEntry.getKey();

            for(ULocale lang : (Set)langEntry.getValue()) {
               temp.put(lang, preserveWeights?weight:LocalePriorityList.D1);
            }
         }

         return new LocalePriorityList(Collections.unmodifiableMap(temp));
      }

      public LocalePriorityList.Builder add(LocalePriorityList languagePriorityList) {
         for(ULocale language : languagePriorityList.languagesAndWeights.keySet()) {
            this.add(language, ((Double)languagePriorityList.languagesAndWeights.get(language)).doubleValue());
         }

         return this;
      }

      public LocalePriorityList.Builder add(ULocale languageCode) {
         return this.add(languageCode, LocalePriorityList.D1.doubleValue());
      }

      public LocalePriorityList.Builder add(ULocale... languageCodes) {
         for(ULocale languageCode : languageCodes) {
            this.add(languageCode, LocalePriorityList.D1.doubleValue());
         }

         return this;
      }

      public LocalePriorityList.Builder add(ULocale languageCode, double weight) {
         if(this.languageToWeight.containsKey(languageCode)) {
            this.languageToWeight.remove(languageCode);
         }

         if(weight <= 0.0D) {
            return this;
         } else {
            if(weight > LocalePriorityList.D1.doubleValue()) {
               weight = LocalePriorityList.D1.doubleValue();
            }

            this.languageToWeight.put(languageCode, Double.valueOf(weight));
            return this;
         }
      }

      public LocalePriorityList.Builder add(String acceptLanguageList) {
         String[] items = LocalePriorityList.languageSplitter.split(acceptLanguageList.trim());
         Matcher itemMatcher = LocalePriorityList.weightSplitter.matcher("");

         for(String item : items) {
            if(itemMatcher.reset(item).matches()) {
               ULocale language = new ULocale(itemMatcher.group(1));
               double weight = Double.parseDouble(itemMatcher.group(2));
               if(weight < 0.0D || weight > LocalePriorityList.D1.doubleValue()) {
                  throw new IllegalArgumentException("Illegal weight, must be 0..1: " + weight);
               }

               this.add(language, weight);
            } else if(item.length() != 0) {
               this.add(new ULocale(item));
            }
         }

         return this;
      }
   }
}
