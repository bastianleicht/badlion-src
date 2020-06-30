package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.Util;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.async.DaemonThreadFactory;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.core.async.RingBufferLogEventHandler;
import org.apache.logging.log4j.core.async.RingBufferLogEventTranslator;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.helpers.Clock;
import org.apache.logging.log4j.core.helpers.ClockFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.status.StatusLogger;

public class AsyncLogger extends Logger {
   private static final int HALF_A_SECOND = 500;
   private static final int MAX_DRAIN_ATTEMPTS_BEFORE_SHUTDOWN = 20;
   private static final int RINGBUFFER_MIN_SIZE = 128;
   private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static volatile Disruptor disruptor;
   private static Clock clock = ClockFactory.getClock();
   private static ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("AsyncLogger-"));
   private final ThreadLocal threadlocalInfo = new ThreadLocal();

   private static int calculateRingBufferSize() {
      int ringBufferSize = 262144;
      String userPreferredRBSize = System.getProperty("AsyncLogger.RingBufferSize", String.valueOf(ringBufferSize));

      try {
         int size = Integer.parseInt(userPreferredRBSize);
         if(size < 128) {
            size = 128;
            LOGGER.warn("Invalid RingBufferSize {}, using minimum size {}.", new Object[]{userPreferredRBSize, Integer.valueOf(128)});
         }

         ringBufferSize = size;
      } catch (Exception var3) {
         LOGGER.warn("Invalid RingBufferSize {}, using default size {}.", new Object[]{userPreferredRBSize, Integer.valueOf(ringBufferSize)});
      }

      return Util.ceilingNextPowerOfTwo(ringBufferSize);
   }

   private static WaitStrategy createWaitStrategy() {
      String strategy = System.getProperty("AsyncLogger.WaitStrategy");
      LOGGER.debug("property AsyncLogger.WaitStrategy={}", new Object[]{strategy});
      if("Sleep".equals(strategy)) {
         LOGGER.debug("disruptor event handler uses SleepingWaitStrategy");
         return new SleepingWaitStrategy();
      } else if("Yield".equals(strategy)) {
         LOGGER.debug("disruptor event handler uses YieldingWaitStrategy");
         return new YieldingWaitStrategy();
      } else if("Block".equals(strategy)) {
         LOGGER.debug("disruptor event handler uses BlockingWaitStrategy");
         return new BlockingWaitStrategy();
      } else {
         LOGGER.debug("disruptor event handler uses SleepingWaitStrategy");
         return new SleepingWaitStrategy();
      }
   }

   private static ExceptionHandler getExceptionHandler() {
      String cls = System.getProperty("AsyncLogger.ExceptionHandler");
      if(cls == null) {
         LOGGER.debug("No AsyncLogger.ExceptionHandler specified");
         return null;
      } else {
         try {
            Class<? extends ExceptionHandler> klass = Class.forName(cls);
            ExceptionHandler result = (ExceptionHandler)klass.newInstance();
            LOGGER.debug("AsyncLogger.ExceptionHandler=" + result);
            return result;
         } catch (Exception var3) {
            LOGGER.debug("AsyncLogger.ExceptionHandler not set: error creating " + cls + ": ", var3);
            return null;
         }
      }
   }

   public AsyncLogger(LoggerContext context, String name, MessageFactory messageFactory) {
      super(context, name, messageFactory);
   }

   public void log(Marker marker, String fqcn, Level level, Message data, Throwable t) {
      AsyncLogger.Info info = (AsyncLogger.Info)this.threadlocalInfo.get();
      if(info == null) {
         info = new AsyncLogger.Info();
         info.translator = new RingBufferLogEventTranslator();
         info.cachedThreadName = Thread.currentThread().getName();
         this.threadlocalInfo.set(info);
      }

      boolean includeLocation = this.config.loggerConfig.isIncludeLocation();
      info.translator.setValues(this, this.getName(), marker, fqcn, level, data, t, ThreadContext.getImmutableContext(), ThreadContext.getImmutableStack(), info.cachedThreadName, includeLocation?this.location(fqcn):null, clock.currentTimeMillis());
      disruptor.publishEvent(info.translator);
   }

   private StackTraceElement location(String fqcnOfLogger) {
      return Log4jLogEvent.calcLocation(fqcnOfLogger);
   }

   public void actualAsyncLog(RingBufferLogEvent event) {
      Map<Property, Boolean> properties = this.config.loggerConfig.getProperties();
      event.mergePropertiesIntoContextMap(properties, this.config.config.getStrSubstitutor());
      this.config.logEvent(event);
   }

   public static void stop() {
      Disruptor<RingBufferLogEvent> temp = disruptor;
      disruptor = null;
      temp.shutdown();
      RingBuffer<RingBufferLogEvent> ringBuffer = temp.getRingBuffer();

      for(int i = 0; i < 20 && !ringBuffer.hasAvailableCapacity(ringBuffer.getBufferSize()); ++i) {
         try {
            Thread.sleep(500L);
         } catch (InterruptedException var4) {
            ;
         }
      }

      executor.shutdown();
   }

   static {
      int ringBufferSize = calculateRingBufferSize();
      WaitStrategy waitStrategy = createWaitStrategy();
      disruptor = new Disruptor(RingBufferLogEvent.FACTORY, ringBufferSize, executor, ProducerType.MULTI, waitStrategy);
      EventHandler<RingBufferLogEvent>[] handlers = new RingBufferLogEventHandler[]{new RingBufferLogEventHandler()};
      disruptor.handleExceptionsWith(getExceptionHandler());
      disruptor.handleEventsWith(handlers);
      LOGGER.debug("Starting AsyncLogger disruptor with ringbuffer size {}...", new Object[]{Integer.valueOf(disruptor.getRingBuffer().getBufferSize())});
      disruptor.start();
   }

   private static class Info {
      private RingBufferLogEventTranslator translator;
      private String cachedThreadName;

      private Info() {
      }
   }
}
