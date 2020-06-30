package com.ibm.icu.text;

import com.ibm.icu.impl.Norm2AllModes;
import com.ibm.icu.text.Normalizer;
import java.io.InputStream;

public abstract class Normalizer2 {
   public static Normalizer2 getNFCInstance() {
      return Norm2AllModes.getNFCInstance().comp;
   }

   public static Normalizer2 getNFDInstance() {
      return Norm2AllModes.getNFCInstance().decomp;
   }

   public static Normalizer2 getNFKCInstance() {
      return Norm2AllModes.getNFKCInstance().comp;
   }

   public static Normalizer2 getNFKDInstance() {
      return Norm2AllModes.getNFKCInstance().decomp;
   }

   public static Normalizer2 getNFKCCasefoldInstance() {
      return Norm2AllModes.getNFKC_CFInstance().comp;
   }

   public static Normalizer2 getInstance(InputStream data, String name, Normalizer2.Mode mode) {
      Norm2AllModes all2Modes = Norm2AllModes.getInstance(data, name);
      switch(mode) {
      case COMPOSE:
         return all2Modes.comp;
      case DECOMPOSE:
         return all2Modes.decomp;
      case FCD:
         return all2Modes.fcd;
      case COMPOSE_CONTIGUOUS:
         return all2Modes.fcc;
      default:
         return null;
      }
   }

   public String normalize(CharSequence src) {
      if(src instanceof String) {
         int spanLength = this.spanQuickCheckYes(src);
         if(spanLength == src.length()) {
            return (String)src;
         } else {
            StringBuilder sb = (new StringBuilder(src.length())).append(src, 0, spanLength);
            return this.normalizeSecondAndAppend(sb, src.subSequence(spanLength, src.length())).toString();
         }
      } else {
         return this.normalize(src, new StringBuilder(src.length())).toString();
      }
   }

   public abstract StringBuilder normalize(CharSequence var1, StringBuilder var2);

   public abstract Appendable normalize(CharSequence var1, Appendable var2);

   public abstract StringBuilder normalizeSecondAndAppend(StringBuilder var1, CharSequence var2);

   public abstract StringBuilder append(StringBuilder var1, CharSequence var2);

   public abstract String getDecomposition(int var1);

   public String getRawDecomposition(int c) {
      return null;
   }

   public int composePair(int a, int b) {
      return -1;
   }

   public int getCombiningClass(int c) {
      return 0;
   }

   public abstract boolean isNormalized(CharSequence var1);

   public abstract Normalizer.QuickCheckResult quickCheck(CharSequence var1);

   public abstract int spanQuickCheckYes(CharSequence var1);

   public abstract boolean hasBoundaryBefore(int var1);

   public abstract boolean hasBoundaryAfter(int var1);

   public abstract boolean isInert(int var1);

   public static enum Mode {
      COMPOSE,
      DECOMPOSE,
      FCD,
      COMPOSE_CONTIGUOUS;
   }
}
