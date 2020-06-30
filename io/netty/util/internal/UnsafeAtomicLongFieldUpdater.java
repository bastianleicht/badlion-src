package io.netty.util.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import sun.misc.Unsafe;

final class UnsafeAtomicLongFieldUpdater extends AtomicLongFieldUpdater {
   private final long offset;
   private final Unsafe unsafe;

   UnsafeAtomicLongFieldUpdater(Unsafe unsafe, Class tClass, String fieldName) throws NoSuchFieldException {
      Field field = tClass.getDeclaredField(fieldName);
      if(!Modifier.isVolatile(field.getModifiers())) {
         throw new IllegalArgumentException("Must be volatile");
      } else {
         this.unsafe = unsafe;
         this.offset = unsafe.objectFieldOffset(field);
      }
   }

   public boolean compareAndSet(Object obj, long expect, long update) {
      return this.unsafe.compareAndSwapLong(obj, this.offset, expect, update);
   }

   public boolean weakCompareAndSet(Object obj, long expect, long update) {
      return this.unsafe.compareAndSwapLong(obj, this.offset, expect, update);
   }

   public void set(Object obj, long newValue) {
      this.unsafe.putLongVolatile(obj, this.offset, newValue);
   }

   public void lazySet(Object obj, long newValue) {
      this.unsafe.putOrderedLong(obj, this.offset, newValue);
   }

   public long get(Object obj) {
      return this.unsafe.getLongVolatile(obj, this.offset);
   }
}
