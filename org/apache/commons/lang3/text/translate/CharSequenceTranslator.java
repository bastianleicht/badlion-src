package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.commons.lang3.text.translate.AggregateTranslator;

public abstract class CharSequenceTranslator {
   public abstract int translate(CharSequence var1, int var2, Writer var3) throws IOException;

   public final String translate(CharSequence input) {
      if(input == null) {
         return null;
      } else {
         try {
            StringWriter writer = new StringWriter(input.length() * 2);
            this.translate(input, writer);
            return writer.toString();
         } catch (IOException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public final void translate(CharSequence input, Writer out) throws IOException {
      if(out == null) {
         throw new IllegalArgumentException("The Writer must not be null");
      } else if(input != null) {
         int pos = 0;
         int len = input.length();

         while(pos < len) {
            int consumed = this.translate(input, pos, out);
            if(consumed == 0) {
               char[] c = Character.toChars(Character.codePointAt(input, pos));
               out.write(c);
               pos += c.length;
            } else {
               for(int pt = 0; pt < consumed; ++pt) {
                  pos += Character.charCount(Character.codePointAt(input, pos));
               }
            }
         }

      }
   }

   public final CharSequenceTranslator with(CharSequenceTranslator... translators) {
      CharSequenceTranslator[] newArray = new CharSequenceTranslator[translators.length + 1];
      newArray[0] = this;
      System.arraycopy(translators, 0, newArray, 1, translators.length);
      return new AggregateTranslator(newArray);
   }

   public static String hex(int codepoint) {
      return Integer.toHexString(codepoint).toUpperCase(Locale.ENGLISH);
   }
}
