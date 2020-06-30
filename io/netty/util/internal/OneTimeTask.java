package io.netty.util.internal;

import io.netty.util.internal.MpscLinkedQueueNode;

public abstract class OneTimeTask extends MpscLinkedQueueNode implements Runnable {
   public Runnable value() {
      return this;
   }
}
