package io.netty.channel.epoll;

import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.epoll.AbstractEpollChannel;
import io.netty.channel.epoll.EpollEventLoop;
import io.netty.channel.epoll.EpollServerSocketChannelConfig;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public final class EpollServerSocketChannel extends AbstractEpollChannel implements ServerSocketChannel {
   private final EpollServerSocketChannelConfig config = new EpollServerSocketChannelConfig(this);
   private volatile InetSocketAddress local;

   public EpollServerSocketChannel() {
      super(Native.socketStreamFd(), 4);
   }

   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof EpollEventLoop;
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      InetSocketAddress addr = (InetSocketAddress)localAddress;
      checkResolvable(addr);
      Native.bind(this.fd, addr.getAddress(), addr.getPort());
      this.local = Native.localAddress(this.fd);
      Native.listen(this.fd, this.config.getBacklog());
      this.active = true;
   }

   public EpollServerSocketChannelConfig config() {
      return this.config;
   }

   protected InetSocketAddress localAddress0() {
      return this.local;
   }

   protected InetSocketAddress remoteAddress0() {
      return null;
   }

   protected AbstractEpollChannel.AbstractEpollUnsafe newUnsafe() {
      return new EpollServerSocketChannel.EpollServerSocketUnsafe();
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      throw new UnsupportedOperationException();
   }

   protected Object filterOutboundMessage(Object msg) throws Exception {
      throw new UnsupportedOperationException();
   }

   final class EpollServerSocketUnsafe extends AbstractEpollChannel.AbstractEpollUnsafe {
      EpollServerSocketUnsafe() {
         super();
      }

      public void connect(SocketAddress socketAddress, SocketAddress socketAddress2, ChannelPromise channelPromise) {
         channelPromise.setFailure(new UnsupportedOperationException());
      }

      void epollInReady() {
         assert EpollServerSocketChannel.this.eventLoop().inEventLoop();

         ChannelPipeline pipeline = EpollServerSocketChannel.this.pipeline();
         Throwable exception = null;

         try {
            try {
               while(true) {
                  int socketFd = Native.accept(EpollServerSocketChannel.this.fd);
                  if(socketFd == -1) {
                     break;
                  }

                  try {
                     this.readPending = false;
                     pipeline.fireChannelRead(new EpollSocketChannel(EpollServerSocketChannel.this, socketFd));
                  } catch (Throwable var9) {
                     pipeline.fireChannelReadComplete();
                     pipeline.fireExceptionCaught(var9);
                  }
               }
            } catch (Throwable var10) {
               exception = var10;
            }

            pipeline.fireChannelReadComplete();
            if(exception != null) {
               pipeline.fireExceptionCaught(exception);
            }
         } finally {
            if(!EpollServerSocketChannel.this.config.isAutoRead() && !this.readPending) {
               this.clearEpollIn0();
            }

         }

      }
   }
}
