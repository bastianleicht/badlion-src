package org.apache.logging.log4j.core.filter;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "BurstFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class BurstFilter extends AbstractFilter {
   private static final long NANOS_IN_SECONDS = 1000000000L;
   private static final int DEFAULT_RATE = 10;
   private static final int DEFAULT_RATE_MULTIPLE = 100;
   private static final int HASH_SHIFT = 32;
   private final Level level;
   private final long burstInterval;
   private final DelayQueue history = new DelayQueue();
   private final Queue available = new ConcurrentLinkedQueue();

   private BurstFilter(Level level, float rate, long maxBurst, Filter.Result onMatch, Filter.Result onMismatch) {
      super(onMatch, onMismatch);
      this.level = level;
      this.burstInterval = (long)(1.0E9F * ((float)maxBurst / rate));

      for(int i = 0; (long)i < maxBurst; ++i) {
         this.available.add(new BurstFilter.LogDelay());
      }

   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
      return this.filter(level);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
      return this.filter(level);
   }

   public Filter.Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
      return this.filter(level);
   }

   public Filter.Result filter(LogEvent event) {
      return this.filter(event.getLevel());
   }

   private Filter.Result filter(Level level) {
      if(!this.level.isAtLeastAsSpecificAs(level)) {
         return this.onMatch;
      } else {
         for(BurstFilter.LogDelay delay = (BurstFilter.LogDelay)this.history.poll(); delay != null; delay = (BurstFilter.LogDelay)this.history.poll()) {
            this.available.add(delay);
         }

         BurstFilter.LogDelay var3 = (BurstFilter.LogDelay)this.available.poll();
         if(var3 != null) {
            var3.setDelay(this.burstInterval);
            this.history.add(var3);
            return this.onMatch;
         } else {
            return this.onMismatch;
         }
      }
   }

   public int getAvailable() {
      return this.available.size();
   }

   public void clear() {
      for(BurstFilter.LogDelay delay : this.history) {
         this.history.remove(delay);
         this.available.add(delay);
      }

   }

   public String toString() {
      return "level=" + this.level.toString() + ", interval=" + this.burstInterval + ", max=" + this.history.size();
   }

   @PluginFactory
   public static BurstFilter createFilter(@PluginAttribute("level") String levelName, @PluginAttribute("rate") String rate, @PluginAttribute("maxBurst") String maxBurst, @PluginAttribute("onMatch") String match, @PluginAttribute("onMismatch") String mismatch) {
      Filter.Result onMatch = Filter.Result.toResult(match, Filter.Result.NEUTRAL);
      Filter.Result onMismatch = Filter.Result.toResult(mismatch, Filter.Result.DENY);
      Level level = Level.toLevel(levelName, Level.WARN);
      float eventRate = rate == null?10.0F:Float.parseFloat(rate);
      if(eventRate <= 0.0F) {
         eventRate = 10.0F;
      }

      long max = maxBurst == null?(long)(eventRate * 100.0F):Long.parseLong(maxBurst);
      return new BurstFilter(level, eventRate, max, onMatch, onMismatch);
   }

   private class LogDelay implements Delayed {
      private long expireTime;

      public void setDelay(long delay) {
         this.expireTime = delay + System.nanoTime();
      }

      public long getDelay(TimeUnit timeUnit) {
         return timeUnit.convert(this.expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
      }

      public int compareTo(Delayed delayed) {
         return this.expireTime < ((BurstFilter.LogDelay)delayed).expireTime?-1:(this.expireTime > ((BurstFilter.LogDelay)delayed).expireTime?1:0);
      }

      public boolean equals(Object o) {
         if(this == o) {
            return true;
         } else if(o != null && this.getClass() == o.getClass()) {
            BurstFilter.LogDelay logDelay = (BurstFilter.LogDelay)o;
            return this.expireTime == logDelay.expireTime;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return (int)(this.expireTime ^ this.expireTime >>> 32);
      }
   }
}
