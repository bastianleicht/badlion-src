package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.oio.OioByteStreamChannel;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.oio.DefaultOioSocketChannelConfig;
import io.netty.channel.socket.oio.OioSocketChannelConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class OioSocketChannel extends OioByteStreamChannel implements SocketChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioSocketChannel.class);
   private final Socket socket;
   private final OioSocketChannelConfig config;

   public OioSocketChannel() {
      this(new Socket());
   }

   public OioSocketChannel(Socket socket) {
      this((Channel)null, socket);
   }

   public OioSocketChannel(Channel parent, Socket socket) {
      super(parent);
      this.socket = socket;
      this.config = new DefaultOioSocketChannelConfig(this, socket);
      boolean success = false;

      try {
         if(socket.isConnected()) {
            this.activate(socket.getInputStream(), socket.getOutputStream());
         }

         socket.setSoTimeout(1000);
         success = true;
      } catch (Exception var12) {
         throw new ChannelException("failed to initialize a socket", var12);
      } finally {
         if(!success) {
            try {
               socket.close();
            } catch (IOException var11) {
               logger.warn("Failed to close a socket.", (Throwable)var11);
            }
         }

      }

   }

   public ServerSocketChannel parent() {
      return (ServerSocketChannel)super.parent();
   }

   public OioSocketChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   public boolean isActive() {
      return !this.socket.isClosed() && this.socket.isConnected();
   }

   public boolean isInputShutdown() {
      return super.isInputShutdown();
   }

   public boolean isOutputShutdown() {
      return this.socket.isOutputShutdown() || !this.isActive();
   }

   public ChannelFuture shutdownOutput() {
      return this.shutdownOutput(this.newPromise());
   }

   protected int doReadBytes(ByteBuf buf) throws Exception {
      if(this.socket.isClosed()) {
         return -1;
      } else {
         try {
            return super.doReadBytes(buf);
         } catch (SocketTimeoutException var3) {
            return 0;
         }
      }
   }

   public ChannelFuture shutdownOutput(final ChannelPromise future) {
      EventLoop loop = this.eventLoop();
      if(loop.inEventLoop()) {
         try {
            this.socket.shutdownOutput();
            future.setSuccess();
         } catch (Throwable var4) {
            future.setFailure(var4);
         }
      } else {
         loop.execute(new Runnable() {
            public void run() {
               OioSocketChannel.this.shutdownOutput(future);
            }
         });
      }

      return future;
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   protected SocketAddress localAddress0() {
      return this.socket.getLocalSocketAddress();
   }

   protected SocketAddress remoteAddress0() {
      return this.socket.getRemoteSocketAddress();
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.socket.bind(localAddress);
   }

   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if(localAddress != null) {
         this.socket.bind(localAddress);
      }

      boolean success = false;

      try {
         this.socket.connect(remoteAddress, this.config().getConnectTimeoutMillis());
         this.activate(this.socket.getInputStream(), this.socket.getOutputStream());
         success = true;
      } catch (SocketTimeoutException var9) {
         ConnectTimeoutException cause = new ConnectTimeoutException("connection timed out: " + remoteAddress);
         cause.setStackTrace(var9.getStackTrace());
         throw cause;
      } finally {
         if(!success) {
            this.doClose();
         }

      }

   }

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doClose() throws Exception {
      this.socket.close();
   }

   protected boolean checkInputShutdown() {
      if(this.isInputShutdown()) {
         try {
            Thread.sleep((long)this.config().getSoTimeout());
         } catch (Throwable var2) {
            ;
         }

         return true;
      } else {
         return false;
      }
   }

   protected void setReadPending(boolean readPending) {
      super.setReadPending(readPending);
   }
}
