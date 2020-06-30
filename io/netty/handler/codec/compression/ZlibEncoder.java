package io.netty.handler.codec.compression;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;

public abstract class ZlibEncoder extends MessageToByteEncoder {
   protected ZlibEncoder() {
      super(false);
   }

   public abstract boolean isClosed();

   public abstract ChannelFuture close();

   public abstract ChannelFuture close(ChannelPromise var1);
}
