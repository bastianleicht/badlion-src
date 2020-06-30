package com.ibm.icu.impl;

import com.ibm.icu.impl.CacheBase;
import com.ibm.icu.impl.Normalizer2Impl;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.Normalizer2;
import java.io.IOException;
import java.io.InputStream;

public final class Norm2AllModes {
   public final Normalizer2Impl impl;
   public final Norm2AllModes.ComposeNormalizer2 comp;
   public final Norm2AllModes.DecomposeNormalizer2 decomp;
   public final Norm2AllModes.FCDNormalizer2 fcd;
   public final Norm2AllModes.ComposeNormalizer2 fcc;
   private static CacheBase cache = new SoftCache() {
      protected Norm2AllModes createInstance(String key, InputStream data) {
         Normalizer2Impl impl;
         if(data == null) {
            impl = (new Normalizer2Impl()).load("data/icudt51b/" + key + ".nrm");
         } else {
            impl = (new Normalizer2Impl()).load(data);
         }

         return new Norm2AllModes(impl);
      }
   };
   public static final Norm2AllModes.NoopNormalizer2 NOOP_NORMALIZER2 = new Norm2AllModes.NoopNormalizer2();

   private Norm2AllModes(Normalizer2Impl ni) {
      this.impl = ni;
      this.comp = new Norm2AllModes.ComposeNormalizer2(ni, false);
      this.decomp = new Norm2AllModes.DecomposeNormalizer2(ni);
      this.fcd = new Norm2AllModes.FCDNormalizer2(ni);
      this.fcc = new Norm2AllModes.ComposeNormalizer2(ni, true);
   }

   private static Norm2AllModes getInstanceFromSingleton(Norm2AllModes.Norm2AllModesSingleton singleton) {
      if(singleton.exception != null) {
         throw singleton.exception;
      } else {
         return singleton.allModes;
      }
   }

   public static Norm2AllModes getNFCInstance() {
      return getInstanceFromSingleton(Norm2AllModes.NFCSingleton.INSTANCE);
   }

   public static Norm2AllModes getNFKCInstance() {
      return getInstanceFromSingleton(Norm2AllModes.NFKCSingleton.INSTANCE);
   }

   public static Norm2AllModes getNFKC_CFInstance() {
      return getInstanceFromSingleton(Norm2AllModes.NFKC_CFSingleton.INSTANCE);
   }

   public static Norm2AllModes.Normalizer2WithImpl getN2WithImpl(int index) {
      switch(index) {
      case 0:
         return getNFCInstance().decomp;
      case 1:
         return getNFKCInstance().decomp;
      case 2:
         return getNFCInstance().comp;
      case 3:
         return getNFKCInstance().comp;
      default:
         return null;
      }
   }

   public static Norm2AllModes getInstance(InputStream data, String name) {
      if(data == null) {
         Norm2AllModes.Norm2AllModesSingleton singleton;
         if(name.equals("nfc")) {
            singleton = Norm2AllModes.NFCSingleton.INSTANCE;
         } else if(name.equals("nfkc")) {
            singleton = Norm2AllModes.NFKCSingleton.INSTANCE;
         } else if(name.equals("nfkc_cf")) {
            singleton = Norm2AllModes.NFKC_CFSingleton.INSTANCE;
         } else {
            singleton = null;
         }

         if(singleton != null) {
            if(singleton.exception != null) {
               throw singleton.exception;
            }

            return singleton.allModes;
         }
      }

      return (Norm2AllModes)cache.getInstance(name, data);
   }

   public static Normalizer2 getFCDNormalizer2() {
      return getNFCInstance().fcd;
   }

   public static final class ComposeNormalizer2 extends Norm2AllModes.Normalizer2WithImpl {
      private final boolean onlyContiguous;

      public ComposeNormalizer2(Normalizer2Impl ni, boolean fcc) {
         super(ni);
         this.onlyContiguous = fcc;
      }

      protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.compose(src, 0, src.length(), this.onlyContiguous, true, buffer);
      }

      protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.composeAndAppend(src, doNormalize, this.onlyContiguous, buffer);
      }

      public boolean isNormalized(CharSequence s) {
         return this.impl.compose(s, 0, s.length(), this.onlyContiguous, false, new Normalizer2Impl.ReorderingBuffer(this.impl, new StringBuilder(), 5));
      }

      public Normalizer.QuickCheckResult quickCheck(CharSequence s) {
         int spanLengthAndMaybe = this.impl.composeQuickCheck(s, 0, s.length(), this.onlyContiguous, false);
         return (spanLengthAndMaybe & 1) != 0?Normalizer.MAYBE:(spanLengthAndMaybe >>> 1 == s.length()?Normalizer.YES:Normalizer.NO);
      }

      public int spanQuickCheckYes(CharSequence s) {
         return this.impl.composeQuickCheck(s, 0, s.length(), this.onlyContiguous, true) >>> 1;
      }

      public int getQuickCheck(int c) {
         return this.impl.getCompQuickCheck(this.impl.getNorm16(c));
      }

      public boolean hasBoundaryBefore(int c) {
         return this.impl.hasCompBoundaryBefore(c);
      }

      public boolean hasBoundaryAfter(int c) {
         return this.impl.hasCompBoundaryAfter(c, this.onlyContiguous, false);
      }

      public boolean isInert(int c) {
         return this.impl.hasCompBoundaryAfter(c, this.onlyContiguous, true);
      }
   }

   public static final class DecomposeNormalizer2 extends Norm2AllModes.Normalizer2WithImpl {
      public DecomposeNormalizer2(Normalizer2Impl ni) {
         super(ni);
      }

      protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.decompose(src, 0, src.length(), buffer);
      }

      protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.decomposeAndAppend(src, doNormalize, buffer);
      }

      public int spanQuickCheckYes(CharSequence s) {
         return this.impl.decompose(s, 0, s.length(), (Normalizer2Impl.ReorderingBuffer)null);
      }

      public int getQuickCheck(int c) {
         return this.impl.isDecompYes(this.impl.getNorm16(c))?1:0;
      }

      public boolean hasBoundaryBefore(int c) {
         return this.impl.hasDecompBoundary(c, true);
      }

      public boolean hasBoundaryAfter(int c) {
         return this.impl.hasDecompBoundary(c, false);
      }

      public boolean isInert(int c) {
         return this.impl.isDecompInert(c);
      }
   }

   public static final class FCDNormalizer2 extends Norm2AllModes.Normalizer2WithImpl {
      public FCDNormalizer2(Normalizer2Impl ni) {
         super(ni);
      }

      protected void normalize(CharSequence src, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.makeFCD(src, 0, src.length(), buffer);
      }

      protected void normalizeAndAppend(CharSequence src, boolean doNormalize, Normalizer2Impl.ReorderingBuffer buffer) {
         this.impl.makeFCDAndAppend(src, doNormalize, buffer);
      }

      public int spanQuickCheckYes(CharSequence s) {
         return this.impl.makeFCD(s, 0, s.length(), (Normalizer2Impl.ReorderingBuffer)null);
      }

      public int getQuickCheck(int c) {
         return this.impl.isDecompYes(this.impl.getNorm16(c))?1:0;
      }

      public boolean hasBoundaryBefore(int c) {
         return this.impl.hasFCDBoundaryBefore(c);
      }

      public boolean hasBoundaryAfter(int c) {
         return this.impl.hasFCDBoundaryAfter(c);
      }

      public boolean isInert(int c) {
         return this.impl.isFCDInert(c);
      }
   }

   private static final class NFCSingleton {
      private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfc");
   }

   private static final class NFKCSingleton {
      private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfkc");
   }

   private static final class NFKC_CFSingleton {
      private static final Norm2AllModes.Norm2AllModesSingleton INSTANCE = new Norm2AllModes.Norm2AllModesSingleton("nfkc_cf");
   }

   public static final class NoopNormalizer2 extends Normalizer2 {
      public StringBuilder normalize(CharSequence src, StringBuilder dest) {
         if(dest != src) {
            dest.setLength(0);
            return dest.append(src);
         } else {
            throw new IllegalArgumentException();
         }
      }

      public Appendable normalize(CharSequence src, Appendable dest) {
         if(dest != src) {
            try {
               return dest.append(src);
            } catch (IOException var4) {
               throw new RuntimeException(var4);
            }
         } else {
            throw new IllegalArgumentException();
         }
      }

      public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second) {
         if(first != second) {
            return first.append(second);
         } else {
            throw new IllegalArgumentException();
         }
      }

      public StringBuilder append(StringBuilder first, CharSequence second) {
         if(first != second) {
            return first.append(second);
         } else {
            throw new IllegalArgumentException();
         }
      }

      public String getDecomposition(int c) {
         return null;
      }

      public boolean isNormalized(CharSequence s) {
         return true;
      }

      public Normalizer.QuickCheckResult quickCheck(CharSequence s) {
         return Normalizer.YES;
      }

      public int spanQuickCheckYes(CharSequence s) {
         return s.length();
      }

      public boolean hasBoundaryBefore(int c) {
         return true;
      }

      public boolean hasBoundaryAfter(int c) {
         return true;
      }

      public boolean isInert(int c) {
         return true;
      }
   }

   private static final class Norm2AllModesSingleton {
      private Norm2AllModes allModes;
      private RuntimeException exception;

      private Norm2AllModesSingleton(String name) {
         try {
            Normalizer2Impl impl = (new Normalizer2Impl()).load("data/icudt51b/" + name + ".nrm");
            this.allModes = new Norm2AllModes(impl);
         } catch (RuntimeException var3) {
            this.exception = var3;
         }

      }
   }

   public abstract static class Normalizer2WithImpl extends Normalizer2 {
      public final Normalizer2Impl impl;

      public Normalizer2WithImpl(Normalizer2Impl ni) {
         this.impl = ni;
      }

      public StringBuilder normalize(CharSequence src, StringBuilder dest) {
         if(dest == src) {
            throw new IllegalArgumentException();
         } else {
            dest.setLength(0);
            this.normalize(src, new Normalizer2Impl.ReorderingBuffer(this.impl, dest, src.length()));
            return dest;
         }
      }

      public Appendable normalize(CharSequence src, Appendable dest) {
         if(dest == src) {
            throw new IllegalArgumentException();
         } else {
            Normalizer2Impl.ReorderingBuffer buffer = new Normalizer2Impl.ReorderingBuffer(this.impl, dest, src.length());
            this.normalize(src, buffer);
            buffer.flush();
            return dest;
         }
      }

      protected abstract void normalize(CharSequence var1, Normalizer2Impl.ReorderingBuffer var2);

      public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second) {
         return this.normalizeSecondAndAppend(first, second, true);
      }

      public StringBuilder append(StringBuilder first, CharSequence second) {
         return this.normalizeSecondAndAppend(first, second, false);
      }

      public StringBuilder normalizeSecondAndAppend(StringBuilder first, CharSequence second, boolean doNormalize) {
         if(first == second) {
            throw new IllegalArgumentException();
         } else {
            this.normalizeAndAppend(second, doNormalize, new Normalizer2Impl.ReorderingBuffer(this.impl, first, first.length() + second.length()));
            return first;
         }
      }

      protected abstract void normalizeAndAppend(CharSequence var1, boolean var2, Normalizer2Impl.ReorderingBuffer var3);

      public String getDecomposition(int c) {
         return this.impl.getDecomposition(c);
      }

      public String getRawDecomposition(int c) {
         return this.impl.getRawDecomposition(c);
      }

      public int composePair(int a, int b) {
         return this.impl.composePair(a, b);
      }

      public int getCombiningClass(int c) {
         return this.impl.getCC(this.impl.getNorm16(c));
      }

      public boolean isNormalized(CharSequence s) {
         return s.length() == this.spanQuickCheckYes(s);
      }

      public Normalizer.QuickCheckResult quickCheck(CharSequence s) {
         return this.isNormalized(s)?Normalizer.YES:Normalizer.NO;
      }

      public int getQuickCheck(int c) {
         return 1;
      }
   }
}
