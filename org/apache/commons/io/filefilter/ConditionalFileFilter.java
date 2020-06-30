package org.apache.commons.io.filefilter;

import java.util.List;
import org.apache.commons.io.filefilter.IOFileFilter;

public interface ConditionalFileFilter {
   void addFileFilter(IOFileFilter var1);

   List getFileFilters();

   boolean removeFileFilter(IOFileFilter var1);

   void setFileFilters(List var1);
}
