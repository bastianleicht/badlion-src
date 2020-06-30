package paulscode.sound;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import paulscode.sound.SimpleThread;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;
import paulscode.sound.Source;

public class StreamThread extends SimpleThread {
   private SoundSystemLogger logger = SoundSystemConfig.getLogger();
   private List streamingSources = new LinkedList();
   private final Object listLock = new Object();

   protected void cleanup() {
      this.kill();
      super.cleanup();
   }

   public void run() {
      // $FF: Couldn't be decompiled
   }

   public void watch(Source source) {
      if(source != null) {
         if(!this.streamingSources.contains(source)) {
            synchronized(this.listLock) {
               ListIterator<Source> iter = this.streamingSources.listIterator();

               while(iter.hasNext()) {
                  Source src = (Source)iter.next();
                  if(src == null) {
                     iter.remove();
                  } else if(source.channel == src.channel) {
                     src.stop();
                     iter.remove();
                  }
               }

               this.streamingSources.add(source);
            }
         }
      }
   }

   private void message(String message) {
      this.logger.message(message, 0);
   }

   private void importantMessage(String message) {
      this.logger.importantMessage(message, 0);
   }

   private boolean errorCheck(boolean error, String message) {
      return this.logger.errorCheck(error, "StreamThread", message, 0);
   }

   private void errorMessage(String message) {
      this.logger.errorMessage("StreamThread", message, 0);
   }
}
