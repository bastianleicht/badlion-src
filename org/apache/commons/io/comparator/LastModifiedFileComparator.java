package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.comparator.AbstractFileComparator;
import org.apache.commons.io.comparator.ReverseComparator;

public class LastModifiedFileComparator extends AbstractFileComparator implements Serializable {
   public static final Comparator LASTMODIFIED_COMPARATOR = new LastModifiedFileComparator();
   public static final Comparator LASTMODIFIED_REVERSE = new ReverseComparator(LASTMODIFIED_COMPARATOR);

   public int compare(File file1, File file2) {
      long result = file1.lastModified() - file2.lastModified();
      return result < 0L?-1:(result > 0L?1:0);
   }
}
