package com.ibm.icu.text;

import com.ibm.icu.text.CurrencyFormat;
import com.ibm.icu.text.UFormat;
import com.ibm.icu.util.ULocale;

public abstract class MeasureFormat extends UFormat {
   static final long serialVersionUID = -7182021401701778240L;

   public static MeasureFormat getCurrencyFormat(ULocale locale) {
      return new CurrencyFormat(locale);
   }

   public static MeasureFormat getCurrencyFormat() {
      return getCurrencyFormat(ULocale.getDefault(ULocale.Category.FORMAT));
   }
}
