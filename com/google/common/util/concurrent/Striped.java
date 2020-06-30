package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.MapMaker;
import com.google.common.math.IntMath;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Beta
public abstract class Striped {
   private static final int LARGE_LAZY_CUTOFF = 1024;
   private static final Supplier READ_WRITE_LOCK_SUPPLIER = new Supplier() {
      public ReadWriteLock get() {
         return new ReentrantReadWriteLock();
      }
   };
   private static final int ALL_SET = -1;

   private Striped() {
   }

   public abstract Object get(Object var1);

   public abstract Object getAt(int var1);

   abstract int indexFor(Object var1);

   public abstract int size();

   public Iterable bulkGet(Iterable keys) {
      Object[] array = Iterables.toArray(keys, Object.class);
      if(array.length == 0) {
         return ImmutableList.of();
      } else {
         int[] stripes = new int[array.length];

         for(int i = 0; i < array.length; ++i) {
            stripes[i] = this.indexFor(array[i]);
         }

         Arrays.sort(stripes);
         int previousStripe = stripes[0];
         array[0] = this.getAt(previousStripe);

         for(int i = 1; i < array.length; ++i) {
            int currentStripe = stripes[i];
            if(currentStripe == previousStripe) {
               array[i] = array[i - 1];
            } else {
               array[i] = this.getAt(currentStripe);
               previousStripe = currentStripe;
            }
         }

         List<L> asList = Arrays.asList(array);
         return Collections.unmodifiableList(asList);
      }
   }

   public static Striped lock(int stripes) {
      return new Striped.CompactStriped(stripes, new Supplier() {
         public Lock get() {
            return new Striped.PaddedLock();
         }
      });
   }

   public static Striped lazyWeakLock(int stripes) {
      return lazy(stripes, new Supplier() {
         public Lock get() {
            return new ReentrantLock(false);
         }
      });
   }

   private static Striped lazy(int stripes, Supplier supplier) {
      return (Striped)(stripes < 1024?new Striped.SmallLazyStriped(stripes, supplier):new Striped.LargeLazyStriped(stripes, supplier));
   }

   public static Striped semaphore(int stripes, final int permits) {
      return new Striped.CompactStriped(stripes, new Supplier() {
         public Semaphore get() {
            return new Striped.PaddedSemaphore(permits);
         }
      });
   }

   public static Striped lazyWeakSemaphore(int stripes, final int permits) {
      return lazy(stripes, new Supplier() {
         public Semaphore get() {
            return new Semaphore(permits, false);
         }
      });
   }

   public static Striped readWriteLock(int stripes) {
      return new Striped.CompactStriped(stripes, READ_WRITE_LOCK_SUPPLIER);
   }

   public static Striped lazyWeakReadWriteLock(int stripes) {
      return lazy(stripes, READ_WRITE_LOCK_SUPPLIER);
   }

   private static int ceilToPowerOfTwo(int x) {
      return 1 << IntMath.log2(x, RoundingMode.CEILING);
   }

   private static int smear(int hashCode) {
      hashCode = hashCode ^ hashCode >>> 20 ^ hashCode >>> 12;
      return hashCode ^ hashCode >>> 7 ^ hashCode >>> 4;
   }

   private static class CompactStriped extends Striped.PowerOfTwoStriped {
      private final Object[] array;

      private CompactStriped(int stripes, Supplier supplier) {
         super(stripes);
         Preconditions.checkArgument(stripes <= 1073741824, "Stripes must be <= 2^30)");
         this.array = new Object[this.mask + 1];

         for(int i = 0; i < this.array.length; ++i) {
            this.array[i] = supplier.get();
         }

      }

      public Object getAt(int index) {
         return this.array[index];
      }

      public int size() {
         return this.array.length;
      }
   }

   @VisibleForTesting
   static class LargeLazyStriped extends Striped.PowerOfTwoStriped {
      final ConcurrentMap locks;
      final Supplier supplier;
      final int size;

      LargeLazyStriped(int stripes, Supplier supplier) {
         super(stripes);
         this.size = this.mask == -1?Integer.MAX_VALUE:this.mask + 1;
         this.supplier = supplier;
         this.locks = (new MapMaker()).weakValues().makeMap();
      }

      public Object getAt(int index) {
         if(this.size != Integer.MAX_VALUE) {
            Preconditions.checkElementIndex(index, this.size());
         }

         L existing = this.locks.get(Integer.valueOf(index));
         if(existing != null) {
            return existing;
         } else {
            L created = this.supplier.get();
            existing = this.locks.putIfAbsent(Integer.valueOf(index), created);
            return Objects.firstNonNull(existing, created);
         }
      }

      public int size() {
         return this.size;
      }
   }

   private static class PaddedLock extends ReentrantLock {
      long q1;
      long q2;
      long q3;

      PaddedLock() {
         super(false);
      }
   }

   private static class PaddedSemaphore extends Semaphore {
      long q1;
      long q2;
      long q3;

      PaddedSemaphore(int permits) {
         super(permits, false);
      }
   }

   private abstract static class PowerOfTwoStriped extends Striped {
      final int mask;

      PowerOfTwoStriped(int stripes) {
         super(null);
         Preconditions.checkArgument(stripes > 0, "Stripes must be positive");
         this.mask = stripes > 1073741824?-1:Striped.ceilToPowerOfTwo(stripes) - 1;
      }

      final int indexFor(Object key) {
         int hash = Striped.smear(key.hashCode());
         return hash & this.mask;
      }

      public final Object get(Object key) {
         return this.getAt(this.indexFor(key));
      }
   }

   @VisibleForTesting
   static class SmallLazyStriped extends Striped.PowerOfTwoStriped {
      final AtomicReferenceArray locks;
      final Supplier supplier;
      final int size;
      final ReferenceQueue queue = new ReferenceQueue();

      SmallLazyStriped(int stripes, Supplier supplier) {
         super(stripes);
         this.size = this.mask == -1?Integer.MAX_VALUE:this.mask + 1;
         this.locks = new AtomicReferenceArray(this.size);
         this.supplier = supplier;
      }

      public Object getAt(int index) {
         if(this.size != Integer.MAX_VALUE) {
            Preconditions.checkElementIndex(index, this.size());
         }

         Striped.SmallLazyStriped.ArrayReference<? extends L> existingRef = (Striped.SmallLazyStriped.ArrayReference)this.locks.get(index);
         L existing = existingRef == null?null:existingRef.get();
         if(existing != null) {
            return existing;
         } else {
            L created = this.supplier.get();
            Striped.SmallLazyStriped.ArrayReference<L> newRef = new Striped.SmallLazyStriped.ArrayReference(created, index, this.queue);

            while(!this.locks.compareAndSet(index, existingRef, newRef)) {
               existingRef = (Striped.SmallLazyStriped.ArrayReference)this.locks.get(index);
               existing = existingRef == null?null:existingRef.get();
               if(existing != null) {
                  return existing;
               }
            }

            this.drainQueue();
            return created;
         }
      }

      private void drainQueue() {
         Reference<? extends L> ref;
         while((ref = this.queue.poll()) != null) {
            Striped.SmallLazyStriped.ArrayReference<? extends L> arrayRef = (Striped.SmallLazyStriped.ArrayReference)ref;
            this.locks.compareAndSet(arrayRef.index, arrayRef, (Object)null);
         }

      }

      public int size() {
         return this.size;
      }

      private static final class ArrayReference extends WeakReference {
         final int index;

         ArrayReference(Object referent, int index, ReferenceQueue queue) {
            super(referent, queue);
            this.index = index;
         }
      }
   }
}
