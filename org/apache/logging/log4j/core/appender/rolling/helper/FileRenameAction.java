package org.apache.logging.log4j.core.appender.rolling.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import org.apache.logging.log4j.core.appender.rolling.helper.AbstractAction;

public class FileRenameAction extends AbstractAction {
   private final File source;
   private final File destination;
   private final boolean renameEmptyFiles;

   public FileRenameAction(File src, File dst, boolean renameEmptyFiles) {
      this.source = src;
      this.destination = dst;
      this.renameEmptyFiles = renameEmptyFiles;
   }

   public boolean execute() {
      return execute(this.source, this.destination, this.renameEmptyFiles);
   }

   public static boolean execute(File source, File destination, boolean renameEmptyFiles) {
      if(!renameEmptyFiles && source.length() <= 0L) {
         try {
            source.delete();
         } catch (Exception var6) {
            LOGGER.error("Unable to delete empty file " + source.getAbsolutePath());
         }
      } else {
         File parent = destination.getParentFile();
         if(parent != null && !parent.exists() && !parent.mkdirs()) {
            LOGGER.error("Unable to create directory {}", new Object[]{parent.getAbsolutePath()});
            return false;
         }

         try {
            if(!source.renameTo(destination)) {
               try {
                  copyFile(source, destination);
                  return source.delete();
               } catch (IOException var7) {
                  LOGGER.error("Unable to rename file {} to {} - {}", new Object[]{source.getAbsolutePath(), destination.getAbsolutePath(), var7.getMessage()});
               }
            }

            return true;
         } catch (Exception var9) {
            try {
               copyFile(source, destination);
               return source.delete();
            } catch (IOException var8) {
               LOGGER.error("Unable to rename file {} to {} - {}", new Object[]{source.getAbsolutePath(), destination.getAbsolutePath(), var8.getMessage()});
            }
         }
      }

      return false;
   }

   private static void copyFile(File source, File destination) throws IOException {
      if(!destination.exists()) {
         destination.createNewFile();
      }

      FileChannel srcChannel = null;
      FileChannel destChannel = null;
      FileInputStream srcStream = null;
      FileOutputStream destStream = null;

      try {
         srcStream = new FileInputStream(source);
         destStream = new FileOutputStream(destination);
         srcChannel = srcStream.getChannel();
         destChannel = destStream.getChannel();
         destChannel.transferFrom(srcChannel, 0L, srcChannel.size());
      } finally {
         if(srcChannel != null) {
            srcChannel.close();
         }

         if(srcStream != null) {
            srcStream.close();
         }

         if(destChannel != null) {
            destChannel.close();
         }

         if(destStream != null) {
            destStream.close();
         }

      }

   }
}
