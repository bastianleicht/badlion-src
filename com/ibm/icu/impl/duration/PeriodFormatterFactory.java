package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.PeriodFormatter;

public interface PeriodFormatterFactory {
   PeriodFormatterFactory setLocale(String var1);

   PeriodFormatterFactory setDisplayLimit(boolean var1);

   PeriodFormatterFactory setDisplayPastFuture(boolean var1);

   PeriodFormatterFactory setSeparatorVariant(int var1);

   PeriodFormatterFactory setUnitVariant(int var1);

   PeriodFormatterFactory setCountVariant(int var1);

   PeriodFormatter getFormatter();
}
