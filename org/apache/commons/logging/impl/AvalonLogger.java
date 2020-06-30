package org.apache.commons.logging.impl;

import org.apache.avalon.framework.logger.Logger;
import org.apache.commons.logging.Log;

public class AvalonLogger implements Log {
   private static volatile Logger defaultLogger = null;
   private final transient Logger logger;

   public AvalonLogger(Logger logger) {
      this.logger = logger;
   }

   public AvalonLogger(String name) {
      if(defaultLogger == null) {
         throw new NullPointerException("default logger has to be specified if this constructor is used!");
      } else {
         this.logger = defaultLogger.getChildLogger(name);
      }
   }

   public Logger getLogger() {
      return this.logger;
   }

   public static void setDefaultLogger(Logger logger) {
      defaultLogger = logger;
   }

   public void debug(Object message, Throwable t) {
      if(this.getLogger().isDebugEnabled()) {
         this.getLogger().debug(String.valueOf(message), t);
      }

   }

   public void debug(Object message) {
      if(this.getLogger().isDebugEnabled()) {
         this.getLogger().debug(String.valueOf(message));
      }

   }

   public void error(Object message, Throwable t) {
      if(this.getLogger().isErrorEnabled()) {
         this.getLogger().error(String.valueOf(message), t);
      }

   }

   public void error(Object message) {
      if(this.getLogger().isErrorEnabled()) {
         this.getLogger().error(String.valueOf(message));
      }

   }

   public void fatal(Object message, Throwable t) {
      if(this.getLogger().isFatalErrorEnabled()) {
         this.getLogger().fatalError(String.valueOf(message), t);
      }

   }

   public void fatal(Object message) {
      if(this.getLogger().isFatalErrorEnabled()) {
         this.getLogger().fatalError(String.valueOf(message));
      }

   }

   public void info(Object message, Throwable t) {
      if(this.getLogger().isInfoEnabled()) {
         this.getLogger().info(String.valueOf(message), t);
      }

   }

   public void info(Object message) {
      if(this.getLogger().isInfoEnabled()) {
         this.getLogger().info(String.valueOf(message));
      }

   }

   public boolean isDebugEnabled() {
      return this.getLogger().isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.getLogger().isErrorEnabled();
   }

   public boolean isFatalEnabled() {
      return this.getLogger().isFatalErrorEnabled();
   }

   public boolean isInfoEnabled() {
      return this.getLogger().isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return this.getLogger().isDebugEnabled();
   }

   public boolean isWarnEnabled() {
      return this.getLogger().isWarnEnabled();
   }

   public void trace(Object message, Throwable t) {
      if(this.getLogger().isDebugEnabled()) {
         this.getLogger().debug(String.valueOf(message), t);
      }

   }

   public void trace(Object message) {
      if(this.getLogger().isDebugEnabled()) {
         this.getLogger().debug(String.valueOf(message));
      }

   }

   public void warn(Object message, Throwable t) {
      if(this.getLogger().isWarnEnabled()) {
         this.getLogger().warn(String.valueOf(message), t);
      }

   }

   public void warn(Object message) {
      if(this.getLogger().isWarnEnabled()) {
         this.getLogger().warn(String.valueOf(message));
      }

   }
}
