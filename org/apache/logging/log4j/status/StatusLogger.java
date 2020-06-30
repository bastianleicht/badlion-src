package org.apache.logging.log4j.status;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.simple.SimpleLogger;
import org.apache.logging.log4j.spi.AbstractLogger;
import org.apache.logging.log4j.status.StatusData;
import org.apache.logging.log4j.status.StatusListener;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class StatusLogger extends AbstractLogger {
   public static final String MAX_STATUS_ENTRIES = "log4j2.status.entries";
   private static final String NOT_AVAIL = "?";
   private static final PropertiesUtil PROPS = new PropertiesUtil("log4j2.StatusLogger.properties");
   private static final int MAX_ENTRIES = PROPS.getIntegerProperty("log4j2.status.entries", 200);
   private static final String DEFAULT_STATUS_LEVEL = PROPS.getStringProperty("log4j2.StatusLogger.level");
   private static final StatusLogger STATUS_LOGGER = new StatusLogger();
   private final SimpleLogger logger;
   private final CopyOnWriteArrayList listeners = new CopyOnWriteArrayList();
   private final ReentrantReadWriteLock listenersLock = new ReentrantReadWriteLock();
   private final Queue messages;
   private final ReentrantLock msgLock;
   private int listenersLevel;

   private StatusLogger() {
      this.messages = new StatusLogger.BoundedQueue(MAX_ENTRIES);
      this.msgLock = new ReentrantLock();
      this.logger = new SimpleLogger("StatusLogger", Level.ERROR, false, true, false, false, "", (MessageFactory)null, PROPS, System.err);
      this.listenersLevel = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();
   }

   public static StatusLogger getLogger() {
      return STATUS_LOGGER;
   }

   public Level getLevel() {
      return this.logger.getLevel();
   }

   public void setLevel(Level level) {
      this.logger.setLevel(level);
   }

   public void registerListener(StatusListener listener) {
      this.listenersLock.writeLock().lock();

      try {
         this.listeners.add(listener);
         Level lvl = listener.getStatusLevel();
         if(this.listenersLevel < lvl.intLevel()) {
            this.listenersLevel = lvl.intLevel();
         }
      } finally {
         this.listenersLock.writeLock().unlock();
      }

   }

   public void removeListener(StatusListener listener) {
      this.listenersLock.writeLock().lock();

      try {
         this.listeners.remove(listener);
         int lowest = Level.toLevel(DEFAULT_STATUS_LEVEL, Level.WARN).intLevel();

         for(StatusListener l : this.listeners) {
            int level = l.getStatusLevel().intLevel();
            if(lowest < level) {
               lowest = level;
            }
         }

         this.listenersLevel = lowest;
      } finally {
         this.listenersLock.writeLock().unlock();
      }

   }

   public Iterator getListeners() {
      return this.listeners.iterator();
   }

   public void reset() {
      this.listeners.clear();
      this.clear();
   }

   public List getStatusData() {
      this.msgLock.lock();

      ArrayList var1;
      try {
         var1 = new ArrayList(this.messages);
      } finally {
         this.msgLock.unlock();
      }

      return var1;
   }

   public void clear() {
      this.msgLock.lock();

      try {
         this.messages.clear();
      } finally {
         this.msgLock.unlock();
      }

   }

   public void log(Marker marker, String fqcn, Level level, Message msg, Throwable t) {
      StackTraceElement element = null;
      if(fqcn != null) {
         element = this.getStackTraceElement(fqcn, Thread.currentThread().getStackTrace());
      }

      StatusData data = new StatusData(element, level, msg, t);
      this.msgLock.lock();

      try {
         this.messages.add(data);
      } finally {
         this.msgLock.unlock();
      }

      if(this.listeners.size() > 0) {
         for(StatusListener listener : this.listeners) {
            if(data.getLevel().isAtLeastAsSpecificAs(listener.getStatusLevel())) {
               listener.log(data);
            }
         }
      } else {
         this.logger.log(marker, fqcn, level, msg, t);
      }

   }

   private StackTraceElement getStackTraceElement(String fqcn, StackTraceElement[] stackTrace) {
      if(fqcn == null) {
         return null;
      } else {
         boolean next = false;

         for(StackTraceElement element : stackTrace) {
            if(next) {
               return element;
            }

            String className = element.getClassName();
            if(fqcn.equals(className)) {
               next = true;
            } else if("?".equals(className)) {
               break;
            }
         }

         return null;
      }
   }

   protected boolean isEnabled(Level level, Marker marker, String data) {
      return this.isEnabled(level, marker);
   }

   protected boolean isEnabled(Level level, Marker marker, String data, Throwable t) {
      return this.isEnabled(level, marker);
   }

   protected boolean isEnabled(Level level, Marker marker, String data, Object... p1) {
      return this.isEnabled(level, marker);
   }

   protected boolean isEnabled(Level level, Marker marker, Object data, Throwable t) {
      return this.isEnabled(level, marker);
   }

   protected boolean isEnabled(Level level, Marker marker, Message data, Throwable t) {
      return this.isEnabled(level, marker);
   }

   public boolean isEnabled(Level level, Marker marker) {
      if(this.listeners.size() > 0) {
         return this.listenersLevel >= level.intLevel();
      } else {
         switch(level) {
         case FATAL:
            return this.logger.isFatalEnabled(marker);
         case TRACE:
            return this.logger.isTraceEnabled(marker);
         case DEBUG:
            return this.logger.isDebugEnabled(marker);
         case INFO:
            return this.logger.isInfoEnabled(marker);
         case WARN:
            return this.logger.isWarnEnabled(marker);
         case ERROR:
            return this.logger.isErrorEnabled(marker);
         default:
            return false;
         }
      }
   }

   private class BoundedQueue extends ConcurrentLinkedQueue {
      private static final long serialVersionUID = -3945953719763255337L;
      private final int size;

      public BoundedQueue(int size) {
         this.size = size;
      }

      public boolean add(Object object) {
         while(StatusLogger.this.messages.size() > this.size) {
            StatusLogger.this.messages.poll();
         }

         return super.add(object);
      }
   }
}
