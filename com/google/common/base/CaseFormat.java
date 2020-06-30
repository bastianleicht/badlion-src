package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Converter;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.Nullable;

@GwtCompatible
public enum CaseFormat {
   LOWER_HYPHEN(CharMatcher.is('-'), "-") {
      String normalizeWord(String word) {
         return Ascii.toLowerCase(word);
      }

      String convert(CaseFormat format, String s) {
         return format == LOWER_UNDERSCORE?s.replace('-', '_'):(format == UPPER_UNDERSCORE?Ascii.toUpperCase(s.replace('-', '_')):super.convert(format, s));
      }
   },
   LOWER_UNDERSCORE(CharMatcher.is('_'), "_") {
      String normalizeWord(String word) {
         return Ascii.toLowerCase(word);
      }

      String convert(CaseFormat format, String s) {
         return format == LOWER_HYPHEN?s.replace('_', '-'):(format == UPPER_UNDERSCORE?Ascii.toUpperCase(s):super.convert(format, s));
      }
   },
   LOWER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
      String normalizeWord(String word) {
         return CaseFormat.firstCharOnlyToUpper(word);
      }
   },
   UPPER_CAMEL(CharMatcher.inRange('A', 'Z'), "") {
      String normalizeWord(String word) {
         return CaseFormat.firstCharOnlyToUpper(word);
      }
   },
   UPPER_UNDERSCORE(CharMatcher.is('_'), "_") {
      String normalizeWord(String word) {
         return Ascii.toUpperCase(word);
      }

      String convert(CaseFormat format, String s) {
         return format == LOWER_HYPHEN?Ascii.toLowerCase(s.replace('_', '-')):(format == LOWER_UNDERSCORE?Ascii.toLowerCase(s):super.convert(format, s));
      }
   };

   private final CharMatcher wordBoundary;
   private final String wordSeparator;

   private CaseFormat(CharMatcher wordBoundary, String wordSeparator) {
      this.wordBoundary = wordBoundary;
      this.wordSeparator = wordSeparator;
   }

   public final String to(CaseFormat format, String str) {
      Preconditions.checkNotNull(format);
      Preconditions.checkNotNull(str);
      return format == this?str:this.convert(format, str);
   }

   String convert(CaseFormat format, String s) {
      StringBuilder out = null;
      int i = 0;
      int j = -1;

      while(true) {
         ++j;
         if((j = this.wordBoundary.indexIn(s, j)) == -1) {
            return i == 0?format.normalizeFirstWord(s):out.append(format.normalizeWord(s.substring(i))).toString();
         }

         if(i == 0) {
            out = new StringBuilder(s.length() + 4 * this.wordSeparator.length());
            out.append(format.normalizeFirstWord(s.substring(i, j)));
         } else {
            out.append(format.normalizeWord(s.substring(i, j)));
         }

         out.append(format.wordSeparator);
         i = j + this.wordSeparator.length();
      }
   }

   @Beta
   public Converter converterTo(CaseFormat targetFormat) {
      return new CaseFormat.StringConverter(this, targetFormat);
   }

   abstract String normalizeWord(String var1);

   private String normalizeFirstWord(String word) {
      return this == LOWER_CAMEL?Ascii.toLowerCase(word):this.normalizeWord(word);
   }

   private static String firstCharOnlyToUpper(String word) {
      return word.isEmpty()?word:(new StringBuilder(word.length())).append(Ascii.toUpperCase(word.charAt(0))).append(Ascii.toLowerCase(word.substring(1))).toString();
   }

   private static final class StringConverter extends Converter implements Serializable {
      private final CaseFormat sourceFormat;
      private final CaseFormat targetFormat;
      private static final long serialVersionUID = 0L;

      StringConverter(CaseFormat sourceFormat, CaseFormat targetFormat) {
         this.sourceFormat = (CaseFormat)Preconditions.checkNotNull(sourceFormat);
         this.targetFormat = (CaseFormat)Preconditions.checkNotNull(targetFormat);
      }

      protected String doForward(String s) {
         return s == null?null:this.sourceFormat.to(this.targetFormat, s);
      }

      protected String doBackward(String s) {
         return s == null?null:this.targetFormat.to(this.sourceFormat, s);
      }

      public boolean equals(@Nullable Object object) {
         if(!(object instanceof CaseFormat.StringConverter)) {
            return false;
         } else {
            CaseFormat.StringConverter that = (CaseFormat.StringConverter)object;
            return this.sourceFormat.equals(that.sourceFormat) && this.targetFormat.equals(that.targetFormat);
         }
      }

      public int hashCode() {
         return this.sourceFormat.hashCode() ^ this.targetFormat.hashCode();
      }

      public String toString() {
         return this.sourceFormat + ".converterTo(" + this.targetFormat + ")";
      }
   }
}
