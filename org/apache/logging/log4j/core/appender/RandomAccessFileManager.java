package org.apache.logging.log4j.core.appender;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

public class RandomAccessFileManager extends OutputStreamManager {
   static final int DEFAULT_BUFFER_SIZE = 262144;
   private static final RandomAccessFileManager.RandomAccessFileManagerFactory FACTORY = new RandomAccessFileManager.RandomAccessFileManagerFactory();
   private final boolean isImmediateFlush;
   private final String advertiseURI;
   private final RandomAccessFile randomAccessFile;
   private final ByteBuffer buffer;
   private final ThreadLocal isEndOfBatch = new ThreadLocal();

   protected RandomAccessFileManager(RandomAccessFile file, String fileName, OutputStream os, boolean immediateFlush, String advertiseURI, Layout layout) {
      super(os, fileName, layout);
      this.isImmediateFlush = immediateFlush;
      this.randomAccessFile = file;
      this.advertiseURI = advertiseURI;
      this.isEndOfBatch.set(Boolean.FALSE);
      this.buffer = ByteBuffer.allocate(262144);
   }

   public static RandomAccessFileManager getFileManager(String fileName, boolean append, boolean isFlush, String advertiseURI, Layout layout) {
      return (RandomAccessFileManager)getManager(fileName, new RandomAccessFileManager.FactoryData(append, isFlush, advertiseURI, layout), FACTORY);
   }

   public Boolean isEndOfBatch() {
      return (Boolean)this.isEndOfBatch.get();
   }

   public void setEndOfBatch(boolean isEndOfBatch) {
      this.isEndOfBatch.set(Boolean.valueOf(isEndOfBatch));
   }

   protected synchronized void write(byte[] bytes, int offset, int length) {
      super.write(bytes, offset, length);
      int chunk = 0;

      while(true) {
         if(length > this.buffer.remaining()) {
            this.flush();
         }

         chunk = Math.min(length, this.buffer.remaining());
         this.buffer.put(bytes, offset, chunk);
         offset += chunk;
         length -= chunk;
         if(length <= 0) {
            break;
         }
      }

      if(this.isImmediateFlush || this.isEndOfBatch.get() == Boolean.TRUE) {
         this.flush();
      }

   }

   public synchronized void flush() {
      this.buffer.flip();

      try {
         this.randomAccessFile.write(this.buffer.array(), 0, this.buffer.limit());
      } catch (IOException var3) {
         String msg = "Error writing to RandomAccessFile " + this.getName();
         throw new AppenderLoggingException(msg, var3);
      }

      this.buffer.clear();
   }

   public synchronized void close() {
      this.flush();

      try {
         this.randomAccessFile.close();
      } catch (IOException var2) {
         LOGGER.error("Unable to close RandomAccessFile " + this.getName() + ". " + var2);
      }

   }

   public String getFileName() {
      return this.getName();
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap(super.getContentFormat());
      result.put("fileURI", this.advertiseURI);
      return result;
   }

   static class DummyOutputStream extends OutputStream {
      public void write(int b) throws IOException {
      }

      public void write(byte[] b, int off, int len) throws IOException {
      }
   }

   private static class FactoryData {
      private final boolean append;
      private final boolean immediateFlush;
      private final String advertiseURI;
      private final Layout layout;

      public FactoryData(boolean append, boolean immediateFlush, String advertiseURI, Layout layout) {
         this.append = append;
         this.immediateFlush = immediateFlush;
         this.advertiseURI = advertiseURI;
         this.layout = layout;
      }
   }

   private static class RandomAccessFileManagerFactory implements ManagerFactory {
      private RandomAccessFileManagerFactory() {
      }

      public RandomAccessFileManager createManager(String name, RandomAccessFileManager.FactoryData data) {
         File file = new File(name);
         File parent = file.getParentFile();
         if(null != parent && !parent.exists()) {
            parent.mkdirs();
         }

         if(!data.append) {
            file.delete();
         }

         OutputStream os = new RandomAccessFileManager.DummyOutputStream();

         try {
            RandomAccessFile raf = new RandomAccessFile(name, "rw");
            if(data.append) {
               raf.seek(raf.length());
            } else {
               raf.setLength(0L);
            }

            return new RandomAccessFileManager(raf, name, os, data.immediateFlush, data.advertiseURI, data.layout);
         } catch (Exception var8) {
            AbstractManager.LOGGER.error("RandomAccessFileManager (" + name + ") " + var8);
            return null;
         }
      }
   }
}
