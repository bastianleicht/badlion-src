package com.google.common.collect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingQueue;
import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Queue;

@Beta
@GwtIncompatible("java.util.ArrayDeque")
public final class EvictingQueue extends ForwardingQueue implements Serializable {
   private final Queue delegate;
   @VisibleForTesting
   final int maxSize;
   private static final long serialVersionUID = 0L;

   private EvictingQueue(int maxSize) {
      Preconditions.checkArgument(maxSize >= 0, "maxSize (%s) must >= 0", new Object[]{Integer.valueOf(maxSize)});
      this.delegate = new ArrayDeque(maxSize);
      this.maxSize = maxSize;
   }

   public static EvictingQueue create(int maxSize) {
      return new EvictingQueue(maxSize);
   }

   public int remainingCapacity() {
      return this.maxSize - this.size();
   }

   protected Queue delegate() {
      return this.delegate;
   }

   public boolean offer(Object e) {
      return this.add(e);
   }

   public boolean add(Object e) {
      Preconditions.checkNotNull(e);
      if(this.maxSize == 0) {
         return true;
      } else {
         if(this.size() == this.maxSize) {
            this.delegate.remove();
         }

         this.delegate.add(e);
         return true;
      }
   }

   public boolean addAll(Collection collection) {
      return this.standardAddAll(collection);
   }

   public boolean contains(Object object) {
      return this.delegate().contains(Preconditions.checkNotNull(object));
   }

   public boolean remove(Object object) {
      return this.delegate().remove(Preconditions.checkNotNull(object));
   }
}
