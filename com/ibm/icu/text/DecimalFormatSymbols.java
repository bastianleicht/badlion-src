package com.ibm.icu.text;

import com.ibm.icu.impl.CurrencyData;
import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.text.NumberingSystem;
import com.ibm.icu.util.Currency;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.ChoiceFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;

public class DecimalFormatSymbols implements Cloneable, Serializable {
   public static final int CURRENCY_SPC_CURRENCY_MATCH = 0;
   public static final int CURRENCY_SPC_SURROUNDING_MATCH = 1;
   public static final int CURRENCY_SPC_INSERT = 2;
   private String[] currencySpcBeforeSym;
   private String[] currencySpcAfterSym;
   private char zeroDigit;
   private char[] digits;
   private char groupingSeparator;
   private char decimalSeparator;
   private char perMill;
   private char percent;
   private char digit;
   private char sigDigit;
   private char patternSeparator;
   private String infinity;
   private String NaN;
   private char minusSign;
   private String currencySymbol;
   private String intlCurrencySymbol;
   private char monetarySeparator;
   private char monetaryGroupingSeparator;
   private char exponential;
   private String exponentSeparator;
   private char padEscape;
   private char plusSign;
   private Locale requestedLocale;
   private ULocale ulocale;
   private static final long serialVersionUID = 5772796243397350300L;
   private static final int currentSerialVersion = 6;
   private int serialVersionOnStream = 6;
   private static final ICUCache cachedLocaleData = new SimpleCache();
   private String currencyPattern = null;
   private ULocale validLocale;
   private ULocale actualLocale;
   private transient Currency currency;

   public DecimalFormatSymbols() {
      this.initialize(ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public DecimalFormatSymbols(Locale locale) {
      this.initialize(ULocale.forLocale(locale));
   }

   public DecimalFormatSymbols(ULocale locale) {
      this.initialize(locale);
   }

   public static DecimalFormatSymbols getInstance() {
      return new DecimalFormatSymbols();
   }

   public static DecimalFormatSymbols getInstance(Locale locale) {
      return new DecimalFormatSymbols(locale);
   }

   public static DecimalFormatSymbols getInstance(ULocale locale) {
      return new DecimalFormatSymbols(locale);
   }

   public static Locale[] getAvailableLocales() {
      return ICUResourceBundle.getAvailableLocales();
   }

   public static ULocale[] getAvailableULocales() {
      return ICUResourceBundle.getAvailableULocales();
   }

   public char getZeroDigit() {
      return this.digits != null?this.digits[0]:this.zeroDigit;
   }

   public char[] getDigits() {
      if(this.digits != null) {
         return (char[])this.digits.clone();
      } else {
         char[] digitArray = new char[10];

         for(int i = 0; i < 10; ++i) {
            digitArray[i] = (char)(this.zeroDigit + i);
         }

         return digitArray;
      }
   }

   char[] getDigitsLocal() {
      if(this.digits != null) {
         return this.digits;
      } else {
         char[] digitArray = new char[10];

         for(int i = 0; i < 10; ++i) {
            digitArray[i] = (char)(this.zeroDigit + i);
         }

         return digitArray;
      }
   }

   public void setZeroDigit(char zeroDigit) {
      if(this.digits != null) {
         this.digits[0] = zeroDigit;
         if(Character.digit(zeroDigit, 10) == 0) {
            for(int i = 1; i < 10; ++i) {
               this.digits[i] = (char)(zeroDigit + i);
            }
         }
      } else {
         this.zeroDigit = zeroDigit;
      }

   }

   public char getSignificantDigit() {
      return this.sigDigit;
   }

   public void setSignificantDigit(char sigDigit) {
      this.sigDigit = sigDigit;
   }

   public char getGroupingSeparator() {
      return this.groupingSeparator;
   }

   public void setGroupingSeparator(char groupingSeparator) {
      this.groupingSeparator = groupingSeparator;
   }

   public char getDecimalSeparator() {
      return this.decimalSeparator;
   }

   public void setDecimalSeparator(char decimalSeparator) {
      this.decimalSeparator = decimalSeparator;
   }

   public char getPerMill() {
      return this.perMill;
   }

   public void setPerMill(char perMill) {
      this.perMill = perMill;
   }

   public char getPercent() {
      return this.percent;
   }

   public void setPercent(char percent) {
      this.percent = percent;
   }

   public char getDigit() {
      return this.digit;
   }

   public void setDigit(char digit) {
      this.digit = digit;
   }

   public char getPatternSeparator() {
      return this.patternSeparator;
   }

   public void setPatternSeparator(char patternSeparator) {
      this.patternSeparator = patternSeparator;
   }

   public String getInfinity() {
      return this.infinity;
   }

   public void setInfinity(String infinity) {
      this.infinity = infinity;
   }

   public String getNaN() {
      return this.NaN;
   }

   public void setNaN(String NaN) {
      this.NaN = NaN;
   }

   public char getMinusSign() {
      return this.minusSign;
   }

   public void setMinusSign(char minusSign) {
      this.minusSign = minusSign;
   }

   public String getCurrencySymbol() {
      return this.currencySymbol;
   }

   public void setCurrencySymbol(String currency) {
      this.currencySymbol = currency;
   }

   public String getInternationalCurrencySymbol() {
      return this.intlCurrencySymbol;
   }

   public void setInternationalCurrencySymbol(String currency) {
      this.intlCurrencySymbol = currency;
   }

   public Currency getCurrency() {
      return this.currency;
   }

   public void setCurrency(Currency currency) {
      if(currency == null) {
         throw new NullPointerException();
      } else {
         this.currency = currency;
         this.intlCurrencySymbol = currency.getCurrencyCode();
         this.currencySymbol = currency.getSymbol(this.requestedLocale);
      }
   }

   public char getMonetaryDecimalSeparator() {
      return this.monetarySeparator;
   }

   public char getMonetaryGroupingSeparator() {
      return this.monetaryGroupingSeparator;
   }

   String getCurrencyPattern() {
      return this.currencyPattern;
   }

   public void setMonetaryDecimalSeparator(char sep) {
      this.monetarySeparator = sep;
   }

   public void setMonetaryGroupingSeparator(char sep) {
      this.monetaryGroupingSeparator = sep;
   }

   public String getExponentSeparator() {
      return this.exponentSeparator;
   }

   public void setExponentSeparator(String exp) {
      this.exponentSeparator = exp;
   }

   public char getPlusSign() {
      return this.plusSign;
   }

   public void setPlusSign(char plus) {
      this.plusSign = plus;
   }

   public char getPadEscape() {
      return this.padEscape;
   }

   public void setPadEscape(char c) {
      this.padEscape = c;
   }

   public String getPatternForCurrencySpacing(int itemType, boolean beforeCurrency) {
      if(itemType >= 0 && itemType <= 2) {
         return beforeCurrency?this.currencySpcBeforeSym[itemType]:this.currencySpcAfterSym[itemType];
      } else {
         throw new IllegalArgumentException("unknown currency spacing: " + itemType);
      }
   }

   public void setPatternForCurrencySpacing(int itemType, boolean beforeCurrency, String pattern) {
      if(itemType >= 0 && itemType <= 2) {
         if(beforeCurrency) {
            this.currencySpcBeforeSym[itemType] = pattern;
         } else {
            this.currencySpcAfterSym[itemType] = pattern;
         }

      } else {
         throw new IllegalArgumentException("unknown currency spacing: " + itemType);
      }
   }

   public Locale getLocale() {
      return this.requestedLocale;
   }

   public ULocale getULocale() {
      return this.ulocale;
   }

   public Object clone() {
      try {
         return (DecimalFormatSymbols)super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException();
      }
   }

   public boolean equals(Object obj) {
      if(!(obj instanceof DecimalFormatSymbols)) {
         return false;
      } else if(this == obj) {
         return true;
      } else {
         DecimalFormatSymbols other = (DecimalFormatSymbols)obj;

         for(int i = 0; i <= 2; ++i) {
            if(!this.currencySpcBeforeSym[i].equals(other.currencySpcBeforeSym[i])) {
               return false;
            }

            if(!this.currencySpcAfterSym[i].equals(other.currencySpcAfterSym[i])) {
               return false;
            }
         }

         if(other.digits == null) {
            for(int i = 0; i < 10; ++i) {
               if(this.digits[i] != other.zeroDigit + i) {
                  return false;
               }
            }
         } else if(!Arrays.equals(this.digits, other.digits)) {
            return false;
         }

         return this.groupingSeparator == other.groupingSeparator && this.decimalSeparator == other.decimalSeparator && this.percent == other.percent && this.perMill == other.perMill && this.digit == other.digit && this.minusSign == other.minusSign && this.patternSeparator == other.patternSeparator && this.infinity.equals(other.infinity) && this.NaN.equals(other.NaN) && this.currencySymbol.equals(other.currencySymbol) && this.intlCurrencySymbol.equals(other.intlCurrencySymbol) && this.padEscape == other.padEscape && this.plusSign == other.plusSign && this.exponentSeparator.equals(other.exponentSeparator) && this.monetarySeparator == other.monetarySeparator && this.monetaryGroupingSeparator == other.monetaryGroupingSeparator;
      }
   }

   public int hashCode() {
      int result = this.digits[0];
      result = result * 37 + this.groupingSeparator;
      result = result * 37 + this.decimalSeparator;
      return result;
   }

   private void initialize(ULocale locale) {
      this.requestedLocale = locale.toLocale();
      this.ulocale = locale;
      NumberingSystem ns = NumberingSystem.getInstance(locale);
      this.digits = new char[10];
      String nsName;
      if(ns != null && ns.getRadix() == 10 && !ns.isAlgorithmic() && NumberingSystem.isValidDigitString(ns.getDescription())) {
         String digitString = ns.getDescription();
         this.digits[0] = digitString.charAt(0);
         this.digits[1] = digitString.charAt(1);
         this.digits[2] = digitString.charAt(2);
         this.digits[3] = digitString.charAt(3);
         this.digits[4] = digitString.charAt(4);
         this.digits[5] = digitString.charAt(5);
         this.digits[6] = digitString.charAt(6);
         this.digits[7] = digitString.charAt(7);
         this.digits[8] = digitString.charAt(8);
         this.digits[9] = digitString.charAt(9);
         nsName = ns.getName();
      } else {
         this.digits[0] = 48;
         this.digits[1] = 49;
         this.digits[2] = 50;
         this.digits[3] = 51;
         this.digits[4] = 52;
         this.digits[5] = 53;
         this.digits[6] = 54;
         this.digits[7] = 55;
         this.digits[8] = 56;
         this.digits[9] = 57;
         nsName = "latn";
      }

      String[][] data = (String[][])cachedLocaleData.get(locale);
      if(data == null) {
         data = new String[1][];
         ICUResourceBundle rb = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
         boolean isLatn = nsName.equals("latn");
         String baseKey = "NumberElements/" + nsName + "/symbols/";
         String latnKey = "NumberElements/latn/symbols/";
         String[] symbolKeys = new String[]{"decimal", "group", "list", "percentSign", "minusSign", "plusSign", "exponential", "perMille", "infinity", "nan", "currencyDecimal", "currencyGroup"};
         String[] fallbackElements = new String[]{".", ",", ";", "%", "-", "+", "E", "‰", "∞", "NaN", null, null};
         String[] symbolsArray = new String[symbolKeys.length];

         for(int i = 0; i < symbolKeys.length; ++i) {
            try {
               symbolsArray[i] = rb.getStringWithFallback(baseKey + symbolKeys[i]);
            } catch (MissingResourceException var17) {
               if(!isLatn) {
                  try {
                     symbolsArray[i] = rb.getStringWithFallback(latnKey + symbolKeys[i]);
                  } catch (MissingResourceException var16) {
                     symbolsArray[i] = fallbackElements[i];
                  }
               } else {
                  symbolsArray[i] = fallbackElements[i];
               }
            }
         }

         data[0] = symbolsArray;
         cachedLocaleData.put(locale, data);
      }

      String[] numberElements = data[0];
      ICUResourceBundle r = (ICUResourceBundle)UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", locale);
      ULocale uloc = r.getULocale();
      this.setLocale(uloc, uloc);
      this.decimalSeparator = numberElements[0].charAt(0);
      this.groupingSeparator = numberElements[1].charAt(0);
      this.patternSeparator = numberElements[2].charAt(0);
      this.percent = numberElements[3].charAt(0);
      this.minusSign = numberElements[4].charAt(0);
      this.plusSign = numberElements[5].charAt(0);
      this.exponentSeparator = numberElements[6];
      this.perMill = numberElements[7].charAt(0);
      this.infinity = numberElements[8];
      this.NaN = numberElements[9];
      if(numberElements[10] != null) {
         this.monetarySeparator = numberElements[10].charAt(0);
      } else {
         this.monetarySeparator = this.decimalSeparator;
      }

      if(numberElements[11] != null) {
         this.monetaryGroupingSeparator = numberElements[11].charAt(0);
      } else {
         this.monetaryGroupingSeparator = this.groupingSeparator;
      }

      this.digit = 35;
      this.padEscape = 42;
      this.sigDigit = 64;
      CurrencyData.CurrencyDisplayInfo info = CurrencyData.provider.getInstance(locale, true);
      String currname = null;
      this.currency = Currency.getInstance(locale);
      if(this.currency != null) {
         this.intlCurrencySymbol = this.currency.getCurrencyCode();
         boolean[] isChoiceFormat = new boolean[1];
         currname = this.currency.getName((ULocale)locale, 0, isChoiceFormat);
         this.currencySymbol = isChoiceFormat[0]?(new ChoiceFormat(currname)).format(2.0D):currname;
         CurrencyData.CurrencyFormatInfo fmtInfo = info.getFormatInfo(this.intlCurrencySymbol);
         if(fmtInfo != null) {
            this.currencyPattern = fmtInfo.currencyPattern;
            this.monetarySeparator = fmtInfo.monetarySeparator;
            this.monetaryGroupingSeparator = fmtInfo.monetaryGroupingSeparator;
         }
      } else {
         this.intlCurrencySymbol = "XXX";
         this.currencySymbol = "¤";
      }

      this.currencySpcBeforeSym = new String[3];
      this.currencySpcAfterSym = new String[3];
      this.initSpacingInfo(info.getSpacingInfo());
   }

   private void initSpacingInfo(CurrencyData.CurrencySpacingInfo spcInfo) {
      this.currencySpcBeforeSym[0] = spcInfo.beforeCurrencyMatch;
      this.currencySpcBeforeSym[1] = spcInfo.beforeContextMatch;
      this.currencySpcBeforeSym[2] = spcInfo.beforeInsert;
      this.currencySpcAfterSym[0] = spcInfo.afterCurrencyMatch;
      this.currencySpcAfterSym[1] = spcInfo.afterContextMatch;
      this.currencySpcAfterSym[2] = spcInfo.afterInsert;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      if(this.serialVersionOnStream < 1) {
         this.monetarySeparator = this.decimalSeparator;
         this.exponential = 69;
      }

      if(this.serialVersionOnStream < 2) {
         this.padEscape = 42;
         this.plusSign = 43;
         this.exponentSeparator = String.valueOf(this.exponential);
      }

      if(this.serialVersionOnStream < 3) {
         this.requestedLocale = Locale.getDefault();
      }

      if(this.serialVersionOnStream < 4) {
         this.ulocale = ULocale.forLocale(this.requestedLocale);
      }

      if(this.serialVersionOnStream < 5) {
         this.monetaryGroupingSeparator = this.groupingSeparator;
      }

      if(this.serialVersionOnStream < 6) {
         if(this.currencySpcBeforeSym == null) {
            this.currencySpcBeforeSym = new String[3];
         }

         if(this.currencySpcAfterSym == null) {
            this.currencySpcAfterSym = new String[3];
         }

         this.initSpacingInfo(CurrencyData.CurrencySpacingInfo.DEFAULT);
      }

      this.serialVersionOnStream = 6;
      this.currency = Currency.getInstance(this.intlCurrencySymbol);
   }

   public final ULocale getLocale(ULocale.Type type) {
      return type == ULocale.ACTUAL_LOCALE?this.actualLocale:this.validLocale;
   }

   final void setLocale(ULocale valid, ULocale actual) {
      if(valid == null != (actual == null)) {
         throw new IllegalArgumentException();
      } else {
         this.validLocale = valid;
         this.actualLocale = actual;
      }
   }
}
