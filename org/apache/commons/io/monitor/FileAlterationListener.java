package org.apache.commons.io.monitor;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationObserver;

public interface FileAlterationListener {
   void onStart(FileAlterationObserver var1);

   void onDirectoryCreate(File var1);

   void onDirectoryChange(File var1);

   void onDirectoryDelete(File var1);

   void onFileCreate(File var1);

   void onFileChange(File var1);

   void onFileDelete(File var1);

   void onStop(FileAlterationObserver var1);
}
