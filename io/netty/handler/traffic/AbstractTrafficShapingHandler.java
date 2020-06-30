package io.netty.handler.traffic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.traffic.TrafficCounter;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTrafficShapingHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractTrafficShapingHandler.class);
   public static final long DEFAULT_CHECK_INTERVAL = 1000L;
   public static final long DEFAULT_MAX_TIME = 15000L;
   static final long MINIMAL_WAIT = 10L;
   protected TrafficCounter trafficCounter;
   private long writeLimit;
   private long readLimit;
   protected long maxTime;
   protected long checkInterval;
   private static final AttributeKey READ_SUSPENDED = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".READ_SUSPENDED");
   private static final AttributeKey REOPEN_TASK = AttributeKey.valueOf(AbstractTrafficShapingHandler.class.getName() + ".REOPEN_TASK");

   void setTrafficCounter(TrafficCounter newTrafficCounter) {
      this.trafficCounter = newTrafficCounter;
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval, long maxTime) {
      this.maxTime = 15000L;
      this.checkInterval = 1000L;
      this.writeLimit = writeLimit;
      this.readLimit = readLimit;
      this.checkInterval = checkInterval;
      this.maxTime = maxTime;
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit, long checkInterval) {
      this(writeLimit, readLimit, checkInterval, 15000L);
   }

   protected AbstractTrafficShapingHandler(long writeLimit, long readLimit) {
      this(writeLimit, readLimit, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler() {
      this(0L, 0L, 1000L, 15000L);
   }

   protected AbstractTrafficShapingHandler(long checkInterval) {
      this(0L, 0L, checkInterval, 15000L);
   }

   public void configure(long newWriteLimit, long newReadLimit, long newCheckInterval) {
      this.configure(newWriteLimit, newReadLimit);
      this.configure(newCheckInterval);
   }

   public void configure(long newWriteLimit, long newReadLimit) {
      this.writeLimit = newWriteLimit;
      this.readLimit = newReadLimit;
      if(this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
      }

   }

   public void configure(long newCheckInterval) {
      this.checkInterval = newCheckInterval;
      if(this.trafficCounter != null) {
         this.trafficCounter.configure(this.checkInterval);
      }

   }

   public long getWriteLimit() {
      return this.writeLimit;
   }

   public void setWriteLimit(long writeLimit) {
      this.writeLimit = writeLimit;
      if(this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
      }

   }

   public long getReadLimit() {
      return this.readLimit;
   }

   public void setReadLimit(long readLimit) {
      this.readLimit = readLimit;
      if(this.trafficCounter != null) {
         this.trafficCounter.resetAccounting(System.currentTimeMillis() + 1L);
      }

   }

   public long getCheckInterval() {
      return this.checkInterval;
   }

   public void setCheckInterval(long checkInterval) {
      this.checkInterval = checkInterval;
      if(this.trafficCounter != null) {
         this.trafficCounter.configure(checkInterval);
      }

   }

   public void setMaxTimeWait(long maxTime) {
      this.maxTime = maxTime;
   }

   public long getMaxTimeWait() {
      return this.maxTime;
   }

   protected void doAccounting(TrafficCounter counter) {
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      long size = this.calculateSize(msg);
      if(size > 0L && this.trafficCounter != null) {
         long wait = this.trafficCounter.readTimeToWait(size, this.readLimit, this.maxTime);
         if(wait >= 10L) {
            if(logger.isDebugEnabled()) {
               logger.debug("Channel:" + ctx.channel().hashCode() + " Read Suspend: " + wait + ":" + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx));
            }

            if(ctx.channel().config().isAutoRead() && isHandlerActive(ctx)) {
               ctx.channel().config().setAutoRead(false);
               ctx.attr(READ_SUSPENDED).set(Boolean.valueOf(true));
               Attribute<Runnable> attr = ctx.attr(REOPEN_TASK);
               Runnable reopenTask = (Runnable)attr.get();
               if(reopenTask == null) {
                  reopenTask = new AbstractTrafficShapingHandler.ReopenReadTimerTask(ctx);
                  attr.set(reopenTask);
               }

               ctx.executor().schedule(reopenTask, wait, TimeUnit.MILLISECONDS);
               if(logger.isDebugEnabled()) {
                  logger.debug("Channel:" + ctx.channel().hashCode() + " Suspend final status => " + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx) + " will reopened at: " + wait);
               }
            }
         }
      }

      ctx.fireChannelRead(msg);
   }

   protected static boolean isHandlerActive(ChannelHandlerContext ctx) {
      Boolean suspended = (Boolean)ctx.attr(READ_SUSPENDED).get();
      return suspended == null || Boolean.FALSE.equals(suspended);
   }

   public void read(ChannelHandlerContext ctx) {
      if(isHandlerActive(ctx)) {
         ctx.read();
      }

   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      long size = this.calculateSize(msg);
      if(size > 0L && this.trafficCounter != null) {
         long wait = this.trafficCounter.writeTimeToWait(size, this.writeLimit, this.maxTime);
         if(wait >= 10L) {
            if(logger.isDebugEnabled()) {
               logger.debug("Channel:" + ctx.channel().hashCode() + " Write suspend: " + wait + ":" + ctx.channel().config().isAutoRead() + ":" + isHandlerActive(ctx));
            }

            this.submitWrite(ctx, msg, wait, promise);
            return;
         }
      }

      this.submitWrite(ctx, msg, 0L, promise);
   }

   protected abstract void submitWrite(ChannelHandlerContext var1, Object var2, long var3, ChannelPromise var5);

   public TrafficCounter trafficCounter() {
      return this.trafficCounter;
   }

   public String toString() {
      return "TrafficShaping with Write Limit: " + this.writeLimit + " Read Limit: " + this.readLimit + " and Counter: " + (this.trafficCounter != null?this.trafficCounter.toString():"none");
   }

   protected long calculateSize(Object msg) {
      return msg instanceof ByteBuf?(long)((ByteBuf)msg).readableBytes():(msg instanceof ByteBufHolder?(long)((ByteBufHolder)msg).content().readableBytes():-1L);
   }

   private static final class ReopenReadTimerTask implements Runnable {
      final ChannelHandlerContext ctx;

      ReopenReadTimerTask(ChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      public void run() {
         if(!this.ctx.channel().config().isAutoRead() && AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
            if(AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               AbstractTrafficShapingHandler.logger.debug("Channel:" + this.ctx.channel().hashCode() + " Not Unsuspend: " + this.ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
            }

            this.ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
         } else {
            if(AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
               if(this.ctx.channel().config().isAutoRead() && !AbstractTrafficShapingHandler.isHandlerActive(this.ctx)) {
                  AbstractTrafficShapingHandler.logger.debug("Channel:" + this.ctx.channel().hashCode() + " Unsuspend: " + this.ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
               } else {
                  AbstractTrafficShapingHandler.logger.debug("Channel:" + this.ctx.channel().hashCode() + " Normal Unsuspend: " + this.ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
               }
            }

            this.ctx.attr(AbstractTrafficShapingHandler.READ_SUSPENDED).set(Boolean.valueOf(false));
            this.ctx.channel().config().setAutoRead(true);
            this.ctx.channel().read();
         }

         if(AbstractTrafficShapingHandler.logger.isDebugEnabled()) {
            AbstractTrafficShapingHandler.logger.debug("Channel:" + this.ctx.channel().hashCode() + " Unsupsend final status => " + this.ctx.channel().config().isAutoRead() + ":" + AbstractTrafficShapingHandler.isHandlerActive(this.ctx));
         }

      }
   }
}
