package com.ibm.icu.text;

import com.ibm.icu.text.RbnfLenientScanner;
import com.ibm.icu.util.ULocale;

public interface RbnfLenientScannerProvider {
   RbnfLenientScanner get(ULocale var1, String var2);
}
