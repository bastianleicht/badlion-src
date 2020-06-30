package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.util.Map;

public final class EpollSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig {
   private final EpollSocketChannel channel;
   private volatile boolean allowHalfClosure;

   EpollSocketChannelConfig(EpollSocketChannel channel) {
      super(channel);
      this.channel = channel;
      if(PlatformDependent.canEnableTcpNoDelayByDefault()) {
         this.setTcpNoDelay(true);
      }

   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE, EpollChannelOption.TCP_CORK, EpollChannelOption.TCP_KEEPCNT, EpollChannelOption.TCP_KEEPIDLE, EpollChannelOption.TCP_KEEPINTVL});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_SNDBUF?Integer.valueOf(this.getSendBufferSize()):(option == ChannelOption.TCP_NODELAY?Boolean.valueOf(this.isTcpNoDelay()):(option == ChannelOption.SO_KEEPALIVE?Boolean.valueOf(this.isKeepAlive()):(option == ChannelOption.SO_REUSEADDR?Boolean.valueOf(this.isReuseAddress()):(option == ChannelOption.SO_LINGER?Integer.valueOf(this.getSoLinger()):(option == ChannelOption.IP_TOS?Integer.valueOf(this.getTrafficClass()):(option == ChannelOption.ALLOW_HALF_CLOSURE?Boolean.valueOf(this.isAllowHalfClosure()):(option == EpollChannelOption.TCP_CORK?Boolean.valueOf(this.isTcpCork()):(option == EpollChannelOption.TCP_KEEPIDLE?Integer.valueOf(this.getTcpKeepIdle()):(option == EpollChannelOption.TCP_KEEPINTVL?Integer.valueOf(this.getTcpKeepIntvl()):(option == EpollChannelOption.TCP_KEEPCNT?Integer.valueOf(this.getTcpKeepCnt()):super.getOption(option))))))))))));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.TCP_NODELAY) {
         this.setTcpNoDelay(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.SO_KEEPALIVE) {
         this.setKeepAlive(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.SO_LINGER) {
         this.setSoLinger(((Integer)value).intValue());
      } else if(option == ChannelOption.IP_TOS) {
         this.setTrafficClass(((Integer)value).intValue());
      } else if(option == ChannelOption.ALLOW_HALF_CLOSURE) {
         this.setAllowHalfClosure(((Boolean)value).booleanValue());
      } else if(option == EpollChannelOption.TCP_CORK) {
         this.setTcpCork(((Boolean)value).booleanValue());
      } else if(option == EpollChannelOption.TCP_KEEPIDLE) {
         this.setTcpKeepIdle(((Integer)value).intValue());
      } else if(option == EpollChannelOption.TCP_KEEPCNT) {
         this.setTcpKeepCntl(((Integer)value).intValue());
      } else {
         if(option != EpollChannelOption.TCP_KEEPINTVL) {
            return super.setOption(option, value);
         }

         this.setTcpKeepIntvl(((Integer)value).intValue());
      }

      return true;
   }

   public int getReceiveBufferSize() {
      return Native.getReceiveBufferSize(this.channel.fd);
   }

   public int getSendBufferSize() {
      return Native.getSendBufferSize(this.channel.fd);
   }

   public int getSoLinger() {
      return Native.getSoLinger(this.channel.fd);
   }

   public int getTrafficClass() {
      return Native.getTrafficClass(this.channel.fd);
   }

   public boolean isKeepAlive() {
      return Native.isKeepAlive(this.channel.fd) == 1;
   }

   public boolean isReuseAddress() {
      return Native.isReuseAddress(this.channel.fd) == 1;
   }

   public boolean isTcpNoDelay() {
      return Native.isTcpNoDelay(this.channel.fd) == 1;
   }

   public boolean isTcpCork() {
      return Native.isTcpCork(this.channel.fd) == 1;
   }

   public int getTcpKeepIdle() {
      return Native.getTcpKeepIdle(this.channel.fd);
   }

   public int getTcpKeepIntvl() {
      return Native.getTcpKeepIntvl(this.channel.fd);
   }

   public int getTcpKeepCnt() {
      return Native.getTcpKeepCnt(this.channel.fd);
   }

   public EpollSocketChannelConfig setKeepAlive(boolean keepAlive) {
      Native.setKeepAlive(this.channel.fd, keepAlive?1:0);
      return this;
   }

   public EpollSocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      return this;
   }

   public EpollSocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      Native.setReceiveBufferSize(this.channel.fd, receiveBufferSize);
      return this;
   }

   public EpollSocketChannelConfig setReuseAddress(boolean reuseAddress) {
      Native.setReuseAddress(this.channel.fd, reuseAddress?1:0);
      return this;
   }

   public EpollSocketChannelConfig setSendBufferSize(int sendBufferSize) {
      Native.setSendBufferSize(this.channel.fd, sendBufferSize);
      return this;
   }

   public EpollSocketChannelConfig setSoLinger(int soLinger) {
      Native.setSoLinger(this.channel.fd, soLinger);
      return this;
   }

   public EpollSocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
      Native.setTcpNoDelay(this.channel.fd, tcpNoDelay?1:0);
      return this;
   }

   public EpollSocketChannelConfig setTcpCork(boolean tcpCork) {
      Native.setTcpCork(this.channel.fd, tcpCork?1:0);
      return this;
   }

   public EpollSocketChannelConfig setTrafficClass(int trafficClass) {
      Native.setTrafficClass(this.channel.fd, trafficClass);
      return this;
   }

   public EpollSocketChannelConfig setTcpKeepIdle(int seconds) {
      Native.setTcpKeepIdle(this.channel.fd, seconds);
      return this;
   }

   public EpollSocketChannelConfig setTcpKeepIntvl(int seconds) {
      Native.setTcpKeepIntvl(this.channel.fd, seconds);
      return this;
   }

   public EpollSocketChannelConfig setTcpKeepCntl(int probes) {
      Native.setTcpKeepCnt(this.channel.fd, probes);
      return this;
   }

   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   public EpollSocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
      this.allowHalfClosure = allowHalfClosure;
      return this;
   }

   public EpollSocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public EpollSocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public EpollSocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollSocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public EpollSocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public EpollSocketChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public EpollSocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public EpollSocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public EpollSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   protected void autoReadCleared() {
      this.channel.clearEpollIn();
   }
}
