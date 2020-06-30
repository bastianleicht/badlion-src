package io.netty.util.internal;

import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public abstract class MpscLinkedQueueNode {
   private static final AtomicReferenceFieldUpdater nextUpdater;
   private volatile MpscLinkedQueueNode next;

   final MpscLinkedQueueNode next() {
      return this.next;
   }

   final void setNext(MpscLinkedQueueNode newNext) {
      nextUpdater.lazySet(this, newNext);
   }

   public abstract Object value();

   protected Object clearMaybe() {
      return this.value();
   }

   void unlink() {
      this.setNext((MpscLinkedQueueNode)null);
   }

   static {
      AtomicReferenceFieldUpdater<MpscLinkedQueueNode, MpscLinkedQueueNode> u = PlatformDependent.newAtomicReferenceFieldUpdater(MpscLinkedQueueNode.class, "next");
      if(u == null) {
         u = AtomicReferenceFieldUpdater.newUpdater(MpscLinkedQueueNode.class, MpscLinkedQueueNode.class, "next");
      }

      nextUpdater = u;
   }
}
