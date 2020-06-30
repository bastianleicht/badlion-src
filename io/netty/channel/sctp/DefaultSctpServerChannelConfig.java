package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions;
import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.sctp.SctpChannelOption;
import io.netty.channel.sctp.SctpServerChannel;
import io.netty.channel.sctp.SctpServerChannelConfig;
import io.netty.util.NetUtil;
import java.io.IOException;
import java.util.Map;

public class DefaultSctpServerChannelConfig extends DefaultChannelConfig implements SctpServerChannelConfig {
   private final com.sun.nio.sctp.SctpServerChannel javaChannel;
   private volatile int backlog = NetUtil.SOMAXCONN;

   public DefaultSctpServerChannelConfig(SctpServerChannel channel, com.sun.nio.sctp.SctpServerChannel javaChannel) {
      super(channel);
      if(javaChannel == null) {
         throw new NullPointerException("javaChannel");
      } else {
         this.javaChannel = javaChannel;
      }
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_RCVBUF, ChannelOption.SO_SNDBUF, SctpChannelOption.SCTP_INIT_MAXSTREAMS});
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_RCVBUF?Integer.valueOf(this.getReceiveBufferSize()):(option == ChannelOption.SO_SNDBUF?Integer.valueOf(this.getSendBufferSize()):super.getOption(option));
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_RCVBUF) {
         this.setReceiveBufferSize(((Integer)value).intValue());
      } else if(option == ChannelOption.SO_SNDBUF) {
         this.setSendBufferSize(((Integer)value).intValue());
      } else {
         if(option != SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS) {
            return super.setOption(option, value);
         }

         this.setInitMaxStreams((InitMaxStreams)value);
      }

      return true;
   }

   public int getSendBufferSize() {
      try {
         return ((Integer)this.javaChannel.getOption(SctpStandardSocketOptions.SO_SNDBUF)).intValue();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   public SctpServerChannelConfig setSendBufferSize(int sendBufferSize) {
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

   public SctpServerChannelConfig setReceiveBufferSize(int receiveBufferSize) {
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

   public SctpServerChannelConfig setInitMaxStreams(InitMaxStreams initMaxStreams) {
      try {
         this.javaChannel.setOption(SctpStandardSocketOptions.SCTP_INIT_MAXSTREAMS, initMaxStreams);
         return this;
      } catch (IOException var3) {
         throw new ChannelException(var3);
      }
   }

   public int getBacklog() {
      return this.backlog;
   }

   public SctpServerChannelConfig setBacklog(int backlog) {
      if(backlog < 0) {
         throw new IllegalArgumentException("backlog: " + backlog);
      } else {
         this.backlog = backlog;
         return this;
      }
   }

   public SctpServerChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public SctpServerChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public SctpServerChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public SctpServerChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public SctpServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public SctpServerChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public SctpServerChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public SctpServerChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public SctpServerChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public SctpServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
