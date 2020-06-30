package org.apache.logging.log4j.core.appender;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.ErrorHandler;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.status.StatusLogger;

public class DefaultErrorHandler implements ErrorHandler {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final int MAX_EXCEPTIONS = 3;
   private static final int EXCEPTION_INTERVAL = 300000;
   private int exceptionCount = 0;
   private long lastException;
   private final Appender appender;

   public DefaultErrorHandler(Appender appender) {
      this.appender = appender;
   }

   public void error(String msg) {
      long current = System.currentTimeMillis();
      if(this.lastException + 300000L < current || this.exceptionCount++ < 3) {
         LOGGER.error(msg);
      }

      this.lastException = current;
   }

   public void error(String msg, Throwable t) {
      long current = System.currentTimeMillis();
      if(this.lastException + 300000L < current || this.exceptionCount++ < 3) {
         LOGGER.error(msg, t);
      }

      this.lastException = current;
      if(!this.appender.ignoreExceptions() && t != null && !(t instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(msg, t);
      }
   }

   public void error(String msg, LogEvent event, Throwable t) {
      long current = System.currentTimeMillis();
      if(this.lastException + 300000L < current || this.exceptionCount++ < 3) {
         LOGGER.error(msg, t);
      }

      this.lastException = current;
      if(!this.appender.ignoreExceptions() && t != null && !(t instanceof AppenderLoggingException)) {
         throw new AppenderLoggingException(msg, t);
      }
   }
}
