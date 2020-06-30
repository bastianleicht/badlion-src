package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.filefilter.IOFileFilter;

public class TrueFileFilter implements IOFileFilter, Serializable {
   public static final IOFileFilter TRUE = new TrueFileFilter();
   public static final IOFileFilter INSTANCE = TRUE;

   public boolean accept(File file) {
      return true;
   }

   public boolean accept(File dir, String name) {
      return true;
   }
}
