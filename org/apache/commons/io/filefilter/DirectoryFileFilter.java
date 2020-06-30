package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

public class DirectoryFileFilter extends AbstractFileFilter implements Serializable {
   public static final IOFileFilter DIRECTORY = new DirectoryFileFilter();
   public static final IOFileFilter INSTANCE = DIRECTORY;

   public boolean accept(File file) {
      return file.isDirectory();
   }
}
