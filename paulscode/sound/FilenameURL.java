package paulscode.sound;

import java.net.URL;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

public class FilenameURL {
   private SoundSystemLogger logger = SoundSystemConfig.getLogger();
   private String filename = null;
   private URL url = null;

   public FilenameURL(URL url, String identifier) {
      this.filename = identifier;
      this.url = url;
   }

   public FilenameURL(String filename) {
      this.filename = filename;
      this.url = null;
   }

   public String getFilename() {
      return this.filename;
   }

   public URL getURL() {
      if(this.url == null) {
         if(this.filename.matches(SoundSystemConfig.PREFIX_URL)) {
            try {
               this.url = new URL(this.filename);
            } catch (Exception var2) {
               this.errorMessage("Unable to access online URL in method \'getURL\'");
               this.printStackTrace(var2);
               return null;
            }
         } else {
            this.url = this.getClass().getClassLoader().getResource(SoundSystemConfig.getSoundFilesPackage() + this.filename);
         }
      }

      return this.url;
   }

   private void errorMessage(String message) {
      this.logger.errorMessage("MidiChannel", message, 0);
   }

   private void printStackTrace(Exception e) {
      this.logger.printStackTrace(e, 1);
   }
}
