package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.NoSuchElementException;

@GwtCompatible
@Beta
public abstract class DiscreteDomain {
   public static DiscreteDomain integers() {
      return DiscreteDomain.IntegerDomain.INSTANCE;
   }

   public static DiscreteDomain longs() {
      return DiscreteDomain.LongDomain.INSTANCE;
   }

   public static DiscreteDomain bigIntegers() {
      return DiscreteDomain.BigIntegerDomain.INSTANCE;
   }

   public abstract Comparable next(Comparable var1);

   public abstract Comparable previous(Comparable var1);

   public abstract long distance(Comparable var1, Comparable var2);

   public Comparable minValue() {
      throw new NoSuchElementException();
   }

   public Comparable maxValue() {
      throw new NoSuchElementException();
   }

   private static final class BigIntegerDomain extends DiscreteDomain implements Serializable {
      private static final DiscreteDomain.BigIntegerDomain INSTANCE = new DiscreteDomain.BigIntegerDomain();
      private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
      private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
      private static final long serialVersionUID = 0L;

      public BigInteger next(BigInteger value) {
         return value.add(BigInteger.ONE);
      }

      public BigInteger previous(BigInteger value) {
         return value.subtract(BigInteger.ONE);
      }

      public long distance(BigInteger start, BigInteger end) {
         return end.subtract(start).max(MIN_LONG).min(MAX_LONG).longValue();
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.bigIntegers()";
      }
   }

   private static final class IntegerDomain extends DiscreteDomain implements Serializable {
      private static final DiscreteDomain.IntegerDomain INSTANCE = new DiscreteDomain.IntegerDomain();
      private static final long serialVersionUID = 0L;

      public Integer next(Integer value) {
         int i = value.intValue();
         return i == Integer.MAX_VALUE?null:Integer.valueOf(i + 1);
      }

      public Integer previous(Integer value) {
         int i = value.intValue();
         return i == Integer.MIN_VALUE?null:Integer.valueOf(i - 1);
      }

      public long distance(Integer start, Integer end) {
         return (long)end.intValue() - (long)start.intValue();
      }

      public Integer minValue() {
         return Integer.valueOf(Integer.MIN_VALUE);
      }

      public Integer maxValue() {
         return Integer.valueOf(Integer.MAX_VALUE);
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.integers()";
      }
   }

   private static final class LongDomain extends DiscreteDomain implements Serializable {
      private static final DiscreteDomain.LongDomain INSTANCE = new DiscreteDomain.LongDomain();
      private static final long serialVersionUID = 0L;

      public Long next(Long value) {
         long l = value.longValue();
         return l == Long.MAX_VALUE?null:Long.valueOf(l + 1L);
      }

      public Long previous(Long value) {
         long l = value.longValue();
         return l == Long.MIN_VALUE?null:Long.valueOf(l - 1L);
      }

      public long distance(Long start, Long end) {
         long result = end.longValue() - start.longValue();
         return end.longValue() > start.longValue() && result < 0L?Long.MAX_VALUE:(end.longValue() < start.longValue() && result > 0L?Long.MIN_VALUE:result);
      }

      public Long minValue() {
         return Long.valueOf(Long.MIN_VALUE);
      }

      public Long maxValue() {
         return Long.valueOf(Long.MAX_VALUE);
      }

      private Object readResolve() {
         return INSTANCE;
      }

      public String toString() {
         return "DiscreteDomain.longs()";
      }
   }
}
