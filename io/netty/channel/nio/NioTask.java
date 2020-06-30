package io.netty.channel.nio;

import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;

public interface NioTask {
   void channelReady(SelectableChannel var1, SelectionKey var2) throws Exception;

   void channelUnregistered(SelectableChannel var1, Throwable var2) throws Exception;
}
