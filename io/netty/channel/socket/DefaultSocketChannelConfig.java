package io.netty.channel.socket;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.util.internal.PlatformDependent;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;

public class DefaultSocketChannelConfig extends DefaultChannelConfig implements SocketChannelConfig {
   protected final Socket javaSocket;
   private volatile boolean allowHalfClosure;

   public DefaultSocketChannelConfig(SocketChannel channel, Socket javaSocket) {
      super(channel);
      if(javaSocket == null) {
         throw new NullPointerException("javaSocket");
      } else {
         this.javaSocket = javaSocket;
         if(PlatformDependent.canEnableTcpNoDelayByDefault()) {
            try {
               this.setTcpNoDelay(true);
            } catch (Exception var4) {
               ;
            }
         }

      }
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, ChannelOption.TCP_NODELAY, ChannelOption.SO_KEEPALIVE, ChannelOption.SO_REUSEADDR, ChannelOption.SO_LINGER, ChannelOption.IP_TOS, ChannelOption.ALLOW_HALF_CLOSURE});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_SNDBUF?Integer.valueOf(this.getSendBufferSize()):(option == ChannelOption.TCP_NODELAY?Boolean.valueOf(this.isTcpNoDelay()):(option == ChannelOption.SO_KEEPALIVE?Boolean.valueOf(this.isKeepAlive()):(option == ChannelOption.SO_REUSEADDR?Boolean.valueOf(this.isReuseAddress()):(option == ChannelOption.SO_LINGER?Integer.valueOf(this.getSoLinger()):(option == ChannelOption.IP_TOS?Integer.valueOf(this.getTrafficClass()):(option == ChannelOption.ALLOW_HALF_CLOSURE?Boolean.valueOf(this.isAllowHalfClosure()):super.getOption(option))))))));
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
      } else {
         if(option != ChannelOption.ALLOW_HALF_CLOSURE) {
            return super.setOption(option, value);
         }

         this.setAllowHalfClosure(((Boolean)value).booleanValue());
      }

      return true;
   }

   public int getReceiveBufferSize() {
      try {
         return this.javaSocket.getReceiveBufferSize();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getSendBufferSize() {
      try {
         return this.javaSocket.getSendBufferSize();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getSoLinger() {
      try {
         return this.javaSocket.getSoLinger();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public int getTrafficClass() {
      try {
         return this.javaSocket.getTrafficClass();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isKeepAlive() {
      try {
         return this.javaSocket.getKeepAlive();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isReuseAddress() {
      try {
         return this.javaSocket.getReuseAddress();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public boolean isTcpNoDelay() {
      try {
         return this.javaSocket.getTcpNoDelay();
      } catch (SocketException var2) {
         throw new ChannelException(var2);
      }
   }

   public SocketChannelConfig setKeepAlive(boolean keepAlive) {
      try {
         this.javaSocket.setKeepAlive(keepAlive);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setPerformancePreferences(int connectionTime, int latency, int bandwidth) {
      this.javaSocket.setPerformancePreferences(connectionTime, latency, bandwidth);
      return this;
   }

   public SocketChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         this.javaSocket.setReceiveBufferSize(receiveBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setReuseAddress(boolean reuseAddress) {
      try {
         this.javaSocket.setReuseAddress(reuseAddress);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         this.javaSocket.setSendBufferSize(sendBufferSize);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setSoLinger(int soLinger) {
      try {
         if(soLinger < 0) {
            this.javaSocket.setSoLinger(false, 0);
         } else {
            this.javaSocket.setSoLinger(true, soLinger);
         }

         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setTcpNoDelay(boolean tcpNoDelay) {
      try {
         this.javaSocket.setTcpNoDelay(tcpNoDelay);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public SocketChannelConfig setTrafficClass(int trafficClass) {
      try {
         this.javaSocket.setTrafficClass(trafficClass);
         return this;
      } catch (SocketException var3) {
         throw new ChannelException(var3);
      }
   }

   public boolean isAllowHalfClosure() {
      return this.allowHalfClosure;
   }

   public SocketChannelConfig setAllowHalfClosure(boolean allowHalfClosure) {
      this.allowHalfClosure = allowHalfClosure;
      return this;
   }

   public SocketChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public SocketChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public SocketChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public SocketChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public SocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public SocketChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public SocketChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public SocketChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public SocketChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public SocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
