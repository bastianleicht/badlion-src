package io.netty.channel.oio;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.ThreadPerChannelEventLoop;
import java.net.ConnectException;
import java.net.SocketAddress;

public abstract class AbstractOioChannel extends AbstractChannel {
   protected static final int SO_TIMEOUT = 1000;
   private volatile boolean readPending;
   private final Runnable readTask = new Runnable() {
      public void run() {
         if(AbstractOioChannel.this.isReadPending() || AbstractOioChannel.this.config().isAutoRead()) {
            AbstractOioChannel.this.setReadPending(false);
            AbstractOioChannel.this.doRead();
         }
      }
   };

   protected AbstractOioChannel(Channel parent) {
      super(parent);
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new AbstractOioChannel.DefaultOioUnsafe();
   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof ThreadPerChannelEventLoop;
   }

   protected abstract void doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   protected void doBeginRead() throws Exception {
      if(!this.isReadPending()) {
         this.setReadPending(true);
         this.eventLoop().execute(this.readTask);
      }
   }

   protected abstract void doRead();

   protected boolean isReadPending() {
      return this.readPending;
   }

   protected void setReadPending(boolean readPending) {
      this.readPending = readPending;
   }

   private final class DefaultOioUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultOioUnsafe() {
         super();
      }

      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if(promise.setUncancellable() && this.ensureOpen(promise)) {
            try {
               boolean wasActive = AbstractOioChannel.this.isActive();
               AbstractOioChannel.this.doConnect(remoteAddress, localAddress);
               this.safeSetSuccess(promise);
               if(!wasActive && AbstractOioChannel.this.isActive()) {
                  AbstractOioChannel.this.pipeline().fireChannelActive();
               }
            } catch (Throwable var6) {
               Throwable t = var6;
               if(var6 instanceof ConnectException) {
                  Throwable newT = new ConnectException(var6.getMessage() + ": " + remoteAddress);
                  newT.setStackTrace(var6.getStackTrace());
                  t = newT;
               }

               this.safeSetFailure(promise, t);
               this.closeIfClosed();
            }

         }
      }
   }
}
