package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.Constraint;
import com.google.common.collect.Constraints;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ForwardingMapEntry;
import com.google.common.collect.ForwardingMultimap;
import com.google.common.collect.ForwardingSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapConstraint;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.SortedSetMultimap;
import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class MapConstraints {
   public static MapConstraint notNull() {
      return MapConstraints.NotNullMapConstraint.INSTANCE;
   }

   public static Map constrainedMap(Map map, MapConstraint constraint) {
      return new MapConstraints.ConstrainedMap(map, constraint);
   }

   public static Multimap constrainedMultimap(Multimap multimap, MapConstraint constraint) {
      return new MapConstraints.ConstrainedMultimap(multimap, constraint);
   }

   public static ListMultimap constrainedListMultimap(ListMultimap multimap, MapConstraint constraint) {
      return new MapConstraints.ConstrainedListMultimap(multimap, constraint);
   }

   public static SetMultimap constrainedSetMultimap(SetMultimap multimap, MapConstraint constraint) {
      return new MapConstraints.ConstrainedSetMultimap(multimap, constraint);
   }

   public static SortedSetMultimap constrainedSortedSetMultimap(SortedSetMultimap multimap, MapConstraint constraint) {
      return new MapConstraints.ConstrainedSortedSetMultimap(multimap, constraint);
   }

   private static Entry constrainedEntry(final Entry entry, final MapConstraint constraint) {
      Preconditions.checkNotNull(entry);
      Preconditions.checkNotNull(constraint);
      return new ForwardingMapEntry() {
         protected Entry delegate() {
            return entry;
         }

         public Object setValue(Object value) {
            constraint.checkKeyValue(this.getKey(), value);
            return entry.setValue(value);
         }
      };
   }

   private static Entry constrainedAsMapEntry(final Entry entry, final MapConstraint constraint) {
      Preconditions.checkNotNull(entry);
      Preconditions.checkNotNull(constraint);
      return new ForwardingMapEntry() {
         protected Entry delegate() {
            return entry;
         }

         public Collection getValue() {
            return Constraints.constrainedTypePreservingCollection((Collection)entry.getValue(), new Constraint() {
               public Object checkElement(Object value) {
                  constraint.checkKeyValue(getKey(), value);
                  return value;
               }
            });
         }
      };
   }

   private static Set constrainedAsMapEntries(Set entries, MapConstraint constraint) {
      return new MapConstraints.ConstrainedAsMapEntries(entries, constraint);
   }

   private static Collection constrainedEntries(Collection entries, MapConstraint constraint) {
      return (Collection)(entries instanceof Set?constrainedEntrySet((Set)entries, constraint):new MapConstraints.ConstrainedEntries(entries, constraint));
   }

   private static Set constrainedEntrySet(Set entries, MapConstraint constraint) {
      return new MapConstraints.ConstrainedEntrySet(entries, constraint);
   }

   public static BiMap constrainedBiMap(BiMap map, MapConstraint constraint) {
      return new MapConstraints.ConstrainedBiMap(map, (BiMap)null, constraint);
   }

   private static Collection checkValues(Object key, Iterable values, MapConstraint constraint) {
      Collection<V> copy = Lists.newArrayList(values);

      for(V value : copy) {
         constraint.checkKeyValue(key, value);
      }

      return copy;
   }

   private static Map checkMap(Map map, MapConstraint constraint) {
      Map<K, V> copy = new LinkedHashMap(map);

      for(Entry<K, V> entry : copy.entrySet()) {
         constraint.checkKeyValue(entry.getKey(), entry.getValue());
      }

      return copy;
   }

   static class ConstrainedAsMapEntries extends ForwardingSet {
      private final MapConstraint constraint;
      private final Set entries;

      ConstrainedAsMapEntries(Set entries, MapConstraint constraint) {
         this.entries = entries;
         this.constraint = constraint;
      }

      protected Set delegate() {
         return this.entries;
      }

      public Iterator iterator() {
         final Iterator<Entry<K, Collection<V>>> iterator = this.entries.iterator();
         return new ForwardingIterator() {
            public Entry next() {
               return MapConstraints.constrainedAsMapEntry((Entry)iterator.next(), ConstrainedAsMapEntries.this.constraint);
            }

            protected Iterator delegate() {
               return iterator;
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public boolean contains(Object o) {
         return Maps.containsEntryImpl(this.delegate(), o);
      }

      public boolean containsAll(Collection c) {
         return this.standardContainsAll(c);
      }

      public boolean equals(@Nullable Object object) {
         return this.standardEquals(object);
      }

      public int hashCode() {
         return this.standardHashCode();
      }

      public boolean remove(Object o) {
         return Maps.removeEntryImpl(this.delegate(), o);
      }

      public boolean removeAll(Collection c) {
         return this.standardRemoveAll(c);
      }

      public boolean retainAll(Collection c) {
         return this.standardRetainAll(c);
      }
   }

   private static class ConstrainedAsMapValues extends ForwardingCollection {
      final Collection delegate;
      final Set entrySet;

      ConstrainedAsMapValues(Collection delegate, Set entrySet) {
         this.delegate = delegate;
         this.entrySet = entrySet;
      }

      protected Collection delegate() {
         return this.delegate;
      }

      public Iterator iterator() {
         final Iterator<Entry<K, Collection<V>>> iterator = this.entrySet.iterator();
         return new Iterator() {
            public boolean hasNext() {
               return iterator.hasNext();
            }

            public Collection next() {
               return (Collection)((Entry)iterator.next()).getValue();
            }

            public void remove() {
               iterator.remove();
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public boolean contains(Object o) {
         return this.standardContains(o);
      }

      public boolean containsAll(Collection c) {
         return this.standardContainsAll(c);
      }

      public boolean remove(Object o) {
         return this.standardRemove(o);
      }

      public boolean removeAll(Collection c) {
         return this.standardRemoveAll(c);
      }

      public boolean retainAll(Collection c) {
         return this.standardRetainAll(c);
      }
   }

   private static class ConstrainedBiMap extends MapConstraints.ConstrainedMap implements BiMap {
      volatile BiMap inverse;

      ConstrainedBiMap(BiMap delegate, @Nullable BiMap inverse, MapConstraint constraint) {
         super(delegate, constraint);
         this.inverse = inverse;
      }

      protected BiMap delegate() {
         return (BiMap)super.delegate();
      }

      public Object forcePut(Object key, Object value) {
         this.constraint.checkKeyValue(key, value);
         return this.delegate().forcePut(key, value);
      }

      public BiMap inverse() {
         if(this.inverse == null) {
            this.inverse = new MapConstraints.ConstrainedBiMap(this.delegate().inverse(), this, new MapConstraints.InverseConstraint(this.constraint));
         }

         return this.inverse;
      }

      public Set values() {
         return this.delegate().values();
      }
   }

   private static class ConstrainedEntries extends ForwardingCollection {
      final MapConstraint constraint;
      final Collection entries;

      ConstrainedEntries(Collection entries, MapConstraint constraint) {
         this.entries = entries;
         this.constraint = constraint;
      }

      protected Collection delegate() {
         return this.entries;
      }

      public Iterator iterator() {
         final Iterator<Entry<K, V>> iterator = this.entries.iterator();
         return new ForwardingIterator() {
            public Entry next() {
               return MapConstraints.constrainedEntry((Entry)iterator.next(), ConstrainedEntries.this.constraint);
            }

            protected Iterator delegate() {
               return iterator;
            }
         };
      }

      public Object[] toArray() {
         return this.standardToArray();
      }

      public Object[] toArray(Object[] array) {
         return this.standardToArray(array);
      }

      public boolean contains(Object o) {
         return Maps.containsEntryImpl(this.delegate(), o);
      }

      public boolean containsAll(Collection c) {
         return this.standardContainsAll(c);
      }

      public boolean remove(Object o) {
         return Maps.removeEntryImpl(this.delegate(), o);
      }

      public boolean removeAll(Collection c) {
         return this.standardRemoveAll(c);
      }

      public boolean retainAll(Collection c) {
         return this.standardRetainAll(c);
      }
   }

   static class ConstrainedEntrySet extends MapConstraints.ConstrainedEntries implements Set {
      ConstrainedEntrySet(Set entries, MapConstraint constraint) {
         super(entries, constraint);
      }

      public boolean equals(@Nullable Object object) {
         return Sets.equalsImpl(this, object);
      }

      public int hashCode() {
         return Sets.hashCodeImpl(this);
      }
   }

   private static class ConstrainedListMultimap extends MapConstraints.ConstrainedMultimap implements ListMultimap {
      ConstrainedListMultimap(ListMultimap delegate, MapConstraint constraint) {
         super(delegate, constraint);
      }

      public List get(Object key) {
         return (List)super.get(key);
      }

      public List removeAll(Object key) {
         return (List)super.removeAll(key);
      }

      public List replaceValues(Object key, Iterable values) {
         return (List)super.replaceValues(key, values);
      }
   }

   static class ConstrainedMap extends ForwardingMap {
      private final Map delegate;
      final MapConstraint constraint;
      private transient Set entrySet;

      ConstrainedMap(Map delegate, MapConstraint constraint) {
         this.delegate = (Map)Preconditions.checkNotNull(delegate);
         this.constraint = (MapConstraint)Preconditions.checkNotNull(constraint);
      }

      protected Map delegate() {
         return this.delegate;
      }

      public Set entrySet() {
         Set<Entry<K, V>> result = this.entrySet;
         if(result == null) {
            this.entrySet = result = MapConstraints.constrainedEntrySet(this.delegate.entrySet(), this.constraint);
         }

         return result;
      }

      public Object put(Object key, Object value) {
         this.constraint.checkKeyValue(key, value);
         return this.delegate.put(key, value);
      }

      public void putAll(Map map) {
         this.delegate.putAll(MapConstraints.checkMap(map, this.constraint));
      }
   }

   private static class ConstrainedMultimap extends ForwardingMultimap implements Serializable {
      final MapConstraint constraint;
      final Multimap delegate;
      transient Collection entries;
      transient Map asMap;

      public ConstrainedMultimap(Multimap delegate, MapConstraint constraint) {
         this.delegate = (Multimap)Preconditions.checkNotNull(delegate);
         this.constraint = (MapConstraint)Preconditions.checkNotNull(constraint);
      }

      protected Multimap delegate() {
         return this.delegate;
      }

      public Map asMap() {
         Map<K, Collection<V>> result = this.asMap;
         if(result == null) {
            final Map<K, Collection<V>> asMapDelegate = this.delegate.asMap();
            this.asMap = result = new ForwardingMap() {
               Set entrySet;
               Collection values;

               protected Map delegate() {
                  return asMapDelegate;
               }

               public Set entrySet() {
                  Set<Entry<K, Collection<V>>> result = this.entrySet;
                  if(result == null) {
                     this.entrySet = result = MapConstraints.constrainedAsMapEntries(asMapDelegate.entrySet(), ConstrainedMultimap.this.constraint);
                  }

                  return result;
               }

               public Collection get(Object key) {
                  try {
                     Collection<V> collection = ConstrainedMultimap.this.get(key);
                     return collection.isEmpty()?null:collection;
                  } catch (ClassCastException var3) {
                     return null;
                  }
               }

               public Collection values() {
                  Collection<Collection<V>> result = this.values;
                  if(result == null) {
                     this.values = result = new MapConstraints.ConstrainedAsMapValues(this.delegate().values(), this.entrySet());
                  }

                  return result;
               }

               public boolean containsValue(Object o) {
                  return this.values().contains(o);
               }
            };
         }

         return result;
      }

      public Collection entries() {
         Collection<Entry<K, V>> result = this.entries;
         if(result == null) {
            this.entries = result = MapConstraints.constrainedEntries(this.delegate.entries(), this.constraint);
         }

         return result;
      }

      public Collection get(final Object key) {
         return Constraints.constrainedTypePreservingCollection(this.delegate.get(key), new Constraint() {
            public Object checkElement(Object value) {
               ConstrainedMultimap.this.constraint.checkKeyValue(key, value);
               return value;
            }
         });
      }

      public boolean put(Object key, Object value) {
         this.constraint.checkKeyValue(key, value);
         return this.delegate.put(key, value);
      }

      public boolean putAll(Object key, Iterable values) {
         return this.delegate.putAll(key, MapConstraints.checkValues(key, values, this.constraint));
      }

      public boolean putAll(Multimap multimap) {
         boolean changed = false;

         for(Entry<? extends K, ? extends V> entry : multimap.entries()) {
            changed |= this.put(entry.getKey(), entry.getValue());
         }

         return changed;
      }

      public Collection replaceValues(Object key, Iterable values) {
         return this.delegate.replaceValues(key, MapConstraints.checkValues(key, values, this.constraint));
      }
   }

   private static class ConstrainedSetMultimap extends MapConstraints.ConstrainedMultimap implements SetMultimap {
      ConstrainedSetMultimap(SetMultimap delegate, MapConstraint constraint) {
         super(delegate, constraint);
      }

      public Set get(Object key) {
         return (Set)super.get(key);
      }

      public Set entries() {
         return (Set)super.entries();
      }

      public Set removeAll(Object key) {
         return (Set)super.removeAll(key);
      }

      public Set replaceValues(Object key, Iterable values) {
         return (Set)super.replaceValues(key, values);
      }
   }

   private static class ConstrainedSortedSetMultimap extends MapConstraints.ConstrainedSetMultimap implements SortedSetMultimap {
      ConstrainedSortedSetMultimap(SortedSetMultimap delegate, MapConstraint constraint) {
         super(delegate, constraint);
      }

      public SortedSet get(Object key) {
         return (SortedSet)super.get(key);
      }

      public SortedSet removeAll(Object key) {
         return (SortedSet)super.removeAll(key);
      }

      public SortedSet replaceValues(Object key, Iterable values) {
         return (SortedSet)super.replaceValues(key, values);
      }

      public Comparator valueComparator() {
         return ((SortedSetMultimap)this.delegate()).valueComparator();
      }
   }

   private static class InverseConstraint implements MapConstraint {
      final MapConstraint constraint;

      public InverseConstraint(MapConstraint constraint) {
         this.constraint = (MapConstraint)Preconditions.checkNotNull(constraint);
      }

      public void checkKeyValue(Object key, Object value) {
         this.constraint.checkKeyValue(value, key);
      }
   }

   private static enum NotNullMapConstraint implements MapConstraint {
      INSTANCE;

      public void checkKeyValue(Object key, Object value) {
         Preconditions.checkNotNull(key);
         Preconditions.checkNotNull(value);
      }

      public String toString() {
         return "Not null";
      }
   }
}
