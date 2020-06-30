package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

public class CanWriteFileFilter extends AbstractFileFilter implements Serializable {
   public static final IOFileFilter CAN_WRITE = new CanWriteFileFilter();
   public static final IOFileFilter CANNOT_WRITE = new NotFileFilter(CAN_WRITE);

   public boolean accept(File file) {
      return file.canWrite();
   }
}
