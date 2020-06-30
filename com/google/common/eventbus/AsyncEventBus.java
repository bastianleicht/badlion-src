package com.google.common.eventbus;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.EventSubscriber;
import com.google.common.eventbus.SubscriberExceptionHandler;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;

@Beta
public class AsyncEventBus extends EventBus {
   private final Executor executor;
   private final ConcurrentLinkedQueue eventsToDispatch = new ConcurrentLinkedQueue();

   public AsyncEventBus(String identifier, Executor executor) {
      super(identifier);
      this.executor = (Executor)Preconditions.checkNotNull(executor);
   }

   public AsyncEventBus(Executor executor, SubscriberExceptionHandler subscriberExceptionHandler) {
      super(subscriberExceptionHandler);
      this.executor = (Executor)Preconditions.checkNotNull(executor);
   }

   public AsyncEventBus(Executor executor) {
      super("default");
      this.executor = (Executor)Preconditions.checkNotNull(executor);
   }

   void enqueueEvent(Object event, EventSubscriber subscriber) {
      this.eventsToDispatch.offer(new EventBus.EventWithSubscriber(event, subscriber));
   }

   protected void dispatchQueuedEvents() {
      while(true) {
         EventBus.EventWithSubscriber eventWithSubscriber = (EventBus.EventWithSubscriber)this.eventsToDispatch.poll();
         if(eventWithSubscriber == null) {
            return;
         }

         this.dispatch(eventWithSubscriber.event, eventWithSubscriber.subscriber);
      }
   }

   void dispatch(final Object event, final EventSubscriber subscriber) {
      Preconditions.checkNotNull(event);
      Preconditions.checkNotNull(subscriber);
      this.executor.execute(new Runnable() {
         public void run() {
            AsyncEventBus.super.dispatch(event, subscriber);
         }
      });
   }
}
