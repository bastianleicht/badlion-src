package com.ibm.icu.util;

import com.ibm.icu.util.DateRule;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class Holiday implements DateRule {
   private String name;
   private DateRule rule;
   private static Holiday[] noHolidays = new Holiday[0];

   public static Holiday[] getHolidays() {
      return getHolidays(ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public static Holiday[] getHolidays(Locale locale) {
      return getHolidays(ULocale.forLocale(locale));
   }

   public static Holiday[] getHolidays(ULocale locale) {
      Holiday[] result = noHolidays;

      try {
         ResourceBundle bundle = UResourceBundle.getBundleInstance("com.ibm.icu.impl.data.HolidayBundle", locale);
         result = (Holiday[])((Holiday[])bundle.getObject("holidays"));
      } catch (MissingResourceException var3) {
         ;
      }

      return result;
   }

   public Date firstAfter(Date start) {
      return this.rule.firstAfter(start);
   }

   public Date firstBetween(Date start, Date end) {
      return this.rule.firstBetween(start, end);
   }

   public boolean isOn(Date date) {
      return this.rule.isOn(date);
   }

   public boolean isBetween(Date start, Date end) {
      return this.rule.isBetween(start, end);
   }

   protected Holiday(String name, DateRule rule) {
      this.name = name;
      this.rule = rule;
   }

   public String getDisplayName() {
      return this.getDisplayName(ULocale.getDefault(ULocale.Category.DISPLAY));
   }

   public String getDisplayName(Locale locale) {
      return this.getDisplayName(ULocale.forLocale(locale));
   }

   public String getDisplayName(ULocale locale) {
      String dispName = this.name;

      try {
         ResourceBundle bundle = UResourceBundle.getBundleInstance("com.ibm.icu.impl.data.HolidayBundle", locale);
         dispName = bundle.getString(this.name);
      } catch (MissingResourceException var4) {
         ;
      }

      return dispName;
   }

   public DateRule getRule() {
      return this.rule;
   }

   public void setRule(DateRule rule) {
      this.rule = rule;
   }
}
