package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

public class AggregateTranslator extends CharSequenceTranslator {
   private final CharSequenceTranslator[] translators;

   public AggregateTranslator(CharSequenceTranslator... translators) {
      this.translators = (CharSequenceTranslator[])ArrayUtils.clone((Object[])translators);
   }

   public int translate(CharSequence input, int index, Writer out) throws IOException {
      for(CharSequenceTranslator translator : this.translators) {
         int consumed = translator.translate(input, index, out);
         if(consumed != 0) {
            return consumed;
         }
      }

      return 0;
   }
}
