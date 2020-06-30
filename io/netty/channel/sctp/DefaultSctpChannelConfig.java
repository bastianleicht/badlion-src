package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.sctp.SctpChannel;
import io.netty.channel.sctp.SctpChannelConfig;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.util.internal.PlatformDependent;
import java.io.IOException;
import java.util.Map;

public class DefaultSctpChannelConfig extends DefaultChannelConfig implements SctpChannelConfig {
   private final com.sun.nio.sctp.SctpChannel javaChannel;

   public DefaultSctpChannelConfig(SctpChannel channel, com.sun.nio.sctp.SctpChannel javaChannel) {
      super(channel);
      if(javaChannel == null) {
         throw new NullPointerException("javaChannel");
      } else {
         this.javaChannel = javaChannel;
         if(PlatformDependent.canEnableTcpNoDelayByDefault()) {
            try {
               this.setSctpNoDelay(true);
            } catch (Exception var4) {
               ;
            }
         }

      }
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{SctpChannelOption.SO_RCVBUF, SctpChannelOption.SO_SNDBUF, SctpChannelOption.SCTP_NODELAY, SctpChannelOption.SCTP_INIT_MAXSTREAMS});
   }

   public Object getOption(ChannelOption option) {
      return option == SctpChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == SctpChannelOption.SO_SNDBUF?Integer.valueOf(this.getSendBufferSize()):(option == SctpChannelOption.SCTP_NODELAY?Boolean.valueOf(this.isSctpNoDelay()):super.getOption(option)));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == SctpChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == SctpChannelOption.SO_SNDBUF) {
         this.setSendBufferSize(((Integer)value).intValue());
      } else if(option == SctpChannelOption.SCTP_NODELAY) {
         this.setSctpNoDelay(((Boolean)value).booleanValue());
      } else {
         if(option != SctpChannelOption.SCTP_INIT_MAXSTREAMS) {
            return super.setOption(option, value);
         }

         this.setInitMaxStreams((InitMaxStreams)value);
      }

      return true;
   }

   public boolean isSctpNoDelay() {
      try {
         return ((Boolean)this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_NODELAY)).booleanValue();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public SctpChannelConfig setSctpNoDelay(boolean sctpNoDelay) {
      try {
         this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_NODELAY, Boolean.valueOf(sctpNoDelay));
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getSendBufferSize() {
      try {
         return ((Integer)this.javaChannel.getOption(SctpStandardSocketOptions.SO_SNDBUF)).intValue();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public SctpChannelConfig setSendBufferSize(int sendBufferSize) {
      try {
         this.javaChannel.setOption(SctpStandardSocketOptions.SO_SNDBUF, Integer.valueOf(sendBufferSize));
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getReceiveBufferSize() {
      try {
         return ((Integer)this.javaChannel.getOption(SctpStandardSocketOptions.SO_RCVBUF)).intValue();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public SctpChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      try {
         this.javaChannel.setOption(SctpStandardSocketOptions.SO_RCVBUF, Integer.valueOf(receiveBufferSize));
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public InitMaxStreams getInitMaxStreams() {
      try {
         return (InitMaxStreams)this.javaChannel.getOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS);
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public SctpChannelConfig setInitMaxStreams(InitMaxStreams initMaxStreams) {
      try {
         this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, initMaxStreams);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public SctpChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public SctpChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public SctpChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public SctpChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public SctpChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public SctpChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public SctpChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public SctpChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public SctpChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public SctpChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
