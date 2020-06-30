package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.AbstractSetMultimap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   serializable = true,
   emulated = true
)
public final class LinkedHashMultimap extends AbstractSetMultimap {
   private static final int DEFAULT_KEY_CAPACITY = 16;
   private static final int DEFAULT_VALUE_SET_CAPACITY = 2;
   @VisibleForTesting
   static final double VALUE_SET_LOAD_FACTOR = 1.0D;
   @VisibleForTesting
   transient int valueSetCapacity = 2;
   private transient LinkedHashMultimap.ValueEntry multimapHeaderEntry;
   @GwtIncompatible("java serialization not supported")
   private static final long serialVersionUID = 1L;

   public static LinkedHashMultimap create() {
      return new LinkedHashMultimap(16, 2);
   }

   public static LinkedHashMultimap create(int expectedKeys, int expectedValuesPerKey) {
      return new LinkedHashMultimap(Maps.capacity(expectedKeys), Maps.capacity(expectedValuesPerKey));
   }

   public static LinkedHashMultimap create(Multimap multimap) {
      LinkedHashMultimap<K, V> result = create(multimap.keySet().size(), 2);
      result.putAll(multimap);
      return result;
   }

   private static void succeedsInValueSet(LinkedHashMultimap.ValueSetLink pred, LinkedHashMultimap.ValueSetLink succ) {
      pred.setSuccessorInValueSet(succ);
      succ.setPredecessorInValueSet(pred);
   }

   private static void succeedsInMultimap(LinkedHashMultimap.ValueEntry pred, LinkedHashMultimap.ValueEntry succ) {
      pred.setSuccessorInMultimap(succ);
      succ.setPredecessorInMultimap(pred);
   }

   private static void deleteFromValueSet(LinkedHashMultimap.ValueSetLink entry) {
      succeedsInValueSet(entry.getPredecessorInValueSet(), entry.getSuccessorInValueSet());
   }

   private static void deleteFromMultimap(LinkedHashMultimap.ValueEntry entry) {
      succeedsInMultimap(entry.getPredecessorInMultimap(), entry.getSuccessorInMultimap());
   }

   private LinkedHashMultimap(int keyCapacity, int valueSetCapacity) {
      super(new LinkedHashMap(keyCapacity));
      CollectPreconditions.checkNonnegative(valueSetCapacity, "expectedValuesPerKey");
      this.valueSetCapacity = valueSetCapacity;
      this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry((Object)null, (Object)null, 0, (LinkedHashMultimap.ValueEntry)null);
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   Set createCollection() {
      return new LinkedHashSet(this.valueSetCapacity);
   }

   Collection createCollection(Object key) {
      return new LinkedHashMultimap.ValueSet(key, this.valueSetCapacity);
   }

   public Set replaceValues(@Nullable Object key, Iterable values) {
      return super.replaceValues(key, values);
   }

   public Set entries() {
      return super.entries();
   }

   public Collection values() {
      return super.values();
   }

   Iterator entryIterator() {
      return new Iterator() {
         LinkedHashMultimap.ValueEntry nextEntry;
         LinkedHashMultimap.ValueEntry toRemove;

         {
            this.nextEntry = LinkedHashMultimap.this.multimapHeaderEntry.successorInMultimap;
         }

         public boolean hasNext() {
            return this.nextEntry != LinkedHashMultimap.this.multimapHeaderEntry;
         }

         public Entry next() {
            if(!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               LinkedHashMultimap.ValueEntry<K, V> result = this.nextEntry;
               this.toRemove = result;
               this.nextEntry = this.nextEntry.successorInMultimap;
               return result;
            }
         }

         public void remove() {
            CollectPreconditions.checkRemove(this.toRemove != null);
            LinkedHashMultimap.this.remove(this.toRemove.getKey(), this.toRemove.getValue());
            this.toRemove = null;
         }
      };
   }

   Iterator valueIterator() {
      return Maps.valueIterator(this.entryIterator());
   }

   public void clear() {
      super.clear();
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      stream.writeInt(this.valueSetCapacity);
      stream.writeInt(this.keySet().size());

      for(K key : this.keySet()) {
         stream.writeObject(key);
      }

      stream.writeInt(this.size());

      for(Entry<K, V> entry : this.entries()) {
         stream.writeObject(entry.getKey());
         stream.writeObject(entry.getValue());
      }

   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.multimapHeaderEntry = new LinkedHashMultimap.ValueEntry((Object)null, (Object)null, 0, (LinkedHashMultimap.ValueEntry)null);
      succeedsInMultimap(this.multimapHeaderEntry, this.multimapHeaderEntry);
      this.valueSetCapacity = stream.readInt();
      int distinctKeys = stream.readInt();
      Map<K, Collection<V>> map = new LinkedHashMap(Maps.capacity(distinctKeys));

      for(int i = 0; i < distinctKeys; ++i) {
         K key = stream.readObject();
         map.put(key, this.createCollection(key));
      }

      int entries = stream.readInt();

      for(int i = 0; i < entries; ++i) {
         K key = stream.readObject();
         V value = stream.readObject();
         ((Collection)map.get(key)).add(value);
      }

      this.setMap(map);
   }

   @VisibleForTesting
   static final class ValueEntry extends ImmutableEntry implements LinkedHashMultimap.ValueSetLink {
      final int smearedValueHash;
      @Nullable
      LinkedHashMultimap.ValueEntry nextInValueBucket;
      LinkedHashMultimap.ValueSetLink predecessorInValueSet;
      LinkedHashMultimap.ValueSetLink successorInValueSet;
      LinkedHashMultimap.ValueEntry predecessorInMultimap;
      LinkedHashMultimap.ValueEntry successorInMultimap;

      ValueEntry(@Nullable Object key, @Nullable Object value, int smearedValueHash, @Nullable LinkedHashMultimap.ValueEntry nextInValueBucket) {
         super(key, value);
         this.smearedValueHash = smearedValueHash;
         this.nextInValueBucket = nextInValueBucket;
      }

      boolean matchesValue(@Nullable Object v, int smearedVHash) {
         return this.smearedValueHash == smearedVHash && Objects.equal(this.getValue(), v);
      }

      public LinkedHashMultimap.ValueSetLink getPredecessorInValueSet() {
         return this.predecessorInValueSet;
      }

      public LinkedHashMultimap.ValueSetLink getSuccessorInValueSet() {
         return this.successorInValueSet;
      }

      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink entry) {
         this.predecessorInValueSet = entry;
      }

      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink entry) {
         this.successorInValueSet = entry;
      }

      public LinkedHashMultimap.ValueEntry getPredecessorInMultimap() {
         return this.predecessorInMultimap;
      }

      public LinkedHashMultimap.ValueEntry getSuccessorInMultimap() {
         return this.successorInMultimap;
      }

      public void setSuccessorInMultimap(LinkedHashMultimap.ValueEntry multimapSuccessor) {
         this.successorInMultimap = multimapSuccessor;
      }

      public void setPredecessorInMultimap(LinkedHashMultimap.ValueEntry multimapPredecessor) {
         this.predecessorInMultimap = multimapPredecessor;
      }
   }

   @VisibleForTesting
   final class ValueSet extends Sets.ImprovedAbstractSet implements LinkedHashMultimap.ValueSetLink {
      private final Object key;
      @VisibleForTesting
      LinkedHashMultimap.ValueEntry[] hashTable;
      private int size = 0;
      private int modCount = 0;
      private LinkedHashMultimap.ValueSetLink firstEntry;
      private LinkedHashMultimap.ValueSetLink lastEntry;

      ValueSet(Object key, int expectedValues) {
         this.key = key;
         this.firstEntry = this;
         this.lastEntry = this;
         int tableSize = Hashing.closedTableSize(expectedValues, 1.0D);
         LinkedHashMultimap.ValueEntry<K, V>[] hashTable = new LinkedHashMultimap.ValueEntry[tableSize];
         this.hashTable = hashTable;
      }

      private int mask() {
         return this.hashTable.length - 1;
      }

      public LinkedHashMultimap.ValueSetLink getPredecessorInValueSet() {
         return this.lastEntry;
      }

      public LinkedHashMultimap.ValueSetLink getSuccessorInValueSet() {
         return this.firstEntry;
      }

      public void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink entry) {
         this.lastEntry = entry;
      }

      public void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink entry) {
         this.firstEntry = entry;
      }

      public Iterator iterator() {
         return new Iterator() {
            LinkedHashMultimap.ValueSetLink nextEntry;
            LinkedHashMultimap.ValueEntry toRemove;
            int expectedModCount;

            {
               this.nextEntry = ValueSet.this.firstEntry;
               this.expectedModCount = ValueSet.this.modCount;
            }

            private void checkForComodification() {
               if(ValueSet.this.modCount != this.expectedModCount) {
                  throw new ConcurrentModificationException();
               }
            }

            public boolean hasNext() {
               this.checkForComodification();
               return this.nextEntry != ValueSet.this;
            }

            public Object next() {
               if(!this.hasNext()) {
                  throw new NoSuchElementException();
               } else {
                  LinkedHashMultimap.ValueEntry<K, V> entry = (LinkedHashMultimap.ValueEntry)this.nextEntry;
                  V result = entry.getValue();
                  this.toRemove = entry;
                  this.nextEntry = entry.getSuccessorInValueSet();
                  return result;
               }
            }

            public void remove() {
               this.checkForComodification();
               CollectPreconditions.checkRemove(this.toRemove != null);
               ValueSet.this.remove(this.toRemove.getValue());
               this.expectedModCount = ValueSet.this.modCount;
               this.toRemove = null;
            }
         };
      }

      public int size() {
         return this.size;
      }

      public boolean contains(@Nullable Object o) {
         int smearedHash = Hashing.smearedHash(o);

         for(LinkedHashMultimap.ValueEntry<K, V> entry = this.hashTable[smearedHash & this.mask()]; entry != null; entry = entry.nextInValueBucket) {
            if(entry.matchesValue(o, smearedHash)) {
               return true;
            }
         }

         return false;
      }

      public boolean add(@Nullable Object value) {
         int smearedHash = Hashing.smearedHash(value);
         int bucket = smearedHash & this.mask();
         LinkedHashMultimap.ValueEntry<K, V> rowHead = this.hashTable[bucket];

         for(LinkedHashMultimap.ValueEntry<K, V> entry = rowHead; entry != null; entry = entry.nextInValueBucket) {
            if(entry.matchesValue(value, smearedHash)) {
               return false;
            }
         }

         LinkedHashMultimap.ValueEntry<K, V> newEntry = new LinkedHashMultimap.ValueEntry(this.key, value, smearedHash, rowHead);
         LinkedHashMultimap.succeedsInValueSet(this.lastEntry, newEntry);
         LinkedHashMultimap.succeedsInValueSet(newEntry, this);
         LinkedHashMultimap.succeedsInMultimap(LinkedHashMultimap.this.multimapHeaderEntry.getPredecessorInMultimap(), newEntry);
         LinkedHashMultimap.succeedsInMultimap(newEntry, LinkedHashMultimap.this.multimapHeaderEntry);
         this.hashTable[bucket] = newEntry;
         ++this.size;
         ++this.modCount;
         this.rehashIfNecessary();
         return true;
      }

      private void rehashIfNecessary() {
         if(Hashing.needsResizing(this.size, this.hashTable.length, 1.0D)) {
            LinkedHashMultimap.ValueEntry<K, V>[] hashTable = new LinkedHashMultimap.ValueEntry[this.hashTable.length * 2];
            this.hashTable = hashTable;
            int mask = hashTable.length - 1;

            for(LinkedHashMultimap.ValueSetLink<K, V> entry = this.firstEntry; entry != this; entry = entry.getSuccessorInValueSet()) {
               LinkedHashMultimap.ValueEntry<K, V> valueEntry = (LinkedHashMultimap.ValueEntry)entry;
               int bucket = valueEntry.smearedValueHash & mask;
               valueEntry.nextInValueBucket = hashTable[bucket];
               hashTable[bucket] = valueEntry;
            }
         }

      }

      public boolean remove(@Nullable Object o) {
         int smearedHash = Hashing.smearedHash(o);
         int bucket = smearedHash & this.mask();
         LinkedHashMultimap.ValueEntry<K, V> prev = null;

         for(LinkedHashMultimap.ValueEntry<K, V> entry = this.hashTable[bucket]; entry != null; entry = entry.nextInValueBucket) {
            if(entry.matchesValue(o, smearedHash)) {
               if(prev == null) {
                  this.hashTable[bucket] = entry.nextInValueBucket;
               } else {
                  prev.nextInValueBucket = entry.nextInValueBucket;
               }

               LinkedHashMultimap.deleteFromValueSet(entry);
               LinkedHashMultimap.deleteFromMultimap(entry);
               --this.size;
               ++this.modCount;
               return true;
            }

            prev = entry;
         }

         return false;
      }

      public void clear() {
         Arrays.fill(this.hashTable, (Object)null);
         this.size = 0;

         for(LinkedHashMultimap.ValueSetLink<K, V> entry = this.firstEntry; entry != this; entry = entry.getSuccessorInValueSet()) {
            LinkedHashMultimap.ValueEntry<K, V> valueEntry = (LinkedHashMultimap.ValueEntry)entry;
            LinkedHashMultimap.deleteFromMultimap(valueEntry);
         }

         LinkedHashMultimap.succeedsInValueSet(this, this);
         ++this.modCount;
      }
   }

   private interface ValueSetLink {
      LinkedHashMultimap.ValueSetLink getPredecessorInValueSet();

      LinkedHashMultimap.ValueSetLink getSuccessorInValueSet();

      void setPredecessorInValueSet(LinkedHashMultimap.ValueSetLink var1);

      void setSuccessorInValueSet(LinkedHashMultimap.ValueSetLink var1);
   }
}
