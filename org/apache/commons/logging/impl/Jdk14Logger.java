package org.apache.commons.logging.impl;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;

public class Jdk14Logger implements Log, Serializable {
   private static final long serialVersionUID = 4784713551416303804L;
   protected static final Level dummyLevel = Level.FINE;
   protected transient Logger logger = null;
   protected String name = null;

   public Jdk14Logger(String name) {
      this.name = name;
      this.logger = this.getLogger();
   }

   protected void log(Level level, String msg, Throwable ex) {
      Logger logger = this.getLogger();
      if(logger.isLoggable(level)) {
         Throwable dummyException = new Throwable();
         StackTraceElement[] locations = dummyException.getStackTrace();
         String cname = this.name;
         String method = "unknown";
         if(locations != null && locations.length > 2) {
            StackTraceElement caller = locations[2];
            method = caller.getMethodName();
         }

         if(ex == null) {
            logger.logp(level, cname, method, msg);
         } else {
            logger.logp(level, cname, method, msg, ex);
         }
      }

   }

   public void debug(Object message) {
      this.log(Level.FINE, String.valueOf(message), (Throwable)null);
   }

   public void debug(Object message, Throwable exception) {
      this.log(Level.FINE, String.valueOf(message), exception);
   }

   public void error(Object message) {
      this.log(Level.SEVERE, String.valueOf(message), (Throwable)null);
   }

   public void error(Object message, Throwable exception) {
      this.log(Level.SEVERE, String.valueOf(message), exception);
   }

   public void fatal(Object message) {
      this.log(Level.SEVERE, String.valueOf(message), (Throwable)null);
   }

   public void fatal(Object message, Throwable exception) {
      this.log(Level.SEVERE, String.valueOf(message), exception);
   }

   public Logger getLogger() {
      if(this.logger == null) {
         this.logger = Logger.getLogger(this.name);
      }

      return this.logger;
   }

   public void info(Object message) {
      this.log(Level.INFO, String.valueOf(message), (Throwable)null);
   }

   public void info(Object message, Throwable exception) {
      this.log(Level.INFO, String.valueOf(message), exception);
   }

   public boolean isDebugEnabled() {
      return this.getLogger().isLoggable(Level.FINE);
   }

   public boolean isErrorEnabled() {
      return this.getLogger().isLoggable(Level.SEVERE);
   }

   public boolean isFatalEnabled() {
      return this.getLogger().isLoggable(Level.SEVERE);
   }

   public boolean isInfoEnabled() {
      return this.getLogger().isLoggable(Level.INFO);
   }

   public boolean isTraceEnabled() {
      return this.getLogger().isLoggable(Level.FINEST);
   }

   public boolean isWarnEnabled() {
      return this.getLogger().isLoggable(Level.WARNING);
   }

   public void trace(Object message) {
      this.log(Level.FINEST, String.valueOf(message), (Throwable)null);
   }

   public void trace(Object message, Throwable exception) {
      this.log(Level.FINEST, String.valueOf(message), exception);
   }

   public void warn(Object message) {
      this.log(Level.WARNING, String.valueOf(message), (Throwable)null);
   }

   public void warn(Object message, Throwable exception) {
      this.log(Level.WARNING, String.valueOf(message), exception);
   }
}
