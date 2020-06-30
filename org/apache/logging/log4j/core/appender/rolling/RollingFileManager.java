package org.apache.logging.log4j.core.appender.rolling;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Semaphore;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.FileManager;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.rolling.PatternProcessor;
import org.apache.logging.log4j.core.appender.rolling.RolloverDescription;
import org.apache.logging.log4j.core.appender.rolling.RolloverStrategy;
import org.apache.logging.log4j.core.appender.rolling.TriggeringPolicy;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;
import org.apache.logging.log4j.core.appender.rolling.helper.Action;

public class RollingFileManager extends FileManager {
   private static RollingFileManager.RollingFileManagerFactory factory = new RollingFileManager.RollingFileManagerFactory();
   private long size;
   private long initialTime;
   private final PatternProcessor patternProcessor;
   private final Semaphore semaphore = new Semaphore(1);
   private final TriggeringPolicy policy;
   private final RolloverStrategy strategy;

   protected RollingFileManager(String fileName, String pattern, OutputStream os, boolean append, long size, long time, TriggeringPolicy policy, RolloverStrategy strategy, String advertiseURI, Layout layout) {
      super(fileName, os, append, false, advertiseURI, layout);
      this.size = size;
      this.initialTime = time;
      this.policy = policy;
      this.strategy = strategy;
      this.patternProcessor = new PatternProcessor(pattern);
      policy.initialize(this);
   }

   public static RollingFileManager getFileManager(String fileName, String pattern, boolean append, boolean bufferedIO, TriggeringPolicy policy, RolloverStrategy strategy, String advertiseURI, Layout layout) {
      return (RollingFileManager)getManager(fileName, new RollingFileManager.FactoryData(pattern, append, bufferedIO, policy, strategy, advertiseURI, layout), factory);
   }

   protected synchronized void write(byte[] bytes, int offset, int length) {
      this.size += (long)length;
      super.write(bytes, offset, length);
   }

   public long getFileSize() {
      return this.size;
   }

   public long getFileTime() {
      return this.initialTime;
   }

   public synchronized void checkRollover(LogEvent event) {
      if(this.policy.isTriggeringEvent(event) && this.rollover(this.strategy)) {
         try {
            this.size = 0L;
            this.initialTime = System.currentTimeMillis();
            this.createFileAfterRollover();
         } catch (IOException var3) {
            LOGGER.error("FileManager (" + this.getFileName() + ") " + var3);
         }
      }

   }

   protected void createFileAfterRollover() throws IOException {
      OutputStream os = new FileOutputStream(this.getFileName(), this.isAppend());
      this.setOutputStream(os);
   }

   public PatternProcessor getPatternProcessor() {
      return this.patternProcessor;
   }

   private boolean rollover(RolloverStrategy strategy) {
      try {
         this.semaphore.acquire();
      } catch (InterruptedException var11) {
         LOGGER.error((String)"Thread interrupted while attempting to check rollover", (Throwable)var11);
         return false;
      }

      boolean success = false;
      Thread thread = null;

      boolean ex;
      try {
         RolloverDescription descriptor = strategy.rollover(this);
         if(descriptor == null) {
            ex = false;
            return ex;
         }

         this.close();
         if(descriptor.getSynchronous() != null) {
            try {
               success = descriptor.getSynchronous().execute();
            } catch (Exception var10) {
               LOGGER.error((String)"Error in synchronous task", (Throwable)var10);
            }
         }

         if(success && descriptor.getAsynchronous() != null) {
            thread = new Thread(new RollingFileManager.AsyncAction(descriptor.getAsynchronous(), this));
            thread.start();
         }

         ex = true;
      } finally {
         if(thread == null) {
            this.semaphore.release();
         }

      }

      return ex;
   }

   private static class AsyncAction extends AbstractAction {
      private final Action action;
      private final RollingFileManager manager;

      public AsyncAction(Action act, RollingFileManager manager) {
         this.action = act;
         this.manager = manager;
      }

      public boolean execute() throws IOException {
         boolean var1;
         try {
            var1 = this.action.execute();
         } finally {
            this.manager.semaphore.release();
         }

         return var1;
      }

      public void close() {
         this.action.close();
      }

      public boolean isComplete() {
         return this.action.isComplete();
      }
   }

   private static class FactoryData {
      private final String pattern;
      private final boolean append;
      private final boolean bufferedIO;
      private final TriggeringPolicy policy;
      private final RolloverStrategy strategy;
      private final String advertiseURI;
      private final Layout layout;

      public FactoryData(String pattern, boolean append, boolean bufferedIO, TriggeringPolicy policy, RolloverStrategy strategy, String advertiseURI, Layout layout) {
         this.pattern = pattern;
         this.append = append;
         this.bufferedIO = bufferedIO;
         this.policy = policy;
         this.strategy = strategy;
         this.advertiseURI = advertiseURI;
         this.layout = layout;
      }
   }

   private static class RollingFileManagerFactory implements ManagerFactory {
      private RollingFileManagerFactory() {
      }

      public RollingFileManager createManager(String name, RollingFileManager.FactoryData data) {
         File file = new File(name);
         File parent = file.getParentFile();
         if(null != parent && !parent.exists()) {
            parent.mkdirs();
         }

         try {
            file.createNewFile();
         } catch (IOException var12) {
            RollingFileManager.LOGGER.error((String)("Unable to create file " + name), (Throwable)var12);
            return null;
         }

         long size = data.append?file.length():0L;
         long time = file.lastModified();

         try {
            OutputStream os = new FileOutputStream(name, data.append);
            if(data.bufferedIO) {
               os = new BufferedOutputStream(os);
            }

            return new RollingFileManager(name, data.pattern, os, data.append, size, time, data.policy, data.strategy, data.advertiseURI, data.layout);
         } catch (FileNotFoundException var11) {
            RollingFileManager.LOGGER.error("FileManager (" + name + ") " + var11);
            return null;
         }
      }
   }
}
