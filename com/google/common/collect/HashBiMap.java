package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.AbstractMapEntry;
import com.google.common.collect.BiMap;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.Hashing;
import com.google.common.collect.ImmutableEntry;
import com.google.common.collect.Maps;
import com.google.common.collect.Serialization;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
public final class HashBiMap extends AbstractMap implements BiMap, Serializable {
   private static final double LOAD_FACTOR = 1.0D;
   private transient HashBiMap.BiEntry[] hashTableKToV;
   private transient HashBiMap.BiEntry[] hashTableVToK;
   private transient int size;
   private transient int mask;
   private transient int modCount;
   private transient BiMap inverse;
   @GwtIncompatible("Not needed in emulated source")
   private static final long serialVersionUID = 0L;

   public static HashBiMap create() {
      return create(16);
   }

   public static HashBiMap create(int expectedSize) {
      return new HashBiMap(expectedSize);
   }

   public static HashBiMap create(Map map) {
      HashBiMap<K, V> bimap = create(map.size());
      bimap.putAll(map);
      return bimap;
   }

   private HashBiMap(int expectedSize) {
      this.init(expectedSize);
   }

   private void init(int expectedSize) {
      CollectPreconditions.checkNonnegative(expectedSize, "expectedSize");
      int tableSize = Hashing.closedTableSize(expectedSize, 1.0D);
      this.hashTableKToV = this.createTable(tableSize);
      this.hashTableVToK = this.createTable(tableSize);
      this.mask = tableSize - 1;
      this.modCount = 0;
      this.size = 0;
   }

   private void delete(HashBiMap.BiEntry entry) {
      int keyBucket = entry.keyHash & this.mask;
      HashBiMap.BiEntry<K, V> prevBucketEntry = null;

      for(HashBiMap.BiEntry<K, V> bucketEntry = this.hashTableKToV[keyBucket]; bucketEntry != entry; bucketEntry = bucketEntry.nextInKToVBucket) {
         prevBucketEntry = bucketEntry;
      }

      if(prevBucketEntry == null) {
         this.hashTableKToV[keyBucket] = entry.nextInKToVBucket;
      } else {
         prevBucketEntry.nextInKToVBucket = entry.nextInKToVBucket;
      }

      int valueBucket = entry.valueHash & this.mask;
      prevBucketEntry = null;

      for(HashBiMap.BiEntry<K, V> bucketEntry = this.hashTableVToK[valueBucket]; bucketEntry != entry; bucketEntry = bucketEntry.nextInVToKBucket) {
         prevBucketEntry = bucketEntry;
      }

      if(prevBucketEntry == null) {
         this.hashTableVToK[valueBucket] = entry.nextInVToKBucket;
      } else {
         prevBucketEntry.nextInVToKBucket = entry.nextInVToKBucket;
      }

      --this.size;
      ++this.modCount;
   }

   private void insert(HashBiMap.BiEntry entry) {
      int keyBucket = entry.keyHash & this.mask;
      entry.nextInKToVBucket = this.hashTableKToV[keyBucket];
      this.hashTableKToV[keyBucket] = entry;
      int valueBucket = entry.valueHash & this.mask;
      entry.nextInVToKBucket = this.hashTableVToK[valueBucket];
      this.hashTableVToK[valueBucket] = entry;
      ++this.size;
      ++this.modCount;
   }

   private static int hash(@Nullable Object o) {
      return Hashing.smear(o == null?0:o.hashCode());
   }

   private HashBiMap.BiEntry seekByKey(@Nullable Object key, int keyHash) {
      for(HashBiMap.BiEntry<K, V> entry = this.hashTableKToV[keyHash & this.mask]; entry != null; entry = entry.nextInKToVBucket) {
         if(keyHash == entry.keyHash && Objects.equal(key, entry.key)) {
            return entry;
         }
      }

      return null;
   }

   private HashBiMap.BiEntry seekByValue(@Nullable Object value, int valueHash) {
      for(HashBiMap.BiEntry<K, V> entry = this.hashTableVToK[valueHash & this.mask]; entry != null; entry = entry.nextInVToKBucket) {
         if(valueHash == entry.valueHash && Objects.equal(value, entry.value)) {
            return entry;
         }
      }

      return null;
   }

   public boolean containsKey(@Nullable Object key) {
      return this.seekByKey(key, hash(key)) != null;
   }

   public boolean containsValue(@Nullable Object value) {
      return this.seekByValue(value, hash(value)) != null;
   }

   @Nullable
   public Object get(@Nullable Object key) {
      HashBiMap.BiEntry<K, V> entry = this.seekByKey(key, hash(key));
      return entry == null?null:entry.value;
   }

   public Object put(@Nullable Object key, @Nullable Object value) {
      return this.put(key, value, false);
   }

   public Object forcePut(@Nullable Object key, @Nullable Object value) {
      return this.put(key, value, true);
   }

   private Object put(@Nullable Object key, @Nullable Object value, boolean force) {
      int keyHash = hash(key);
      int valueHash = hash(value);
      HashBiMap.BiEntry<K, V> oldEntryForKey = this.seekByKey(key, keyHash);
      if(oldEntryForKey != null && valueHash == oldEntryForKey.valueHash && Objects.equal(value, oldEntryForKey.value)) {
         return value;
      } else {
         HashBiMap.BiEntry<K, V> oldEntryForValue = this.seekByValue(value, valueHash);
         if(oldEntryForValue != null) {
            if(!force) {
               throw new IllegalArgumentException("value already present: " + value);
            }

            this.delete(oldEntryForValue);
         }

         if(oldEntryForKey != null) {
            this.delete(oldEntryForKey);
         }

         HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry(key, keyHash, value, valueHash);
         this.insert(newEntry);
         this.rehashIfNecessary();
         return oldEntryForKey == null?null:oldEntryForKey.value;
      }
   }

   @Nullable
   private Object putInverse(@Nullable Object value, @Nullable Object key, boolean force) {
      int valueHash = hash(value);
      int keyHash = hash(key);
      HashBiMap.BiEntry<K, V> oldEntryForValue = this.seekByValue(value, valueHash);
      if(oldEntryForValue != null && keyHash == oldEntryForValue.keyHash && Objects.equal(key, oldEntryForValue.key)) {
         return key;
      } else {
         HashBiMap.BiEntry<K, V> oldEntryForKey = this.seekByKey(key, keyHash);
         if(oldEntryForKey != null) {
            if(!force) {
               throw new IllegalArgumentException("value already present: " + key);
            }

            this.delete(oldEntryForKey);
         }

         if(oldEntryForValue != null) {
            this.delete(oldEntryForValue);
         }

         HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry(key, keyHash, value, valueHash);
         this.insert(newEntry);
         this.rehashIfNecessary();
         return oldEntryForValue == null?null:oldEntryForValue.key;
      }
   }

   private void rehashIfNecessary() {
      HashBiMap.BiEntry<K, V>[] oldKToV = this.hashTableKToV;
      if(Hashing.needsResizing(this.size, oldKToV.length, 1.0D)) {
         int newTableSize = oldKToV.length * 2;
         this.hashTableKToV = this.createTable(newTableSize);
         this.hashTableVToK = this.createTable(newTableSize);
         this.mask = newTableSize - 1;
         this.size = 0;

         HashBiMap.BiEntry<K, V> nextEntry;
         for(int bucket = 0; bucket < oldKToV.length; ++bucket) {
            for(HashBiMap.BiEntry<K, V> entry = oldKToV[bucket]; entry != null; entry = nextEntry) {
               nextEntry = entry.nextInKToVBucket;
               this.insert(entry);
            }
         }

         ++this.modCount;
      }

   }

   private HashBiMap.BiEntry[] createTable(int length) {
      return new HashBiMap.BiEntry[length];
   }

   public Object remove(@Nullable Object key) {
      HashBiMap.BiEntry<K, V> entry = this.seekByKey(key, hash(key));
      if(entry == null) {
         return null;
      } else {
         this.delete(entry);
         return entry.value;
      }
   }

   public void clear() {
      this.size = 0;
      Arrays.fill(this.hashTableKToV, (Object)null);
      Arrays.fill(this.hashTableVToK, (Object)null);
      ++this.modCount;
   }

   public int size() {
      return this.size;
   }

   public Set keySet() {
      return new HashBiMap.KeySet();
   }

   public Set values() {
      return this.inverse().keySet();
   }

   public Set entrySet() {
      return new HashBiMap.EntrySet();
   }

   public BiMap inverse() {
      return this.inverse == null?(this.inverse = new HashBiMap.Inverse()):this.inverse;
   }

   @GwtIncompatible("java.io.ObjectOutputStream")
   private void writeObject(ObjectOutputStream stream) throws IOException {
      stream.defaultWriteObject();
      Serialization.writeMap(this, stream);
   }

   @GwtIncompatible("java.io.ObjectInputStream")
   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      int size = Serialization.readCount(stream);
      this.init(size);
      Serialization.populateMap(this, stream, size);
   }

   private static final class BiEntry extends ImmutableEntry {
      final int keyHash;
      final int valueHash;
      @Nullable
      HashBiMap.BiEntry nextInKToVBucket;
      @Nullable
      HashBiMap.BiEntry nextInVToKBucket;

      BiEntry(Object key, int keyHash, Object value, int valueHash) {
         super(key, value);
         this.keyHash = keyHash;
         this.valueHash = valueHash;
      }
   }

   private final class EntrySet extends Maps.EntrySet {
      private EntrySet() {
      }

      Map map() {
         return HashBiMap.this;
      }

      public Iterator iterator() {
         return new HashBiMap.Itr() {
            Entry output(HashBiMap.BiEntry entry) {
               return new null.MapEntry(entry);
            }

            class MapEntry extends AbstractMapEntry {
               HashBiMap.BiEntry delegate;

               MapEntry(HashBiMap.BiEntry entry) {
                  this.delegate = entry;
               }

               public Object getKey() {
                  return this.delegate.key;
               }

               public Object getValue() {
                  return this.delegate.value;
               }

               public Object setValue(Object value) {
                  V oldValue = this.delegate.value;
                  int valueHash = HashBiMap.hash(value);
                  if(valueHash == this.delegate.valueHash && Objects.equal(value, oldValue)) {
                     return value;
                  } else {
                     Preconditions.checkArgument(HashBiMap.this.seekByValue(value, valueHash) == null, "value already present: %s", new Object[]{value});
                     HashBiMap.this.delete(this.delegate);
                     HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry(this.delegate.key, this.delegate.keyHash, value, valueHash);
                     HashBiMap.this.insert(newEntry);
                     expectedModCount = HashBiMap.this.modCount;
                     if(toRemove == this.delegate) {
                        toRemove = newEntry;
                     }

                     this.delegate = newEntry;
                     return oldValue;
                  }
               }
            }
         };
      }
   }

   private final class Inverse extends AbstractMap implements BiMap, Serializable {
      private Inverse() {
      }

      BiMap forward() {
         return HashBiMap.this;
      }

      public int size() {
         return HashBiMap.this.size;
      }

      public void clear() {
         this.forward().clear();
      }

      public boolean containsKey(@Nullable Object value) {
         return this.forward().containsValue(value);
      }

      public Object get(@Nullable Object value) {
         HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByValue(value, HashBiMap.hash(value));
         return entry == null?null:entry.key;
      }

      public Object put(@Nullable Object value, @Nullable Object key) {
         return HashBiMap.this.putInverse(value, key, false);
      }

      public Object forcePut(@Nullable Object value, @Nullable Object key) {
         return HashBiMap.this.putInverse(value, key, true);
      }

      public Object remove(@Nullable Object value) {
         HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByValue(value, HashBiMap.hash(value));
         if(entry == null) {
            return null;
         } else {
            HashBiMap.this.delete(entry);
            return entry.key;
         }
      }

      public BiMap inverse() {
         return this.forward();
      }

      public Set keySet() {
         return new HashBiMap.Inverse.InverseKeySet();
      }

      public Set values() {
         return this.forward().keySet();
      }

      public Set entrySet() {
         return new Maps.EntrySet() {
            Map map() {
               return Inverse.this;
            }

            public Iterator iterator() {
               return new HashBiMap.Itr() {
                  Entry output(HashBiMap.BiEntry entry) {
                     return new null.InverseEntry(entry);
                  }

                  class InverseEntry extends AbstractMapEntry {
                     HashBiMap.BiEntry delegate;

                     InverseEntry(HashBiMap.BiEntry entry) {
                        this.delegate = entry;
                     }

                     public Object getKey() {
                        return this.delegate.value;
                     }

                     public Object getValue() {
                        return this.delegate.key;
                     }

                     public Object setValue(Object key) {
                        K oldKey = this.delegate.key;
                        int keyHash = HashBiMap.hash(key);
                        if(keyHash == this.delegate.keyHash && Objects.equal(key, oldKey)) {
                           return key;
                        } else {
                           Preconditions.checkArgument(HashBiMap.this.seekByKey(key, keyHash) == null, "value already present: %s", new Object[]{key});
                           HashBiMap.this.delete(this.delegate);
                           HashBiMap.BiEntry<K, V> newEntry = new HashBiMap.BiEntry(key, keyHash, this.delegate.value, this.delegate.valueHash);
                           HashBiMap.this.insert(newEntry);
                           expectedModCount = HashBiMap.this.modCount;
                           return oldKey;
                        }
                     }
                  }
               };
            }
         };
      }

      Object writeReplace() {
         return new HashBiMap.InverseSerializedForm(HashBiMap.this);
      }

      private final class InverseKeySet extends Maps.KeySet {
         InverseKeySet() {
            super(Inverse.this);
         }

         public boolean remove(@Nullable Object o) {
            HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByValue(o, HashBiMap.hash(o));
            if(entry == null) {
               return false;
            } else {
               HashBiMap.this.delete(entry);
               return true;
            }
         }

         public Iterator iterator() {
            return new HashBiMap.Itr() {
               Object output(HashBiMap.BiEntry entry) {
                  return entry.value;
               }
            };
         }
      }
   }

   private static final class InverseSerializedForm implements Serializable {
      private final HashBiMap bimap;

      InverseSerializedForm(HashBiMap bimap) {
         this.bimap = bimap;
      }

      Object readResolve() {
         return this.bimap.inverse();
      }
   }

   abstract class Itr implements Iterator {
      int nextBucket = 0;
      HashBiMap.BiEntry next = null;
      HashBiMap.BiEntry toRemove = null;
      int expectedModCount;

      Itr() {
         this.expectedModCount = HashBiMap.this.modCount;
      }

      private void checkForConcurrentModification() {
         if(HashBiMap.this.modCount != this.expectedModCount) {
            throw new ConcurrentModificationException();
         }
      }

      public boolean hasNext() {
         this.checkForConcurrentModification();
         if(this.next != null) {
            return true;
         } else {
            while(this.nextBucket < HashBiMap.this.hashTableKToV.length) {
               if(HashBiMap.this.hashTableKToV[this.nextBucket] != null) {
                  this.next = HashBiMap.this.hashTableKToV[this.nextBucket++];
                  return true;
               }

               ++this.nextBucket;
            }

            return false;
         }
      }

      public Object next() {
         this.checkForConcurrentModification();
         if(!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            HashBiMap.BiEntry<K, V> entry = this.next;
            this.next = entry.nextInKToVBucket;
            this.toRemove = entry;
            return this.output(entry);
         }
      }

      public void remove() {
         this.checkForConcurrentModification();
         CollectPreconditions.checkRemove(this.toRemove != null);
         HashBiMap.this.delete(this.toRemove);
         this.expectedModCount = HashBiMap.this.modCount;
         this.toRemove = null;
      }

      abstract Object output(HashBiMap.BiEntry var1);
   }

   private final class KeySet extends Maps.KeySet {
      KeySet() {
         super(HashBiMap.this);
      }

      public Iterator iterator() {
         return new HashBiMap.Itr() {
            Object output(HashBiMap.BiEntry entry) {
               return entry.key;
            }
         };
      }

      public boolean remove(@Nullable Object o) {
         HashBiMap.BiEntry<K, V> entry = HashBiMap.this.seekByKey(o, HashBiMap.hash(o));
         if(entry == null) {
            return false;
         } else {
            HashBiMap.this.delete(entry);
            return true;
         }
      }
   }
}
