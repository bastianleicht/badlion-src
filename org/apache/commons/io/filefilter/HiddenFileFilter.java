package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;

public class HiddenFileFilter extends AbstractFileFilter implements Serializable {
   public static final IOFileFilter HIDDEN = new HiddenFileFilter();
   public static final IOFileFilter VISIBLE = new NotFileFilter(HIDDEN);

   public boolean accept(File file) {
      return file.isHidden();
   }
}
