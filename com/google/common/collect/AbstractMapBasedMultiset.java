package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Count;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.primitives.Ints;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
abstract class AbstractMapBasedMultiset extends AbstractMultiset implements Serializable {
   private transient Map backingMap;
   private transient long size;
   @GwtIncompatible("not needed in emulated source.")
   private static final long serialVersionUID = -2250766705698539974L;

   protected AbstractMapBasedMultiset(Map backingMap) {
      this.backingMap = (Map)Preconditions.checkNotNull(backingMap);
      this.size = (long)super.size();
   }

   void setBackingMap(Map backingMap) {
      this.backingMap = backingMap;
   }

   public Set entrySet() {
      return super.entrySet();
   }

   Iterator entryIterator() {
      final Iterator<Entry<E, Count>> backingEntries = this.backingMap.entrySet().iterator();
      return new Iterator() {
         Entry toRemove;

         public boolean hasNext() {
            return backingEntries.hasNext();
         }

         public Multiset.Entry next() {
            final Entry<E, Count> mapEntry = (Entry)backingEntries.next();
            this.toRemove = mapEntry;
            return new Multisets.AbstractEntry() {
               public Object getElement() {
                  return mapEntry.getKey();
               }

               public int getCount() {
                  Count count = (Count)mapEntry.getValue();
                  if(count == null || count.get() == 0) {
                     Count frequency = (Count)AbstractMapBasedMultiset.this.backingMap.get(this.getElement());
                     if(frequency != null) {
                        return frequency.get();
                     }
                  }

                  return count == null?0:count.get();
               }
            };
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.toRemove != null);
            AbstractMapBasedMultiset.this.size = (long)((Count)this.toRemove.getValue()).getAndSet(0);
            backingEntries.remove();
            this.toRemove = null;
         }
      };
   }

   public void clear() {
      for(Count frequency : this.backingMap.values()) {
         frequency.set(0);
      }

      this.backingMap.clear();
      this.size = 0L;
   }

   int distinctElements() {
      return this.backingMap.size();
   }

   public int size() {
      return Ints.saturatedCast(this.size);
   }

   public Iterator iterator() {
      return new AbstractMapBasedMultiset.MapBasedMultisetIterator();
   }

   public int count(@Nullable Object element) {
      Count frequency = (Count)Maps.safeGet(this.backingMap, element);
      return frequency == null?0:frequency.get();
   }

   public int add(@Nullable Object element, int occurrences) {
      if(occurrences == 0) {
         return this.count(element);
      } else {
         Preconditions.checkArgument(occurrences > 0, "occurrences cannot be negative: %s", new Object[]{Integer.valueOf(occurrences)});
         Count frequency = (Count)this.backingMap.get(element);
         int oldCount;
         if(frequency == null) {
            oldCount = 0;
            this.backingMap.put(element, new Count(occurrences));
         } else {
            oldCount = frequency.get();
            long newCount = (long)oldCount + (long)occurrences;
            Preconditions.checkArgument(newCount <= 2147483647L, "too many occurrences: %s", new Object[]{Long.valueOf(newCount)});
            frequency.getAndAdd(occurrences);
         }

         this.size += (long)occurrences;
         return oldCount;
      }
   }

   public int remove(@Nullable Object element, int occurrences) {
      if(occurrences == 0) {
         return this.count(element);
      } else {
         Preconditions.checkArgument(occurrences > 0, "occurrences cannot be negative: %s", new Object[]{Integer.valueOf(occurrences)});
         Count frequency = (Count)this.backingMap.get(element);
         if(frequency == null) {
            return 0;
         } else {
            int oldCount = frequency.get();
            int numberRemoved;
            if(oldCount > occurrences) {
               numberRemoved = occurrences;
            } else {
               numberRemoved = oldCount;
               this.backingMap.remove(element);
            }

            frequency.addAndGet(-numberRemoved);
            this.size -= (long)numberRemoved;
            return oldCount;
         }
      }
   }

   public int setCount(@Nullable Object element, int count) {
      CollectPreconditions.checkNonnegative(count, "count");
      int oldCount;
      if(count == 0) {
         Count existingCounter = (Count)this.backingMap.remove(element);
         oldCount = getAndSet(existingCounter, count);
      } else {
         Count existingCounter = (Count)this.backingMap.get(element);
         oldCount = getAndSet(existingCounter, count);
         if(existingCounter == null) {
            this.backingMap.put(element, new Count(count));
         }
      }

      this.size += (long)(count - oldCount);
      return oldCount;
   }

   private static int getAndSet(Count i, int count) {
      return i == null?0:i.getAndSet(count);
   }

   @GwtIncompatible("java.io.ObjectStreamException")
   private void readObjectNoData() throws ObjectStreamException {
      throw new InvalidObjectException("Stream data required");
   }

   private class MapBasedMultisetIterator implements Iterator {
      final Iterator entryIterator;
      Entry currentEntry;
      int occurrencesLeft;
      boolean canRemove;

      MapBasedMultisetIterator() {
         this.entryIterator = AbstractMapBasedMultiset.this.backingMap.entrySet().iterator();
      }

      public boolean hasNext() {
         return this.occurrencesLeft > 0 || this.entryIterator.hasNext();
      }

      public Object next() {
         if(this.occurrencesLeft == 0) {
            this.currentEntry = (Entry)this.entryIterator.next();
            this.occurrencesLeft = ((Count)this.currentEntry.getValue()).get();
         }

         --this.occurrencesLeft;
         this.canRemove = true;
         return this.currentEntry.getKey();
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.canRemove);
         int frequency = ((Count)this.currentEntry.getValue()).get();
         if(frequency <= 0) {
            throw new ConcurrentModificationException();
         } else {
            if(((Count)this.currentEntry.getValue()).addAndGet(-1) == 0) {
               this.entryIterator.remove();
            }

            AbstractMapBasedMultiset.this.size--;
            this.canRemove = false;
         }
      }
   }
}
