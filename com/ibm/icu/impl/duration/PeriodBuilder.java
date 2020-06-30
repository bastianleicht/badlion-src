package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.Period;
import java.util.TimeZone;

public interface PeriodBuilder {
   Period create(long var1);

   Period createWithReferenceDate(long var1, long var3);

   PeriodBuilder withLocale(String var1);

   PeriodBuilder withTimeZone(TimeZone var1);
}
