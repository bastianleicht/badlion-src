package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.DateFormatter;
import com.ibm.icu.impl.duration.DurationFormatter;
import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.PeriodFormatter;
import java.util.TimeZone;

public interface DurationFormatterFactory {
   DurationFormatterFactory setPeriodFormatter(PeriodFormatter var1);

   DurationFormatterFactory setPeriodBuilder(PeriodBuilder var1);

   DurationFormatterFactory setFallback(DateFormatter var1);

   DurationFormatterFactory setFallbackLimit(long var1);

   DurationFormatterFactory setLocale(String var1);

   DurationFormatterFactory setTimeZone(TimeZone var1);

   DurationFormatter getFormatter();
}
