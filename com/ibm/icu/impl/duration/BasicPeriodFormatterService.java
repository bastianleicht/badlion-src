package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicDurationFormatterFactory;
import com.ibm.icu.impl.duration.BasicPeriodBuilderFactory;
import com.ibm.icu.impl.duration.BasicPeriodFormatterFactory;
import com.ibm.icu.impl.duration.DurationFormatterFactory;
import com.ibm.icu.impl.duration.PeriodBuilderFactory;
import com.ibm.icu.impl.duration.PeriodFormatterFactory;
import com.ibm.icu.impl.duration.PeriodFormatterService;
import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import com.ibm.icu.impl.duration.impl.ResourceBasedPeriodFormatterDataService;
import java.util.Collection;

public class BasicPeriodFormatterService implements PeriodFormatterService {
   private static BasicPeriodFormatterService instance;
   private PeriodFormatterDataService ds;

   public static BasicPeriodFormatterService getInstance() {
      if(instance == null) {
         PeriodFormatterDataService ds = ResourceBasedPeriodFormatterDataService.getInstance();
         instance = new BasicPeriodFormatterService(ds);
      }

      return instance;
   }

   public BasicPeriodFormatterService(PeriodFormatterDataService ds) {
      this.ds = ds;
   }

   public DurationFormatterFactory newDurationFormatterFactory() {
      return new BasicDurationFormatterFactory(this);
   }

   public PeriodFormatterFactory newPeriodFormatterFactory() {
      return new BasicPeriodFormatterFactory(this.ds);
   }

   public PeriodBuilderFactory newPeriodBuilderFactory() {
      return new BasicPeriodBuilderFactory(this.ds);
   }

   public Collection getAvailableLocaleNames() {
      return this.ds.getAvailableLocales();
   }
}
