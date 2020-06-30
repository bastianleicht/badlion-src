package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicPeriodBuilderFactory;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.PeriodBuilderImpl;
import com.ibm.icu.impl.duration.TimeUnit;

class MultiUnitBuilder extends PeriodBuilderImpl {
   private int nPeriods;

   MultiUnitBuilder(int nPeriods, BasicPeriodBuilderFactory.Settings settings) {
      super(settings);
      this.nPeriods = nPeriods;
   }

   public static MultiUnitBuilder get(int nPeriods, BasicPeriodBuilderFactory.Settings settings) {
      return nPeriods > 0 && settings != null?new MultiUnitBuilder(nPeriods, settings):null;
   }

   protected PeriodBuilder withSettings(BasicPeriodBuilderFactory.Settings settingsToUse) {
      return get(this.nPeriods, settingsToUse);
   }

   protected Period handleCreate(long duration, long referenceDate, boolean inPast) {
      Period period = null;
      int n = 0;
      short uset = this.settings.effectiveSet();

      for(int i = 0; i < TimeUnit.units.length; ++i) {
         if(0 != (uset & 1 << i)) {
            TimeUnit unit = TimeUnit.units[i];
            if(n == this.nPeriods) {
               break;
            }

            long unitDuration = this.approximateDurationOf(unit);
            if(duration >= unitDuration || n > 0) {
               ++n;
               double count = (double)duration / (double)unitDuration;
               if(n < this.nPeriods) {
                  count = Math.floor(count);
                  duration -= (long)(count * (double)unitDuration);
               }

               if(period == null) {
                  period = Period.at((float)count, unit).inPast(inPast);
               } else {
                  period = period.and((float)count, unit);
               }
            }
         }
      }

      return period;
   }
}
