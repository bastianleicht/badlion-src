package org.apache.commons.logging.impl;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

public class Log4JLogger implements Log, Serializable {
   private static final long serialVersionUID = 5160705895411730424L;
   private static final String FQCN;
   private transient volatile Logger logger = null;
   private final String name;
   private static final Priority traceLevel;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$impl$Log4JLogger;
   // $FF: synthetic field
   static Class class$org$apache$log4j$Level;
   // $FF: synthetic field
   static Class class$org$apache$log4j$Priority;

   public Log4JLogger() {
      this.name = null;
   }

   public Log4JLogger(String name) {
      this.name = name;
      this.logger = this.getLogger();
   }

   public Log4JLogger(Logger logger) {
      if(logger == null) {
         throw new IllegalArgumentException("Warning - null logger in constructor; possible log4j misconfiguration.");
      } else {
         this.name = logger.getName();
         this.logger = logger;
      }
   }

   public void trace(Object message) {
      this.getLogger().log(FQCN, traceLevel, message, (Throwable)null);
   }

   public void trace(Object message, Throwable t) {
      this.getLogger().log(FQCN, traceLevel, message, t);
   }

   public void debug(Object message) {
      this.getLogger().log(FQCN, Level.DEBUG, message, (Throwable)null);
   }

   public void debug(Object message, Throwable t) {
      this.getLogger().log(FQCN, Level.DEBUG, message, t);
   }

   public void info(Object message) {
      this.getLogger().log(FQCN, Level.INFO, message, (Throwable)null);
   }

   public void info(Object message, Throwable t) {
      this.getLogger().log(FQCN, Level.INFO, message, t);
   }

   public void warn(Object message) {
      this.getLogger().log(FQCN, Level.WARN, message, (Throwable)null);
   }

   public void warn(Object message, Throwable t) {
      this.getLogger().log(FQCN, Level.WARN, message, t);
   }

   public void error(Object message) {
      this.getLogger().log(FQCN, Level.ERROR, message, (Throwable)null);
   }

   public void error(Object message, Throwable t) {
      this.getLogger().log(FQCN, Level.ERROR, message, t);
   }

   public void fatal(Object message) {
      this.getLogger().log(FQCN, Level.FATAL, message, (Throwable)null);
   }

   public void fatal(Object message, Throwable t) {
      this.getLogger().log(FQCN, Level.FATAL, message, t);
   }

   public Logger getLogger() {
      Logger result = this.logger;
      if(result == null) {
         synchronized(this) {
            result = this.logger;
            if(result == null) {
               this.logger = result = Logger.getLogger(this.name);
            }
         }
      }

      return result;
   }

   public boolean isDebugEnabled() {
      return this.getLogger().isDebugEnabled();
   }

   public boolean isErrorEnabled() {
      return this.getLogger().isEnabledFor(Level.ERROR);
   }

   public boolean isFatalEnabled() {
      return this.getLogger().isEnabledFor(Level.FATAL);
   }

   public boolean isInfoEnabled() {
      return this.getLogger().isInfoEnabled();
   }

   public boolean isTraceEnabled() {
      return this.getLogger().isEnabledFor(traceLevel);
   }

   public boolean isWarnEnabled() {
      return this.getLogger().isEnabledFor(Level.WARN);
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   static {
      FQCN = (class$org$apache$commons$logging$impl$Log4JLogger == null?(class$org$apache$commons$logging$impl$Log4JLogger = class$("org.apache.commons.logging.impl.Log4JLogger")):class$org$apache$commons$logging$impl$Log4JLogger).getName();
      if(!(class$org$apache$log4j$Priority == null?(class$org$apache$log4j$Priority = class$("org.apache.log4j.Priority")):class$org$apache$log4j$Priority).isAssignableFrom(class$org$apache$log4j$Level == null?(class$org$apache$log4j$Level = class$("org.apache.log4j.Level")):class$org$apache$log4j$Level)) {
         throw new InstantiationError("Log4J 1.2 not available");
      } else {
         Priority _traceLevel;
         try {
            _traceLevel = (Priority)(class$org$apache$log4j$Level == null?(class$org$apache$log4j$Level = class$("org.apache.log4j.Level")):class$org$apache$log4j$Level).getDeclaredField("TRACE").get((Object)null);
         } catch (Exception var2) {
            _traceLevel = Level.DEBUG;
         }

         traceLevel = _traceLevel;
      }
   }
}
