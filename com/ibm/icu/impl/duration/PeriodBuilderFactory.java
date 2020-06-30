package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.TimeUnit;
import java.util.TimeZone;

public interface PeriodBuilderFactory {
   PeriodBuilderFactory setAvailableUnitRange(TimeUnit var1, TimeUnit var2);

   PeriodBuilderFactory setUnitIsAvailable(TimeUnit var1, boolean var2);

   PeriodBuilderFactory setMaxLimit(float var1);

   PeriodBuilderFactory setMinLimit(float var1);

   PeriodBuilderFactory setAllowZero(boolean var1);

   PeriodBuilderFactory setWeeksAloneOnly(boolean var1);

   PeriodBuilderFactory setAllowMilliseconds(boolean var1);

   PeriodBuilderFactory setLocale(String var1);

   PeriodBuilderFactory setTimeZone(TimeZone var1);

   PeriodBuilder getFixedUnitBuilder(TimeUnit var1);

   PeriodBuilder getSingleUnitBuilder();

   PeriodBuilder getOneOrTwoUnitBuilder();

   PeriodBuilder getMultiUnitBuilder(int var1);
}
