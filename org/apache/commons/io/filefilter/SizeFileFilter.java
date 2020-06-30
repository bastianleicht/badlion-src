package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class SizeFileFilter extends AbstractFileFilter implements Serializable {
   private final long size;
   private final boolean acceptLarger;

   public SizeFileFilter(long size) {
      this(size, true);
   }

   public SizeFileFilter(long size, boolean acceptLarger) {
      if(size < 0L) {
         throw new IllegalArgumentException("The size must be non-negative");
      } else {
         this.size = size;
         this.acceptLarger = acceptLarger;
      }
   }

   public boolean accept(File file) {
      boolean smaller = file.length() < this.size;
      return this.acceptLarger?!smaller:smaller;
   }

   public String toString() {
      String condition = this.acceptLarger?">=":"<";
      return super.toString() + "(" + condition + this.size + ")";
   }
}
