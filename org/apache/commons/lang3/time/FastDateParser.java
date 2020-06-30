package org.apache.commons.lang3.time;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.time.DateParser;

public class FastDateParser implements DateParser, Serializable {
   private static final long serialVersionUID = 2L;
   static final Locale JAPANESE_IMPERIAL = new Locale("ja", "JP", "JP");
   private final String pattern;
   private final TimeZone timeZone;
   private final Locale locale;
   private final int century;
   private final int startYear;
   private transient Pattern parsePattern;
   private transient FastDateParser.Strategy[] strategies;
   private transient String currentFormatField;
   private transient FastDateParser.Strategy nextStrategy;
   private static final Pattern formatPattern = Pattern.compile("D+|E+|F+|G+|H+|K+|M+|S+|W+|Z+|a+|d+|h+|k+|m+|s+|w+|y+|z+|\'\'|\'[^\']++(\'\'[^\']*+)*+\'|[^\'A-Za-z]++");
   private static final ConcurrentMap[] caches = new ConcurrentMap[17];
   private static final FastDateParser.Strategy ABBREVIATED_YEAR_STRATEGY = new FastDateParser.NumberStrategy(1) {
      void setCalendar(FastDateParser parser, Calendar cal, String value) {
         int iValue = Integer.parseInt(value);
         if(iValue < 100) {
            iValue = parser.adjustYear(iValue);
         }

         cal.set(1, iValue);
      }
   };
   private static final FastDateParser.Strategy NUMBER_MONTH_STRATEGY = new FastDateParser.NumberStrategy(2) {
      int modify(int iValue) {
         return iValue - 1;
      }
   };
   private static final FastDateParser.Strategy LITERAL_YEAR_STRATEGY = new FastDateParser.NumberStrategy(1);
   private static final FastDateParser.Strategy WEEK_OF_YEAR_STRATEGY = new FastDateParser.NumberStrategy(3);
   private static final FastDateParser.Strategy WEEK_OF_MONTH_STRATEGY = new FastDateParser.NumberStrategy(4);
   private static final FastDateParser.Strategy DAY_OF_YEAR_STRATEGY = new FastDateParser.NumberStrategy(6);
   private static final FastDateParser.Strategy DAY_OF_MONTH_STRATEGY = new FastDateParser.NumberStrategy(5);
   private static final FastDateParser.Strategy DAY_OF_WEEK_IN_MONTH_STRATEGY = new FastDateParser.NumberStrategy(8);
   private static final FastDateParser.Strategy HOUR_OF_DAY_STRATEGY = new FastDateParser.NumberStrategy(11);
   private static final FastDateParser.Strategy MODULO_HOUR_OF_DAY_STRATEGY = new FastDateParser.NumberStrategy(11) {
      int modify(int iValue) {
         return iValue % 24;
      }
   };
   private static final FastDateParser.Strategy MODULO_HOUR_STRATEGY = new FastDateParser.NumberStrategy(10) {
      int modify(int iValue) {
         return iValue % 12;
      }
   };
   private static final FastDateParser.Strategy HOUR_STRATEGY = new FastDateParser.NumberStrategy(10);
   private static final FastDateParser.Strategy MINUTE_STRATEGY = new FastDateParser.NumberStrategy(12);
   private static final FastDateParser.Strategy SECOND_STRATEGY = new FastDateParser.NumberStrategy(13);
   private static final FastDateParser.Strategy MILLISECOND_STRATEGY = new FastDateParser.NumberStrategy(14);

   protected FastDateParser(String pattern, TimeZone timeZone, Locale locale) {
      this(pattern, timeZone, locale, (Date)null);
   }

   protected FastDateParser(String pattern, TimeZone timeZone, Locale locale, Date centuryStart) {
      this.pattern = pattern;
      this.timeZone = timeZone;
      this.locale = locale;
      Calendar definingCalendar = Calendar.getInstance(timeZone, locale);
      int centuryStartYear;
      if(centuryStart != null) {
         definingCalendar.setTime(centuryStart);
         centuryStartYear = definingCalendar.get(1);
      } else if(locale.equals(JAPANESE_IMPERIAL)) {
         centuryStartYear = 0;
      } else {
         definingCalendar.setTime(new Date());
         centuryStartYear = definingCalendar.get(1) - 80;
      }

      this.century = centuryStartYear / 100 * 100;
      this.startYear = centuryStartYear - this.century;
      this.init(definingCalendar);
   }

   private void init(Calendar definingCalendar) {
      StringBuilder regex = new StringBuilder();
      List<FastDateParser.Strategy> collector = new ArrayList();
      Matcher patternMatcher = formatPattern.matcher(this.pattern);
      if(!patternMatcher.lookingAt()) {
         throw new IllegalArgumentException("Illegal pattern character \'" + this.pattern.charAt(patternMatcher.regionStart()) + "\'");
      } else {
         this.currentFormatField = patternMatcher.group();
         FastDateParser.Strategy currentStrategy = this.getStrategy(this.currentFormatField, definingCalendar);

         while(true) {
            patternMatcher.region(patternMatcher.end(), patternMatcher.regionEnd());
            if(!patternMatcher.lookingAt()) {
               this.nextStrategy = null;
               if(patternMatcher.regionStart() != patternMatcher.regionEnd()) {
                  throw new IllegalArgumentException("Failed to parse \"" + this.pattern + "\" ; gave up at index " + patternMatcher.regionStart());
               }

               if(currentStrategy.addRegex(this, regex)) {
                  collector.add(currentStrategy);
               }

               this.currentFormatField = null;
               this.strategies = (FastDateParser.Strategy[])collector.toArray(new FastDateParser.Strategy[collector.size()]);
               this.parsePattern = Pattern.compile(regex.toString());
               return;
            }

            String nextFormatField = patternMatcher.group();
            this.nextStrategy = this.getStrategy(nextFormatField, definingCalendar);
            if(currentStrategy.addRegex(this, regex)) {
               collector.add(currentStrategy);
            }

            this.currentFormatField = nextFormatField;
            currentStrategy = this.nextStrategy;
         }
      }
   }

   public String getPattern() {
      return this.pattern;
   }

   public TimeZone getTimeZone() {
      return this.timeZone;
   }

   public Locale getLocale() {
      return this.locale;
   }

   Pattern getParsePattern() {
      return this.parsePattern;
   }

   public boolean equals(Object obj) {
      if(!(obj instanceof FastDateParser)) {
         return false;
      } else {
         FastDateParser other = (FastDateParser)obj;
         return this.pattern.equals(other.pattern) && this.timeZone.equals(other.timeZone) && this.locale.equals(other.locale);
      }
   }

   public int hashCode() {
      return this.pattern.hashCode() + 13 * (this.timeZone.hashCode() + 13 * this.locale.hashCode());
   }

   public String toString() {
      return "FastDateParser[" + this.pattern + "," + this.locale + "," + this.timeZone.getID() + "]";
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      Calendar definingCalendar = Calendar.getInstance(this.timeZone, this.locale);
      this.init(definingCalendar);
   }

   public Object parseObject(String source) throws ParseException {
      return this.parse(source);
   }

   public Date parse(String source) throws ParseException {
      Date date = this.parse(source, new ParsePosition(0));
      if(date == null) {
         if(this.locale.equals(JAPANESE_IMPERIAL)) {
            throw new ParseException("(The " + this.locale + " locale does not support dates before 1868 AD)\n" + "Unparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0);
         } else {
            throw new ParseException("Unparseable date: \"" + source + "\" does not match " + this.parsePattern.pattern(), 0);
         }
      } else {
         return date;
      }
   }

   public Object parseObject(String source, ParsePosition pos) {
      return this.parse(source, pos);
   }

   public Date parse(String source, ParsePosition pos) {
      int offset = pos.getIndex();
      Matcher matcher = this.parsePattern.matcher(source.substring(offset));
      if(!matcher.lookingAt()) {
         return null;
      } else {
         Calendar cal = Calendar.getInstance(this.timeZone, this.locale);
         cal.clear();
         int i = 0;

         while(i < this.strategies.length) {
            FastDateParser.Strategy strategy = this.strategies[i++];
            strategy.setCalendar(this, cal, matcher.group(i));
         }

         pos.setIndex(offset + matcher.end());
         return cal.getTime();
      }
   }

   private static StringBuilder escapeRegex(StringBuilder regex, String value, boolean unquote) {
      regex.append("\\Q");

      for(int i = 0; i < value.length(); ++i) {
         char c = value.charAt(i);
         switch(c) {
         case '\'':
            if(unquote) {
               ++i;
               if(i == value.length()) {
                  return regex;
               }

               c = value.charAt(i);
            }
            break;
         case '\\':
            ++i;
            if(i != value.length()) {
               regex.append(c);
               c = value.charAt(i);
               if(c == 69) {
                  regex.append("E\\\\E\\");
                  c = 81;
               }
            }
         }

         regex.append(c);
      }

      regex.append("\\E");
      return regex;
   }

   private static Map getDisplayNames(int field, Calendar definingCalendar, Locale locale) {
      return definingCalendar.getDisplayNames(field, 0, locale);
   }

   private int adjustYear(int twoDigitYear) {
      int trial = this.century + twoDigitYear;
      return twoDigitYear >= this.startYear?trial:trial + 100;
   }

   boolean isNextNumber() {
      return this.nextStrategy != null && this.nextStrategy.isNumber();
   }

   int getFieldWidth() {
      return this.currentFormatField.length();
   }

   private FastDateParser.Strategy getStrategy(String formatField, Calendar definingCalendar) {
      switch(formatField.charAt(0)) {
      case '\'':
         if(formatField.length() > 2) {
            return new FastDateParser.CopyQuotedStrategy(formatField.substring(1, formatField.length() - 1));
         }
      case '(':
      case ')':
      case '*':
      case '+':
      case ',':
      case '-':
      case '.':
      case '/':
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case ':':
      case ';':
      case '<':
      case '=':
      case '>':
      case '?':
      case '@':
      case 'A':
      case 'B':
      case 'C':
      case 'I':
      case 'J':
      case 'L':
      case 'N':
      case 'O':
      case 'P':
      case 'Q':
      case 'R':
      case 'T':
      case 'U':
      case 'V':
      case 'X':
      case 'Y':
      case '[':
      case '\\':
      case ']':
      case '^':
      case '_':
      case '`':
      case 'b':
      case 'c':
      case 'e':
      case 'f':
      case 'g':
      case 'i':
      case 'j':
      case 'l':
      case 'n':
      case 'o':
      case 'p':
      case 'q':
      case 'r':
      case 't':
      case 'u':
      case 'v':
      case 'x':
      default:
         return new FastDateParser.CopyQuotedStrategy(formatField);
      case 'D':
         return DAY_OF_YEAR_STRATEGY;
      case 'E':
         return this.getLocaleSpecificStrategy(7, definingCalendar);
      case 'F':
         return DAY_OF_WEEK_IN_MONTH_STRATEGY;
      case 'G':
         return this.getLocaleSpecificStrategy(0, definingCalendar);
      case 'H':
         return MODULO_HOUR_OF_DAY_STRATEGY;
      case 'K':
         return HOUR_STRATEGY;
      case 'M':
         return formatField.length() >= 3?this.getLocaleSpecificStrategy(2, definingCalendar):NUMBER_MONTH_STRATEGY;
      case 'S':
         return MILLISECOND_STRATEGY;
      case 'W':
         return WEEK_OF_MONTH_STRATEGY;
      case 'Z':
      case 'z':
         return this.getLocaleSpecificStrategy(15, definingCalendar);
      case 'a':
         return this.getLocaleSpecificStrategy(9, definingCalendar);
      case 'd':
         return DAY_OF_MONTH_STRATEGY;
      case 'h':
         return MODULO_HOUR_STRATEGY;
      case 'k':
         return HOUR_OF_DAY_STRATEGY;
      case 'm':
         return MINUTE_STRATEGY;
      case 's':
         return SECOND_STRATEGY;
      case 'w':
         return WEEK_OF_YEAR_STRATEGY;
      case 'y':
         return formatField.length() > 2?LITERAL_YEAR_STRATEGY:ABBREVIATED_YEAR_STRATEGY;
      }
   }

   private static ConcurrentMap getCache(int field) {
      synchronized(caches) {
         if(caches[field] == null) {
            caches[field] = new ConcurrentHashMap(3);
         }

         return caches[field];
      }
   }

   private FastDateParser.Strategy getLocaleSpecificStrategy(int field, Calendar definingCalendar) {
      ConcurrentMap<Locale, FastDateParser.Strategy> cache = getCache(field);
      FastDateParser.Strategy strategy = (FastDateParser.Strategy)cache.get(this.locale);
      if(strategy == null) {
         strategy = (FastDateParser.Strategy)(field == 15?new FastDateParser.TimeZoneStrategy(this.locale):new FastDateParser.TextStrategy(field, definingCalendar, this.locale));
         FastDateParser.Strategy inCache = (FastDateParser.Strategy)cache.putIfAbsent(this.locale, strategy);
         if(inCache != null) {
            return inCache;
         }
      }

      return strategy;
   }

   private static class CopyQuotedStrategy extends FastDateParser.Strategy {
      private final String formatField;

      CopyQuotedStrategy(String formatField) {
         super(null);
         this.formatField = formatField;
      }

      boolean isNumber() {
         char c = this.formatField.charAt(0);
         if(c == 39) {
            c = this.formatField.charAt(1);
         }

         return Character.isDigit(c);
      }

      boolean addRegex(FastDateParser parser, StringBuilder regex) {
         FastDateParser.escapeRegex(regex, this.formatField, true);
         return false;
      }
   }

   private static class NumberStrategy extends FastDateParser.Strategy {
      private final int field;

      NumberStrategy(int field) {
         super(null);
         this.field = field;
      }

      boolean isNumber() {
         return true;
      }

      boolean addRegex(FastDateParser parser, StringBuilder regex) {
         if(parser.isNextNumber()) {
            regex.append("(\\p{Nd}{").append(parser.getFieldWidth()).append("}+)");
         } else {
            regex.append("(\\p{Nd}++)");
         }

         return true;
      }

      void setCalendar(FastDateParser parser, Calendar cal, String value) {
         cal.set(this.field, this.modify(Integer.parseInt(value)));
      }

      int modify(int iValue) {
         return iValue;
      }
   }

   private abstract static class Strategy {
      private Strategy() {
      }

      boolean isNumber() {
         return false;
      }

      void setCalendar(FastDateParser parser, Calendar cal, String value) {
      }

      abstract boolean addRegex(FastDateParser var1, StringBuilder var2);
   }

   private static class TextStrategy extends FastDateParser.Strategy {
      private final int field;
      private final Map keyValues;

      TextStrategy(int field, Calendar definingCalendar, Locale locale) {
         super(null);
         this.field = field;
         this.keyValues = FastDateParser.getDisplayNames(field, definingCalendar, locale);
      }

      boolean addRegex(FastDateParser parser, StringBuilder regex) {
         regex.append('(');

         for(String textKeyValue : this.keyValues.keySet()) {
            FastDateParser.escapeRegex(regex, textKeyValue, false).append('|');
         }

         regex.setCharAt(regex.length() - 1, ')');
         return true;
      }

      void setCalendar(FastDateParser parser, Calendar cal, String value) {
         Integer iVal = (Integer)this.keyValues.get(value);
         if(iVal != null) {
            cal.set(this.field, iVal.intValue());
         } else {
            StringBuilder sb = new StringBuilder(value);
            sb.append(" not in (");

            for(String textKeyValue : this.keyValues.keySet()) {
               sb.append(textKeyValue).append(' ');
            }

            sb.setCharAt(sb.length() - 1, ')');
            throw new IllegalArgumentException(sb.toString());
         }
      }
   }

   private static class TimeZoneStrategy extends FastDateParser.Strategy {
      private final String validTimeZoneChars;
      private final SortedMap tzNames = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      private static final int ID = 0;
      private static final int LONG_STD = 1;
      private static final int SHORT_STD = 2;
      private static final int LONG_DST = 3;
      private static final int SHORT_DST = 4;

      TimeZoneStrategy(Locale locale) {
         super(null);
         String[][] zones = DateFormatSymbols.getInstance(locale).getZoneStrings();

         for(String[] zone : zones) {
            if(!zone[0].startsWith("GMT")) {
               TimeZone tz = TimeZone.getTimeZone(zone[0]);
               if(!this.tzNames.containsKey(zone[1])) {
                  this.tzNames.put(zone[1], tz);
               }

               if(!this.tzNames.containsKey(zone[2])) {
                  this.tzNames.put(zone[2], tz);
               }

               if(tz.useDaylightTime()) {
                  if(!this.tzNames.containsKey(zone[3])) {
                     this.tzNames.put(zone[3], tz);
                  }

                  if(!this.tzNames.containsKey(zone[4])) {
                     this.tzNames.put(zone[4], tz);
                  }
               }
            }
         }

         StringBuilder sb = new StringBuilder();
         sb.append("(GMT[+\\-]\\d{0,1}\\d{2}|[+\\-]\\d{2}:?\\d{2}|");

         for(String id : this.tzNames.keySet()) {
            FastDateParser.escapeRegex(sb, id, false).append('|');
         }

         sb.setCharAt(sb.length() - 1, ')');
         this.validTimeZoneChars = sb.toString();
      }

      boolean addRegex(FastDateParser parser, StringBuilder regex) {
         regex.append(this.validTimeZoneChars);
         return true;
      }

      void setCalendar(FastDateParser parser, Calendar cal, String value) {
         TimeZone tz;
         if(value.charAt(0) != 43 && value.charAt(0) != 45) {
            if(value.startsWith("GMT")) {
               tz = TimeZone.getTimeZone(value);
            } else {
               tz = (TimeZone)this.tzNames.get(value);
               if(tz == null) {
                  throw new IllegalArgumentException(value + " is not a supported timezone name");
               }
            }
         } else {
            tz = TimeZone.getTimeZone("GMT" + value);
         }

         cal.setTimeZone(tz);
      }
   }
}
