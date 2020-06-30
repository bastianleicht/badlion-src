package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.EventFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.async.AsyncLogger;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.lookup.StrSubstitutor;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;

public class RingBufferLogEvent implements LogEvent {
   private static final long serialVersionUID = 8462119088943934758L;
   public static final RingBufferLogEvent.Factory FACTORY = new RingBufferLogEvent.Factory();
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
   private boolean endOfBatch;
   private boolean includeLocation;

   public void setValues(AsyncLogger asyncLogger, String loggerName, Marker marker, String fqcn, Level level, Message data, Throwable t, Map map, ThreadContext.ContextStack contextStack, String threadName, StackTraceElement location, long currentTimeMillis) {
      this.asyncLogger = asyncLogger;
      this.loggerName = loggerName;
      this.marker = marker;
      this.fqcn = fqcn;
      this.level = level;
      this.message = data;
      this.thrown = t;
      this.contextMap = map;
      this.contextStack = contextStack;
      this.threadName = threadName;
      this.location = location;
      this.currentTimeMillis = currentTimeMillis;
   }

   public void execute(boolean endOfBatch) {
      this.endOfBatch = endOfBatch;
      this.asyncLogger.actualAsyncLog(this);
   }

   public boolean isEndOfBatch() {
      return this.endOfBatch;
   }

   public void setEndOfBatch(boolean endOfBatch) {
      this.endOfBatch = endOfBatch;
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public void setIncludeLocation(boolean includeLocation) {
      this.includeLocation = includeLocation;
   }

   public String getLoggerName() {
      return this.loggerName;
   }

   public Marker getMarker() {
      return this.marker;
   }

   public String getFQCN() {
      return this.fqcn;
   }

   public Level getLevel() {
      return this.level;
   }

   public Message getMessage() {
      if(this.message == null) {
         this.message = new SimpleMessage("");
      }

      return this.message;
   }

   public Throwable getThrown() {
      return this.thrown;
   }

   public Map getContextMap() {
      return this.contextMap;
   }

   public ThreadContext.ContextStack getContextStack() {
      return this.contextStack;
   }

   public String getThreadName() {
      return this.threadName;
   }

   public StackTraceElement getSource() {
      return this.location;
   }

   public long getMillis() {
      return this.currentTimeMillis;
   }

   public void mergePropertiesIntoContextMap(Map properties, StrSubstitutor strSubstitutor) {
      if(properties != null) {
         Map<String, String> map = this.contextMap == null?new HashMap():new HashMap(this.contextMap);

         for(Entry<Property, Boolean> entry : properties.entrySet()) {
            Property prop = (Property)entry.getKey();
            if(!map.containsKey(prop.getName())) {
               String value = ((Boolean)entry.getValue()).booleanValue()?strSubstitutor.replace(prop.getValue()):prop.getValue();
               map.put(prop.getName(), value);
            }
         }

         this.contextMap = map;
      }
   }

   public void clear() {
      this.setValues((AsyncLogger)null, (String)null, (Marker)null, (String)null, (Level)null, (Message)null, (Throwable)null, (Map)null, (ThreadContext.ContextStack)null, (String)null, (StackTraceElement)null, 0L);
   }

   private static class Factory implements EventFactory {
      private Factory() {
      }

      public RingBufferLogEvent newInstance() {
         return new RingBufferLogEvent();
      }
   }
}
