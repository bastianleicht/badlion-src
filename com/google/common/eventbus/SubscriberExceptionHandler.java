package com.google.common.eventbus;

import com.google.common.eventbus.SubscriberExceptionContext;

public interface SubscriberExceptionHandler {
   void handleException(Throwable var1, SubscriberExceptionContext var2);
}
