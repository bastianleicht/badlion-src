package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.DefaultServerSocketChannelConfig;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannel;
import io.netty.channel.socket.oio.OioServerSocketChannelConfig;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class DefaultOioServerSocketChannelConfig extends DefaultServerSocketChannelConfig implements OioServerSocketChannelConfig {
   /** @deprecated */
   @Deprecated
   public DefaultOioServerSocketChannelConfig(ServerSocketChannel channel, ServerSocket javaSocket) {
      super(channel, javaSocket);
   }

   DefaultOioServerSocketChannelConfig(OioServerSocketChannel channel, ServerSocket javaSocket) {
      super(channel, javaSocket);
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_TIMEOUT});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_TIMEOUT?Integer.valueOf(this.getSoTimeout()):super.getOption(option);
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_TIMEOUT) {
         this.setSoTimeout(((Integer)value).intValue());
         return true;
      } else {
         return super.setOption(option, value);
      }
   }

   public OioServerSocketChannelConfig setSoTimeout(int timeout) {
      try {
         this.javaSocket.setSoTimeout(timeout);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getSoTimeout() {
      try {
         return this.javaSocket.getSoTimeout();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public OioServerSocketChannelConfig setBacklog(int backlog) {
      super.setBacklog(backlog);
      return this;
   }

   public OioServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      super.setReuseAddress(reuseAddress);
      return this;
   }

   public OioServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      super.setReceiveBufferSize(receiveBufferSize);
      return this;
   }

   public OioServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      super.setPerformancePreferences(connectionTime, latency, bandwidth);
      return this;
   }

   public OioServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public OioServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public OioServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public OioServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public OioServerSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   protected void autoReadCleared() {
      if(this.channel instanceof OioServerSocketChannel) {
         ((OioServerSocketChannel)this.channel).setReadPending(false);
      }

   }

   public OioServerSocketChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public OioServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public OioServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
