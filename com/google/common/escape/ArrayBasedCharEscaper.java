package com.google.common.escape;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.escape.ArrayBasedEscaperMap;
import com.google.common.escape.CharEscaper;
import java.util.Map;

@Beta
@GwtCompatible
public abstract class ArrayBasedCharEscaper extends CharEscaper {
   private final char[][] replacements;
   private final int replacementsLength;
   private final char safeMin;
   private final char safeMax;

   protected ArrayBasedCharEscaper(Map replacementMap, char safeMin, char safeMax) {
      this(ArrayBasedEscaperMap.create(replacementMap), safeMin, safeMax);
   }

   protected ArrayBasedCharEscaper(ArrayBasedEscaperMap escaperMap, char safeMin, char safeMax) {
      Preconditions.checkNotNull(escaperMap);
      this.replacements = escaperMap.getReplacementArray();
      this.replacementsLength = this.replacements.length;
      if(safeMax < safeMin) {
         safeMax = 0;
         safeMin = '\uffff';
      }

      this.safeMin = safeMin;
      this.safeMax = safeMax;
   }

   public final String escape(String s) {
      Preconditions.checkNotNull(s);

      for(int i = 0; i < s.length(); ++i) {
         char c = s.charAt(i);
         if(c < this.replacementsLength && this.replacements[c] != null || c > this.safeMax || c < this.safeMin) {
            return this.escapeSlow(s, i);
         }
      }

      return s;
   }

   protected final char[] escape(char c) {
      if(c < this.replacementsLength) {
         char[] chars = this.replacements[c];
         if(chars != null) {
            return chars;
         }
      }

      return c >= this.safeMin && c <= this.safeMax?null:this.escapeUnsafe(c);
   }

   protected abstract char[] escapeUnsafe(char var1);
}
