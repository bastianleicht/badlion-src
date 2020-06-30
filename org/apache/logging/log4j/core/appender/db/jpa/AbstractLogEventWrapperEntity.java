package org.apache.logging.log4j.core.appender.db.jpa;

import java.util.Map;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.Message;

@MappedSuperclass
@Inheritance(
   strategy = InheritanceType.SINGLE_TABLE
)
public abstract class AbstractLogEventWrapperEntity implements LogEvent {
   private static final long serialVersionUID = 1L;
   private final LogEvent wrappedEvent;

   protected AbstractLogEventWrapperEntity() {
      this(new AbstractLogEventWrapperEntity.NullLogEvent());
   }

   protected AbstractLogEventWrapperEntity(LogEvent wrappedEvent) {
      if(wrappedEvent == null) {
         throw new IllegalArgumentException("The wrapped event cannot be null.");
      } else {
         this.wrappedEvent = wrappedEvent;
      }
   }

   @Transient
   protected final LogEvent getWrappedEvent() {
      return this.wrappedEvent;
   }

   public void setLevel(Level level) {
   }

   public void setLoggerName(String loggerName) {
   }

   public void setSource(StackTraceElement source) {
   }

   public void setMessage(Message message) {
   }

   public void setMarker(Marker marker) {
   }

   public void setThreadName(String threadName) {
   }

   public void setMillis(long millis) {
   }

   public void setThrown(Throwable throwable) {
   }

   public void setContextMap(Map contextMap) {
   }

   public void setContextStack(ThreadContext.ContextStack contextStack) {
   }

   public void setFQCN(String fqcn) {
   }

   @Transient
   public final boolean isIncludeLocation() {
      return this.getWrappedEvent().isIncludeLocation();
   }

   public final void setIncludeLocation(boolean locationRequired) {
      this.getWrappedEvent().setIncludeLocation(locationRequired);
   }

   @Transient
   public final boolean isEndOfBatch() {
      return this.getWrappedEvent().isEndOfBatch();
   }

   public final void setEndOfBatch(boolean endOfBatch) {
      this.getWrappedEvent().setEndOfBatch(endOfBatch);
   }

   private static class NullLogEvent implements LogEvent {
      private static final long serialVersionUID = 1L;

      private NullLogEvent() {
      }

      public Level getLevel() {
         return null;
      }

      public String getLoggerName() {
         return null;
      }

      public StackTraceElement getSource() {
         return null;
      }

      public Message getMessage() {
         return null;
      }

      public Marker getMarker() {
         return null;
      }

      public String getThreadName() {
         return null;
      }

      public long getMillis() {
         return 0L;
      }

      public Throwable getThrown() {
         return null;
      }

      public Map getContextMap() {
         return null;
      }

      public ThreadContext.ContextStack getContextStack() {
         return null;
      }

      public String getFQCN() {
         return null;
      }

      public boolean isIncludeLocation() {
         return false;
      }

      public void setIncludeLocation(boolean locationRequired) {
      }

      public boolean isEndOfBatch() {
         return false;
      }

      public void setEndOfBatch(boolean endOfBatch) {
      }
   }
}
