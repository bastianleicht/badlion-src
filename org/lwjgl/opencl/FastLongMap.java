package org.lwjgl.opencl;

import java.util.Iterator;

final class FastLongMap implements Iterable {
   private FastLongMap.Entry[] table;
   private int size;
   private int mask;
   private int capacity;
   private int threshold;

   FastLongMap() {
      this(16, 0.75F);
   }

   FastLongMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   FastLongMap(int initialCapacity, float loadFactor) {
      if(initialCapacity > 1073741824) {
         throw new IllegalArgumentException("initialCapacity is too large.");
      } else if(initialCapacity < 0) {
         throw new IllegalArgumentException("initialCapacity must be greater than zero.");
      } else if(loadFactor <= 0.0F) {
         throw new IllegalArgumentException("initialCapacity must be greater than zero.");
      } else {
         for(this.capacity = 1; this.capacity < initialCapacity; this.capacity <<= 1) {
            ;
         }

         this.threshold = (int)((float)this.capacity * loadFactor);
         this.table = new FastLongMap.Entry[this.capacity];
         this.mask = this.capacity - 1;
      }
   }

   private int index(long key) {
      return index(key, this.mask);
   }

   private static int index(long key, int mask) {
      int hash = (int)(key ^ key >>> 32);
      return hash & mask;
   }

   public Object put(long key, Object value) {
      FastLongMap.Entry<V>[] table = this.table;
      int index = this.index(key);

      for(FastLongMap.Entry<V> e = table[index]; e != null; e = e.next) {
         if(e.key == key) {
            V oldValue = e.value;
            e.value = value;
            return oldValue;
         }
      }

      table[index] = new FastLongMap.Entry(key, value, table[index]);
      if(this.size++ >= this.threshold) {
         this.rehash(table);
      }

      return null;
   }

   private void rehash(FastLongMap.Entry[] table) {
      int newCapacity = 2 * this.capacity;
      int newMask = newCapacity - 1;
      FastLongMap.Entry<V>[] newTable = new FastLongMap.Entry[newCapacity];

      for(int i = 0; i < table.length; ++i) {
         FastLongMap.Entry<V> e = table[i];
         if(e != null) {
            while(true) {
               FastLongMap.Entry<V> next = e.next;
               int index = index(e.key, newMask);
               e.next = newTable[index];
               newTable[index] = e;
               e = next;
               if(next == null) {
                  break;
               }
            }
         }
      }

      this.table = newTable;
      this.capacity = newCapacity;
      this.mask = newMask;
      this.threshold *= 2;
   }

   public Object get(long key) {
      int index = this.index(key);

      for(FastLongMap.Entry<V> e = this.table[index]; e != null; e = e.next) {
         if(e.key == key) {
            return e.value;
         }
      }

      return null;
   }

   public boolean containsValue(Object value) {
      FastLongMap.Entry<V>[] table = this.table;

      for(int i = table.length - 1; i >= 0; --i) {
         for(FastLongMap.Entry<V> e = table[i]; e != null; e = e.next) {
            if(e.value.equals(value)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsKey(long key) {
      int index = this.index(key);

      for(FastLongMap.Entry<V> e = this.table[index]; e != null; e = e.next) {
         if(e.key == key) {
            return true;
         }
      }

      return false;
   }

   public Object remove(long key) {
      int index = this.index(key);
      FastLongMap.Entry<V> prev = this.table[index];

      FastLongMap.Entry<V> next;
      for(FastLongMap.Entry<V> e = prev; e != null; e = next) {
         next = e.next;
         if(e.key == key) {
            --this.size;
            if(prev == e) {
               this.table[index] = next;
            } else {
               prev.next = next;
            }

            return e.value;
         }

         prev = e;
      }

      return null;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public void clear() {
      FastLongMap.Entry<V>[] table = this.table;

      for(int index = table.length - 1; index >= 0; --index) {
         table[index] = null;
      }

      this.size = 0;
   }

   public FastLongMap.EntryIterator iterator() {
      return new FastLongMap.EntryIterator();
   }

   static final class Entry {
      final long key;
      Object value;
      FastLongMap.Entry next;

      Entry(long key, Object value, FastLongMap.Entry next) {
         this.key = key;
         this.value = value;
         this.next = next;
      }

      public long getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }
   }

   public class EntryIterator implements Iterator {
      private int nextIndex;
      private FastLongMap.Entry current;

      EntryIterator() {
         this.reset();
      }

      public void reset() {
         this.current = null;
         FastLongMap.Entry<V>[] table = FastLongMap.this.table;

         int i;
         for(i = table.length - 1; i >= 0 && table[i] == null; --i) {
            ;
         }

         this.nextIndex = i;
      }

      public boolean hasNext() {
         if(this.nextIndex >= 0) {
            return true;
         } else {
            FastLongMap.Entry e = this.current;
            return e != null && e.next != null;
         }
      }

      public FastLongMap.Entry next() {
         FastLongMap.Entry<V> e = this.current;
         if(e != null) {
            e = e.next;
            if(e != null) {
               this.current = e;
               return e;
            }
         }

         FastLongMap.Entry<V>[] table = FastLongMap.this.table;
         int i = this.nextIndex;
         e = this.current = table[i];

         while(true) {
            --i;
            if(i < 0 || table[i] != null) {
               break;
            }
         }

         this.nextIndex = i;
         return e;
      }

      public void remove() {
         FastLongMap.this.remove(this.current.key);
      }
   }
}
