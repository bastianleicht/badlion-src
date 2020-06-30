package org.apache.commons.compress.archivers;

import java.util.Date;

public interface ArchiveEntry {
   long SIZE_UNKNOWN = -1L;

   String getName();

   long getSize();

   boolean isDirectory();

   Date getLastModifiedDate();
}
