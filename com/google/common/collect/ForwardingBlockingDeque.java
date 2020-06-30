package com.google.common.collect;

import com.google.common.collect.ForwardingDeque;
import java.util.Collection;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

public abstract class ForwardingBlockingDeque extends ForwardingDeque implements BlockingDeque {
   protected abstract BlockingDeque delegate();

   public int remainingCapacity() {
      return this.delegate().remainingCapacity();
   }

   public void putFirst(Object e) throws InterruptedException {
      this.delegate().putFirst(e);
   }

   public void putLast(Object e) throws InterruptedException {
      this.delegate().putLast(e);
   }

   public boolean offerFirst(Object e, long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().offerFirst(e, timeout, unit);
   }

   public boolean offerLast(Object e, long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().offerLast(e, timeout, unit);
   }

   public Object takeFirst() throws InterruptedException {
      return this.delegate().takeFirst();
   }

   public Object takeLast() throws InterruptedException {
      return this.delegate().takeLast();
   }

   public Object pollFirst(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().pollFirst(timeout, unit);
   }

   public Object pollLast(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().pollLast(timeout, unit);
   }

   public void put(Object e) throws InterruptedException {
      this.delegate().put(e);
   }

   public boolean offer(Object e, long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().offer(e, timeout, unit);
   }

   public Object take() throws InterruptedException {
      return this.delegate().take();
   }

   public Object poll(long timeout, TimeUnit unit) throws InterruptedException {
      return this.delegate().poll(timeout, unit);
   }

   public int drainTo(Collection c) {
      return this.delegate().drainTo(c);
   }

   public int drainTo(Collection c, int maxElements) {
      return this.delegate().drainTo(c, maxElements);
   }
}
