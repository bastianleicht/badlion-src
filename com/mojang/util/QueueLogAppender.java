package com.mojang.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.pattern.RegexReplacement;

@Plugin(
   name = "Queue",
   category = "Core",
   elementType = "appender",
   printObject = true
)
public class QueueLogAppender extends AbstractAppender {
   private static final int MAX_CAPACITY = 250;
   private static final Map QUEUES = new HashMap();
   private static final ReadWriteLock QUEUE_LOCK = new ReentrantReadWriteLock();
   private final BlockingQueue queue;

   public QueueLogAppender(String name, Filter filter, Layout layout, boolean ignoreExceptions, BlockingQueue queue) {
      super(name, filter, layout, ignoreExceptions);
      this.queue = queue;
   }

   public void append(LogEvent event) {
      if(this.queue.size() >= 250) {
         this.queue.clear();
      }

      this.queue.add(this.getLayout().toSerializable(event).toString());
   }

   @PluginFactory
   public static QueueLogAppender createAppender(@PluginAttribute("name") String name, @PluginAttribute("ignoreExceptions") String ignore, @PluginElement("Layout") Layout layout, @PluginElement("Filters") Filter filter, @PluginAttribute("target") String target) {
      boolean ignoreExceptions = Boolean.parseBoolean(ignore);
      if(name == null) {
         LOGGER.error("No name provided for QueueLogAppender");
         return null;
      } else {
         if(target == null) {
            target = name;
         }

         QUEUE_LOCK.writeLock().lock();
         BlockingQueue<String> queue = (BlockingQueue)QUEUES.get(target);
         if(queue == null) {
            queue = new LinkedBlockingQueue();
            QUEUES.put(target, queue);
         }

         QUEUE_LOCK.writeLock().unlock();
         if(layout == null) {
            layout = PatternLayout.createLayout((String)null, (Configuration)null, (RegexReplacement)null, (String)null, (String)null);
         }

         return new QueueLogAppender(name, filter, (Layout)layout, ignoreExceptions, queue);
      }
   }

   public static String getNextLogEvent(String queueName) {
      QUEUE_LOCK.readLock().lock();
      BlockingQueue<String> queue = (BlockingQueue)QUEUES.get(queueName);
      QUEUE_LOCK.readLock().unlock();
      if(queue != null) {
         try {
            return (String)queue.take();
         } catch (InterruptedException var3) {
            ;
         }
      }

      return null;
   }
}
