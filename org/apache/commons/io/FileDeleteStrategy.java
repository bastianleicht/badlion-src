package org.apache.commons.io;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;

public class FileDeleteStrategy {
   public static final FileDeleteStrategy NORMAL = new FileDeleteStrategy("Normal");
   public static final FileDeleteStrategy FORCE = new FileDeleteStrategy.ForceFileDeleteStrategy();
   private final String name;

   protected FileDeleteStrategy(String name) {
      this.name = name;
   }

   public boolean deleteQuietly(File fileToDelete) {
      if(fileToDelete != null && fileToDelete.exists()) {
         try {
            return this.doDelete(fileToDelete);
         } catch (IOException var3) {
            return false;
         }
      } else {
         return true;
      }
   }

   public void delete(File fileToDelete) throws IOException {
      if(fileToDelete.exists() && !this.doDelete(fileToDelete)) {
         throw new IOException("Deletion failed: " + fileToDelete);
      }
   }

   protected boolean doDelete(File fileToDelete) throws IOException {
      return fileToDelete.delete();
   }

   public String toString() {
      return "FileDeleteStrategy[" + this.name + "]";
   }

   static class ForceFileDeleteStrategy extends FileDeleteStrategy {
      ForceFileDeleteStrategy() {
         super("Force");
      }

      protected boolean doDelete(File fileToDelete) throws IOException {
         FileUtils.forceDelete(fileToDelete);
         return true;
      }
   }
}
