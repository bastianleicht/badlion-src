package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;

public final class GZCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8102;
   private final File source;
   private final File destination;
   private final boolean deleteSource;

   public GZCompressAction(File source, File destination, boolean deleteSource) {
      if(source == null) {
         throw new NullPointerException("source");
      } else if(destination == null) {
         throw new NullPointerException("destination");
      } else {
         this.source = source;
         this.destination = destination;
         this.deleteSource = deleteSource;
      }
   }

   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource);
   }

   public static boolean execute(File source, File destination, boolean deleteSource) throws IOException {
      if(!source.exists()) {
         return false;
      } else {
         FileInputStream fis = new FileInputStream(source);
         FileOutputStream fos = new FileOutputStream(destination);
         GZIPOutputStream gzos = new GZIPOutputStream(fos);
         BufferedOutputStream os = new BufferedOutputStream(gzos);
         byte[] inbuf = new byte[8102];

         int n;
         while((n = fis.read(inbuf)) != -1) {
            os.write(inbuf, 0, n);
         }

         os.close();
         fis.close();
         if(deleteSource && !source.delete()) {
            LOGGER.warn("Unable to delete " + source.toString() + '.');
         }

         return true;
      }
   }

   protected void reportException(Exception ex) {
      LOGGER.warn((String)("Exception during compression of \'" + this.source.toString() + "\'."), (Throwable)ex);
   }
}
