package io.netty.util;

import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface Timer {
   Timeout newTimeout(TimerTask var1, long var2, TimeUnit var4);

   Set stop();
}
