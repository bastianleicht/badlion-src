package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.hash.BloomFilterStrategies;
import com.google.common.hash.Funnel;
import java.io.Serializable;
import javax.annotation.Nullable;

@Beta
public final class BloomFilter implements Predicate, Serializable {
   private final BloomFilterStrategies.BitArray bits;
   private final int numHashFunctions;
   private final Funnel funnel;
   private final BloomFilter.Strategy strategy;
   private static final BloomFilter.Strategy DEFAULT_STRATEGY = getDefaultStrategyFromSystemProperty();
   @VisibleForTesting
   static final String USE_MITZ32_PROPERTY = "com.google.common.hash.BloomFilter.useMitz32";

   private BloomFilter(BloomFilterStrategies.BitArray bits, int numHashFunctions, Funnel funnel, BloomFilter.Strategy strategy) {
      Preconditions.checkArgument(numHashFunctions > 0, "numHashFunctions (%s) must be > 0", new Object[]{Integer.valueOf(numHashFunctions)});
      Preconditions.checkArgument(numHashFunctions <= 255, "numHashFunctions (%s) must be <= 255", new Object[]{Integer.valueOf(numHashFunctions)});
      this.bits = (BloomFilterStrategies.BitArray)Preconditions.checkNotNull(bits);
      this.numHashFunctions = numHashFunctions;
      this.funnel = (Funnel)Preconditions.checkNotNull(funnel);
      this.strategy = (BloomFilter.Strategy)Preconditions.checkNotNull(strategy);
   }

   public BloomFilter copy() {
      return new BloomFilter(this.bits.copy(), this.numHashFunctions, this.funnel, this.strategy);
   }

   public boolean mightContain(Object object) {
      return this.strategy.mightContain(object, this.funnel, this.numHashFunctions, this.bits);
   }

   /** @deprecated */
   @Deprecated
   public boolean apply(Object input) {
      return this.mightContain(input);
   }

   public boolean put(Object object) {
      return this.strategy.put(object, this.funnel, this.numHashFunctions, this.bits);
   }

   public double expectedFpp() {
      return Math.pow((double)this.bits.bitCount() / (double)this.bitSize(), (double)this.numHashFunctions);
   }

   @VisibleForTesting
   long bitSize() {
      return this.bits.bitSize();
   }

   public boolean isCompatible(BloomFilter that) {
      Preconditions.checkNotNull(that);
      return this != that && this.numHashFunctions == that.numHashFunctions && this.bitSize() == that.bitSize() && this.strategy.equals(that.strategy) && this.funnel.equals(that.funnel);
   }

   public void putAll(BloomFilter that) {
      Preconditions.checkNotNull(that);
      Preconditions.checkArgument(this != that, "Cannot combine a BloomFilter with itself.");
      Preconditions.checkArgument(this.numHashFunctions == that.numHashFunctions, "BloomFilters must have the same number of hash functions (%s != %s)", new Object[]{Integer.valueOf(this.numHashFunctions), Integer.valueOf(that.numHashFunctions)});
      Preconditions.checkArgument(this.bitSize() == that.bitSize(), "BloomFilters must have the same size underlying bit arrays (%s != %s)", new Object[]{Long.valueOf(this.bitSize()), Long.valueOf(that.bitSize())});
      Preconditions.checkArgument(this.strategy.equals(that.strategy), "BloomFilters must have equal strategies (%s != %s)", new Object[]{this.strategy, that.strategy});
      Preconditions.checkArgument(this.funnel.equals(that.funnel), "BloomFilters must have equal funnels (%s != %s)", new Object[]{this.funnel, that.funnel});
      this.bits.putAll(that.bits);
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(!(object instanceof BloomFilter)) {
         return false;
      } else {
         BloomFilter<?> that = (BloomFilter)object;
         return this.numHashFunctions == that.numHashFunctions && this.funnel.equals(that.funnel) && this.bits.equals(that.bits) && this.strategy.equals(that.strategy);
      }
   }

   public int hashCode() {
      return Objects.hashCode(new Object[]{Integer.valueOf(this.numHashFunctions), this.funnel, this.strategy, this.bits});
   }

   @VisibleForTesting
   static BloomFilter.Strategy getDefaultStrategyFromSystemProperty() {
      return Boolean.parseBoolean(System.getProperty("com.google.common.hash.BloomFilter.useMitz32"))?BloomFilterStrategies.MURMUR128_MITZ_32:BloomFilterStrategies.MURMUR128_MITZ_64;
   }

   public static BloomFilter create(Funnel funnel, int expectedInsertions, double fpp) {
      return create(funnel, expectedInsertions, fpp, DEFAULT_STRATEGY);
   }

   @VisibleForTesting
   static BloomFilter create(Funnel funnel, int expectedInsertions, double fpp, BloomFilter.Strategy strategy) {
      Preconditions.checkNotNull(funnel);
      Preconditions.checkArgument(expectedInsertions >= 0, "Expected insertions (%s) must be >= 0", new Object[]{Integer.valueOf(expectedInsertions)});
      Preconditions.checkArgument(fpp > 0.0D, "False positive probability (%s) must be > 0.0", new Object[]{Double.valueOf(fpp)});
      Preconditions.checkArgument(fpp < 1.0D, "False positive probability (%s) must be < 1.0", new Object[]{Double.valueOf(fpp)});
      Preconditions.checkNotNull(strategy);
      if(expectedInsertions == 0) {
         expectedInsertions = 1;
      }

      long numBits = optimalNumOfBits((long)expectedInsertions, fpp);
      int numHashFunctions = optimalNumOfHashFunctions((long)expectedInsertions, numBits);

      try {
         return new BloomFilter(new BloomFilterStrategies.BitArray(numBits), numHashFunctions, funnel, strategy);
      } catch (IllegalArgumentException var9) {
         throw new IllegalArgumentException("Could not create BloomFilter of " + numBits + " bits", var9);
      }
   }

   public static BloomFilter create(Funnel funnel, int expectedInsertions) {
      return create(funnel, expectedInsertions, 0.03D);
   }

   @VisibleForTesting
   static int optimalNumOfHashFunctions(long n, long m) {
      return Math.max(1, (int)Math.round((double)(m / n) * Math.log(2.0D)));
   }

   @VisibleForTesting
   static long optimalNumOfBits(long n, double p) {
      if(p == 0.0D) {
         p = Double.MIN_VALUE;
      }

      return (long)((double)(-n) * Math.log(p) / (Math.log(2.0D) * Math.log(2.0D)));
   }

   private Object writeReplace() {
      return new BloomFilter.SerialForm(this);
   }

   private static class SerialForm implements Serializable {
      final long[] data;
      final int numHashFunctions;
      final Funnel funnel;
      final BloomFilter.Strategy strategy;
      private static final long serialVersionUID = 1L;

      SerialForm(BloomFilter bf) {
         this.data = bf.bits.data;
         this.numHashFunctions = bf.numHashFunctions;
         this.funnel = bf.funnel;
         this.strategy = bf.strategy;
      }

      Object readResolve() {
         return new BloomFilter(new BloomFilterStrategies.BitArray(this.data), this.numHashFunctions, this.funnel, this.strategy);
      }
   }

   interface Strategy extends Serializable {
      boolean put(Object var1, Funnel var2, int var3, BloomFilterStrategies.BitArray var4);

      boolean mightContain(Object var1, Funnel var2, int var3, BloomFilterStrategies.BitArray var4);

      int ordinal();
   }
}
