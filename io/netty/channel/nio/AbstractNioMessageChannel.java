package io.netty.channel.nio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ServerChannel;
import io.netty.channel.nio.AbstractNioChannel;
import java.io.IOException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractNioMessageChannel extends AbstractNioChannel {
   protected AbstractNioMessageChannel(Channel parent, SelectableChannel ch, int readInterestOp) {
      super(parent, ch, readInterestOp);
   }

   protected AbstractNioChannel.AbstractNioUnsafe newUnsafe() {
      return new AbstractNioMessageChannel.NioMessageUnsafe();
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      SelectionKey key = this.selectionKey();
      int interestOps = key.interestOps();

      while(true) {
         Object msg = in.current();
         if(msg == null) {
            if((interestOps & 4) != 0) {
               key.interestOps(interestOps & -5);
            }
            break;
         }

         try {
            boolean done = false;

            for(int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
               if(this.doWriteMessage(msg, in)) {
                  done = true;
                  break;
               }
            }

            if(!done) {
               if((interestOps & 4) == 0) {
                  key.interestOps(interestOps | 4);
               }
               break;
            }

            in.remove();
         } catch (IOException var7) {
            if(!this.continueOnWriteError()) {
               throw var7;
            }

            in.remove(var7);
         }
      }

   }

   protected boolean continueOnWriteError() {
      return false;
   }

   protected abstract int doReadMessages(List var1) throws Exception;

   protected abstract boolean doWriteMessage(Object var1, ChannelOutboundBuffer var2) throws Exception;

   private final class NioMessageUnsafe extends AbstractNioChannel.AbstractNioUnsafe {
      private final List readBuf;

      private NioMessageUnsafe() {
         super();
         this.readBuf = new ArrayList();
      }

      public void read() {
         assert AbstractNioMessageChannel.this.eventLoop().inEventLoop();

         ChannelConfig config = AbstractNioMessageChannel.this.config();
         if(!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
            this.removeReadOp();
         } else {
            int maxMessagesPerRead = config.getMaxMessagesPerRead();
            ChannelPipeline pipeline = AbstractNioMessageChannel.this.pipeline();
            boolean closed = false;
            Throwable exception = null;

            try {
               try {
                  while(true) {
                     int localRead = AbstractNioMessageChannel.this.doReadMessages(this.readBuf);
                     if(localRead == 0) {
                        break;
                     }

                     if(localRead < 0) {
                        closed = true;
                        break;
                     }

                     if(!config.isAutoRead() || this.readBuf.size() >= maxMessagesPerRead) {
                        break;
                     }
                  }
               } catch (Throwable var11) {
                  exception = var11;
               }

               AbstractNioMessageChannel.this.setReadPending(false);
               int size = this.readBuf.size();

               for(int i = 0; i < size; ++i) {
                  pipeline.fireChannelRead(this.readBuf.get(i));
               }

               this.readBuf.clear();
               pipeline.fireChannelReadComplete();
               if(exception != null) {
                  if(exception instanceof IOException) {
                     closed = !(AbstractNioMessageChannel.this instanceof ServerChannel);
                  }

                  pipeline.fireExceptionCaught(exception);
               }

               if(closed && AbstractNioMessageChannel.this.isOpen()) {
                  this.close(this.voidPromise());
               }
            } finally {
               if(!config.isAutoRead() && !AbstractNioMessageChannel.this.isReadPending()) {
                  this.removeReadOp();
               }

            }

         }
      }
   }
}
