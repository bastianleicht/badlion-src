package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.comparator.AbstractFileComparator;
import org.apache.commons.io.comparator.ReverseComparator;

public class DefaultFileComparator extends AbstractFileComparator implements Serializable {
   public static final Comparator DEFAULT_COMPARATOR = new DefaultFileComparator();
   public static final Comparator DEFAULT_REVERSE = new ReverseComparator(DEFAULT_COMPARATOR);

   public int compare(File file1, File file2) {
      return file1.compareTo(file2);
   }
}
