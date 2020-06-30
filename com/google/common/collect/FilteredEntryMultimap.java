package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.AbstractMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Collections2;
import com.google.common.collect.FilteredMultimap;
import com.google.common.collect.FilteredMultimapValues;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible
class FilteredEntryMultimap extends AbstractMultimap implements FilteredMultimap {
   final Multimap unfiltered;
   final Predicate predicate;

   FilteredEntryMultimap(Multimap unfiltered, Predicate predicate) {
      this.unfiltered = (Multimap)Preconditions.checkNotNull(unfiltered);
      this.predicate = (Predicate)Preconditions.checkNotNull(predicate);
   }

   public Multimap unfiltered() {
      return this.unfiltered;
   }

   public Predicate entryPredicate() {
      return this.predicate;
   }

   public int size() {
      return this.entries().size();
   }

   private boolean satisfies(Object key, Object value) {
      return this.predicate.apply(Maps.immutableEntry(key, value));
   }

   static Collection filterCollection(Collection collection, Predicate predicate) {
      return (Collection)(collection instanceof Set?Sets.filter((Set)collection, predicate):Collections2.filter(collection, predicate));
   }

   public boolean containsKey(@Nullable Object key) {
      return this.asMap().get(key) != null;
   }

   public Collection removeAll(@Nullable Object key) {
      return (Collection)Objects.firstNonNull(this.asMap().remove(key), this.unmodifiableEmptyCollection());
   }

   Collection unmodifiableEmptyCollection() {
      return (Collection)(this.unfiltered instanceof SetMultimap?Collections.emptySet():Collections.emptyList());
   }

   public void clear() {
      this.entries().clear();
   }

   public Collection get(Object key) {
      return filterCollection(this.unfiltered.get(key), new FilteredEntryMultimap.ValuePredicate(key));
   }

   Collection createEntries() {
      return filterCollection(this.unfiltered.entries(), this.predicate);
   }

   Collection createValues() {
      return new FilteredMultimapValues(this);
   }

   Iterator entryIterator() {
      throw new AssertionError("should never be called");
   }

   Map createAsMap() {
      return new FilteredEntryMultimap.AsMap();
   }

   public Set keySet() {
      return this.asMap().keySet();
   }

   boolean removeEntriesIf(Predicate predicate) {
      Iterator<Entry<K, Collection<V>>> entryIterator = this.unfiltered.asMap().entrySet().iterator();
      boolean changed = false;

      while(entryIterator.hasNext()) {
         Entry<K, Collection<V>> entry = (Entry)entryIterator.next();
         K key = entry.getKey();
         Collection<V> collection = filterCollection((Collection)entry.getValue(), new FilteredEntryMultimap.ValuePredicate(key));
         if(!collection.isEmpty() && predicate.apply(Maps.immutableEntry(key, collection))) {
            if(collection.size() == ((Collection)entry.getValue()).size()) {
               entryIterator.remove();
            } else {
               collection.clear();
            }

            changed = true;
         }
      }

      return changed;
   }

   Multiset createKeys() {
      return new FilteredEntryMultimap.Keys();
   }

   class AsMap extends Maps.ImprovedAbstractMap {
      public boolean containsKey(@Nullable Object key) {
         return this.get(key) != null;
      }

      public void clear() {
         FilteredEntryMultimap.this.clear();
      }

      public Collection get(@Nullable Object key) {
         Collection<V> result = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
         if(result == null) {
            return null;
         } else {
            result = FilteredEntryMultimap.filterCollection(result, FilteredEntryMultimap.this.new ValuePredicate(key));
            return result.isEmpty()?null:result;
         }
      }

      public Collection remove(@Nullable Object key) {
         Collection<V> collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
         if(collection == null) {
            return null;
         } else {
            K k = key;
            List<V> result = Lists.newArrayList();
            Iterator<V> itr = collection.iterator();

            while(itr.hasNext()) {
               V v = itr.next();
               if(FilteredEntryMultimap.this.satisfies(k, v)) {
                  itr.remove();
                  result.add(v);
               }
            }

            if(result.isEmpty()) {
               return null;
            } else if(FilteredEntryMultimap.this.unfiltered instanceof SetMultimap) {
               return Collections.unmodifiableSet(Sets.newLinkedHashSet(result));
            } else {
               return Collections.unmodifiableList(result);
            }
         }
      }

      Set createKeySet() {
         return new Maps.KeySet(this) {
            public boolean removeAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.in(c)));
            }

            public boolean retainAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.keyPredicateOnEntries(Predicates.not(Predicates.in(c))));
            }

            public boolean remove(@Nullable Object o) {
               return AsMap.this.remove(o) != null;
            }
         };
      }

      Set createEntrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return AsMap.this;
            }

            public Iterator iterator() {
               return new AbstractIterator() {
                  final Iterator backingIterator;

                  {
                     this.backingIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();
                  }

                  protected Entry computeNext() {
                     while(true) {
                        if(this.backingIterator.hasNext()) {
                           Entry<K, Collection<V>> entry = (Entry)this.backingIterator.next();
                           K key = entry.getKey();
                           Collection<V> collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), FilteredEntryMultimap.this.new ValuePredicate(key));
                           if(collection.isEmpty()) {
                              continue;
                           }

                           return Maps.immutableEntry(key, collection);
                        }

                        return (Entry)this.endOfData();
                     }
                  }
               };
            }

            public boolean removeAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.in(c));
            }

            public boolean retainAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Predicates.not(Predicates.in(c)));
            }

            public int size() {
               return Iterators.size(this.iterator());
            }
         };
      }

      Collection createValues() {
         return new Maps.Values(this) {
            public boolean remove(@Nullable Object o) {
               if(o instanceof Collection) {
                  Collection<?> c = (Collection)o;
                  Iterator<Entry<K, Collection<V>>> entryIterator = FilteredEntryMultimap.this.unfiltered.asMap().entrySet().iterator();

                  while(entryIterator.hasNext()) {
                     Entry<K, Collection<V>> entry = (Entry)entryIterator.next();
                     K key = entry.getKey();
                     Collection<V> collection = FilteredEntryMultimap.filterCollection((Collection)entry.getValue(), FilteredEntryMultimap.this.new ValuePredicate(key));
                     if(!collection.isEmpty() && c.equals(collection)) {
                        if(collection.size() == ((Collection)entry.getValue()).size()) {
                           entryIterator.remove();
                        } else {
                           collection.clear();
                        }

                        return true;
                     }
                  }
               }

               return false;
            }

            public boolean removeAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.in(c)));
            }

            public boolean retainAll(Collection c) {
               return FilteredEntryMultimap.this.removeEntriesIf(Maps.valuePredicateOnEntries(Predicates.not(Predicates.in(c))));
            }
         };
      }
   }

   class Keys extends Multimaps.Keys {
      Keys() {
         super(FilteredEntryMultimap.this);
      }

      public int remove(@Nullable Object key, int occurrences) {
         CollectPreconditions.checkNonnegative(occurrences, "occurrences");
         if(occurrences == 0) {
            return this.count(key);
         } else {
            Collection<V> collection = (Collection)FilteredEntryMultimap.this.unfiltered.asMap().get(key);
            if(collection == null) {
               return 0;
            } else {
               K k = key;
               int oldCount = 0;
               Iterator<V> itr = collection.iterator();

               while(itr.hasNext()) {
                  V v = itr.next();
                  if(FilteredEntryMultimap.this.satisfies(k, v)) {
                     ++oldCount;
                     if(oldCount <= occurrences) {
                        itr.remove();
                     }
                  }
               }

               return oldCount;
            }
         }
      }

      public Set entrySet() {
         return new Multisets.EntrySet() {
            Multiset multiset() {
               return Keys.this;
            }

            public Iterator iterator() {
               return Keys.this.entryIterator();
            }

            public int size() {
               return FilteredEntryMultimap.this.keySet().size();
            }

            private boolean removeEntriesIf(final Predicate predicate) {
               return FilteredEntryMultimap.this.removeEntriesIf(new Predicate() {
                  public boolean apply(Entry entry) {
                     return predicate.apply(Multisets.immutableEntry(entry.getKey(), ((Collection)entry.getValue()).size()));
                  }
               });
            }

            public boolean removeAll(Collection c) {
               return this.removeEntriesIf(Predicates.in(c));
            }

            public boolean retainAll(Collection c) {
               return this.removeEntriesIf(Predicates.not(Predicates.in(c)));
            }
         };
      }
   }

   final class ValuePredicate implements Predicate {
      private final Object key;

      ValuePredicate(Object key) {
         this.key = key;
      }

      public boolean apply(@Nullable Object value) {
         return FilteredEntryMultimap.this.satisfies(this.key, value);
      }
   }
}
