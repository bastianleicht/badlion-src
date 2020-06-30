package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.TimeLimiter;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Beta
public final class FakeTimeLimiter implements TimeLimiter {
   public Object newProxy(Object target, Class interfaceType, long timeoutDuration, TimeUnit timeoutUnit) {
      Preconditions.checkNotNull(target);
      Preconditions.checkNotNull(interfaceType);
      Preconditions.checkNotNull(timeoutUnit);
      return target;
   }

   public Object callWithTimeout(Callable callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible) throws Exception {
      Preconditions.checkNotNull(timeoutUnit);
      return callable.call();
   }
}
