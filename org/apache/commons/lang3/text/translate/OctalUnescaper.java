package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;

public class OctalUnescaper extends CharSequenceTranslator {
   public int translate(CharSequence input, int index, Writer out) throws IOException {
      int remaining = input.length() - index - 1;
      StringBuilder builder = new StringBuilder();
      if(input.charAt(index) == 92 && remaining > 0 && this.isOctalDigit(input.charAt(index + 1))) {
         int next = index + 1;
         int next2 = index + 2;
         int next3 = index + 3;
         builder.append(input.charAt(next));
         if(remaining > 1 && this.isOctalDigit(input.charAt(next2))) {
            builder.append(input.charAt(next2));
            if(remaining > 2 && this.isZeroToThree(input.charAt(next)) && this.isOctalDigit(input.charAt(next3))) {
               builder.append(input.charAt(next3));
            }
         }

         out.write(Integer.parseInt(builder.toString(), 8));
         return 1 + builder.length();
      } else {
         return 0;
      }
   }

   private boolean isOctalDigit(char ch) {
      return ch >= 48 && ch <= 55;
   }

   private boolean isZeroToThree(char ch) {
      return ch >= 48 && ch <= 51;
   }
}
