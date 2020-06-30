package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.Ordering;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.SortedSetMultimap;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

@Beta
@GwtCompatible
public abstract class MultimapBuilder {
   private static final int DEFAULT_EXPECTED_KEYS = 8;

   private MultimapBuilder() {
   }

   public static MultimapBuilder.MultimapBuilderWithKeys hashKeys() {
      return hashKeys(8);
   }

   public static MultimapBuilder.MultimapBuilderWithKeys hashKeys(final int expectedKeys) {
      CollectPreconditions.checkNonnegative(expectedKeys, "expectedKeys");
      return new MultimapBuilder.MultimapBuilderWithKeys() {
         Map createMap() {
            return new HashMap(expectedKeys);
         }
      };
   }

   public static MultimapBuilder.MultimapBuilderWithKeys linkedHashKeys() {
      return linkedHashKeys(8);
   }

   public static MultimapBuilder.MultimapBuilderWithKeys linkedHashKeys(final int expectedKeys) {
      CollectPreconditions.checkNonnegative(expectedKeys, "expectedKeys");
      return new MultimapBuilder.MultimapBuilderWithKeys() {
         Map createMap() {
            return new LinkedHashMap(expectedKeys);
         }
      };
   }

   public static MultimapBuilder.MultimapBuilderWithKeys treeKeys() {
      return treeKeys(Ordering.natural());
   }

   public static MultimapBuilder.MultimapBuilderWithKeys treeKeys(final Comparator comparator) {
      Preconditions.checkNotNull(comparator);
      return new MultimapBuilder.MultimapBuilderWithKeys() {
         Map createMap() {
            return new TreeMap(comparator);
         }
      };
   }

   public static MultimapBuilder.MultimapBuilderWithKeys enumKeys(final Class keyClass) {
      Preconditions.checkNotNull(keyClass);
      return new MultimapBuilder.MultimapBuilderWithKeys() {
         Map createMap() {
            return new EnumMap(keyClass);
         }
      };
   }

   public abstract Multimap build();

   public Multimap build(Multimap multimap) {
      Multimap<K, V> result = this.build();
      result.putAll(multimap);
      return result;
   }

   private static final class ArrayListSupplier implements Supplier, Serializable {
      private final int expectedValuesPerKey;

      ArrayListSupplier(int expectedValuesPerKey) {
         this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
      }

      public List get() {
         return new ArrayList(this.expectedValuesPerKey);
      }
   }

   private static final class EnumSetSupplier implements Supplier, Serializable {
      private final Class clazz;

      EnumSetSupplier(Class clazz) {
         this.clazz = (Class)Preconditions.checkNotNull(clazz);
      }

      public Set get() {
         return EnumSet.noneOf(this.clazz);
      }
   }

   private static final class HashSetSupplier implements Supplier, Serializable {
      private final int expectedValuesPerKey;

      HashSetSupplier(int expectedValuesPerKey) {
         this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
      }

      public Set get() {
         return new HashSet(this.expectedValuesPerKey);
      }
   }

   private static final class LinkedHashSetSupplier implements Supplier, Serializable {
      private final int expectedValuesPerKey;

      LinkedHashSetSupplier(int expectedValuesPerKey) {
         this.expectedValuesPerKey = CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
      }

      public Set get() {
         return new LinkedHashSet(this.expectedValuesPerKey);
      }
   }

   private static enum LinkedListSupplier implements Supplier {
      INSTANCE;

      public static Supplier instance() {
         Supplier<List<V>> result = INSTANCE;
         return result;
      }

      public List get() {
         return new LinkedList();
      }
   }

   public abstract static class ListMultimapBuilder extends MultimapBuilder {
      ListMultimapBuilder() {
         super(null);
      }

      public abstract ListMultimap build();

      public ListMultimap build(Multimap multimap) {
         return (ListMultimap)super.build(multimap);
      }
   }

   public abstract static class MultimapBuilderWithKeys {
      private static final int DEFAULT_EXPECTED_VALUES_PER_KEY = 2;

      abstract Map createMap();

      public MultimapBuilder.ListMultimapBuilder arrayListValues() {
         return this.arrayListValues(2);
      }

      public MultimapBuilder.ListMultimapBuilder arrayListValues(final int expectedValuesPerKey) {
         CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
         return new MultimapBuilder.ListMultimapBuilder() {
            public ListMultimap build() {
               return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.ArrayListSupplier(expectedValuesPerKey));
            }
         };
      }

      public MultimapBuilder.ListMultimapBuilder linkedListValues() {
         return new MultimapBuilder.ListMultimapBuilder() {
            public ListMultimap build() {
               return Multimaps.newListMultimap(MultimapBuilderWithKeys.this.createMap(), MultimapBuilder.LinkedListSupplier.instance());
            }
         };
      }

      public MultimapBuilder.SetMultimapBuilder hashSetValues() {
         return this.hashSetValues(2);
      }

      public MultimapBuilder.SetMultimapBuilder hashSetValues(final int expectedValuesPerKey) {
         CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
         return new MultimapBuilder.SetMultimapBuilder() {
            public SetMultimap build() {
               return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.HashSetSupplier(expectedValuesPerKey));
            }
         };
      }

      public MultimapBuilder.SetMultimapBuilder linkedHashSetValues() {
         return this.linkedHashSetValues(2);
      }

      public MultimapBuilder.SetMultimapBuilder linkedHashSetValues(final int expectedValuesPerKey) {
         CollectPreconditions.checkNonnegative(expectedValuesPerKey, "expectedValuesPerKey");
         return new MultimapBuilder.SetMultimapBuilder() {
            public SetMultimap build() {
               return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.LinkedHashSetSupplier(expectedValuesPerKey));
            }
         };
      }

      public MultimapBuilder.SortedSetMultimapBuilder treeSetValues() {
         return this.treeSetValues(Ordering.natural());
      }

      public MultimapBuilder.SortedSetMultimapBuilder treeSetValues(final Comparator comparator) {
         Preconditions.checkNotNull(comparator, "comparator");
         return new MultimapBuilder.SortedSetMultimapBuilder() {
            public SortedSetMultimap build() {
               return Multimaps.newSortedSetMultimap(MultimapBuilderWithKeys.this.createMap(), new MultimapBuilder.TreeSetSupplier(comparator));
            }
         };
      }

      public MultimapBuilder.SetMultimapBuilder enumSetValues(final Class valueClass) {
         Preconditions.checkNotNull(valueClass, "valueClass");
         return new MultimapBuilder.SetMultimapBuilder() {
            public SetMultimap build() {
               Supplier<Set<V>> factory = new MultimapBuilder.EnumSetSupplier(valueClass);
               return Multimaps.newSetMultimap(MultimapBuilderWithKeys.this.createMap(), factory);
            }
         };
      }
   }

   public abstract static class SetMultimapBuilder extends MultimapBuilder {
      SetMultimapBuilder() {
         super(null);
      }

      public abstract SetMultimap build();

      public SetMultimap build(Multimap multimap) {
         return (SetMultimap)super.build(multimap);
      }
   }

   public abstract static class SortedSetMultimapBuilder extends MultimapBuilder.SetMultimapBuilder {
      public abstract SortedSetMultimap build();

      public SortedSetMultimap build(Multimap multimap) {
         return (SortedSetMultimap)super.build(multimap);
      }
   }

   private static final class TreeSetSupplier implements Supplier, Serializable {
      private final Comparator comparator;

      TreeSetSupplier(Comparator comparator) {
         this.comparator = (Comparator)Preconditions.checkNotNull(comparator);
      }

      public SortedSet get() {
         return new TreeSet(this.comparator);
      }
   }
}
