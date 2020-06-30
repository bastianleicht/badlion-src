package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicPeriodBuilderFactory;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.TimeUnit;
import java.util.TimeZone;

abstract class PeriodBuilderImpl implements PeriodBuilder {
   protected BasicPeriodBuilderFactory.Settings settings;

   public Period create(long duration) {
      return this.createWithReferenceDate(duration, System.currentTimeMillis());
   }

   public long approximateDurationOf(TimeUnit unit) {
      return BasicPeriodBuilderFactory.approximateDurationOf(unit);
   }

   public Period createWithReferenceDate(long duration, long referenceDate) {
      boolean inPast = duration < 0L;
      if(inPast) {
         duration = -duration;
      }

      Period ts = this.settings.createLimited(duration, inPast);
      if(ts == null) {
         ts = this.handleCreate(duration, referenceDate, inPast);
         if(ts == null) {
            ts = Period.lessThan(1.0F, this.settings.effectiveMinUnit()).inPast(inPast);
         }
      }

      return ts;
   }

   public PeriodBuilder withTimeZone(TimeZone timeZone) {
      return this;
   }

   public PeriodBuilder withLocale(String localeName) {
      BasicPeriodBuilderFactory.Settings newSettings = this.settings.setLocale(localeName);
      return (PeriodBuilder)(newSettings != this.settings?this.withSettings(newSettings):this);
   }

   protected abstract PeriodBuilder withSettings(BasicPeriodBuilderFactory.Settings var1);

   protected abstract Period handleCreate(long var1, long var3, boolean var5);

   protected PeriodBuilderImpl(BasicPeriodBuilderFactory.Settings settings) {
      this.settings = settings;
   }
}
