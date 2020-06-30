package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.DefaultErrorHandler;
import org.apache.logging.log4j.core.filter.AbstractFilterable;
import org.apache.logging.log4j.core.helpers.Integers;

public abstract class AbstractAppender extends AbstractFilterable implements Appender {
   private final boolean ignoreExceptions;
   private ErrorHandler handler;
   private final Layout layout;
   private final String name;
   private boolean started;

   public static int parseInt(String s, int defaultValue) {
      try {
         return Integers.parseInt(s, defaultValue);
      } catch (NumberFormatException var3) {
         LOGGER.error("Could not parse \"{}\" as an integer,  using default value {}: {}", new Object[]{s, Integer.valueOf(defaultValue), var3});
         return defaultValue;
      }
   }

   protected AbstractAppender(String name, Filter filter, Layout layout) {
      this(name, filter, layout, true);
   }

   protected AbstractAppender(String name, Filter filter, Layout layout, boolean ignoreExceptions) {
      super(filter);
      this.handler = new DefaultErrorHandler(this);
      this.started = false;
      this.name = name;
      this.layout = layout;
      this.ignoreExceptions = ignoreExceptions;
   }

   public void error(String msg) {
      this.handler.error(msg);
   }

   public void error(String msg, LogEvent event, Throwable t) {
      this.handler.error(msg, event, t);
   }

   public void error(String msg, Throwable t) {
      this.handler.error(msg, t);
   }

   public ErrorHandler getHandler() {
      return this.handler;
   }

   public Layout getLayout() {
      return this.layout;
   }

   public String getName() {
      return this.name;
   }

   public boolean ignoreExceptions() {
      return this.ignoreExceptions;
   }

   public boolean isStarted() {
      return this.started;
   }

   public void setHandler(ErrorHandler handler) {
      if(handler == null) {
         LOGGER.error("The handler cannot be set to null");
      }

      if(this.isStarted()) {
         LOGGER.error("The handler cannot be changed once the appender is started");
      } else {
         this.handler = handler;
      }
   }

   public void start() {
      this.startFilter();
      this.started = true;
   }

   public void stop() {
      this.started = false;
      this.stopFilter();
   }

   public String toString() {
      return this.name;
   }
}
