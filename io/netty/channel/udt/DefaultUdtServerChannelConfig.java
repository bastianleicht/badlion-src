package io.netty.channel.udt;

import com.barchart.udt.nio.ChannelUDT;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.udt.DefaultUdtChannelConfig;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.UdtServerChannelConfig;
import java.io.IOException;
import java.util.Map;

public class DefaultUdtServerChannelConfig extends DefaultUdtChannelConfig implements UdtServerChannelConfig {
   private volatile int backlog = 64;

   public DefaultUdtServerChannelConfig(UdtChannel channel, ChannelUDT channelUDT, boolean apply) throws IOException {
      super(channel, channelUDT, apply);
      if(apply) {
         this.apply(channelUDT);
      }

   }

   protected void apply(ChannelUDT channelUDT) throws IOException {
   }

   public int getBacklog() {
      return this.backlog;
   }

   public Object getOption(ChannelOption option) {
      return option == ChannelOption.SO_BACKLOG?Integer.valueOf(this.getBacklog()):super.getOption(option);
   }

   public Map getOptions() {
      return this.getOptions(super.getOptions(), new ChannelOption[]{ChannelOption.SO_BACKLOG});
   }

   public UdtServerChannelConfig setBacklog(int backlog) {
      this.backlog = backlog;
      return this;
   }

   public boolean setOption(ChannelOption option, Object value) {
      this.validate(option, value);
      if(option == ChannelOption.SO_BACKLOG) {
         this.setBacklog(((Integer)value).intValue());
         return true;
      } else {
         return super.setOption(option, value);
      }
   }

   public UdtServerChannelConfig setProtocolReceiveBufferSize(int protocolReceiveBuferSize) {
      super.setProtocolReceiveBufferSize(protocolReceiveBuferSize);
      return this;
   }

   public UdtServerChannelConfig setProtocolSendBufferSize(int protocolSendBuferSize) {
      super.setProtocolSendBufferSize(protocolSendBuferSize);
      return this;
   }

   public UdtServerChannelConfig setReceiveBufferSize(int receiveBufferSize) {
      super.setReceiveBufferSize(receiveBufferSize);
      return this;
   }

   public UdtServerChannelConfig setReuseAddress(boolean reuseAddress) {
      super.setReuseAddress(reuseAddress);
      return this;
   }

   public UdtServerChannelConfig setSendBufferSize(int sendBufferSize) {
      super.setSendBufferSize(sendBufferSize);
      return this;
   }

   public UdtServerChannelConfig setSoLinger(int soLinger) {
      super.setSoLinger(soLinger);
      return this;
   }

   public UdtServerChannelConfig setSystemReceiveBufferSize(int systemSendBuferSize) {
      super.setSystemReceiveBufferSize(systemSendBuferSize);
      return this;
   }

   public UdtServerChannelConfig setSystemSendBufferSize(int systemReceiveBufferSize) {
      super.setSystemSendBufferSize(systemReceiveBufferSize);
      return this;
   }

   public UdtServerChannelConfig setConnectTimeoutMillis(int connectTimeoutMillis) {
      super.setConnectTimeoutMillis(connectTimeoutMillis);
      return this;
   }

   public UdtServerChannelConfig setMaxMessagesPerRead(int maxMessagesPerRead) {
      super.setMaxMessagesPerRead(maxMessagesPerRead);
      return this;
   }

   public UdtServerChannelConfig setWriteSpinCount(int writeSpinCount) {
      super.setWriteSpinCount(writeSpinCount);
      return this;
   }

   public UdtServerChannelConfig setAllocator(ByteBufAllocator allocator) {
      super.setAllocator(allocator);
      return this;
   }

   public UdtServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator allocator) {
      super.setRecvByteBufAllocator(allocator);
      return this;
   }

   public UdtServerChannelConfig setAutoRead(boolean autoRead) {
      super.setAutoRead(autoRead);
      return this;
   }

   public UdtServerChannelConfig setAutoClose(boolean autoClose) {
      super.setAutoClose(autoClose);
      return this;
   }

   public UdtServerChannelConfig setWriteBufferLowWaterMark(int writeBufferLowWaterMark) {
      super.setWriteBufferLowWaterMark(writeBufferLowWaterMark);
      return this;
   }

   public UdtServerChannelConfig setWriteBufferHighWaterMark(int writeBufferHighWaterMark) {
      super.setWriteBufferHighWaterMark(writeBufferHighWaterMark);
      return this;
   }

   public UdtServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator estimator) {
      super.setMessageSizeEstimator(estimator);
      return this;
   }
}
