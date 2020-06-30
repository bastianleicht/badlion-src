package com.ibm.icu.text;

import com.ibm.icu.text.Replaceable;
import com.ibm.icu.text.UnicodeSet;

public interface UnicodeMatcher {
   int U_MISMATCH = 0;
   int U_PARTIAL_MATCH = 1;
   int U_MATCH = 2;
   char ETHER = '\uffff';

   int matches(Replaceable var1, int[] var2, int var3, boolean var4);

   String toPattern(boolean var1);

   boolean matchesIndexValue(int var1);

   void addMatchSetTo(UnicodeSet var1);
}
