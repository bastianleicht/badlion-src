package io.netty.channel;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import java.util.LinkedHashSet;
import java.util.Set;

public final class ChannelPromiseAggregator implements ChannelFutureListener {
   private final ChannelPromise aggregatePromise;
   private Set pendingPromises;

   public ChannelPromiseAggregator(ChannelPromise aggregatePromise) {
      if(aggregatePromise == null) {
         throw new NullPointerException("aggregatePromise");
      } else {
         this.aggregatePromise = aggregatePromise;
      }
   }

   public ChannelPromiseAggregator add(ChannelPromise... promises) {
      if(promises == null) {
         throw new NullPointerException("promises");
      } else if(promises.length == 0) {
         return this;
      } else {
         synchronized(this) {
            if(this.pendingPromises == null) {
               int size;
               if(promises.length > 1) {
                  size = promises.length;
               } else {
                  size = 2;
               }

               this.pendingPromises = new LinkedHashSet(size);
            }

            for(ChannelPromise p : promises) {
               if(p != null) {
                  this.pendingPromises.add(p);
                  p.addListener(this);
               }
            }

            return this;
         }
      }
   }

   public synchronized void operationComplete(ChannelFuture future) throws Exception {
      if(this.pendingPromises == null) {
         this.aggregatePromise.setSuccess();
      } else {
         this.pendingPromises.remove(future);
         if(!future.isSuccess()) {
            this.aggregatePromise.setFailure(future.cause());

            for(ChannelPromise pendingFuture : this.pendingPromises) {
               pendingFuture.setFailure(future.cause());
            }
         } else if(this.pendingPromises.isEmpty()) {
            this.aggregatePromise.setSuccess();
         }
      }

   }
}
