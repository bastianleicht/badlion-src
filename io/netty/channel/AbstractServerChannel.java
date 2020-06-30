package io.netty.channel;

import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ServerChannel;
import java.net.SocketAddress;

public abstract class AbstractServerChannel extends AbstractChannel implements ServerChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);

   protected AbstractServerChannel() {
      super((Channel)null);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public SocketAddress remoteAddress() {
      return null;
   }

   protected SocketAddress remoteAddress0() {
      return null;
   }

   protected void doDisconnect() throws Exception {
      throw new UnsupportedOperationException();
   }

   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new AbstractServerChannel.DefaultServerUnsafe();
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected final Object filterOutboundMessage(Object msg) {
      throw new UnsupportedOperationException();
   }

   private final class DefaultServerUnsafe extends AbstractChannel.AbstractUnsafe {
      private DefaultServerUnsafe() {
         super();
      }

      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         this.safeSetFailure(promise, new UnsupportedOperationException());
      }
   }
}
