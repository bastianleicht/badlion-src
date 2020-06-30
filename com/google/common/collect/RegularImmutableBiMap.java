package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableAsList;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.UnmodifiableIterator;
import java.io.Serializable;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
class RegularImmutableBiMap extends ImmutableBiMap {
   static final double MAX_LOAD_FACTOR = 1.2D;
   private final transient ImmutableMapEntry[] keyTable;
   private final transient ImmutableMapEntry[] valueTable;
   private final transient ImmutableMapEntry[] entries;
   private final transient int mask;
   private final transient int hashCode;
   private transient ImmutableBiMap inverse;

   RegularImmutableBiMap(ImmutableMapEntry.TerminalEntry... entriesToAdd) {
      this(entriesToAdd.length, entriesToAdd);
   }

   RegularImmutableBiMap(int n, ImmutableMapEntry.TerminalEntry[] entriesToAdd) {
      int tableSize = Hashing.closedTableSize(n, 1.2D);
      this.mask = tableSize - 1;
      ImmutableMapEntry<K, V>[] keyTable = createEntryArray(tableSize);
      ImmutableMapEntry<K, V>[] valueTable = createEntryArray(tableSize);
      ImmutableMapEntry<K, V>[] entries = createEntryArray(n);
      int hashCode = 0;

      for(int i = 0; i < n; ++i) {
         ImmutableMapEntry.TerminalEntry<K, V> entry = entriesToAdd[i];
         K key = entry.getKey();
         V value = entry.getValue();
         int keyHash = key.hashCode();
         int valueHash = value.hashCode();
         int keyBucket = Hashing.smear(keyHash) & this.mask;
         int valueBucket = Hashing.smear(valueHash) & this.mask;
         ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];

         for(ImmutableMapEntry<K, V> keyEntry = nextInKeyBucket; keyEntry != null; keyEntry = keyEntry.getNextInKeyBucket()) {
            checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
         }

         ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];

         for(ImmutableMapEntry<K, V> valueEntry = nextInValueBucket; valueEntry != null; valueEntry = valueEntry.getNextInValueBucket()) {
            checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
         }

         ImmutableMapEntry<K, V> newEntry = (ImmutableMapEntry)(nextInKeyBucket == null && nextInValueBucket == null?entry:new RegularImmutableBiMap.NonTerminalBiMapEntry(entry, nextInKeyBucket, nextInValueBucket));
         keyTable[keyBucket] = newEntry;
         valueTable[valueBucket] = newEntry;
         entries[i] = newEntry;
         hashCode += keyHash ^ valueHash;
      }

      this.keyTable = keyTable;
      this.valueTable = valueTable;
      this.entries = entries;
      this.hashCode = hashCode;
   }

   RegularImmutableBiMap(Entry[] entriesToAdd) {
      int n = entriesToAdd.length;
      int tableSize = Hashing.closedTableSize(n, 1.2D);
      this.mask = tableSize - 1;
      ImmutableMapEntry<K, V>[] keyTable = createEntryArray(tableSize);
      ImmutableMapEntry<K, V>[] valueTable = createEntryArray(tableSize);
      ImmutableMapEntry<K, V>[] entries = createEntryArray(n);
      int hashCode = 0;

      for(int i = 0; i < n; ++i) {
         Entry<K, V> entry = entriesToAdd[i];
         K key = entry.getKey();
         V value = entry.getValue();
         CollectPreconditions.checkEntryNotNull(key, value);
         int keyHash = key.hashCode();
         int valueHash = value.hashCode();
         int keyBucket = Hashing.smear(keyHash) & this.mask;
         int valueBucket = Hashing.smear(valueHash) & this.mask;
         ImmutableMapEntry<K, V> nextInKeyBucket = keyTable[keyBucket];

         for(ImmutableMapEntry<K, V> keyEntry = nextInKeyBucket; keyEntry != null; keyEntry = keyEntry.getNextInKeyBucket()) {
            checkNoConflict(!key.equals(keyEntry.getKey()), "key", entry, keyEntry);
         }

         ImmutableMapEntry<K, V> nextInValueBucket = valueTable[valueBucket];

         for(ImmutableMapEntry<K, V> valueEntry = nextInValueBucket; valueEntry != null; valueEntry = valueEntry.getNextInValueBucket()) {
            checkNoConflict(!value.equals(valueEntry.getValue()), "value", entry, valueEntry);
         }

         ImmutableMapEntry<K, V> newEntry = (ImmutableMapEntry)(nextInKeyBucket == null && nextInValueBucket == null?new ImmutableMapEntry.TerminalEntry(key, value):new RegularImmutableBiMap.NonTerminalBiMapEntry(key, value, nextInKeyBucket, nextInValueBucket));
         keyTable[keyBucket] = newEntry;
         valueTable[valueBucket] = newEntry;
         entries[i] = newEntry;
         hashCode += keyHash ^ valueHash;
      }

      this.keyTable = keyTable;
      this.valueTable = valueTable;
      this.entries = entries;
      this.hashCode = hashCode;
   }

   private static ImmutableMapEntry[] createEntryArray(int length) {
      return new ImmutableMapEntry[length];
   }

   @Nullable
   public Object get(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int bucket = Hashing.smear(key.hashCode()) & this.mask;

         for(ImmutableMapEntry<K, V> entry = this.keyTable[bucket]; entry != null; entry = entry.getNextInKeyBucket()) {
            if(key.equals(entry.getKey())) {
               return entry.getValue();
            }
         }

         return null;
      }
   }

   ImmutableSet createEntrySet() {
      return new ImmutableMapEntrySet() {
         ImmutableMap map() {
            return RegularImmutableBiMap.this;
         }

         public UnmodifiableIterator iterator() {
            return this.asList().iterator();
         }

         ImmutableList createAsList() {
            return new RegularImmutableAsList(this, RegularImmutableBiMap.this.entries);
         }

         boolean isHashCodeFast() {
            return true;
         }

         public int hashCode() {
            return RegularImmutableBiMap.this.hashCode;
         }
      };
   }

   boolean isPartialView() {
      return false;
   }

   public int size() {
      return this.entries.length;
   }

   public ImmutableBiMap inverse() {
      ImmutableBiMap<V, K> result = this.inverse;
      return result == null?(this.inverse = new RegularImmutableBiMap.Inverse()):result;
   }

   private final class Inverse extends ImmutableBiMap {
      private Inverse() {
      }

      public int size() {
         return this.inverse().size();
      }

      public ImmutableBiMap inverse() {
         return RegularImmutableBiMap.this;
      }

      public Object get(@Nullable Object value) {
         if(value == null) {
            return null;
         } else {
            int bucket = Hashing.smear(value.hashCode()) & RegularImmutableBiMap.this.mask;

            for(ImmutableMapEntry<K, V> entry = RegularImmutableBiMap.this.valueTable[bucket]; entry != null; entry = entry.getNextInValueBucket()) {
               if(value.equals(entry.getValue())) {
                  return entry.getKey();
               }
            }

            return null;
         }
      }

      ImmutableSet createEntrySet() {
         return new RegularImmutableBiMap.Inverse.InverseEntrySet();
      }

      boolean isPartialView() {
         return false;
      }

      Object writeReplace() {
         return new RegularImmutableBiMap.InverseSerializedForm(RegularImmutableBiMap.this);
      }

      final class InverseEntrySet extends ImmutableMapEntrySet {
         ImmutableMap map() {
            return Inverse.this;
         }

         boolean isHashCodeFast() {
            return true;
         }

         public int hashCode() {
            return RegularImmutableBiMap.this.hashCode;
         }

         public UnmodifiableIterator iterator() {
            return this.asList().iterator();
         }

         ImmutableList createAsList() {
            return new ImmutableAsList() {
               public Entry get(int index) {
                  Entry<K, V> entry = RegularImmutableBiMap.this.entries[index];
                  return Maps.immutableEntry(entry.getValue(), entry.getKey());
               }

               ImmutableCollection delegateCollection() {
                  return InverseEntrySet.this;
               }
            };
         }
      }
   }

   private static class InverseSerializedForm implements Serializable {
      private final ImmutableBiMap forward;
      private static final long serialVersionUID = 1L;

      InverseSerializedForm(ImmutableBiMap forward) {
         this.forward = forward;
      }

      Object readResolve() {
         return this.forward.inverse();
      }
   }

   private static final class NonTerminalBiMapEntry extends ImmutableMapEntry {
      @Nullable
      private final ImmutableMapEntry nextInKeyBucket;
      @Nullable
      private final ImmutableMapEntry nextInValueBucket;

      NonTerminalBiMapEntry(Object key, Object value, @Nullable ImmutableMapEntry nextInKeyBucket, @Nullable ImmutableMapEntry nextInValueBucket) {
         super(key, value);
         this.nextInKeyBucket = nextInKeyBucket;
         this.nextInValueBucket = nextInValueBucket;
      }

      NonTerminalBiMapEntry(ImmutableMapEntry contents, @Nullable ImmutableMapEntry nextInKeyBucket, @Nullable ImmutableMapEntry nextInValueBucket) {
         super(contents);
         this.nextInKeyBucket = nextInKeyBucket;
         this.nextInValueBucket = nextInValueBucket;
      }

      @Nullable
      ImmutableMapEntry getNextInKeyBucket() {
         return this.nextInKeyBucket;
      }

      @Nullable
      ImmutableMapEntry getNextInValueBucket() {
         return this.nextInValueBucket;
      }
   }
}
