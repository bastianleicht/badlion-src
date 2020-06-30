package io.netty.util.internal.logging;

import io.netty.util.internal.logging.AbstractInternalLogger;
import org.slf4j.Logger;

class Slf4JLogger extends AbstractInternalLogger {
   private static final long serialVersionUID = 108038972685130825L;
   private final transient Logger logger;

   Slf4JLogger(Logger logger) {
      super(logger.getName());
      this.logger = logger;
   }

   public boolean isTraceEnabled() {
      return this.logger.isTraceEnabled();
   }

   public void trace(String msg) {
      this.logger.trace(msg);
   }

   public void trace(String format, Object arg) {
      this.logger.trace(format, arg);
   }

   public void trace(String format, Object argA, Object argB) {
      this.logger.trace(format, argA, argB);
   }

   public void trace(String format, Object... argArray) {
      this.logger.trace(format, argArray);
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
      this.logger.debug(format, arg);
   }

   public void debug(String format, Object argA, Object argB) {
      this.logger.debug(format, argA, argB);
   }

   public void debug(String format, Object... argArray) {
      this.logger.debug(format, argArray);
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
      this.logger.info(format, arg);
   }

   public void info(String format, Object argA, Object argB) {
      this.logger.info(format, argA, argB);
   }

   public void info(String format, Object... argArray) {
      this.logger.info(format, argArray);
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
      this.logger.warn(format, arg);
   }

   public void warn(String format, Object... argArray) {
      this.logger.warn(format, argArray);
   }

   public void warn(String format, Object argA, Object argB) {
      this.logger.warn(format, argA, argB);
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
      this.logger.error(format, arg);
   }

   public void error(String format, Object argA, Object argB) {
      this.logger.error(format, argA, argB);
   }

   public void error(String format, Object... argArray) {
      this.logger.error(format, argArray);
   }

   public void error(String msg, Throwable t) {
      this.logger.error(msg, t);
   }
}
