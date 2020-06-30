package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import io.netty.util.internal.logging.FormattingTuple;
import io.netty.util.internal.logging.MessageFormatter;
import org.apache.commons.logging.Log;

class CommonsLogger extends AbstractInternalLogger {
   private static final long serialVersionUID = 8647838678388394885L;
   private final transient Log logger;

   CommonsLogger(Log logger, String name) {
      super(name);
      if(logger == null) {
         throw new NullPointerException("logger");
      } else {
         this.logger = logger;
      }
   }

   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   public void trace(String msg) {
      this.logger.trace(msg);
   }

   public void trace(String format, Object arg) {
      if(this.logger.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.trace(ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String format, Object argA, Object argB) {
      if(this.logger.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.trace(ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String format, Object... arguments) {
      if(this.logger.isTraceEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.trace(ft.getMessage(), ft.getThrowable());
      }

   }

   public void trace(String msg, Throwable t) {
      this.logger.trace(msg, t);
   }

   public boolean isDebugEnabled() {
      return this.logger.isDebugEnabled();
   }

   public void debug(String msg) {
      this.logger.debug(msg);
   }

   public void debug(String format, Object arg) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.debug(ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String format, Object argA, Object argB) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.debug(ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String format, Object... arguments) {
      if(this.logger.isDebugEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.debug(ft.getMessage(), ft.getThrowable());
      }

   }

   public void debug(String msg, Throwable t) {
      this.logger.debug(msg, t);
   }

   public boolean isInfoEnabled() {
      return this.logger.isInfoEnabled();
   }

   public void info(String msg) {
      this.logger.info(msg);
   }

   public void info(String format, Object arg) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.info(ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String format, Object argA, Object argB) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.info(ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String format, Object... arguments) {
      if(this.logger.isInfoEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.info(ft.getMessage(), ft.getThrowable());
      }

   }

   public void info(String msg, Throwable t) {
      this.logger.info(msg, t);
   }

   public boolean isWarnEnabled() {
      return this.logger.isWarnEnabled();
   }

   public void warn(String msg) {
      this.logger.warn(msg);
   }

   public void warn(String format, Object arg) {
      if(this.logger.isWarnEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.warn(ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String format, Object argA, Object argB) {
      if(this.logger.isWarnEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.warn(ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String format, Object... arguments) {
      if(this.logger.isWarnEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.warn(ft.getMessage(), ft.getThrowable());
      }

   }

   public void warn(String msg, Throwable t) {
      this.logger.warn(msg, t);
   }

   public boolean isErrorEnabled() {
      return this.logger.isErrorEnabled();
   }

   public void error(String msg) {
      this.logger.error(msg);
   }

   public void error(String format, Object arg) {
      if(this.logger.isErrorEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, arg);
         this.logger.error(ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String format, Object argA, Object argB) {
      if(this.logger.isErrorEnabled()) {
         FormattingTuple ft = MessageFormatter.format(format, argA, argB);
         this.logger.error(ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String format, Object... arguments) {
      if(this.logger.isErrorEnabled()) {
         FormattingTuple ft = MessageFormatter.arrayFormat(format, arguments);
         this.logger.error(ft.getMessage(), ft.getThrowable());
      }

   }

   public void error(String msg, Throwable t) {
      this.logger.error(msg, t);
   }
}
