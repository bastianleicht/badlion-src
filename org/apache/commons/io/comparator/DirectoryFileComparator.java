package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.comparator.AbstractFileComparator;
import org.apache.commons.io.comparator.ReverseComparator;

public class DirectoryFileComparator extends AbstractFileComparator implements Serializable {
   public static final Comparator DIRECTORY_COMPARATOR = new DirectoryFileComparator();
   public static final Comparator DIRECTORY_REVERSE = new ReverseComparator(DIRECTORY_COMPARATOR);

   public int compare(File file1, File file2) {
      return this.getType(file1) - this.getType(file2);
   }

   private int getType(File file) {
      return file.isDirectory()?1:2;
   }
}
