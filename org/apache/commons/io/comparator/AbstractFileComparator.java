package org.apache.commons.io.comparator;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

abstract class AbstractFileComparator implements Comparator {
   public File[] sort(File... files) {
      if(files != null) {
         Arrays.sort(files, this);
      }

      return files;
   }

   public List sort(List files) {
      if(files != null) {
         Collections.sort(files, this);
      }

      return files;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }
}
