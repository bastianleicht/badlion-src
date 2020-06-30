package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventTranslator;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;
import org.apache.logging.log4j.message.Message;

public class RingBufferLogEventTranslator implements EventTranslator {
   private AsyncLogger asyncLogger;
   private String loggerName;
   private Marker marker;
   private String fqcn;
   private Level level;
   private Message message;
   private Throwable thrown;
   private Map contextMap;
   private ThreadContext.ContextStack contextStack;
   private String threadName;
   private StackTraceElement location;
   private long currentTimeMillis;

   public void translateTo(RingBufferLogEvent event, long sequence) {
      event.setValues(this.asyncLogger, this.loggerName, this.marker, this.fqcn, this.level, this.message, this.thrown, this.contextMap, this.contextStack, this.threadName, this.location, this.currentTimeMillis);
   }

   public void setValues(AsyncLogger asyncLogger, String loggerName, Marker marker, String fqcn, Level level, Message message, Throwable thrown, Map contextMap, ThreadContext.ContextStack contextStack, String threadName, StackTraceElement location, long currentTimeMillis) {
      this.asyncLogger = asyncLogger;
      this.loggerName = loggerName;
      this.marker = marker;
      this.fqcn = fqcn;
      this.level = level;
      this.message = message;
      this.thrown = thrown;
      this.contextMap = contextMap;
      this.contextStack = contextStack;
      this.threadName = threadName;
      this.location = location;
      this.currentTimeMillis = currentTimeMillis;
   }
}
