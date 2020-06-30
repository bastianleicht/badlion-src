package io.netty.util.internal;

import io.netty.util.Recycler;
import io.netty.util.internal.MpscLinkedQueueNode;

public abstract class RecyclableMpscLinkedQueueNode extends MpscLinkedQueueNode {
   private final Recycler.Handle handle;

   protected RecyclableMpscLinkedQueueNode(Recycler.Handle handle) {
      if(handle == null) {
         throw new NullPointerException("handle");
      } else {
         this.handle = handle;
      }
   }

   final void unlink() {
      super.unlink();
      this.recycle(this.handle);
   }

   protected abstract void recycle(Recycler.Handle var1);
}
