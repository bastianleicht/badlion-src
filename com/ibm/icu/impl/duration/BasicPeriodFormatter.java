package com.ibm.icu.impl.duration;

import com.ibm.icu.impl.duration.BasicPeriodFormatterFactory;
import com.ibm.icu.impl.duration.Period;
import com.ibm.icu.impl.duration.PeriodFormatter;
import com.ibm.icu.impl.duration.TimeUnit;
import com.ibm.icu.impl.duration.impl.PeriodFormatterData;

class BasicPeriodFormatter implements PeriodFormatter {
   private BasicPeriodFormatterFactory factory;
   private String localeName;
   private PeriodFormatterData data;
   private BasicPeriodFormatterFactory.Customizations customs;

   BasicPeriodFormatter(BasicPeriodFormatterFactory factory, String localeName, PeriodFormatterData data, BasicPeriodFormatterFactory.Customizations customs) {
      this.factory = factory;
      this.localeName = localeName;
      this.data = data;
      this.customs = customs;
   }

   public String format(Period period) {
      if(!period.isSet()) {
         throw new IllegalArgumentException("period is not set");
      } else {
         return this.format(period.timeLimit, period.inFuture, period.counts);
      }
   }

   public PeriodFormatter withLocale(String locName) {
      if(!this.localeName.equals(locName)) {
         PeriodFormatterData newData = this.factory.getData(locName);
         return new BasicPeriodFormatter(this.factory, locName, newData, this.customs);
      } else {
         return this;
      }
   }

   private String format(int tl, boolean inFuture, int[] counts) {
      int mask = 0;

      for(int i = 0; i < counts.length; ++i) {
         if(counts[i] > 0) {
            mask |= 1 << i;
         }
      }

      if(!this.data.allowZero()) {
         int i = 0;

         for(int m = 1; i < counts.length; m <<= 1) {
            if((mask & m) != 0 && counts[i] == 1) {
               mask &= ~m;
            }

            ++i;
         }

         if(mask == 0) {
            return null;
         }
      }

      boolean forceD3Seconds = false;
      if(this.data.useMilliseconds() != 0 && (mask & 1 << TimeUnit.MILLISECOND.ordinal) != 0) {
         int sx = TimeUnit.SECOND.ordinal;
         int mx = TimeUnit.MILLISECOND.ordinal;
         int sf = 1 << sx;
         int mf = 1 << mx;
         switch(this.data.useMilliseconds()) {
         case 1:
            if((mask & sf) == 0) {
               mask |= sf;
               counts[sx] = 1;
            }

            counts[sx] += (counts[mx] - 1) / 1000;
            mask &= ~mf;
            forceD3Seconds = true;
            break;
         case 2:
            if((mask & sf) != 0) {
               counts[sx] += (counts[mx] - 1) / 1000;
               mask &= ~mf;
               forceD3Seconds = true;
            }
         }
      }

      int first = 0;

      int last;
      for(last = counts.length - 1; first < counts.length && (mask & 1 << first) == 0; ++first) {
         ;
      }

      while(last > first && (mask & 1 << last) == 0) {
         --last;
      }

      boolean isZero = true;

      for(int i = first; i <= last; ++i) {
         if((mask & 1 << i) != 0 && counts[i] > 1) {
            isZero = false;
            break;
         }
      }

      StringBuffer sb = new StringBuffer();
      if(!this.customs.displayLimit || isZero) {
         tl = 0;
      }

      int td;
      if(this.customs.displayDirection && !isZero) {
         td = inFuture?2:1;
      } else {
         td = 0;
      }

      boolean useDigitPrefix = this.data.appendPrefix(tl, td, sb);
      boolean multiple = first != last;
      boolean wasSkipped = true;
      boolean skipped = false;
      boolean countSep = this.customs.separatorVariant != 0;
      int i = first;

      for(int j = first; i <= last; i = j) {
         if(skipped) {
            this.data.appendSkippedUnit(sb);
            skipped = false;
            wasSkipped = true;
         }

         while(true) {
            ++j;
            if(j >= last || (mask & 1 << j) != 0) {
               TimeUnit unit = TimeUnit.units[i];
               int count = counts[i] - 1;
               int cv = this.customs.countVariant;
               if(i == last) {
                  if(forceD3Seconds) {
                     cv = 5;
                  }
               } else {
                  cv = 0;
               }

               boolean isLast = i == last;
               boolean mustSkip = this.data.appendUnit(unit, count, cv, this.customs.unitVariant, countSep, useDigitPrefix, multiple, isLast, wasSkipped, sb);
               skipped |= mustSkip;
               wasSkipped = false;
               if(this.customs.separatorVariant != 0 && j <= last) {
                  boolean afterFirst = i == first;
                  boolean beforeLast = j == last;
                  boolean fullSep = this.customs.separatorVariant == 2;
                  useDigitPrefix = this.data.appendUnitSeparator(unit, fullSep, afterFirst, beforeLast, sb);
               } else {
                  useDigitPrefix = false;
               }
               break;
            }

            skipped = true;
         }
      }

      this.data.appendSuffix(tl, td, sb);
      return sb.toString();
   }
}
