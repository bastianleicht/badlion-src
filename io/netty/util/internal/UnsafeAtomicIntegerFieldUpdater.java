package io.netty.util.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import sun.misc.Unsafe;

final class UnsafeAtomicIntegerFieldUpdater extends AtomicIntegerFieldUpdater {
   private final long offset;
   private final Unsafe unsafe;

   UnsafeAtomicIntegerFieldUpdater(Unsafe unsafe, Class tClass, String fieldName) throws NoSuchFieldException {
      Field field = tClass.getDeclaredField(fieldName);
      if(!Modifier.isVolatile(field.getModifiers())) {
         throw new IllegalArgumentException("Must be volatile");
      } else {
         this.unsafe = unsafe;
         this.offset = unsafe.objectFieldOffset(field);
      }
   }

   public boolean compareAndSet(Object obj, int expect, int update) {
      return this.unsafe.compareAndSwapInt(obj, this.offset, expect, update);
   }

   public boolean weakCompareAndSet(Object obj, int expect, int update) {
      return this.unsafe.compareAndSwapInt(obj, this.offset, expect, update);
   }

   public void set(Object obj, int newValue) {
      this.unsafe.putIntVolatile(obj, this.offset, newValue);
   }

   public void lazySet(Object obj, int newValue) {
      this.unsafe.putOrderedInt(obj, this.offset, newValue);
   }

   public int get(Object obj) {
      return this.unsafe.getIntVolatile(obj, this.offset);
   }
}
