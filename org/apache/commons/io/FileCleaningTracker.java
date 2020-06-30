package org.apache.commons.io;

import java.io.File;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.io.FileDeleteStrategy;

public class FileCleaningTracker {
   ReferenceQueue q = new ReferenceQueue();
   final Collection trackers = Collections.synchronizedSet(new HashSet());
   final List deleteFailures = Collections.synchronizedList(new ArrayList());
   volatile boolean exitWhenFinished = false;
   Thread reaper;

   public void track(File file, Object marker) {
      this.track(file, marker, (FileDeleteStrategy)null);
   }

   public void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
      if(file == null) {
         throw new NullPointerException("The file must not be null");
      } else {
         this.addTracker(file.getPath(), marker, deleteStrategy);
      }
   }

   public void track(String path, Object marker) {
      this.track(path, marker, (FileDeleteStrategy)null);
   }

   public void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
      if(path == null) {
         throw new NullPointerException("The path must not be null");
      } else {
         this.addTracker(path, marker, deleteStrategy);
      }
   }

   private synchronized void addTracker(String path, Object marker, FileDeleteStrategy deleteStrategy) {
      if(this.exitWhenFinished) {
         throw new IllegalStateException("No new trackers can be added once exitWhenFinished() is called");
      } else {
         if(this.reaper == null) {
            this.reaper = new FileCleaningTracker.Reaper();
            this.reaper.start();
         }

         this.trackers.add(new FileCleaningTracker.Tracker(path, deleteStrategy, marker, this.q));
      }
   }

   public int getTrackCount() {
      return this.trackers.size();
   }

   public List getDeleteFailures() {
      return this.deleteFailures;
   }

   public synchronized void exitWhenFinished() {
      this.exitWhenFinished = true;
      if(this.reaper != null) {
         synchronized(this.reaper) {
            this.reaper.interrupt();
         }
      }

   }

   private final class Reaper extends Thread {
      Reaper() {
         super("File Reaper");
         this.setPriority(10);
         this.setDaemon(true);
      }

      public void run() {
         while(!FileCleaningTracker.this.exitWhenFinished || FileCleaningTracker.this.trackers.size() > 0) {
            try {
               FileCleaningTracker.Tracker tracker = (FileCleaningTracker.Tracker)FileCleaningTracker.this.q.remove();
               FileCleaningTracker.this.trackers.remove(tracker);
               if(!tracker.delete()) {
                  FileCleaningTracker.this.deleteFailures.add(tracker.getPath());
               }

               tracker.clear();
            } catch (InterruptedException var2) {
               ;
            }
         }

      }
   }

   private static final class Tracker extends PhantomReference {
      private final String path;
      private final FileDeleteStrategy deleteStrategy;

      Tracker(String path, FileDeleteStrategy deleteStrategy, Object marker, ReferenceQueue queue) {
         super(marker, queue);
         this.path = path;
         this.deleteStrategy = deleteStrategy == null?FileDeleteStrategy.NORMAL:deleteStrategy;
      }

      public String getPath() {
         return this.path;
      }

      public boolean delete() {
         return this.deleteStrategy.deleteQuietly(new File(this.path));
      }
   }
}
