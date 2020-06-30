package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.util.NetUtil;
import java.util.Map;

public final class EpollServerSocketChannelConfig extends DefaultChannelConfig implements ServerSocketChannelConfig {
   private final EpollServerSocketChannel channel;
   private volatile int backlog = NetUtil.SOMAXCONN;

   EpollServerSocketChannelConfig(EpollServerSocketChannel channel) {
      super(channel);
      this.channel = channel;
      this.setReuseAddress(true);
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_REUSEADDR, ChannelOption.SO_BACKLOG, EpollChannelOption.SO_REUSEPORT});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_REUSEADDR?Boolean.valueOf(this.isReuseAddress()):(option == ChannelOption.SO_BACKLOG?Integer.valueOf(this.getBacklog()):(option == EpollChannelOption.SO_REUSEPORT?Boolean.valueOf(this.isReusePort()):super.getOption(option))));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.SO_BACKLOG) {
         this.setBacklog(((Integer)value).intValue());
      } else {
         if(option != EpollChannelOption.SO_REUSEPORT) {
            return super.setOption(option, value);
         }

         this.setReusePort(((Boolean)value).booleanValue());
      }

      return true;
   }

   public boolean isReuseAddress() {
      return Native.isReuseAddress(this.channel.fd) == 1;
   }

   public EpollServerSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      Native.setReuseAddress(this.channel.fd, reuseAddress?1:0);
      return this;
   }

   public int getReceiveBufferSize() {
      return Native.getReceiveBufferSize(this.channel.fd);
   }

   public EpollServerSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      Native.setReceiveBufferSize(this.channel.fd, receiveBufferSize);
      return this;
   }

   public EpollServerSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      return this;
   }

   public int getBacklog() {
      return this.backlog;
   }

   public EpollServerSocketChannelConfig setBacklog(int backlog) {
      if(backlog < 0) {
         throw new IllegalArgumentException("backlog: " + backlog);
      } else {
         this.backlog = backlog;
         return this;
      }
   }

   public EpollServerSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public EpollServerSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollServerSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public EpollServerSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public EpollServerSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public EpollServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   public boolean isReusePort() {
      return Native.isReusePort(this.channel.fd) == 1;
   }

   public EpollServerSocketChannelConfig setReusePort(boolean reusePort) {
      Native.setReusePort(this.channel.fd, reusePort?1:0);
      return this;
   }

   protected void autoReadCleared() {
      this.channel.clearEpollIn();
   }
}
