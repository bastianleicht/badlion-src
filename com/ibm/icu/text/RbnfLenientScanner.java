package com.ibm.icu.text;

public interface RbnfLenientScanner {
   boolean allIgnorable(String var1);

   int prefixLength(String var1, String var2);

   int[] findText(String var1, String var2, int var3);
}
