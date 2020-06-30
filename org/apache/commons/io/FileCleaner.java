package org.apache.commons.io;

import java.io.File;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;

/** @deprecated */
@Deprecated
public class FileCleaner {
   static final FileCleaningTracker theInstance = new FileCleaningTracker();

   /** @deprecated */
   @Deprecated
   public static void track(File file, Object marker) {
      theInstance.track(file, marker);
   }

   /** @deprecated */
   @Deprecated
   public static void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
      theInstance.track(file, marker, deleteStrategy);
   }

   /** @deprecated */
   @Deprecated
   public static void track(String path, Object marker) {
      theInstance.track(path, marker);
   }

   /** @deprecated */
   @Deprecated
   public static void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
      theInstance.track(path, marker, deleteStrategy);
   }

   /** @deprecated */
   @Deprecated
   public static int getTrackCount() {
      return theInstance.getTrackCount();
   }

   /** @deprecated */
   @Deprecated
   public static synchronized void exitWhenFinished() {
      theInstance.exitWhenFinished();
   }

   public static FileCleaningTracker getInstance() {
      return theInstance;
   }
}
