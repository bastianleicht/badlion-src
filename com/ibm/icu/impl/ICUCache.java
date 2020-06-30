package com.ibm.icu.impl;

public interface ICUCache {
   int SOFT = 0;
   int WEAK = 1;
   Object NULL = new Object();

   void clear();

   void put(Object var1, Object var2);

   Object get(Object var1);
}
