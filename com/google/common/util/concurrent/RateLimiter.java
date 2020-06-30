package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Ticker;
import com.google.common.util.concurrent.Uninterruptibles;
import java.util.concurrent.TimeUnit;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
@Beta
public abstract class RateLimiter {
   private final RateLimiter.SleepingTicker ticker;
   private final long offsetNanos;
   double storedPermits;
   double maxPermits;
   volatile double stableIntervalMicros;
   private final Object mutex;
   private long nextFreeTicketMicros;

   public static RateLimiter create(double permitsPerSecond) {
      return create(RateLimiter.SleepingTicker.SYSTEM_TICKER, permitsPerSecond);
   }

   @VisibleForTesting
   static RateLimiter create(RateLimiter.SleepingTicker ticker, double permitsPerSecond) {
      RateLimiter rateLimiter = new RateLimiter.Bursty(ticker, 1.0D);
      rateLimiter.setRate(permitsPerSecond);
      return rateLimiter;
   }

   public static RateLimiter create(double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
      return create(RateLimiter.SleepingTicker.SYSTEM_TICKER, permitsPerSecond, warmupPeriod, unit);
   }

   @VisibleForTesting
   static RateLimiter create(RateLimiter.SleepingTicker ticker, double permitsPerSecond, long warmupPeriod, TimeUnit unit) {
      RateLimiter rateLimiter = new RateLimiter.WarmingUp(ticker, warmupPeriod, unit);
      rateLimiter.setRate(permitsPerSecond);
      return rateLimiter;
   }

   @VisibleForTesting
   static RateLimiter createWithCapacity(RateLimiter.SleepingTicker ticker, double permitsPerSecond, long maxBurstBuildup, TimeUnit unit) {
      double maxBurstSeconds = (double)unit.toNanos(maxBurstBuildup) / 1.0E9D;
      RateLimiter.Bursty rateLimiter = new RateLimiter.Bursty(ticker, maxBurstSeconds);
      rateLimiter.setRate(permitsPerSecond);
      return rateLimiter;
   }

   private RateLimiter(RateLimiter.SleepingTicker ticker) {
      this.mutex = new Object();
      this.nextFreeTicketMicros = 0L;
      this.ticker = ticker;
      this.offsetNanos = ticker.read();
   }

   public final void setRate(double permitsPerSecond) {
      Preconditions.checkArgument(permitsPerSecond > 0.0D && !Double.isNaN(permitsPerSecond), "rate must be positive");
      synchronized(this.mutex) {
         this.resync(this.readSafeMicros());
         double stableIntervalMicros = (double)TimeUnit.SECONDS.toMicros(1L) / permitsPerSecond;
         this.stableIntervalMicros = stableIntervalMicros;
         this.doSetRate(permitsPerSecond, stableIntervalMicros);
      }
   }

   abstract void doSetRate(double var1, double var3);

   public final double getRate() {
      return (double)TimeUnit.SECONDS.toMicros(1L) / this.stableIntervalMicros;
   }

   public double acquire() {
      return this.acquire(1);
   }

   public double acquire(int permits) {
      long microsToWait = this.reserve(permits);
      this.ticker.sleepMicrosUninterruptibly(microsToWait);
      return 1.0D * (double)microsToWait / (double)TimeUnit.SECONDS.toMicros(1L);
   }

   long reserve() {
      return this.reserve(1);
   }

   long reserve(int permits) {
      checkPermits(permits);
      synchronized(this.mutex) {
         return this.reserveNextTicket((double)permits, this.readSafeMicros());
      }
   }

   public boolean tryAcquire(long timeout, TimeUnit unit) {
      return this.tryAcquire(1, timeout, unit);
   }

   public boolean tryAcquire(int permits) {
      return this.tryAcquire(permits, 0L, TimeUnit.MICROSECONDS);
   }

   public boolean tryAcquire() {
      return this.tryAcquire(1, 0L, TimeUnit.MICROSECONDS);
   }

   public boolean tryAcquire(int permits, long timeout, TimeUnit unit) {
      long timeoutMicros = unit.toMicros(timeout);
      checkPermits(permits);
      long microsToWait;
      synchronized(this.mutex) {
         long nowMicros = this.readSafeMicros();
         if(this.nextFreeTicketMicros > nowMicros + timeoutMicros) {
            return false;
         }

         microsToWait = this.reserveNextTicket((double)permits, nowMicros);
      }

      this.ticker.sleepMicrosUninterruptibly(microsToWait);
      return true;
   }

   private static void checkPermits(int permits) {
      Preconditions.checkArgument(permits > 0, "Requested permits must be positive");
   }

   private long reserveNextTicket(double requiredPermits, long nowMicros) {
      this.resync(nowMicros);
      long microsToNextFreeTicket = Math.max(0L, this.nextFreeTicketMicros - nowMicros);
      double storedPermitsToSpend = Math.min(requiredPermits, this.storedPermits);
      double freshPermits = requiredPermits - storedPermitsToSpend;
      long waitMicros = this.storedPermitsToWaitTime(this.storedPermits, storedPermitsToSpend) + (long)(freshPermits * this.stableIntervalMicros);
      this.nextFreeTicketMicros += waitMicros;
      this.storedPermits -= storedPermitsToSpend;
      return microsToNextFreeTicket;
   }

   abstract long storedPermitsToWaitTime(double var1, double var3);

   private void resync(long nowMicros) {
      if(nowMicros > this.nextFreeTicketMicros) {
         this.storedPermits = Math.min(this.maxPermits, this.storedPermits + (double)(nowMicros - this.nextFreeTicketMicros) / this.stableIntervalMicros);
         this.nextFreeTicketMicros = nowMicros;
      }

   }

   private long readSafeMicros() {
      return TimeUnit.NANOSECONDS.toMicros(this.ticker.read() - this.offsetNanos);
   }

   public String toString() {
      return String.format("RateLimiter[stableRate=%3.1fqps]", new Object[]{Double.valueOf(1000000.0D / this.stableIntervalMicros)});
   }

   private static class Bursty extends RateLimiter {
      final double maxBurstSeconds;

      Bursty(RateLimiter.SleepingTicker ticker, double maxBurstSeconds) {
         super(ticker, null);
         this.maxBurstSeconds = maxBurstSeconds;
      }

      void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
         double oldMaxPermits = this.maxPermits;
         this.maxPermits = this.maxBurstSeconds * permitsPerSecond;
         this.storedPermits = oldMaxPermits == 0.0D?0.0D:this.storedPermits * this.maxPermits / oldMaxPermits;
      }

      long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
         return 0L;
      }
   }

   @VisibleForTesting
   abstract static class SleepingTicker extends Ticker {
      static final RateLimiter.SleepingTicker SYSTEM_TICKER = new RateLimiter.SleepingTicker() {
         public long read() {
            return systemTicker().read();
         }

         public void sleepMicrosUninterruptibly(long micros) {
            if(micros > 0L) {
               Uninterruptibles.sleepUninterruptibly(micros, TimeUnit.MICROSECONDS);
            }

         }
      };

      abstract void sleepMicrosUninterruptibly(long var1);
   }

   private static class WarmingUp extends RateLimiter {
      final long warmupPeriodMicros;
      private double slope;
      private double halfPermits;

      WarmingUp(RateLimiter.SleepingTicker ticker, long warmupPeriod, TimeUnit timeUnit) {
         super(ticker, null);
         this.warmupPeriodMicros = timeUnit.toMicros(warmupPeriod);
      }

      void doSetRate(double permitsPerSecond, double stableIntervalMicros) {
         double oldMaxPermits = this.maxPermits;
         this.maxPermits = (double)this.warmupPeriodMicros / stableIntervalMicros;
         this.halfPermits = this.maxPermits / 2.0D;
         double coldIntervalMicros = stableIntervalMicros * 3.0D;
         this.slope = (coldIntervalMicros - stableIntervalMicros) / this.halfPermits;
         if(oldMaxPermits == Double.POSITIVE_INFINITY) {
            this.storedPermits = 0.0D;
         } else {
            this.storedPermits = oldMaxPermits == 0.0D?this.maxPermits:this.storedPermits * this.maxPermits / oldMaxPermits;
         }

      }

      long storedPermitsToWaitTime(double storedPermits, double permitsToTake) {
         double availablePermitsAboveHalf = storedPermits - this.halfPermits;
         long micros = 0L;
         if(availablePermitsAboveHalf > 0.0D) {
            double permitsAboveHalfToTake = Math.min(availablePermitsAboveHalf, permitsToTake);
            micros = (long)(permitsAboveHalfToTake * (this.permitsToTime(availablePermitsAboveHalf) + this.permitsToTime(availablePermitsAboveHalf - permitsAboveHalfToTake)) / 2.0D);
            permitsToTake -= permitsAboveHalfToTake;
         }

         micros = (long)((double)micros + this.stableIntervalMicros * permitsToTake);
         return micros;
      }

      private double permitsToTime(double permits) {
         return this.stableIntervalMicros + permits * this.slope;
      }
   }
}
