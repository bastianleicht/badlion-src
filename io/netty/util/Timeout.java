package io.netty.util;

import io.netty.util.Timer;
import io.netty.util.TimerTask;

public interface Timeout {
   Timer timer();

   TimerTask task();

   boolean isExpired();

   boolean isCancelled();

   boolean cancel();
}
