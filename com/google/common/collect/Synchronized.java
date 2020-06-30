package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.RandomAccess;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
final class Synchronized {
   private static Collection collection(Collection collection, @Nullable Object mutex) {
      return new Synchronized.SynchronizedCollection(collection, mutex);
   }

   @VisibleForTesting
   static Set set(Set set, @Nullable Object mutex) {
      return new Synchronized.SynchronizedSet(set, mutex);
   }

   private static SortedSet sortedSet(SortedSet set, @Nullable Object mutex) {
      return new Synchronized.SynchronizedSortedSet(set, mutex);
   }

   private static List list(List list, @Nullable Object mutex) {
      return (List)(list instanceof RandomAccess?new Synchronized.SynchronizedRandomAccessList(list, mutex):new Synchronized.SynchronizedList(list, mutex));
   }

   static Multiset multiset(Multiset multiset, @Nullable Object mutex) {
      return (Multiset)(!(multiset instanceof Synchronized.SynchronizedMultiset) && !(multiset instanceof ImmutableMultiset)?new Synchronized.SynchronizedMultiset(multiset, mutex):multiset);
   }

   static Multimap multimap(Multimap multimap, @Nullable Object mutex) {
      return (Multimap)(!(multimap instanceof Synchronized.SynchronizedMultimap) && !(multimap instanceof ImmutableMultimap)?new Synchronized.SynchronizedMultimap(multimap, mutex):multimap);
   }

   static ListMultimap listMultimap(ListMultimap multimap, @Nullable Object mutex) {
      return (ListMultimap)(!(multimap instanceof Synchronized.SynchronizedListMultimap) && !(multimap instanceof ImmutableListMultimap)?new Synchronized.SynchronizedListMultimap(multimap, mutex):multimap);
   }

   static SetMultimap setMultimap(SetMultimap multimap, @Nullable Object mutex) {
      return (SetMultimap)(!(multimap instanceof Synchronized.SynchronizedSetMultimap) && !(multimap instanceof ImmutableSetMultimap)?new Synchronized.SynchronizedSetMultimap(multimap, mutex):multimap);
   }

   static SortedSetMultimap sortedSetMultimap(SortedSetMultimap multimap, @Nullable Object mutex) {
      return (SortedSetMultimap)(multimap instanceof Synchronized.SynchronizedSortedSetMultimap?multimap:new Synchronized.SynchronizedSortedSetMultimap(multimap, mutex));
   }

   private static Collection typePreservingCollection(Collection collection, @Nullable Object mutex) {
      return (Collection)(collection instanceof SortedSet?sortedSet((SortedSet)collection, mutex):(collection instanceof Set?set((Set)collection, mutex):(collection instanceof List?list((List)collection, mutex):collection(collection, mutex))));
   }

   private static Set typePreservingSet(Set set, @Nullable Object mutex) {
      return (Set)(set instanceof SortedSet?sortedSet((SortedSet)set, mutex):set(set, mutex));
   }

   @VisibleForTesting
   static Map map(Map map, @Nullable Object mutex) {
      return new Synchronized.SynchronizedMap(map, mutex);
   }

   static SortedMap sortedMap(SortedMap sortedMap, @Nullable Object mutex) {
      return new Synchronized.SynchronizedSortedMap(sortedMap, mutex);
   }

   static BiMap biMap(BiMap bimap, @Nullable Object mutex) {
      return (BiMap)(!(bimap instanceof Synchronized.SynchronizedBiMap) && !(bimap instanceof ImmutableBiMap)?new Synchronized.SynchronizedBiMap(bimap, mutex, (BiMap)null):bimap);
   }

   @GwtIncompatible("NavigableSet")
   static NavigableSet navigableSet(NavigableSet navigableSet, @Nullable Object mutex) {
      return new Synchronized.SynchronizedNavigableSet(navigableSet, mutex);
   }

   @GwtIncompatible("NavigableSet")
   static NavigableSet navigableSet(NavigableSet navigableSet) {
      return navigableSet(navigableSet, (Object)null);
   }

   @GwtIncompatible("NavigableMap")
   static NavigableMap navigableMap(NavigableMap navigableMap) {
      return navigableMap(navigableMap, (Object)null);
   }

   @GwtIncompatible("NavigableMap")
   static NavigableMap navigableMap(NavigableMap navigableMap, @Nullable Object mutex) {
      return new Synchronized.SynchronizedNavigableMap(navigableMap, mutex);
   }

   @GwtIncompatible("works but is needed only for NavigableMap")
   private static Entry nullableSynchronizedEntry(@Nullable Entry entry, @Nullable Object mutex) {
      return entry == null?null:new Synchronized.SynchronizedEntry(entry, mutex);
   }

   static Queue queue(Queue queue, @Nullable Object mutex) {
      return (Queue)(queue instanceof Synchronized.SynchronizedQueue?queue:new Synchronized.SynchronizedQueue(queue, mutex));
   }

   @GwtIncompatible("Deque")
   static Deque deque(Deque deque, @Nullable Object mutex) {
      return new Synchronized.SynchronizedDeque(deque, mutex);
   }

   private static class SynchronizedAsMap extends Synchronized.SynchronizedMap {
      transient Set asMapEntrySet;
      transient Collection asMapValues;
      private static final long serialVersionUID = 0L;

      SynchronizedAsMap(Map delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      public Collection get(Object key) {
         synchronized(this.mutex) {
            Collection<V> collection = (Collection)super.get(key);
            return collection == null?null:Synchronized.typePreservingCollection(collection, this.mutex);
         }
      }

      public Set entrySet() {
         synchronized(this.mutex) {
            if(this.asMapEntrySet == null) {
               this.asMapEntrySet = new Synchronized.SynchronizedAsMapEntries(this.delegate().entrySet(), this.mutex);
            }

            return this.asMapEntrySet;
         }
      }

      public Collection values() {
         synchronized(this.mutex) {
            if(this.asMapValues == null) {
               this.asMapValues = new Synchronized.SynchronizedAsMapValues(this.delegate().values(), this.mutex);
            }

            return this.asMapValues;
         }
      }

      public boolean containsValue(Object o) {
         return this.values().contains(o);
      }
   }

   private static class SynchronizedAsMapEntries extends Synchronized.SynchronizedSet {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapEntries(Set delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      public Iterator iterator() {
         final Iterator<Entry<K, Collection<V>>> iterator = super.iterator();
         return new ForwardingIterator() {
            protected Iterator delegate() {
               return iterator;
            }

            public Entry next() {
               final Entry<K, Collection<V>> entry = (Entry)super.next();
               return new ForwardingMapEntry() {
                  protected Entry delegate() {
                     return entry;
                  }

                  public Collection getValue() {
                     return Synchronized.typePreservingCollection((Collection)entry.getValue(), SynchronizedAsMapEntries.this.mutex);
                  }
               };
            }
         };
      }

      public Object[] toArray() {
         synchronized(this.mutex) {
            return ObjectArrays.toArrayImpl(this.delegate());
         }
      }

      public Object[] toArray(Object[] array) {
         synchronized(this.mutex) {
            return ObjectArrays.toArrayImpl(this.delegate(), array);
         }
      }

      public boolean contains(Object o) {
         synchronized(this.mutex) {
            return Maps.containsEntryImpl(this.delegate(), o);
         }
      }

      public boolean containsAll(Collection c) {
         synchronized(this.mutex) {
            return Collections2.containsAllImpl(this.delegate(), c);
         }
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return Sets.equalsImpl(this.delegate(), o);
            }
         }
      }

      public boolean remove(Object o) {
         synchronized(this.mutex) {
            return Maps.removeEntryImpl(this.delegate(), o);
         }
      }

      public boolean removeAll(Collection c) {
         synchronized(this.mutex) {
            return Iterators.removeAll(this.delegate().iterator(), c);
         }
      }

      public boolean retainAll(Collection c) {
         synchronized(this.mutex) {
            return Iterators.retainAll(this.delegate().iterator(), c);
         }
      }
   }

   private static class SynchronizedAsMapValues extends Synchronized.SynchronizedCollection {
      private static final long serialVersionUID = 0L;

      SynchronizedAsMapValues(Collection delegate, @Nullable Object mutex) {
         super(delegate, mutex, null);
      }

      public Iterator iterator() {
         final Iterator<Collection<V>> iterator = super.iterator();
         return new ForwardingIterator() {
            protected Iterator delegate() {
               return iterator;
            }

            public Collection next() {
               return Synchronized.typePreservingCollection((Collection)super.next(), SynchronizedAsMapValues.this.mutex);
            }
         };
      }
   }

   @VisibleForTesting
   static class SynchronizedBiMap extends Synchronized.SynchronizedMap implements BiMap, Serializable {
      private transient Set valueSet;
      private transient BiMap inverse;
      private static final long serialVersionUID = 0L;

      private SynchronizedBiMap(BiMap delegate, @Nullable Object mutex, @Nullable BiMap inverse) {
         super(delegate, mutex);
         this.inverse = inverse;
      }

      BiMap delegate() {
         return (BiMap)super.delegate();
      }

      public Set values() {
         synchronized(this.mutex) {
            if(this.valueSet == null) {
               this.valueSet = Synchronized.set(this.delegate().values(), this.mutex);
            }

            return this.valueSet;
         }
      }

      public Object forcePut(Object key, Object value) {
         synchronized(this.mutex) {
            return this.delegate().forcePut(key, value);
         }
      }

      public BiMap inverse() {
         synchronized(this.mutex) {
            if(this.inverse == null) {
               this.inverse = new Synchronized.SynchronizedBiMap(this.delegate().inverse(), this.mutex, this);
            }

            return this.inverse;
         }
      }
   }

   @VisibleForTesting
   static class SynchronizedCollection extends Synchronized.SynchronizedObject implements Collection {
      private static final long serialVersionUID = 0L;

      private SynchronizedCollection(Collection delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      Collection delegate() {
         return (Collection)super.delegate();
      }

      public boolean add(Object e) {
         synchronized(this.mutex) {
            return this.delegate().add(e);
         }
      }

      public boolean addAll(Collection c) {
         synchronized(this.mutex) {
            return this.delegate().addAll(c);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public boolean contains(Object o) {
         synchronized(this.mutex) {
            return this.delegate().contains(o);
         }
      }

      public boolean containsAll(Collection c) {
         synchronized(this.mutex) {
            return this.delegate().containsAll(c);
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public Iterator iterator() {
         return this.delegate().iterator();
      }

      public boolean remove(Object o) {
         synchronized(this.mutex) {
            return this.delegate().remove(o);
         }
      }

      public boolean removeAll(Collection c) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(c);
         }
      }

      public boolean retainAll(Collection c) {
         synchronized(this.mutex) {
            return this.delegate().retainAll(c);
         }
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public Object[] toArray() {
         synchronized(this.mutex) {
            return this.delegate().toArray();
         }
      }

      public Object[] toArray(Object[] a) {
         synchronized(this.mutex) {
            return this.delegate().toArray(a);
         }
      }
   }

   @GwtIncompatible("Deque")
   private static final class SynchronizedDeque extends Synchronized.SynchronizedQueue implements Deque {
      private static final long serialVersionUID = 0L;

      SynchronizedDeque(Deque delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      Deque delegate() {
         return (Deque)super.delegate();
      }

      public void addFirst(Object e) {
         synchronized(this.mutex) {
            this.delegate().addFirst(e);
         }
      }

      public void addLast(Object e) {
         synchronized(this.mutex) {
            this.delegate().addLast(e);
         }
      }

      public boolean offerFirst(Object e) {
         synchronized(this.mutex) {
            return this.delegate().offerFirst(e);
         }
      }

      public boolean offerLast(Object e) {
         synchronized(this.mutex) {
            return this.delegate().offerLast(e);
         }
      }

      public Object removeFirst() {
         synchronized(this.mutex) {
            return this.delegate().removeFirst();
         }
      }

      public Object removeLast() {
         synchronized(this.mutex) {
            return this.delegate().removeLast();
         }
      }

      public Object pollFirst() {
         synchronized(this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      public Object pollLast() {
         synchronized(this.mutex) {
            return this.delegate().pollLast();
         }
      }

      public Object getFirst() {
         synchronized(this.mutex) {
            return this.delegate().getFirst();
         }
      }

      public Object getLast() {
         synchronized(this.mutex) {
            return this.delegate().getLast();
         }
      }

      public Object peekFirst() {
         synchronized(this.mutex) {
            return this.delegate().peekFirst();
         }
      }

      public Object peekLast() {
         synchronized(this.mutex) {
            return this.delegate().peekLast();
         }
      }

      public boolean removeFirstOccurrence(Object o) {
         synchronized(this.mutex) {
            return this.delegate().removeFirstOccurrence(o);
         }
      }

      public boolean removeLastOccurrence(Object o) {
         synchronized(this.mutex) {
            return this.delegate().removeLastOccurrence(o);
         }
      }

      public void push(Object e) {
         synchronized(this.mutex) {
            this.delegate().push(e);
         }
      }

      public Object pop() {
         synchronized(this.mutex) {
            return this.delegate().pop();
         }
      }

      public Iterator descendingIterator() {
         synchronized(this.mutex) {
            return this.delegate().descendingIterator();
         }
      }
   }

   @GwtIncompatible("works but is needed only for NavigableMap")
   private static class SynchronizedEntry extends Synchronized.SynchronizedObject implements Entry {
      private static final long serialVersionUID = 0L;

      SynchronizedEntry(Entry delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      Entry delegate() {
         return (Entry)super.delegate();
      }

      public boolean equals(Object obj) {
         synchronized(this.mutex) {
            return this.delegate().equals(obj);
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }

      public Object getKey() {
         synchronized(this.mutex) {
            return this.delegate().getKey();
         }
      }

      public Object getValue() {
         synchronized(this.mutex) {
            return this.delegate().getValue();
         }
      }

      public Object setValue(Object value) {
         synchronized(this.mutex) {
            return this.delegate().setValue(value);
         }
      }
   }

   private static class SynchronizedList extends Synchronized.SynchronizedCollection implements List {
      private static final long serialVersionUID = 0L;

      SynchronizedList(List delegate, @Nullable Object mutex) {
         super(delegate, mutex, null);
      }

      List delegate() {
         return (List)super.delegate();
      }

      public void add(int index, Object element) {
         synchronized(this.mutex) {
            this.delegate().add(index, element);
         }
      }

      public boolean addAll(int index, Collection c) {
         synchronized(this.mutex) {
            return this.delegate().addAll(index, c);
         }
      }

      public Object get(int index) {
         synchronized(this.mutex) {
            return this.delegate().get(index);
         }
      }

      public int indexOf(Object o) {
         synchronized(this.mutex) {
            return this.delegate().indexOf(o);
         }
      }

      public int lastIndexOf(Object o) {
         synchronized(this.mutex) {
            return this.delegate().lastIndexOf(o);
         }
      }

      public ListIterator listIterator() {
         return this.delegate().listIterator();
      }

      public ListIterator listIterator(int index) {
         return this.delegate().listIterator(index);
      }

      public Object remove(int index) {
         synchronized(this.mutex) {
            return this.delegate().remove(index);
         }
      }

      public Object set(int index, Object element) {
         synchronized(this.mutex) {
            return this.delegate().set(index, element);
         }
      }

      public List subList(int fromIndex, int toIndex) {
         synchronized(this.mutex) {
            return Synchronized.list(this.delegate().subList(fromIndex, toIndex), this.mutex);
         }
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(o);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedListMultimap extends Synchronized.SynchronizedMultimap implements ListMultimap {
      private static final long serialVersionUID = 0L;

      SynchronizedListMultimap(ListMultimap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      ListMultimap delegate() {
         return (ListMultimap)super.delegate();
      }

      public List get(Object key) {
         synchronized(this.mutex) {
            return Synchronized.list(this.delegate().get(key), this.mutex);
         }
      }

      public List removeAll(Object key) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(key);
         }
      }

      public List replaceValues(Object key, Iterable values) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(key, values);
         }
      }
   }

   private static class SynchronizedMap extends Synchronized.SynchronizedObject implements Map {
      transient Set keySet;
      transient Collection values;
      transient Set entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMap(Map delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      Map delegate() {
         return (Map)super.delegate();
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public boolean containsKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().containsKey(key);
         }
      }

      public boolean containsValue(Object value) {
         synchronized(this.mutex) {
            return this.delegate().containsValue(value);
         }
      }

      public Set entrySet() {
         synchronized(this.mutex) {
            if(this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      public Object get(Object key) {
         synchronized(this.mutex) {
            return this.delegate().get(key);
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public Set keySet() {
         synchronized(this.mutex) {
            if(this.keySet == null) {
               this.keySet = Synchronized.set(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      public Object put(Object key, Object value) {
         synchronized(this.mutex) {
            return this.delegate().put(key, value);
         }
      }

      public void putAll(Map map) {
         synchronized(this.mutex) {
            this.delegate().putAll(map);
         }
      }

      public Object remove(Object key) {
         synchronized(this.mutex) {
            return this.delegate().remove(key);
         }
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public Collection values() {
         synchronized(this.mutex) {
            if(this.values == null) {
               this.values = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.values;
         }
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(o);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedMultimap extends Synchronized.SynchronizedObject implements Multimap {
      transient Set keySet;
      transient Collection valuesCollection;
      transient Collection entries;
      transient Map asMap;
      transient Multiset keys;
      private static final long serialVersionUID = 0L;

      Multimap delegate() {
         return (Multimap)super.delegate();
      }

      SynchronizedMultimap(Multimap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      public int size() {
         synchronized(this.mutex) {
            return this.delegate().size();
         }
      }

      public boolean isEmpty() {
         synchronized(this.mutex) {
            return this.delegate().isEmpty();
         }
      }

      public boolean containsKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().containsKey(key);
         }
      }

      public boolean containsValue(Object value) {
         synchronized(this.mutex) {
            return this.delegate().containsValue(value);
         }
      }

      public boolean containsEntry(Object key, Object value) {
         synchronized(this.mutex) {
            return this.delegate().containsEntry(key, value);
         }
      }

      public Collection get(Object key) {
         synchronized(this.mutex) {
            return Synchronized.typePreservingCollection(this.delegate().get(key), this.mutex);
         }
      }

      public boolean put(Object key, Object value) {
         synchronized(this.mutex) {
            return this.delegate().put(key, value);
         }
      }

      public boolean putAll(Object key, Iterable values) {
         synchronized(this.mutex) {
            return this.delegate().putAll(key, values);
         }
      }

      public boolean putAll(Multimap multimap) {
         synchronized(this.mutex) {
            return this.delegate().putAll(multimap);
         }
      }

      public Collection replaceValues(Object key, Iterable values) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(key, values);
         }
      }

      public boolean remove(Object key, Object value) {
         synchronized(this.mutex) {
            return this.delegate().remove(key, value);
         }
      }

      public Collection removeAll(Object key) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(key);
         }
      }

      public void clear() {
         synchronized(this.mutex) {
            this.delegate().clear();
         }
      }

      public Set keySet() {
         synchronized(this.mutex) {
            if(this.keySet == null) {
               this.keySet = Synchronized.typePreservingSet(this.delegate().keySet(), this.mutex);
            }

            return this.keySet;
         }
      }

      public Collection values() {
         synchronized(this.mutex) {
            if(this.valuesCollection == null) {
               this.valuesCollection = Synchronized.collection(this.delegate().values(), this.mutex);
            }

            return this.valuesCollection;
         }
      }

      public Collection entries() {
         synchronized(this.mutex) {
            if(this.entries == null) {
               this.entries = Synchronized.typePreservingCollection(this.delegate().entries(), this.mutex);
            }

            return this.entries;
         }
      }

      public Map asMap() {
         synchronized(this.mutex) {
            if(this.asMap == null) {
               this.asMap = new Synchronized.SynchronizedAsMap(this.delegate().asMap(), this.mutex);
            }

            return this.asMap;
         }
      }

      public Multiset keys() {
         synchronized(this.mutex) {
            if(this.keys == null) {
               this.keys = Synchronized.multiset(this.delegate().keys(), this.mutex);
            }

            return this.keys;
         }
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(o);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedMultiset extends Synchronized.SynchronizedCollection implements Multiset {
      transient Set elementSet;
      transient Set entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedMultiset(Multiset delegate, @Nullable Object mutex) {
         super(delegate, mutex, null);
      }

      Multiset delegate() {
         return (Multiset)super.delegate();
      }

      public int count(Object o) {
         synchronized(this.mutex) {
            return this.delegate().count(o);
         }
      }

      public int add(Object e, int n) {
         synchronized(this.mutex) {
            return this.delegate().add(e, n);
         }
      }

      public int remove(Object o, int n) {
         synchronized(this.mutex) {
            return this.delegate().remove(o, n);
         }
      }

      public int setCount(Object element, int count) {
         synchronized(this.mutex) {
            return this.delegate().setCount(element, count);
         }
      }

      public boolean setCount(Object element, int oldCount, int newCount) {
         synchronized(this.mutex) {
            return this.delegate().setCount(element, oldCount, newCount);
         }
      }

      public Set elementSet() {
         synchronized(this.mutex) {
            if(this.elementSet == null) {
               this.elementSet = Synchronized.typePreservingSet(this.delegate().elementSet(), this.mutex);
            }

            return this.elementSet;
         }
      }

      public Set entrySet() {
         synchronized(this.mutex) {
            if(this.entrySet == null) {
               this.entrySet = Synchronized.typePreservingSet(this.delegate().entrySet(), this.mutex);
            }

            return this.entrySet;
         }
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(o);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   @GwtIncompatible("NavigableMap")
   @VisibleForTesting
   static class SynchronizedNavigableMap extends Synchronized.SynchronizedSortedMap implements NavigableMap {
      transient NavigableSet descendingKeySet;
      transient NavigableMap descendingMap;
      transient NavigableSet navigableKeySet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableMap(NavigableMap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      NavigableMap delegate() {
         return (NavigableMap)super.delegate();
      }

      public Entry ceilingEntry(Object key) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().ceilingEntry(key), this.mutex);
         }
      }

      public Object ceilingKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().ceilingKey(key);
         }
      }

      public NavigableSet descendingKeySet() {
         synchronized(this.mutex) {
            return this.descendingKeySet == null?(this.descendingKeySet = Synchronized.navigableSet(this.delegate().descendingKeySet(), this.mutex)):this.descendingKeySet;
         }
      }

      public NavigableMap descendingMap() {
         synchronized(this.mutex) {
            return this.descendingMap == null?(this.descendingMap = Synchronized.navigableMap(this.delegate().descendingMap(), this.mutex)):this.descendingMap;
         }
      }

      public Entry firstEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().firstEntry(), this.mutex);
         }
      }

      public Entry floorEntry(Object key) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().floorEntry(key), this.mutex);
         }
      }

      public Object floorKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().floorKey(key);
         }
      }

      public NavigableMap headMap(Object toKey, boolean inclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().headMap(toKey, inclusive), this.mutex);
         }
      }

      public Entry higherEntry(Object key) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().higherEntry(key), this.mutex);
         }
      }

      public Object higherKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().higherKey(key);
         }
      }

      public Entry lastEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lastEntry(), this.mutex);
         }
      }

      public Entry lowerEntry(Object key) {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().lowerEntry(key), this.mutex);
         }
      }

      public Object lowerKey(Object key) {
         synchronized(this.mutex) {
            return this.delegate().lowerKey(key);
         }
      }

      public Set keySet() {
         return this.navigableKeySet();
      }

      public NavigableSet navigableKeySet() {
         synchronized(this.mutex) {
            return this.navigableKeySet == null?(this.navigableKeySet = Synchronized.navigableSet(this.delegate().navigableKeySet(), this.mutex)):this.navigableKeySet;
         }
      }

      public Entry pollFirstEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollFirstEntry(), this.mutex);
         }
      }

      public Entry pollLastEntry() {
         synchronized(this.mutex) {
            return Synchronized.nullableSynchronizedEntry(this.delegate().pollLastEntry(), this.mutex);
         }
      }

      public NavigableMap subMap(Object fromKey, boolean fromInclusive, Object toKey, boolean toInclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().subMap(fromKey, fromInclusive, toKey, toInclusive), this.mutex);
         }
      }

      public NavigableMap tailMap(Object fromKey, boolean inclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableMap(this.delegate().tailMap(fromKey, inclusive), this.mutex);
         }
      }

      public SortedMap headMap(Object toKey) {
         return this.headMap(toKey, false);
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         return this.subMap(fromKey, true, toKey, false);
      }

      public SortedMap tailMap(Object fromKey) {
         return this.tailMap(fromKey, true);
      }
   }

   @GwtIncompatible("NavigableSet")
   @VisibleForTesting
   static class SynchronizedNavigableSet extends Synchronized.SynchronizedSortedSet implements NavigableSet {
      transient NavigableSet descendingSet;
      private static final long serialVersionUID = 0L;

      SynchronizedNavigableSet(NavigableSet delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      NavigableSet delegate() {
         return (NavigableSet)super.delegate();
      }

      public Object ceiling(Object e) {
         synchronized(this.mutex) {
            return this.delegate().ceiling(e);
         }
      }

      public Iterator descendingIterator() {
         return this.delegate().descendingIterator();
      }

      public NavigableSet descendingSet() {
         synchronized(this.mutex) {
            if(this.descendingSet == null) {
               NavigableSet<E> dS = Synchronized.navigableSet(this.delegate().descendingSet(), this.mutex);
               this.descendingSet = dS;
               return dS;
            } else {
               return this.descendingSet;
            }
         }
      }

      public Object floor(Object e) {
         synchronized(this.mutex) {
            return this.delegate().floor(e);
         }
      }

      public NavigableSet headSet(Object toElement, boolean inclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().headSet(toElement, inclusive), this.mutex);
         }
      }

      public Object higher(Object e) {
         synchronized(this.mutex) {
            return this.delegate().higher(e);
         }
      }

      public Object lower(Object e) {
         synchronized(this.mutex) {
            return this.delegate().lower(e);
         }
      }

      public Object pollFirst() {
         synchronized(this.mutex) {
            return this.delegate().pollFirst();
         }
      }

      public Object pollLast() {
         synchronized(this.mutex) {
            return this.delegate().pollLast();
         }
      }

      public NavigableSet subSet(Object fromElement, boolean fromInclusive, Object toElement, boolean toInclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().subSet(fromElement, fromInclusive, toElement, toInclusive), this.mutex);
         }
      }

      public NavigableSet tailSet(Object fromElement, boolean inclusive) {
         synchronized(this.mutex) {
            return Synchronized.navigableSet(this.delegate().tailSet(fromElement, inclusive), this.mutex);
         }
      }

      public SortedSet headSet(Object toElement) {
         return this.headSet(toElement, false);
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         return this.subSet(fromElement, true, toElement, false);
      }

      public SortedSet tailSet(Object fromElement) {
         return this.tailSet(fromElement, true);
      }
   }

   static class SynchronizedObject implements Serializable {
      final Object delegate;
      final Object mutex;
      @GwtIncompatible("not needed in emulated source")
      private static final long serialVersionUID = 0L;

      SynchronizedObject(Object delegate, @Nullable Object mutex) {
         this.delegate = Preconditions.checkNotNull(delegate);
         this.mutex = mutex == null?this:mutex;
      }

      Object delegate() {
         return this.delegate;
      }

      public String toString() {
         synchronized(this.mutex) {
            return this.delegate.toString();
         }
      }

      @GwtIncompatible("java.io.ObjectOutputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         synchronized(this.mutex) {
            stream.defaultWriteObject();
         }
      }
   }

   private static class SynchronizedQueue extends Synchronized.SynchronizedCollection implements Queue {
      private static final long serialVersionUID = 0L;

      SynchronizedQueue(Queue delegate, @Nullable Object mutex) {
         super(delegate, mutex, null);
      }

      Queue delegate() {
         return (Queue)super.delegate();
      }

      public Object element() {
         synchronized(this.mutex) {
            return this.delegate().element();
         }
      }

      public boolean offer(Object e) {
         synchronized(this.mutex) {
            return this.delegate().offer(e);
         }
      }

      public Object peek() {
         synchronized(this.mutex) {
            return this.delegate().peek();
         }
      }

      public Object poll() {
         synchronized(this.mutex) {
            return this.delegate().poll();
         }
      }

      public Object remove() {
         synchronized(this.mutex) {
            return this.delegate().remove();
         }
      }
   }

   private static class SynchronizedRandomAccessList extends Synchronized.SynchronizedList implements RandomAccess {
      private static final long serialVersionUID = 0L;

      SynchronizedRandomAccessList(List list, @Nullable Object mutex) {
         super(list, mutex);
      }
   }

   static class SynchronizedSet extends Synchronized.SynchronizedCollection implements Set {
      private static final long serialVersionUID = 0L;

      SynchronizedSet(Set delegate, @Nullable Object mutex) {
         super(delegate, mutex, null);
      }

      Set delegate() {
         return (Set)super.delegate();
      }

      public boolean equals(Object o) {
         if(o == this) {
            return true;
         } else {
            synchronized(this.mutex) {
               return this.delegate().equals(o);
            }
         }
      }

      public int hashCode() {
         synchronized(this.mutex) {
            return this.delegate().hashCode();
         }
      }
   }

   private static class SynchronizedSetMultimap extends Synchronized.SynchronizedMultimap implements SetMultimap {
      transient Set entrySet;
      private static final long serialVersionUID = 0L;

      SynchronizedSetMultimap(SetMultimap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      SetMultimap delegate() {
         return (SetMultimap)super.delegate();
      }

      public Set get(Object key) {
         synchronized(this.mutex) {
            return Synchronized.set(this.delegate().get(key), this.mutex);
         }
      }

      public Set removeAll(Object key) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(key);
         }
      }

      public Set replaceValues(Object key, Iterable values) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(key, values);
         }
      }

      public Set entries() {
         synchronized(this.mutex) {
            if(this.entrySet == null) {
               this.entrySet = Synchronized.set(this.delegate().entries(), this.mutex);
            }

            return this.entrySet;
         }
      }
   }

   static class SynchronizedSortedMap extends Synchronized.SynchronizedMap implements SortedMap {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedMap(SortedMap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      SortedMap delegate() {
         return (SortedMap)super.delegate();
      }

      public Comparator comparator() {
         synchronized(this.mutex) {
            return this.delegate().comparator();
         }
      }

      public Object firstKey() {
         synchronized(this.mutex) {
            return this.delegate().firstKey();
         }
      }

      public SortedMap headMap(Object toKey) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().headMap(toKey), this.mutex);
         }
      }

      public Object lastKey() {
         synchronized(this.mutex) {
            return this.delegate().lastKey();
         }
      }

      public SortedMap subMap(Object fromKey, Object toKey) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().subMap(fromKey, toKey), this.mutex);
         }
      }

      public SortedMap tailMap(Object fromKey) {
         synchronized(this.mutex) {
            return Synchronized.sortedMap(this.delegate().tailMap(fromKey), this.mutex);
         }
      }
   }

   static class SynchronizedSortedSet extends Synchronized.SynchronizedSet implements SortedSet {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSet(SortedSet delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      SortedSet delegate() {
         return (SortedSet)super.delegate();
      }

      public Comparator comparator() {
         synchronized(this.mutex) {
            return this.delegate().comparator();
         }
      }

      public SortedSet subSet(Object fromElement, Object toElement) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().subSet(fromElement, toElement), this.mutex);
         }
      }

      public SortedSet headSet(Object toElement) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().headSet(toElement), this.mutex);
         }
      }

      public SortedSet tailSet(Object fromElement) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().tailSet(fromElement), this.mutex);
         }
      }

      public Object first() {
         synchronized(this.mutex) {
            return this.delegate().first();
         }
      }

      public Object last() {
         synchronized(this.mutex) {
            return this.delegate().last();
         }
      }
   }

   private static class SynchronizedSortedSetMultimap extends Synchronized.SynchronizedSetMultimap implements SortedSetMultimap {
      private static final long serialVersionUID = 0L;

      SynchronizedSortedSetMultimap(SortedSetMultimap delegate, @Nullable Object mutex) {
         super(delegate, mutex);
      }

      SortedSetMultimap delegate() {
         return (SortedSetMultimap)super.delegate();
      }

      public SortedSet get(Object key) {
         synchronized(this.mutex) {
            return Synchronized.sortedSet(this.delegate().get(key), this.mutex);
         }
      }

      public SortedSet removeAll(Object key) {
         synchronized(this.mutex) {
            return this.delegate().removeAll(key);
         }
      }

      public SortedSet replaceValues(Object key, Iterable values) {
         synchronized(this.mutex) {
            return this.delegate().replaceValues(key, values);
         }
      }

      public Comparator valueComparator() {
         synchronized(this.mutex) {
            return this.delegate().valueComparator();
         }
      }
   }
}
