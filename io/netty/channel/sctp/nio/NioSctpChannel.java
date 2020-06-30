package io.netty.channel.sctp.nio;

import com.sun.nio.sctp.Association;
import com.sun.nio.sctp.MessageInfo;
import com.sun.nio.sctp.NotificationHandler;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.nio.AbstractNioMessageChannel;
import io.netty.channel.sctp.DefaultSctpChannelConfig;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpMessage;
import io.netty.channel.sctp.SctpNotificationHandler;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class NioSctpChannel extends AbstractNioMessageChannel implements SctpChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioSctpChannel.class);
   private final SctpChannelConfig config;
   private final NotificationHandler notificationHandler;
   private RecvByteBufAllocator.Handle allocHandle;

   private static com.sun.nio.sctp.SctpChannel newSctpChannel() {
      try {
         return com.sun.nio.sctp.SctpChannel.open();
      } catch (IOException var1) {
         throw new ChannelException("Failed to open a sctp channel.", var1);
      }
   }

   public NioSctpChannel() {
      this(newSctpChannel());
   }

   public NioSctpChannel(com.sun.nio.sctp.SctpChannel sctpChannel) {
      this((Channel)null, sctpChannel);
   }

   public NioSctpChannel(Channel parent, com.sun.nio.sctp.SctpChannel sctpChannel) {
      super(parent, sctpChannel, 1);

      try {
         sctpChannel.configureBlocking(false);
         this.config = new NioSctpChannel.NioSctpChannelConfig(this, sctpChannel);
         this.notificationHandler = new SctpNotificationHandler(this);
      } catch (IOException var6) {
         try {
            sctpChannel.close();
         } catch (IOException var5) {
            if(logger.isWarnEnabled()) {
               logger.warn("Failed to close a partially initialized sctp channel.", (Throwable)var5);
            }
         }

         throw new ChannelException("Failed to enter non-blocking mode.", var6);
      }
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   public SctpServerChannel parent() {
      return (SctpServerChannel)super.parent();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public Association association() {
      try {
         return this.javaChannel().association();
      } catch (IOException var2) {
         return null;
      }
   }

   public Set allLocalAddresses() {
      try {
         Set<SocketAddress> allLocalAddresses = this.javaChannel().getAllLocalAddresses();
         Set<InetSocketAddress> addresses = new LinkedHashSet(allLocalAddresses.size());

         for(SocketAddress socketAddress : allLocalAddresses) {
            addresses.add((InetSocketAddress)socketAddress);
         }

         return addresses;
      } catch (Throwable var5) {
         return Collections.emptySet();
      }
   }

   public SctpChannelConfig config() {
      return this.config;
   }

   public Set allRemoteAddresses() {
      try {
         Set<SocketAddress> allLocalAddresses = this.javaChannel().getRemoteAddresses();
         Set<InetSocketAddress> addresses = new HashSet(allLocalAddresses.size());

         for(SocketAddress socketAddress : allLocalAddresses) {
            addresses.add((InetSocketAddress)socketAddress);
         }

         return addresses;
      } catch (Throwable var5) {
         return Collections.emptySet();
      }
   }

   protected com.sun.nio.sctp.SctpChannel javaChannel() {
      return (com.sun.nio.sctp.SctpChannel)super.javaChannel();
   }

   public boolean isActive() {
      com.sun.nio.sctp.SctpChannel ch = this.javaChannel();
      return ch.isOpen() && this.association() != null;
   }

   protected SocketAddress localAddress0() {
      try {
         Iterator<SocketAddress> i = this.javaChannel().getAllLocalAddresses().iterator();
         if(i.hasNext()) {
            return (SocketAddress)i.next();
         }
      } catch (IOException var2) {
         ;
      }

      return null;
   }

   protected SocketAddress remoteAddress0() {
      try {
         Iterator<SocketAddress> i = this.javaChannel().getRemoteAddresses().iterator();
         if(i.hasNext()) {
            return (SocketAddress)i.next();
         }
      } catch (IOException var2) {
         ;
      }

      return null;
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.javaChannel().bind(localAddress);
   }

   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if(localAddress != null) {
         this.javaChannel().bind(localAddress);
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

   protected int doReadMessages(List buf) throws Exception {
      // $FF: Couldn't be decompiled
   }

   protected boolean doWriteMessage(Object msg, ChannelOutboundBuffer in) throws Exception {
      SctpMessage packet = (SctpMessage)msg;
      ByteBuf data = packet.content();
      int dataLen = data.readableBytes();
      if(dataLen == 0) {
         return true;
      } else {
         ByteBufAllocator alloc = this.alloc();
         boolean needsCopy = data.nioBufferCount() != 1;
         if(!needsCopy && !data.isDirect() && alloc.isDirectBufferPooled()) {
            needsCopy = true;
         }

         ByteBuffer nioData;
         if(!needsCopy) {
            nioData = data.nioBuffer();
         } else {
            data = alloc.directBuffer(dataLen).writeBytes(data);
            nioData = data.nioBuffer();
         }

         MessageInfo mi = MessageInfo.createOutgoing(this.association(), (SocketAddress)null, packet.streamIdentifier());
         mi.payloadProtocolID(packet.protocolIdentifier());
         mi.streamNumber(packet.streamIdentifier());
         int writtenBytes = this.javaChannel().send(nioData, mi);
         return writtenBytes > 0;
      }
   }

   protected final Object filterOutboundMessage(Object msg) throws Exception {
      if(msg instanceof SctpMessage) {
         SctpMessage m = (SctpMessage)msg;
         ByteBuf buf = m.content();
         return buf.isDirect() && buf.nioBufferCount() == 1?m:new SctpMessage(m.protocolIdentifier(), m.streamIdentifier(), this.newDirectBuffer(m, buf));
      } else {
         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + " (expected: " + StringUtil.simpleClassName(SctpMessage.class));
      }
   }

   public ChannelFuture bindAddress(InetAddress localAddress) {
      return this.bindAddress(localAddress, this.newPromise());
   }

   public ChannelFuture bindAddress(final InetAddress localAddress, final ChannelPromise promise) {
      if(this.eventLoop().inEventLoop()) {
         try {
            this.javaChannel().bindAddress(localAddress);
            promise.setSuccess();
         } catch (Throwable var4) {
            promise.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               NioSctpChannel.this.bindAddress(localAddress, promise);
            }
         });
      }

      return promise;
   }

   public ChannelFuture unbindAddress(InetAddress localAddress) {
      return this.unbindAddress(localAddress, this.newPromise());
   }

   public ChannelFuture unbindAddress(final InetAddress localAddress, final ChannelPromise promise) {
      if(this.eventLoop().inEventLoop()) {
         try {
            this.javaChannel().unbindAddress(localAddress);
            promise.setSuccess();
         } catch (Throwable var4) {
            promise.setFailure(var4);
         }
      } else {
         this.eventLoop().execute(new Runnable() {
            public void run() {
               NioSctpChannel.this.unbindAddress(localAddress, promise);
            }
         });
      }

      return promise;
   }

   private final class NioSctpChannelConfig extends DefaultSctpChannelConfig {
      private NioSctpChannelConfig(NioSctpChannel channel, com.sun.nio.sctp.SctpChannel javaChannel) {
         super(channel, javaChannel);
      }

      protected void autoReadCleared() {
         NioSctpChannel.this.setReadPending(false);
      }
   }
}
