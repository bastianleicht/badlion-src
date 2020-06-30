package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.AbstractFileComparator;
import org.apache.commons.io.comparator.ReverseComparator;

public class SizeFileComparator extends AbstractFileComparator implements Serializable {
   public static final Comparator SIZE_COMPARATOR = new SizeFileComparator();
   public static final Comparator SIZE_REVERSE = new ReverseComparator(SIZE_COMPARATOR);
   public static final Comparator SIZE_SUMDIR_COMPARATOR = new SizeFileComparator(true);
   public static final Comparator SIZE_SUMDIR_REVERSE = new ReverseComparator(SIZE_SUMDIR_COMPARATOR);
   private final boolean sumDirectoryContents;

   public SizeFileComparator() {
      this.sumDirectoryContents = false;
   }

   public SizeFileComparator(boolean sumDirectoryContents) {
      this.sumDirectoryContents = sumDirectoryContents;
   }

   public int compare(File file1, File file2) {
      long size1 = 0L;
      if(file1.isDirectory()) {
         size1 = this.sumDirectoryContents && file1.exists()?FileUtils.sizeOfDirectory(file1):0L;
      } else {
         size1 = file1.length();
      }

      long size2 = 0L;
      if(file2.isDirectory()) {
         size2 = this.sumDirectoryContents && file2.exists()?FileUtils.sizeOfDirectory(file2):0L;
      } else {
         size2 = file2.length();
      }

      long result = size1 - size2;
      return result < 0L?-1:(result > 0L?1:0);
   }

   public String toString() {
      return super.toString() + "[sumDirectoryContents=" + this.sumDirectoryContents + "]";
   }
}
