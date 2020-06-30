package com.google.common.base;

import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.SoftReference;

public abstract class FinalizableSoftReference extends SoftReference implements FinalizableReference {
   protected FinalizableSoftReference(Object referent, FinalizableReferenceQueue queue) {
      super(referent, queue.queue);
      queue.cleanUp();
   }
}
