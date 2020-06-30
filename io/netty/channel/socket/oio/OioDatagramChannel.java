package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.oio.AbstractOioMessageChannel;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramChannelConfig;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.DefaultDatagramChannelConfig;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;

public class OioDatagramChannel extends AbstractOioMessageChannel implements DatagramChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(OioDatagramChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(true);
   private static final String EXPECTED_TYPES = " (expected: " + StringUtil.simpleClassName(DatagramPacket.class) + ", " + StringUtil.simpleClassName(AddressedEnvelope.class) + '<' + StringUtil.simpleClassName(ByteBuf.class) + ", " + StringUtil.simpleClassName(SocketAddress.class) + ">, " + StringUtil.simpleClassName(ByteBuf.class) + ')';
   private final MulticastSocket socket;
   private final DatagramChannelConfig config;
   private final java.net.DatagramPacket tmpPacket;
   private RecvByteBufAllocator.Handle allocHandle;

   private static MulticastSocket newSocket() {
      try {
         return new MulticastSocket((SocketAddress)null);
      } catch (Exception var1) {
         throw new ChannelException("failed to create a new socket", var1);
      }
   }

   public OioDatagramChannel() {
      this(newSocket());
   }

   public OioDatagramChannel(MulticastSocket socket) {
      super((Channel)null);
      this.tmpPacket = new java.net.DatagramPacket(EmptyArrays.EMPTY_BYTES, 0);
      boolean success = false;

      try {
         socket.setSoTimeout(1000);
         socket.setBroadcast(false);
         success = true;
      } catch (SocketException var7) {
         throw new ChannelException("Failed to configure the datagram socket timeout.", var7);
      } finally {
         if(!success) {
            socket.close();
         }

      }

      this.socket = socket;
      this.config = new DefaultDatagramChannelConfig(this, socket);
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   public DatagramChannelConfig config() {
      return this.config;
   }

   public boolean isOpen() {
      return !this.socket.isClosed();
   }

   public boolean isActive() {
      return this.isOpen() && (((Boolean)this.config.getOption(ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION)).booleanValue() && this.isRegistered() || this.socket.isBound());
   }

   public boolean isConnected() {
      return this.socket.isConnected();
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

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }

   protected void doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      if(localAddress != null) {
         this.socket.bind(localAddress);
      }

      boolean success = false;

      try {
         this.socket.connect(remoteAddress);
         success = true;
      } finally {
         if(!success) {
            try {
               this.socket.close();
            } catch (Throwable var10) {
               logger.warn("Failed to close a socket.", var10);
            }
         }

      }

   }

   protected void doDisconnect() throws Exception {
      this.socket.disconnect();
   }

   protected void doClose() throws Exception {
      this.socket.close();
   }

   protected int doReadMessages(List buf) throws Exception {
      DatagramChannelConfig config = this.config();
      RecvByteBufAllocator.Handle allocHandle = this.allocHandle;
      if(allocHandle == null) {
         this.allocHandle = allocHandle = config.getRecvByteBufAllocator().newHandle();
      }

      ByteBuf data = config.getAllocator().heapBuffer(allocHandle.guess());
      boolean free = true;

      int var19;
      try {
         this.tmpPacket.setData(data.array(), data.arrayOffset(), data.capacity());
         this.socket.receive(this.tmpPacket);
         InetSocketAddress remoteAddr = (InetSocketAddress)this.tmpPacket.getSocketAddress();
         readBytes = this.tmpPacket.getLength();
         allocHandle.record(var19);
         buf.add(new DatagramPacket(data.writerIndex(var19), this.localAddress(), remoteAddr));
         free = false;
         byte var8 = 1;
         return var8;
      } catch (SocketTimeoutException var14) {
         var19 = 0;
      } catch (SocketException var15) {
         if(!var15.getMessage().toLowerCase(Locale.US).contains("socket closed")) {
            throw var15;
         }

         var19 = -1;
         return var19;
      } catch (Throwable var16) {
         PlatformDependent.throwException(var16);
         var19 = -1;
         return var19;
      } finally {
         if(free) {
            data.release();
         }

      }

      return var19;
   }

   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      while(true) {
         Object o = in.current();
         if(o == null) {
            return;
         }

         ByteBuf data;
         SocketAddress remoteAddress;
         if(o instanceof AddressedEnvelope) {
            AddressedEnvelope<ByteBuf, SocketAddress> envelope = (AddressedEnvelope)o;
            remoteAddress = envelope.recipient();
            data = (ByteBuf)envelope.content();
         } else {
            data = (ByteBuf)o;
            remoteAddress = null;
         }

         int length = data.readableBytes();
         if(remoteAddress != null) {
            this.tmpPacket.setSocketAddress(remoteAddress);
         }

         if(data.hasArray()) {
            this.tmpPacket.setData(data.array(), data.arrayOffset() + data.readerIndex(), length);
         } else {
            byte[] tmp = new byte[length];
            data.getBytes(data.readerIndex(), tmp);
            this.tmpPacket.setData(tmp);
         }

         try {
            this.socket.send(this.tmpPacket);
            in.remove();
         } catch (IOException var7) {
            in.remove(var7);
         }
      }
   }

   protected Object filterOutboundMessage(Object msg) {
      if(!(msg instanceof DatagramPacket) && !(msg instanceof ByteBuf)) {
         if(msg instanceof AddressedEnvelope) {
            AddressedEnvelope<Object, SocketAddress> e = (AddressedEnvelope)msg;
            if(e.content() instanceof ByteBuf) {
               return msg;
            }
         }

         throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg) + EXPECTED_TYPES);
      } else {
         return msg;
      }
   }

   public ChannelFuture joinGroup(InetAddress multicastAddress) {
      return this.joinGroup(multicastAddress, this.newPromise());
   }

   public ChannelFuture joinGroup(InetAddress multicastAddress, ChannelPromise promise) {
      this.ensureBound();

      try {
         this.socket.joinGroup(multicastAddress);
         promise.setSuccess();
      } catch (IOException var4) {
         promise.setFailure(var4);
      }

      return promise;
   }

   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.joinGroup(multicastAddress, networkInterface, this.newPromise());
   }

   public ChannelFuture joinGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      this.ensureBound();

      try {
         this.socket.joinGroup(multicastAddress, networkInterface);
         promise.setSuccess();
      } catch (IOException var5) {
         promise.setFailure(var5);
      }

      return promise;
   }

   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture joinGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   private void ensureBound() {
      if(!this.isActive()) {
         throw new IllegalStateException(DatagramChannel.class.getName() + " must be bound to join a group.");
      }
   }

   public ChannelFuture leaveGroup(InetAddress multicastAddress) {
      return this.leaveGroup(multicastAddress, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetAddress multicastAddress, ChannelPromise promise) {
      try {
         this.socket.leaveGroup(multicastAddress);
         promise.setSuccess();
      } catch (IOException var4) {
         promise.setFailure(var4);
      }

      return promise;
   }

   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface) {
      return this.leaveGroup(multicastAddress, networkInterface, this.newPromise());
   }

   public ChannelFuture leaveGroup(InetSocketAddress multicastAddress, NetworkInterface networkInterface, ChannelPromise promise) {
      try {
         this.socket.leaveGroup(multicastAddress, networkInterface);
         promise.setSuccess();
      } catch (IOException var5) {
         promise.setFailure(var5);
      }

      return promise;
   }

   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture leaveGroup(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress source, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture block(InetAddress multicastAddress, NetworkInterface networkInterface, InetAddress sourceToBlock, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }

   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock) {
      return this.newFailedFuture(new UnsupportedOperationException());
   }

   public ChannelFuture block(InetAddress multicastAddress, InetAddress sourceToBlock, ChannelPromise promise) {
      promise.setFailure(new UnsupportedOperationException());
      return promise;
   }
}
