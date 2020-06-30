package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Ascii;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.base.Ticker;
import com.google.common.collect.ComputationException;
import com.google.common.collect.ComputingConcurrentHashMap;
import com.google.common.collect.GenericMapMaker;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.MapMakerInternalMap;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class MapMaker extends GenericMapMaker {
   private static final int DEFAULT_INITIAL_CAPACITY = 16;
   private static final int DEFAULT_CONCURRENCY_LEVEL = 4;
   private static final int DEFAULT_EXPIRATION_NANOS = 0;
   static final int UNSET_INT = -1;
   boolean useCustomMap;
   int initialCapacity = -1;
   int concurrencyLevel = -1;
   int maximumSize = -1;
   MapMakerInternalMap.Strength keyStrength;
   MapMakerInternalMap.Strength valueStrength;
   long expireAfterWriteNanos = -1L;
   long expireAfterAccessNanos = -1L;
   MapMaker.RemovalCause nullRemovalCause;
   Equivalence keyEquivalence;
   Ticker ticker;

   @GwtIncompatible("To be supported")
   MapMaker keyEquivalence(Equivalence equivalence) {
      Preconditions.checkState(this.keyEquivalence == null, "key equivalence was already set to %s", new Object[]{this.keyEquivalence});
      this.keyEquivalence = (Equivalence)Preconditions.checkNotNull(equivalence);
      this.useCustomMap = true;
      return this;
   }

   Equivalence getKeyEquivalence() {
      return (Equivalence)Objects.firstNonNull(this.keyEquivalence, this.getKeyStrength().defaultEquivalence());
   }

   public MapMaker initialCapacity(int initialCapacity) {
      Preconditions.checkState(this.initialCapacity == -1, "initial capacity was already set to %s", new Object[]{Integer.valueOf(this.initialCapacity)});
      Preconditions.checkArgument(initialCapacity >= 0);
      this.initialCapacity = initialCapacity;
      return this;
   }

   int getInitialCapacity() {
      return this.initialCapacity == -1?16:this.initialCapacity;
   }

   /** @deprecated */
   @Deprecated
   MapMaker maximumSize(int size) {
      Preconditions.checkState(this.maximumSize == -1, "maximum size was already set to %s", new Object[]{Integer.valueOf(this.maximumSize)});
      Preconditions.checkArgument(size >= 0, "maximum size must not be negative");
      this.maximumSize = size;
      this.useCustomMap = true;
      if(this.maximumSize == 0) {
         this.nullRemovalCause = MapMaker.RemovalCause.SIZE;
      }

      return this;
   }

   public MapMaker concurrencyLevel(int concurrencyLevel) {
      Preconditions.checkState(this.concurrencyLevel == -1, "concurrency level was already set to %s", new Object[]{Integer.valueOf(this.concurrencyLevel)});
      Preconditions.checkArgument(concurrencyLevel > 0);
      this.concurrencyLevel = concurrencyLevel;
      return this;
   }

   int getConcurrencyLevel() {
      return this.concurrencyLevel == -1?4:this.concurrencyLevel;
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public MapMaker weakKeys() {
      return this.setKeyStrength(MapMakerInternalMap.Strength.WEAK);
   }

   MapMaker setKeyStrength(MapMakerInternalMap.Strength strength) {
      Preconditions.checkState(this.keyStrength == null, "Key strength was already set to %s", new Object[]{this.keyStrength});
      this.keyStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength);
      Preconditions.checkArgument(this.keyStrength != MapMakerInternalMap.Strength.SOFT, "Soft keys are not supported");
      if(strength != MapMakerInternalMap.Strength.STRONG) {
         this.useCustomMap = true;
      }

      return this;
   }

   MapMakerInternalMap.Strength getKeyStrength() {
      return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.keyStrength, MapMakerInternalMap.Strength.STRONG);
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public MapMaker weakValues() {
      return this.setValueStrength(MapMakerInternalMap.Strength.WEAK);
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible("java.lang.ref.SoftReference")
   public MapMaker softValues() {
      return this.setValueStrength(MapMakerInternalMap.Strength.SOFT);
   }

   MapMaker setValueStrength(MapMakerInternalMap.Strength strength) {
      Preconditions.checkState(this.valueStrength == null, "Value strength was already set to %s", new Object[]{this.valueStrength});
      this.valueStrength = (MapMakerInternalMap.Strength)Preconditions.checkNotNull(strength);
      if(strength != MapMakerInternalMap.Strength.STRONG) {
         this.useCustomMap = true;
      }

      return this;
   }

   MapMakerInternalMap.Strength getValueStrength() {
      return (MapMakerInternalMap.Strength)Objects.firstNonNull(this.valueStrength, MapMakerInternalMap.Strength.STRONG);
   }

   /** @deprecated */
   @Deprecated
   MapMaker expireAfterWrite(long duration, TimeUnit unit) {
      this.checkExpiration(duration, unit);
      this.expireAfterWriteNanos = unit.toNanos(duration);
      if(duration == 0L && this.nullRemovalCause == null) {
         this.nullRemovalCause = MapMaker.RemovalCause.EXPIRED;
      }

      this.useCustomMap = true;
      return this;
   }

   private void checkExpiration(long duration, TimeUnit unit) {
      Preconditions.checkState(this.expireAfterWriteNanos == -1L, "expireAfterWrite was already set to %s ns", new Object[]{Long.valueOf(this.expireAfterWriteNanos)});
      Preconditions.checkState(this.expireAfterAccessNanos == -1L, "expireAfterAccess was already set to %s ns", new Object[]{Long.valueOf(this.expireAfterAccessNanos)});
      Preconditions.checkArgument(duration >= 0L, "duration cannot be negative: %s %s", new Object[]{Long.valueOf(duration), unit});
   }

   long getExpireAfterWriteNanos() {
      return this.expireAfterWriteNanos == -1L?0L:this.expireAfterWriteNanos;
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible("To be supported")
   MapMaker expireAfterAccess(long duration, TimeUnit unit) {
      this.checkExpiration(duration, unit);
      this.expireAfterAccessNanos = unit.toNanos(duration);
      if(duration == 0L && this.nullRemovalCause == null) {
         this.nullRemovalCause = MapMaker.RemovalCause.EXPIRED;
      }

      this.useCustomMap = true;
      return this;
   }

   long getExpireAfterAccessNanos() {
      return this.expireAfterAccessNanos == -1L?0L:this.expireAfterAccessNanos;
   }

   Ticker getTicker() {
      return (Ticker)Objects.firstNonNull(this.ticker, Ticker.systemTicker());
   }

   /** @deprecated */
   @Deprecated
   @GwtIncompatible("To be supported")
   GenericMapMaker removalListener(MapMaker.RemovalListener listener) {
      Preconditions.checkState(this.removalListener == null);
      super.removalListener = (MapMaker.RemovalListener)Preconditions.checkNotNull(listener);
      this.useCustomMap = true;
      return this;
   }

   public ConcurrentMap makeMap() {
      return (ConcurrentMap)(!this.useCustomMap?new ConcurrentHashMap(this.getInitialCapacity(), 0.75F, this.getConcurrencyLevel()):(ConcurrentMap)(this.nullRemovalCause == null?new MapMakerInternalMap(this):new MapMaker.NullConcurrentMap(this)));
   }

   @GwtIncompatible("MapMakerInternalMap")
   MapMakerInternalMap makeCustomMap() {
      return new MapMakerInternalMap(this);
   }

   /** @deprecated */
   @Deprecated
   ConcurrentMap makeComputingMap(Function computingFunction) {
      return (ConcurrentMap)(this.nullRemovalCause == null?new MapMaker.ComputingMapAdapter(this, computingFunction):new MapMaker.NullComputingConcurrentMap(this, computingFunction));
   }

   public String toString() {
      Objects.ToStringHelper s = Objects.toStringHelper((Object)this);
      if(this.initialCapacity != -1) {
         s.add("initialCapacity", this.initialCapacity);
      }

      if(this.concurrencyLevel != -1) {
         s.add("concurrencyLevel", this.concurrencyLevel);
      }

      if(this.maximumSize != -1) {
         s.add("maximumSize", this.maximumSize);
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

      if(this.removalListener != null) {
         s.addValue("removalListener");
      }

      return s.toString();
   }

   static final class ComputingMapAdapter extends ComputingConcurrentHashMap implements Serializable {
      private static final long serialVersionUID = 0L;

      ComputingMapAdapter(MapMaker mapMaker, Function computingFunction) {
         super(mapMaker, computingFunction);
      }

      public Object get(Object key) {
         V value;
         try {
            value = this.getOrCompute(key);
         } catch (ExecutionException var5) {
            Throwable cause = var5.getCause();
            Throwables.propagateIfInstanceOf(cause, ComputationException.class);
            throw new ComputationException(cause);
         }

         if(value == null) {
            throw new NullPointerException(this.computingFunction + " returned null for key " + key + ".");
         } else {
            return value;
         }
      }
   }

   static final class NullComputingConcurrentMap extends MapMaker.NullConcurrentMap {
      private static final long serialVersionUID = 0L;
      final Function computingFunction;

      NullComputingConcurrentMap(MapMaker mapMaker, Function computingFunction) {
         super(mapMaker);
         this.computingFunction = (Function)Preconditions.checkNotNull(computingFunction);
      }

      public Object get(Object k) {
         V value = this.compute(k);
         Preconditions.checkNotNull(value, "%s returned null for key %s.", new Object[]{this.computingFunction, k});
         this.notifyRemoval(k, value);
         return value;
      }

      private Object compute(Object key) {
         Preconditions.checkNotNull(key);

         try {
            return this.computingFunction.apply(key);
         } catch (ComputationException var3) {
            throw var3;
         } catch (Throwable var4) {
            throw new ComputationException(var4);
         }
      }
   }

   static class NullConcurrentMap extends AbstractMap implements ConcurrentMap, Serializable {
      private static final long serialVersionUID = 0L;
      private final MapMaker.RemovalListener removalListener;
      private final MapMaker.RemovalCause removalCause;

      NullConcurrentMap(MapMaker mapMaker) {
         this.removalListener = mapMaker.getRemovalListener();
         this.removalCause = mapMaker.nullRemovalCause;
      }

      public boolean containsKey(@Nullable Object key) {
         return false;
      }

      public boolean containsValue(@Nullable Object value) {
         return false;
      }

      public Object get(@Nullable Object key) {
         return null;
      }

      void notifyRemoval(Object key, Object value) {
         MapMaker.RemovalNotification<K, V> notification = new MapMaker.RemovalNotification(key, value, this.removalCause);
         this.removalListener.onRemoval(notification);
      }

      public Object put(Object key, Object value) {
         Preconditions.checkNotNull(key);
         Preconditions.checkNotNull(value);
         this.notifyRemoval(key, value);
         return null;
      }

      public Object putIfAbsent(Object key, Object value) {
         return this.put(key, value);
      }

      public Object remove(@Nullable Object key) {
         return null;
      }

      public boolean remove(@Nullable Object key, @Nullable Object value) {
         return false;
      }

      public Object replace(Object key, Object value) {
         Preconditions.checkNotNull(key);
         Preconditions.checkNotNull(value);
         return null;
      }

      public boolean replace(Object key, @Nullable Object oldValue, Object newValue) {
         Preconditions.checkNotNull(key);
         Preconditions.checkNotNull(newValue);
         return false;
      }

      public Set entrySet() {
         return Collections.emptySet();
      }
   }

   static enum RemovalCause {
      EXPLICIT {
         boolean wasEvicted() {
            return false;
         }
      },
      REPLACED {
         boolean wasEvicted() {
            return false;
         }
      },
      COLLECTED {
         boolean wasEvicted() {
            return true;
         }
      },
      EXPIRED {
         boolean wasEvicted() {
            return true;
         }
      },
      SIZE {
         boolean wasEvicted() {
            return true;
         }
      };

      private RemovalCause() {
      }

      abstract boolean wasEvicted();
   }

   interface RemovalListener {
      void onRemoval(MapMaker.RemovalNotification var1);
   }

   static final class RemovalNotification extends ImmutableEntry {
      private static final long serialVersionUID = 0L;
      private final MapMaker.RemovalCause cause;

      RemovalNotification(@Nullable Object key, @Nullable Object value, MapMaker.RemovalCause cause) {
         super(key, value);
         this.cause = cause;
      }

      public MapMaker.RemovalCause getCause() {
         return this.cause;
      }

      public boolean wasEvicted() {
         return this.cause.wasEvicted();
      }
   }
}
