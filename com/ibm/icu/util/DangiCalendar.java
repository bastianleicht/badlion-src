package com.ibm.icu.util;

import com.ibm.icu.util.ChineseCalendar;
import com.ibm.icu.util.InitialTimeZoneRule;
import com.ibm.icu.util.RuleBasedTimeZone;
import com.ibm.icu.util.TimeArrayTimeZoneRule;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.TimeZoneRule;
import com.ibm.icu.util.ULocale;
import java.util.Date;

/** @deprecated */
public class DangiCalendar extends ChineseCalendar {
   private static final long serialVersionUID = 8156297445349501985L;
   private static final int DANGI_EPOCH_YEAR = -2332;
   private static final TimeZone KOREA_ZONE;

   /** @deprecated */
   public DangiCalendar() {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
   }

   /** @deprecated */
   public DangiCalendar(Date date) {
      this(TimeZone.getDefault(), ULocale.getDefault(ULocale.Category.FORMAT));
      this.setTime(date);
   }

   /** @deprecated */
   public DangiCalendar(TimeZone zone, ULocale locale) {
      super(zone, locale, -2332, KOREA_ZONE);
   }

   /** @deprecated */
   public String getType() {
      return "dangi";
   }

   static {
      InitialTimeZoneRule initialTimeZone = new InitialTimeZoneRule("GMT+8", 28800000, 0);
      long[] millis1897 = new long[]{-2302128000000L};
      long[] millis1898 = new long[]{-2270592000000L};
      long[] millis1912 = new long[]{-1829088000000L};
      TimeZoneRule rule1897 = new TimeArrayTimeZoneRule("Korean 1897", 25200000, 0, millis1897, 1);
      TimeZoneRule rule1898to1911 = new TimeArrayTimeZoneRule("Korean 1898-1911", 28800000, 0, millis1898, 1);
      TimeZoneRule ruleFrom1912 = new TimeArrayTimeZoneRule("Korean 1912-", 32400000, 0, millis1912, 1);
      RuleBasedTimeZone tz = new RuleBasedTimeZone("KOREA_ZONE", initialTimeZone);
      tz.addTransitionRule(rule1897);
      tz.addTransitionRule(rule1898to1911);
      tz.addTransitionRule(ruleFrom1912);
      tz.freeze();
      KOREA_ZONE = tz;
   }
}
