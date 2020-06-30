package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.lang3.text.translate.CodePointTranslator;

public class UnicodeUnpairedSurrogateRemover extends CodePointTranslator {
   public boolean translate(int codepoint, Writer out) throws IOException {
      return codepoint >= '\ud800' && codepoint <= '\udfff';
   }
}
