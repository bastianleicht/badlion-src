package com.ibm.icu.util;

import java.io.Serializable;

public final class DateInterval implements Serializable {
   private static final long serialVersionUID = 1L;
   private final long fromDate;
   private final long toDate;

   public DateInterval(long from, long to) {
      this.fromDate = from;
      this.toDate = to;
   }

   public long getFromDate() {
      return this.fromDate;
   }

   public long getToDate() {
      return this.toDate;
   }

   public boolean equals(Object a) {
      if(!(a instanceof DateInterval)) {
         return false;
      } else {
         DateInterval di = (DateInterval)a;
         return this.fromDate == di.fromDate && this.toDate == di.toDate;
      }
   }

   public int hashCode() {
      return (int)(this.fromDate + this.toDate);
   }

   public String toString() {
      return this.fromDate + " " + this.toDate;
   }
}
