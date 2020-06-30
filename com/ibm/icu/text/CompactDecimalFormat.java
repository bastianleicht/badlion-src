package com.ibm.icu.text;

import com.ibm.icu.text.CompactDecimalDataCache;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.AttributedCharacterIterator;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class CompactDecimalFormat extends DecimalFormat {
   private static final long serialVersionUID = 4716293295276629682L;
   private static final int POSITIVE_PREFIX = 0;
   private static final int POSITIVE_SUFFIX = 1;
   private static final int AFFIX_SIZE = 2;
   private static final CompactDecimalDataCache cache = new CompactDecimalDataCache();
   private final Map units;
   private final long[] divisor;
   private final String[] currencyAffixes;
   private final PluralRules pluralRules;

   public static CompactDecimalFormat getInstance(ULocale locale, CompactDecimalFormat.CompactStyle style) {
      return new CompactDecimalFormat(locale, style);
   }

   public static CompactDecimalFormat getInstance(Locale locale, CompactDecimalFormat.CompactStyle style) {
      return new CompactDecimalFormat(ULocale.forLocale(locale), style);
   }

   CompactDecimalFormat(ULocale locale, CompactDecimalFormat.CompactStyle style) {
      DecimalFormat format = (DecimalFormat)NumberFormat.getInstance(locale);
      CompactDecimalDataCache.Data data = this.getData(locale, style);
      this.units = data.units;
      this.divisor = data.divisors;
      this.applyPattern(format.toPattern());
      this.setDecimalFormatSymbols(format.getDecimalFormatSymbols());
      this.setMaximumSignificantDigits(3);
      this.setSignificantDigitsUsed(true);
      if(style == CompactDecimalFormat.CompactStyle.SHORT) {
         this.setGroupingUsed(false);
      }

      this.pluralRules = PluralRules.forLocale(locale);
      DecimalFormat currencyFormat = (DecimalFormat)NumberFormat.getCurrencyInstance(locale);
      this.currencyAffixes = new String[2];
      this.currencyAffixes[0] = currencyFormat.getPositivePrefix();
      this.currencyAffixes[1] = currencyFormat.getPositiveSuffix();
      this.setCurrency((Currency)null);
   }

   /** @deprecated */
   public CompactDecimalFormat(String pattern, DecimalFormatSymbols formatSymbols, String[] prefix, String[] suffix, long[] divisor, Collection debugCreationErrors, CompactDecimalFormat.CompactStyle style, String[] currencyAffixes) {
      if(prefix.length < 15) {
         this.recordError(debugCreationErrors, "Must have at least 15 prefix items.");
      }

      if(prefix.length != suffix.length || prefix.length != divisor.length) {
         this.recordError(debugCreationErrors, "Prefix, suffix, and divisor arrays must have the same length.");
      }

      long oldDivisor = 0L;
      Map<String, Integer> seen = new HashMap();

      for(int i = 0; i < prefix.length; ++i) {
         if(prefix[i] == null || suffix[i] == null) {
            this.recordError(debugCreationErrors, "Prefix or suffix is null for " + i);
         }

         int log = (int)Math.log10((double)divisor[i]);
         if(log > i) {
            this.recordError(debugCreationErrors, "Divisor[" + i + "] must be less than or equal to 10^" + i + ", but is: " + divisor[i]);
         }

         long roundTrip = (long)Math.pow(10.0D, (double)log);
         if(roundTrip != divisor[i]) {
            this.recordError(debugCreationErrors, "Divisor[" + i + "] must be a power of 10, but is: " + divisor[i]);
         }

         String key = prefix[i] + "\uffff" + suffix[i] + "\uffff" + (i - log);
         Integer old = (Integer)seen.get(key);
         if(old != null) {
            this.recordError(debugCreationErrors, "Collision between values for " + i + " and " + old + " for [prefix/suffix/index-log(divisor)" + key.replace('\uffff', ';'));
         } else {
            seen.put(key, Integer.valueOf(i));
         }

         if(divisor[i] < oldDivisor) {
            this.recordError(debugCreationErrors, "Bad divisor, the divisor for 10E" + i + "(" + divisor[i] + ") is less than the divisor for the divisor for 10E" + (i - 1) + "(" + oldDivisor + ")");
         }

         oldDivisor = divisor[i];
      }

      this.units = this.otherPluralVariant(prefix, suffix);
      this.divisor = (long[])divisor.clone();
      this.applyPattern(pattern);
      this.setDecimalFormatSymbols(formatSymbols);
      this.setMaximumSignificantDigits(2);
      this.setSignificantDigitsUsed(true);
      this.setGroupingUsed(false);
      this.currencyAffixes = (String[])currencyAffixes.clone();
      this.pluralRules = null;
      this.setCurrency((Currency)null);
   }

   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(!super.equals(obj)) {
         return false;
      } else {
         CompactDecimalFormat other = (CompactDecimalFormat)obj;
         return this.mapsAreEqual(this.units, other.units) && Arrays.equals(this.divisor, other.divisor) && Arrays.equals(this.currencyAffixes, other.currencyAffixes) && this.pluralRules.equals(other.pluralRules);
      }
   }

   private boolean mapsAreEqual(Map lhs, Map rhs) {
      if(lhs.size() != rhs.size()) {
         return false;
      } else {
         for(Entry<String, DecimalFormat.Unit[]> entry : lhs.entrySet()) {
            DecimalFormat.Unit[] value = (DecimalFormat.Unit[])rhs.get(entry.getKey());
            if(value == null || !Arrays.equals((Object[])entry.getValue(), value)) {
               return false;
            }
         }

         return true;
      }
   }

   public StringBuffer format(double number, StringBuffer toAppendTo, FieldPosition pos) {
      CompactDecimalFormat.Amount amount = this.toAmount(number);
      DecimalFormat.Unit unit = amount.getUnit();
      unit.writePrefix(toAppendTo);
      super.format(amount.getQty(), toAppendTo, pos);
      unit.writeSuffix(toAppendTo);
      return toAppendTo;
   }

   public AttributedCharacterIterator formatToCharacterIterator(Object obj) {
      if(!(obj instanceof Number)) {
         throw new IllegalArgumentException();
      } else {
         Number number = (Number)obj;
         CompactDecimalFormat.Amount amount = this.toAmount(number.doubleValue());
         return super.formatToCharacterIterator(Double.valueOf(amount.getQty()), amount.getUnit());
      }
   }

   public StringBuffer format(long number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format((double)number, toAppendTo, pos);
   }

   public StringBuffer format(BigInteger number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format(number.doubleValue(), toAppendTo, pos);
   }

   public StringBuffer format(BigDecimal number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format(number.doubleValue(), toAppendTo, pos);
   }

   public StringBuffer format(com.ibm.icu.math.BigDecimal number, StringBuffer toAppendTo, FieldPosition pos) {
      return this.format(number.doubleValue(), toAppendTo, pos);
   }

   public Number parse(String text, ParsePosition parsePosition) {
      throw new UnsupportedOperationException();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      throw new NotSerializableException();
   }

   private void readObject(ObjectInputStream in) throws IOException {
      throw new NotSerializableException();
   }

   private CompactDecimalFormat.Amount toAmount(double number) {
      boolean negative = this.isNumberNegative(number);
      number = this.adjustNumberAsInFormatting(number);
      int base = number <= 1.0D?0:(int)Math.log10(number);
      if(base >= 15) {
         base = 14;
      }

      number = number / (double)this.divisor[base];
      String pluralVariant = this.getPluralForm(number);
      if(negative) {
         number = -number;
      }

      return new CompactDecimalFormat.Amount(number, CompactDecimalDataCache.getUnit(this.units, pluralVariant, base));
   }

   private void recordError(Collection creationErrors, String errorMessage) {
      if(creationErrors == null) {
         throw new IllegalArgumentException(errorMessage);
      } else {
         creationErrors.add(errorMessage);
      }
   }

   private Map otherPluralVariant(String[] prefix, String[] suffix) {
      Map<String, DecimalFormat.Unit[]> result = new HashMap();
      DecimalFormat.Unit[] units = new DecimalFormat.Unit[prefix.length];

      for(int i = 0; i < units.length; ++i) {
         units[i] = new DecimalFormat.Unit(prefix[i], suffix[i]);
      }

      result.put("other", units);
      return result;
   }

   private String getPluralForm(double number) {
      return this.pluralRules == null?"other":this.pluralRules.select(number);
   }

   private CompactDecimalDataCache.Data getData(ULocale locale, CompactDecimalFormat.CompactStyle style) {
      CompactDecimalDataCache.DataBundle bundle = cache.get(locale);
      switch(style) {
      case SHORT:
         return bundle.shortData;
      case LONG:
         return bundle.longData;
      default:
         return bundle.shortData;
      }
   }

   private static class Amount {
      private final double qty;
      private final DecimalFormat.Unit unit;

      public Amount(double qty, DecimalFormat.Unit unit) {
         this.qty = qty;
         this.unit = unit;
      }

      public double getQty() {
         return this.qty;
      }

      public DecimalFormat.Unit getUnit() {
         return this.unit;
      }
   }

   public static enum CompactStyle {
      SHORT,
      LONG;
   }
}
