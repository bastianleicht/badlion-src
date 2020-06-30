package com.ibm.icu.impl.duration;

import java.util.Date;
import java.util.TimeZone;

public interface DurationFormatter {
   String formatDurationFromNowTo(Date var1);

   String formatDurationFromNow(long var1);

   String formatDurationFrom(long var1, long var3);

   DurationFormatter withLocale(String var1);

   DurationFormatter withTimeZone(TimeZone var1);
}
