package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicPeriodBuilderFactory;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.PeriodBuilderImpl;
import com.ibm.icu.impl.duration.TimeUnit;

class SingleUnitBuilder extends PeriodBuilderImpl {
   SingleUnitBuilder(BasicPeriodBuilderFactory.Settings settings) {
      super(settings);
   }

   public static SingleUnitBuilder get(BasicPeriodBuilderFactory.Settings settings) {
      return settings == null?null:new SingleUnitBuilder(settings);
   }

   protected PeriodBuilder withSettings(BasicPeriodBuilderFactory.Settings settingsToUse) {
      return get(settingsToUse);
   }

   protected Period handleCreate(long duration, long referenceDate, boolean inPast) {
      short uset = this.settings.effectiveSet();

      for(int i = 0; i < TimeUnit.units.length; ++i) {
         if(0 != (uset & 1 << i)) {
            TimeUnit unit = TimeUnit.units[i];
            long unitDuration = this.approximateDurationOf(unit);
            if(duration >= unitDuration) {
               return Period.at((float)((double)duration / (double)unitDuration), unit).inPast(inPast);
            }
         }
      }

      return null;
   }
}
