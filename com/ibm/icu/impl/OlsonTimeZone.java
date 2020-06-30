package com.ibm.icu.impl;

import com.ibm.icu.impl.Grego;
import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.impl.ZoneMeta;
import com.ibm.icu.util.AnnualTimeZoneRule;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.DateTimeRule;
import com.ibm.icu.util.InitialTimeZoneRule;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeArrayTimeZoneRule;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.TimeZoneTransition;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.Date;
import java.util.MissingResourceException;

public class OlsonTimeZone extends BasicTimeZone {
   static final long serialVersionUID = -6281977362477515376L;
   private int transitionCount;
   private int typeCount;
   private long[] transitionTimes64;
   private int[] typeOffsets;
   private byte[] typeMapData;
   private int finalStartYear = Integer.MAX_VALUE;
   private double finalStartMillis = Double.MAX_VALUE;
   private SimpleTimeZone finalZone = null;
   private volatile String canonicalID = null;
   private static final String ZONEINFORES = "zoneinfo64";
   private static final boolean DEBUG = ICUDebug.enabled("olson");
   private static final int SECONDS_PER_DAY = 86400;
   private transient InitialTimeZoneRule initialRule;
   private transient TimeZoneTransition firstTZTransition;
   private transient int firstTZTransitionIdx;
   private transient TimeZoneTransition firstFinalTZTransition;
   private transient TimeArrayTimeZoneRule[] historicRules;
   private transient SimpleTimeZone finalZoneWithStartYear;
   private transient boolean transitionRulesInitialized;
   private static final int currentSerialVersion = 1;
   private int serialVersionOnStream = 1;
   private transient boolean isFrozen = false;

   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
      if(month >= 0 && month <= 11) {
         return this.getOffset(era, year, month, day, dayOfWeek, milliseconds, Grego.monthLength(year, month));
      } else {
         throw new IllegalArgumentException("Month is not in the legal range: " + month);
      }
   }

   public int getOffset(int era, int year, int month, int dom, int dow, int millis, int monthLength) {
      if((era == 1 || era == 0) && month >= 0 && month <= 11 && dom >= 1 && dom <= monthLength && dow >= 1 && dow <= 7 && millis >= 0 && millis < 86400000 && monthLength >= 28 && monthLength <= 31) {
         if(era == 0) {
            year = -year;
         }

         if(this.finalZone != null && year >= this.finalStartYear) {
            return this.finalZone.getOffset(era, year, month, dom, dow, millis);
         } else {
            long time = Grego.fieldsToDay(year, month, dom) * 86400000L + (long)millis;
            int[] offsets = new int[2];
            this.getHistoricalOffset(time, true, 3, 1, offsets);
            return offsets[0] + offsets[1];
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public void setRawOffset(int offsetMillis) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
      } else if(this.getRawOffset() != offsetMillis) {
         long current = System.currentTimeMillis();
         if((double)current < this.finalStartMillis) {
            SimpleTimeZone stz = new SimpleTimeZone(offsetMillis, this.getID());
            boolean bDst = this.useDaylightTime();
            if(bDst) {
               TimeZoneRule[] currentRules = this.getSimpleTimeZoneRulesNear(current);
               if(currentRules.length != 3) {
                  TimeZoneTransition tzt = this.getPreviousTransition(current, false);
                  if(tzt != null) {
                     currentRules = this.getSimpleTimeZoneRulesNear(tzt.getTime() - 1L);
                  }
               }

               if(currentRules.length == 3 && currentRules[1] instanceof AnnualTimeZoneRule && currentRules[2] instanceof AnnualTimeZoneRule) {
                  AnnualTimeZoneRule r1 = (AnnualTimeZoneRule)currentRules[1];
                  AnnualTimeZoneRule r2 = (AnnualTimeZoneRule)currentRules[2];
                  int offset1 = r1.getRawOffset() + r1.getDSTSavings();
                  int offset2 = r2.getRawOffset() + r2.getDSTSavings();
                  DateTimeRule start;
                  DateTimeRule end;
                  int sav;
                  if(offset1 > offset2) {
                     start = r1.getRule();
                     end = r2.getRule();
                     sav = offset1 - offset2;
                  } else {
                     start = r2.getRule();
                     end = r1.getRule();
                     sav = offset2 - offset1;
                  }

                  stz.setStartRule(start.getRuleMonth(), start.getRuleWeekInMonth(), start.getRuleDayOfWeek(), start.getRuleMillisInDay());
                  stz.setEndRule(end.getRuleMonth(), end.getRuleWeekInMonth(), end.getRuleDayOfWeek(), end.getRuleMillisInDay());
                  stz.setDSTSavings(sav);
               } else {
                  stz.setStartRule(0, 1, 0);
                  stz.setEndRule(11, 31, 86399999);
               }
            }

            int[] fields = Grego.timeToFields(current, (int[])null);
            this.finalStartYear = fields[0];
            this.finalStartMillis = (double)Grego.fieldsToDay(fields[0], 0, 1);
            if(bDst) {
               stz.setStartYear(this.finalStartYear);
            }

            this.finalZone = stz;
         } else {
            this.finalZone.setRawOffset(offsetMillis);
         }

         this.transitionRulesInitialized = false;
      }
   }

   public Object clone() {
      return this.isFrozen()?this:this.cloneAsThawed();
   }

   public void getOffset(long date, boolean local, int[] offsets) {
      if(this.finalZone != null && (double)date >= this.finalStartMillis) {
         this.finalZone.getOffset(date, local, offsets);
      } else {
         this.getHistoricalOffset(date, local, 4, 12, offsets);
      }

   }

   /** @deprecated */
   public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets) {
      if(this.finalZone != null && (double)date >= this.finalStartMillis) {
         this.finalZone.getOffsetFromLocal(date, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
      } else {
         this.getHistoricalOffset(date, true, nonExistingTimeOpt, duplicatedTimeOpt, offsets);
      }

   }

   public int getRawOffset() {
      int[] ret = new int[2];
      this.getOffset(System.currentTimeMillis(), false, ret);
      return ret[0];
   }

   public boolean useDaylightTime() {
      long current = System.currentTimeMillis();
      if(this.finalZone != null && (double)current >= this.finalStartMillis) {
         return this.finalZone != null && this.finalZone.useDaylightTime();
      } else {
         int[] fields = Grego.timeToFields(current, (int[])null);
         long start = Grego.fieldsToDay(fields[0], 0, 1) * 86400L;
         long limit = Grego.fieldsToDay(fields[0] + 1, 0, 1) * 86400L;
         int i = 0;

         while(true) {
            if(i < this.transitionCount && this.transitionTimes64[i] < limit) {
               if((this.transitionTimes64[i] < start || this.dstOffsetAt(i) == 0) && (this.transitionTimes64[i] <= start || i <= 0 || this.dstOffsetAt(i - 1) == 0)) {
                  ++i;
                  continue;
               }

               return true;
            }

            return false;
         }
      }
   }

   public boolean observesDaylightTime() {
      long current = System.currentTimeMillis();
      if(this.finalZone != null) {
         if(this.finalZone.useDaylightTime()) {
            return true;
         }

         if((double)current >= this.finalStartMillis) {
            return false;
         }
      }

      long currentSec = Grego.floorDivide(current, 1000L);
      int trsIdx = this.transitionCount - 1;
      if(this.dstOffsetAt(trsIdx) != 0) {
         return true;
      } else {
         while(trsIdx >= 0 && this.transitionTimes64[trsIdx] > currentSec) {
            if(this.dstOffsetAt(trsIdx - 1) != 0) {
               return true;
            }
         }

         return false;
      }
   }

   public int getDSTSavings() {
      return this.finalZone != null?this.finalZone.getDSTSavings():super.getDSTSavings();
   }

   public boolean inDaylightTime(Date date) {
      int[] temp = new int[2];
      this.getOffset(date.getTime(), false, temp);
      return temp[1] != 0;
   }

   public boolean hasSameRules(TimeZone other) {
      if(this == other) {
         return true;
      } else if(!super.hasSameRules(other)) {
         return false;
      } else if(!(other instanceof OlsonTimeZone)) {
         return false;
      } else {
         OlsonTimeZone o = (OlsonTimeZone)other;
         if(this.finalZone == null) {
            if(o.finalZone != null) {
               return false;
            }
         } else if(o.finalZone == null || this.finalStartYear != o.finalStartYear || !this.finalZone.hasSameRules(o.finalZone)) {
            return false;
         }

         if(this.transitionCount == o.transitionCount && Arrays.equals(this.transitionTimes64, o.transitionTimes64) && this.typeCount == o.typeCount && Arrays.equals(this.typeMapData, o.typeMapData) && Arrays.equals(this.typeOffsets, o.typeOffsets)) {
            return true;
         } else {
            return false;
         }
      }
   }

   public String getCanonicalID() {
      if(this.canonicalID == null) {
         synchronized(this) {
            if(this.canonicalID == null) {
               this.canonicalID = getCanonicalID(this.getID());

               assert this.canonicalID != null;

               if(this.canonicalID == null) {
                  this.canonicalID = this.getID();
               }
            }
         }
      }

      return this.canonicalID;
   }

   private void constructEmpty() {
      this.transitionCount = 0;
      this.transitionTimes64 = null;
      this.typeMapData = null;
      this.typeCount = 1;
      this.typeOffsets = new int[]{0, 0};
      this.finalZone = null;
      this.finalStartYear = Integer.MAX_VALUE;
      this.finalStartMillis = Double.MAX_VALUE;
      this.transitionRulesInitialized = false;
   }

   public OlsonTimeZone(UResourceBundle top, UResourceBundle res, String id) {
      super(id);
      this.construct(top, res);
   }

   private void construct(UResourceBundle top, UResourceBundle res) {
      if(top != null && res != null) {
         if(DEBUG) {
            System.out.println("OlsonTimeZone(" + res.getKey() + ")");
         }

         int[] transPost32 = null;
         int[] trans32 = null;
         int[] transPre32 = null;
         this.transitionCount = 0;

         try {
            UResourceBundle r = res.get("transPre32");
            transPre32 = r.getIntVector();
            if(transPre32.length % 2 != 0) {
               throw new IllegalArgumentException("Invalid Format");
            }

            this.transitionCount += transPre32.length / 2;
         } catch (MissingResourceException var12) {
            ;
         }

         try {
            UResourceBundle var14 = res.get("trans");
            trans32 = var14.getIntVector();
            this.transitionCount += trans32.length;
         } catch (MissingResourceException var11) {
            ;
         }

         try {
            UResourceBundle var15 = res.get("transPost32");
            transPost32 = var15.getIntVector();
            if(transPost32.length % 2 != 0) {
               throw new IllegalArgumentException("Invalid Format");
            }

            this.transitionCount += transPost32.length / 2;
         } catch (MissingResourceException var10) {
            ;
         }

         if(this.transitionCount > 0) {
            this.transitionTimes64 = new long[this.transitionCount];
            int idx = 0;
            if(transPre32 != null) {
               for(int i = 0; i < transPre32.length / 2; ++idx) {
                  this.transitionTimes64[idx] = ((long)transPre32[i * 2] & 4294967295L) << 32 | (long)transPre32[i * 2 + 1] & 4294967295L;
                  ++i;
               }
            }

            if(trans32 != null) {
               for(int i = 0; i < trans32.length; ++idx) {
                  this.transitionTimes64[idx] = (long)trans32[i];
                  ++i;
               }
            }

            if(transPost32 != null) {
               for(int i = 0; i < transPost32.length / 2; ++idx) {
                  this.transitionTimes64[idx] = ((long)transPost32[i * 2] & 4294967295L) << 32 | (long)transPost32[i * 2 + 1] & 4294967295L;
                  ++i;
               }
            }
         } else {
            this.transitionTimes64 = null;
         }

         UResourceBundle var16 = res.get("typeOffsets");
         this.typeOffsets = var16.getIntVector();
         if(this.typeOffsets.length >= 2 && this.typeOffsets.length <= 32766 && this.typeOffsets.length % 2 == 0) {
            this.typeCount = this.typeOffsets.length / 2;
            if(this.transitionCount > 0) {
               var16 = res.get("typeMap");
               this.typeMapData = var16.getBinary((byte[])null);
               if(this.typeMapData.length != this.transitionCount) {
                  throw new IllegalArgumentException("Invalid Format");
               }
            } else {
               this.typeMapData = null;
            }

            this.finalZone = null;
            this.finalStartYear = Integer.MAX_VALUE;
            this.finalStartMillis = Double.MAX_VALUE;
            String ruleID = null;

            try {
               ruleID = res.getString("finalRule");
               var16 = res.get("finalRaw");
               int ruleRaw = var16.getInt() * 1000;
               var16 = loadRule(top, ruleID);
               int[] ruleData = var16.getIntVector();
               if(ruleData == null || ruleData.length != 11) {
                  throw new IllegalArgumentException("Invalid Format");
               }

               this.finalZone = new SimpleTimeZone(ruleRaw, "", ruleData[0], ruleData[1], ruleData[2], ruleData[3] * 1000, ruleData[4], ruleData[5], ruleData[6], ruleData[7], ruleData[8] * 1000, ruleData[9], ruleData[10] * 1000);
               var16 = res.get("finalYear");
               this.finalStartYear = var16.getInt();
               this.finalStartMillis = (double)(Grego.fieldsToDay(this.finalStartYear, 0, 1) * 86400000L);
            } catch (MissingResourceException var13) {
               if(ruleID != null) {
                  throw new IllegalArgumentException("Invalid Format");
               }
            }

         } else {
            throw new IllegalArgumentException("Invalid Format");
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   public OlsonTimeZone(String id) {
      super(id);
      UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
      UResourceBundle res = ZoneMeta.openOlsonResource(top, id);
      this.construct(top, res);
      if(this.finalZone != null) {
         this.finalZone.setID(id);
      }

   }

   public void setID(String id) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen OlsonTimeZone instance.");
      } else {
         if(this.canonicalID == null) {
            this.canonicalID = getCanonicalID(this.getID());

            assert this.canonicalID != null;

            if(this.canonicalID == null) {
               this.canonicalID = this.getID();
            }
         }

         if(this.finalZone != null) {
            this.finalZone.setID(id);
         }

         super.setID(id);
         this.transitionRulesInitialized = false;
      }
   }

   private void getHistoricalOffset(long date, boolean local, int NonExistingTimeOpt, int DuplicatedTimeOpt, int[] offsets) {
      if(this.transitionCount != 0) {
         long sec = Grego.floorDivide(date, 1000L);
         if(!local && sec < this.transitionTimes64[0]) {
            offsets[0] = this.initialRawOffset() * 1000;
            offsets[1] = this.initialDstOffset() * 1000;
         } else {
            int transIdx;
            for(transIdx = this.transitionCount - 1; transIdx >= 0; --transIdx) {
               long transition = this.transitionTimes64[transIdx];
               if(local) {
                  int offsetBefore = this.zoneOffsetAt(transIdx - 1);
                  boolean dstBefore = this.dstOffsetAt(transIdx - 1) != 0;
                  int offsetAfter = this.zoneOffsetAt(transIdx);
                  boolean dstAfter = this.dstOffsetAt(transIdx) != 0;
                  boolean dstToStd = dstBefore && !dstAfter;
                  boolean stdToDst = !dstBefore && dstAfter;
                  if(offsetAfter - offsetBefore >= 0) {
                     if(((NonExistingTimeOpt & 3) != 1 || !dstToStd) && ((NonExistingTimeOpt & 3) != 3 || !stdToDst)) {
                        if(((NonExistingTimeOpt & 3) != 1 || !stdToDst) && ((NonExistingTimeOpt & 3) != 3 || !dstToStd)) {
                           if((NonExistingTimeOpt & 12) == 12) {
                              transition += (long)offsetBefore;
                           } else {
                              transition += (long)offsetAfter;
                           }
                        } else {
                           transition += (long)offsetAfter;
                        }
                     } else {
                        transition += (long)offsetBefore;
                     }
                  } else if(((DuplicatedTimeOpt & 3) != 1 || !dstToStd) && ((DuplicatedTimeOpt & 3) != 3 || !stdToDst)) {
                     if(((DuplicatedTimeOpt & 3) != 1 || !stdToDst) && ((DuplicatedTimeOpt & 3) != 3 || !dstToStd)) {
                        if((DuplicatedTimeOpt & 12) == 4) {
                           transition += (long)offsetBefore;
                        } else {
                           transition += (long)offsetAfter;
                        }
                     } else {
                        transition += (long)offsetBefore;
                     }
                  } else {
                     transition += (long)offsetAfter;
                  }
               }

               if(sec >= transition) {
                  break;
               }
            }

            offsets[0] = this.rawOffsetAt(transIdx) * 1000;
            offsets[1] = this.dstOffsetAt(transIdx) * 1000;
         }
      } else {
         offsets[0] = this.initialRawOffset() * 1000;
         offsets[1] = this.initialDstOffset() * 1000;
      }

   }

   private int getInt(byte val) {
      return val & 255;
   }

   private int zoneOffsetAt(int transIdx) {
      int typeIdx = transIdx >= 0?this.getInt(this.typeMapData[transIdx]) * 2:0;
      return this.typeOffsets[typeIdx] + this.typeOffsets[typeIdx + 1];
   }

   private int rawOffsetAt(int transIdx) {
      int typeIdx = transIdx >= 0?this.getInt(this.typeMapData[transIdx]) * 2:0;
      return this.typeOffsets[typeIdx];
   }

   private int dstOffsetAt(int transIdx) {
      int typeIdx = transIdx >= 0?this.getInt(this.typeMapData[transIdx]) * 2:0;
      return this.typeOffsets[typeIdx + 1];
   }

   private int initialRawOffset() {
      return this.typeOffsets[0];
   }

   private int initialDstOffset() {
      return this.typeOffsets[1];
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(super.toString());
      buf.append('[');
      buf.append("transitionCount=" + this.transitionCount);
      buf.append(",typeCount=" + this.typeCount);
      buf.append(",transitionTimes=");
      if(this.transitionTimes64 != null) {
         buf.append('[');

         for(int i = 0; i < this.transitionTimes64.length; ++i) {
            if(i > 0) {
               buf.append(',');
            }

            buf.append(Long.toString(this.transitionTimes64[i]));
         }

         buf.append(']');
      } else {
         buf.append("null");
      }

      buf.append(",typeOffsets=");
      if(this.typeOffsets != null) {
         buf.append('[');

         for(int i = 0; i < this.typeOffsets.length; ++i) {
            if(i > 0) {
               buf.append(',');
            }

            buf.append(Integer.toString(this.typeOffsets[i]));
         }

         buf.append(']');
      } else {
         buf.append("null");
      }

      buf.append(",typeMapData=");
      if(this.typeMapData != null) {
         buf.append('[');

         for(int i = 0; i < this.typeMapData.length; ++i) {
            if(i > 0) {
               buf.append(',');
            }

            buf.append(Byte.toString(this.typeMapData[i]));
         }
      } else {
         buf.append("null");
      }

      buf.append(",finalStartYear=" + this.finalStartYear);
      buf.append(",finalStartMillis=" + this.finalStartMillis);
      buf.append(",finalZone=" + this.finalZone);
      buf.append(']');
      return buf.toString();
   }

   private static UResourceBundle loadRule(UResourceBundle top, String ruleid) {
      UResourceBundle r = top.get("Rules");
      r = r.get(ruleid);
      return r;
   }

   public boolean equals(Object obj) {
      if(!super.equals(obj)) {
         return false;
      } else {
         OlsonTimeZone z = (OlsonTimeZone)obj;
         return Utility.arrayEquals((byte[])this.typeMapData, z.typeMapData) || this.finalStartYear == z.finalStartYear && (this.finalZone == null && z.finalZone == null || this.finalZone != null && z.finalZone != null && this.finalZone.equals(z.finalZone) && this.transitionCount == z.transitionCount && this.typeCount == z.typeCount && Utility.arrayEquals((Object)this.transitionTimes64, z.transitionTimes64) && Utility.arrayEquals((int[])this.typeOffsets, z.typeOffsets) && Utility.arrayEquals((byte[])this.typeMapData, z.typeMapData));
      }
   }

   public int hashCode() {
      int ret = (int)((long)(this.finalStartYear ^ (this.finalStartYear >>> 4) + this.transitionCount ^ (this.transitionCount >>> 6) + this.typeCount) ^ (long)(this.typeCount >>> 8) + Double.doubleToLongBits(this.finalStartMillis) + (long)(this.finalZone == null?0:this.finalZone.hashCode()) + (long)super.hashCode());
      if(this.transitionTimes64 != null) {
         for(int i = 0; i < this.transitionTimes64.length; ++i) {
            ret = (int)((long)ret + (this.transitionTimes64[i] ^ this.transitionTimes64[i] >>> 8));
         }
      }

      for(int i = 0; i < this.typeOffsets.length; ++i) {
         ret += this.typeOffsets[i] ^ this.typeOffsets[i] >>> 8;
      }

      if(this.typeMapData != null) {
         for(int i = 0; i < this.typeMapData.length; ++i) {
            ret += this.typeMapData[i] & 255;
         }
      }

      return ret;
   }

   public TimeZoneTransition getNextTransition(long base, boolean inclusive) {
      this.initTransitionRules();
      if(this.finalZone != null) {
         if(inclusive && base == this.firstFinalTZTransition.getTime()) {
            return this.firstFinalTZTransition;
         }

         if(base >= this.firstFinalTZTransition.getTime()) {
            if(this.finalZone.useDaylightTime()) {
               return this.finalZoneWithStartYear.getNextTransition(base, inclusive);
            }

            return null;
         }
      }

      if(this.historicRules == null) {
         return null;
      } else {
         int ttidx;
         for(ttidx = this.transitionCount - 1; ttidx >= this.firstTZTransitionIdx; --ttidx) {
            long t = this.transitionTimes64[ttidx] * 1000L;
            if(base > t || !inclusive && base == t) {
               break;
            }
         }

         if(ttidx == this.transitionCount - 1) {
            return this.firstFinalTZTransition;
         } else if(ttidx < this.firstTZTransitionIdx) {
            return this.firstTZTransition;
         } else {
            TimeZoneRule to = this.historicRules[this.getInt(this.typeMapData[ttidx + 1])];
            TimeZoneRule from = this.historicRules[this.getInt(this.typeMapData[ttidx])];
            long startTime = this.transitionTimes64[ttidx + 1] * 1000L;
            return from.getName().equals(to.getName()) && from.getRawOffset() == to.getRawOffset() && from.getDSTSavings() == to.getDSTSavings()?this.getNextTransition(startTime, false):new TimeZoneTransition(startTime, from, to);
         }
      }
   }

   public TimeZoneTransition getPreviousTransition(long base, boolean inclusive) {
      this.initTransitionRules();
      if(this.finalZone != null) {
         if(inclusive && base == this.firstFinalTZTransition.getTime()) {
            return this.firstFinalTZTransition;
         }

         if(base > this.firstFinalTZTransition.getTime()) {
            if(this.finalZone.useDaylightTime()) {
               return this.finalZoneWithStartYear.getPreviousTransition(base, inclusive);
            }

            return this.firstFinalTZTransition;
         }
      }

      if(this.historicRules == null) {
         return null;
      } else {
         int ttidx;
         for(ttidx = this.transitionCount - 1; ttidx >= this.firstTZTransitionIdx; --ttidx) {
            long t = this.transitionTimes64[ttidx] * 1000L;
            if(base > t || inclusive && base == t) {
               break;
            }
         }

         if(ttidx < this.firstTZTransitionIdx) {
            return null;
         } else if(ttidx == this.firstTZTransitionIdx) {
            return this.firstTZTransition;
         } else {
            TimeZoneRule to = this.historicRules[this.getInt(this.typeMapData[ttidx])];
            TimeZoneRule from = this.historicRules[this.getInt(this.typeMapData[ttidx - 1])];
            long startTime = this.transitionTimes64[ttidx] * 1000L;
            return from.getName().equals(to.getName()) && from.getRawOffset() == to.getRawOffset() && from.getDSTSavings() == to.getDSTSavings()?this.getPreviousTransition(startTime, false):new TimeZoneTransition(startTime, from, to);
         }
      }
   }

   public TimeZoneRule[] getTimeZoneRules() {
      this.initTransitionRules();
      int size = 1;
      if(this.historicRules != null) {
         for(int i = 0; i < this.historicRules.length; ++i) {
            if(this.historicRules[i] != null) {
               ++size;
            }
         }
      }

      if(this.finalZone != null) {
         if(this.finalZone.useDaylightTime()) {
            size += 2;
         } else {
            ++size;
         }
      }

      TimeZoneRule[] rules = new TimeZoneRule[size];
      int idx = 0;
      rules[idx++] = this.initialRule;
      if(this.historicRules != null) {
         for(int i = 0; i < this.historicRules.length; ++i) {
            if(this.historicRules[i] != null) {
               rules[idx++] = this.historicRules[i];
            }
         }
      }

      if(this.finalZone != null) {
         if(this.finalZone.useDaylightTime()) {
            TimeZoneRule[] stzr = this.finalZoneWithStartYear.getTimeZoneRules();
            rules[idx++] = stzr[1];
            rules[idx++] = stzr[2];
         } else {
            rules[idx++] = new TimeArrayTimeZoneRule(this.getID() + "(STD)", this.finalZone.getRawOffset(), 0, new long[]{(long)this.finalStartMillis}, 2);
         }
      }

      return rules;
   }

   private synchronized void initTransitionRules() {
      if(!this.transitionRulesInitialized) {
         this.initialRule = null;
         this.firstTZTransition = null;
         this.firstFinalTZTransition = null;
         this.historicRules = null;
         this.firstTZTransitionIdx = 0;
         this.finalZoneWithStartYear = null;
         String stdName = this.getID() + "(STD)";
         String dstName = this.getID() + "(DST)";
         int raw = this.initialRawOffset() * 1000;
         int dst = this.initialDstOffset() * 1000;
         this.initialRule = new InitialTimeZoneRule(dst == 0?stdName:dstName, raw, dst);
         if(this.transitionCount > 0) {
            int transitionIdx;
            for(transitionIdx = 0; transitionIdx < this.transitionCount && this.getInt(this.typeMapData[transitionIdx]) == 0; ++transitionIdx) {
               ++this.firstTZTransitionIdx;
            }

            if(transitionIdx != this.transitionCount) {
               long[] times = new long[this.transitionCount];

               for(int typeIdx = 0; typeIdx < this.typeCount; ++typeIdx) {
                  int nTimes = 0;

                  for(transitionIdx = this.firstTZTransitionIdx; transitionIdx < this.transitionCount; ++transitionIdx) {
                     if(typeIdx == this.getInt(this.typeMapData[transitionIdx])) {
                        long tt = this.transitionTimes64[transitionIdx] * 1000L;
                        if((double)tt < this.finalStartMillis) {
                           times[nTimes++] = tt;
                        }
                     }
                  }

                  if(nTimes > 0) {
                     long[] startTimes = new long[nTimes];
                     System.arraycopy(times, 0, startTimes, 0, nTimes);
                     raw = this.typeOffsets[typeIdx * 2] * 1000;
                     dst = this.typeOffsets[typeIdx * 2 + 1] * 1000;
                     if(this.historicRules == null) {
                        this.historicRules = new TimeArrayTimeZoneRule[this.typeCount];
                     }

                     this.historicRules[typeIdx] = new TimeArrayTimeZoneRule(dst == 0?stdName:dstName, raw, dst, startTimes, 2);
                  }
               }

               int var15 = this.getInt(this.typeMapData[this.firstTZTransitionIdx]);
               this.firstTZTransition = new TimeZoneTransition(this.transitionTimes64[this.firstTZTransitionIdx] * 1000L, this.initialRule, this.historicRules[var15]);
            }
         }

         if(this.finalZone != null) {
            long startTime = (long)this.finalStartMillis;
            TimeZoneRule firstFinalRule;
            if(this.finalZone.useDaylightTime()) {
               this.finalZoneWithStartYear = (SimpleTimeZone)this.finalZone.clone();
               this.finalZoneWithStartYear.setStartYear(this.finalStartYear);
               TimeZoneTransition tzt = this.finalZoneWithStartYear.getNextTransition(startTime, false);
               firstFinalRule = tzt.getTo();
               startTime = tzt.getTime();
            } else {
               this.finalZoneWithStartYear = this.finalZone;
               firstFinalRule = new TimeArrayTimeZoneRule(this.finalZone.getID(), this.finalZone.getRawOffset(), 0, new long[]{startTime}, 2);
            }

            TimeZoneRule prevRule = null;
            if(this.transitionCount > 0) {
               prevRule = this.historicRules[this.getInt(this.typeMapData[this.transitionCount - 1])];
            }

            if(prevRule == null) {
               prevRule = this.initialRule;
            }

            this.firstFinalTZTransition = new TimeZoneTransition(startTime, prevRule, firstFinalRule);
         }

         this.transitionRulesInitialized = true;
      }
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      if(this.serialVersionOnStream < 1) {
         boolean initialized = false;
         String tzid = this.getID();
         if(tzid != null) {
            try {
               UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
               UResourceBundle res = ZoneMeta.openOlsonResource(top, tzid);
               this.construct(top, res);
               if(this.finalZone != null) {
                  this.finalZone.setID(tzid);
               }

               initialized = true;
            } catch (Exception var6) {
               ;
            }
         }

         if(!initialized) {
            this.constructEmpty();
         }
      }

      this.transitionRulesInitialized = false;
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   public TimeZone freeze() {
      this.isFrozen = true;
      return this;
   }

   public TimeZone cloneAsThawed() {
      OlsonTimeZone tz = (OlsonTimeZone)super.cloneAsThawed();
      if(this.finalZone != null) {
         this.finalZone.setID(this.getID());
         tz.finalZone = (SimpleTimeZone)this.finalZone.clone();
      }

      tz.isFrozen = false;
      return tz;
   }
}
