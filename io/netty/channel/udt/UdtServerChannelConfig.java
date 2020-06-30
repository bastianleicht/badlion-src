package io.netty.channel.udt;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.udt.UdtChannelConfig;

public interface UdtServerChannelConfig extends UdtChannelConfig {
   int getBacklog();

   UdtServerChannelConfig setBacklog(int var1);

   UdtServerChannelConfig setConnectTimeoutMillis(int var1);

   UdtServerChannelConfig setMaxMessagesPerRead(int var1);

   UdtServerChannelConfig setWriteSpinCount(int var1);

   UdtServerChannelConfig setAllocator(ByteBufAllocator var1);

   UdtServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   UdtServerChannelConfig setAutoRead(boolean var1);

   UdtServerChannelConfig setAutoClose(boolean var1);

   UdtServerChannelConfig setProtocolReceiveBufferSize(int var1);

   UdtServerChannelConfig setProtocolSendBufferSize(int var1);

   UdtServerChannelConfig setReceiveBufferSize(int var1);

   UdtServerChannelConfig setReuseAddress(boolean var1);

   UdtServerChannelConfig setSendBufferSize(int var1);

   UdtServerChannelConfig setSoLinger(int var1);

   UdtServerChannelConfig setSystemReceiveBufferSize(int var1);

   UdtServerChannelConfig setSystemSendBufferSize(int var1);

   UdtServerChannelConfig setWriteBufferHighWaterMark(int var1);

   UdtServerChannelConfig setWriteBufferLowWaterMark(int var1);

   UdtServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
