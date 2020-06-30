package com.google.common.base;

import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference extends WeakReference implements FinalizableReference {
   protected FinalizableWeakReference(Object referent, FinalizableReferenceQueue queue) {
      super(referent, queue.queue);
      queue.cleanUp();
   }
}
