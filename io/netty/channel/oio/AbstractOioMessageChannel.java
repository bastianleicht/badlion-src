package io.netty.channel.oio;

import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.oio.AbstractOioChannel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractOioMessageChannel extends AbstractOioChannel {
   private final List readBuf = new ArrayList();

   protected AbstractOioMessageChannel(Channel parent) {
      super(parent);
   }

   protected void doRead() {
      ChannelConfig config = this.config();
      ChannelPipeline pipeline = this.pipeline();
      boolean closed = false;
      int maxMessagesPerRead = config.getMaxMessagesPerRead();
      Throwable exception = null;
      int localRead = 0;

      try {
         while(true) {
            localRead = this.doReadMessages(this.readBuf);
            if(localRead == 0) {
               break;
            }

            if(localRead < 0) {
               closed = true;
               break;
            }

            if(this.readBuf.size() >= maxMessagesPerRead || !config.isAutoRead()) {
               break;
            }
         }
      } catch (Throwable var9) {
         exception = var9;
      }

      int size = this.readBuf.size();

      for(int i = 0; i < size; ++i) {
         pipeline.fireChannelRead(this.readBuf.get(i));
      }

      this.readBuf.clear();
      pipeline.fireChannelReadComplete();
      if(exception != null) {
         if(exception instanceof IOException) {
            closed = true;
         }

         this.pipeline().fireExceptionCaught(exception);
      }

      if(closed) {
         if(this.isOpen()) {
            this.unsafe().close(this.unsafe().voidPromise());
         }
      } else if(localRead == 0 && this.isActive()) {
         this.read();
      }

   }

   protected abstract int doReadMessages(List var1) throws Exception;
}
