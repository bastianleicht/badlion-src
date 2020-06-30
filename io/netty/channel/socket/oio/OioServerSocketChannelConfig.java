package io.netty.channel.socket.oio;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.socket.ServerSocketChannelConfig;

public interface OioServerSocketChannelConfig extends ServerSocketChannelConfig {
   OioServerSocketChannelConfig setSoTimeout(int var1);

   int getSoTimeout();

   OioServerSocketChannelConfig setBacklog(int var1);

   OioServerSocketChannelConfig setReuseAddress(boolean var1);

   OioServerSocketChannelConfig setReceiveBufferSize(int var1);

   OioServerSocketChannelConfig setPerformancePreferences(int var1, int var2, int var3);

   OioServerSocketChannelConfig setConnectTimeoutMillis(int var1);

   OioServerSocketChannelConfig setMaxMessagesPerRead(int var1);

   OioServerSocketChannelConfig setWriteSpinCount(int var1);

   OioServerSocketChannelConfig setAllocator(ByteBufAllocator var1);

   OioServerSocketChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   OioServerSocketChannelConfig setAutoRead(boolean var1);

   OioServerSocketChannelConfig setAutoClose(boolean var1);

   OioServerSocketChannelConfig setWriteBufferHighWaterMark(int var1);

   OioServerSocketChannelConfig setWriteBufferLowWaterMark(int var1);

   OioServerSocketChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
