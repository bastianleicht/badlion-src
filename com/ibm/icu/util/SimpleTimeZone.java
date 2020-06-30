package com.ibm.icu.util;

import com.ibm.icu.impl.Grego;
import com.ibm.icu.util.AnnualTimeZoneRule;
import com.ibm.icu.util.BasicTimeZone;
import com.ibm.icu.util.DateTimeRule;
import com.ibm.icu.util.GregorianCalendar;
import com.ibm.icu.util.InitialTimeZoneRule;
import com.ibm.icu.util.STZInfo;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.TimeZoneTransition;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

public class SimpleTimeZone extends BasicTimeZone {
   private static final long serialVersionUID = -7034676239311322769L;
   public static final int WALL_TIME = 0;
   public static final int STANDARD_TIME = 1;
   public static final int UTC_TIME = 2;
   private static final byte[] staticMonthLength = new byte[]{(byte)31, (byte)29, (byte)31, (byte)30, (byte)31, (byte)30, (byte)31, (byte)31, (byte)30, (byte)31, (byte)30, (byte)31};
   private static final int DOM_MODE = 1;
   private static final int DOW_IN_MONTH_MODE = 2;
   private static final int DOW_GE_DOM_MODE = 3;
   private static final int DOW_LE_DOM_MODE = 4;
   private int raw;
   private int dst = 3600000;
   private STZInfo xinfo = null;
   private int startMonth;
   private int startDay;
   private int startDayOfWeek;
   private int startTime;
   private int startTimeMode;
   private int endTimeMode;
   private int endMonth;
   private int endDay;
   private int endDayOfWeek;
   private int endTime;
   private int startYear;
   private boolean useDaylight;
   private int startMode;
   private int endMode;
   private transient boolean transitionRulesInitialized;
   private transient InitialTimeZoneRule initialRule;
   private transient TimeZoneTransition firstTransition;
   private transient AnnualTimeZoneRule stdRule;
   private transient AnnualTimeZoneRule dstRule;
   private transient boolean isFrozen = false;

   public SimpleTimeZone(int rawOffset, String ID) {
      super(ID);
      this.construct(rawOffset, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3600000);
   }

   public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime) {
      super(ID);
      this.construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, 3600000);
   }

   public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode, int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode, int dstSavings) {
      super(ID);
      this.construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, startTimeMode, endMonth, endDay, endDayOfWeek, endTime, endTimeMode, dstSavings);
   }

   public SimpleTimeZone(int rawOffset, String ID, int startMonth, int startDay, int startDayOfWeek, int startTime, int endMonth, int endDay, int endDayOfWeek, int endTime, int dstSavings) {
      super(ID);
      this.construct(rawOffset, startMonth, startDay, startDayOfWeek, startTime, 0, endMonth, endDay, endDayOfWeek, endTime, 0, dstSavings);
   }

   public void setID(String ID) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         super.setID(ID);
         this.transitionRulesInitialized = false;
      }
   }

   public void setRawOffset(int offsetMillis) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.raw = offsetMillis;
         this.transitionRulesInitialized = false;
      }
   }

   public int getRawOffset() {
      return this.raw;
   }

   public void setStartYear(int year) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().sy = year;
         this.startYear = year;
         this.transitionRulesInitialized = false;
      }
   }

   public void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setStart(month, dayOfWeekInMonth, dayOfWeek, time, -1, false);
         this.setStartRule(month, dayOfWeekInMonth, dayOfWeek, time, 0);
      }
   }

   private void setStartRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time, int mode) {
      assert !this.isFrozen();

      this.startMonth = month;
      this.startDay = dayOfWeekInMonth;
      this.startDayOfWeek = dayOfWeek;
      this.startTime = time;
      this.startTimeMode = mode;
      this.decodeStartRule();
      this.transitionRulesInitialized = false;
   }

   public void setStartRule(int month, int dayOfMonth, int time) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setStart(month, -1, -1, time, dayOfMonth, false);
         this.setStartRule(month, dayOfMonth, 0, time, 0);
      }
   }

   public void setStartRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setStart(month, -1, dayOfWeek, time, dayOfMonth, after);
         this.setStartRule(month, after?dayOfMonth:-dayOfMonth, -dayOfWeek, time, 0);
      }
   }

   public void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setEnd(month, dayOfWeekInMonth, dayOfWeek, time, -1, false);
         this.setEndRule(month, dayOfWeekInMonth, dayOfWeek, time, 0);
      }
   }

   public void setEndRule(int month, int dayOfMonth, int time) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setEnd(month, -1, -1, time, dayOfMonth, false);
         this.setEndRule(month, dayOfMonth, 0, time);
      }
   }

   public void setEndRule(int month, int dayOfMonth, int dayOfWeek, int time, boolean after) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else {
         this.getSTZInfo().setEnd(month, -1, dayOfWeek, time, dayOfMonth, after);
         this.setEndRule(month, dayOfMonth, dayOfWeek, time, 0, after);
      }
   }

   private void setEndRule(int month, int dayOfMonth, int dayOfWeek, int time, int mode, boolean after) {
      assert !this.isFrozen();

      this.setEndRule(month, after?dayOfMonth:-dayOfMonth, -dayOfWeek, time, mode);
   }

   private void setEndRule(int month, int dayOfWeekInMonth, int dayOfWeek, int time, int mode) {
      assert !this.isFrozen();

      this.endMonth = month;
      this.endDay = dayOfWeekInMonth;
      this.endDayOfWeek = dayOfWeek;
      this.endTime = time;
      this.endTimeMode = mode;
      this.decodeEndRule();
      this.transitionRulesInitialized = false;
   }

   public void setDSTSavings(int millisSavedDuringDST) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen SimpleTimeZone instance.");
      } else if(millisSavedDuringDST <= 0) {
         throw new IllegalArgumentException();
      } else {
         this.dst = millisSavedDuringDST;
         this.transitionRulesInitialized = false;
      }
   }

   public int getDSTSavings() {
      return this.dst;
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      if(this.xinfo != null) {
         this.xinfo.applyTo(this);
      }

   }

   public String toString() {
      return "SimpleTimeZone: " + this.getID();
   }

   private STZInfo getSTZInfo() {
      if(this.xinfo == null) {
         this.xinfo = new STZInfo();
      }

      return this.xinfo;
   }

   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
      if(month >= 0 && month <= 11) {
         return this.getOffset(era, year, month, day, dayOfWeek, millis, Grego.monthLength(year, month));
      } else {
         throw new IllegalArgumentException();
      }
   }

   /** @deprecated */
   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength) {
      if(month >= 0 && month <= 11) {
         return this.getOffset(era, year, month, day, dayOfWeek, millis, Grego.monthLength(year, month), Grego.previousMonthLength(year, month));
      } else {
         throw new IllegalArgumentException();
      }
   }

   private int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis, int monthLength, int prevMonthLength) {
      if((era == 1 || era == 0) && month >= 0 && month <= 11 && day >= 1 && day <= monthLength && dayOfWeek >= 1 && dayOfWeek <= 7 && millis >= 0 && millis < 86400000 && monthLength >= 28 && monthLength <= 31 && prevMonthLength >= 28 && prevMonthLength <= 31) {
         int result = this.raw;
         if(this.useDaylight && year >= this.startYear && era == 1) {
            boolean southern = this.startMonth > this.endMonth;
            int startCompare = this.compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, this.startTimeMode == 2?-this.raw:0, this.startMode, this.startMonth, this.startDayOfWeek, this.startDay, this.startTime);
            int endCompare = 0;
            if(southern != startCompare >= 0) {
               endCompare = this.compareToRule(month, monthLength, prevMonthLength, day, dayOfWeek, millis, this.endTimeMode == 0?this.dst:(this.endTimeMode == 2?-this.raw:0), this.endMode, this.endMonth, this.endDayOfWeek, this.endDay, this.endTime);
            }

            if(!southern && startCompare >= 0 && endCompare < 0 || southern && (startCompare >= 0 || endCompare < 0)) {
               result += this.dst;
            }

            return result;
         } else {
            return result;
         }
      } else {
         throw new IllegalArgumentException();
      }
   }

   /** @deprecated */
   public void getOffsetFromLocal(long date, int nonExistingTimeOpt, int duplicatedTimeOpt, int[] offsets) {
      offsets[0] = this.getRawOffset();
      int[] fields = new int[6];
      Grego.timeToFields(date, fields);
      offsets[1] = this.getOffset(1, fields[0], fields[1], fields[2], fields[3], fields[5]) - offsets[0];
      boolean recalc = false;
      if(offsets[1] > 0) {
         if((nonExistingTimeOpt & 3) == 1 || (nonExistingTimeOpt & 3) != 3 && (nonExistingTimeOpt & 12) != 12) {
            date -= (long)this.getDSTSavings();
            recalc = true;
         }
      } else if((duplicatedTimeOpt & 3) == 3 || (duplicatedTimeOpt & 3) != 1 && (duplicatedTimeOpt & 12) == 4) {
         date -= (long)this.getDSTSavings();
         recalc = true;
      }

      if(recalc) {
         Grego.timeToFields(date, fields);
         offsets[1] = this.getOffset(1, fields[0], fields[1], fields[2], fields[3], fields[5]) - offsets[0];
      }

   }

   private int compareToRule(int month, int monthLen, int prevMonthLen, int dayOfMonth, int dayOfWeek, int millis, int millisDelta, int ruleMode, int ruleMonth, int ruleDayOfWeek, int ruleDay, int ruleMillis) {
      millis = millis + millisDelta;

      while(millis >= 86400000) {
         millis -= 86400000;
         ++dayOfMonth;
         dayOfWeek = 1 + dayOfWeek % 7;
         if(dayOfMonth > monthLen) {
            dayOfMonth = 1;
            ++month;
         }
      }

      for(; millis < 0; millis += 86400000) {
         --dayOfMonth;
         dayOfWeek = 1 + (dayOfWeek + 5) % 7;
         if(dayOfMonth < 1) {
            dayOfMonth = prevMonthLen;
            --month;
         }
      }

      if(month < ruleMonth) {
         return -1;
      } else if(month > ruleMonth) {
         return 1;
      } else {
         int ruleDayOfMonth = 0;
         if(ruleDay > monthLen) {
            ruleDay = monthLen;
         }

         switch(ruleMode) {
         case 1:
            ruleDayOfMonth = ruleDay;
            break;
         case 2:
            if(ruleDay > 0) {
               ruleDayOfMonth = 1 + (ruleDay - 1) * 7 + (7 + ruleDayOfWeek - (dayOfWeek - dayOfMonth + 1)) % 7;
            } else {
               ruleDayOfMonth = monthLen + (ruleDay + 1) * 7 - (7 + (dayOfWeek + monthLen - dayOfMonth) - ruleDayOfWeek) % 7;
            }
            break;
         case 3:
            ruleDayOfMonth = ruleDay + (49 + ruleDayOfWeek - ruleDay - dayOfWeek + dayOfMonth) % 7;
            break;
         case 4:
            ruleDayOfMonth = ruleDay - (49 - ruleDayOfWeek + ruleDay + dayOfWeek - dayOfMonth) % 7;
         }

         if(dayOfMonth < ruleDayOfMonth) {
            return -1;
         } else if(dayOfMonth > ruleDayOfMonth) {
            return 1;
         } else if(millis < ruleMillis) {
            return -1;
         } else if(millis > ruleMillis) {
            return 1;
         } else {
            return 0;
         }
      }
   }

   public boolean useDaylightTime() {
      return this.useDaylight;
   }

   public boolean observesDaylightTime() {
      return this.useDaylight;
   }

   public boolean inDaylightTime(Date date) {
      GregorianCalendar gc = new GregorianCalendar(this);
      gc.setTime(date);
      return gc.inDaylightTime();
   }

   private void construct(int _raw, int _startMonth, int _startDay, int _startDayOfWeek, int _startTime, int _startTimeMode, int _endMonth, int _endDay, int _endDayOfWeek, int _endTime, int _endTimeMode, int _dst) {
      this.raw = _raw;
      this.startMonth = _startMonth;
      this.startDay = _startDay;
      this.startDayOfWeek = _startDayOfWeek;
      this.startTime = _startTime;
      this.startTimeMode = _startTimeMode;
      this.endMonth = _endMonth;
      this.endDay = _endDay;
      this.endDayOfWeek = _endDayOfWeek;
      this.endTime = _endTime;
      this.endTimeMode = _endTimeMode;
      this.dst = _dst;
      this.startYear = 0;
      this.startMode = 1;
      this.endMode = 1;
      this.decodeRules();
      if(_dst <= 0) {
         throw new IllegalArgumentException();
      }
   }

   private void decodeRules() {
      this.decodeStartRule();
      this.decodeEndRule();
   }

   private void decodeStartRule() {
      this.useDaylight = this.startDay != 0 && this.endDay != 0;
      if(this.useDaylight && this.dst == 0) {
         this.dst = 86400000;
      }

      if(this.startDay != 0) {
         if(this.startMonth < 0 || this.startMonth > 11) {
            throw new IllegalArgumentException();
         }

         if(this.startTime < 0 || this.startTime > 86400000 || this.startTimeMode < 0 || this.startTimeMode > 2) {
            throw new IllegalArgumentException();
         }

         if(this.startDayOfWeek == 0) {
            this.startMode = 1;
         } else {
            if(this.startDayOfWeek > 0) {
               this.startMode = 2;
            } else {
               this.startDayOfWeek = -this.startDayOfWeek;
               if(this.startDay > 0) {
                  this.startMode = 3;
               } else {
                  this.startDay = -this.startDay;
                  this.startMode = 4;
               }
            }

            if(this.startDayOfWeek > 7) {
               throw new IllegalArgumentException();
            }
         }

         if(this.startMode == 2) {
            if(this.startDay < -5 || this.startDay > 5) {
               throw new IllegalArgumentException();
            }
         } else if(this.startDay < 1 || this.startDay > staticMonthLength[this.startMonth]) {
            throw new IllegalArgumentException();
         }
      }

   }

   private void decodeEndRule() {
      this.useDaylight = this.startDay != 0 && this.endDay != 0;
      if(this.useDaylight && this.dst == 0) {
         this.dst = 86400000;
      }

      if(this.endDay != 0) {
         if(this.endMonth < 0 || this.endMonth > 11) {
            throw new IllegalArgumentException();
         }

         if(this.endTime < 0 || this.endTime > 86400000 || this.endTimeMode < 0 || this.endTimeMode > 2) {
            throw new IllegalArgumentException();
         }

         if(this.endDayOfWeek == 0) {
            this.endMode = 1;
         } else {
            if(this.endDayOfWeek > 0) {
               this.endMode = 2;
            } else {
               this.endDayOfWeek = -this.endDayOfWeek;
               if(this.endDay > 0) {
                  this.endMode = 3;
               } else {
                  this.endDay = -this.endDay;
                  this.endMode = 4;
               }
            }

            if(this.endDayOfWeek > 7) {
               throw new IllegalArgumentException();
            }
         }

         if(this.endMode == 2) {
            if(this.endDay < -5 || this.endDay > 5) {
               throw new IllegalArgumentException();
            }
         } else if(this.endDay < 1 || this.endDay > staticMonthLength[this.endMonth]) {
            throw new IllegalArgumentException();
         }
      }

   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(obj != null && this.getClass() == obj.getClass()) {
         SimpleTimeZone that = (SimpleTimeZone)obj;
         return this.raw == that.raw && this.useDaylight == that.useDaylight && this.idEquals(this.getID(), that.getID()) && (!this.useDaylight || this.dst == that.dst && this.startMode == that.startMode && this.startMonth == that.startMonth && this.startDay == that.startDay && this.startDayOfWeek == that.startDayOfWeek && this.startTime == that.startTime && this.startTimeMode == that.startTimeMode && this.endMode == that.endMode && this.endMonth == that.endMonth && this.endDay == that.endDay && this.endDayOfWeek == that.endDayOfWeek && this.endTime == that.endTime && this.endTimeMode == that.endTimeMode && this.startYear == that.startYear);
      } else {
         return false;
      }
   }

   private boolean idEquals(String id1, String id2) {
      return id1 == null && id2 == null?true:(id1 != null && id2 != null?id1.equals(id2):false);
   }

   public int hashCode() {
      int ret = super.hashCode() + this.raw ^ (this.raw >>> 8) + (this.useDaylight?0:1);
      if(!this.useDaylight) {
         ret += this.dst ^ (this.dst >>> 10) + this.startMode ^ (this.startMode >>> 11) + this.startMonth ^ (this.startMonth >>> 12) + this.startDay ^ (this.startDay >>> 13) + this.startDayOfWeek ^ (this.startDayOfWeek >>> 14) + this.startTime ^ (this.startTime >>> 15) + this.startTimeMode ^ (this.startTimeMode >>> 16) + this.endMode ^ (this.endMode >>> 17) + this.endMonth ^ (this.endMonth >>> 18) + this.endDay ^ (this.endDay >>> 19) + this.endDayOfWeek ^ (this.endDayOfWeek >>> 20) + this.endTime ^ (this.endTime >>> 21) + this.endTimeMode ^ (this.endTimeMode >>> 22) + this.startYear ^ this.startYear >>> 23;
      }

      return ret;
   }

   public Object clone() {
      return this.isFrozen()?this:this.cloneAsThawed();
   }

   public boolean hasSameRules(TimeZone othr) {
      if(this == othr) {
         return true;
      } else if(!(othr instanceof SimpleTimeZone)) {
         return false;
      } else {
         SimpleTimeZone other = (SimpleTimeZone)othr;
         return other != null && this.raw == other.raw && this.useDaylight == other.useDaylight && (!this.useDaylight || this.dst == other.dst && this.startMode == other.startMode && this.startMonth == other.startMonth && this.startDay == other.startDay && this.startDayOfWeek == other.startDayOfWeek && this.startTime == other.startTime && this.startTimeMode == other.startTimeMode && this.endMode == other.endMode && this.endMonth == other.endMonth && this.endDay == other.endDay && this.endDayOfWeek == other.endDayOfWeek && this.endTime == other.endTime && this.endTimeMode == other.endTimeMode && this.startYear == other.startYear);
      }
   }

   public TimeZoneTransition getNextTransition(long base, boolean inclusive) {
      if(!this.useDaylight) {
         return null;
      } else {
         this.initTransitionRules();
         long firstTransitionTime = this.firstTransition.getTime();
         if(base >= firstTransitionTime && (!inclusive || base != firstTransitionTime)) {
            Date stdDate = this.stdRule.getNextStart(base, this.dstRule.getRawOffset(), this.dstRule.getDSTSavings(), inclusive);
            Date dstDate = this.dstRule.getNextStart(base, this.stdRule.getRawOffset(), this.stdRule.getDSTSavings(), inclusive);
            return stdDate == null || dstDate != null && !stdDate.before(dstDate)?(dstDate == null || stdDate != null && !dstDate.before(stdDate)?null:new TimeZoneTransition(dstDate.getTime(), this.stdRule, this.dstRule)):new TimeZoneTransition(stdDate.getTime(), this.dstRule, this.stdRule);
         } else {
            return this.firstTransition;
         }
      }
   }

   public TimeZoneTransition getPreviousTransition(long base, boolean inclusive) {
      if(!this.useDaylight) {
         return null;
      } else {
         this.initTransitionRules();
         long firstTransitionTime = this.firstTransition.getTime();
         if(base >= firstTransitionTime && (inclusive || base != firstTransitionTime)) {
            Date stdDate = this.stdRule.getPreviousStart(base, this.dstRule.getRawOffset(), this.dstRule.getDSTSavings(), inclusive);
            Date dstDate = this.dstRule.getPreviousStart(base, this.stdRule.getRawOffset(), this.stdRule.getDSTSavings(), inclusive);
            return stdDate == null || dstDate != null && !stdDate.after(dstDate)?(dstDate == null || stdDate != null && !dstDate.after(stdDate)?null:new TimeZoneTransition(dstDate.getTime(), this.stdRule, this.dstRule)):new TimeZoneTransition(stdDate.getTime(), this.dstRule, this.stdRule);
         } else {
            return null;
         }
      }
   }

   public TimeZoneRule[] getTimeZoneRules() {
      this.initTransitionRules();
      int size = this.useDaylight?3:1;
      TimeZoneRule[] rules = new TimeZoneRule[size];
      rules[0] = this.initialRule;
      if(this.useDaylight) {
         rules[1] = this.stdRule;
         rules[2] = this.dstRule;
      }

      return rules;
   }

   private synchronized void initTransitionRules() {
      if(!this.transitionRulesInitialized) {
         if(this.useDaylight) {
            DateTimeRule dtRule = null;
            int timeRuleType = this.startTimeMode == 1?1:(this.startTimeMode == 2?2:0);
            switch(this.startMode) {
            case 1:
               dtRule = new DateTimeRule(this.startMonth, this.startDay, this.startTime, timeRuleType);
               break;
            case 2:
               dtRule = new DateTimeRule(this.startMonth, this.startDay, this.startDayOfWeek, this.startTime, timeRuleType);
               break;
            case 3:
               dtRule = new DateTimeRule(this.startMonth, this.startDay, this.startDayOfWeek, true, this.startTime, timeRuleType);
               break;
            case 4:
               dtRule = new DateTimeRule(this.startMonth, this.startDay, this.startDayOfWeek, false, this.startTime, timeRuleType);
            }

            this.dstRule = new AnnualTimeZoneRule(this.getID() + "(DST)", this.getRawOffset(), this.getDSTSavings(), dtRule, this.startYear, Integer.MAX_VALUE);
            long firstDstStart = this.dstRule.getFirstStart(this.getRawOffset(), 0).getTime();
            timeRuleType = this.endTimeMode == 1?1:(this.endTimeMode == 2?2:0);
            switch(this.endMode) {
            case 1:
               dtRule = new DateTimeRule(this.endMonth, this.endDay, this.endTime, timeRuleType);
               break;
            case 2:
               dtRule = new DateTimeRule(this.endMonth, this.endDay, this.endDayOfWeek, this.endTime, timeRuleType);
               break;
            case 3:
               dtRule = new DateTimeRule(this.endMonth, this.endDay, this.endDayOfWeek, true, this.endTime, timeRuleType);
               break;
            case 4:
               dtRule = new DateTimeRule(this.endMonth, this.endDay, this.endDayOfWeek, false, this.endTime, timeRuleType);
            }

            this.stdRule = new AnnualTimeZoneRule(this.getID() + "(STD)", this.getRawOffset(), 0, dtRule, this.startYear, Integer.MAX_VALUE);
            long firstStdStart = this.stdRule.getFirstStart(this.getRawOffset(), this.dstRule.getDSTSavings()).getTime();
            if(firstStdStart < firstDstStart) {
               this.initialRule = new InitialTimeZoneRule(this.getID() + "(DST)", this.getRawOffset(), this.dstRule.getDSTSavings());
               this.firstTransition = new TimeZoneTransition(firstStdStart, this.initialRule, this.stdRule);
            } else {
               this.initialRule = new InitialTimeZoneRule(this.getID() + "(STD)", this.getRawOffset(), 0);
               this.firstTransition = new TimeZoneTransition(firstDstStart, this.initialRule, this.dstRule);
            }
         } else {
            this.initialRule = new InitialTimeZoneRule(this.getID(), this.getRawOffset(), 0);
         }

         this.transitionRulesInitialized = true;
      }
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   public TimeZone freeze() {
      this.isFrozen = true;
      return this;
   }

   public TimeZone cloneAsThawed() {
      SimpleTimeZone tz = (SimpleTimeZone)super.cloneAsThawed();
      tz.isFrozen = false;
      return tz;
   }
}
