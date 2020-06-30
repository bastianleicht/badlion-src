package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.DateFormatter;
import com.ibm.icu.impl.duration.impl.Utils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class YMDDateFormatter implements DateFormatter {
   private String requestedFields;
   private String localeName;
   private TimeZone timeZone;
   private SimpleDateFormat df;

   public YMDDateFormatter(String requestedFields) {
      this(requestedFields, Locale.getDefault().toString(), TimeZone.getDefault());
   }

   public YMDDateFormatter(String requestedFields, String localeName, TimeZone timeZone) {
      this.requestedFields = requestedFields;
      this.localeName = localeName;
      this.timeZone = timeZone;
      Locale locale = Utils.localeFromString(localeName);
      this.df = new SimpleDateFormat("yyyy/mm/dd", locale);
      this.df.setTimeZone(timeZone);
   }

   public String format(long date) {
      return this.format(new Date(date));
   }

   public String format(Date date) {
      return this.df.format(date);
   }

   public DateFormatter withLocale(String locName) {
      return !locName.equals(this.localeName)?new YMDDateFormatter(this.requestedFields, locName, this.timeZone):this;
   }

   public DateFormatter withTimeZone(TimeZone tz) {
      return !tz.equals(this.timeZone)?new YMDDateFormatter(this.requestedFields, this.localeName, tz):this;
   }
}
