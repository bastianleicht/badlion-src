package paulscode.sound.libraries;

import java.util.LinkedList;
import java.util.List;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.DataLine.Info;
import javax.sound.sampled.FloatControl.Type;
import paulscode.sound.Channel;
import paulscode.sound.SoundBuffer;
import paulscode.sound.libraries.LibraryJavaSound;

public class ChannelJavaSound extends Channel {
   public Clip clip = null;
   SoundBuffer soundBuffer;
   public SourceDataLine sourceDataLine = null;
   private List streamBuffers;
   private int processed = 0;
   private Mixer myMixer = null;
   private AudioFormat myFormat = null;
   private FloatControl gainControl = null;
   private FloatControl panControl = null;
   private FloatControl sampleRateControl = null;
   private float initialGain = 0.0F;
   private float initialSampleRate = 0.0F;
   private boolean toLoop = false;

   public ChannelJavaSound(int type, Mixer mixer) {
      super(type);
      this.libraryType = LibraryJavaSound.class;
      this.myMixer = mixer;
      this.clip = null;
      this.sourceDataLine = null;
      this.streamBuffers = new LinkedList();
   }

   public void cleanup() {
      if(this.streamBuffers != null) {
         SoundBuffer var3;
         for(buf = null; !this.streamBuffers.isEmpty(); var3 = null) {
            var3 = (SoundBuffer)this.streamBuffers.remove(0);
            var3.cleanup();
         }

         this.streamBuffers.clear();
      }

      this.clip = null;
      this.soundBuffer = null;
      this.sourceDataLine = null;
      this.streamBuffers.clear();
      this.myMixer = null;
      this.myFormat = null;
      this.streamBuffers = null;
      super.cleanup();
   }

   public void newMixer(Mixer m) {
      if(this.myMixer != m) {
         try {
            if(this.clip != null) {
               this.clip.close();
            } else if(this.sourceDataLine != null) {
               this.sourceDataLine.close();
            }
         } catch (SecurityException var3) {
            ;
         }

         this.myMixer = m;
         if(this.attachedSource != null) {
            if(this.channelType == 0 && this.soundBuffer != null) {
               this.attachBuffer(this.soundBuffer);
            } else if(this.myFormat != null) {
               this.resetStream(this.myFormat);
            }
         }
      }

   }

   public boolean attachBuffer(SoundBuffer buffer) {
      if(this.errorCheck(this.channelType != 0, "Buffers may only be attached to non-streaming sources")) {
         return false;
      } else if(this.errorCheck(this.myMixer == null, "Mixer null in method \'attachBuffer\'")) {
         return false;
      } else if(this.errorCheck(buffer == null, "Buffer null in method \'attachBuffer\'")) {
         return false;
      } else if(this.errorCheck(buffer.audioData == null, "Buffer missing audio data in method \'attachBuffer\'")) {
         return false;
      } else if(this.errorCheck(buffer.audioFormat == null, "Buffer missing format information in method \'attachBuffer\'")) {
         return false;
      } else {
         Info lineInfo = new Info(Clip.class, buffer.audioFormat);
         if(this.errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method \'attachBuffer\'")) {
            return false;
         } else {
            Clip newClip = null;

            try {
               newClip = (Clip)this.myMixer.getLine(lineInfo);
            } catch (Exception var6) {
               this.errorMessage("Unable to create clip in method \'attachBuffer\'");
               this.printStackTrace(var6);
               return false;
            }

            if(this.errorCheck(newClip == null, "New clip null in method \'attachBuffer\'")) {
               return false;
            } else {
               if(this.clip != null) {
                  this.clip.stop();
                  this.clip.flush();
                  this.clip.close();
               }

               this.clip = newClip;
               this.soundBuffer = buffer;
               this.myFormat = buffer.audioFormat;
               newClip = null;

               try {
                  this.clip.open(this.myFormat, buffer.audioData, 0, buffer.audioData.length);
               } catch (Exception var5) {
                  this.errorMessage("Unable to attach buffer to clip in method \'attachBuffer\'");
                  this.printStackTrace(var5);
                  return false;
               }

               this.resetControls();
               return true;
            }
         }
      }
   }

   public void setAudioFormat(AudioFormat audioFormat) {
      this.resetStream(audioFormat);
      if(this.attachedSource != null && this.attachedSource.rawDataStream && this.attachedSource.active() && this.sourceDataLine != null) {
         this.sourceDataLine.start();
      }

   }

   public boolean resetStream(AudioFormat format) {
      if(this.errorCheck(this.myMixer == null, "Mixer null in method \'resetStream\'")) {
         return false;
      } else if(this.errorCheck(format == null, "AudioFormat null in method \'resetStream\'")) {
         return false;
      } else {
         Info lineInfo = new Info(SourceDataLine.class, format);
         if(this.errorCheck(!AudioSystem.isLineSupported(lineInfo), "Line not supported in method \'resetStream\'")) {
            return false;
         } else {
            SourceDataLine newSourceDataLine = null;

            try {
               newSourceDataLine = (SourceDataLine)this.myMixer.getLine(lineInfo);
            } catch (Exception var6) {
               this.errorMessage("Unable to create a SourceDataLine in method \'resetStream\'");
               this.printStackTrace(var6);
               return false;
            }

            if(this.errorCheck(newSourceDataLine == null, "New SourceDataLine null in method \'resetStream\'")) {
               return false;
            } else {
               this.streamBuffers.clear();
               this.processed = 0;
               if(this.sourceDataLine != null) {
                  this.sourceDataLine.stop();
                  this.sourceDataLine.flush();
                  this.sourceDataLine.close();
               }

               this.sourceDataLine = newSourceDataLine;
               this.myFormat = format;
               newSourceDataLine = null;

               try {
                  this.sourceDataLine.open(this.myFormat);
               } catch (Exception var5) {
                  this.errorMessage("Unable to open the new SourceDataLine in method \'resetStream\'");
                  this.printStackTrace(var5);
                  return false;
               }

               this.resetControls();
               return true;
            }
         }
      }
   }

   private void resetControls() {
      switch(this.channelType) {
      case 0:
         try {
            if(!this.clip.isControlSupported(Type.PAN)) {
               this.panControl = null;
            } else {
               this.panControl = (FloatControl)this.clip.getControl(Type.PAN);
            }
         } catch (IllegalArgumentException var7) {
            this.panControl = null;
         }

         try {
            if(!this.clip.isControlSupported(Type.MASTER_GAIN)) {
               this.gainControl = null;
               this.initialGain = 0.0F;
            } else {
               this.gainControl = (FloatControl)this.clip.getControl(Type.MASTER_GAIN);
               this.initialGain = this.gainControl.getValue();
            }
         } catch (IllegalArgumentException var6) {
            this.gainControl = null;
            this.initialGain = 0.0F;
         }

         try {
            if(!this.clip.isControlSupported(Type.SAMPLE_RATE)) {
               this.sampleRateControl = null;
               this.initialSampleRate = 0.0F;
            } else {
               this.sampleRateControl = (FloatControl)this.clip.getControl(Type.SAMPLE_RATE);
               this.initialSampleRate = this.sampleRateControl.getValue();
            }
         } catch (IllegalArgumentException var5) {
            this.sampleRateControl = null;
            this.initialSampleRate = 0.0F;
         }
         break;
      case 1:
         try {
            if(!this.sourceDataLine.isControlSupported(Type.PAN)) {
               this.panControl = null;
            } else {
               this.panControl = (FloatControl)this.sourceDataLine.getControl(Type.PAN);
            }
         } catch (IllegalArgumentException var4) {
            this.panControl = null;
         }

         try {
            if(!this.sourceDataLine.isControlSupported(Type.MASTER_GAIN)) {
               this.gainControl = null;
               this.initialGain = 0.0F;
            } else {
               this.gainControl = (FloatControl)this.sourceDataLine.getControl(Type.MASTER_GAIN);
               this.initialGain = this.gainControl.getValue();
            }
         } catch (IllegalArgumentException var3) {
            this.gainControl = null;
            this.initialGain = 0.0F;
         }

         try {
            if(!this.sourceDataLine.isControlSupported(Type.SAMPLE_RATE)) {
               this.sampleRateControl = null;
               this.initialSampleRate = 0.0F;
            } else {
               this.sampleRateControl = (FloatControl)this.sourceDataLine.getControl(Type.SAMPLE_RATE);
               this.initialSampleRate = this.sampleRateControl.getValue();
            }
         } catch (IllegalArgumentException var2) {
            this.sampleRateControl = null;
            this.initialSampleRate = 0.0F;
         }
         break;
      default:
         this.errorMessage("Unrecognized channel type in method \'resetControls\'");
         this.panControl = null;
         this.gainControl = null;
         this.sampleRateControl = null;
      }

   }

   public void setLooping(boolean value) {
      this.toLoop = value;
   }

   public void setPan(float p) {
      if(this.panControl != null) {
         float pan = p;
         if(p < -1.0F) {
            pan = -1.0F;
         }

         if(pan > 1.0F) {
            pan = 1.0F;
         }

         this.panControl.setValue(pan);
      }
   }

   public void setGain(float g) {
      if(this.gainControl != null) {
         float gain = g;
         if(g < 0.0F) {
            gain = 0.0F;
         }

         if(gain > 1.0F) {
            gain = 1.0F;
         }

         double minimumDB = (double)this.gainControl.getMinimum();
         double maximumDB = (double)this.initialGain;
         double ampGainDB = 0.5D * maximumDB - minimumDB;
         double cste = Math.log(10.0D) / 20.0D;
         float valueDB = (float)(minimumDB + 1.0D / cste * Math.log(1.0D + (Math.exp(cste * ampGainDB) - 1.0D) * (double)gain));
         this.gainControl.setValue(valueDB);
      }
   }

   public void setPitch(float p) {
      if(this.sampleRateControl != null) {
         float sampleRate = p;
         if(p < 0.5F) {
            sampleRate = 0.5F;
         }

         if(sampleRate > 2.0F) {
            sampleRate = 2.0F;
         }

         sampleRate = sampleRate * this.initialSampleRate;
         this.sampleRateControl.setValue(sampleRate);
      }
   }

   public boolean preLoadBuffers(LinkedList bufferList) {
      if(this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else if(this.errorCheck(this.sourceDataLine == null, "SourceDataLine null in method \'preLoadBuffers\'.")) {
         return false;
      } else {
         this.sourceDataLine.start();
         if(bufferList.isEmpty()) {
            return true;
         } else {
            byte[] preLoad = (byte[])bufferList.remove(0);
            if(this.errorCheck(preLoad == null, "Missing sound-bytes in method \'preLoadBuffers\'.")) {
               return false;
            } else {
               while(!bufferList.isEmpty()) {
                  this.streamBuffers.add(new SoundBuffer((byte[])bufferList.remove(0), this.myFormat));
               }

               this.sourceDataLine.write(preLoad, 0, preLoad.length);
               this.processed = 0;
               return true;
            }
         }
      }
   }

   public boolean queueBuffer(byte[] buffer) {
      if(this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         return false;
      } else if(this.errorCheck(this.sourceDataLine == null, "SourceDataLine null in method \'queueBuffer\'.")) {
         return false;
      } else if(this.errorCheck(this.myFormat == null, "AudioFormat null in method \'queueBuffer\'")) {
         return false;
      } else {
         this.streamBuffers.add(new SoundBuffer(buffer, this.myFormat));
         this.processBuffer();
         this.processed = 0;
         return true;
      }
   }

   public boolean processBuffer() {
      if(this.errorCheck(this.channelType != 1, "Buffers are only processed for streaming sources.")) {
         return false;
      } else if(this.errorCheck(this.sourceDataLine == null, "SourceDataLine null in method \'processBuffer\'.")) {
         return false;
      } else if(this.streamBuffers != null && !this.streamBuffers.isEmpty()) {
         SoundBuffer nextBuffer = (SoundBuffer)this.streamBuffers.remove(0);
         this.sourceDataLine.write(nextBuffer.audioData, 0, nextBuffer.audioData.length);
         if(!this.sourceDataLine.isActive()) {
            this.sourceDataLine.start();
         }

         nextBuffer.cleanup();
         nextBuffer = null;
         return true;
      } else {
         return false;
      }
   }

   public int feedRawAudioData(byte[] buffer) {
      if(this.errorCheck(this.channelType != 1, "Raw audio data can only be processed by streaming sources.")) {
         return -1;
      } else if(this.errorCheck(this.streamBuffers == null, "StreamBuffers queue null in method \'feedRawAudioData\'.")) {
         return -1;
      } else {
         this.streamBuffers.add(new SoundBuffer(buffer, this.myFormat));
         return this.buffersProcessed();
      }
   }

   public int buffersProcessed() {
      this.processed = 0;
      if(this.errorCheck(this.channelType != 1, "Buffers may only be queued for streaming sources.")) {
         if(this.streamBuffers != null) {
            this.streamBuffers.clear();
         }

         return 0;
      } else if(this.sourceDataLine == null) {
         if(this.streamBuffers != null) {
            this.streamBuffers.clear();
         }

         return 0;
      } else {
         if(this.sourceDataLine.available() > 0) {
            this.processed = 1;
         }

         return this.processed;
      }
   }

   public void flush() {
      if(this.channelType == 1) {
         if(!this.errorCheck(this.sourceDataLine == null, "SourceDataLine null in method \'flush\'.")) {
            this.sourceDataLine.stop();
            this.sourceDataLine.flush();
            this.sourceDataLine.drain();
            this.streamBuffers.clear();
            this.processed = 0;
         }
      }
   }

   public void close() {
      switch(this.channelType) {
      case 0:
         if(this.clip != null) {
            this.clip.stop();
            this.clip.flush();
            this.clip.close();
         }
         break;
      case 1:
         if(this.sourceDataLine != null) {
            this.flush();
            this.sourceDataLine.close();
         }
      }

   }

   public void play() {
      switch(this.channelType) {
      case 0:
         if(this.clip != null) {
            if(this.toLoop) {
               this.clip.stop();
               this.clip.loop(-1);
            } else {
               this.clip.stop();
               this.clip.start();
            }
         }
         break;
      case 1:
         if(this.sourceDataLine != null) {
            this.sourceDataLine.start();
         }
      }

   }

   public void pause() {
      switch(this.channelType) {
      case 0:
         if(this.clip != null) {
            this.clip.stop();
         }
         break;
      case 1:
         if(this.sourceDataLine != null) {
            this.sourceDataLine.stop();
         }
      }

   }

   public void stop() {
      switch(this.channelType) {
      case 0:
         if(this.clip != null) {
            this.clip.stop();
            this.clip.setFramePosition(0);
         }
         break;
      case 1:
         if(this.sourceDataLine != null) {
            this.sourceDataLine.stop();
         }
      }

   }

   public void rewind() {
      switch(this.channelType) {
      case 0:
         if(this.clip != null) {
            boolean rePlay = this.clip.isRunning();
            this.clip.stop();
            this.clip.setFramePosition(0);
            if(rePlay) {
               if(this.toLoop) {
                  this.clip.loop(-1);
               } else {
                  this.clip.start();
               }
            }
         }
      case 1:
      default:
      }
   }

   public float millisecondsPlayed() {
      switch(this.channelType) {
      case 0:
         if(this.clip == null) {
            return -1.0F;
         }

         return (float)this.clip.getMicrosecondPosition() / 1000.0F;
      case 1:
         if(this.sourceDataLine == null) {
            return -1.0F;
         }

         return (float)this.sourceDataLine.getMicrosecondPosition() / 1000.0F;
      default:
         return -1.0F;
      }
   }

   public boolean playing() {
      switch(this.channelType) {
      case 0:
         if(this.clip == null) {
            return false;
         }

         return this.clip.isActive();
      case 1:
         if(this.sourceDataLine == null) {
            return false;
         }

         return this.sourceDataLine.isActive();
      default:
         return false;
      }
   }
}
