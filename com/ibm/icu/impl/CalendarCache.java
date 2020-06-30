package com.ibm.icu.impl;

public class CalendarCache {
   private static final int[] primes = new int[]{61, 127, 509, 1021, 2039, 4093, 8191, 16381, 32749, '\ufff1', 131071, 262139};
   private int pIndex = 0;
   private int size = 0;
   private int arraySize;
   private int threshold;
   private long[] keys;
   private long[] values;
   public static long EMPTY = Long.MIN_VALUE;

   public CalendarCache() {
      this.arraySize = primes[this.pIndex];
      this.threshold = this.arraySize * 3 / 4;
      this.keys = new long[this.arraySize];
      this.values = new long[this.arraySize];
      this.makeArrays(this.arraySize);
   }

   private void makeArrays(int newSize) {
      this.keys = new long[newSize];
      this.values = new long[newSize];

      for(int i = 0; i < newSize; ++i) {
         this.values[i] = EMPTY;
      }

      this.arraySize = newSize;
      this.threshold = (int)((double)this.arraySize * 0.75D);
      this.size = 0;
   }

   public synchronized long get(long key) {
      return this.values[this.findIndex(key)];
   }

   public synchronized void put(long key, long value) {
      if(this.size >= this.threshold) {
         this.rehash();
      }

      int index = this.findIndex(key);
      this.keys[index] = key;
      this.values[index] = value;
      ++this.size;
   }

   private final int findIndex(long key) {
      int index = this.hash(key);

      for(int delta = 0; this.values[index] != EMPTY && this.keys[index] != key; index = (index + delta) % this.arraySize) {
         if(delta == 0) {
            delta = this.hash2(key);
         }
      }

      return index;
   }

   private void rehash() {
      int oldSize = this.arraySize;
      long[] oldKeys = this.keys;
      long[] oldValues = this.values;
      if(this.pIndex < primes.length - 1) {
         this.arraySize = primes[++this.pIndex];
      } else {
         this.arraySize = this.arraySize * 2 + 1;
      }

      this.size = 0;
      this.makeArrays(this.arraySize);

      for(int i = 0; i < oldSize; ++i) {
         if(oldValues[i] != EMPTY) {
            this.put(oldKeys[i], oldValues[i]);
         }
      }

   }

   private final int hash(long key) {
      int h = (int)((key * 15821L + 1L) % (long)this.arraySize);
      if(h < 0) {
         h += this.arraySize;
      }

      return h;
   }

   private final int hash2(long key) {
      return this.arraySize - 2 - (int)(key % (long)(this.arraySize - 2));
   }
}
