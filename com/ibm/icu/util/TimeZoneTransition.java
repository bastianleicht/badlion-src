package com.ibm.icu.util;

import com.ibm.icu.util.TimeZoneRule;

public class TimeZoneTransition {
   private final TimeZoneRule from;
   private final TimeZoneRule to;
   private final long time;

   public TimeZoneTransition(long time, TimeZoneRule from, TimeZoneRule to) {
      this.time = time;
      this.from = from;
      this.to = to;
   }

   public long getTime() {
      return this.time;
   }

   public TimeZoneRule getTo() {
      return this.to;
   }

   public TimeZoneRule getFrom() {
      return this.from;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("time=" + this.time);
      buf.append(", from={" + this.from + "}");
      buf.append(", to={" + this.to + "}");
      return buf.toString();
   }
}
