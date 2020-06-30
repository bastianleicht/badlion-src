package com.google.common.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;
import javax.annotation.Nullable;

public final class Atomics {
   public static AtomicReference newReference() {
      return new AtomicReference();
   }

   public static AtomicReference newReference(@Nullable Object initialValue) {
      return new AtomicReference(initialValue);
   }

   public static AtomicReferenceArray newReferenceArray(int length) {
      return new AtomicReferenceArray(length);
   }

   public static AtomicReferenceArray newReferenceArray(Object[] array) {
      return new AtomicReferenceArray(array);
   }
}
