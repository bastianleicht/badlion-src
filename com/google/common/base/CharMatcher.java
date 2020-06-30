package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.SmallCharMatcher;
import java.util.Arrays;
import java.util.BitSet;
import javax.annotation.CheckReturnValue;

@Beta
@GwtCompatible(
   emulated = true
)
public abstract class CharMatcher implements Predicate {
   public static final CharMatcher BREAKING_WHITESPACE = new CharMatcher() {
      public boolean matches(char c) {
         switch(c) {
         case '\t':
         case '\n':
         case '\u000b':
         case '\f':
         case '\r':
         case ' ':
         case '\u0085':
         case ' ':
         case '\u2028':
         case '\u2029':
         case ' ':
         case '　':
            return true;
         case ' ':
            return false;
         default:
            return c >= 8192 && c <= 8202;
         }
      }

      public String toString() {
         return "CharMatcher.BREAKING_WHITESPACE";
      }
   };
   public static final CharMatcher ASCII = inRange('\u0000', '\u007f', "CharMatcher.ASCII");
   private static final String ZEROES = "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０";
   private static final String NINES;
   public static final CharMatcher DIGIT;
   public static final CharMatcher JAVA_DIGIT;
   public static final CharMatcher JAVA_LETTER;
   public static final CharMatcher JAVA_LETTER_OR_DIGIT;
   public static final CharMatcher JAVA_UPPER_CASE;
   public static final CharMatcher JAVA_LOWER_CASE;
   public static final CharMatcher JAVA_ISO_CONTROL;
   public static final CharMatcher INVISIBLE;
   public static final CharMatcher SINGLE_WIDTH;
   public static final CharMatcher ANY;
   public static final CharMatcher NONE;
   final String description;
   private static final int DISTINCT_CHARS = 65536;
   static final String WHITESPACE_TABLE = " 　\r\u0085   　\u2029\u000b　   　 \t     \f 　 　　\u2028\n 　";
   static final int WHITESPACE_MULTIPLIER = 1682554634;
   static final int WHITESPACE_SHIFT;
   public static final CharMatcher WHITESPACE;

   private static String showCharacter(char c) {
      String hex = "0123456789ABCDEF";
      char[] tmp = new char[]{'\\', 'u', '\u0000', '\u0000', '\u0000', '\u0000'};

      for(int i = 0; i < 4; ++i) {
         tmp[5 - i] = hex.charAt(c & 15);
         c = (char)(c >> 4);
      }

      return String.copyValueOf(tmp);
   }

   public static CharMatcher is(final char match) {
      final String description = "CharMatcher.is(\'" + showCharacter(match) + "\')";
      return new CharMatcher.FastMatcher(description) {
         public boolean matches(char c) {
            return c == match;
         }

         public String replaceFrom(CharSequence sequence, char replacement) {
            return sequence.toString().replace(match, replacement);
         }

         public CharMatcher and(CharMatcher other) {
            return (CharMatcher)(other.matches(match)?this:NONE);
         }

         public CharMatcher or(CharMatcher other) {
            return other.matches(match)?other:super.or(other);
         }

         public CharMatcher negate() {
            return isNot(match);
         }

         @GwtIncompatible("java.util.BitSet")
         void setBits(BitSet table) {
            table.set(match);
         }
      };
   }

   public static CharMatcher isNot(final char match) {
      final String description = "CharMatcher.isNot(\'" + showCharacter(match) + "\')";
      return new CharMatcher.FastMatcher(description) {
         public boolean matches(char c) {
            return c != match;
         }

         public CharMatcher and(CharMatcher other) {
            return other.matches(match)?super.and(other):other;
         }

         public CharMatcher or(CharMatcher other) {
            return (CharMatcher)(other.matches(match)?ANY:this);
         }

         @GwtIncompatible("java.util.BitSet")
         void setBits(BitSet table) {
            table.set(0, match);
            table.set(match + 1, 65536);
         }

         public CharMatcher negate() {
            return is(match);
         }
      };
   }

   public static CharMatcher anyOf(CharSequence sequence) {
      switch(sequence.length()) {
      case 0:
         return NONE;
      case 1:
         return is(sequence.charAt(0));
      case 2:
         return isEither(sequence.charAt(0), sequence.charAt(1));
      default:
         final char[] chars = sequence.toString().toCharArray();
         Arrays.sort(chars);
         StringBuilder description = new StringBuilder("CharMatcher.anyOf(\"");

         for(char c : chars) {
            description.append(showCharacter(c));
         }

         description.append("\")");
         return new CharMatcher(description.toString()) {
            public boolean matches(char c) {
               return Arrays.binarySearch(chars, c) >= 0;
            }

            @GwtIncompatible("java.util.BitSet")
            void setBits(BitSet table) {
               for(char c : chars) {
                  table.set(c);
               }

            }
         };
      }
   }

   private static CharMatcher isEither(final char match1, final char match2) {
      final String description = "CharMatcher.anyOf(\"" + showCharacter(match1) + showCharacter(match2) + "\")";
      return new CharMatcher.FastMatcher(description) {
         public boolean matches(char c) {
            return c == match1 || c == match2;
         }

         @GwtIncompatible("java.util.BitSet")
         void setBits(BitSet table) {
            table.set(match1);
            table.set(match2);
         }
      };
   }

   public static CharMatcher noneOf(CharSequence sequence) {
      return anyOf(sequence).negate();
   }

   public static CharMatcher inRange(char startInclusive, char endInclusive) {
      Preconditions.checkArgument(endInclusive >= startInclusive);
      String description = "CharMatcher.inRange(\'" + showCharacter(startInclusive) + "\', \'" + showCharacter(endInclusive) + "\')";
      return inRange(startInclusive, endInclusive, description);
   }

   static CharMatcher inRange(final char startInclusive, final char endInclusive, final String description) {
      return new CharMatcher.FastMatcher(description) {
         public boolean matches(char c) {
            return startInclusive <= c && c <= endInclusive;
         }

         @GwtIncompatible("java.util.BitSet")
         void setBits(BitSet table) {
            table.set(startInclusive, endInclusive + 1);
         }
      };
   }

   public static CharMatcher forPredicate(final Predicate predicate) {
      Preconditions.checkNotNull(predicate);
      if(predicate instanceof CharMatcher) {
         return (CharMatcher)predicate;
      } else {
         final String description = "CharMatcher.forPredicate(" + predicate + ")";
         return new CharMatcher(description) {
            public boolean matches(char c) {
               return predicate.apply(Character.valueOf(c));
            }

            public boolean apply(Character character) {
               return predicate.apply(Preconditions.checkNotNull(character));
            }
         };
      }
   }

   CharMatcher(String description) {
      this.description = description;
   }

   protected CharMatcher() {
      this.description = super.toString();
   }

   public abstract boolean matches(char var1);

   public CharMatcher negate() {
      return new CharMatcher.NegatedMatcher(this);
   }

   public CharMatcher and(CharMatcher other) {
      return new CharMatcher.And(this, (CharMatcher)Preconditions.checkNotNull(other));
   }

   public CharMatcher or(CharMatcher other) {
      return new CharMatcher.Or(this, (CharMatcher)Preconditions.checkNotNull(other));
   }

   public CharMatcher precomputed() {
      return Platform.precomputeCharMatcher(this);
   }

   CharMatcher withToString(String description) {
      throw new UnsupportedOperationException();
   }

   @GwtIncompatible("java.util.BitSet")
   CharMatcher precomputedInternal() {
      BitSet table = new BitSet();
      this.setBits(table);
      int totalCharacters = table.cardinality();
      if(totalCharacters * 2 <= 65536) {
         return precomputedPositive(totalCharacters, table, this.description);
      } else {
         table.flip(0, 65536);
         int negatedCharacters = 65536 - totalCharacters;
         String suffix = ".negate()";
         String negatedDescription = this.description.endsWith(suffix)?this.description.substring(0, this.description.length() - suffix.length()):this.description + suffix;
         return new CharMatcher.NegatedFastMatcher(this.toString(), precomputedPositive(negatedCharacters, table, negatedDescription));
      }
   }

   @GwtIncompatible("java.util.BitSet")
   private static CharMatcher precomputedPositive(int totalCharacters, BitSet table, String description) {
      switch(totalCharacters) {
      case 0:
         return NONE;
      case 1:
         return is((char)table.nextSetBit(0));
      case 2:
         char c1 = (char)table.nextSetBit(0);
         char c2 = (char)table.nextSetBit(c1 + 1);
         return isEither(c1, c2);
      default:
         return (CharMatcher)(isSmall(totalCharacters, table.length())?SmallCharMatcher.from(table, description):new CharMatcher.BitSetMatcher(table, description));
      }
   }

   @GwtIncompatible("SmallCharMatcher")
   private static boolean isSmall(int totalCharacters, int tableLength) {
      return totalCharacters <= 1023 && tableLength > totalCharacters * 4 * 16;
   }

   @GwtIncompatible("java.util.BitSet")
   void setBits(BitSet table) {
      for(int c = '\uffff'; c >= 0; --c) {
         if(this.matches((char)c)) {
            table.set(c);
         }
      }

   }

   public boolean matchesAnyOf(CharSequence sequence) {
      return !this.matchesNoneOf(sequence);
   }

   public boolean matchesAllOf(CharSequence sequence) {
      for(int i = sequence.length() - 1; i >= 0; --i) {
         if(!this.matches(sequence.charAt(i))) {
            return false;
         }
      }

      return true;
   }

   public boolean matchesNoneOf(CharSequence sequence) {
      return this.indexIn(sequence) == -1;
   }

   public int indexIn(CharSequence sequence) {
      int length = sequence.length();

      for(int i = 0; i < length; ++i) {
         if(this.matches(sequence.charAt(i))) {
            return i;
         }
      }

      return -1;
   }

   public int indexIn(CharSequence sequence, int start) {
      int length = sequence.length();
      Preconditions.checkPositionIndex(start, length);

      for(int i = start; i < length; ++i) {
         if(this.matches(sequence.charAt(i))) {
            return i;
         }
      }

      return -1;
   }

   public int lastIndexIn(CharSequence sequence) {
      for(int i = sequence.length() - 1; i >= 0; --i) {
         if(this.matches(sequence.charAt(i))) {
            return i;
         }
      }

      return -1;
   }

   public int countIn(CharSequence sequence) {
      int count = 0;

      for(int i = 0; i < sequence.length(); ++i) {
         if(this.matches(sequence.charAt(i))) {
            ++count;
         }
      }

      return count;
   }

   @CheckReturnValue
   public String removeFrom(CharSequence sequence) {
      String string = sequence.toString();
      int pos = this.indexIn(string);
      if(pos == -1) {
         return string;
      } else {
         char[] chars = string.toCharArray();
         int spread = 1;

         label29:
         while(true) {
            ++pos;

            while(pos != chars.length) {
               if(this.matches(chars[pos])) {
                  ++spread;
                  continue label29;
               }

               chars[pos - spread] = chars[pos];
               ++pos;
            }

            return new String(chars, 0, pos - spread);
         }
      }
   }

   @CheckReturnValue
   public String retainFrom(CharSequence sequence) {
      return this.negate().removeFrom(sequence);
   }

   @CheckReturnValue
   public String replaceFrom(CharSequence sequence, char replacement) {
      String string = sequence.toString();
      int pos = this.indexIn(string);
      if(pos == -1) {
         return string;
      } else {
         char[] chars = string.toCharArray();
         chars[pos] = replacement;

         for(int i = pos + 1; i < chars.length; ++i) {
            if(this.matches(chars[i])) {
               chars[i] = replacement;
            }
         }

         return new String(chars);
      }
   }

   @CheckReturnValue
   public String replaceFrom(CharSequence sequence, CharSequence replacement) {
      int replacementLen = replacement.length();
      if(replacementLen == 0) {
         return this.removeFrom(sequence);
      } else if(replacementLen == 1) {
         return this.replaceFrom(sequence, replacement.charAt(0));
      } else {
         String string = sequence.toString();
         int pos = this.indexIn(string);
         if(pos == -1) {
            return string;
         } else {
            int len = string.length();
            StringBuilder buf = new StringBuilder(len * 3 / 2 + 16);
            int oldpos = 0;

            while(true) {
               buf.append(string, oldpos, pos);
               buf.append(replacement);
               oldpos = pos + 1;
               pos = this.indexIn(string, oldpos);
               if(pos == -1) {
                  break;
               }
            }

            buf.append(string, oldpos, len);
            return buf.toString();
         }
      }
   }

   @CheckReturnValue
   public String trimFrom(CharSequence sequence) {
      int len = sequence.length();

      int first;
      for(first = 0; first < len && this.matches(sequence.charAt(first)); ++first) {
         ;
      }

      int last;
      for(last = len - 1; last > first && this.matches(sequence.charAt(last)); --last) {
         ;
      }

      return sequence.subSequence(first, last + 1).toString();
   }

   @CheckReturnValue
   public String trimLeadingFrom(CharSequence sequence) {
      int len = sequence.length();

      for(int first = 0; first < len; ++first) {
         if(!this.matches(sequence.charAt(first))) {
            return sequence.subSequence(first, len).toString();
         }
      }

      return "";
   }

   @CheckReturnValue
   public String trimTrailingFrom(CharSequence sequence) {
      int len = sequence.length();

      for(int last = len - 1; last >= 0; --last) {
         if(!this.matches(sequence.charAt(last))) {
            return sequence.subSequence(0, last + 1).toString();
         }
      }

      return "";
   }

   @CheckReturnValue
   public String collapseFrom(CharSequence sequence, char replacement) {
      int len = sequence.length();

      for(int i = 0; i < len; ++i) {
         char c = sequence.charAt(i);
         if(this.matches(c)) {
            if(c != replacement || i != len - 1 && this.matches(sequence.charAt(i + 1))) {
               StringBuilder builder = (new StringBuilder(len)).append(sequence.subSequence(0, i)).append(replacement);
               return this.finishCollapseFrom(sequence, i + 1, len, replacement, builder, true);
            }

            ++i;
         }
      }

      return sequence.toString();
   }

   @CheckReturnValue
   public String trimAndCollapseFrom(CharSequence sequence, char replacement) {
      int len = sequence.length();

      int first;
      for(first = 0; first < len && this.matches(sequence.charAt(first)); ++first) {
         ;
      }

      int last;
      for(last = len - 1; last > first && this.matches(sequence.charAt(last)); --last) {
         ;
      }

      return first == 0 && last == len - 1?this.collapseFrom(sequence, replacement):this.finishCollapseFrom(sequence, first, last + 1, replacement, new StringBuilder(last + 1 - first), false);
   }

   private String finishCollapseFrom(CharSequence sequence, int start, int end, char replacement, StringBuilder builder, boolean inMatchingGroup) {
      for(int i = start; i < end; ++i) {
         char c = sequence.charAt(i);
         if(this.matches(c)) {
            if(!inMatchingGroup) {
               builder.append(replacement);
               inMatchingGroup = true;
            }
         } else {
            builder.append(c);
            inMatchingGroup = false;
         }
      }

      return builder.toString();
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(Character character) {
      return this.matches(character.charValue());
   }

   public String toString() {
      return this.description;
   }

   static {
      StringBuilder builder = new StringBuilder("0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".length());

      for(int i = 0; i < "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".length(); ++i) {
         builder.append((char)("0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".charAt(i) + 9));
      }

      NINES = builder.toString();
      DIGIT = new CharMatcher.RangesMatcher("CharMatcher.DIGIT", "0٠۰߀०০੦૦୦௦౦೦൦๐໐༠၀႐០᠐᥆᧐᭐᮰᱀᱐꘠꣐꤀꩐０".toCharArray(), NINES.toCharArray());
      JAVA_DIGIT = new CharMatcher("CharMatcher.JAVA_DIGIT") {
         public boolean matches(char c) {
            return Character.isDigit(c);
         }
      };
      JAVA_LETTER = new CharMatcher("CharMatcher.JAVA_LETTER") {
         public boolean matches(char c) {
            return Character.isLetter(c);
         }
      };
      JAVA_LETTER_OR_DIGIT = new CharMatcher("CharMatcher.JAVA_LETTER_OR_DIGIT") {
         public boolean matches(char c) {
            return Character.isLetterOrDigit(c);
         }
      };
      JAVA_UPPER_CASE = new CharMatcher("CharMatcher.JAVA_UPPER_CASE") {
         public boolean matches(char c) {
            return Character.isUpperCase(c);
         }
      };
      JAVA_LOWER_CASE = new CharMatcher("CharMatcher.JAVA_LOWER_CASE") {
         public boolean matches(char c) {
            return Character.isLowerCase(c);
         }
      };
      JAVA_ISO_CONTROL = inRange('\u0000', '\u001f').or(inRange('\u007f', '\u009f')).withToString("CharMatcher.JAVA_ISO_CONTROL");
      INVISIBLE = new CharMatcher.RangesMatcher("CharMatcher.INVISIBLE", "\u0000\u007f\u00ad\u0600\u061c\u06dd\u070f ᠎ \u2028 \u2066\u2067\u2068\u2069\u206a　\ud800\ufeff\ufff9\ufffa".toCharArray(), "  \u00ad\u0604\u061c\u06dd\u070f ᠎\u200f \u2064\u2066\u2067\u2068\u2069\u206f　\uf8ff\ufeff\ufff9\ufffb".toCharArray());
      SINGLE_WIDTH = new CharMatcher.RangesMatcher("CharMatcher.SINGLE_WIDTH", "\u0000־א׳\u0600ݐ\u0e00Ḁ℀ﭐﹰ｡".toCharArray(), "ӹ־ת״ۿݿ\u0e7f₯℺\ufdff\ufeffￜ".toCharArray());
      ANY = new CharMatcher.FastMatcher("CharMatcher.ANY") {
         public boolean matches(char c) {
            return true;
         }

         public int indexIn(CharSequence sequence) {
            return sequence.length() == 0?-1:0;
         }

         public int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            Preconditions.checkPositionIndex(start, length);
            return start == length?-1:start;
         }

         public int lastIndexIn(CharSequence sequence) {
            return sequence.length() - 1;
         }

         public boolean matchesAllOf(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return true;
         }

         public boolean matchesNoneOf(CharSequence sequence) {
            return sequence.length() == 0;
         }

         public String removeFrom(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return "";
         }

         public String replaceFrom(CharSequence sequence, char replacement) {
            char[] array = new char[sequence.length()];
            Arrays.fill(array, replacement);
            return new String(array);
         }

         public String replaceFrom(CharSequence sequence, CharSequence replacement) {
            StringBuilder retval = new StringBuilder(sequence.length() * replacement.length());

            for(int i = 0; i < sequence.length(); ++i) {
               retval.append(replacement);
            }

            return retval.toString();
         }

         public String collapseFrom(CharSequence sequence, char replacement) {
            return sequence.length() == 0?"":String.valueOf(replacement);
         }

         public String trimFrom(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return "";
         }

         public int countIn(CharSequence sequence) {
            return sequence.length();
         }

         public CharMatcher and(CharMatcher other) {
            return (CharMatcher)Preconditions.checkNotNull(other);
         }

         public CharMatcher or(CharMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
         }

         public CharMatcher negate() {
            return NONE;
         }
      };
      NONE = new CharMatcher.FastMatcher("CharMatcher.NONE") {
         public boolean matches(char c) {
            return false;
         }

         public int indexIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return -1;
         }

         public int indexIn(CharSequence sequence, int start) {
            int length = sequence.length();
            Preconditions.checkPositionIndex(start, length);
            return -1;
         }

         public int lastIndexIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return -1;
         }

         public boolean matchesAllOf(CharSequence sequence) {
            return sequence.length() == 0;
         }

         public boolean matchesNoneOf(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return true;
         }

         public String removeFrom(CharSequence sequence) {
            return sequence.toString();
         }

         public String replaceFrom(CharSequence sequence, char replacement) {
            return sequence.toString();
         }

         public String replaceFrom(CharSequence sequence, CharSequence replacement) {
            Preconditions.checkNotNull(replacement);
            return sequence.toString();
         }

         public String collapseFrom(CharSequence sequence, char replacement) {
            return sequence.toString();
         }

         public String trimFrom(CharSequence sequence) {
            return sequence.toString();
         }

         public String trimLeadingFrom(CharSequence sequence) {
            return sequence.toString();
         }

         public String trimTrailingFrom(CharSequence sequence) {
            return sequence.toString();
         }

         public int countIn(CharSequence sequence) {
            Preconditions.checkNotNull(sequence);
            return 0;
         }

         public CharMatcher and(CharMatcher other) {
            Preconditions.checkNotNull(other);
            return this;
         }

         public CharMatcher or(CharMatcher other) {
            return (CharMatcher)Preconditions.checkNotNull(other);
         }

         public CharMatcher negate() {
            return ANY;
         }
      };
      WHITESPACE_SHIFT = Integer.numberOfLeadingZeros(" 　\r\u0085   　\u2029\u000b　   　 \t     \f 　 　　\u2028\n 　".length() - 1);
      WHITESPACE = new CharMatcher.FastMatcher("WHITESPACE") {
         public boolean matches(char c) {
            return " 　\r\u0085   　\u2029\u000b　   　 \t     \f 　 　　\u2028\n 　".charAt(1682554634 * c >>> WHITESPACE_SHIFT) == c;
         }

         @GwtIncompatible("java.util.BitSet")
         void setBits(BitSet table) {
            for(int i = 0; i < " 　\r\u0085   　\u2029\u000b　   　 \t     \f 　 　　\u2028\n 　".length(); ++i) {
               table.set(" 　\r\u0085   　\u2029\u000b　   　 \t     \f 　 　　\u2028\n 　".charAt(i));
            }

         }
      };
   }

   private static class And extends CharMatcher {
      final CharMatcher first;
      final CharMatcher second;

      And(CharMatcher a, CharMatcher b) {
         this(a, b, "CharMatcher.and(" + a + ", " + b + ")");
      }

      And(CharMatcher a, CharMatcher b, String description) {
         super(description);
         this.first = (CharMatcher)Preconditions.checkNotNull(a);
         this.second = (CharMatcher)Preconditions.checkNotNull(b);
      }

      public boolean matches(char c) {
         return this.first.matches(c) && this.second.matches(c);
      }

      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet table) {
         BitSet tmp1 = new BitSet();
         this.first.setBits(tmp1);
         BitSet tmp2 = new BitSet();
         this.second.setBits(tmp2);
         tmp1.and(tmp2);
         table.or(tmp1);
      }

      CharMatcher withToString(String description) {
         return new CharMatcher.And(this.first, this.second, description);
      }
   }

   @GwtIncompatible("java.util.BitSet")
   private static class BitSetMatcher extends CharMatcher.FastMatcher {
      private final BitSet table;

      private BitSetMatcher(BitSet table, String description) {
         super(description);
         if(table.length() + 64 < table.size()) {
            table = (BitSet)table.clone();
         }

         this.table = table;
      }

      public boolean matches(char c) {
         return this.table.get(c);
      }

      void setBits(BitSet bitSet) {
         bitSet.or(this.table);
      }
   }

   abstract static class FastMatcher extends CharMatcher {
      FastMatcher() {
      }

      FastMatcher(String description) {
         super(description);
      }

      public final CharMatcher precomputed() {
         return this;
      }

      public CharMatcher negate() {
         return new CharMatcher.NegatedFastMatcher(this);
      }
   }

   static final class NegatedFastMatcher extends CharMatcher.NegatedMatcher {
      NegatedFastMatcher(CharMatcher original) {
         super(original);
      }

      NegatedFastMatcher(String toString, CharMatcher original) {
         super(toString, original);
      }

      public final CharMatcher precomputed() {
         return this;
      }

      CharMatcher withToString(String description) {
         return new CharMatcher.NegatedFastMatcher(description, this.original);
      }
   }

   private static class NegatedMatcher extends CharMatcher {
      final CharMatcher original;

      NegatedMatcher(String toString, CharMatcher original) {
         super(toString);
         this.original = original;
      }

      NegatedMatcher(CharMatcher original) {
         this(original + ".negate()", original);
      }

      public boolean matches(char c) {
         return !this.original.matches(c);
      }

      public boolean matchesAllOf(CharSequence sequence) {
         return this.original.matchesNoneOf(sequence);
      }

      public boolean matchesNoneOf(CharSequence sequence) {
         return this.original.matchesAllOf(sequence);
      }

      public int countIn(CharSequence sequence) {
         return sequence.length() - this.original.countIn(sequence);
      }

      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet table) {
         BitSet tmp = new BitSet();
         this.original.setBits(tmp);
         tmp.flip(0, 65536);
         table.or(tmp);
      }

      public CharMatcher negate() {
         return this.original;
      }

      CharMatcher withToString(String description) {
         return new CharMatcher.NegatedMatcher(description, this.original);
      }
   }

   private static class Or extends CharMatcher {
      final CharMatcher first;
      final CharMatcher second;

      Or(CharMatcher a, CharMatcher b, String description) {
         super(description);
         this.first = (CharMatcher)Preconditions.checkNotNull(a);
         this.second = (CharMatcher)Preconditions.checkNotNull(b);
      }

      Or(CharMatcher a, CharMatcher b) {
         this(a, b, "CharMatcher.or(" + a + ", " + b + ")");
      }

      @GwtIncompatible("java.util.BitSet")
      void setBits(BitSet table) {
         this.first.setBits(table);
         this.second.setBits(table);
      }

      public boolean matches(char c) {
         return this.first.matches(c) || this.second.matches(c);
      }

      CharMatcher withToString(String description) {
         return new CharMatcher.Or(this.first, this.second, description);
      }
   }

   private static class RangesMatcher extends CharMatcher {
      private final char[] rangeStarts;
      private final char[] rangeEnds;

      RangesMatcher(String description, char[] rangeStarts, char[] rangeEnds) {
         super(description);
         this.rangeStarts = rangeStarts;
         this.rangeEnds = rangeEnds;
         Preconditions.checkArgument(rangeStarts.length == rangeEnds.length);

         for(int i = 0; i < rangeStarts.length; ++i) {
            Preconditions.checkArgument(rangeStarts[i] <= rangeEnds[i]);
            if(i + 1 < rangeStarts.length) {
               Preconditions.checkArgument(rangeEnds[i] < rangeStarts[i + 1]);
            }
         }

      }

      public boolean matches(char c) {
         int index = Arrays.binarySearch(this.rangeStarts, c);
         if(index >= 0) {
            return true;
         } else {
            index = ~index - 1;
            return index >= 0 && c <= this.rangeEnds[index];
         }
      }
   }
}
