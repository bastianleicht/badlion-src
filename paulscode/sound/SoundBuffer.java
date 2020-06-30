package paulscode.sound;

import javax.sound.sampled.AudioFormat;

public class SoundBuffer {
   public byte[] audioData;
   public AudioFormat audioFormat;

   public SoundBuffer(byte[] audioData, AudioFormat audioFormat) {
      this.audioData = audioData;
      this.audioFormat = audioFormat;
   }

   public void cleanup() {
      this.audioData = null;
      this.audioFormat = null;
   }

   public void trimData(int maxLength) {
      if(this.audioData != null && maxLength != 0) {
         if(this.audioData.length > maxLength) {
            byte[] trimmedArray = new byte[maxLength];
            System.arraycopy(this.audioData, 0, trimmedArray, 0, maxLength);
            this.audioData = trimmedArray;
         }
      } else {
         this.audioData = null;
      }

   }
}
