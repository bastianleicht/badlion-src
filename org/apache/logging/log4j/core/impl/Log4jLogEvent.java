package org.apache.logging.log4j.core.impl;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.message.LoggerNameAwareMessage;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.TimestampMessage;

public class Log4jLogEvent implements LogEvent {
   private static final long serialVersionUID = -1351367343806656055L;
   private static final String NOT_AVAIL = "?";
   private final String fqcnOfLogger;
   private final Marker marker;
   private final Level level;
   private final String name;
   private final Message message;
   private final long timestamp;
   private final ThrowableProxy throwable;
   private final Map mdc;
   private final ThreadContext.ContextStack ndc;
   private String threadName;
   private StackTraceElement location;
   private boolean includeLocation;
   private boolean endOfBatch;

   public Log4jLogEvent(long timestamp) {
      this("", (Marker)null, "", (Level)null, (Message)null, (ThrowableProxy)((ThrowableProxy)null), (Map)null, (ThreadContext.ContextStack)null, (String)null, (StackTraceElement)null, timestamp);
   }

   public Log4jLogEvent(String loggerName, Marker marker, String fqcn, Level level, Message message, Throwable t) {
      this(loggerName, marker, fqcn, level, message, (List)null, t);
   }

   public Log4jLogEvent(String loggerName, Marker marker, String fqcn, Level level, Message message, List properties, Throwable t) {
      this(loggerName, marker, fqcn, level, message, (Throwable)t, createMap(properties), ThreadContext.getDepth() == 0?null:ThreadContext.cloneStack(), (String)null, (StackTraceElement)null, System.currentTimeMillis());
   }

   public Log4jLogEvent(String loggerName, Marker marker, String fqcn, Level level, Message message, Throwable t, Map mdc, ThreadContext.ContextStack ndc, String threadName, StackTraceElement location, long timestamp) {
      this(loggerName, marker, fqcn, level, message, t == null?null:new ThrowableProxy(t), mdc, ndc, threadName, location, timestamp);
   }

   public static Log4jLogEvent createEvent(String loggerName, Marker marker, String fqcn, Level level, Message message, ThrowableProxy t, Map mdc, ThreadContext.ContextStack ndc, String threadName, StackTraceElement location, long timestamp) {
      return new Log4jLogEvent(loggerName, marker, fqcn, level, message, t, mdc, ndc, threadName, location, timestamp);
   }

   private Log4jLogEvent(String loggerName, Marker marker, String fqcn, Level level, Message message, ThrowableProxy t, Map mdc, ThreadContext.ContextStack ndc, String threadName, StackTraceElement location, long timestamp) {
      this.threadName = null;
      this.endOfBatch = false;
      this.name = loggerName;
      this.marker = marker;
      this.fqcnOfLogger = fqcn;
      this.level = level;
      this.message = message;
      this.throwable = t;
      this.mdc = mdc;
      this.ndc = ndc;
      this.timestamp = message instanceof TimestampMessage?((TimestampMessage)message).getTimestamp():timestamp;
      this.threadName = threadName;
      this.location = location;
      if(message != null && message instanceof LoggerNameAwareMessage) {
         ((LoggerNameAwareMessage)message).setLoggerName(this.name);
      }

   }

   private static Map createMap(List properties) {
      Map<String, String> contextMap = ThreadContext.getImmutableContext();
      if(contextMap != null || properties != null && properties.size() != 0) {
         if(properties != null && properties.size() != 0) {
            Map<String, String> map = new HashMap(contextMap);

            for(Property prop : properties) {
               if(!map.containsKey(prop.getName())) {
                  map.put(prop.getName(), prop.getValue());
               }
            }

            return Collections.unmodifiableMap(map);
         } else {
            return contextMap;
         }
      } else {
         return null;
      }
   }

   public Level getLevel() {
      return this.level;
   }

   public String getLoggerName() {
      return this.name;
   }

   public Message getMessage() {
      return this.message;
   }

   public String getThreadName() {
      if(this.threadName == null) {
         this.threadName = Thread.currentThread().getName();
      }

      return this.threadName;
   }

   public long getMillis() {
      return this.timestamp;
   }

   public Throwable getThrown() {
      return this.throwable == null?null:this.throwable.getThrowable();
   }

   public ThrowableProxy getThrownProxy() {
      return this.throwable;
   }

   public Marker getMarker() {
      return this.marker;
   }

   public String getFQCN() {
      return this.fqcnOfLogger;
   }

   public Map getContextMap() {
      return this.mdc == null?ThreadContext.EMPTY_MAP:this.mdc;
   }

   public ThreadContext.ContextStack getContextStack() {
      return (ThreadContext.ContextStack)(this.ndc == null?ThreadContext.EMPTY_STACK:this.ndc);
   }

   public StackTraceElement getSource() {
      if(this.location != null) {
         return this.location;
      } else if(this.fqcnOfLogger != null && this.includeLocation) {
         this.location = calcLocation(this.fqcnOfLogger);
         return this.location;
      } else {
         return null;
      }
   }

   public static StackTraceElement calcLocation(String fqcnOfLogger) {
      if(fqcnOfLogger == null) {
         return null;
      } else {
         StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
         boolean next = false;

         for(StackTraceElement element : stackTrace) {
            String className = element.getClassName();
            if(next) {
               if(!fqcnOfLogger.equals(className)) {
                  return element;
               }
            } else if(fqcnOfLogger.equals(className)) {
               next = true;
            } else if("?".equals(className)) {
               break;
            }
         }

         return null;
      }
   }

   public boolean isIncludeLocation() {
      return this.includeLocation;
   }

   public void setIncludeLocation(boolean includeLocation) {
      this.includeLocation = includeLocation;
   }

   public boolean isEndOfBatch() {
      return this.endOfBatch;
   }

   public void setEndOfBatch(boolean endOfBatch) {
      this.endOfBatch = endOfBatch;
   }

   protected Object writeReplace() {
      return new Log4jLogEvent.LogEventProxy(this, this.includeLocation);
   }

   public static Serializable serialize(Log4jLogEvent event, boolean includeLocation) {
      return new Log4jLogEvent.LogEventProxy(event, includeLocation);
   }

   public static Log4jLogEvent deserialize(Serializable event) {
      if(event == null) {
         throw new NullPointerException("Event cannot be null");
      } else if(event instanceof Log4jLogEvent.LogEventProxy) {
         Log4jLogEvent.LogEventProxy proxy = (Log4jLogEvent.LogEventProxy)event;
         Log4jLogEvent result = new Log4jLogEvent(proxy.name, proxy.marker, proxy.fqcnOfLogger, proxy.level, proxy.message, proxy.throwable, proxy.mdc, proxy.ndc, proxy.threadName, proxy.location, proxy.timestamp);
         result.setEndOfBatch(proxy.isEndOfBatch);
         result.setIncludeLocation(proxy.isLocationRequired);
         return result;
      } else {
         throw new IllegalArgumentException("Event is not a serialized LogEvent: " + event.toString());
      }
   }

   private void readObject(ObjectInputStream stream) throws InvalidObjectException {
      throw new InvalidObjectException("Proxy required");
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();
      String n = this.name.isEmpty()?"root":this.name;
      sb.append("Logger=").append(n);
      sb.append(" Level=").append(this.level.name());
      sb.append(" Message=").append(this.message.getFormattedMessage());
      return sb.toString();
   }

   private static class LogEventProxy implements Serializable {
      private static final long serialVersionUID = -7139032940312647146L;
      private final String fqcnOfLogger;
      private final Marker marker;
      private final Level level;
      private final String name;
      private final Message message;
      private final long timestamp;
      private final ThrowableProxy throwable;
      private final Map mdc;
      private final ThreadContext.ContextStack ndc;
      private final String threadName;
      private final StackTraceElement location;
      private final boolean isLocationRequired;
      private final boolean isEndOfBatch;

      public LogEventProxy(Log4jLogEvent event, boolean includeLocation) {
         this.fqcnOfLogger = event.fqcnOfLogger;
         this.marker = event.marker;
         this.level = event.level;
         this.name = event.name;
         this.message = event.message;
         this.timestamp = event.timestamp;
         this.throwable = event.throwable;
         this.mdc = event.mdc;
         this.ndc = event.ndc;
         this.location = includeLocation?event.getSource():null;
         this.threadName = event.getThreadName();
         this.isLocationRequired = includeLocation;
         this.isEndOfBatch = event.endOfBatch;
      }

      protected Object readResolve() {
         Log4jLogEvent result = new Log4jLogEvent(this.name, this.marker, this.fqcnOfLogger, this.level, this.message, this.throwable, this.mdc, this.ndc, this.threadName, this.location, this.timestamp);
         result.setEndOfBatch(this.isEndOfBatch);
         result.setIncludeLocation(this.isLocationRequired);
         return result;
      }
   }
}
