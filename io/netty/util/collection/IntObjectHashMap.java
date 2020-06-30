package io.netty.util.collection;

import io.netty.util.collection.IntObjectMap;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntObjectHashMap implements IntObjectMap, Iterable {
   private static final int DEFAULT_CAPACITY = 11;
   private static final float DEFAULT_LOAD_FACTOR = 0.5F;
   private static final Object NULL_VALUE = new Object();
   private int maxSize;
   private final float loadFactor;
   private int[] keys;
   private Object[] values;
   private int size;

   public IntObjectHashMap() {
      this(11, 0.5F);
   }

   public IntObjectHashMap(int initialCapacity) {
      this(initialCapacity, 0.5F);
   }

   public IntObjectHashMap(int initialCapacity, float loadFactor) {
      if(initialCapacity < 1) {
         throw new IllegalArgumentException("initialCapacity must be >= 1");
      } else if(loadFactor > 0.0F && loadFactor <= 1.0F) {
         this.loadFactor = loadFactor;
         int capacity = adjustCapacity(initialCapacity);
         this.keys = new int[capacity];
         V[] temp = (Object[])(new Object[capacity]);
         this.values = temp;
         this.maxSize = this.calcMaxSize(capacity);
      } else {
         throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
      }
   }

   private static Object toExternal(Object value) {
      return value == NULL_VALUE?null:value;
   }

   private static Object toInternal(Object value) {
      return value == null?NULL_VALUE:value;
   }

   public Object get(int key) {
      int index = this.indexOf(key);
      return index == -1?null:toExternal(this.values[index]);
   }

   public Object put(int key, Object value) {
      int startIndex = this.hashIndex(key);
      int index = startIndex;

      while(this.values[index] != null) {
         if(this.keys[index] == key) {
            V previousValue = this.values[index];
            this.values[index] = toInternal(value);
            return toExternal(previousValue);
         }

         if((index = this.probeNext(index)) == startIndex) {
            throw new IllegalStateException("Unable to insert");
         }
      }

      this.keys[index] = key;
      this.values[index] = toInternal(value);
      this.growSize();
      return null;
   }

   private int probeNext(int index) {
      return index == this.values.length - 1?0:index + 1;
   }

   public void putAll(IntObjectMap sourceMap) {
      if(sourceMap instanceof IntObjectHashMap) {
         IntObjectHashMap<V> source = (IntObjectHashMap)sourceMap;

         for(int i = 0; i < source.values.length; ++i) {
            V sourceValue = source.values[i];
            if(sourceValue != null) {
               this.put(source.keys[i], sourceValue);
            }
         }

      } else {
         for(IntObjectMap.Entry<V> entry : sourceMap.entries()) {
            this.put(entry.key(), entry.value());
         }

      }
   }

   public Object remove(int key) {
      int index = this.indexOf(key);
      if(index == -1) {
         return null;
      } else {
         V prev = this.values[index];
         this.removeAt(index);
         return toExternal(prev);
      }
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void clear() {
      Arrays.fill(this.keys, 0);
      Arrays.fill(this.values, (Object)null);
      this.size = 0;
   }

   public boolean containsKey(int key) {
      return this.indexOf(key) >= 0;
   }

   public boolean containsValue(Object value) {
      V v = toInternal(value);

      for(int i = 0; i < this.values.length; ++i) {
         if(this.values[i] != null && this.values[i].equals(v)) {
            return true;
         }
      }

      return false;
   }

   public Iterable entries() {
      return this;
   }

   public Iterator iterator() {
      return new IntObjectHashMap.IteratorImpl();
   }

   public int[] keys() {
      int[] outKeys = new int[this.size()];
      int targetIx = 0;

      for(int i = 0; i < this.values.length; ++i) {
         if(this.values[i] != null) {
            outKeys[targetIx++] = this.keys[i];
         }
      }

      return outKeys;
   }

   public Object[] values(Class clazz) {
      V[] outValues = (Object[])((Object[])Array.newInstance(clazz, this.size()));
      int targetIx = 0;

      for(int i = 0; i < this.values.length; ++i) {
         if(this.values[i] != null) {
            outValues[targetIx++] = this.values[i];
         }
      }

      return outValues;
   }

   public int hashCode() {
      int hash = this.size;

      for(int i = 0; i < this.keys.length; ++i) {
         hash ^= this.keys[i];
      }

      return hash;
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof IntObjectMap)) {
         return false;
      } else {
         IntObjectMap other = (IntObjectMap)obj;
         if(this.size != other.size()) {
            return false;
         } else {
            for(int i = 0; i < this.values.length; ++i) {
               V value = this.values[i];
               if(value != null) {
                  int key = this.keys[i];
                  Object otherValue = other.get(key);
                  if(value == NULL_VALUE) {
                     if(otherValue != null) {
                        return false;
                     }
                  } else if(!value.equals(otherValue)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   private int indexOf(int key) {
      int startIndex = this.hashIndex(key);
      int index = startIndex;

      while(this.values[index] != null) {
         if(key == this.keys[index]) {
            return index;
         }

         if((index = this.probeNext(index)) == startIndex) {
            return -1;
         }
      }

      return -1;
   }

   private int hashIndex(int key) {
      return key % this.keys.length;
   }

   private void growSize() {
      ++this.size;
      if(this.size > this.maxSize) {
         this.rehash(adjustCapacity((int)Math.min((double)this.keys.length * 2.0D, 2.147483639E9D)));
      } else if(this.size == this.keys.length) {
         this.rehash(this.keys.length);
      }

   }

   private static int adjustCapacity(int capacity) {
      return capacity | 1;
   }

   private void removeAt(int index) {
      --this.size;
      this.keys[index] = 0;
      this.values[index] = null;
      int nextFree = index;

      for(int i = this.probeNext(index); this.values[i] != null; i = this.probeNext(i)) {
         int bucket = this.hashIndex(this.keys[i]);
         if(i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
            this.keys[nextFree] = this.keys[i];
            this.values[nextFree] = this.values[i];
            this.keys[i] = 0;
            this.values[i] = null;
            nextFree = i;
         }
      }

   }

   private int calcMaxSize(int capacity) {
      int upperBound = capacity - 1;
      return Math.min(upperBound, (int)((float)capacity * this.loadFactor));
   }

   private void rehash(int newCapacity) {
      int[] oldKeys = this.keys;
      V[] oldVals = this.values;
      this.keys = new int[newCapacity];
      V[] temp = (Object[])(new Object[newCapacity]);
      this.values = temp;
      this.maxSize = this.calcMaxSize(newCapacity);

      for(int i = 0; i < oldVals.length; ++i) {
         V oldVal = oldVals[i];
         if(oldVal != null) {
            int oldKey = oldKeys[i];
            int startIndex = this.hashIndex(oldKey);

            int index;
            for(index = startIndex; this.values[index] != null; index = this.probeNext(index)) {
               ;
            }

            this.keys[index] = oldKey;
            this.values[index] = toInternal(oldVal);
         }
      }

   }

   public String toString() {
      if(this.size == 0) {
         return "{}";
      } else {
         StringBuilder sb = new StringBuilder(4 * this.size);

         for(int i = 0; i < this.values.length; ++i) {
            V value = this.values[i];
            if(value != null) {
               sb.append(sb.length() == 0?"{":", ");
               sb.append(this.keys[i]).append('=').append(value == this?"(this Map)":value);
            }
         }

         return sb.append('}').toString();
      }
   }

   private final class IteratorImpl implements Iterator, IntObjectMap.Entry {
      private int prevIndex;
      private int nextIndex;
      private int entryIndex;

      private IteratorImpl() {
         this.prevIndex = -1;
         this.nextIndex = -1;
         this.entryIndex = -1;
      }

      private void scanNext() {
         while(++this.nextIndex != IntObjectHashMap.this.values.length && IntObjectHashMap.this.values[this.nextIndex] == null) {
            ;
         }

      }

      public boolean hasNext() {
         if(this.nextIndex == -1) {
            this.scanNext();
         }

         return this.nextIndex < IntObjectHashMap.this.keys.length;
      }

      public IntObjectMap.Entry next() {
         if(!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.prevIndex = this.nextIndex;
            this.scanNext();
            this.entryIndex = this.prevIndex;
            return this;
         }
      }

      public void remove() {
         if(this.prevIndex < 0) {
            throw new IllegalStateException("next must be called before each remove.");
         } else {
            IntObjectHashMap.this.removeAt(this.prevIndex);
            this.prevIndex = -1;
         }
      }

      public int key() {
         return IntObjectHashMap.this.keys[this.entryIndex];
      }

      public Object value() {
         return IntObjectHashMap.toExternal(IntObjectHashMap.this.values[this.entryIndex]);
      }

      public void setValue(Object value) {
         IntObjectHashMap.this.values[this.entryIndex] = IntObjectHashMap.toInternal(value);
      }
   }
}
