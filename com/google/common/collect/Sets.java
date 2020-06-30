package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIndexedListIterator;
import com.google.common.collect.CartesianList;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingNavigableSet;
import com.google.common.collect.ForwardingSortedSet;
import com.google.common.collect.ImmutableEnumSet;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Platform;
import com.google.common.collect.Synchronized;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Sets {
   @GwtCompatible(
      serializable = true
   )
   public static ImmutableSet immutableEnumSet(Enum anElement, Enum... otherElements) {
      return ImmutableEnumSet.asImmutable(EnumSet.of(anElement, otherElements));
   }

   @GwtCompatible(
      serializable = true
   )
   public static ImmutableSet immutableEnumSet(Iterable elements) {
      if(elements instanceof ImmutableEnumSet) {
         return (ImmutableEnumSet)elements;
      } else if(elements instanceof Collection) {
         Collection<E> collection = (Collection)elements;
         return collection.isEmpty()?ImmutableSet.of():ImmutableEnumSet.asImmutable(EnumSet.copyOf(collection));
      } else {
         Iterator<E> itr = elements.iterator();
         if(itr.hasNext()) {
            EnumSet<E> enumSet = EnumSet.of((Enum)itr.next());
            Iterators.addAll(enumSet, itr);
            return ImmutableEnumSet.asImmutable(enumSet);
         } else {
            return ImmutableSet.of();
         }
      }
   }

   public static EnumSet newEnumSet(Iterable iterable, Class elementType) {
      EnumSet<E> set = EnumSet.noneOf(elementType);
      Iterables.addAll(set, iterable);
      return set;
   }

   public static HashSet newHashSet() {
      return new HashSet();
   }

   public static HashSet newHashSet(Object... elements) {
      HashSet<E> set = newHashSetWithExpectedSize(elements.length);
      Collections.addAll(set, elements);
      return set;
   }

   public static HashSet newHashSetWithExpectedSize(int expectedSize) {
      return new HashSet(Maps.capacity(expectedSize));
   }

   public static HashSet newHashSet(Iterable elements) {
      return elements instanceof Collection?new HashSet(Collections2.cast(elements)):newHashSet(elements.iterator());
   }

   public static HashSet newHashSet(Iterator elements) {
      HashSet<E> set = newHashSet();
      Iterators.addAll(set, elements);
      return set;
   }

   public static Set newConcurrentHashSet() {
      return newSetFromMap(new ConcurrentHashMap());
   }

   public static Set newConcurrentHashSet(Iterable elements) {
      Set<E> set = newConcurrentHashSet();
      Iterables.addAll(set, elements);
      return set;
   }

   public static LinkedHashSet newLinkedHashSet() {
      return new LinkedHashSet();
   }

   public static LinkedHashSet newLinkedHashSetWithExpectedSize(int expectedSize) {
      return new LinkedHashSet(Maps.capacity(expectedSize));
   }

   public static LinkedHashSet newLinkedHashSet(Iterable elements) {
      if(elements instanceof Collection) {
         return new LinkedHashSet(Collections2.cast(elements));
      } else {
         LinkedHashSet<E> set = newLinkedHashSet();
         Iterables.addAll(set, elements);
         return set;
      }
   }

   public static TreeSet newTreeSet() {
      return new TreeSet();
   }

   public static TreeSet newTreeSet(Iterable elements) {
      TreeSet<E> set = newTreeSet();
      Iterables.addAll(set, elements);
      return set;
   }

   public static TreeSet newTreeSet(Comparator comparator) {
      return new TreeSet((Comparator)Preconditions.checkNotNull(comparator));
   }

   public static Set newIdentityHashSet() {
      return newSetFromMap(Maps.newIdentityHashMap());
   }

   @GwtIncompatible("CopyOnWriteArraySet")
   public static CopyOnWriteArraySet newCopyOnWriteArraySet() {
      return new CopyOnWriteArraySet();
   }

   @GwtIncompatible("CopyOnWriteArraySet")
   public static CopyOnWriteArraySet newCopyOnWriteArraySet(Iterable elements) {
      Collection<? extends E> elementsCollection = (Collection)(elements instanceof Collection?Collections2.cast(elements):Lists.newArrayList(elements));
      return new CopyOnWriteArraySet(elementsCollection);
   }

   public static EnumSet complementOf(Collection collection) {
      if(collection instanceof EnumSet) {
         return EnumSet.complementOf((EnumSet)collection);
      } else {
         Preconditions.checkArgument(!collection.isEmpty(), "collection is empty; use the other version of this method");
         Class<E> type = ((Enum)collection.iterator().next()).getDeclaringClass();
         return makeComplementByHand(collection, type);
      }
   }

   public static EnumSet complementOf(Collection collection, Class type) {
      Preconditions.checkNotNull(collection);
      return collection instanceof EnumSet?EnumSet.complementOf((EnumSet)collection):makeComplementByHand(collection, type);
   }

   private static EnumSet makeComplementByHand(Collection collection, Class type) {
      EnumSet<E> result = EnumSet.allOf(type);
      result.removeAll(collection);
      return result;
   }

   public static Set newSetFromMap(Map map) {
      return Platform.newSetFromMap(map);
   }

   public static Sets.SetView union(final Set set1, final Set set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      final Set<? extends E> set2minus1 = difference(set2, set1);
      return new Sets.SetView(null) {
         public int size() {
            return set1.size() + set2minus1.size();
         }

         public boolean isEmpty() {
            return set1.isEmpty() && set2.isEmpty();
         }

         public Iterator iterator() {
            return Iterators.unmodifiableIterator(Iterators.concat(set1.iterator(), set2minus1.iterator()));
         }

         public boolean contains(Object object) {
            return set1.contains(object) || set2.contains(object);
         }

         public Set copyInto(Set set) {
            set.addAll(set1);
            set.addAll(set2);
            return set;
         }

         public ImmutableSet immutableCopy() {
            return (new ImmutableSet.Builder()).addAll((Iterable)set1).addAll((Iterable)set2).build();
         }
      };
   }

   public static Sets.SetView intersection(final Set set1, final Set set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      final Predicate<Object> inSet2 = Predicates.in(set2);
      return new Sets.SetView(null) {
         public Iterator iterator() {
            return Iterators.filter(set1.iterator(), inSet2);
         }

         public int size() {
            return Iterators.size(this.iterator());
         }

         public boolean isEmpty() {
            return !this.iterator().hasNext();
         }

         public boolean contains(Object object) {
            return set1.contains(object) && set2.contains(object);
         }

         public boolean containsAll(Collection collection) {
            return set1.containsAll(collection) && set2.containsAll(collection);
         }
      };
   }

   public static Sets.SetView difference(final Set set1, final Set set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      final Predicate<Object> notInSet2 = Predicates.not(Predicates.in(set2));
      return new Sets.SetView(null) {
         public Iterator iterator() {
            return Iterators.filter(set1.iterator(), notInSet2);
         }

         public int size() {
            return Iterators.size(this.iterator());
         }

         public boolean isEmpty() {
            return set2.containsAll(set1);
         }

         public boolean contains(Object element) {
            return set1.contains(element) && !set2.contains(element);
         }
      };
   }

   public static Sets.SetView symmetricDifference(Set set1, Set set2) {
      Preconditions.checkNotNull(set1, "set1");
      Preconditions.checkNotNull(set2, "set2");
      return difference(union(set1, set2), intersection(set1, set2));
   }

   public static Set filter(Set unfiltered, Predicate predicate) {
      if(unfiltered instanceof SortedSet) {
         return filter((SortedSet)unfiltered, predicate);
      } else if(unfiltered instanceof Sets.FilteredSet) {
         Sets.FilteredSet<E> filtered = (Sets.FilteredSet)unfiltered;
         Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
         return new Sets.FilteredSet((Set)filtered.unfiltered, combinedPredicate);
      } else {
         return new Sets.FilteredSet((Set)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
      }
   }

   public static SortedSet filter(SortedSet unfiltered, Predicate predicate) {
      return Platform.setsFilterSortedSet(unfiltered, predicate);
   }

   static SortedSet filterSortedIgnoreNavigable(SortedSet unfiltered, Predicate predicate) {
      if(unfiltered instanceof Sets.FilteredSet) {
         Sets.FilteredSet<E> filtered = (Sets.FilteredSet)unfiltered;
         Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
         return new Sets.FilteredSortedSet((SortedSet)filtered.unfiltered, combinedPredicate);
      } else {
         return new Sets.FilteredSortedSet((SortedSet)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
      }
   }

   @GwtIncompatible("NavigableSet")
   public static NavigableSet filter(NavigableSet unfiltered, Predicate predicate) {
      if(unfiltered instanceof Sets.FilteredSet) {
         Sets.FilteredSet<E> filtered = (Sets.FilteredSet)unfiltered;
         Predicate<E> combinedPredicate = Predicates.and(filtered.predicate, predicate);
         return new Sets.FilteredNavigableSet((NavigableSet)filtered.unfiltered, combinedPredicate);
      } else {
         return new Sets.FilteredNavigableSet((NavigableSet)Preconditions.checkNotNull(unfiltered), (Predicate)Preconditions.checkNotNull(predicate));
      }
   }

   public static Set cartesianProduct(List sets) {
      return Sets.CartesianSet.create(sets);
   }

   public static Set cartesianProduct(Set... sets) {
      return cartesianProduct(Arrays.asList(sets));
   }

   @GwtCompatible(
      serializable = false
   )
   public static Set powerSet(Set set) {
      return new Sets.PowerSet(set);
   }

   static int hashCodeImpl(Set s) {
      int hashCode = 0;

      for(Object o : s) {
         hashCode = hashCode + (o != null?o.hashCode():0);
         hashCode = ~(~hashCode);
      }

      return hashCode;
   }

   static boolean equalsImpl(Set s, @Nullable Object object) {
      if(s == object) {
         return true;
      } else if(object instanceof Set) {
         Set<?> o = (Set)object;

         try {
            return s.size() == o.size() && s.containsAll(o);
         } catch (NullPointerException var4) {
            return false;
         } catch (ClassCastException var5) {
            return false;
         }
      } else {
         return false;
      }
   }

   @GwtIncompatible("NavigableSet")
   public static NavigableSet unmodifiableNavigableSet(NavigableSet set) {
      return (NavigableSet)(!(set instanceof ImmutableSortedSet) && !(set instanceof Sets.UnmodifiableNavigableSet)?new Sets.UnmodifiableNavigableSet(set):set);
   }

   @GwtIncompatible("NavigableSet")
   public static NavigableSet synchronizedNavigableSet(NavigableSet navigableSet) {
      return Synchronized.navigableSet(navigableSet);
   }

   static boolean removeAllImpl(Set set, Iterator iterator) {
      boolean changed;
      for(changed = false; iterator.hasNext(); changed |= set.remove(iterator.next())) {
         ;
      }

      return changed;
   }

   static boolean removeAllImpl(Set set, Collection collection) {
      Preconditions.checkNotNull(collection);
      if(collection instanceof Multiset) {
         collection = ((Multiset)collection).elementSet();
      }

      return collection instanceof Set && ((Collection)collection).size() > set.size()?Iterators.removeAll(set.iterator(), (Collection)collection):removeAllImpl(set, ((Collection)collection).iterator());
   }

   private static final class CartesianSet extends ForwardingCollection implements Set {
      private final transient ImmutableList axes;
      private final transient CartesianList delegate;

      static Set create(List sets) {
         ImmutableList.Builder<ImmutableSet<E>> axesBuilder = new ImmutableList.Builder(sets.size());

         for(Set<? extends E> set : sets) {
            ImmutableSet<E> copy = ImmutableSet.copyOf((Collection)set);
            if(copy.isEmpty()) {
               return ImmutableSet.of();
            }

            axesBuilder.add((Object)copy);
         }

         final ImmutableList<ImmutableSet<E>> axes = axesBuilder.build();
         ImmutableList<List<E>> listAxes = new ImmutableList() {
            public int size() {
               return axes.size();
            }

            public List get(int index) {
               return ((ImmutableSet)axes.get(index)).asList();
            }

            boolean isPartialView() {
               return true;
            }
         };
         return new Sets.CartesianSet(axes, new CartesianList(listAxes));
      }

      private CartesianSet(ImmutableList axes, CartesianList delegate) {
         this.axes = axes;
         this.delegate = delegate;
      }

      protected Collection delegate() {
         return this.delegate;
      }

      public boolean equals(@Nullable Object object) {
         if(object instanceof Sets.CartesianSet) {
            Sets.CartesianSet<?> that = (Sets.CartesianSet)object;
            return this.axes.equals(that.axes);
         } else {
            return super.equals(object);
         }
      }

      public int hashCode() {
         int adjust = this.size() - 1;

         for(int i = 0; i < this.axes.size(); ++i) {
            adjust = adjust * 31;
            adjust = ~(~adjust);
         }

         int hash = 1;

         for(Set<E> axis : this.axes) {
            hash = 31 * hash + this.size() / axis.size() * axis.hashCode();
            hash = ~(~hash);
         }

         hash = hash + adjust;
         return ~(~hash);
      }
   }

   @GwtIncompatible("NavigableSet")
   static class DescendingSet extends ForwardingNavigableSet {
      private final NavigableSet forward;

      DescendingSet(NavigableSet forward) {
         this.forward = forward;
      }

      protected NavigableSet delegate() {
         return this.forward;
      }

      public Object lower(Object e) {
         return this.forward.higher(e);
      }

      public Object floor(Object e) {
         return this.forward.ceiling(e);
      }

      public Object ceiling(Object e) {
         return this.forward.floor(e);
      }

      public Object higher(Object e) {
         return this.forward.lower(e);
      }

      public Object pollFirst() {
         return this.forward.pollLast();
      }

      public Object pollLast() {
         return this.forward.pollFirst();
      }

      public NavigableSet descendingSet() {
         return this.forward;
      }

      public Iterator descendingIterator() {
         return this.forward.iterator();
      }

      public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
         return this.forward.subSet(toElement, toInclusive, fromElement, fromInclusive).descendingSet();
      }

      public NavigableSet headSet(Object toElement, boolean inclusive) {
         return this.forward.tailSet(toElement, inclusive).descendingSet();
      }

      public NavigableSet tailSet(Object fromElement, boolean inclusive) {
         return this.forward.headSet(fromElement, inclusive).descendingSet();
      }

      public Comparator comparator() {
         Comparator<? super E> forwardComparator = this.forward.comparator();
         return forwardComparator == null?Ordering.natural().reverse():reverse(forwardComparator);
      }

      private static Ordering reverse(Comparator forward) {
         return Ordering.from(forward).reverse();
      }

      public Object first() {
         return this.forward.last();
      }

      public SortedSet headSet(Object toElement) {
         return this.standardHeadSet(toElement);
      }

      public Object last() {
         return this.forward.first();
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return this.standardSubSet(fromElement, toElement);
      }

      public SortedSet tailSet(Object fromElement) {
         return this.standardTailSet(fromElement);
      }

      public Iterator iterator() {
         return this.forward.descendingIterator();
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public String toString() {
         return this.standardToString();
      }
   }

   @GwtIncompatible("NavigableSet")
   private static class FilteredNavigableSet extends Sets.FilteredSortedSet implements NavigableSet {
      FilteredNavigableSet(NavigableSet unfiltered, Predicate predicate) {
         super(unfiltered, predicate);
      }

      NavigableSet unfiltered() {
         return (NavigableSet)this.unfiltered;
      }

      @Nullable
      public Object lower(Object e) {
         return Iterators.getNext(this.headSet(e, false).descendingIterator(), (Object)null);
      }

      @Nullable
      public Object floor(Object e) {
         return Iterators.getNext(this.headSet(e, true).descendingIterator(), (Object)null);
      }

      public Object ceiling(Object e) {
         return Iterables.getFirst(this.tailSet(e, true), (Object)null);
      }

      public Object higher(Object e) {
         return Iterables.getFirst(this.tailSet(e, false), (Object)null);
      }

      public Object pollFirst() {
         return Iterables.removeFirstMatching(this.unfiltered(), this.predicate);
      }

      public Object pollLast() {
         return Iterables.removeFirstMatching(this.unfiltered().descendingSet(), this.predicate);
      }

      public NavigableSet descendingSet() {
         return Sets.filter(this.unfiltered().descendingSet(), this.predicate);
      }

      public Iterator descendingIterator() {
         return Iterators.filter(this.unfiltered().descendingIterator(), this.predicate);
      }

      public Object last() {
         return this.descendingIterator().next();
      }

      public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
         return Sets.filter(this.unfiltered().subSet(fromElement, fromInclusive, toElement, toInclusive), this.predicate);
      }

      public NavigableSet headSet(Object toElement, boolean inclusive) {
         return Sets.filter(this.unfiltered().headSet(toElement, inclusive), this.predicate);
      }

      public NavigableSet tailSet(Object fromElement, boolean inclusive) {
         return Sets.filter(this.unfiltered().tailSet(fromElement, inclusive), this.predicate);
      }
   }

   private static class FilteredSet extends Collections2.FilteredCollection implements Set {
      FilteredSet(Set unfiltered, Predicate predicate) {
         super(unfiltered, predicate);
      }

      public boolean equals(@Nullable Object object) {
         return Sets.equalsImpl(this, object);
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }
   }

   private static class FilteredSortedSet extends Sets.FilteredSet implements SortedSet {
      FilteredSortedSet(SortedSet unfiltered, Predicate predicate) {
         super(unfiltered, predicate);
      }

      public Comparator comparator() {
         return ((SortedSet)this.unfiltered).comparator();
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return new Sets.FilteredSortedSet(((SortedSet)this.unfiltered).subSet(fromElement, toElement), this.predicate);
      }

      public SortedSet headSet(Object toElement) {
         return new Sets.FilteredSortedSet(((SortedSet)this.unfiltered).headSet(toElement), this.predicate);
      }

      public SortedSet tailSet(Object fromElement) {
         return new Sets.FilteredSortedSet(((SortedSet)this.unfiltered).tailSet(fromElement), this.predicate);
      }

      public Object first() {
         return this.iterator().next();
      }

      public Object last() {
         SortedSet<E> sortedUnfiltered = (SortedSet)this.unfiltered;

         while(true) {
            E element = sortedUnfiltered.last();
            if(this.predicate.apply(element)) {
               return element;
            }

            sortedUnfiltered = sortedUnfiltered.headSet(element);
         }
      }
   }

   abstract static class ImprovedAbstractSet extends AbstractSet {
      public boolean removeAll(Collection c) {
         return Sets.removeAllImpl(this, (Collection)c);
      }

      public boolean retainAll(Collection c) {
         return super.retainAll((Collection)Preconditions.checkNotNull(c));
      }
   }

   private static final class PowerSet extends AbstractSet {
      final ImmutableMap inputSet;

      PowerSet(Set input) {
         ImmutableMap.Builder<E, Integer> builder = ImmutableMap.builder();
         int i = 0;

         for(E e : (Set)Preconditions.checkNotNull(input)) {
            builder.put(e, Integer.valueOf(i++));
         }

         this.inputSet = builder.build();
         Preconditions.checkArgument(this.inputSet.size() <= 30, "Too many elements to create power set: %s > 30", new Object[]{Integer.valueOf(this.inputSet.size())});
      }

      public int size() {
         return 1 << this.inputSet.size();
      }

      public boolean isEmpty() {
         return false;
      }

      public Iterator iterator() {
         return new AbstractIndexedListIterator(this.size()) {
            protected Set get(int setBits) {
               return new Sets.SubSet(PowerSet.this.inputSet, setBits);
            }
         };
      }

      public boolean contains(@Nullable Object obj) {
         if(obj instanceof Set) {
            Set<?> set = (Set)obj;
            return this.inputSet.keySet().containsAll(set);
         } else {
            return false;
         }
      }

      public boolean equals(@Nullable Object obj) {
         if(obj instanceof Sets.PowerSet) {
            Sets.PowerSet<?> that = (Sets.PowerSet)obj;
            return this.inputSet.equals(that.inputSet);
         } else {
            return super.equals(obj);
         }
      }

      public int hashCode() {
         return this.inputSet.keySet().hashCode() << this.inputSet.size() - 1;
      }

      public String toString() {
         return "powerSet(" + this.inputSet + ")";
      }
   }

   public abstract static class SetView extends AbstractSet {
      private SetView() {
      }

      public ImmutableSet immutableCopy() {
         return ImmutableSet.copyOf((Collection)this);
      }

      public Set copyInto(Set set) {
         set.addAll(this);
         return set;
      }
   }

   private static final class SubSet extends AbstractSet {
      private final ImmutableMap inputSet;
      private final int mask;

      SubSet(ImmutableMap inputSet, int mask) {
         this.inputSet = inputSet;
         this.mask = mask;
      }

      public Iterator iterator() {
         return new UnmodifiableIterator() {
            final ImmutableList elements;
            int remainingSetBits;

            {
               this.elements = SubSet.this.inputSet.keySet().asList();
               this.remainingSetBits = SubSet.this.mask;
            }

            public boolean hasNext() {
               return this.remainingSetBits != 0;
            }

            public Object next() {
               int index = Integer.numberOfTrailingZeros(this.remainingSetBits);
               if(index == 32) {
                  throw new NoSuchElementException();
               } else {
                  this.remainingSetBits &= ~(1 << index);
                  return this.elements.get(index);
               }
            }
         };
      }

      public int size() {
         return Integer.bitCount(this.mask);
      }

      public boolean contains(@Nullable Object o) {
         Integer index = (Integer)this.inputSet.get(o);
         return index != null && (this.mask & 1 << index.intValue()) != 0;
      }
   }

   @GwtIncompatible("NavigableSet")
   static final class UnmodifiableNavigableSet extends ForwardingSortedSet implements NavigableSet, Serializable {
      private final NavigableSet delegate;
      private transient Sets.UnmodifiableNavigableSet descendingSet;
      private static final long serialVersionUID = 0L;

      UnmodifiableNavigableSet(NavigableSet delegate) {
         this.delegate = (NavigableSet)Preconditions.checkNotNull(delegate);
      }

      protected SortedSet delegate() {
         return Collections.unmodifiableSortedSet(this.delegate);
      }

      public Object lower(Object e) {
         return this.delegate.lower(e);
      }

      public Object floor(Object e) {
         return this.delegate.floor(e);
      }

      public Object ceiling(Object e) {
         return this.delegate.ceiling(e);
      }

      public Object higher(Object e) {
         return this.delegate.higher(e);
      }

      public Object pollFirst() {
         throw new UnsupportedOperationException();
      }

      public Object pollLast() {
         throw new UnsupportedOperationException();
      }

      public NavigableSet descendingSet() {
         Sets.UnmodifiableNavigableSet<E> result = this.descendingSet;
         if(result == null) {
            result = this.descendingSet = new Sets.UnmodifiableNavigableSet(this.delegate.descendingSet());
            result.descendingSet = this;
         }

         return result;
      }

      public Iterator descendingIterator() {
         return Iterators.unmodifiableIterator(this.delegate.descendingIterator());
      }

      public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
         return Sets.unmodifiableNavigableSet(this.delegate.subSet(fromElement, fromInclusive, toElement, toInclusive));
      }

      public NavigableSet headSet(Object toElement, boolean inclusive) {
         return Sets.unmodifiableNavigableSet(this.delegate.headSet(toElement, inclusive));
      }

      public NavigableSet tailSet(Object fromElement, boolean inclusive) {
         return Sets.unmodifiableNavigableSet(this.delegate.tailSet(fromElement, inclusive));
      }
   }
}
