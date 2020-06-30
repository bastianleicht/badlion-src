package org.apache.commons.io.monitor;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationObserver;

public class FileAlterationListenerAdaptor implements FileAlterationListener {
   public void onStart(FileAlterationObserver observer) {
   }

   public void onDirectoryCreate(File directory) {
   }

   public void onDirectoryChange(File directory) {
   }

   public void onDirectoryDelete(File directory) {
   }

   public void onFileCreate(File file) {
   }

   public void onFileChange(File file) {
   }

   public void onFileDelete(File file) {
   }

   public void onStop(FileAlterationObserver observer) {
   }
}
