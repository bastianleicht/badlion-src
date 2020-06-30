package io.netty.channel;

import io.netty.channel.ChannelPromise;
import java.util.ArrayDeque;
import java.util.Queue;

public final class ChannelFlushPromiseNotifier {
   private long writeCounter;
   private final Queue flushCheckpoints;
   private final boolean tryNotify;

   public ChannelFlushPromiseNotifier(boolean tryNotify) {
      this.flushCheckpoints = new ArrayDeque();
      this.tryNotify = tryNotify;
   }

   public ChannelFlushPromiseNotifier() {
      this(false);
   }

   /** @deprecated */
   @Deprecated
   public ChannelFlushPromiseNotifier add(ChannelPromise promise, int pendingDataSize) {
      return this.add(promise, (long)pendingDataSize);
   }

   public ChannelFlushPromiseNotifier add(ChannelPromise promise, long pendingDataSize) {
      if(promise == null) {
         throw new NullPointerException("promise");
      } else if(pendingDataSize < 0L) {
         throw new IllegalArgumentException("pendingDataSize must be >= 0 but was " + pendingDataSize);
      } else {
         long checkpoint = this.writeCounter + pendingDataSize;
         if(promise instanceof ChannelFlushPromiseNotifier.FlushCheckpoint) {
            ChannelFlushPromiseNotifier.FlushCheckpoint cp = (ChannelFlushPromiseNotifier.FlushCheckpoint)promise;
            cp.flushCheckpoint(checkpoint);
            this.flushCheckpoints.add(cp);
         } else {
            this.flushCheckpoints.add(new ChannelFlushPromiseNotifier.DefaultFlushCheckpoint(checkpoint, promise));
         }

         return this;
      }
   }

   public ChannelFlushPromiseNotifier increaseWriteCounter(long delta) {
      if(delta < 0L) {
         throw new IllegalArgumentException("delta must be >= 0 but was " + delta);
      } else {
         this.writeCounter += delta;
         return this;
      }
   }

   public long writeCounter() {
      return this.writeCounter;
   }

   public ChannelFlushPromiseNotifier notifyPromises() {
      this.notifyPromises0((Throwable)null);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public ChannelFlushPromiseNotifier notifyFlushFutures() {
      return this.notifyPromises();
   }

   public ChannelFlushPromiseNotifier notifyPromises(Throwable cause) {
      this.notifyPromises();

      while(true) {
         ChannelFlushPromiseNotifier.FlushCheckpoint cp = (ChannelFlushPromiseNotifier.FlushCheckpoint)this.flushCheckpoints.poll();
         if(cp == null) {
            return this;
         }

         if(this.tryNotify) {
            cp.promise().tryFailure(cause);
         } else {
            cp.promise().setFailure(cause);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause) {
      return this.notifyPromises(cause);
   }

   public ChannelFlushPromiseNotifier notifyPromises(Throwable cause1, Throwable cause2) {
      this.notifyPromises0(cause1);

      while(true) {
         ChannelFlushPromiseNotifier.FlushCheckpoint cp = (ChannelFlushPromiseNotifier.FlushCheckpoint)this.flushCheckpoints.poll();
         if(cp == null) {
            return this;
         }

         if(this.tryNotify) {
            cp.promise().tryFailure(cause2);
         } else {
            cp.promise().setFailure(cause2);
         }
      }
   }

   /** @deprecated */
   @Deprecated
   public ChannelFlushPromiseNotifier notifyFlushFutures(Throwable cause1, Throwable cause2) {
      return this.notifyPromises(cause1, cause2);
   }

   private void notifyPromises0(Throwable cause) {
      if(this.flushCheckpoints.isEmpty()) {
         this.writeCounter = 0L;
      } else {
         long writeCounter = this.writeCounter;

         while(true) {
            ChannelFlushPromiseNotifier.FlushCheckpoint cp = (ChannelFlushPromiseNotifier.FlushCheckpoint)this.flushCheckpoints.peek();
            if(cp == null) {
               this.writeCounter = 0L;
               break;
            }

            if(cp.flushCheckpoint() > writeCounter) {
               if(writeCounter > 0L && this.flushCheckpoints.size() == 1) {
                  this.writeCounter = 0L;
                  cp.flushCheckpoint(cp.flushCheckpoint() - writeCounter);
               }
               break;
            }

            this.flushCheckpoints.remove();
            ChannelPromise promise = cp.promise();
            if(cause == null) {
               if(this.tryNotify) {
                  promise.trySuccess();
               } else {
                  promise.setSuccess();
               }
            } else if(this.tryNotify) {
               promise.tryFailure(cause);
            } else {
               promise.setFailure(cause);
            }
         }

         long newWriteCounter = this.writeCounter;
         if(newWriteCounter >= 549755813888L) {
            this.writeCounter = 0L;

            for(ChannelFlushPromiseNotifier.FlushCheckpoint cp : this.flushCheckpoints) {
               cp.flushCheckpoint(cp.flushCheckpoint() - newWriteCounter);
            }
         }

      }
   }

   private static class DefaultFlushCheckpoint implements ChannelFlushPromiseNotifier.FlushCheckpoint {
      private long checkpoint;
      private final ChannelPromise future;

      DefaultFlushCheckpoint(long checkpoint, ChannelPromise future) {
         this.checkpoint = checkpoint;
         this.future = future;
      }

      public long flushCheckpoint() {
         return this.checkpoint;
      }

      public void flushCheckpoint(long checkpoint) {
         this.checkpoint = checkpoint;
      }

      public ChannelPromise promise() {
         return this.future;
      }
   }

   interface FlushCheckpoint {
      long flushCheckpoint();

      void flushCheckpoint(long var1);

      ChannelPromise promise();
   }
}
