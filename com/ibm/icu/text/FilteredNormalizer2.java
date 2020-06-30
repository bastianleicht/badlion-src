package com.ibm.icu.text;

import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.UnicodeSet;
import java.io.IOException;

public class FilteredNormalizer2 extends Normalizer2 {
   private Normalizer2 norm2;
   private UnicodeSet set;

   public FilteredNormalizer2(Normalizer2 n2, UnicodeSet filterSet) {
      this.norm2 = n2;
      this.set = filterSet;
   }

   public StringBuilder normalize(CharSequence src, StringBuilder dest) {
      if(dest == src) {
         throw new IllegalArgumentException();
      } else {
         dest.setLength(0);
         this.normalize(src, dest, UnicodeSet.SpanCondition.SIMPLE);
         return dest;
      }
   }

   public Appendable normalize(CharSequence src, Appendable dest) {
      if(dest == src) {
         throw new IllegalArgumentException();
      } else {
         return this.normalize(src, dest, UnicodeSet.SpanCondition.SIMPLE);
      }
   }

   public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second) {
      return this.normalizeSecondAndAppend(first, second, true);
   }

   public StringBuilder append(StringBuilder first, CharSequence second) {
      return this.normalizeSecondAndAppend(first, second, false);
   }

   public String getDecomposition(int c) {
      return this.set.contains(c)?this.norm2.getDecomposition(c):null;
   }

   public String getRawDecomposition(int c) {
      return this.set.contains(c)?this.norm2.getRawDecomposition(c):null;
   }

   public int composePair(int a, int b) {
      return this.set.contains(a) && this.set.contains(b)?this.norm2.composePair(a, b):-1;
   }

   public int getCombiningClass(int c) {
      return this.set.contains(c)?this.norm2.getCombiningClass(c):0;
   }

   public boolean isNormalized(CharSequence s) {
      UnicodeSet.SpanCondition spanCondition = UnicodeSet.SpanCondition.SIMPLE;

      int spanLimit;
      for(int prevSpanLimit = 0; prevSpanLimit < s.length(); prevSpanLimit = spanLimit) {
         spanLimit = this.set.span(s, prevSpanLimit, spanCondition);
         if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            spanCondition = UnicodeSet.SpanCondition.SIMPLE;
         } else {
            if(!this.norm2.isNormalized(s.subSequence(prevSpanLimit, spanLimit))) {
               return false;
            }

            spanCondition = UnicodeSet.SpanCondition.NOT_CONTAINED;
         }
      }

      return true;
   }

   public Normalizer.QuickCheckResult quickCheck(CharSequence s) {
      Normalizer.QuickCheckResult result = Normalizer.YES;
      UnicodeSet.SpanCondition spanCondition = UnicodeSet.SpanCondition.SIMPLE;

      int spanLimit;
      for(int prevSpanLimit = 0; prevSpanLimit < s.length(); prevSpanLimit = spanLimit) {
         spanLimit = this.set.span(s, prevSpanLimit, spanCondition);
         if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            spanCondition = UnicodeSet.SpanCondition.SIMPLE;
         } else {
            Normalizer.QuickCheckResult qcResult = this.norm2.quickCheck(s.subSequence(prevSpanLimit, spanLimit));
            if(qcResult == Normalizer.NO) {
               return qcResult;
            }

            if(qcResult == Normalizer.MAYBE) {
               result = qcResult;
            }

            spanCondition = UnicodeSet.SpanCondition.NOT_CONTAINED;
         }
      }

      return result;
   }

   public int spanQuickCheckYes(CharSequence s) {
      UnicodeSet.SpanCondition spanCondition = UnicodeSet.SpanCondition.SIMPLE;

      int spanLimit;
      for(int prevSpanLimit = 0; prevSpanLimit < s.length(); prevSpanLimit = spanLimit) {
         spanLimit = this.set.span(s, prevSpanLimit, spanCondition);
         if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
            spanCondition = UnicodeSet.SpanCondition.SIMPLE;
         } else {
            int yesLimit = prevSpanLimit + this.norm2.spanQuickCheckYes(s.subSequence(prevSpanLimit, spanLimit));
            if(yesLimit < spanLimit) {
               return yesLimit;
            }

            spanCondition = UnicodeSet.SpanCondition.NOT_CONTAINED;
         }
      }

      return s.length();
   }

   public boolean hasBoundaryBefore(int c) {
      return !this.set.contains(c) || this.norm2.hasBoundaryBefore(c);
   }

   public boolean hasBoundaryAfter(int c) {
      return !this.set.contains(c) || this.norm2.hasBoundaryAfter(c);
   }

   public boolean isInert(int c) {
      return !this.set.contains(c) || this.norm2.isInert(c);
   }

   private Appendable normalize(CharSequence src, Appendable dest, UnicodeSet.SpanCondition spanCondition) {
      StringBuilder tempDest = new StringBuilder();

      try {
         int spanLimit;
         for(int prevSpanLimit = 0; prevSpanLimit < src.length(); prevSpanLimit = spanLimit) {
            spanLimit = this.set.span(src, prevSpanLimit, spanCondition);
            int spanLength = spanLimit - prevSpanLimit;
            if(spanCondition == UnicodeSet.SpanCondition.NOT_CONTAINED) {
               if(spanLength != 0) {
                  dest.append(src, prevSpanLimit, spanLimit);
               }

               spanCondition = UnicodeSet.SpanCondition.SIMPLE;
            } else {
               if(spanLength != 0) {
                  dest.append(this.norm2.normalize(src.subSequence(prevSpanLimit, spanLimit), tempDest));
               }

               spanCondition = UnicodeSet.SpanCondition.NOT_CONTAINED;
            }
         }

         return dest;
      } catch (IOException var8) {
         throw new RuntimeException(var8);
      }
   }

   private StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second, boolean doNormalize) {
      if(first == second) {
         throw new IllegalArgumentException();
      } else if(first.length() == 0) {
         return doNormalize?this.normalize(second, first):first.append(second);
      } else {
         int prefixLimit = this.set.span(second, 0, UnicodeSet.SpanCondition.SIMPLE);
         if(prefixLimit != 0) {
            CharSequence prefix = second.subSequence(0, prefixLimit);
            int suffixStart = this.set.spanBack(first, Integer.MAX_VALUE, UnicodeSet.SpanCondition.SIMPLE);
            if(suffixStart == 0) {
               if(doNormalize) {
                  this.norm2.normalizeSecondAndAppend(first, prefix);
               } else {
                  this.norm2.append(first, prefix);
               }
            } else {
               StringBuilder middle = new StringBuilder(first.subSequence(suffixStart, first.length()));
               if(doNormalize) {
                  this.norm2.normalizeSecondAndAppend(middle, prefix);
               } else {
                  this.norm2.append(middle, prefix);
               }

               first.delete(suffixStart, Integer.MAX_VALUE).append(middle);
            }
         }

         if(prefixLimit < second.length()) {
            CharSequence rest = second.subSequence(prefixLimit, second.length());
            if(doNormalize) {
               this.normalize(rest, first, UnicodeSet.SpanCondition.NOT_CONTAINED);
            } else {
               first.append(rest);
            }
         }

         return first;
      }
   }
}
