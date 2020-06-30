package io.netty.util;

import io.netty.util.Timeout;

public interface TimerTask {
   void run(Timeout var1) throws Exception;
}
