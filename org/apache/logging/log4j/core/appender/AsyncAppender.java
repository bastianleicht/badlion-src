package org.apache.logging.log4j.core.appender;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAliases;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginConfiguration;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.helpers.Booleans;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;

@Plugin(
   name = "Async",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public final class AsyncAppender extends AbstractAppender {
   private static final int DEFAULT_QUEUE_SIZE = 128;
   private static final String SHUTDOWN = "Shutdown";
   private final BlockingQueue queue;
   private final boolean blocking;
   private final Configuration config;
   private final AppenderRef[] appenderRefs;
   private final String errorRef;
   private final boolean includeLocation;
   private AppenderControl errorAppender;
   private AsyncAppender.AsyncThread thread;
   private static final AtomicLong threadSequence = new AtomicLong(1L);

   private AsyncAppender(String name, Filter filter, AppenderRef[] appenderRefs, String errorRef, int queueSize, boolean blocking, boolean ignoreExceptions, Configuration config, boolean includeLocation) {
      super(name, filter, (Layout)null, ignoreExceptions);
      this.queue = new ArrayBlockingQueue(queueSize);
      this.blocking = blocking;
      this.config = config;
      this.appenderRefs = appenderRefs;
      this.errorRef = errorRef;
      this.includeLocation = includeLocation;
   }

   public void start() {
      Map<String, Appender> map = this.config.getAppenders();
      List<AppenderControl> appenders = new ArrayList();

      for(AppenderRef appenderRef : this.appenderRefs) {
         if(map.containsKey(appenderRef.getRef())) {
            appenders.add(new AppenderControl((Appender)map.get(appenderRef.getRef()), appenderRef.getLevel(), appenderRef.getFilter()));
         } else {
            LOGGER.error("No appender named {} was configured", new Object[]{appenderRef});
         }
      }

      if(this.errorRef != null) {
         if(map.containsKey(this.errorRef)) {
            this.errorAppender = new AppenderControl((Appender)map.get(this.errorRef), (Level)null, (Filter)null);
         } else {
            LOGGER.error("Unable to set up error Appender. No appender named {} was configured", new Object[]{this.errorRef});
         }
      }

      if(appenders.size() > 0) {
         this.thread = new AsyncAppender.AsyncThread(appenders, this.queue);
         this.thread.setName("AsyncAppender-" + this.getName());
      } else if(this.errorRef == null) {
         throw new ConfigurationException("No appenders are available for AsyncAppender " + this.getName());
      }

      this.thread.start();
      super.start();
   }

   public void stop() {
      super.stop();
      this.thread.shutdown();

      try {
         this.thread.join();
      } catch (InterruptedException var2) {
         LOGGER.warn("Interrupted while stopping AsyncAppender {}", new Object[]{this.getName()});
      }

   }

   public void append(LogEvent event) {
      if(!this.isStarted()) {
         throw new IllegalStateException("AsyncAppender " + this.getName() + " is not active");
      } else {
         if(event instanceof Log4jLogEvent) {
            boolean appendSuccessful = false;
            if(this.blocking) {
               try {
                  this.queue.put(Log4jLogEvent.serialize((Log4jLogEvent)event, this.includeLocation));
                  appendSuccessful = true;
               } catch (InterruptedException var4) {
                  LOGGER.warn("Interrupted while waiting for a free slot in the AsyncAppender LogEvent-queue {}", new Object[]{this.getName()});
               }
            } else {
               appendSuccessful = this.queue.offer(Log4jLogEvent.serialize((Log4jLogEvent)event, this.includeLocation));
               if(!appendSuccessful) {
                  this.error("Appender " + this.getName() + " is unable to write primary appenders. queue is full");
               }
            }

            if(!appendSuccessful && this.errorAppender != null) {
               this.errorAppender.callAppender(event);
            }
         }

      }
   }

   @PluginFactory
   public static AsyncAppender createAppender(@PluginElement("AppenderRef") AppenderRef[] appenderRefs, @PluginAttribute("errorRef") @PluginAliases({"error-ref"}) String errorRef, @PluginAttribute("blocking") String blocking, @PluginAttribute("bufferSize") String size, @PluginAttribute("name") String name, @PluginAttribute("includeLocation") String includeLocation, @PluginElement("Filter") Filter filter, @PluginConfiguration Configuration config, @PluginAttribute("ignoreExceptions") String ignore) {
      if(name == null) {
         LOGGER.error("No name provided for AsyncAppender");
         return null;
      } else {
         if(appenderRefs == null) {
            LOGGER.error("No appender references provided to AsyncAppender {}", new Object[]{name});
         }

         boolean isBlocking = Booleans.parseBoolean(blocking, true);
         int queueSize = AbstractAppender.parseInt(size, 128);
         boolean isIncludeLocation = Boolean.parseBoolean(includeLocation);
         boolean ignoreExceptions = Booleans.parseBoolean(ignore, true);
         return new AsyncAppender(name, filter, appenderRefs, errorRef, queueSize, isBlocking, ignoreExceptions, config, isIncludeLocation);
      }
   }

   private class AsyncThread extends Thread {
      private volatile boolean shutdown = false;
      private final List appenders;
      private final BlockingQueue queue;

      public AsyncThread(List appenders, BlockingQueue queue) {
         this.appenders = appenders;
         this.queue = queue;
         this.setDaemon(true);
         this.setName("AsyncAppenderThread" + AsyncAppender.threadSequence.getAndIncrement());
      }

      public void run() {
         while(!this.shutdown) {
            Serializable s;
            try {
               s = (Serializable)this.queue.take();
               if(s != null && s instanceof String && "Shutdown".equals(s.toString())) {
                  this.shutdown = true;
                  continue;
               }
            } catch (InterruptedException var9) {
               continue;
            }

            Log4jLogEvent event = Log4jLogEvent.deserialize(s);
            event.setEndOfBatch(this.queue.isEmpty());
            boolean success = false;

            for(AppenderControl control : this.appenders) {
               try {
                  control.callAppender(event);
                  success = true;
               } catch (Exception var8) {
                  ;
               }
            }

            if(!success && AsyncAppender.this.errorAppender != null) {
               try {
                  AsyncAppender.this.errorAppender.callAppender(event);
               } catch (Exception var7) {
                  ;
               }
            }
         }

         while(!this.queue.isEmpty()) {
            try {
               Serializable s = (Serializable)this.queue.take();
               if(s instanceof Log4jLogEvent) {
                  Log4jLogEvent event = Log4jLogEvent.deserialize(s);
                  event.setEndOfBatch(this.queue.isEmpty());

                  for(AppenderControl control : this.appenders) {
                     control.callAppender(event);
                  }
               }
            } catch (InterruptedException var10) {
               ;
            }
         }

      }

      public void shutdown() {
         this.shutdown = true;
         if(this.queue.isEmpty()) {
            this.queue.offer("Shutdown");
         }

      }
   }
}
