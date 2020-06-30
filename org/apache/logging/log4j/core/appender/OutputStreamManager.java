package org.apache.logging.log4j.core.appender;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.AbstractManager;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.ManagerFactory;

public class OutputStreamManager extends AbstractManager {
   private volatile OutputStream os;
   private final byte[] footer;
   private final byte[] header;

   protected OutputStreamManager(OutputStream os, String streamName, Layout layout) {
      super(streamName);
      this.os = os;
      if(layout != null) {
         this.footer = layout.getFooter();
         this.header = layout.getHeader();
         if(this.header != null) {
            try {
               this.os.write(this.header, 0, this.header.length);
            } catch (IOException var5) {
               LOGGER.error((String)"Unable to write header", (Throwable)var5);
            }
         }
      } else {
         this.footer = null;
         this.header = null;
      }

   }

   public static OutputStreamManager getManager(String name, Object data, ManagerFactory factory) {
      return (OutputStreamManager)AbstractManager.getManager(name, factory, data);
   }

   public void releaseSub() {
      if(this.footer != null) {
         this.write(this.footer);
      }

      this.close();
   }

   public boolean isOpen() {
      return this.getCount() > 0;
   }

   protected OutputStream getOutputStream() {
      return this.os;
   }

   protected void setOutputStream(OutputStream os) {
      if(this.header != null) {
         try {
            os.write(this.header, 0, this.header.length);
            this.os = os;
         } catch (IOException var3) {
            LOGGER.error((String)"Unable to write header", (Throwable)var3);
         }
      } else {
         this.os = os;
      }

   }

   protected synchronized void write(byte[] bytes, int offset, int length) {
      try {
         this.os.write(bytes, offset, length);
      } catch (IOException var6) {
         String msg = "Error writing to stream " + this.getName();
         throw new AppenderLoggingException(msg, var6);
      }
   }

   protected void write(byte[] bytes) {
      this.write(bytes, 0, bytes.length);
   }

   protected synchronized void close() {
      OutputStream stream = this.os;
      if(stream != System.out && stream != System.err) {
         try {
            stream.close();
         } catch (IOException var3) {
            LOGGER.error("Unable to close stream " + this.getName() + ". " + var3);
         }

      }
   }

   public synchronized void flush() {
      try {
         this.os.flush();
      } catch (IOException var3) {
         String msg = "Error flushing stream " + this.getName();
         throw new AppenderLoggingException(msg, var3);
      }
   }
}
