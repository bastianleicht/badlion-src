package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MapMakerInternalMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/** @deprecated */
@Deprecated
@Beta
@GwtCompatible(
   emulated = true
)
abstract class GenericMapMaker {
   @GwtIncompatible("To be supported")
   MapMaker.RemovalListener removalListener;

   @GwtIncompatible("To be supported")
   abstract GenericMapMaker keyEquivalence(Equivalence var1);

   public abstract GenericMapMaker initialCapacity(int var1);

   abstract GenericMapMaker maximumSize(int var1);

   public abstract GenericMapMaker concurrencyLevel(int var1);

   @GwtIncompatible("java.lang.ref.WeakReference")
   public abstract GenericMapMaker weakKeys();

   @GwtIncompatible("java.lang.ref.WeakReference")
   public abstract GenericMapMaker weakValues();

   /** @deprecated */
   @Deprecated
   @GwtIncompatible("java.lang.ref.SoftReference")
   public abstract GenericMapMaker softValues();

   abstract GenericMapMaker expireAfterWrite(long var1, TimeUnit var3);

   @GwtIncompatible("To be supported")
   abstract GenericMapMaker expireAfterAccess(long var1, TimeUnit var3);

   @GwtIncompatible("To be supported")
   MapMaker.RemovalListener getRemovalListener() {
      return (MapMaker.RemovalListener)Objects.firstNonNull(this.removalListener, GenericMapMaker.NullListener.INSTANCE);
   }

   public abstract ConcurrentMap makeMap();

   @GwtIncompatible("MapMakerInternalMap")
   abstract MapMakerInternalMap makeCustomMap();

   /** @deprecated */
   @Deprecated
   abstract ConcurrentMap makeComputingMap(Function var1);

   @GwtIncompatible("To be supported")
   static enum NullListener implements MapMaker.RemovalListener {
      INSTANCE;

      public void onRemoval(MapMaker.RemovalNotification notification) {
      }
   }
}
