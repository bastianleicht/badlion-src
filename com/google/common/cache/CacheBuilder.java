package com.google.common.cache;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Ticker;
import com.google.common.cache.AbstractCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheStats;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.LocalCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.cache.Weigher;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.CheckReturnValue;

@GwtCompatible(
   emulated = true
)
public final class CacheBuilder {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
   private static final int DEFAULT_EXPIRATION_NANOS = 0;
   private static final int DEFAULT_REFRESH_NANOS = 0;
   static final Supplier NULL_STATS_COUNTER = Suppliers.ofInstance(new AbstractCache.StatsCounter() {
      public void recordHits(int count) {
      }

      public void recordMisses(int count) {
      }

      public void recordLoadSuccess(long loadTime) {
      }

      public void recordLoadException(long loadTime) {
      }

      public void recordEviction() {
      }

      public CacheStats snapshot() {
         return CacheBuilder.EMPTY_STATS;
      }
   });
   static final CacheStats EMPTY_STATS = new CacheStats(0L, 0L, 0L, 0L, 0L, 0L);
   static final Supplier CACHE_STATS_COUNTER = new Supplier() {
      public AbstractCache.StatsCounter get() {
         return new AbstractCache.SimpleStatsCounter();
      }
   };
   static final Ticker NULL_TICKER = new Ticker() {
      public long read() {
         return 0L;
      }
   };
   private static final Logger logger = Logger.getLogger(CacheBuilder.class.getName());
   static final int UNSET_INT = -1;
   boolean strictParsing = true;
   int initialCapacity = -1;
   int concurrencyLevel = -1;
   long maximumSize = -1L;
   long maximumWeight = -1L;
   Weigher weigher;
   LocalCache.Strength keyStrength;
   LocalCache.Strength valueStrength;
   long expireAfterWriteNanos = -1L;
   long expireAfterAccessNanos = -1L;
   long refreshNanos = -1L;
   Equivalence keyEquivalence;
   Equivalence valueEquivalence;
   RemovalListener removalListener;
   Ticker ticker;
   Supplier statsCounterSupplier;

   CacheBuilder() {
      this.statsCounterSupplier = NULL_STATS_COUNTER;
   }

   public static CacheBuilder newBuilder() {
      return new CacheBuilder();
   }

   @Beta
   @GwtIncompatible("To be supported")
   public static CacheBuilder from(CacheBuilderSpec spec) {
      return spec.toCacheBuilder().lenientParsing();
   }

   @Beta
   @GwtIncompatible("To be supported")
   public static CacheBuilder from(String spec) {
      return from(CacheBuilderSpec.parse(spec));
   }

   @GwtIncompatible("To be supported")
   CacheBuilder lenientParsing() {
      this.strictParsing = false;
      return this;
   }

   @GwtIncompatible("To be supported")
   CacheBuilder keyEquivalence(Equivalence equivalence) {
      Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[]{this.keyEquivalence});
      this.keyEquivalence = (Equivalence)Preconditions.checkNotNull(equivalence);
      return this;
   }

   Equivalence getKeyEquivalence() {
      return (Equivalence)Objects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
   }

   @GwtIncompatible("To be supported")
   CacheBuilder valueEquivalence(Equivalence equivalence) {
      Preconditions.checkState(this.valueEquivalence == null, "value equivalence was already set to %s", new Object[]{this.valueEquivalence});
      this.valueEquivalence = (Equivalence)Preconditions.checkNotNull(equivalence);
      return this;
   }

   Equivalence getValueEquivalence() {
      return (Equivalence)Objects.firstNonNull(this.valueEquivalence, this.getValueStrength().defaultEquivalence());
   }

   public CacheBuilder initialCapacity(int initialCapacity) {
      Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[]{Integer.valueOf(this.initialCapacity)});
      Preconditions.checkArgument(initialCapacity >= 0);
      this.initialCapacity = initialCapacity;
      return this;
   }

   int getInitialCapacity() {
      return this.initialCapacity == -1?16:this.initialCapacity;
   }

   public CacheBuilder concurrencyLevel(int concurrencyLevel) {
      Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[]{Integer.valueOf(this.concurrencyLevel)});
      Preconditions.checkArgument(concurrencyLevel > 0);
      this.concurrencyLevel = concurrencyLevel;
      return this;
   }

   int getConcurrencyLevel() {
      return this.concurrencyLevel == -1?4:this.concurrencyLevel;
   }

   public CacheBuilder maximumSize(long size) {
      Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[]{Long.valueOf(this.maximumSize)});
      Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[]{Long.valueOf(this.maximumWeight)});
      Preconditions.checkState(this.weigher == null, "maximum size can not be combined with weigher");
      Preconditions.checkArgument(size >= 0L, "maximum size must not be negative");
      this.maximumSize = size;
      return this;
   }

   @GwtIncompatible("To be supported")
   public CacheBuilder maximumWeight(long weight) {
      Preconditions.checkState(this.maximumWeight == -1L, "maximum weight was already set to %s", new Object[]{Long.valueOf(this.maximumWeight)});
      Preconditions.checkState(this.maximumSize == -1L, "maximum size was already set to %s", new Object[]{Long.valueOf(this.maximumSize)});
      this.maximumWeight = weight;
      Preconditions.checkArgument(weight >= 0L, "maximum weight must not be negative");
      return this;
   }

   @GwtIncompatible("To be supported")
   public CacheBuilder weigher(Weigher weigher) {
      Preconditions.checkState(this.weigher == null);
      if(this.strictParsing) {
         Preconditions.checkState(this.maximumSize == -1L, "weigher can not be combined with maximum size", new Object[]{Long.valueOf(this.maximumSize)});
      }

      this.weigher = (Weigher)Preconditions.checkNotNull(weigher);
      return this;
   }

   long getMaximumWeight() {
      return this.expireAfterWriteNanos != 0L && this.expireAfterAccessNanos != 0L?(this.weigher == null?this.maximumSize:this.maximumWeight):0L;
   }

   Weigher getWeigher() {
      return (Weigher)Objects.firstNonNull(this.weigher, CacheBuilder.OneWeigher.INSTANCE);
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public CacheBuilder weakKeys() {
      return this.setKeyStrength(LocalCache.Strength.WEAK);
   }

   CacheBuilder setKeyStrength(LocalCache.Strength strength) {
      Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[]{this.keyStrength});
      this.keyStrength = (LocalCache.Strength)Preconditions.checkNotNull(strength);
      return this;
   }

   LocalCache.Strength getKeyStrength() {
      return (LocalCache.Strength)Objects.firstNonNull(this.keyStrength, LocalCache.Strength.STRONG);
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public CacheBuilder weakValues() {
      return this.setValueStrength(LocalCache.Strength.WEAK);
   }

   @GwtIncompatible("java.lang.ref.SoftReference")
   public CacheBuilder softValues() {
      return this.setValueStrength(LocalCache.Strength.SOFT);
   }

   CacheBuilder setValueStrength(LocalCache.Strength strength) {
      Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[]{this.valueStrength});
      this.valueStrength = (LocalCache.Strength)Preconditions.checkNotNull(strength);
      return this;
   }

   LocalCache.Strength getValueStrength() {
      return (LocalCache.Strength)Objects.firstNonNull(this.valueStrength, LocalCache.Strength.STRONG);
   }

   public CacheBuilder expireAfterWrite(long duration, TimeUnit unit) {
      Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[]{Long.valueOf(this.expireAfterWriteNanos)});
      Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[]{Long.valueOf(duration), unit});
      this.expireAfterWriteNanos = unit.toNanos(duration);
      return this;
   }

   long getExpireAfterWriteNanos() {
      return this.expireAfterWriteNanos == -1L?0L:this.expireAfterWriteNanos;
   }

   public CacheBuilder expireAfterAccess(long duration, TimeUnit unit) {
      Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[]{Long.valueOf(this.expireAfterAccessNanos)});
      Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[]{Long.valueOf(duration), unit});
      this.expireAfterAccessNanos = unit.toNanos(duration);
      return this;
   }

   long getExpireAfterAccessNanos() {
      return this.expireAfterAccessNanos == -1L?0L:this.expireAfterAccessNanos;
   }

   @Beta
   @GwtIncompatible("To be supported (synchronously).")
   public CacheBuilder refreshAfterWrite(long duration, TimeUnit unit) {
      Preconditions.checkNotNull(unit);
      Preconditions.checkState(this.refreshNanos == -1L, "refresh was already set to %s ns", new Object[]{Long.valueOf(this.refreshNanos)});
      Preconditions.checkArgument(duration > 0L, "duration must be positive: %s %s", new Object[]{Long.valueOf(duration), unit});
      this.refreshNanos = unit.toNanos(duration);
      return this;
   }

   long getRefreshNanos() {
      return this.refreshNanos == -1L?0L:this.refreshNanos;
   }

   public CacheBuilder ticker(Ticker ticker) {
      Preconditions.checkState(this.ticker == null);
      this.ticker = (Ticker)Preconditions.checkNotNull(ticker);
      return this;
   }

   Ticker getTicker(boolean recordsTime) {
      return this.ticker != null?this.ticker:(recordsTime?Ticker.systemTicker():NULL_TICKER);
   }

   @CheckReturnValue
   public CacheBuilder removalListener(RemovalListener listener) {
      Preconditions.checkState(this.removalListener == null);
      this.removalListener = (RemovalListener)Preconditions.checkNotNull(listener);
      return this;
   }

   RemovalListener getRemovalListener() {
      return (RemovalListener)Objects.firstNonNull(this.removalListener, CacheBuilder.NullListener.INSTANCE);
   }

   public CacheBuilder recordStats() {
      this.statsCounterSupplier = CACHE_STATS_COUNTER;
      return this;
   }

   boolean isRecordingStats() {
      return this.statsCounterSupplier == CACHE_STATS_COUNTER;
   }

   Supplier getStatsCounterSupplier() {
      return this.statsCounterSupplier;
   }

   public LoadingCache build(CacheLoader loader) {
      this.checkWeightWithWeigher();
      return new LocalCache.LocalLoadingCache(this, loader);
   }

   public Cache build() {
      this.checkWeightWithWeigher();
      this.checkNonLoadingCache();
      return new LocalCache.LocalManualCache(this);
   }

   private void checkNonLoadingCache() {
      Preconditions.checkState(this.refreshNanos == -1L, "refreshAfterWrite requires a LoadingCache");
   }

   private void checkWeightWithWeigher() {
      if(this.weigher == null) {
         Preconditions.checkState(this.maximumWeight == -1L, "maximumWeight requires weigher");
      } else if(this.strictParsing) {
         Preconditions.checkState(this.maximumWeight != -1L, "weigher requires maximumWeight");
      } else if(this.maximumWeight == -1L) {
         logger.log(Level.WARNING, "ignoring weigher specified without maximumWeight");
      }

   }

   public String toString() {
      Objects.ToStringHelper s = Objects.toStringHelper((Object)this);
      if(this.initialCapacity != -1) {
         s.add("initialCapacity", this.initialCapacity);
      }

      if(this.concurrencyLevel != -1) {
         s.add("concurrencyLevel", this.concurrencyLevel);
      }

      if(this.maximumSize != -1L) {
         s.add("maximumSize", this.maximumSize);
      }

      if(this.maximumWeight != -1L) {
         s.add("maximumWeight", this.maximumWeight);
      }

      if(this.expireAfterWriteNanos != -1L) {
         s.add("expireAfterWrite", this.expireAfterWriteNanos + "ns");
      }

      if(this.expireAfterAccessNanos != -1L) {
         s.add("expireAfterAccess", this.expireAfterAccessNanos + "ns");
      }

      if(this.keyStrength != null) {
         s.add("keyStrength", Ascii.toLowerCase(this.keyStrength.toString()));
      }

      if(this.valueStrength != null) {
         s.add("valueStrength", Ascii.toLowerCase(this.valueStrength.toString()));
      }

      if(this.keyEquivalence != null) {
         s.addValue("keyEquivalence");
      }

      if(this.valueEquivalence != null) {
         s.addValue("valueEquivalence");
      }

      if(this.removalListener != null) {
         s.addValue("removalListener");
      }

      return s.toString();
   }

   static enum NullListener implements RemovalListener {
      INSTANCE;

      public void onRemoval(RemovalNotification notification) {
      }
   }

   static enum OneWeigher implements Weigher {
      INSTANCE;

      public int weigh(Object key, Object value) {
         return 1;
      }
   }
}
