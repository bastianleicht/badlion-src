package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;
import com.google.common.collect.AbstractListMultimap;
import com.google.common.collect.AbstractMapBasedMultimap;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.AbstractMultiset;
import com.google.common.collect.AbstractSetMultimap;
import com.google.common.collect.AbstractSortedSetMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.FilteredEntryMultimap;
import com.google.common.collect.FilteredEntrySetMultimap;
import com.google.common.collect.FilteredKeyListMultimap;
import com.google.common.collect.FilteredKeyMultimap;
import com.google.common.collect.FilteredKeySetMultimap;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.FilteredSetMultimap;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.Synchronized;
import com.google.common.collect.TransformedIterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class Multimaps {
   public static Multimap newMultimap(Map map, Supplier factory) {
      return new Multimaps.CustomMultimap(map, factory);
   }

   public static ListMultimap newListMultimap(Map map, Supplier factory) {
      return new Multimaps.CustomListMultimap(map, factory);
   }

   public static SetMultimap newSetMultimap(Map map, Supplier factory) {
      return new Multimaps.CustomSetMultimap(map, factory);
   }

   public static SortedSetMultimap newSortedSetMultimap(Map map, Supplier factory) {
      return new Multimaps.CustomSortedSetMultimap(map, factory);
   }

   public static Multimap invertFrom(Multimap source, Multimap dest) {
      Preconditions.checkNotNull(dest);

      for(Entry<? extends V, ? extends K> entry : source.entries()) {
         dest.put(entry.getValue(), entry.getKey());
      }

      return dest;
   }

   public static Multimap synchronizedMultimap(Multimap multimap) {
      return Synchronized.multimap(multimap, (Object)null);
   }

   public static Multimap unmodifiableMultimap(Multimap delegate) {
      return (Multimap)(!(delegate instanceof Multimaps.UnmodifiableMultimap) && !(delegate instanceof ImmutableMultimap)?new Multimaps.UnmodifiableMultimap(delegate):delegate);
   }

   /** @deprecated */
   @Deprecated
   public static Multimap unmodifiableMultimap(ImmutableMultimap delegate) {
      return (Multimap)Preconditions.checkNotNull(delegate);
   }

   public static SetMultimap synchronizedSetMultimap(SetMultimap multimap) {
      return Synchronized.setMultimap(multimap, (Object)null);
   }

   public static SetMultimap unmodifiableSetMultimap(SetMultimap delegate) {
      return (SetMultimap)(!(delegate instanceof Multimaps.UnmodifiableSetMultimap) && !(delegate instanceof ImmutableSetMultimap)?new Multimaps.UnmodifiableSetMultimap(delegate):delegate);
   }

   /** @deprecated */
   @Deprecated
   public static SetMultimap unmodifiableSetMultimap(ImmutableSetMultimap delegate) {
      return (SetMultimap)Preconditions.checkNotNull(delegate);
   }

   public static SortedSetMultimap synchronizedSortedSetMultimap(SortedSetMultimap multimap) {
      return Synchronized.sortedSetMultimap(multimap, (Object)null);
   }

   public static SortedSetMultimap unmodifiableSortedSetMultimap(SortedSetMultimap delegate) {
      return (SortedSetMultimap)(delegate instanceof Multimaps.UnmodifiableSortedSetMultimap?delegate:new Multimaps.UnmodifiableSortedSetMultimap(delegate));
   }

   public static ListMultimap synchronizedListMultimap(ListMultimap multimap) {
      return Synchronized.listMultimap(multimap, (Object)null);
   }

   public static ListMultimap unmodifiableListMultimap(ListMultimap delegate) {
      return (ListMultimap)(!(delegate instanceof Multimaps.UnmodifiableListMultimap) && !(delegate instanceof ImmutableListMultimap)?new Multimaps.UnmodifiableListMultimap(delegate):delegate);
   }

   /** @deprecated */
   @Deprecated
   public static ListMultimap unmodifiableListMultimap(ImmutableListMultimap delegate) {
      return (ListMultimap)Preconditions.checkNotNull(delegate);
   }

   private static Collection unmodifiableValueCollection(Collection collection) {
      return (Collection)(collection instanceof SortedSet?Collections.unmodifiableSortedSet((SortedSet)collection):(collection instanceof Set?Collections.unmodifiableSet((Set)collection):(collection instanceof List?Collections.unmodifiableList((List)collection):Collections.unmodifiableCollection(collection))));
   }

   private static Collection unmodifiableEntries(Collection entries) {
      return (Collection)(entries instanceof Set?Maps.unmodifiableEntrySet((Set)entries):new Maps.UnmodifiableEntries(Collections.unmodifiableCollection(entries)));
   }

   @Beta
   public static Map asMap(ListMultimap multimap) {
      return multimap.asMap();
   }

   @Beta
   public static Map asMap(SetMultimap multimap) {
      return multimap.asMap();
   }

   @Beta
   public static Map asMap(SortedSetMultimap multimap) {
      return multimap.asMap();
   }

   @Beta
   public static Map asMap(Multimap multimap) {
      return multimap.asMap();
   }

   public static SetMultimap forMap(Map map) {
      return new Multimaps.MapMultimap(map);
   }

   public static Multimap transformValues(Multimap fromMultimap, Function function) {
      Preconditions.checkNotNull(function);
      Maps.EntryTransformer<K, V1, V2> transformer = Maps.asEntryTransformer(function);
      return transformEntries(fromMultimap, transformer);
   }

   public static Multimap transformEntries(Multimap fromMap, Maps.EntryTransformer transformer) {
      return new Multimaps.TransformedEntriesMultimap(fromMap, transformer);
   }

   public static ListMultimap transformValues(ListMultimap fromMultimap, Function function) {
      Preconditions.checkNotNull(function);
      Maps.EntryTransformer<K, V1, V2> transformer = Maps.asEntryTransformer(function);
      return transformEntries(fromMultimap, transformer);
   }

   public static ListMultimap transformEntries(ListMultimap fromMap, Maps.EntryTransformer transformer) {
      return new Multimaps.TransformedEntriesListMultimap(fromMap, transformer);
   }

   public static ImmutableListMultimap index(Iterable values, Function keyFunction) {
      return index(values.iterator(), keyFunction);
   }

   public static ImmutableListMultimap index(Iterator values, Function keyFunction) {
      Preconditions.checkNotNull(keyFunction);
      ImmutableListMultimap.Builder<K, V> builder = ImmutableListMultimap.builder();

      while(values.hasNext()) {
         V value = values.next();
         Preconditions.checkNotNull(value, values);
         builder.put(keyFunction.apply(value), value);
      }

      return builder.build();
   }

   public static Multimap filterKeys(Multimap unfiltered, Predicate keyPredicate) {
      if(unfiltered instanceof SetMultimap) {
         return filterKeys((SetMultimap)unfiltered, keyPredicate);
      } else if(unfiltered instanceof ListMultimap) {
         return filterKeys((ListMultimap)unfiltered, keyPredicate);
      } else if(unfiltered instanceof FilteredKeyMultimap) {
         FilteredKeyMultimap<K, V> prev = (FilteredKeyMultimap)unfiltered;
         return new FilteredKeyMultimap(prev.unfiltered, Predicates.and(prev.keyPredicate, keyPredicate));
      } else if(unfiltered instanceof FilteredMultimap) {
         FilteredMultimap<K, V> prev = (FilteredMultimap)unfiltered;
         return filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
      } else {
         return new FilteredKeyMultimap(unfiltered, keyPredicate);
      }
   }

   public static SetMultimap filterKeys(SetMultimap unfiltered, Predicate keyPredicate) {
      if(unfiltered instanceof FilteredKeySetMultimap) {
         FilteredKeySetMultimap<K, V> prev = (FilteredKeySetMultimap)unfiltered;
         return new FilteredKeySetMultimap(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
      } else if(unfiltered instanceof FilteredSetMultimap) {
         FilteredSetMultimap<K, V> prev = (FilteredSetMultimap)unfiltered;
         return filterFiltered(prev, Maps.keyPredicateOnEntries(keyPredicate));
      } else {
         return new FilteredKeySetMultimap(unfiltered, keyPredicate);
      }
   }

   public static ListMultimap filterKeys(ListMultimap unfiltered, Predicate keyPredicate) {
      if(unfiltered instanceof FilteredKeyListMultimap) {
         FilteredKeyListMultimap<K, V> prev = (FilteredKeyListMultimap)unfiltered;
         return new FilteredKeyListMultimap(prev.unfiltered(), Predicates.and(prev.keyPredicate, keyPredicate));
      } else {
         return new FilteredKeyListMultimap(unfiltered, keyPredicate);
      }
   }

   public static Multimap filterValues(Multimap unfiltered, Predicate valuePredicate) {
      return filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
   }

   public static SetMultimap filterValues(SetMultimap unfiltered, Predicate valuePredicate) {
      return filterEntries(unfiltered, Maps.valuePredicateOnEntries(valuePredicate));
   }

   public static Multimap filterEntries(Multimap unfiltered, Predicate entryPredicate) {
      Preconditions.checkNotNull(entryPredicate);
      return (Multimap)(unfiltered instanceof SetMultimap?filterEntries((SetMultimap)unfiltered, entryPredicate):(unfiltered instanceof FilteredMultimap?filterFiltered((FilteredMultimap)unfiltered, entryPredicate):new FilteredEntryMultimap((Multimap)Preconditions.checkNotNull(unfiltered), entryPredicate)));
   }

   public static SetMultimap filterEntries(SetMultimap unfiltered, Predicate entryPredicate) {
      Preconditions.checkNotNull(entryPredicate);
      return (SetMultimap)(unfiltered instanceof FilteredSetMultimap?filterFiltered((FilteredSetMultimap)unfiltered, entryPredicate):new FilteredEntrySetMultimap((SetMultimap)Preconditions.checkNotNull(unfiltered), entryPredicate));
   }

   private static Multimap filterFiltered(FilteredMultimap multimap, Predicate entryPredicate) {
      Predicate<Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
      return new FilteredEntryMultimap(multimap.unfiltered(), predicate);
   }

   private static SetMultimap filterFiltered(FilteredSetMultimap multimap, Predicate entryPredicate) {
      Predicate<Entry<K, V>> predicate = Predicates.and(multimap.entryPredicate(), entryPredicate);
      return new FilteredEntrySetMultimap(multimap.unfiltered(), predicate);
   }

   static boolean equalsImpl(Multimap multimap, @Nullable Object object) {
      if(object == multimap) {
         return true;
      } else if(object instanceof Multimap) {
         Multimap<?, ?> that = (Multimap)object;
         return multimap.asMap().equals(that.asMap());
      } else {
         return false;
      }
   }

   static final class AsMap extends Maps.ImprovedAbstractMap {
      private final Multimap multimap;

      AsMap(Multimap multimap) {
         this.multimap = (Multimap)Preconditions.checkNotNull(multimap);
      }

      public int size() {
         return this.multimap.keySet().size();
      }

      protected Set createEntrySet() {
         return new Multimaps.AsMap.EntrySet();
      }

      void removeValuesForKey(Object key) {
         this.multimap.keySet().remove(key);
      }

      public Collection get(Object key) {
         return this.containsKey(key)?this.multimap.get(key):null;
      }

      public Collection remove(Object key) {
         return this.containsKey(key)?this.multimap.removeAll(key):null;
      }

      public Set keySet() {
         return this.multimap.keySet();
      }

      public boolean isEmpty() {
         return this.multimap.isEmpty();
      }

      public boolean containsKey(Object key) {
         return this.multimap.containsKey(key);
      }

      public void clear() {
         this.multimap.clear();
      }

      class EntrySet extends Maps.EntrySet {
         Map map() {
            return AsMap.this;
         }

         public Iterator iterator() {
            return Maps.asMapEntryIterator(AsMap.this.multimap.keySet(), new Function() {
               public Collection apply(Object key) {
                  return AsMap.this.multimap.get(key);
               }
            });
         }

         public boolean remove(Object o) {
            if(!this.contains(o)) {
               return false;
            } else {
               Entry<?, ?> entry = (Entry)o;
               AsMap.this.removeValuesForKey(entry.getKey());
               return true;
            }
         }
      }
   }

   private static class CustomListMultimap extends AbstractListMultimap {
      transient Supplier factory;
      @GwtIncompatible("java serialization not supported")
      private static final long serialVersionUID = 0L;

      CustomListMultimap(Map map, Supplier factory) {
         super(map);
         this.factory = (Supplier)Preconditions.checkNotNull(factory);
      }

      protected List createCollection() {
         return (List)this.factory.get();
      }

      @GwtIncompatible("java.io.ObjectOutputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.factory);
         stream.writeObject(this.backingMap());
      }

      @GwtIncompatible("java.io.ObjectInputStream")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.factory = (Supplier)stream.readObject();
         Map<K, Collection<V>> map = (Map)stream.readObject();
         this.setMap(map);
      }
   }

   private static class CustomMultimap extends AbstractMapBasedMultimap {
      transient Supplier factory;
      @GwtIncompatible("java serialization not supported")
      private static final long serialVersionUID = 0L;

      CustomMultimap(Map map, Supplier factory) {
         super(map);
         this.factory = (Supplier)Preconditions.checkNotNull(factory);
      }

      protected Collection createCollection() {
         return (Collection)this.factory.get();
      }

      @GwtIncompatible("java.io.ObjectOutputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.factory);
         stream.writeObject(this.backingMap());
      }

      @GwtIncompatible("java.io.ObjectInputStream")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.factory = (Supplier)stream.readObject();
         Map<K, Collection<V>> map = (Map)stream.readObject();
         this.setMap(map);
      }
   }

   private static class CustomSetMultimap extends AbstractSetMultimap {
      transient Supplier factory;
      @GwtIncompatible("not needed in emulated source")
      private static final long serialVersionUID = 0L;

      CustomSetMultimap(Map map, Supplier factory) {
         super(map);
         this.factory = (Supplier)Preconditions.checkNotNull(factory);
      }

      protected Set createCollection() {
         return (Set)this.factory.get();
      }

      @GwtIncompatible("java.io.ObjectOutputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.factory);
         stream.writeObject(this.backingMap());
      }

      @GwtIncompatible("java.io.ObjectInputStream")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.factory = (Supplier)stream.readObject();
         Map<K, Collection<V>> map = (Map)stream.readObject();
         this.setMap(map);
      }
   }

   private static class CustomSortedSetMultimap extends AbstractSortedSetMultimap {
      transient Supplier factory;
      transient Comparator valueComparator;
      @GwtIncompatible("not needed in emulated source")
      private static final long serialVersionUID = 0L;

      CustomSortedSetMultimap(Map map, Supplier factory) {
         super(map);
         this.factory = (Supplier)Preconditions.checkNotNull(factory);
         this.valueComparator = ((SortedSet)factory.get()).comparator();
      }

      protected SortedSet createCollection() {
         return (SortedSet)this.factory.get();
      }

      public Comparator valueComparator() {
         return this.valueComparator;
      }

      @GwtIncompatible("java.io.ObjectOutputStream")
      private void writeObject(ObjectOutputStream stream) throws IOException {
         stream.defaultWriteObject();
         stream.writeObject(this.factory);
         stream.writeObject(this.backingMap());
      }

      @GwtIncompatible("java.io.ObjectInputStream")
      private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
         stream.defaultReadObject();
         this.factory = (Supplier)stream.readObject();
         this.valueComparator = ((SortedSet)this.factory.get()).comparator();
         Map<K, Collection<V>> map = (Map)stream.readObject();
         this.setMap(map);
      }
   }

   abstract static class Entries extends AbstractCollection {
      abstract Multimap multimap();

      public int size() {
         return this.multimap().size();
      }

      public boolean contains(@Nullable Object o) {
         if(o instanceof Entry) {
            Entry<?, ?> entry = (Entry)o;
            return this.multimap().containsEntry(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }

      public boolean remove(@Nullable Object o) {
         if(o instanceof Entry) {
            Entry<?, ?> entry = (Entry)o;
            return this.multimap().remove(entry.getKey(), entry.getValue());
         } else {
            return false;
         }
      }

      public void clear() {
         this.multimap().clear();
      }
   }

   static class Keys extends AbstractMultiset {
      final Multimap multimap;

      Keys(Multimap multimap) {
         this.multimap = multimap;
      }

      Iterator entryIterator() {
         return new TransformedIterator(this.multimap.asMap().entrySet().iterator()) {
            Multiset.Entry transform(final Entry backingEntry) {
               return new Multisets.AbstractEntry() {
                  public Object getElement() {
                     return backingEntry.getKey();
                  }

                  public int getCount() {
                     return ((Collection)backingEntry.getValue()).size();
                  }
               };
            }
         };
      }

      int distinctElements() {
         return this.multimap.asMap().size();
      }

      Set createEntrySet() {
         return new Multimaps.Keys.KeysEntrySet();
      }

      public boolean contains(@Nullable Object element) {
         return this.multimap.containsKey(element);
      }

      public Iterator iterator() {
         return Maps.keyIterator(this.multimap.entries().iterator());
      }

      public int count(@Nullable Object element) {
         Collection<V> values = (Collection)Maps.safeGet(this.multimap.asMap(), element);
         return values == null?0:values.size();
      }

      public int remove(@Nullable Object element, int occurrences) {
         CollectPreconditions.checkNonnegative(occurrences, "occurrences");
         if(occurrences == 0) {
            return this.count(element);
         } else {
            Collection<V> values = (Collection)Maps.safeGet(this.multimap.asMap(), element);
            if(values == null) {
               return 0;
            } else {
               int oldCount = values.size();
               if(occurrences >= oldCount) {
                  values.clear();
               } else {
                  Iterator<V> iterator = values.iterator();

                  for(int i = 0; i < occurrences; ++i) {
                     iterator.next();
                     iterator.remove();
                  }
               }

               return oldCount;
            }
         }
      }

      public void clear() {
         this.multimap.clear();
      }

      public Set elementSet() {
         return this.multimap.keySet();
      }

      class KeysEntrySet extends Multisets.EntrySet {
         Multiset multiset() {
            return Keys.this;
         }

         public Iterator iterator() {
            return Keys.this.entryIterator();
         }

         public int size() {
            return Keys.this.distinctElements();
         }

         public boolean isEmpty() {
            return Keys.this.multimap.isEmpty();
         }

         public boolean contains(@Nullable Object o) {
            if(!(o instanceof Multiset.Entry)) {
               return false;
            } else {
               Multiset.Entry<?> entry = (Multiset.Entry)o;
               Collection<V> collection = (Collection)Keys.this.multimap.asMap().get(entry.getElement());
               return collection != null && collection.size() == entry.getCount();
            }
         }

         public boolean remove(@Nullable Object o) {
            if(o instanceof Multiset.Entry) {
               Multiset.Entry<?> entry = (Multiset.Entry)o;
               Collection<V> collection = (Collection)Keys.this.multimap.asMap().get(entry.getElement());
               if(collection != null && collection.size() == entry.getCount()) {
                  collection.clear();
                  return true;
               }
            }

            return false;
         }
      }
   }

   private static class MapMultimap extends AbstractMultimap implements SetMultimap, Serializable {
      final Map map;
      private static final long serialVersionUID = 7845222491160860175L;

      MapMultimap(Map map) {
         this.map = (Map)Preconditions.checkNotNull(map);
      }

      public int size() {
         return this.map.size();
      }

      public boolean containsKey(Object key) {
         return this.map.containsKey(key);
      }

      public boolean containsValue(Object value) {
         return this.map.containsValue(value);
      }

      public boolean containsEntry(Object key, Object value) {
         return this.map.entrySet().contains(Maps.immutableEntry(key, value));
      }

      public Set get(final Object key) {
         return new Sets.ImprovedAbstractSet() {
            public Iterator iterator() {
               return new Iterator() {
                  int i;

                  public boolean hasNext() {
                     return this.i == 0 && MapMultimap.this.map.containsKey(key);
                  }

                  public Object next() {
                     if(!this.hasNext()) {
                        throw new NoSuchElementException();
                     } else {
                        ++this.i;
                        return MapMultimap.this.map.get(key);
                     }
                  }

                  public void remove() {
                     CollectPreconditions.checkRemove(this.i == 1);
                     this.i = -1;
                     MapMultimap.this.map.remove(key);
                  }
               };
            }

            public int size() {
               return MapMultimap.this.map.containsKey(key)?1:0;
            }
         };
      }

      public boolean put(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Multimap multimap) {
         throw new UnsupportedOperationException();
      }

      public Set replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object key, Object value) {
         return this.map.entrySet().remove(Maps.immutableEntry(key, value));
      }

      public Set removeAll(Object key) {
         Set<V> values = new HashSet(2);
         if(!this.map.containsKey(key)) {
            return values;
         } else {
            values.add(this.map.remove(key));
            return values;
         }
      }

      public void clear() {
         this.map.clear();
      }

      public Set keySet() {
         return this.map.keySet();
      }

      public Collection values() {
         return this.map.values();
      }

      public Set entries() {
         return this.map.entrySet();
      }

      Iterator entryIterator() {
         return this.map.entrySet().iterator();
      }

      Map createAsMap() {
         return new Multimaps.AsMap(this);
      }

      public int hashCode() {
         return this.map.hashCode();
      }
   }

   private static final class TransformedEntriesListMultimap extends Multimaps.TransformedEntriesMultimap implements ListMultimap {
      TransformedEntriesListMultimap(ListMultimap fromMultimap, Maps.EntryTransformer transformer) {
         super(fromMultimap, transformer);
      }

      List transform(Object key, Collection values) {
         return Lists.transform((List)values, Maps.asValueToValueFunction(this.transformer, key));
      }

      public List get(Object key) {
         return this.transform(key, this.fromMultimap.get(key));
      }

      public List removeAll(Object key) {
         return this.transform(key, this.fromMultimap.removeAll(key));
      }

      public List replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }
   }

   private static class TransformedEntriesMultimap extends AbstractMultimap {
      final Multimap fromMultimap;
      final Maps.EntryTransformer transformer;

      TransformedEntriesMultimap(Multimap fromMultimap, Maps.EntryTransformer transformer) {
         this.fromMultimap = (Multimap)Preconditions.checkNotNull(fromMultimap);
         this.transformer = (Maps.EntryTransformer)Preconditions.checkNotNull(transformer);
      }

      Collection transform(Object key, Collection values) {
         Function<? super V1, V2> function = Maps.asValueToValueFunction(this.transformer, key);
         return (Collection)(values instanceof List?Lists.transform((List)values, function):Collections2.transform(values, function));
      }

      Map createAsMap() {
         return Maps.transformEntries(this.fromMultimap.asMap(), new Maps.EntryTransformer() {
            public Collection transformEntry(Object key, Collection value) {
               return TransformedEntriesMultimap.this.transform(key, value);
            }
         });
      }

      public void clear() {
         this.fromMultimap.clear();
      }

      public boolean containsKey(Object key) {
         return this.fromMultimap.containsKey(key);
      }

      Iterator entryIterator() {
         return Iterators.transform(this.fromMultimap.entries().iterator(), Maps.asEntryToEntryFunction(this.transformer));
      }

      public Collection get(Object key) {
         return this.transform(key, this.fromMultimap.get(key));
      }

      public boolean isEmpty() {
         return this.fromMultimap.isEmpty();
      }

      public Set keySet() {
         return this.fromMultimap.keySet();
      }

      public Multiset keys() {
         return this.fromMultimap.keys();
      }

      public boolean put(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Multimap multimap) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object key, Object value) {
         return this.get(key).remove(value);
      }

      public Collection removeAll(Object key) {
         return this.transform(key, this.fromMultimap.removeAll(key));
      }

      public Collection replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public int size() {
         return this.fromMultimap.size();
      }

      Collection createValues() {
         return Collections2.transform(this.fromMultimap.entries(), Maps.asEntryToValueFunction(this.transformer));
      }
   }

   private static class UnmodifiableListMultimap extends Multimaps.UnmodifiableMultimap implements ListMultimap {
      private static final long serialVersionUID = 0L;

      UnmodifiableListMultimap(ListMultimap delegate) {
         super(delegate);
      }

      public ListMultimap delegate() {
         return (ListMultimap)super.delegate();
      }

      public List get(Object key) {
         return Collections.unmodifiableList(this.delegate().get(key));
      }

      public List removeAll(Object key) {
         throw new UnsupportedOperationException();
      }

      public List replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }
   }

   private static class UnmodifiableMultimap extends ForwardingMultimap implements Serializable {
      final Multimap delegate;
      transient Collection entries;
      transient Multiset keys;
      transient Set keySet;
      transient Collection values;
      transient Map map;
      private static final long serialVersionUID = 0L;

      UnmodifiableMultimap(Multimap delegate) {
         this.delegate = (Multimap)Preconditions.checkNotNull(delegate);
      }

      protected Multimap delegate() {
         return this.delegate;
      }

      public void clear() {
         throw new UnsupportedOperationException();
      }

      public Map asMap() {
         Map<K, Collection<V>> result = this.map;
         if(result == null) {
            result = this.map = Collections.unmodifiableMap(Maps.transformValues(this.delegate.asMap(), new Function() {
               public Collection apply(Collection collection) {
                  return Multimaps.unmodifiableValueCollection(collection);
               }
            }));
         }

         return result;
      }

      public Collection entries() {
         Collection<Entry<K, V>> result = this.entries;
         if(result == null) {
            this.entries = result = Multimaps.unmodifiableEntries(this.delegate.entries());
         }

         return result;
      }

      public Collection get(Object key) {
         return Multimaps.unmodifiableValueCollection(this.delegate.get(key));
      }

      public Multiset keys() {
         Multiset<K> result = this.keys;
         if(result == null) {
            this.keys = result = Multisets.unmodifiableMultiset(this.delegate.keys());
         }

         return result;
      }

      public Set keySet() {
         Set<K> result = this.keySet;
         if(result == null) {
            this.keySet = result = Collections.unmodifiableSet(this.delegate.keySet());
         }

         return result;
      }

      public boolean put(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public boolean putAll(Multimap multimap) {
         throw new UnsupportedOperationException();
      }

      public boolean remove(Object key, Object value) {
         throw new UnsupportedOperationException();
      }

      public Collection removeAll(Object key) {
         throw new UnsupportedOperationException();
      }

      public Collection replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public Collection values() {
         Collection<V> result = this.values;
         if(result == null) {
            this.values = result = Collections.unmodifiableCollection(this.delegate.values());
         }

         return result;
      }
   }

   private static class UnmodifiableSetMultimap extends Multimaps.UnmodifiableMultimap implements SetMultimap {
      private static final long serialVersionUID = 0L;

      UnmodifiableSetMultimap(SetMultimap delegate) {
         super(delegate);
      }

      public SetMultimap delegate() {
         return (SetMultimap)super.delegate();
      }

      public Set get(Object key) {
         return Collections.unmodifiableSet(this.delegate().get(key));
      }

      public Set entries() {
         return Maps.unmodifiableEntrySet(this.delegate().entries());
      }

      public Set removeAll(Object key) {
         throw new UnsupportedOperationException();
      }

      public Set replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }
   }

   private static class UnmodifiableSortedSetMultimap extends Multimaps.UnmodifiableSetMultimap implements SortedSetMultimap {
      private static final long serialVersionUID = 0L;

      UnmodifiableSortedSetMultimap(SortedSetMultimap delegate) {
         super(delegate);
      }

      public SortedSetMultimap delegate() {
         return (SortedSetMultimap)super.delegate();
      }

      public SortedSet get(Object key) {
         return Collections.unmodifiableSortedSet(this.delegate().get(key));
      }

      public SortedSet removeAll(Object key) {
         throw new UnsupportedOperationException();
      }

      public SortedSet replaceValues(Object key, Iterable values) {
         throw new UnsupportedOperationException();
      }

      public Comparator valueComparator() {
         return this.delegate().valueComparator();
      }
   }
}
