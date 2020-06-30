package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.Serializable;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class DelegateFileFilter extends AbstractFileFilter implements Serializable {
   private final FilenameFilter filenameFilter;
   private final FileFilter fileFilter;

   public DelegateFileFilter(FilenameFilter filter) {
      if(filter == null) {
         throw new IllegalArgumentException("The FilenameFilter must not be null");
      } else {
         this.filenameFilter = filter;
         this.fileFilter = null;
      }
   }

   public DelegateFileFilter(FileFilter filter) {
      if(filter == null) {
         throw new IllegalArgumentException("The FileFilter must not be null");
      } else {
         this.fileFilter = filter;
         this.filenameFilter = null;
      }
   }

   public boolean accept(File file) {
      return this.fileFilter != null?this.fileFilter.accept(file):super.accept(file);
   }

   public boolean accept(File dir, String name) {
      return this.filenameFilter != null?this.filenameFilter.accept(dir, name):super.accept(dir, name);
   }

   public String toString() {
      String delegate = this.fileFilter != null?this.fileFilter.toString():this.filenameFilter.toString();
      return super.toString() + "(" + delegate + ")";
   }
}
