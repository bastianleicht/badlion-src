package com.google.common.eventbus;

import com.google.common.eventbus.EventSubscriber;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class SynchronizedEventSubscriber extends EventSubscriber {
   public SynchronizedEventSubscriber(Object target, Method method) {
      super(target, method);
   }

   public void handleEvent(Object event) throws InvocationTargetException {
      synchronized(this) {
         super.handleEvent(event);
      }
   }
}
