package io.netty.util.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import sun.misc.Unsafe;

final class UnsafeAtomicReferenceFieldUpdater extends AtomicReferenceFieldUpdater {
   private final long offset;
   private final Unsafe unsafe;

   UnsafeAtomicReferenceFieldUpdater(Unsafe unsafe, Class tClass, String fieldName) throws NoSuchFieldException {
      Field field = tClass.getDeclaredField(fieldName);
      if(!Modifier.isVolatile(field.getModifiers())) {
         throw new IllegalArgumentException("Must be volatile");
      } else {
         this.unsafe = unsafe;
         this.offset = unsafe.objectFieldOffset(field);
      }
   }

   public boolean compareAndSet(Object obj, Object expect, Object update) {
      return this.unsafe.compareAndSwapObject(obj, this.offset, expect, update);
   }

   public boolean weakCompareAndSet(Object obj, Object expect, Object update) {
      return this.unsafe.compareAndSwapObject(obj, this.offset, expect, update);
   }

   public void set(Object obj, Object newValue) {
      this.unsafe.putObjectVolatile(obj, this.offset, newValue);
   }

   public void lazySet(Object obj, Object newValue) {
      this.unsafe.putOrderedObject(obj, this.offset, newValue);
   }

   public Object get(Object obj) {
      return this.unsafe.getObjectVolatile(obj, this.offset);
   }
}
