package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.AbstractTrafficShapingHandler;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.concurrent.EventExecutor;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class GlobalTrafficShapingHandler extends AbstractTrafficShapingHandler {
   private Map messagesQueues = new HashMap();

   void createGlobalTrafficCounter(ScheduledExecutorService executor) {
      if(executor == null) {
         throw new NullPointerException("executor");
      } else {
         TrafficCounter tc = new TrafficCounter(this, executor, "GlobalTC", this.checkInterval);
         this.setTrafficCounter(tc);
         tc.start();
      }
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval, long maxTime) {
      super(writeLimit, readLimit, checkInterval, maxTime);
      this.createGlobalTrafficCounter(executor);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit, long checkInterval) {
      super(writeLimit, readLimit, checkInterval);
      this.createGlobalTrafficCounter(executor);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long writeLimit, long readLimit) {
      super(writeLimit, readLimit);
      this.createGlobalTrafficCounter(executor);
   }

   public GlobalTrafficShapingHandler(ScheduledExecutorService executor, long checkInterval) {
      super(checkInterval);
      this.createGlobalTrafficCounter(executor);
   }

   public GlobalTrafficShapingHandler(EventExecutor executor) {
      this.createGlobalTrafficCounter(executor);
   }

   public final void release() {
      if(this.trafficCounter != null) {
         this.trafficCounter.stop();
      }

   }

   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      Integer key = Integer.valueOf(ctx.channel().hashCode());
      List<GlobalTrafficShapingHandler.ToSend> mq = new LinkedList();
      this.messagesQueues.put(key, mq);
   }

   public synchronized void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      Integer key = Integer.valueOf(ctx.channel().hashCode());
      List<GlobalTrafficShapingHandler.ToSend> mq = (List)this.messagesQueues.remove(key);
      if(mq != null) {
         for(GlobalTrafficShapingHandler.ToSend toSend : mq) {
            if(toSend.toSend instanceof ByteBuf) {
               ((ByteBuf)toSend.toSend).release();
            }
         }

         mq.clear();
      }

   }

   protected synchronized void submitWrite(final ChannelHandlerContext ctx, Object msg, long delay, ChannelPromise promise) {
      Integer key = Integer.valueOf(ctx.channel().hashCode());
      final List<GlobalTrafficShapingHandler.ToSend> messagesQueue = (List)this.messagesQueues.get(key);
      if(delay != 0L || messagesQueue != null && !messagesQueue.isEmpty()) {
         GlobalTrafficShapingHandler.ToSend newToSend = new GlobalTrafficShapingHandler.ToSend(delay, msg, promise);
         if(messagesQueue == null) {
            messagesQueue = new LinkedList();
            this.messagesQueues.put(key, messagesQueue);
         }

         messagesQueue.add(newToSend);
         ctx.executor().schedule(new Runnable() {
            public void run() {
               GlobalTrafficShapingHandler.this.sendAllValid(ctx, messagesQueue);
            }
         }, delay, TimeUnit.MILLISECONDS);
      } else {
         ctx.write(msg, promise);
      }
   }

   private synchronized void sendAllValid(ChannelHandlerContext ctx, List messagesQueue) {
      while(true) {
         if(!messagesQueue.isEmpty()) {
            GlobalTrafficShapingHandler.ToSend newToSend = (GlobalTrafficShapingHandler.ToSend)messagesQueue.remove(0);
            if(newToSend.date <= System.currentTimeMillis()) {
               ctx.write(newToSend.toSend, newToSend.promise);
               continue;
            }

            messagesQueue.add(0, newToSend);
         }

         ctx.flush();
         return;
      }
   }

   private static final class ToSend {
      final long date;
      final Object toSend;
      final ChannelPromise promise;

      private ToSend(long delay, Object toSend, ChannelPromise promise) {
         this.date = System.currentTimeMillis() + delay;
         this.toSend = toSend;
         this.promise = promise;
      }
   }
}
