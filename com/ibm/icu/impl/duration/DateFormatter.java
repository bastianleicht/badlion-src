package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public interface DateFormatter {
   String format(Date var1);

   String format(long var1);

   DateFormatter withLocale(String var1);

   DateFormatter withTimeZone(TimeZone var1);
}
