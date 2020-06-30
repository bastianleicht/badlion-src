package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;

public interface IOFileFilter extends FileFilter, FilenameFilter {
   boolean accept(File var1);

   boolean accept(File var1, String var2);
}
