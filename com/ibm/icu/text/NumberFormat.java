package com.ibm.icu.text;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.text.UFormat;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.CurrencyAmount;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;

public abstract class NumberFormat extends UFormat {
   public static final int NUMBERSTYLE = 0;
   public static final int CURRENCYSTYLE = 1;
   public static final int PERCENTSTYLE = 2;
   public static final int SCIENTIFICSTYLE = 3;
   public static final int INTEGERSTYLE = 4;
   public static final int ISOCURRENCYSTYLE = 5;
   public static final int PLURALCURRENCYSTYLE = 6;
   public static final int INTEGER_FIELD = 0;
   public static final int FRACTION_FIELD = 1;
   private static NumberFormat.NumberFormatShim shim;
   private static final char[] doubleCurrencySign = new char[]{'¤', '¤'};
   private static final String doubleCurrencyStr = new String(doubleCurrencySign);
   private boolean groupingUsed = true;
   private byte maxIntegerDigits = 40;
   private byte minIntegerDigits = 1;
   private byte maxFractionDigits = 3;
   private byte minFractionDigits = 0;
   private boolean parseIntegerOnly = false;
   private int maximumIntegerDigits = 40;
   private int minimumIntegerDigits = 1;
   private int maximumFractionDigits = 3;
   private int minimumFractionDigits = 0;
   private Currency currency;
   static final int currentSerialVersion = 1;
   private int serialVersionOnStream = 1;
   private static final long serialVersionUID = -2308460125733713944L;
   private boolean parseStrict;

   public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
      if(number instanceof Long) {
         return this.format(((Long)number).longValue(), toAppendTo, pos);
      } else if(number instanceof BigInteger) {
         return this.format((BigInteger)number, toAppendTo, pos);
      } else if(number instanceof BigDecimal) {
         return this.format((BigDecimal)number, toAppendTo, pos);
      } else if(number instanceof com.ibm.icu.math.BigDecimal) {
         return this.format((com.ibm.icu.math.BigDecimal)number, toAppendTo, pos);
      } else if(number instanceof CurrencyAmount) {
         return this.format((CurrencyAmount)number, toAppendTo, pos);
      } else if(number instanceof Number) {
         return this.format(((Number)number).doubleValue(), toAppendTo, pos);
      } else {
         throw new IllegalArgumentException("Cannot format given Object as a Number");
      }
   }

   public final Object parseObject(String source, ParsePosition parsePosition) {
      return this.parse(source, parsePosition);
   }

   public final String format(double number) {
      return this.format(number, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public final String format(long number) {
      StringBuffer buf = new StringBuffer(19);
      FieldPosition pos = new FieldPosition(0);
      this.format(number, buf, pos);
      return buf.toString();
   }

   public final String format(BigInteger number) {
      return this.format(number, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public final String format(BigDecimal number) {
      return this.format(number, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public final String format(com.ibm.icu.math.BigDecimal number) {
      return this.format(number, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public final String format(CurrencyAmount currAmt) {
      return this.format(currAmt, new StringBuffer(), new FieldPosition(0)).toString();
   }

   public abstract StringBuffer format(double var1, StringBuffer var3, FieldPosition var4);

   public abstract StringBuffer format(long var1, StringBuffer var3, FieldPosition var4);

   public abstract StringBuffer format(BigInteger var1, StringBuffer var2, FieldPosition var3);

   public abstract StringBuffer format(BigDecimal var1, StringBuffer var2, FieldPosition var3);

   public abstract StringBuffer format(com.ibm.icu.math.BigDecimal var1, StringBuffer var2, FieldPosition var3);

   public StringBuffer format(CurrencyAmount currAmt, StringBuffer toAppendTo, FieldPosition pos) {
      Currency save = this.getCurrency();
      Currency curr = currAmt.getCurrency();
      boolean same = curr.equals(save);
      if(!same) {
         this.setCurrency(curr);
      }

      this.format((Object)currAmt.getNumber(), toAppendTo, pos);
      if(!same) {
         this.setCurrency(save);
      }

      return toAppendTo;
   }

   public abstract Number parse(String var1, ParsePosition var2);

   public Number parse(String text) throws ParseException {
      ParsePosition parsePosition = new ParsePosition(0);
      Number result = this.parse(text, parsePosition);
      if(parsePosition.getIndex() == 0) {
         throw new ParseException("Unparseable number: \"" + text + '\"', parsePosition.getErrorIndex());
      } else {
         return result;
      }
   }

   public CurrencyAmount parseCurrency(CharSequence text, ParsePosition pos) {
      Number n = this.parse(text.toString(), pos);
      return n == null?null:new CurrencyAmount(n, this.getEffectiveCurrency());
   }

   public boolean isParseIntegerOnly() {
      return this.parseIntegerOnly;
   }

   public void setParseIntegerOnly(boolean value) {
      this.parseIntegerOnly = value;
   }

   public void setParseStrict(boolean value) {
      this.parseStrict = value;
   }

   public boolean isParseStrict() {
      return this.parseStrict;
   }

   public static final NumberFormat getInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 0);
   }

   public static NumberFormat getInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 0);
   }

   public static NumberFormat getInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 0);
   }

   public static final NumberFormat getInstance(int style) {
      return getInstance(ULocale.getDefault(ULocale.Category.FORMAT), style);
   }

   public static NumberFormat getInstance(Locale inLocale, int style) {
      return getInstance(ULocale.forLocale(inLocale), style);
   }

   public static final NumberFormat getNumberInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 0);
   }

   public static NumberFormat getNumberInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 0);
   }

   public static NumberFormat getNumberInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 0);
   }

   public static final NumberFormat getIntegerInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 4);
   }

   public static NumberFormat getIntegerInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 4);
   }

   public static NumberFormat getIntegerInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 4);
   }

   public static final NumberFormat getCurrencyInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 1);
   }

   public static NumberFormat getCurrencyInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 1);
   }

   public static NumberFormat getCurrencyInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 1);
   }

   public static final NumberFormat getPercentInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 2);
   }

   public static NumberFormat getPercentInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 2);
   }

   public static NumberFormat getPercentInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 2);
   }

   public static final NumberFormat getScientificInstance() {
      return getInstance((ULocale)ULocale.getDefault(ULocale.Category.FORMAT), 3);
   }

   public static NumberFormat getScientificInstance(Locale inLocale) {
      return getInstance((ULocale)ULocale.forLocale(inLocale), 3);
   }

   public static NumberFormat getScientificInstance(ULocale inLocale) {
      return getInstance((ULocale)inLocale, 3);
   }

   private static NumberFormat.NumberFormatShim getShim() {
      if(shim == null) {
         try {
            Class<?> cls = Class.forName("com.ibm.icu.text.NumberFormatServiceShim");
            shim = (NumberFormat.NumberFormatShim)cls.newInstance();
         } catch (MissingResourceException var1) {
            throw var1;
         } catch (Exception var2) {
            throw new RuntimeException(var2.getMessage());
         }
      }

      return shim;
   }

   public static Locale[] getAvailableLocales() {
      return shim == null?ICUResourceBundle.getAvailableLocales():getShim().getAvailableLocales();
   }

   public static ULocale[] getAvailableULocales() {
      return shim == null?ICUResourceBundle.getAvailableULocales():getShim().getAvailableULocales();
   }

   public static Object registerFactory(NumberFormat.NumberFormatFactory factory) {
      if(factory == null) {
         throw new IllegalArgumentException("factory must not be null");
      } else {
         return getShim().registerFactory(factory);
      }
   }

   public static boolean unregister(Object registryKey) {
      if(registryKey == null) {
         throw new IllegalArgumentException("registryKey must not be null");
      } else {
         return shim == null?false:shim.unregister(registryKey);
      }
   }

   public int hashCode() {
      return this.maximumIntegerDigits * 37 + this.maxFractionDigits;
   }

   public boolean equals(Object obj) {
      if(obj == null) {
         return false;
      } else if(this == obj) {
         return true;
      } else if(this.getClass() != obj.getClass()) {
         return false;
      } else {
         NumberFormat other = (NumberFormat)obj;
         return this.maximumIntegerDigits == other.maximumIntegerDigits && this.minimumIntegerDigits == other.minimumIntegerDigits && this.maximumFractionDigits == other.maximumFractionDigits && this.minimumFractionDigits == other.minimumFractionDigits && this.groupingUsed == other.groupingUsed && this.parseIntegerOnly == other.parseIntegerOnly && this.parseStrict == other.parseStrict;
      }
   }

   public Object clone() {
      NumberFormat other = (NumberFormat)super.clone();
      return other;
   }

   public boolean isGroupingUsed() {
      return this.groupingUsed;
   }

   public void setGroupingUsed(boolean newValue) {
      this.groupingUsed = newValue;
   }

   public int getMaximumIntegerDigits() {
      return this.maximumIntegerDigits;
   }

   public void setMaximumIntegerDigits(int newValue) {
      this.maximumIntegerDigits = Math.max(0, newValue);
      if(this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.minimumIntegerDigits = this.maximumIntegerDigits;
      }

   }

   public int getMinimumIntegerDigits() {
      return this.minimumIntegerDigits;
   }

   public void setMinimumIntegerDigits(int newValue) {
      this.minimumIntegerDigits = Math.max(0, newValue);
      if(this.minimumIntegerDigits > this.maximumIntegerDigits) {
         this.maximumIntegerDigits = this.minimumIntegerDigits;
      }

   }

   public int getMaximumFractionDigits() {
      return this.maximumFractionDigits;
   }

   public void setMaximumFractionDigits(int newValue) {
      this.maximumFractionDigits = Math.max(0, newValue);
      if(this.maximumFractionDigits < this.minimumFractionDigits) {
         this.minimumFractionDigits = this.maximumFractionDigits;
      }

   }

   public int getMinimumFractionDigits() {
      return this.minimumFractionDigits;
   }

   public void setMinimumFractionDigits(int newValue) {
      this.minimumFractionDigits = Math.max(0, newValue);
      if(this.maximumFractionDigits < this.minimumFractionDigits) {
         this.maximumFractionDigits = this.minimumFractionDigits;
      }

   }

   public void setCurrency(Currency theCurrency) {
      this.currency = theCurrency;
   }

   public Currency getCurrency() {
      return this.currency;
   }

   /** @deprecated */
   @Deprecated
   protected Currency getEffectiveCurrency() {
      Currency c = this.getCurrency();
      if(c == null) {
         ULocale uloc = this.getLocale(ULocale.VALID_LOCALE);
         if(uloc == null) {
            uloc = ULocale.getDefault(ULocale.Category.FORMAT);
         }

         c = Currency.getInstance(uloc);
      }

      return c;
   }

   public int getRoundingMode() {
      throw new UnsupportedOperationException("getRoundingMode must be implemented by the subclass implementation.");
   }

   public void setRoundingMode(int roundingMode) {
      throw new UnsupportedOperationException("setRoundingMode must be implemented by the subclass implementation.");
   }

   public static NumberFormat getInstance(ULocale desiredLocale, int choice) {
      if(choice >= 0 && choice <= 6) {
         return getShim().createInstance(desiredLocale, choice);
      } else {
         throw new IllegalArgumentException("choice should be from NUMBERSTYLE to PLURALCURRENCYSTYLE");
      }
   }

   static NumberFormat createInstance(ULocale desiredLocale, int choice) {
      String pattern = getPattern(desiredLocale, choice);
      DecimalFormatSymbols symbols = new DecimalFormatSymbols(desiredLocale);
      if(choice == 1 || choice == 5) {
         String temp = symbols.getCurrencyPattern();
         if(temp != null) {
            pattern = temp;
         }
      }

      if(choice == 5) {
         pattern = pattern.replace("¤", doubleCurrencyStr);
      }

      NumberingSystem ns = NumberingSystem.getInstance(desiredLocale);
      if(ns == null) {
         return null;
      } else {
         NumberFormat format;
         if(ns != null && ns.isAlgorithmic()) {
            int desiredRulesType = 4;
            String nsDesc = ns.getDescription();
            int firstSlash = nsDesc.indexOf("/");
            int lastSlash = nsDesc.lastIndexOf("/");
            String nsRuleSetName;
            ULocale nsLoc;
            if(lastSlash > firstSlash) {
               String nsLocID = nsDesc.substring(0, firstSlash);
               String nsRuleSetGroup = nsDesc.substring(firstSlash + 1, lastSlash);
               nsRuleSetName = nsDesc.substring(lastSlash + 1);
               nsLoc = new ULocale(nsLocID);
               if(nsRuleSetGroup.equals("SpelloutRules")) {
                  desiredRulesType = 1;
               }
            } else {
               nsLoc = desiredLocale;
               nsRuleSetName = nsDesc;
            }

            RuleBasedNumberFormat r = new RuleBasedNumberFormat(nsLoc, desiredRulesType);
            r.setDefaultRuleSet(nsRuleSetName);
            format = r;
         } else {
            DecimalFormat f = new DecimalFormat(pattern, symbols, choice);
            if(choice == 4) {
               f.setMaximumFractionDigits(0);
               f.setDecimalSeparatorAlwaysShown(false);
               f.setParseIntegerOnly(true);
            }

            format = f;
         }

         ULocale valid = symbols.getLocale(ULocale.VALID_LOCALE);
         ULocale actual = symbols.getLocale(ULocale.ACTUAL_LOCALE);
         format.setLocale(valid, actual);
         return format;
      }
   }

   /** @deprecated */
   @Deprecated
   protected static String getPattern(Locale forLocale, int choice) {
      return getPattern(ULocale.forLocale(forLocale), choice);
   }

   protected static String getPattern(ULocale forLocale, int choice) {
      int entry = choice == 4?0:(choice != 5 && choice != 6?choice:1);
      ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", forLocale);
      String[] numberPatternKeys = new String[]{"decimalFormat", "currencyFormat", "percentFormat", "scientificFormat"};
      NumberingSystem ns = NumberingSystem.getInstance(forLocale);
      String result = null;

      try {
         result = rb.getStringWithFallback("NumberElements/" + ns.getName() + "/patterns/" + numberPatternKeys[entry]);
      } catch (MissingResourceException var8) {
         result = rb.getStringWithFallback("NumberElements/latn/patterns/" + numberPatternKeys[entry]);
      }

      return result;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      if(this.serialVersionOnStream < 1) {
         this.maximumIntegerDigits = this.maxIntegerDigits;
         this.minimumIntegerDigits = this.minIntegerDigits;
         this.maximumFractionDigits = this.maxFractionDigits;
         this.minimumFractionDigits = this.minFractionDigits;
      }

      if(this.minimumIntegerDigits <= this.maximumIntegerDigits && this.minimumFractionDigits <= this.maximumFractionDigits && this.minimumIntegerDigits >= 0 && this.minimumFractionDigits >= 0) {
         this.serialVersionOnStream = 1;
      } else {
         throw new InvalidObjectException("Digit count range invalid");
      }
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      this.maxIntegerDigits = this.maximumIntegerDigits > 127?127:(byte)this.maximumIntegerDigits;
      this.minIntegerDigits = this.minimumIntegerDigits > 127?127:(byte)this.minimumIntegerDigits;
      this.maxFractionDigits = this.maximumFractionDigits > 127?127:(byte)this.maximumFractionDigits;
      this.minFractionDigits = this.minimumFractionDigits > 127?127:(byte)this.minimumFractionDigits;
      stream.defaultWriteObject();
   }

   public static class Field extends java.text.Format.Field {
      static final long serialVersionUID = -4516273749929385842L;
      public static final NumberFormat.Field SIGN = new NumberFormat.Field("sign");
      public static final NumberFormat.Field INTEGER = new NumberFormat.Field("integer");
      public static final NumberFormat.Field FRACTION = new NumberFormat.Field("fraction");
      public static final NumberFormat.Field EXPONENT = new NumberFormat.Field("exponent");
      public static final NumberFormat.Field EXPONENT_SIGN = new NumberFormat.Field("exponent sign");
      public static final NumberFormat.Field EXPONENT_SYMBOL = new NumberFormat.Field("exponent symbol");
      public static final NumberFormat.Field DECIMAL_SEPARATOR = new NumberFormat.Field("decimal separator");
      public static final NumberFormat.Field GROUPING_SEPARATOR = new NumberFormat.Field("grouping separator");
      public static final NumberFormat.Field PERCENT = new NumberFormat.Field("percent");
      public static final NumberFormat.Field PERMILLE = new NumberFormat.Field("per mille");
      public static final NumberFormat.Field CURRENCY = new NumberFormat.Field("currency");

      protected Field(String fieldName) {
         super(fieldName);
      }

      protected Object readResolve() throws InvalidObjectException {
         if(this.getName().equals(INTEGER.getName())) {
            return INTEGER;
         } else if(this.getName().equals(FRACTION.getName())) {
            return FRACTION;
         } else if(this.getName().equals(EXPONENT.getName())) {
            return EXPONENT;
         } else if(this.getName().equals(EXPONENT_SIGN.getName())) {
            return EXPONENT_SIGN;
         } else if(this.getName().equals(EXPONENT_SYMBOL.getName())) {
            return EXPONENT_SYMBOL;
         } else if(this.getName().equals(CURRENCY.getName())) {
            return CURRENCY;
         } else if(this.getName().equals(DECIMAL_SEPARATOR.getName())) {
            return DECIMAL_SEPARATOR;
         } else if(this.getName().equals(GROUPING_SEPARATOR.getName())) {
            return GROUPING_SEPARATOR;
         } else if(this.getName().equals(PERCENT.getName())) {
            return PERCENT;
         } else if(this.getName().equals(PERMILLE.getName())) {
            return PERMILLE;
         } else if(this.getName().equals(SIGN.getName())) {
            return SIGN;
         } else {
            throw new InvalidObjectException("An invalid object.");
         }
      }
   }

   public abstract static class NumberFormatFactory {
      public static final int FORMAT_NUMBER = 0;
      public static final int FORMAT_CURRENCY = 1;
      public static final int FORMAT_PERCENT = 2;
      public static final int FORMAT_SCIENTIFIC = 3;
      public static final int FORMAT_INTEGER = 4;

      public boolean visible() {
         return true;
      }

      public abstract Set getSupportedLocaleNames();

      public NumberFormat createFormat(ULocale loc, int formatType) {
         return this.createFormat(loc.toLocale(), formatType);
      }

      public NumberFormat createFormat(Locale loc, int formatType) {
         return this.createFormat(ULocale.forLocale(loc), formatType);
      }
   }

   abstract static class NumberFormatShim {
      abstract Locale[] getAvailableLocales();

      abstract ULocale[] getAvailableULocales();

      abstract Object registerFactory(NumberFormat.NumberFormatFactory var1);

      abstract boolean unregister(Object var1);

      abstract NumberFormat createInstance(ULocale var1, int var2);
   }

   public abstract static class SimpleNumberFormatFactory extends NumberFormat.NumberFormatFactory {
      final Set localeNames;
      final boolean visible;

      public SimpleNumberFormatFactory(Locale locale) {
         this(locale, true);
      }

      public SimpleNumberFormatFactory(Locale locale, boolean visible) {
         this.localeNames = Collections.singleton(ULocale.forLocale(locale).getBaseName());
         this.visible = visible;
      }

      public SimpleNumberFormatFactory(ULocale locale) {
         this(locale, true);
      }

      public SimpleNumberFormatFactory(ULocale locale, boolean visible) {
         this.localeNames = Collections.singleton(locale.getBaseName());
         this.visible = visible;
      }

      public final boolean visible() {
         return this.visible;
      }

      public final Set getSupportedLocaleNames() {
         return this.localeNames;
      }
   }
}
