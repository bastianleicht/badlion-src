package org.apache.commons.io;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public abstract class DirectoryWalker {
   private final FileFilter filter;
   private final int depthLimit;

   protected DirectoryWalker() {
      this((FileFilter)null, -1);
   }

   protected DirectoryWalker(FileFilter filter, int depthLimit) {
      this.filter = filter;
      this.depthLimit = depthLimit;
   }

   protected DirectoryWalker(IOFileFilter directoryFilter, IOFileFilter fileFilter, int depthLimit) {
      if(directoryFilter == null && fileFilter == null) {
         this.filter = null;
      } else {
         directoryFilter = directoryFilter != null?directoryFilter:TrueFileFilter.TRUE;
         fileFilter = fileFilter != null?fileFilter:TrueFileFilter.TRUE;
         directoryFilter = FileFilterUtils.makeDirectoryOnly(directoryFilter);
         fileFilter = FileFilterUtils.makeFileOnly(fileFilter);
         this.filter = FileFilterUtils.or(new IOFileFilter[]{directoryFilter, fileFilter});
      }

      this.depthLimit = depthLimit;
   }

   protected final void walk(File startDirectory, Collection results) throws IOException {
      if(startDirectory == null) {
         throw new NullPointerException("Start Directory is null");
      } else {
         try {
            this.handleStart(startDirectory, results);
            this.walk(startDirectory, 0, results);
            this.handleEnd(results);
         } catch (DirectoryWalker.CancelException var4) {
            this.handleCancelled(startDirectory, results, var4);
         }

      }
   }

   private void walk(File directory, int depth, Collection results) throws IOException {
      this.checkIfCancelled(directory, depth, results);
      if(this.handleDirectory(directory, depth, results)) {
         this.handleDirectoryStart(directory, depth, results);
         int childDepth = depth + 1;
         if(this.depthLimit < 0 || childDepth <= this.depthLimit) {
            this.checkIfCancelled(directory, depth, results);
            File[] childFiles = this.filter == null?directory.listFiles():directory.listFiles(this.filter);
            childFiles = this.filterDirectoryContents(directory, depth, childFiles);
            if(childFiles == null) {
               this.handleRestricted(directory, childDepth, results);
            } else {
               for(File childFile : childFiles) {
                  if(childFile.isDirectory()) {
                     this.walk(childFile, childDepth, results);
                  } else {
                     this.checkIfCancelled(childFile, childDepth, results);
                     this.handleFile(childFile, childDepth, results);
                     this.checkIfCancelled(childFile, childDepth, results);
                  }
               }
            }
         }

         this.handleDirectoryEnd(directory, depth, results);
      }

      this.checkIfCancelled(directory, depth, results);
   }

   protected final void checkIfCancelled(File file, int depth, Collection results) throws IOException {
      if(this.handleIsCancelled(file, depth, results)) {
         throw new DirectoryWalker.CancelException(file, depth);
      }
   }

   protected boolean handleIsCancelled(File file, int depth, Collection results) throws IOException {
      return false;
   }

   protected void handleCancelled(File startDirectory, Collection results, DirectoryWalker.CancelException cancel) throws IOException {
      throw cancel;
   }

   protected void handleStart(File startDirectory, Collection results) throws IOException {
   }

   protected boolean handleDirectory(File directory, int depth, Collection results) throws IOException {
      return true;
   }

   protected void handleDirectoryStart(File directory, int depth, Collection results) throws IOException {
   }

   protected File[] filterDirectoryContents(File directory, int depth, File[] files) throws IOException {
      return files;
   }

   protected void handleFile(File file, int depth, Collection results) throws IOException {
   }

   protected void handleRestricted(File directory, int depth, Collection results) throws IOException {
   }

   protected void handleDirectoryEnd(File directory, int depth, Collection results) throws IOException {
   }

   protected void handleEnd(Collection results) throws IOException {
   }

   public static class CancelException extends IOException {
      private static final long serialVersionUID = 1347339620135041008L;
      private final File file;
      private final int depth;

      public CancelException(File file, int depth) {
         this("Operation Cancelled", file, depth);
      }

      public CancelException(String message, File file, int depth) {
         super(message);
         this.file = file;
         this.depth = depth;
      }

      public File getFile() {
         return this.file;
      }

      public int getDepth() {
         return this.depth;
      }
   }
}
