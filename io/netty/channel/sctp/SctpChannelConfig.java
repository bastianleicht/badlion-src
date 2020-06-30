package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SctpChannelConfig extends ChannelConfig {
   boolean isSctpNoDelay();

   SctpChannelConfig setSctpNoDelay(boolean var1);

   int getSendBufferSize();

   SctpChannelConfig setSendBufferSize(int var1);

   int getReceiveBufferSize();

   SctpChannelConfig setReceiveBufferSize(int var1);

   InitMaxStreams getInitMaxStreams();

   SctpChannelConfig setInitMaxStreams(InitMaxStreams var1);

   SctpChannelConfig setConnectTimeoutMillis(int var1);

   SctpChannelConfig setMaxMessagesPerRead(int var1);

   SctpChannelConfig setWriteSpinCount(int var1);

   SctpChannelConfig setAllocator(ByteBufAllocator var1);

   SctpChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   SctpChannelConfig setAutoRead(boolean var1);

   SctpChannelConfig setAutoClose(boolean var1);

   SctpChannelConfig setWriteBufferHighWaterMark(int var1);

   SctpChannelConfig setWriteBufferLowWaterMark(int var1);

   SctpChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
