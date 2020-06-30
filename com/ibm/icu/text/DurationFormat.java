package com.ibm.icu.text;

import com.ibm.icu.impl.duration.BasicDurationFormat;
import com.ibm.icu.text.UFormat;
import com.ibm.icu.util.ULocale;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;

public abstract class DurationFormat extends UFormat {
   private static final long serialVersionUID = -2076961954727774282L;

   public static DurationFormat getInstance(ULocale locale) {
      return BasicDurationFormat.getInstance(locale);
   }

   /** @deprecated */
   protected DurationFormat() {
   }

   /** @deprecated */
   protected DurationFormat(ULocale locale) {
      this.setLocale(locale, locale);
   }

   public abstract StringBuffer format(Object var1, StringBuffer var2, FieldPosition var3);

   public Object parseObject(String source, ParsePosition pos) {
      throw new UnsupportedOperationException();
   }

   public abstract String formatDurationFromNowTo(Date var1);

   public abstract String formatDurationFromNow(long var1);

   public abstract String formatDurationFrom(long var1, long var3);
}
