package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.Period;

public interface PeriodFormatter {
   String format(Period var1);

   PeriodFormatter withLocale(String var1);
}
