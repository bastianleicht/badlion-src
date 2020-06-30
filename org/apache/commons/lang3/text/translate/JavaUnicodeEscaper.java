package org.apache.commons.lang3.text.translate;

import org.apache.commons.lang3.text.translate.UnicodeEscaper;

public class JavaUnicodeEscaper extends UnicodeEscaper {
   public static JavaUnicodeEscaper above(int codepoint) {
      return outsideOf(0, codepoint);
   }

   public static JavaUnicodeEscaper below(int codepoint) {
      return outsideOf(codepoint, Integer.MAX_VALUE);
   }

   public static JavaUnicodeEscaper between(int codepointLow, int codepointHigh) {
      return new JavaUnicodeEscaper(codepointLow, codepointHigh, true);
   }

   public static JavaUnicodeEscaper outsideOf(int codepointLow, int codepointHigh) {
      return new JavaUnicodeEscaper(codepointLow, codepointHigh, false);
   }

   public JavaUnicodeEscaper(int below, int above, boolean between) {
      super(below, above, between);
   }

   protected String toUtf16Escape(int codepoint) {
      char[] surrogatePair = Character.toChars(codepoint);
      return "\\u" + hex(surrogatePair[0]) + "\\u" + hex(surrogatePair[1]);
   }
}
