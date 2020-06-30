package com.ibm.icu.text;

import com.ibm.icu.impl.Utility;
import com.ibm.icu.text.MessagePattern;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralRules;
import com.ibm.icu.text.UFormat;
import com.ibm.icu.util.ULocale;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Map;

public class PluralFormat extends UFormat {
   private static final long serialVersionUID = 1L;
   private ULocale ulocale = null;
   private PluralRules pluralRules = null;
   private String pattern = null;
   private transient MessagePattern msgPattern;
   private Map parsedValues = null;
   private NumberFormat numberFormat = null;
   private transient double offset = 0.0D;
   private transient PluralFormat.PluralSelectorAdapter pluralRulesWrapper = new PluralFormat.PluralSelectorAdapter();
   // $FF: synthetic field
   static final boolean $assertionsDisabled = !PluralFormat.class.desiredAssertionStatus();

   public PluralFormat() {
      this.init((PluralRules)null, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public PluralFormat(ULocale ulocale) {
      this.init((PluralRules)null, PluralRules.PluralType.CARDINAL, ulocale);
   }

   public PluralFormat(PluralRules rules) {
      this.init(rules, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
   }

   public PluralFormat(ULocale ulocale, PluralRules rules) {
      this.init(rules, PluralRules.PluralType.CARDINAL, ulocale);
   }

   public PluralFormat(ULocale ulocale, PluralRules.PluralType type) {
      this.init((PluralRules)null, type, ulocale);
   }

   public PluralFormat(String pattern) {
      this.init((PluralRules)null, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
      this.applyPattern(pattern);
   }

   public PluralFormat(ULocale ulocale, String pattern) {
      this.init((PluralRules)null, PluralRules.PluralType.CARDINAL, ulocale);
      this.applyPattern(pattern);
   }

   public PluralFormat(PluralRules rules, String pattern) {
      this.init(rules, PluralRules.PluralType.CARDINAL, ULocale.getDefault(ULocale.Category.FORMAT));
      this.applyPattern(pattern);
   }

   public PluralFormat(ULocale ulocale, PluralRules rules, String pattern) {
      this.init(rules, PluralRules.PluralType.CARDINAL, ulocale);
      this.applyPattern(pattern);
   }

   public PluralFormat(ULocale ulocale, PluralRules.PluralType type, String pattern) {
      this.init((PluralRules)null, type, ulocale);
      this.applyPattern(pattern);
   }

   private void init(PluralRules rules, PluralRules.PluralType type, ULocale locale) {
      this.ulocale = locale;
      this.pluralRules = rules == null?PluralRules.forLocale(this.ulocale, type):rules;
      this.resetPattern();
      this.numberFormat = NumberFormat.getInstance(this.ulocale);
   }

   private void resetPattern() {
      this.pattern = null;
      if(this.msgPattern != null) {
         this.msgPattern.clear();
      }

      this.offset = 0.0D;
   }

   public void applyPattern(String pattern) {
      this.pattern = pattern;
      if(this.msgPattern == null) {
         this.msgPattern = new MessagePattern();
      }

      try {
         this.msgPattern.parsePluralStyle(pattern);
         this.offset = this.msgPattern.getPluralOffset(0);
      } catch (RuntimeException var3) {
         this.resetPattern();
         throw var3;
      }
   }

   public String toPattern() {
      return this.pattern;
   }

   static int findSubMessage(MessagePattern param0, int param1, PluralFormat.PluralSelector param2, double param3) {
      // $FF: Couldn't be decompiled
   }

   public final String format(double number) {
      if(this.msgPattern != null && this.msgPattern.countParts() != 0) {
         int partIndex = findSubMessage(this.msgPattern, 0, this.pluralRulesWrapper, number);
         number = number - this.offset;
         StringBuilder result = null;
         int prevIndex = this.msgPattern.getPart(partIndex).getLimit();

         while(true) {
            ++partIndex;
            MessagePattern.Part part = this.msgPattern.getPart(partIndex);
            MessagePattern.Part.Type type = part.getType();
            int index = part.getIndex();
            if(type == MessagePattern.Part.Type.MSG_LIMIT) {
               if(result == null) {
                  return this.pattern.substring(prevIndex, index);
               }

               return result.append(this.pattern, prevIndex, index).toString();
            }

            if(type != MessagePattern.Part.Type.REPLACE_NUMBER && (type != MessagePattern.Part.Type.SKIP_SYNTAX || !this.msgPattern.jdkAposMode())) {
               if(type == MessagePattern.Part.Type.ARG_START) {
                  if(result == null) {
                     result = new StringBuilder();
                  }

                  result.append(this.pattern, prevIndex, index);
                  partIndex = this.msgPattern.getLimitPartIndex(partIndex);
                  index = this.msgPattern.getPart(partIndex).getLimit();
                  MessagePattern.appendReducedApostrophes(this.pattern, index, index, result);
                  prevIndex = index;
               }
            } else {
               if(result == null) {
                  result = new StringBuilder();
               }

               result.append(this.pattern, prevIndex, index);
               if(type == MessagePattern.Part.Type.REPLACE_NUMBER) {
                  result.append(this.numberFormat.format(number));
               }

               prevIndex = part.getLimit();
            }
         }
      } else {
         return this.numberFormat.format(number);
      }
   }

   public StringBuffer format(Object number, StringBuffer toAppendTo, FieldPosition pos) {
      if(number instanceof Number) {
         toAppendTo.append(this.format(((Number)number).doubleValue()));
         return toAppendTo;
      } else {
         throw new IllegalArgumentException("\'" + number + "\' is not a Number");
      }
   }

   public Number parse(String text, ParsePosition parsePosition) {
      throw new UnsupportedOperationException();
   }

   public Object parseObject(String source, ParsePosition pos) {
      throw new UnsupportedOperationException();
   }

   /** @deprecated */
   public void setLocale(ULocale ulocale) {
      if(ulocale == null) {
         ulocale = ULocale.getDefault(ULocale.Category.FORMAT);
      }

      this.init((PluralRules)null, PluralRules.PluralType.CARDINAL, ulocale);
   }

   public void setNumberFormat(NumberFormat format) {
      this.numberFormat = format;
   }

   public boolean equals(Object rhs) {
      if(this == rhs) {
         return true;
      } else if(rhs != null && this.getClass() == rhs.getClass()) {
         PluralFormat pf = (PluralFormat)rhs;
         return Utility.objectEquals(this.ulocale, pf.ulocale) && Utility.objectEquals(this.pluralRules, pf.pluralRules) && Utility.objectEquals(this.msgPattern, pf.msgPattern) && Utility.objectEquals(this.numberFormat, pf.numberFormat);
      } else {
         return false;
      }
   }

   public boolean equals(PluralFormat rhs) {
      return this.equals((Object)rhs);
   }

   public int hashCode() {
      return this.pluralRules.hashCode() ^ this.parsedValues.hashCode();
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("locale=" + this.ulocale);
      buf.append(", rules=\'" + this.pluralRules + "\'");
      buf.append(", pattern=\'" + this.pattern + "\'");
      buf.append(", format=\'" + this.numberFormat + "\'");
      return buf.toString();
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      this.pluralRulesWrapper = new PluralFormat.PluralSelectorAdapter();
      this.parsedValues = null;
      if(this.pattern != null) {
         this.applyPattern(this.pattern);
      }

   }

   interface PluralSelector {
      String select(double var1);
   }

   private final class PluralSelectorAdapter implements PluralFormat.PluralSelector {
      private PluralSelectorAdapter() {
      }

      public String select(double number) {
         return PluralFormat.this.pluralRules.select(number);
      }
   }
}
