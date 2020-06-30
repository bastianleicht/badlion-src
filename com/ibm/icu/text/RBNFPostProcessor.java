package com.ibm.icu.text;

import com.ibm.icu.text.NFRuleSet;
import com.ibm.icu.text.RuleBasedNumberFormat;

interface RBNFPostProcessor {
   void init(RuleBasedNumberFormat var1, String var2);

   void process(StringBuffer var1, NFRuleSet var2);
}
