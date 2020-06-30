package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Serialization;
import com.google.common.math.IntMath;
import com.google.common.primitives.Ints;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public final class ConcurrentHashMultiset extends AbstractMultiset implements Serializable {
   private final transient ConcurrentMap countMap;
   private transient ConcurrentHashMultiset.EntrySet entrySet;
   private static final long serialVersionUID = 1L;

   public static ConcurrentHashMultiset create() {
      return new ConcurrentHashMultiset(new ConcurrentHashMap());
   }

   public static ConcurrentHashMultiset create(Iterable elements) {
      ConcurrentHashMultiset<E> multiset = create();
      Iterables.addAll(multiset, elements);
      return multiset;
   }

   @Beta
   public static ConcurrentHashMultiset create(MapMaker mapMaker) {
      return new ConcurrentHashMultiset(mapMaker.makeMap());
   }

   @VisibleForTesting
   ConcurrentHashMultiset(ConcurrentMap countMap) {
      Preconditions.checkArgument(countMap.isEmpty());
      this.countMap = countMap;
   }

   public int count(@Nullable Object element) {
      AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
      return existingCounter == null?0:existingCounter.get();
   }

   public int size() {
      long sum = 0L;

      for(AtomicInteger value : this.countMap.values()) {
         sum += (long)value.get();
      }

      return Ints.saturatedCast(sum);
   }

   public Object[] toArray() {
      return this.snapshot().toArray();
   }

   public Object[] toArray(Object[] array) {
      return this.snapshot().toArray(array);
   }

   private List snapshot() {
      List<E> list = Lists.newArrayListWithExpectedSize(this.size());

      for(Multiset.Entry<E> entry : this.entrySet()) {
         E element = entry.getElement();

         for(int i = entry.getCount(); i > 0; --i) {
            list.add(element);
         }
      }

      return list;
   }

   public int add(Object element, int occurrences) {
      Preconditions.checkNotNull(element);
      if(occurrences == 0) {
         return this.count(element);
      } else {
         Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[]{Integer.valueOf(occurrences)});

         while(true) {
            AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
            if(existingCounter == null) {
               existingCounter = (AtomicInteger)this.countMap.putIfAbsent(element, new AtomicInteger(occurrences));
               if(existingCounter == null) {
                  return 0;
               }
            }

            while(true) {
               int oldValue = existingCounter.get();
               if(oldValue == 0) {
                  AtomicInteger newCounter = new AtomicInteger(occurrences);
                  if(this.countMap.putIfAbsent(element, newCounter) == null || this.countMap.replace(element, existingCounter, newCounter)) {
                     return 0;
                  }
                  break;
               }

               try {
                  int newValue = IntMath.checkedAdd(oldValue, occurrences);
                  if(existingCounter.compareAndSet(oldValue, newValue)) {
                     return oldValue;
                  }
               } catch (ArithmeticException var6) {
                  throw new IllegalArgumentException("Overflow adding " + occurrences + " occurrences to a count of " + oldValue);
               }
            }
         }
      }
   }

   public int remove(@Nullable Object element, int occurrences) {
      if(occurrences == 0) {
         return this.count(element);
      } else {
         Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[]{Integer.valueOf(occurrences)});
         AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
         if(existingCounter == null) {
            return 0;
         } else {
            int oldValue;
            int newValue;
            while(true) {
               oldValue = existingCounter.get();
               if(oldValue == 0) {
                  return 0;
               }

               newValue = Math.max(0, oldValue - occurrences);
               if(existingCounter.compareAndSet(oldValue, newValue)) {
                  break;
               }
            }

            if(newValue == 0) {
               this.countMap.remove(element, existingCounter);
            }

            return oldValue;
         }
      }
   }

   public boolean removeExactly(@Nullable Object element, int occurrences) {
      if(occurrences == 0) {
         return true;
      } else {
         Preconditions.checkArgument(occurrences > 0, "Invalid occurrences: %s", new Object[]{Integer.valueOf(occurrences)});
         AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
         if(existingCounter == null) {
            return false;
         } else {
            int newValue;
            while(true) {
               int oldValue = existingCounter.get();
               if(oldValue < occurrences) {
                  return false;
               }

               newValue = oldValue - occurrences;
               if(existingCounter.compareAndSet(oldValue, newValue)) {
                  break;
               }
            }

            if(newValue == 0) {
               this.countMap.remove(element, existingCounter);
            }

            return true;
         }
      }
   }

   public int setCount(Object element, int count) {
      Preconditions.checkNotNull(element);
      CollectPreconditions.checkNonnegative(count, "count");

      label12:
      while(true) {
         AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
         if(existingCounter == null) {
            if(count == 0) {
               return 0;
            }

            existingCounter = (AtomicInteger)this.countMap.putIfAbsent(element, new AtomicInteger(count));
            if(existingCounter == null) {
               return 0;
            }
         }

         int oldValue;
         while(true) {
            oldValue = existingCounter.get();
            if(oldValue == 0) {
               if(count == 0) {
                  return 0;
               }

               AtomicInteger newCounter = new AtomicInteger(count);
               if(this.countMap.putIfAbsent(element, newCounter) == null || this.countMap.replace(element, existingCounter, newCounter)) {
                  return 0;
               }
               continue label12;
            }

            if(existingCounter.compareAndSet(oldValue, count)) {
               break;
            }
         }

         if(count == 0) {
            this.countMap.remove(element, existingCounter);
         }

         return oldValue;
      }
   }

   public boolean setCount(Object element, int expectedOldCount, int newCount) {
      Preconditions.checkNotNull(element);
      CollectPreconditions.checkNonnegative(expectedOldCount, "oldCount");
      CollectPreconditions.checkNonnegative(newCount, "newCount");
      AtomicInteger existingCounter = (AtomicInteger)Maps.safeGet(this.countMap, element);
      if(existingCounter == null) {
         return expectedOldCount != 0?false:(newCount == 0?true:this.countMap.putIfAbsent(element, new AtomicInteger(newCount)) == null);
      } else {
         int oldValue = existingCounter.get();
         if(oldValue == expectedOldCount) {
            if(oldValue == 0) {
               if(newCount == 0) {
                  this.countMap.remove(element, existingCounter);
                  return true;
               }

               AtomicInteger newCounter = new AtomicInteger(newCount);
               return this.countMap.putIfAbsent(element, newCounter) == null || this.countMap.replace(element, existingCounter, newCounter);
            }

            if(existingCounter.compareAndSet(oldValue, newCount)) {
               if(newCount == 0) {
                  this.countMap.remove(element, existingCounter);
               }

               return true;
            }
         }

         return false;
      }
   }

   Set createElementSet() {
      final Set<E> delegate = this.countMap.keySet();
      return new ForwardingSet() {
         protected Set delegate() {
            return delegate;
         }

         public boolean contains(@Nullable Object object) {
            return object != null && Collections2.safeContains(delegate, object);
         }

         public boolean containsAll(Collection collection) {
            return this.standardContainsAll(collection);
         }

         public boolean remove(Object object) {
            return object != null && Collections2.safeRemove(delegate, object);
         }

         public boolean removeAll(Collection c) {
            return this.standardRemoveAll(c);
         }
      };
   }

   public Set entrySet() {
      ConcurrentHashMultiset<E>.EntrySet result = this.entrySet;
      if(result == null) {
         this.entrySet = result = new ConcurrentHashMultiset.EntrySet();
      }

      return result;
   }

   int distinctElements() {
      return this.countMap.size();
   }

   public boolean isEmpty() {
      return this.countMap.isEmpty();
   }

   Iterator entryIterator() {
      final Iterator<Multiset.Entry<E>> readOnlyIterator = new AbstractIterator() {
         private Iterator mapEntries;

         {
            this.mapEntries = ConcurrentHashMultiset.this.countMap.entrySet().iterator();
         }

         protected Multiset.Entry computeNext() {
            while(this.mapEntries.hasNext()) {
               Entry<E, AtomicInteger> mapEntry = (Entry)this.mapEntries.next();
               int count = ((AtomicInteger)mapEntry.getValue()).get();
               if(count != 0) {
                  return Multisets.immutableEntry(mapEntry.getKey(), count);
               }
            }

            return (Multiset.Entry)this.endOfData();
         }
      };
      return new ForwardingIterator() {
         private Multiset.Entry last;

         protected Iterator delegate() {
            return readOnlyIterator;
         }

         public Multiset.Entry next() {
            this.last = (Multiset.Entry)super.next();
            return this.last;
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.last != null);
            ConcurrentHashMultiset.this.setCount(this.last.getElement(), 0);
            this.last = null;
         }
      };
   }

   public void clear() {
      this.countMap.clear();
   }

   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeObject(this.countMap);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      ConcurrentMap<E, Integer> deserializedCountMap = (ConcurrentMap)stream.readObject();
      ConcurrentHashMultiset.FieldSettersHolder.COUNT_MAP_FIELD_SETTER.set(this, deserializedCountMap);
   }

   private class EntrySet extends AbstractMultiset.EntrySet {
      private EntrySet() {
         super();
      }

      ConcurrentHashMultiset multiset() {
         return ConcurrentHashMultiset.this;
      }

      public Object[] toArray() {
         return this.snapshot().toArray();
      }

      public Object[] toArray(Object[] array) {
         return this.snapshot().toArray(array);
      }

      private List snapshot() {
         List<Multiset.Entry<E>> list = Lists.newArrayListWithExpectedSize(this.size());
         Iterators.addAll(list, this.iterator());
         return list;
      }
   }

   private static class FieldSettersHolder {
      static final Serialization.FieldSetter COUNT_MAP_FIELD_SETTER = Serialization.getFieldSetter(ConcurrentHashMultiset.class, "countMap");
   }
}
