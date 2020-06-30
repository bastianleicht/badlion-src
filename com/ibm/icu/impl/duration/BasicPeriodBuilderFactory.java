package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.FixedUnitBuilder;
import com.ibm.icu.impl.duration.MultiUnitBuilder;
import com.ibm.icu.impl.duration.OneOrTwoUnitBuilder;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodBuilder;
import com.ibm.icu.impl.duration.PeriodBuilderFactory;
import com.ibm.icu.impl.duration.SingleUnitBuilder;
import com.ibm.icu.impl.duration.TimeUnit;
import com.ibm.icu.impl.duration.impl.PeriodFormatterData;
import com.ibm.icu.impl.duration.impl.PeriodFormatterDataService;
import java.util.TimeZone;

class BasicPeriodBuilderFactory implements PeriodBuilderFactory {
   private PeriodFormatterDataService ds;
   private BasicPeriodBuilderFactory.Settings settings;
   private static final short allBits = 255;

   BasicPeriodBuilderFactory(PeriodFormatterDataService ds) {
      this.ds = ds;
      this.settings = new BasicPeriodBuilderFactory.Settings();
   }

   static long approximateDurationOf(TimeUnit unit) {
      return TimeUnit.approxDurations[unit.ordinal];
   }

   public PeriodBuilderFactory setAvailableUnitRange(TimeUnit minUnit, TimeUnit maxUnit) {
      int uset = 0;

      for(int i = maxUnit.ordinal; i <= minUnit.ordinal; ++i) {
         uset |= 1 << i;
      }

      if(uset == 0) {
         throw new IllegalArgumentException("range " + minUnit + " to " + maxUnit + " is empty");
      } else {
         this.settings = this.settings.setUnits(uset);
         return this;
      }
   }

   public PeriodBuilderFactory setUnitIsAvailable(TimeUnit unit, boolean available) {
      int uset = this.settings.uset;
      if(available) {
         uset = uset | 1 << unit.ordinal;
      } else {
         uset = uset & ~(1 << unit.ordinal);
      }

      this.settings = this.settings.setUnits(uset);
      return this;
   }

   public PeriodBuilderFactory setMaxLimit(float maxLimit) {
      this.settings = this.settings.setMaxLimit(maxLimit);
      return this;
   }

   public PeriodBuilderFactory setMinLimit(float minLimit) {
      this.settings = this.settings.setMinLimit(minLimit);
      return this;
   }

   public PeriodBuilderFactory setAllowZero(boolean allow) {
      this.settings = this.settings.setAllowZero(allow);
      return this;
   }

   public PeriodBuilderFactory setWeeksAloneOnly(boolean aloneOnly) {
      this.settings = this.settings.setWeeksAloneOnly(aloneOnly);
      return this;
   }

   public PeriodBuilderFactory setAllowMilliseconds(boolean allow) {
      this.settings = this.settings.setAllowMilliseconds(allow);
      return this;
   }

   public PeriodBuilderFactory setLocale(String localeName) {
      this.settings = this.settings.setLocale(localeName);
      return this;
   }

   public PeriodBuilderFactory setTimeZone(TimeZone timeZone) {
      return this;
   }

   private BasicPeriodBuilderFactory.Settings getSettings() {
      return this.settings.effectiveSet() == 0?null:this.settings.setInUse();
   }

   public PeriodBuilder getFixedUnitBuilder(TimeUnit unit) {
      return FixedUnitBuilder.get(unit, this.getSettings());
   }

   public PeriodBuilder getSingleUnitBuilder() {
      return SingleUnitBuilder.get(this.getSettings());
   }

   public PeriodBuilder getOneOrTwoUnitBuilder() {
      return OneOrTwoUnitBuilder.get(this.getSettings());
   }

   public PeriodBuilder getMultiUnitBuilder(int periodCount) {
      return MultiUnitBuilder.get(periodCount, this.getSettings());
   }

   class Settings {
      boolean inUse;
      short uset = 255;
      TimeUnit maxUnit = TimeUnit.YEAR;
      TimeUnit minUnit = TimeUnit.MILLISECOND;
      int maxLimit;
      int minLimit;
      boolean allowZero = true;
      boolean weeksAloneOnly;
      boolean allowMillis = true;

      BasicPeriodBuilderFactory.Settings setUnits(int uset) {
         if(this.uset == uset) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.uset = (short)uset;
            if((uset & 255) == 255) {
               result.uset = 255;
               result.maxUnit = TimeUnit.YEAR;
               result.minUnit = TimeUnit.MILLISECOND;
            } else {
               int lastUnit = -1;

               for(int i = 0; i < TimeUnit.units.length; ++i) {
                  if(0 != (uset & 1 << i)) {
                     if(lastUnit == -1) {
                        result.maxUnit = TimeUnit.units[i];
                     }

                     lastUnit = i;
                  }
               }

               if(lastUnit == -1) {
                  result.minUnit = result.maxUnit = null;
               } else {
                  result.minUnit = TimeUnit.units[lastUnit];
               }
            }

            return result;
         }
      }

      short effectiveSet() {
         return this.allowMillis?this.uset:(short)(this.uset & ~(1 << TimeUnit.MILLISECOND.ordinal));
      }

      TimeUnit effectiveMinUnit() {
         if(!this.allowMillis && this.minUnit == TimeUnit.MILLISECOND) {
            int i = TimeUnit.units.length - 1;

            while(true) {
               --i;
               if(i < 0) {
                  return TimeUnit.SECOND;
               }

               if(0 != (this.uset & 1 << i)) {
                  break;
               }
            }

            return TimeUnit.units[i];
         } else {
            return this.minUnit;
         }
      }

      BasicPeriodBuilderFactory.Settings setMaxLimit(float maxLimit) {
         int val = maxLimit <= 0.0F?0:(int)(maxLimit * 1000.0F);
         if(maxLimit == (float)val) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.maxLimit = val;
            return result;
         }
      }

      BasicPeriodBuilderFactory.Settings setMinLimit(float minLimit) {
         int val = minLimit <= 0.0F?0:(int)(minLimit * 1000.0F);
         if(minLimit == (float)val) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.minLimit = val;
            return result;
         }
      }

      BasicPeriodBuilderFactory.Settings setAllowZero(boolean allow) {
         if(this.allowZero == allow) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.allowZero = allow;
            return result;
         }
      }

      BasicPeriodBuilderFactory.Settings setWeeksAloneOnly(boolean weeksAlone) {
         if(this.weeksAloneOnly == weeksAlone) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.weeksAloneOnly = weeksAlone;
            return result;
         }
      }

      BasicPeriodBuilderFactory.Settings setAllowMilliseconds(boolean allowMillis) {
         if(this.allowMillis == allowMillis) {
            return this;
         } else {
            BasicPeriodBuilderFactory.Settings result = this.inUse?this.copy():this;
            result.allowMillis = allowMillis;
            return result;
         }
      }

      BasicPeriodBuilderFactory.Settings setLocale(String localeName) {
         PeriodFormatterData data = BasicPeriodBuilderFactory.this.ds.get(localeName);
         return this.setAllowZero(data.allowZero()).setWeeksAloneOnly(data.weeksAloneOnly()).setAllowMilliseconds(data.useMilliseconds() != 1);
      }

      BasicPeriodBuilderFactory.Settings setInUse() {
         this.inUse = true;
         return this;
      }

      Period createLimited(long duration, boolean inPast) {
         if(this.maxLimit > 0) {
            long maxUnitDuration = BasicPeriodBuilderFactory.approximateDurationOf(this.maxUnit);
            if(duration * 1000L > (long)this.maxLimit * maxUnitDuration) {
               return Period.moreThan((float)this.maxLimit / 1000.0F, this.maxUnit).inPast(inPast);
            }
         }

         if(this.minLimit > 0) {
            TimeUnit emu = this.effectiveMinUnit();
            long emud = BasicPeriodBuilderFactory.approximateDurationOf(emu);
            long eml = emu == this.minUnit?(long)this.minLimit:Math.max(1000L, BasicPeriodBuilderFactory.approximateDurationOf(this.minUnit) * (long)this.minLimit / emud);
            if(duration * 1000L < eml * emud) {
               return Period.lessThan((float)eml / 1000.0F, emu).inPast(inPast);
            }
         }

         return null;
      }

      public BasicPeriodBuilderFactory.Settings copy() {
         BasicPeriodBuilderFactory.Settings result = BasicPeriodBuilderFactory.this.new Settings();
         result.inUse = this.inUse;
         result.uset = this.uset;
         result.maxUnit = this.maxUnit;
         result.minUnit = this.minUnit;
         result.maxLimit = this.maxLimit;
         result.minLimit = this.minLimit;
         result.allowZero = this.allowZero;
         result.weeksAloneOnly = this.weeksAloneOnly;
         result.allowMillis = this.allowMillis;
         return result;
      }
   }
}
