package com.ibm.icu.util;

import java.io.Serializable;
import java.util.Date;

public abstract class TimeZoneRule implements Serializable {
   private static final long serialVersionUID = 6374143828553768100L;
   private final String name;
   private final int rawOffset;
   private final int dstSavings;

   public TimeZoneRule(String name, int rawOffset, int dstSavings) {
      this.name = name;
      this.rawOffset = rawOffset;
      this.dstSavings = dstSavings;
   }

   public String getName() {
      return this.name;
   }

   public int getRawOffset() {
      return this.rawOffset;
   }

   public int getDSTSavings() {
      return this.dstSavings;
   }

   public boolean isEquivalentTo(TimeZoneRule other) {
      return this.rawOffset == other.rawOffset && this.dstSavings == other.dstSavings;
   }

   public abstract Date getFirstStart(int var1, int var2);

   public abstract Date getFinalStart(int var1, int var2);

   public abstract Date getNextStart(long var1, int var3, int var4, boolean var5);

   public abstract Date getPreviousStart(long var1, int var3, int var4, boolean var5);

   public abstract boolean isTransitionRule();

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("name=" + this.name);
      buf.append(", stdOffset=" + this.rawOffset);
      buf.append(", dstSaving=" + this.dstSavings);
      return buf.toString();
   }
}
