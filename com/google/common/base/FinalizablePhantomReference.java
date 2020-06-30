package com.google.common.base;

import com.google.common.base.FinalizableReference;
import com.google.common.base.FinalizableReferenceQueue;
import java.lang.ref.PhantomReference;

public abstract class FinalizablePhantomReference extends PhantomReference implements FinalizableReference {
   protected FinalizablePhantomReference(Object referent, FinalizableReferenceQueue queue) {
      super(referent, queue.queue);
      queue.cleanUp();
   }
}
