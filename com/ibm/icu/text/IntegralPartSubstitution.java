package com.ibm.icu.text;

import com.ibm.icu.text.NFRuleSet;
import com.ibm.icu.text.NFSubstitution;
import com.ibm.icu.text.RuleBasedNumberFormat;

class IntegralPartSubstitution extends NFSubstitution {
   IntegralPartSubstitution(int pos, NFRuleSet ruleSet, RuleBasedNumberFormat formatter, String description) {
      super(pos, ruleSet, formatter, description);
   }

   public long transformNumber(long number) {
      return number;
   }

   public double transformNumber(double number) {
      return Math.floor(number);
   }

   public double composeRuleValue(double newRuleValue, double oldRuleValue) {
      return newRuleValue + oldRuleValue;
   }

   public double calcUpperBound(double oldUpperBound) {
      return Double.MAX_VALUE;
   }

   char tokenChar() {
      return '<';
   }
}
