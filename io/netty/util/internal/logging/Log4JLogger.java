package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

class Log4JLogger extends AbstractInternalLogger {
   private static final long serialVersionUID = 2851357342488183058L;
   final transient Logger logger;
   static final String FQCN = Log4JLogger.class.getName();
   final boolean traceCapable;

   Log4JLogger(Logger logger) {
      super(logger.getName());
      this.logger = logger;
      this.traceCapable = this.isTraceCapable();
   }

   private boolean isTraceCapable() {
      try {
         this.logger.isTraceEnabled();
         return true;
      } catch (NoSuchMethodError var2) {
         return false;
      }
   }

   public boolean isTraceEnabled() {
      return this.traceCapable?this.logger.isTraceEnabled():this.logger.isDebugEnabled();
   }

   public void trace(String msg) {
      this.logger.log(FQCN, this.traceCapable?Level.TRACE:Level.DEBUG, msg, (Throwable)null);
   }

   public void trace(String format, Object arg) {
      if(this.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.log(FQCN, this.traceCapable?Level.TRACE:Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String format, Object argA, Object argB) {
      if(this.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.log(FQCN, this.traceCapable?Level.TRACE:Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String format, Object... arguments) {
      if(this.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.log(FQCN, this.traceCapable?Level.TRACE:Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String msg, Throwable t) {
      this.logger.log(FQCN, this.traceCapable?Level.TRACE:Level.DEBUG, msg, t);
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String msg) {
      this.logger.log(FQCN, Level.DEBUG, msg, (Throwable)null);
   }

   public void debug(String format, Object arg) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String format, Object argA, Object argB) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String format, Object... arguments) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.log(FQCN, Level.DEBUG, ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String msg, Throwable t) {
      this.logger.log(FQCN, Level.DEBUG, msg, t);
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String msg) {
      this.logger.log(FQCN, Level.INFO, msg, (Throwable)null);
   }

   public void info(String format, Object arg) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String format, Object argA, Object argB) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String format, Object... argArray) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
         this.logger.log(FQCN, Level.INFO, ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String msg, Throwable t) {
      this.logger.log(FQCN, Level.INFO, msg, t);
   }

   public boolean isWarnEnabled() {
      return this.logger.isEnabledFor(Level.WARN);
   }

   public void warn(String msg) {
      this.logger.log(FQCN, Level.WARN, msg, (Throwable)null);
   }

   public void warn(String format, Object arg) {
      if(this.logger.isEnabledFor(Level.WARN)) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String format, Object argA, Object argB) {
      if(this.logger.isEnabledFor(Level.WARN)) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String format, Object... argArray) {
      if(this.logger.isEnabledFor(Level.WARN)) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
         this.logger.log(FQCN, Level.WARN, ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String msg, Throwable t) {
      this.logger.log(FQCN, Level.WARN, msg, t);
   }

   public boolean isErrorEnabled() {
      return this.logger.isEnabledFor(Level.ERROR);
   }

   public void error(String msg) {
      this.logger.log(FQCN, Level.ERROR, msg, (Throwable)null);
   }

   public void error(String format, Object arg) {
      if(this.logger.isEnabledFor(Level.ERROR)) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String format, Object argA, Object argB) {
      if(this.logger.isEnabledFor(Level.ERROR)) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String format, Object... argArray) {
      if(this.logger.isEnabledFor(Level.ERROR)) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, argArray);
         this.logger.log(FQCN, Level.ERROR, ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String msg, Throwable t) {
      this.logger.log(FQCN, Level.ERROR, msg, t);
   }
}
