package io.netty.util.internal;

import io.netty.util.internal.MpscLinkedQueueNode;
import io.netty.util.internal.MpscLinkedQueuePad1;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class MpscLinkedQueueTailRef extends MpscLinkedQueuePad1 {
   private static final long serialVersionUID = 8717072462993327429L;
   private static final AtomicReferenceFieldUpdater UPDATER;
   private transient volatile MpscLinkedQueueNode tailRef;

   protected final MpscLinkedQueueNode tailRef() {
      return this.tailRef;
   }

   protected final void setTailRef(MpscLinkedQueueNode tailRef) {
      this.tailRef = tailRef;
   }

   protected final MpscLinkedQueueNode getAndSetTailRef(MpscLinkedQueueNode tailRef) {
      return (MpscLinkedQueueNode)UPDATER.getAndSet(this, tailRef);
   }

   static {
      AtomicReferenceFieldUpdater<MpscLinkedQueueTailRef, MpscLinkedQueueNode> updater = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueTailRef.class, "tailRef");
      if(updater == null) {
         updater = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueTailRef.class, MpscLinkedQueueNode.class, "tailRef");
      }

      UPDATER = updater;
   }
}
