package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;

public final class ZipCompressAction extends AbstractAction {
   private static final int BUF_SIZE = 8102;
   private final File source;
   private final File destination;
   private final boolean deleteSource;
   private final int level;

   public ZipCompressAction(File source, File destination, boolean deleteSource, int level) {
      if(source == null) {
         throw new NullPointerException("source");
      } else if(destination == null) {
         throw new NullPointerException("destination");
      } else {
         this.source = source;
         this.destination = destination;
         this.deleteSource = deleteSource;
         this.level = level;
      }
   }

   public boolean execute() throws IOException {
      return execute(this.source, this.destination, this.deleteSource, this.level);
   }

   public static boolean execute(File source, File destination, boolean deleteSource, int level) throws IOException {
      if(!source.exists()) {
         return false;
      } else {
         FileInputStream fis = new FileInputStream(source);
         FileOutputStream fos = new FileOutputStream(destination);
         ZipOutputStream zos = new ZipOutputStream(fos);
         zos.setLevel(level);
         ZipEntry zipEntry = new ZipEntry(source.getName());
         zos.putNextEntry(zipEntry);
         byte[] inbuf = new byte[8102];

         int n;
         while((n = fis.read(inbuf)) != -1) {
            zos.write(inbuf, 0, n);
         }

         zos.close();
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
