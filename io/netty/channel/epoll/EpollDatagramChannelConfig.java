package io.netty.channel.epoll;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.Native;
import io.netty.channel.socket.DatagramChannelConfig;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Map;

public final class EpollDatagramChannelConfig extends DefaultChannelConfig implements DatagramChannelConfig {
   private static final RecvByteBufAllocator DEFAULT_RCVBUF_ALLOCATOR = new FixedRecvByteBufAllocator(2048);
   private final EpollDatagramChannel datagramChannel;
   private boolean activeOnOpen;

   EpollDatagramChannelConfig(EpollDatagramChannel channel) {
      super(channel);
      this.datagramChannel = channel;
      this.setRecvByteBufAllocator(DEFAULT_RCVBUF_ALLOCATOR);
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_BROADCAST, ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.SO_REUSEADDR, ChannelOption.IP_MULTICAST_LOOP_DISABLED, ChannelOption.IP_MULTICAST_ADDR, ChannelOption.IP_MULTICAST_IF, ChannelOption.IP_MULTICAST_TTL, ChannelOption.IP_TOS, ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION, EpollChannelOption.SO_REUSEPORT});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_BROADCAST?Boolean.valueOf(this.isBroadcast()):(option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_SNDBUF?Integer.valueOf(this.getSendBufferSize()):(option == ChannelOption.SO_REUSEADDR?Boolean.valueOf(this.isReuseAddress()):(option == ChannelOption.IP_MULTICAST_LOOP_DISABLED?Boolean.valueOf(this.isLoopbackModeDisabled()):(option == ChannelOption.IP_MULTICAST_ADDR?this.getInterface():(option == ChannelOption.IP_MULTICAST_IF?this.getNetworkInterface():(option == ChannelOption.IP_MULTICAST_TTL?Integer.valueOf(this.getTimeToLive()):(option == ChannelOption.IP_TOS?Integer.valueOf(this.getTrafficClass()):(option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION?Boolean.valueOf(this.activeOnOpen):(option == EpollChannelOption.SO_REUSEPORT?Boolean.valueOf(this.isReusePort()):super.getOption(option)))))))))));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_BROADCAST) {
         this.setBroadcast(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_REUSEADDR) {
         this.setReuseAddress(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.IP_MULTICAST_LOOP_DISABLED) {
         this.setLoopbackModeDisabled(((Boolean)value).booleanValue());
      } else if(option == ChannelOption.IP_MULTICAST_ADDR) {
         this.setInterface((InetAddress)value);
      } else if(option == ChannelOption.IP_MULTICAST_IF) {
         this.setNetworkInterface((NetworkInterface)value);
      } else if(option == ChannelOption.IP_MULTICAST_TTL) {
         this.setTimeToLive(((Integer)value).intValue());
      } else if(option == ChannelOption.IP_TOS) {
         this.setTrafficClass(((Integer)value).intValue());
      } else if(option == ChannelOption.DATAGRAM_CHANNEL_ACTIVE_ON_REGISTRATION) {
         this.setActiveOnOpen(((Boolean)value).booleanValue());
      } else {
         if(option != EpollChannelOption.SO_REUSEPORT) {
            return super.setOption(option, value);
         }

         this.setReusePort(((Boolean)value).booleanValue());
      }

      return true;
   }

   private void setActiveOnOpen(boolean activeOnOpen) {
      if(this.channel.isRegistered()) {
         throw new IllegalStateException("Can only changed before channel was registered");
      } else {
         this.activeOnOpen = activeOnOpen;
      }
   }

   public EpollDatagramChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }

   public EpollDatagramChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public EpollDatagramChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public EpollDatagramChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public EpollDatagramChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public EpollDatagramChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public EpollDatagramChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public EpollDatagramChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public EpollDatagramChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public EpollDatagramChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public int getSendBufferSize() {
      return Native.getSendBufferSize(this.datagramChannel.fd);
   }

   public EpollDatagramChannelConfig setSendBufferSize(int sendBufferSize) {
      Native.setSendBufferSize(this.datagramChannel.fd, sendBufferSize);
      return this;
   }

   public int getReceiveBufferSize() {
      return Native.getReceiveBufferSize(this.datagramChannel.fd);
   }

   public EpollDatagramChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      Native.setReceiveBufferSize(this.datagramChannel.fd, receiveBufferSize);
      return this;
   }

   public int getTrafficClass() {
      return Native.getTrafficClass(this.datagramChannel.fd);
   }

   public EpollDatagramChannelConfig setTrafficClass(int trafficClass) {
      Native.setTrafficClass(this.datagramChannel.fd, trafficClass);
      return this;
   }

   public boolean isReuseAddress() {
      return Native.isReuseAddress(this.datagramChannel.fd) == 1;
   }

   public EpollDatagramChannelConfig setReuseAddress(boolean reuseAddress) {
      Native.setReuseAddress(this.datagramChannel.fd, reuseAddress?1:0);
      return this;
   }

   public boolean isBroadcast() {
      return Native.isBroadcast(this.datagramChannel.fd) == 1;
   }

   public EpollDatagramChannelConfig setBroadcast(boolean broadcast) {
      Native.setBroadcast(this.datagramChannel.fd, broadcast?1:0);
      return this;
   }

   public boolean isLoopbackModeDisabled() {
      return false;
   }

   public DatagramChannelConfig setLoopbackModeDisabled(boolean loopbackModeDisabled) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public int getTimeToLive() {
      return -1;
   }

   public EpollDatagramChannelConfig setTimeToLive(int ttl) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public InetAddress getInterface() {
      return null;
   }

   public EpollDatagramChannelConfig setInterface(InetAddress interfaceAddress) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public NetworkInterface getNetworkInterface() {
      return null;
   }

   public EpollDatagramChannelConfig setNetworkInterface(NetworkInterface networkInterface) {
      throw new UnsupportedOperationException("Multicast not supported");
   }

   public boolean isReusePort() {
      return Native.isReusePort(this.datagramChannel.fd) == 1;
   }

   public EpollDatagramChannelConfig setReusePort(boolean reusePort) {
      Native.setReusePort(this.datagramChannel.fd, reusePort?1:0);
      return this;
   }

   protected void autoReadCleared() {
      this.datagramChannel.clearEpollIn();
   }
}
