package io.netty.util.collection;

public interface IntObjectMap {
   Object get(int var1);

   Object put(int var1, Object var2);

   void putAll(IntObjectMap var1);

   Object remove(int var1);

   int size();

   boolean isEmpty();

   void clear();

   boolean containsKey(int var1);

   boolean containsValue(Object var1);

   Iterable entries();

   int[] keys();

   Object[] values(Class var1);

   public interface Entry {
      int key();

      Object value();

      void setValue(Object var1);
   }
}
