package io.netty.channel;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;

public interface EventLoop extends EventExecutor, EventLoopGroup {
   EventLoopGroup parent();
}
