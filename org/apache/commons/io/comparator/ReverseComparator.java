package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.comparator.AbstractFileComparator;

class ReverseComparator extends AbstractFileComparator implements Serializable {
   private final Comparator delegate;

   public ReverseComparator(Comparator delegate) {
      if(delegate == null) {
         throw new IllegalArgumentException("Delegate comparator is missing");
      } else {
         this.delegate = delegate;
      }
   }

   public int compare(File file1, File file2) {
      return this.delegate.compare(file2, file1);
   }

   public String toString() {
      return super.toString() + "[" + this.delegate.toString() + "]";
   }
}
