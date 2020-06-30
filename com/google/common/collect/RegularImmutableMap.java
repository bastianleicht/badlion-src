package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMapEntry;
import com.google.common.collect.ImmutableMapEntrySet;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.RegularImmutableAsList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
final class RegularImmutableMap extends ImmutableMap {
   private final transient ImmutableMapEntry[] entries;
   private final transient ImmutableMapEntry[] table;
   private final transient int mask;
   private static final double MAX_LOAD_FACTOR = 1.2D;
   private static final long serialVersionUID = 0L;

   RegularImmutableMap(ImmutableMapEntry.TerminalEntry... theEntries) {
      this(theEntries.length, theEntries);
   }

   RegularImmutableMap(int size, ImmutableMapEntry.TerminalEntry[] theEntries) {
      this.entries = this.createEntryArray(size);
      int tableSize = Hashing.closedTableSize(size, 1.2D);
      this.table = this.createEntryArray(tableSize);
      this.mask = tableSize - 1;

      for(int entryIndex = 0; entryIndex < size; ++entryIndex) {
         ImmutableMapEntry.TerminalEntry<K, V> entry = theEntries[entryIndex];
         K key = entry.getKey();
         int tableIndex = Hashing.smear(key.hashCode()) & this.mask;
         ImmutableMapEntry<K, V> existing = this.table[tableIndex];
         ImmutableMapEntry<K, V> newEntry = (ImmutableMapEntry)(existing == null?entry:new RegularImmutableMap.NonTerminalMapEntry(entry, existing));
         this.table[tableIndex] = newEntry;
         this.entries[entryIndex] = newEntry;
         this.checkNoConflictInBucket(key, newEntry, existing);
      }

   }

   RegularImmutableMap(Entry[] theEntries) {
      int size = theEntries.length;
      this.entries = this.createEntryArray(size);
      int tableSize = Hashing.closedTableSize(size, 1.2D);
      this.table = this.createEntryArray(tableSize);
      this.mask = tableSize - 1;

      for(int entryIndex = 0; entryIndex < size; ++entryIndex) {
         Entry<K, V> entry = theEntries[entryIndex];
         K key = entry.getKey();
         V value = entry.getValue();
         CollectPreconditions.checkEntryNotNull(key, value);
         int tableIndex = Hashing.smear(key.hashCode()) & this.mask;
         ImmutableMapEntry<K, V> existing = this.table[tableIndex];
         ImmutableMapEntry<K, V> newEntry = (ImmutableMapEntry)(existing == null?new ImmutableMapEntry.TerminalEntry(key, value):new RegularImmutableMap.NonTerminalMapEntry(key, value, existing));
         this.table[tableIndex] = newEntry;
         this.entries[entryIndex] = newEntry;
         this.checkNoConflictInBucket(key, newEntry, existing);
      }

   }

   private void checkNoConflictInBucket(Object key, ImmutableMapEntry entry, ImmutableMapEntry bucketHead) {
      while(bucketHead != null) {
         checkNoConflict(!key.equals(bucketHead.getKey()), "key", entry, bucketHead);
         bucketHead = bucketHead.getNextInKeyBucket();
      }

   }

   private ImmutableMapEntry[] createEntryArray(int size) {
      return new ImmutableMapEntry[size];
   }

   public Object get(@Nullable Object key) {
      if(key == null) {
         return null;
      } else {
         int index = Hashing.smear(key.hashCode()) & this.mask;

         for(ImmutableMapEntry<K, V> entry = this.table[index]; entry != null; entry = entry.getNextInKeyBucket()) {
            K candidateKey = entry.getKey();
            if(key.equals(candidateKey)) {
               return entry.getValue();
            }
         }

         return null;
      }
   }

   public int size() {
      return this.entries.length;
   }

   boolean isPartialView() {
      return false;
   }

   ImmutableSet createEntrySet() {
      return new RegularImmutableMap.EntrySet();
   }

   private class EntrySet extends ImmutableMapEntrySet {
      private EntrySet() {
      }

      ImmutableMap map() {
         return RegularImmutableMap.this;
      }

      public UnmodifiableIterator iterator() {
         return this.asList().iterator();
      }

      ImmutableList createAsList() {
         return new RegularImmutableAsList(this, RegularImmutableMap.this.entries);
      }
   }

   private static final class NonTerminalMapEntry extends ImmutableMapEntry {
      private final ImmutableMapEntry nextInKeyBucket;

      NonTerminalMapEntry(Object key, Object value, ImmutableMapEntry nextInKeyBucket) {
         super(key, value);
         this.nextInKeyBucket = nextInKeyBucket;
      }

      NonTerminalMapEntry(ImmutableMapEntry contents, ImmutableMapEntry nextInKeyBucket) {
         super(contents);
         this.nextInKeyBucket = nextInKeyBucket;
      }

      ImmutableMapEntry getNextInKeyBucket() {
         return this.nextInKeyBucket;
      }

      @Nullable
      ImmutableMapEntry getNextInValueBucket() {
         return null;
      }
   }
}
