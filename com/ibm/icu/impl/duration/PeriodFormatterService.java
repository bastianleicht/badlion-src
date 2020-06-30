package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.DurationFormatterFactory;
import com.ibm.icu.impl.duration.PeriodBuilderFactory;
import com.ibm.icu.impl.duration.PeriodFormatterFactory;
import java.util.Collection;

public interface PeriodFormatterService {
   DurationFormatterFactory newDurationFormatterFactory();

   PeriodFormatterFactory newPeriodFormatterFactory();

   PeriodBuilderFactory newPeriodBuilderFactory();

   Collection getAvailableLocaleNames();
}
