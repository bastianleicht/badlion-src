package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Map;

public class DefaultServerSocketChannelConfig extends DefaultChannelConfig implements ServerSocketChannelConfig {
   protected final ServerSocket javaSocket;
   private volatile int backlog = NetUtil.SOMAXCONN;

   public DefaultServerSocketChannelConfig(ServerSocketChannel channel, ServerSocket javaSocket) {
      super(channel);
      if(javaSocket == null) {
         throw new NullPointerException("javaSocket");
      } else {
         this.javaSocket = javaSocket;
      }
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_REUSEADDR?Boolean.valueOf(this.isReuseAddress()):(option == ChannelOption.SO_BACKLOG?Integer.valueOf(this.getBacklog()):super.getOption(option)));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress(((Boolean)value).booleanValue());
      } else {
         if(option != ChannelOption.SO_BACKLOG) {
            return super.setOption(option, value);
         }

         this.setBacklog(((Integer)value).intValue());
      }

      return true;
   }

   public boolean isReuseAddress() {
      try {
         return this.javaSocket.getReuseAddress();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public ServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      try {
         this.javaSocket.setReuseAddress(reuseAddress);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getReceiveBufferSize() {
      try {
         return this.javaSocket.getReceiveBufferSize();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public ServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         this.javaSocket.setReceiveBufferSize(receiveBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public ServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      this.javaSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
      return this;
   }

   public int getBacklog() {
      return this.backlog;
   }

   public ServerSocketChannelConfig setBacklog(int backlog) {
      if(backlog < 0) {
         throw new IllegalArgumentException("backlog: " + backlog);
      } else {
         this.backlog = backlog;
         return this;
      }
   }

   public ServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public ServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public ServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public ServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public ServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public ServerSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public ServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public ServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public ServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
