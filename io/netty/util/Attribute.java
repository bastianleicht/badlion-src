package io.netty.util;

import io.netty.util.AttributeKey;

public interface Attribute {
   AttributeKey key();

   Object get();

   void set(Object var1);

   Object getAndSet(Object var1);

   Object setIfAbsent(Object var1);

   Object getAndRemove();

   boolean compareAndSet(Object var1, Object var2);

   void remove();
}
