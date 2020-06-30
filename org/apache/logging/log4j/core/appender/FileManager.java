package org.apache.logging.log4j.core.appender;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

public class FileManager extends OutputStreamManager {
   private static final FileManager.FileManagerFactory FACTORY = new FileManager.FileManagerFactory();
   private final boolean isAppend;
   private final boolean isLocking;
   private final String advertiseURI;

   protected FileManager(String fileName, OutputStream os, boolean append, boolean locking, String advertiseURI, Layout layout) {
      super(os, fileName, layout);
      this.isAppend = append;
      this.isLocking = locking;
      this.advertiseURI = advertiseURI;
   }

   public static FileManager getFileManager(String fileName, boolean append, boolean locking, boolean bufferedIO, String advertiseURI, Layout layout) {
      if(locking && bufferedIO) {
         locking = false;
      }

      return (FileManager)getManager(fileName, new FileManager.FactoryData(append, locking, bufferedIO, advertiseURI, layout), FACTORY);
   }

   protected synchronized void write(byte[] bytes, int offset, int length) {
      if(this.isLocking) {
         FileChannel channel = ((FileOutputStream)this.getOutputStream()).getChannel();

         try {
            FileLock lock = channel.lock(0L, Long.MAX_VALUE, false);

            try {
               super.write(bytes, offset, length);
            } finally {
               lock.release();
            }
         } catch (IOException var10) {
            throw new AppenderLoggingException("Unable to obtain lock on " + this.getName(), var10);
         }
      } else {
         super.write(bytes, offset, length);
      }

   }

   public String getFileName() {
      return this.getName();
   }

   public boolean isAppend() {
      return this.isAppend;
   }

   public boolean isLocking() {
      return this.isLocking;
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap(super.getContentFormat());
      result.put("fileURI", this.advertiseURI);
      return result;
   }

   private static class FactoryData {
      private final boolean append;
      private final boolean locking;
      private final boolean bufferedIO;
      private final String advertiseURI;
      private final Layout layout;

      public FactoryData(boolean append, boolean locking, boolean bufferedIO, String advertiseURI, Layout layout) {
         this.append = append;
         this.locking = locking;
         this.bufferedIO = bufferedIO;
         this.advertiseURI = advertiseURI;
         this.layout = layout;
      }
   }

   private static class FileManagerFactory implements ManagerFactory {
      private FileManagerFactory() {
      }

      public FileManager createManager(String name, FileManager.FactoryData data) {
         File file = new File(name);
         File parent = file.getParentFile();
         if(null != parent && !parent.exists()) {
            parent.mkdirs();
         }

         try {
            OutputStream os = new FileOutputStream(name, data.append);
            if(data.bufferedIO) {
               os = new BufferedOutputStream(os);
            }

            return new FileManager(name, os, data.append, data.locking, data.advertiseURI, data.layout);
         } catch (FileNotFoundException var7) {
            AbstractManager.LOGGER.error("FileManager (" + name + ") " + var7);
            return null;
         }
      }
   }
}
