package io.netty.util.internal;

import io.netty.util.internal.MpscLinkedQueueNode;
import io.netty.util.internal.MpscLinkedQueuePad0;
import io.netty.util.internal.PlatformDependent;
import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

abstract class MpscLinkedQueueHeadRef extends MpscLinkedQueuePad0 implements Serializable {
   private static final long serialVersionUID = 8467054865577874285L;
   private static final AtomicReferenceFieldUpdater UPDATER;
   private transient volatile MpscLinkedQueueNode headRef;

   protected final MpscLinkedQueueNode headRef() {
      return this.headRef;
   }

   protected final void setHeadRef(MpscLinkedQueueNode headRef) {
      this.headRef = headRef;
   }

   protected final void lazySetHeadRef(MpscLinkedQueueNode headRef) {
      UPDATER.lazySet(this, headRef);
   }

   static {
      AtomicReferenceFieldUpdater<MpscLinkedQueueHeadRef, MpscLinkedQueueNode> updater = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueHeadRef.class, "headRef");
      if(updater == null) {
         updater = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueHeadRef.class, MpscLinkedQueueNode.class, "headRef");
      }

      UPDATER = updater;
   }
}
