package io.netty.channel.socket.nio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.socket.DefaultSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.OneTimeTask;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.spi.SelectorProvider;

public class NioSocketChannel extends AbstractNioByteChannel implements SocketChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final SelectorProvider DEFAULT_SELECTOR_PROVIDER = SelectorProvider.provider();
   private final SocketChannelConfig config;

   private static java.nio.channels.SocketChannel newSocket(SelectorProvider provider) {
      try {
         return provider.openSocketChannel();
      } catch (IOException var2) {
         throw new ChannelException("Failed to open a socket.", var2);
      }
   }

   public NioSocketChannel() {
      this(newSocket(DEFAULT_SELECTOR_PROVIDER));
   }

   public NioSocketChannel(SelectorProvider provider) {
      this(newSocket(provider));
   }

   public NioSocketChannel(java.nio.channels.SocketChannel socket) {
      this((Channel)null, socket);
   }

   public NioSocketChannel(Channel parent, java.nio.channels.SocketChannel socket) {
      super(parent, socket);
      this.config = new NioSocketChannel.NioSocketChannelConfig(this, socket.socket());
   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public SocketChannelConfig config() {
      return this.config;
   }

   protected java.nio.channels.SocketChannel javaChannel() {
      return (java.nio.channels.SocketChannel)super.javaChannel();
   }

   public boolean isActive() {
      java.nio.channels.SocketChannel ch = this.javaChannel();
      return ch.isOpen() && ch.isConnected();
   }

   public boolean isInputShutdown() {
      return super.isInputShutdown();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public boolean isOutputShutdown() {
      return this.javaChannel().socket().isOutputShutdown() || !this.isActive();
   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   public ChannelFuture shutdownOutput(final ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if(loop.inEventLoop()) {
         try {
            this.javaChannel().socket().shutdownOutput();
            promise.setSuccess();
         } catch (Throwable var4) {
            promise.setFailure(var4);
         }
      } else {
         loop.execute(new OneTimeTask() {
            public void run() {
               NioSocketChannel.this.shutdownOutput(promise);
            }
         });
      }

      return promise;
   }

   protected SocketAddress localAddress0() {
      return this.javaChannel().socket().getLocalSocketAddress();
   }

   protected SocketAddress remoteAddress0() {
      return this.javaChannel().socket().getRemoteSocketAddress();
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.javaChannel().socket().bind(localAddress);
   }

   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if(localAddress != null) {
         this.javaChannel().socket().bind(localAddress);
      }

      boolean success = false;

      boolean var5;
      try {
         boolean connected = this.javaChannel().connect(remoteAddress);
         if(!connected) {
            this.selectionKey().interestOps(8);
         }

         success = true;
         var5 = connected;
      } finally {
         if(!success) {
            this.doClose();
         }

      }

      return var5;
   }

   protected void doFinishConnect() throws Exception {
      if(!this.javaChannel().finishConnect()) {
         throw new Error();
      }
   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   protected int doReadBytes(ByteBuf byteBuf) throws Exception {
      return byteBuf.writeBytes((ScatteringByteChannel)this.javaChannel(), byteBuf.writableBytes());
   }

   protected int doWriteBytes(ByteBuf buf) throws Exception {
      int expectedWrittenBytes = buf.readableBytes();
      return buf.readBytes((GatheringByteChannel)this.javaChannel(), expectedWrittenBytes);
   }

   protected long doWriteFileRegion(FileRegion region) throws Exception {
      long position = region.transfered();
      return region.transferTo(this.javaChannel(), position);
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while(true) {
         int size = in.size();
         if(size == 0) {
            this.clearOpWrite();
            break;
         }

         long writtenBytes;
         boolean done;
         boolean setOpWrite;
         writtenBytes = 0L;
         done = false;
         setOpWrite = false;
         ByteBuffer[] nioBuffers = in.nioBuffers();
         int nioBufferCnt = in.nioBufferCount();
         long expectedWrittenBytes = in.nioBufferSize();
         java.nio.channels.SocketChannel ch = this.javaChannel();
         label16:
         switch(nioBufferCnt) {
         case 0:
            super.doWrite(in);
            return;
         case 1:
            ByteBuffer nioBuffer = nioBuffers[0];
            int i = this.config().getWriteSpinCount() - 1;

            while(true) {
               if(i < 0) {
                  break label16;
               }

               int localWrittenBytes = ch.write(nioBuffer);
               if(localWrittenBytes == 0) {
                  setOpWrite = true;
                  break label16;
               }

               expectedWrittenBytes -= (long)localWrittenBytes;
               writtenBytes += (long)localWrittenBytes;
               if(expectedWrittenBytes == 0L) {
                  done = true;
                  break label16;
               }

               --i;
            }
         default:
            for(int i = this.config().getWriteSpinCount() - 1; i >= 0; --i) {
               long localWrittenBytes = ch.write(nioBuffers, 0, nioBufferCnt);
               if(localWrittenBytes == 0L) {
                  setOpWrite = true;
                  break;
               }

               expectedWrittenBytes -= localWrittenBytes;
               writtenBytes += localWrittenBytes;
               if(expectedWrittenBytes == 0L) {
                  done = true;
                  break;
               }
            }
         }

         in.removeBytes(writtenBytes);
         if(!done) {
            this.incompleteWrite(setOpWrite);
            break;
         }
      }

   }

   private final class NioSocketChannelConfig extends DefaultSocketChannelConfig {
      private NioSocketChannelConfig(NioSocketChannel channel, Socket javaSocket) {
         super(channel, javaSocket);
      }

      protected void autoReadCleared() {
         NioSocketChannel.this.setReadPending(false);
      }
   }
}
