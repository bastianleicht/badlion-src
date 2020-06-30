package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ForwardingMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TransformedIterator;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.collect.UnmodifiableSortedMultiset;
import com.google.common.primitives.Ints;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.annotation.Nullable;

@GwtCompatible
public final class Multisets {
   private static final Ordering DECREASING_COUNT_ORDERING = new Ordering() {
      public int compare(Multiset.Entry entry1, Multiset.Entry entry2) {
         return Ints.compare(entry2.getCount(), entry1.getCount());
      }
   };

   public static Multiset unmodifiableMultiset(Multiset multiset) {
      return (Multiset)(!(multiset instanceof Multisets.UnmodifiableMultiset) && !(multiset instanceof ImmutableMultiset)?new Multisets.UnmodifiableMultiset((Multiset)Preconditions.checkNotNull(multiset)):multiset);
   }

   /** @deprecated */
   @Deprecated
   public static Multiset unmodifiableMultiset(ImmutableMultiset multiset) {
      return (Multiset)Preconditions.checkNotNull(multiset);
   }

   @Beta
   public static SortedMultiset unmodifiableSortedMultiset(SortedMultiset sortedMultiset) {
      return new UnmodifiableSortedMultiset((SortedMultiset)Preconditions.checkNotNull(sortedMultiset));
   }

   public static Multiset.Entry immutableEntry(@Nullable Object e, int n) {
      return new Multisets.ImmutableEntry(e, n);
   }

   @Beta
   public static Multiset filter(Multiset unfiltered, Predicate predicate) {
      if(unfiltered instanceof Multisets.FilteredMultiset) {
         Multisets.FilteredMultiset<E> filtered = (Multisets.FilteredMultiset)unfiltered;
         Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
         return new Multisets.FilteredMultiset(filtered.unfiltered, combinedPredicate);
      } else {
         return new Multisets.FilteredMultiset(unfiltered, predicate);
      }
   }

   static int inferDistinctElements(Iterable elements) {
      return elements instanceof Multiset?((Multiset)elements).elementSet().size():11;
   }

   @Beta
   public static Multiset union(final Multiset multiset1, final Multiset multiset2) {
      Preconditions.checkNotNull(multiset1);
      Preconditions.checkNotNull(multiset2);
      return new AbstractMultiset() {
         public boolean contains(@Nullable Object element) {
            return multiset1.contains(element) || multiset2.contains(element);
         }

         public boolean isEmpty() {
            return multiset1.isEmpty() && multiset2.isEmpty();
         }

         public int count(Object element) {
            return Math.max(multiset1.count(element), multiset2.count(element));
         }

         Set createElementSet() {
            return Sets.union(multiset1.elementSet(), multiset2.elementSet());
         }

         Iterator entryIterator() {
            final Iterator<? extends Multiset.Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
            final Iterator<? extends Multiset.Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
            return new AbstractIterator() {
               protected Multiset.Entry computeNext() {
                  if(iterator1.hasNext()) {
                     Multiset.Entry<? extends E> entry1 = (Multiset.Entry)iterator1.next();
                     E element = entry1.getElement();
                     int count = Math.max(entry1.getCount(), multiset2.count(element));
                     return Multisets.immutableEntry(element, count);
                  } else {
                     while(iterator2.hasNext()) {
                        Multiset.Entry<? extends E> entry2 = (Multiset.Entry)iterator2.next();
                        E element = entry2.getElement();
                        if(!multiset1.contains(element)) {
                           return Multisets.immutableEntry(element, entry2.getCount());
                        }
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   public static Multiset intersection(final Multiset multiset1, final Multiset multiset2) {
      Preconditions.checkNotNull(multiset1);
      Preconditions.checkNotNull(multiset2);
      return new AbstractMultiset() {
         public int count(Object element) {
            int count1 = multiset1.count(element);
            return count1 == 0?0:Math.min(count1, multiset2.count(element));
         }

         Set createElementSet() {
            return Sets.intersection(multiset1.elementSet(), multiset2.elementSet());
         }

         Iterator entryIterator() {
            final Iterator<Multiset.Entry<E>> iterator1 = multiset1.entrySet().iterator();
            return new AbstractIterator() {
               protected Multiset.Entry computeNext() {
                  while(true) {
                     if(iterator1.hasNext()) {
                        Multiset.Entry<E> entry1 = (Multiset.Entry)iterator1.next();
                        E element = entry1.getElement();
                        int count = Math.min(entry1.getCount(), multiset2.count(element));
                        if(count <= 0) {
                           continue;
                        }

                        return Multisets.immutableEntry(element, count);
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   @Beta
   public static Multiset sum(final Multiset multiset1, final Multiset multiset2) {
      Preconditions.checkNotNull(multiset1);
      Preconditions.checkNotNull(multiset2);
      return new AbstractMultiset() {
         public boolean contains(@Nullable Object element) {
            return multiset1.contains(element) || multiset2.contains(element);
         }

         public boolean isEmpty() {
            return multiset1.isEmpty() && multiset2.isEmpty();
         }

         public int size() {
            return multiset1.size() + multiset2.size();
         }

         public int count(Object element) {
            return multiset1.count(element) + multiset2.count(element);
         }

         Set createElementSet() {
            return Sets.union(multiset1.elementSet(), multiset2.elementSet());
         }

         Iterator entryIterator() {
            final Iterator<? extends Multiset.Entry<? extends E>> iterator1 = multiset1.entrySet().iterator();
            final Iterator<? extends Multiset.Entry<? extends E>> iterator2 = multiset2.entrySet().iterator();
            return new AbstractIterator() {
               protected Multiset.Entry computeNext() {
                  if(iterator1.hasNext()) {
                     Multiset.Entry<? extends E> entry1 = (Multiset.Entry)iterator1.next();
                     E element = entry1.getElement();
                     int count = entry1.getCount() + multiset2.count(element);
                     return Multisets.immutableEntry(element, count);
                  } else {
                     while(iterator2.hasNext()) {
                        Multiset.Entry<? extends E> entry2 = (Multiset.Entry)iterator2.next();
                        E element = entry2.getElement();
                        if(!multiset1.contains(element)) {
                           return Multisets.immutableEntry(element, entry2.getCount());
                        }
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return this.elementSet().size();
         }
      };
   }

   @Beta
   public static Multiset difference(final Multiset multiset1, final Multiset multiset2) {
      Preconditions.checkNotNull(multiset1);
      Preconditions.checkNotNull(multiset2);
      return new AbstractMultiset() {
         public int count(@Nullable Object element) {
            int count1 = multiset1.count(element);
            return count1 == 0?0:Math.max(0, count1 - multiset2.count(element));
         }

         Iterator entryIterator() {
            final Iterator<Multiset.Entry<E>> iterator1 = multiset1.entrySet().iterator();
            return new AbstractIterator() {
               protected Multiset.Entry computeNext() {
                  while(true) {
                     if(iterator1.hasNext()) {
                        Multiset.Entry<E> entry1 = (Multiset.Entry)iterator1.next();
                        E element = entry1.getElement();
                        int count = entry1.getCount() - multiset2.count(element);
                        if(count <= 0) {
                           continue;
                        }

                        return Multisets.immutableEntry(element, count);
                     }

                     return (Multiset.Entry)this.endOfData();
                  }
               }
            };
         }

         int distinctElements() {
            return Iterators.size(this.entryIterator());
         }
      };
   }

   public static boolean containsOccurrences(Multiset superMultiset, Multiset subMultiset) {
      Preconditions.checkNotNull(superMultiset);
      Preconditions.checkNotNull(subMultiset);

      for(Multiset.Entry<?> entry : subMultiset.entrySet()) {
         int superCount = superMultiset.count(entry.getElement());
         if(superCount < entry.getCount()) {
            return false;
         }
      }

      return true;
   }

   public static boolean retainOccurrences(Multiset multisetToModify, Multiset multisetToRetain) {
      return retainOccurrencesImpl(multisetToModify, multisetToRetain);
   }

   private static boolean retainOccurrencesImpl(Multiset multisetToModify, Multiset occurrencesToRetain) {
      Preconditions.checkNotNull(multisetToModify);
      Preconditions.checkNotNull(occurrencesToRetain);
      Iterator<Multiset.Entry<E>> entryIterator = multisetToModify.entrySet().iterator();
      boolean changed = false;

      while(entryIterator.hasNext()) {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         int retainCount = occurrencesToRetain.count(entry.getElement());
         if(retainCount == 0) {
            entryIterator.remove();
            changed = true;
         } else if(retainCount < entry.getCount()) {
            multisetToModify.setCount(entry.getElement(), retainCount);
            changed = true;
         }
      }

      return changed;
   }

   public static boolean removeOccurrences(Multiset multisetToModify, Multiset occurrencesToRemove) {
      return removeOccurrencesImpl(multisetToModify, occurrencesToRemove);
   }

   private static boolean removeOccurrencesImpl(Multiset multisetToModify, Multiset occurrencesToRemove) {
      Preconditions.checkNotNull(multisetToModify);
      Preconditions.checkNotNull(occurrencesToRemove);
      boolean changed = false;
      Iterator<Multiset.Entry<E>> entryIterator = multisetToModify.entrySet().iterator();

      while(entryIterator.hasNext()) {
         Multiset.Entry<E> entry = (Multiset.Entry)entryIterator.next();
         int removeCount = occurrencesToRemove.count(entry.getElement());
         if(removeCount >= entry.getCount()) {
            entryIterator.remove();
            changed = true;
         } else if(removeCount > 0) {
            multisetToModify.remove(entry.getElement(), removeCount);
            changed = true;
         }
      }

      return changed;
   }

   static boolean equalsImpl(Multiset multiset, @Nullable Object object) {
      if(object == multiset) {
         return true;
      } else if(object instanceof Multiset) {
         Multiset<?> that = (Multiset)object;
         if(multiset.size() == that.size() && multiset.entrySet().size() == that.entrySet().size()) {
            for(Multiset.Entry<?> entry : that.entrySet()) {
               if(multiset.count(entry.getElement()) != entry.getCount()) {
                  return false;
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static boolean addAllImpl(Multiset self, Collection elements) {
      if(elements.isEmpty()) {
         return false;
      } else {
         if(elements instanceof Multiset) {
            Multiset<? extends E> that = cast(elements);

            for(Multiset.Entry<? extends E> entry : that.entrySet()) {
               self.add(entry.getElement(), entry.getCount());
            }
         } else {
            Iterators.addAll(self, elements.iterator());
         }

         return true;
      }
   }

   static boolean removeAllImpl(Multiset self, Collection elementsToRemove) {
      Collection<?> collection = (Collection)(elementsToRemove instanceof Multiset?((Multiset)elementsToRemove).elementSet():elementsToRemove);
      return self.elementSet().removeAll(collection);
   }

   static boolean retainAllImpl(Multiset self, Collection elementsToRetain) {
      Preconditions.checkNotNull(elementsToRetain);
      Collection<?> collection = (Collection)(elementsToRetain instanceof Multiset?((Multiset)elementsToRetain).elementSet():elementsToRetain);
      return self.elementSet().retainAll(collection);
   }

   static int setCountImpl(Multiset self, Object element, int count) {
      CollectPreconditions.checkNonnegative(count, "count");
      int oldCount = self.count(element);
      int delta = count - oldCount;
      if(delta > 0) {
         self.add(element, delta);
      } else if(delta < 0) {
         self.remove(element, -delta);
      }

      return oldCount;
   }

   static boolean setCountImpl(Multiset self, Object element, int oldCount, int newCount) {
      CollectPreconditions.checkNonnegative(oldCount, "oldCount");
      CollectPreconditions.checkNonnegative(newCount, "newCount");
      if(self.count(element) == oldCount) {
         self.setCount(element, newCount);
         return true;
      } else {
         return false;
      }
   }

   static Iterator iteratorImpl(Multiset multiset) {
      return new Multisets.MultisetIteratorImpl(multiset, multiset.entrySet().iterator());
   }

   static int sizeImpl(Multiset multiset) {
      long size = 0L;

      for(Multiset.Entry<?> entry : multiset.entrySet()) {
         size += (long)entry.getCount();
      }

      return Ints.saturatedCast(size);
   }

   static Multiset cast(Iterable iterable) {
      return (Multiset)iterable;
   }

   @Beta
   public static ImmutableMultiset copyHighestCountFirst(Multiset multiset) {
      List<Multiset.Entry<E>> sortedEntries = DECREASING_COUNT_ORDERING.immutableSortedCopy(multiset.entrySet());
      return ImmutableMultiset.copyFromEntries(sortedEntries);
   }

   abstract static class AbstractEntry implements Multiset.Entry {
      public boolean equals(@Nullable Object object) {
         if(!(object instanceof Multiset.Entry)) {
            return false;
         } else {
            Multiset.Entry<?> that = (Multiset.Entry)object;
            return this.getCount() == that.getCount() && Objects.equal(this.getElement(), that.getElement());
         }
      }

      public int hashCode() {
         E e = this.getElement();
         return (e == null?0:e.hashCode()) ^ this.getCount();
      }

      public String toString() {
         String text = String.valueOf(this.getElement());
         int n = this.getCount();
         return n == 1?text:text + " x " + n;
      }
   }

   abstract static class ElementSet extends Sets.ImprovedAbstractSet {
      abstract Multiset multiset();

      public void clear() {
         this.multiset().clear();
      }

      public boolean contains(Object o) {
         return this.multiset().contains(o);
      }

      public boolean containsAll(Collection c) {
         return this.multiset().containsAll(c);
      }

      public boolean isEmpty() {
         return this.multiset().isEmpty();
      }

      public Iterator iterator() {
         return new TransformedIterator(this.multiset().entrySet().iterator()) {
            Object transform(Multiset.Entry entry) {
               return entry.getElement();
            }
         };
      }

      public boolean remove(Object o) {
         int count = this.multiset().count(o);
         if(count > 0) {
            this.multiset().remove(o, count);
            return true;
         } else {
            return false;
         }
      }

      public int size() {
         return this.multiset().entrySet().size();
      }
   }

   abstract static class EntrySet extends Sets.ImprovedAbstractSet {
      abstract Multiset multiset();

      public boolean contains(@Nullable Object o) {
         if(o instanceof Multiset.Entry) {
            Multiset.Entry<?> entry = (Multiset.Entry)o;
            if(entry.getCount() <= 0) {
               return false;
            } else {
               int count = this.multiset().count(entry.getElement());
               return count == entry.getCount();
            }
         } else {
            return false;
         }
      }

      public boolean remove(Object object) {
         if(object instanceof Multiset.Entry) {
            Multiset.Entry<?> entry = (Multiset.Entry)object;
            Object element = entry.getElement();
            int entryCount = entry.getCount();
            if(entryCount != 0) {
               Multiset<Object> multiset = this.multiset();
               return multiset.setCount(element, entryCount, 0);
            }
         }

         return false;
      }

      public void clear() {
         this.multiset().clear();
      }
   }

   private static final class FilteredMultiset extends AbstractMultiset {
      final Multiset unfiltered;
      final Predicate predicate;

      FilteredMultiset(Multiset unfiltered, Predicate predicate) {
         this.unfiltered = (Multiset)Preconditions.checkNotNull(unfiltered);
         this.predicate = (Predicate)Preconditions.checkNotNull(predicate);
      }

      public UnmodifiableIterator iterator() {
         return Iterators.filter(this.unfiltered.iterator(), this.predicate);
      }

      Set createElementSet() {
         return Sets.filter(this.unfiltered.elementSet(), this.predicate);
      }

      Set createEntrySet() {
         return Sets.filter(this.unfiltered.entrySet(), new Predicate() {
            public boolean apply(Multiset.Entry entry) {
               return FilteredMultiset.this.predicate.apply(entry.getElement());
            }
         });
      }

      Iterator entryIterator() {
         throw new AssertionError("should never be called");
      }

      int distinctElements() {
         return this.elementSet().size();
      }

      public int count(@Nullable Object element) {
         int count = this.unfiltered.count(element);
         return count > 0?(this.predicate.apply(element)?count:0):0;
      }

      public int add(@Nullable Object element, int occurrences) {
         Preconditions.checkArgument(this.predicate.apply(element), "Element %s does not match predicate %s", new Object[]{element, this.predicate});
         return this.unfiltered.add(element, occurrences);
      }

      public int remove(@Nullable Object element, int occurrences) {
         CollectPreconditions.checkNonnegative(occurrences, "occurrences");
         return occurrences == 0?this.count(element):(this.contains(element)?this.unfiltered.remove(element, occurrences):0);
      }

      public void clear() {
         this.elementSet().clear();
      }
   }

   static final class ImmutableEntry extends Multisets.AbstractEntry implements Serializable {
      @Nullable
      final Object element;
      final int count;
      private static final long serialVersionUID = 0L;

      ImmutableEntry(@Nullable Object element, int count) {
         this.element = element;
         this.count = count;
         CollectPreconditions.checkNonnegative(count, "count");
      }

      @Nullable
      public Object getElement() {
         return this.element;
      }

      public int getCount() {
         return this.count;
      }
   }

   static final class MultisetIteratorImpl implements Iterator {
      private final Multiset multiset;
      private final Iterator entryIterator;
      private Multiset.Entry currentEntry;
      private int laterCount;
      private int totalCount;
      private boolean canRemove;

      MultisetIteratorImpl(Multiset multiset, Iterator entryIterator) {
         this.multiset = multiset;
         this.entryIterator = entryIterator;
      }

      public boolean hasNext() {
         return this.laterCount > 0 || this.entryIterator.hasNext();
      }

      public Object next() {
         if(!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            if(this.laterCount == 0) {
               this.currentEntry = (Multiset.Entry)this.entryIterator.next();
               this.totalCount = this.laterCount = this.currentEntry.getCount();
            }

            --this.laterCount;
            this.canRemove = true;
            return this.currentEntry.getElement();
         }
      }

      public void remove() {
         CollectPreconditions.checkRemove(this.canRemove);
         if(this.totalCount == 1) {
            this.entryIterator.remove();
         } else {
            this.multiset.remove(this.currentEntry.getElement());
         }

         --this.totalCount;
         this.canRemove = false;
      }
   }

   static class UnmodifiableMultiset extends ForwardingMultiset implements Serializable {
      final Multiset delegate;
      transient Set elementSet;
      transient Set entrySet;
      private static final long serialVersionUID = 0L;

      UnmodifiableMultiset(Multiset delegate) {
         this.delegate = delegate;
      }

      protected Multiset delegate() {
         return this.delegate;
      }

      Set createElementSet() {
         return Collections.unmodifiableSet(this.delegate.elementSet());
      }

      public Set elementSet() {
         Set<E> es = this.elementSet;
         return es == null?(this.elementSet = this.createElementSet()):es;
      }

      public Set entrySet() {
         Set<Multiset.Entry<E>> es = this.entrySet;
         return es == null?(this.entrySet = Collections.unmodifiableSet(this.delegate.entrySet())):es;
      }

      public Iterator iterator() {
         return Iterators.unmodifiableIterator(this.delegate.iterator());
      }

      public boolean add(Object element) {
         throw new UnsupportedOperationException();
      }

      public int add(Object element, int occurences) {
         throw new UnsupportedOperationException();
      }

      public boolean addAll(Collection elementsToAdd) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object element) {
         throw new UnsupportedOperationException();
      }

      public int remove(Object element, int occurrences) {
         throw new UnsupportedOperationException();
      }

      public boolean removeAll(Collection elementsToRemove) {
         throw new UnsupportedOperationException();
      }

      public boolean retainAll(Collection elementsToRetain) {
         throw new UnsupportedOperationException();
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public int setCount(Object element, int count) {
         throw new UnsupportedOperationException();
      }

      public boolean setCount(Object element, int oldCount, int newCount) {
         throw new UnsupportedOperationException();
      }
   }
}
