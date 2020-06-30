package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Equivalence;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Interner;
import com.google.common.collect.MapMaker;
import com.google.common.collect.MapMakerInternalMap;
import java.util.concurrent.ConcurrentMap;

@Beta
public final class Interners {
   public static Interner newStrongInterner() {
      final ConcurrentMap<E, E> map = (new MapMaker()).makeMap();
      return new Interner() {
         public Object intern(Object sample) {
            E canonical = map.putIfAbsent(Preconditions.checkNotNull(sample), sample);
            return canonical == null?sample:canonical;
         }
      };
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   public static Interner newWeakInterner() {
      return new Interners.WeakInterner();
   }

   public static Function asFunction(Interner interner) {
      return new Interners.InternerFunction((Interner)Preconditions.checkNotNull(interner));
   }

   private static class InternerFunction implements Function {
      private final Interner interner;

      public InternerFunction(Interner interner) {
         this.interner = interner;
      }

      public Object apply(Object input) {
         return this.interner.intern(input);
      }

      public int hashCode() {
         return this.interner.hashCode();
      }

      public boolean equals(Object other) {
         if(other instanceof Interners.InternerFunction) {
            Interners.InternerFunction<?> that = (Interners.InternerFunction)other;
            return this.interner.equals(that.interner);
         } else {
            return false;
         }
      }
   }

   private static class WeakInterner implements Interner {
      private final MapMakerInternalMap map;

      private WeakInterner() {
         this.map = (new MapMaker()).weakKeys().keyEquivalence(Equivalence.equals()).makeCustomMap();
      }

      public Object intern(Object sample) {
         while(true) {
            MapMakerInternalMap.ReferenceEntry<E, Interners.WeakInterner.Dummy> entry = this.map.getEntry(sample);
            if(entry != null) {
               E canonical = entry.getKey();
               if(canonical != null) {
                  return canonical;
               }
            }

            Interners.WeakInterner.Dummy sneaky = (Interners.WeakInterner.Dummy)this.map.putIfAbsent(sample, Interners.WeakInterner.Dummy.VALUE);
            if(sneaky == null) {
               break;
            }
         }

         return sample;
      }

      private static enum Dummy {
         VALUE;
      }
   }
}
