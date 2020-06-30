package org.apache.logging.log4j.core.appender.db.jpa;

import java.util.Map;
import javax.persistence.Basic;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.db.jpa.AbstractLogEventWrapperEntity;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextMapAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ContextStackAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.MarkerAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.MessageAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.StackTraceElementAttributeConverter;
import org.apache.logging.log4j.core.appender.db.jpa.converter.ThrowableAttributeConverter;
import org.apache.logging.log4j.message.Message;

@MappedSuperclass
public abstract class BasicLogEventEntity extends AbstractLogEventWrapperEntity {
   private static final long serialVersionUID = 1L;

   public BasicLogEventEntity() {
   }

   public BasicLogEventEntity(LogEvent wrappedEvent) {
      super(wrappedEvent);
   }

   @Basic
   @Enumerated(EnumType.STRING)
   public Level getLevel() {
      return this.getWrappedEvent().getLevel();
   }

   @Basic
   public String getLoggerName() {
      return this.getWrappedEvent().getLoggerName();
   }

   @Convert(
      converter = StackTraceElementAttributeConverter.class
   )
   public StackTraceElement getSource() {
      return this.getWrappedEvent().getSource();
   }

   @Convert(
      converter = MessageAttributeConverter.class
   )
   public Message getMessage() {
      return this.getWrappedEvent().getMessage();
   }

   @Convert(
      converter = MarkerAttributeConverter.class
   )
   public Marker getMarker() {
      return this.getWrappedEvent().getMarker();
   }

   @Basic
   public String getThreadName() {
      return this.getWrappedEvent().getThreadName();
   }

   @Basic
   public long getMillis() {
      return this.getWrappedEvent().getMillis();
   }

   @Convert(
      converter = ThrowableAttributeConverter.class
   )
   public Throwable getThrown() {
      return this.getWrappedEvent().getThrown();
   }

   @Convert(
      converter = ContextMapAttributeConverter.class
   )
   public Map getContextMap() {
      return this.getWrappedEvent().getContextMap();
   }

   @Convert(
      converter = ContextStackAttributeConverter.class
   )
   public ThreadContext.ContextStack getContextStack() {
      return this.getWrappedEvent().getContextStack();
   }

   @Basic
   public String getFQCN() {
      return this.getWrappedEvent().getFQCN();
   }
}
