package io.netty.channel.sctp;

import com.sun.nio.sctp.SctpStandardSocketOptions.InitMaxStreams;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelConfig;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;

public interface SctpServerChannelConfig extends ChannelConfig {
   int getBacklog();

   SctpServerChannelConfig setBacklog(int var1);

   int getSendBufferSize();

   SctpServerChannelConfig setSendBufferSize(int var1);

   int getReceiveBufferSize();

   SctpServerChannelConfig setReceiveBufferSize(int var1);

   InitMaxStreams getInitMaxStreams();

   SctpServerChannelConfig setInitMaxStreams(InitMaxStreams var1);

   SctpServerChannelConfig setMaxMessagesPerRead(int var1);

   SctpServerChannelConfig setWriteSpinCount(int var1);

   SctpServerChannelConfig setConnectTimeoutMillis(int var1);

   SctpServerChannelConfig setAllocator(ByteBufAllocator var1);

   SctpServerChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   SctpServerChannelConfig setAutoRead(boolean var1);

   SctpServerChannelConfig setAutoClose(boolean var1);

   SctpServerChannelConfig setWriteBufferHighWaterMark(int var1);

   SctpServerChannelConfig setWriteBufferLowWaterMark(int var1);

   SctpServerChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
