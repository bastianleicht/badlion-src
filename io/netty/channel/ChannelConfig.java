package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.MessageSizeEstimator;
import io.netty.channel.RecvByteBufAllocator;
import java.util.Map;

public interface ChannelConfig {
   Map getOptions();

   boolean setOptions(Map var1);

   Object getOption(ChannelOption var1);

   boolean setOption(ChannelOption var1, Object var2);

   int getConnectTimeoutMillis();

   ChannelConfig setConnectTimeoutMillis(int var1);

   int getMaxMessagesPerRead();

   ChannelConfig setMaxMessagesPerRead(int var1);

   int getWriteSpinCount();

   ChannelConfig setWriteSpinCount(int var1);

   ByteBufAllocator getAllocator();

   ChannelConfig setAllocator(ByteBufAllocator var1);

   RecvByteBufAllocator getRecvByteBufAllocator();

   ChannelConfig setRecvByteBufAllocator(RecvByteBufAllocator var1);

   boolean isAutoRead();

   ChannelConfig setAutoRead(boolean var1);

   /** @deprecated */
   @Deprecated
   boolean isAutoClose();

   /** @deprecated */
   @Deprecated
   ChannelConfig setAutoClose(boolean var1);

   int getWriteBufferHighWaterMark();

   ChannelConfig setWriteBufferHighWaterMark(int var1);

   int getWriteBufferLowWaterMark();

   ChannelConfig setWriteBufferLowWaterMark(int var1);

   MessageSizeEstimator getMessageSizeEstimator();

   ChannelConfig setMessageSizeEstimator(MessageSizeEstimator var1);
}
