package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.CharEscaper;
import com.google.common.escape.Escaper;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

@Beta
@GwtCompatible
public final class CharEscaperBuilder {
   private final Map map = new HashMap();
   private int max = -1;

   public CharEscaperBuilder addEscape(char c, String r) {
      this.map.put(Character.valueOf(c), Preconditions.checkNotNull(r));
      if(c > this.max) {
         this.max = c;
      }

      return this;
   }

   public CharEscaperBuilder addEscapes(char[] cs, String r) {
      Preconditions.checkNotNull(r);

      for(char c : cs) {
         this.addEscape(c, r);
      }

      return this;
   }

   public char[][] toArray() {
      char[][] result = new char[this.max + 1][];

      for(Entry<Character, String> entry : this.map.entrySet()) {
         result[((Character)entry.getKey()).charValue()] = ((String)entry.getValue()).toCharArray();
      }

      return result;
   }

   public Escaper toEscaper() {
      return new CharEscaperBuilder.CharArrayDecorator(this.toArray());
   }

   private static class CharArrayDecorator extends CharEscaper {
      private final char[][] replacements;
      private final int replaceLength;

      CharArrayDecorator(char[][] replacements) {
         this.replacements = replacements;
         this.replaceLength = replacements.length;
      }

      public String escape(String s) {
         int slen = s.length();

         for(int index = 0; index < slen; ++index) {
            char c = s.charAt(index);
            if(c < this.replacements.length && this.replacements[c] != null) {
               return this.escapeSlow(s, index);
            }
         }

         return s;
      }

      protected char[] escape(char c) {
         return c < this.replaceLength?this.replacements[c]:null;
      }
   }
}
