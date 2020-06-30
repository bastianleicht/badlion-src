package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class AgeFileFilter extends AbstractFileFilter implements Serializable {
   private final long cutoff;
   private final boolean acceptOlder;

   public AgeFileFilter(long cutoff) {
      this(cutoff, true);
   }

   public AgeFileFilter(long cutoff, boolean acceptOlder) {
      this.acceptOlder = acceptOlder;
      this.cutoff = cutoff;
   }

   public AgeFileFilter(Date cutoffDate) {
      this(cutoffDate, true);
   }

   public AgeFileFilter(Date cutoffDate, boolean acceptOlder) {
      this(cutoffDate.getTime(), acceptOlder);
   }

   public AgeFileFilter(File cutoffReference) {
      this(cutoffReference, true);
   }

   public AgeFileFilter(File cutoffReference, boolean acceptOlder) {
      this(cutoffReference.lastModified(), acceptOlder);
   }

   public boolean accept(File file) {
      boolean newer = FileUtils.isFileNewer(file, this.cutoff);
      return this.acceptOlder?!newer:newer;
   }

   public String toString() {
      String condition = this.acceptOlder?"<=":">";
      return super.toString() + "(" + condition + this.cutoff + ")";
   }
}
