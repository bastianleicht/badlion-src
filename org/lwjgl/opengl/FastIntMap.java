package org.lwjgl.opengl;

import java.util.Iterator;

final class FastIntMap implements Iterable {
   private FastIntMap.Entry[] table;
   private int size;
   private int mask;
   private int capacity;
   private int threshold;

   FastIntMap() {
      this(16, 0.75F);
   }

   FastIntMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   FastIntMap(int initialCapacity, float loadFactor) {
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
         this.table = new FastIntMap.Entry[this.capacity];
         this.mask = this.capacity - 1;
      }
   }

   private int index(int key) {
      return index(key, this.mask);
   }

   private static int index(int key, int mask) {
      return key & mask;
   }

   public Object put(int key, Object value) {
      FastIntMap.Entry<V>[] table = this.table;
      int index = this.index(key);

      for(FastIntMap.Entry<V> e = table[index]; e != null; e = e.next) {
         if(e.key == key) {
            V oldValue = e.value;
            e.value = value;
            return oldValue;
         }
      }

      table[index] = new FastIntMap.Entry(key, value, table[index]);
      if(this.size++ >= this.threshold) {
         this.rehash(table);
      }

      return null;
   }

   private void rehash(FastIntMap.Entry[] table) {
      int newCapacity = 2 * this.capacity;
      int newMask = newCapacity - 1;
      FastIntMap.Entry<V>[] newTable = new FastIntMap.Entry[newCapacity];

      for(int i = 0; i < table.length; ++i) {
         FastIntMap.Entry<V> e = table[i];
         if(e != null) {
            while(true) {
               FastIntMap.Entry<V> next = e.next;
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

   public Object get(int key) {
      int index = this.index(key);

      for(FastIntMap.Entry<V> e = this.table[index]; e != null; e = e.next) {
         if(e.key == key) {
            return e.value;
         }
      }

      return null;
   }

   public boolean containsValue(Object value) {
      FastIntMap.Entry<V>[] table = this.table;

      for(int i = table.length - 1; i >= 0; --i) {
         for(FastIntMap.Entry<V> e = table[i]; e != null; e = e.next) {
            if(e.value.equals(value)) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean containsKey(int key) {
      int index = this.index(key);

      for(FastIntMap.Entry<V> e = this.table[index]; e != null; e = e.next) {
         if(e.key == key) {
            return true;
         }
      }

      return false;
   }

   public Object remove(int key) {
      int index = this.index(key);
      FastIntMap.Entry<V> prev = this.table[index];

      FastIntMap.Entry<V> next;
      for(FastIntMap.Entry<V> e = prev; e != null; e = next) {
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
      FastIntMap.Entry<V>[] table = this.table;

      for(int index = table.length - 1; index >= 0; --index) {
         table[index] = null;
      }

      this.size = 0;
   }

   public FastIntMap.EntryIterator iterator() {
      return new FastIntMap.EntryIterator();
   }

   static final class Entry {
      final int key;
      Object value;
      FastIntMap.Entry next;

      Entry(int key, Object value, FastIntMap.Entry next) {
         this.key = key;
         this.value = value;
         this.next = next;
      }

      public int getKey() {
         return this.key;
      }

      public Object getValue() {
         return this.value;
      }
   }

   public class EntryIterator implements Iterator {
      private int nextIndex;
      private FastIntMap.Entry current;

      EntryIterator() {
         this.reset();
      }

      public void reset() {
         this.current = null;
         FastIntMap.Entry<V>[] table = FastIntMap.this.table;

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
            FastIntMap.Entry e = this.current;
            return e != null && e.next != null;
         }
      }

      public FastIntMap.Entry next() {
         FastIntMap.Entry<V> e = this.current;
         if(e != null) {
            e = e.next;
            if(e != null) {
               this.current = e;
               return e;
            }
         }

         FastIntMap.Entry<V>[] table = FastIntMap.this.table;
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
         FastIntMap.this.remove(this.current.key);
      }
   }
}
