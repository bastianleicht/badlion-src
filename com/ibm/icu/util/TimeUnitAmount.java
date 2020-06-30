package com.ibm.icu.util;

import com.ibm.icu.util.Measure;
import com.ibm.icu.util.TimeUnit;

public class TimeUnitAmount extends Measure {
   public TimeUnitAmount(Number number, TimeUnit unit) {
      super(number, unit);
   }

   public TimeUnitAmount(double number, TimeUnit unit) {
      super(new Double(number), unit);
   }

   public TimeUnit getTimeUnit() {
      return (TimeUnit)this.getUnit();
   }
}
