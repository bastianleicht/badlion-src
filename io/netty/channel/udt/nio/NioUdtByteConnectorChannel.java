package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.FileRegion;
import io.netty.channel.nio.AbstractNioByteChannel;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtChannelConfig;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

public class NioUdtByteConnectorChannel extends AbstractNioByteChannel implements UdtChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(NioUdtByteConnectorChannel.class);
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private final UdtChannelConfig config;

   public NioUdtByteConnectorChannel() {
      this(TypeUDT.STREAM);
   }

   public NioUdtByteConnectorChannel(Channel parent, SocketChannelUDT channelUDT) {
      super(parent, channelUDT);

      try {
         channelUDT.configureBlocking(false);
         switch(channelUDT.socketUDT().status()) {
         case INIT:
         case OPENED:
            this.config = new DefaultUdtChannelConfig(this, channelUDT, true);
            break;
         default:
            this.config = new DefaultUdtChannelConfig(this, channelUDT, false);
         }

      } catch (Exception var6) {
         try {
            channelUDT.close();
         } catch (Exception var5) {
            if(logger.isWarnEnabled()) {
               logger.warn("Failed to close channel.", (Throwable)var5);
            }
         }

         throw new ChannelException("Failed to configure channel.", var6);
      }
   }

   public NioUdtByteConnectorChannel(SocketChannelUDT channelUDT) {
      this((Channel)null, channelUDT);
   }

   public NioUdtByteConnectorChannel(TypeUDT type) {
      this(NioUdtProvider.newConnectorChannelUDT(type));
   }

   public UdtChannelConfig config() {
      return this.config;
   }

   protected void doBind(SocketAddress localAddress) throws Exception {
      this.javaChannel().bind(localAddress);
   }

   protected void doClose() throws Exception {
      this.javaChannel().close();
   }

   protected boolean doConnect(SocketAddress remoteAddress, SocketAddress localAddress) throws Exception {
      this.doBind((SocketAddress)(localAddress != null?localAddress:new InetSocketAddress(0)));
      boolean success = false;

      boolean var5;
      try {
         boolean connected = this.javaChannel().connect(remoteAddress);
         if(!connected) {
            this.selectionKey().interestOps(this.selectionKey().interestOps() | 8);
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

   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   protected void doFinishConnect() throws Exception {
      if(this.javaChannel().finishConnect()) {
         this.selectionKey().interestOps(this.selectionKey().interestOps() & -9);
      } else {
         throw new Error("Provider error: failed to finish connect. Provider library should be upgraded.");
      }
   }

   protected int doReadBytes(ByteBuf byteBuf) throws Exception {
      return byteBuf.writeBytes((ScatteringByteChannel)this.javaChannel(), byteBuf.writableBytes());
   }

   protected int doWriteBytes(ByteBuf byteBuf) throws Exception {
      int expectedWrittenBytes = byteBuf.readableBytes();
      return byteBuf.readBytes((GatheringByteChannel)this.javaChannel(), expectedWrittenBytes);
   }

   protected long doWriteFileRegion(FileRegion region) throws Exception {
      throw new UnsupportedOperationException();
   }

   public boolean isActive() {
      SocketChannelUDT channelUDT = this.javaChannel();
      return channelUDT.isOpen() && channelUDT.isConnectFinished();
   }

   protected SocketChannelUDT javaChannel() {
      return (SocketChannelUDT)super.javaChannel();
   }

   protected SocketAddress localAddress0() {
      return this.javaChannel().socket().getLocalSocketAddress();
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }

   protected SocketAddress remoteAddress0() {
      return this.javaChannel().socket().getRemoteSocketAddress();
   }

   public InetSocketAddress localAddress() {
      return (InetSocketAddress)super.localAddress();
   }

   public InetSocketAddress remoteAddress() {
      return (InetSocketAddress)super.remoteAddress();
   }
}
