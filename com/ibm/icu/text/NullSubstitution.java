package com.ibm.icu.text;

import com.ibm.icu.text.NFRuleSet;
import com.ibm.icu.text.NFSubstitution;
import com.ibm.icu.text.RuleBasedNumberFormat;
import java.text.ParsePosition;

class NullSubstitution extends NFSubstitution {
   NullSubstitution(int pos, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description) {
      super(pos, ruleSet, formatter, description);
   }

   public boolean equals(Object that) {
      return super.equals(that);
   }

   public int hashCode() {
      assert false : "hashCode not designed";

      return 42;
   }

   public String toString() {
      return "";
   }

   public void doSubstitution(long number, StringBuffer toInsertInto, int position) {
   }

   public void doSubstitution(double number, StringBuffer toInsertInto, int position) {
   }

   public long transformNumber(long number) {
      return 0L;
   }

   public double transformNumber(double number) {
      return 0.0D;
   }

   public Number doParse(String text, ParsePosition parsePosition, double baseValue, double upperBound, boolean lenientParse) {
      return (Number)(baseValue == (double)((long)baseValue)?Long.valueOf((long)baseValue):new Double(baseValue));
   }

   public double composeRuleValue(double newRuleValue, double oldRuleValue) {
      return 0.0D;
   }

   public double calcUpperBound(double oldUpperBound) {
      return 0.0D;
   }

   public boolean isNullSubstitution() {
      return true;
   }

   char tokenChar() {
      return ' ';
   }
}
