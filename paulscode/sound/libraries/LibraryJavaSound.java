package paulscode.sound.libraries;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.Mixer.Info;
import paulscode.sound.Channel;
import paulscode.sound.FilenameURL;
import paulscode.sound.ICodec;
import paulscode.sound.Library;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.Source;
import paulscode.sound.libraries.ChannelJavaSound;
import paulscode.sound.libraries.SourceJavaSound;

public class LibraryJavaSound extends Library {
   private static final boolean GET = false;
   private static final boolean SET = true;
   private static final int XXX = 0;
   private final int maxClipSize = 1048576;
   private static Mixer myMixer = null;
   private static LibraryJavaSound.MixerRanking myMixerRanking = null;
   private static LibraryJavaSound instance = null;
   private static int minSampleRate = 4000;
   private static int maxSampleRate = '뮀';
   private static int lineCount = 32;
   private static boolean useGainControl = true;
   private static boolean usePanControl = true;
   private static boolean useSampleRateControl = true;

   public LibraryJavaSound() throws SoundSystemException {
      instance = this;
   }

   public void init() throws SoundSystemException {
      LibraryJavaSound.MixerRanking mixerRanker = null;
      if(myMixer == null) {
         for(Info mixerInfo : AudioSystem.getMixerInfo()) {
            if(mixerInfo.getName().equals("Java Sound Audio Engine")) {
               mixerRanker = new LibraryJavaSound.MixerRanking();

               try {
                  mixerRanker.rank(mixerInfo);
               } catch (LibraryJavaSound.Exception var10) {
                  break;
               }

               if(mixerRanker.rank >= 14) {
                  myMixer = AudioSystem.getMixer(mixerInfo);
                  mixerRanking(true, mixerRanker);
               }
               break;
            }
         }

         if(myMixer == null) {
            LibraryJavaSound.MixerRanking bestRankedMixer = mixerRanker;

            for(Info mixerInfo : AudioSystem.getMixerInfo()) {
               mixerRanker = new LibraryJavaSound.MixerRanking();

               try {
                  mixerRanker.rank(mixerInfo);
               } catch (LibraryJavaSound.Exception var9) {
                  ;
               }

               if(bestRankedMixer == null || mixerRanker.rank > bestRankedMixer.rank) {
                  bestRankedMixer = mixerRanker;
               }
            }

            if(bestRankedMixer == null) {
               throw new LibraryJavaSound.Exception("No useable mixers found!", new LibraryJavaSound.MixerRanking());
            }

            try {
               myMixer = AudioSystem.getMixer(bestRankedMixer.mixerInfo);
               mixerRanking(true, bestRankedMixer);
            } catch (java.lang.Exception var8) {
               throw new LibraryJavaSound.Exception("No useable mixers available!", new LibraryJavaSound.MixerRanking());
            }
         }
      }

      this.setMasterVolume(1.0F);
      this.message("JavaSound initialized.");
      super.init();
   }

   public static boolean libraryCompatible() {
      for(Info mixerInfo : AudioSystem.getMixerInfo()) {
         if(mixerInfo.getName().equals("Java Sound Audio Engine")) {
            return true;
         }
      }

      return false;
   }

   protected Channel createChannel(int type) {
      return new ChannelJavaSound(type, myMixer);
   }

   public void cleanup() {
      super.cleanup();
      instance = null;
      myMixer = null;
      myMixerRanking = null;
   }

   public boolean loadSound(FilenameURL filenameURL) {
      if(this.bufferMap == null) {
         this.bufferMap = new HashMap();
         this.importantMessage("Buffer Map was null in method \'loadSound\'");
      }

      if(this.errorCheck(filenameURL == null, "Filename/URL not specified in method \'loadSound\'")) {
         return false;
      } else if(this.bufferMap.get(filenameURL.getFilename()) != null) {
         return true;
      } else {
         ICodec codec = SoundSystemConfig.getCodec(filenameURL.getFilename());
         if(this.errorCheck(codec == null, "No codec found for file \'" + filenameURL.getFilename() + "\' in method \'loadSound\'")) {
            return false;
         } else {
            URL url = filenameURL.getURL();
            if(this.errorCheck(url == null, "Unable to open file \'" + filenameURL.getFilename() + "\' in method \'loadSound\'")) {
               return false;
            } else {
               codec.initialize(url);
               SoundBuffer buffer = codec.readAll();
               codec.cleanup();
               codec = null;
               if(buffer != null) {
                  this.bufferMap.put(filenameURL.getFilename(), buffer);
               } else {
                  this.errorMessage("Sound buffer null in method \'loadSound\'");
               }

               return true;
            }
         }
      }
   }

   public boolean loadSound(SoundBuffer buffer, String identifier) {
      if(this.bufferMap == null) {
         this.bufferMap = new HashMap();
         this.importantMessage("Buffer Map was null in method \'loadSound\'");
      }

      if(this.errorCheck(identifier == null, "Identifier not specified in method \'loadSound\'")) {
         return false;
      } else if(this.bufferMap.get(identifier) != null) {
         return true;
      } else {
         if(buffer != null) {
            this.bufferMap.put(identifier, buffer);
         } else {
            this.errorMessage("Sound buffer null in method \'loadSound\'");
         }

         return true;
      }
   }

   public void setMasterVolume(float value) {
      super.setMasterVolume(value);

      for(String sourcename : this.sourceMap.keySet()) {
         Source source = (Source)this.sourceMap.get(sourcename);
         if(source != null) {
            source.positionChanged();
         }
      }

   }

   public void newSource(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll) {
      SoundBuffer buffer = null;
      if(!toStream) {
         buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
         if(buffer == null && !this.loadSound(filenameURL)) {
            this.errorMessage("Source \'" + sourcename + "\' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
            return;
         }

         buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
         if(buffer == null) {
            this.errorMessage("Source \'" + sourcename + "\' was not created " + "because audio data was not found for " + filenameURL.getFilename());
            return;
         }
      }

      if(!toStream && buffer != null) {
         buffer.trimData(1048576);
      }

      this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, false));
   }

   public void rawDataStream(AudioFormat audioFormat, boolean priority, String sourcename, float x, float y, float z, int attModel, float distOrRoll) {
      this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, audioFormat, priority, sourcename, x, y, z, attModel, distOrRoll));
   }

   public void quickPlay(boolean priority, boolean toStream, boolean toLoop, String sourcename, FilenameURL filenameURL, float x, float y, float z, int attModel, float distOrRoll, boolean temporary) {
      SoundBuffer buffer = null;
      if(!toStream) {
         buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
         if(buffer == null && !this.loadSound(filenameURL)) {
            this.errorMessage("Source \'" + sourcename + "\' was not created " + "because an error occurred while loading " + filenameURL.getFilename());
            return;
         }

         buffer = (SoundBuffer)this.bufferMap.get(filenameURL.getFilename());
         if(buffer == null) {
            this.errorMessage("Source \'" + sourcename + "\' was not created " + "because audio data was not found for " + filenameURL.getFilename());
            return;
         }
      }

      if(!toStream && buffer != null) {
         buffer.trimData(1048576);
      }

      this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, priority, toStream, toLoop, sourcename, filenameURL, buffer, x, y, z, attModel, distOrRoll, temporary));
   }

   public void copySources(HashMap srcMap) {
      if(srcMap != null) {
         Set<String> keys = srcMap.keySet();
         Iterator<String> iter = keys.iterator();
         if(this.bufferMap == null) {
            this.bufferMap = new HashMap();
            this.importantMessage("Buffer Map was null in method \'copySources\'");
         }

         this.sourceMap.clear();

         while(iter.hasNext()) {
            String sourcename = (String)iter.next();
            Source source = (Source)srcMap.get(sourcename);
            if(source != null) {
               SoundBuffer buffer = null;
               if(!source.toStream) {
                  this.loadSound(source.filenameURL);
                  buffer = (SoundBuffer)this.bufferMap.get(source.filenameURL.getFilename());
               }

               if(!source.toStream && buffer != null) {
                  buffer.trimData(1048576);
               }

               if(source.toStream || buffer != null) {
                  this.sourceMap.put(sourcename, new SourceJavaSound(this.listener, source, buffer));
               }
            }
         }

      }
   }

   public void setListenerVelocity(float x, float y, float z) {
      super.setListenerVelocity(x, y, z);
      this.listenerMoved();
   }

   public void dopplerChanged() {
      super.dopplerChanged();
      this.listenerMoved();
   }

   public static Mixer getMixer() {
      return mixer(false, (Mixer)null);
   }

   public static void setMixer(Mixer m) throws SoundSystemException {
      mixer(true, m);
      SoundSystemException e = SoundSystem.getLastException();
      SoundSystem.setException((SoundSystemException)null);
      if(e != null) {
         throw e;
      }
   }

   private static synchronized Mixer mixer(boolean action, Mixer m) {
      if(action) {
         if(m == null) {
            return myMixer;
         }

         LibraryJavaSound.MixerRanking mixerRanker = new LibraryJavaSound.MixerRanking();

         try {
            mixerRanker.rank(m.getMixerInfo());
         } catch (LibraryJavaSound.Exception var5) {
            SoundSystemConfig.getLogger().printStackTrace(var5, 1);
            SoundSystem.setException(var5);
         }

         myMixer = m;
         mixerRanking(true, mixerRanker);
         if(instance != null) {
            ListIterator<Channel> itr = instance.normalChannels.listIterator();
            SoundSystem.setException((SoundSystemException)null);

            while(itr.hasNext()) {
               ChannelJavaSound c = (ChannelJavaSound)itr.next();
               c.newMixer(m);
            }

            for(ChannelJavaSound c : instance.streamingChannels) {
               c.newMixer(m);
            }
         }
      }

      return myMixer;
   }

   public static LibraryJavaSound.MixerRanking getMixerRanking() {
      return mixerRanking(false, (LibraryJavaSound.MixerRanking)null);
   }

   private static synchronized LibraryJavaSound.MixerRanking mixerRanking(boolean action, LibraryJavaSound.MixerRanking value) {
      if(action) {
         myMixerRanking = value;
      }

      return myMixerRanking;
   }

   public static void setMinSampleRate(int value) {
      minSampleRate(true, value);
   }

   private static synchronized int minSampleRate(boolean action, int value) {
      if(action) {
         minSampleRate = value;
      }

      return minSampleRate;
   }

   public static void setMaxSampleRate(int value) {
      maxSampleRate(true, value);
   }

   private static synchronized int maxSampleRate(boolean action, int value) {
      if(action) {
         maxSampleRate = value;
      }

      return maxSampleRate;
   }

   public static void setLineCount(int value) {
      lineCount(true, value);
   }

   private static synchronized int lineCount(boolean action, int value) {
      if(action) {
         lineCount = value;
      }

      return lineCount;
   }

   public static void useGainControl(boolean value) {
      useGainControl(true, value);
   }

   private static synchronized boolean useGainControl(boolean action, boolean value) {
      if(action) {
         useGainControl = value;
      }

      return useGainControl;
   }

   public static void usePanControl(boolean value) {
      usePanControl(true, value);
   }

   private static synchronized boolean usePanControl(boolean action, boolean value) {
      if(action) {
         usePanControl = value;
      }

      return usePanControl;
   }

   public static void useSampleRateControl(boolean value) {
      useSampleRateControl(true, value);
   }

   private static synchronized boolean useSampleRateControl(boolean action, boolean value) {
      if(action) {
         useSampleRateControl = value;
      }

      return useSampleRateControl;
   }

   public static String getTitle() {
      return "Java Sound";
   }

   public static String getDescription() {
      return "The Java Sound API.  For more information, see http://java.sun.com/products/java-media/sound/";
   }

   public String getClassName() {
      return "LibraryJavaSound";
   }

   public static class Exception extends SoundSystemException {
      public static final int MIXER_PROBLEM = 101;
      public static LibraryJavaSound.MixerRanking mixerRanking = null;

      public Exception(String message) {
         super(message);
      }

      public Exception(String message, int type) {
         super(message, type);
      }

      public Exception(String message, LibraryJavaSound.MixerRanking rank) {
         super(message, 101);
         mixerRanking = rank;
      }
   }

   public static class MixerRanking {
      public static final int HIGH = 1;
      public static final int MEDIUM = 2;
      public static final int LOW = 3;
      public static final int NONE = 4;
      public static int MIXER_EXISTS_PRIORITY = 1;
      public static int MIN_SAMPLE_RATE_PRIORITY = 1;
      public static int MAX_SAMPLE_RATE_PRIORITY = 1;
      public static int LINE_COUNT_PRIORITY = 1;
      public static int GAIN_CONTROL_PRIORITY = 2;
      public static int PAN_CONTROL_PRIORITY = 2;
      public static int SAMPLE_RATE_CONTROL_PRIORITY = 3;
      public Info mixerInfo = null;
      public int rank = 0;
      public boolean mixerExists = false;
      public boolean minSampleRateOK = false;
      public boolean maxSampleRateOK = false;
      public boolean lineCountOK = false;
      public boolean gainControlOK = false;
      public boolean panControlOK = false;
      public boolean sampleRateControlOK = false;
      public int minSampleRatePossible = -1;
      public int maxSampleRatePossible = -1;
      public int maxLinesPossible = 0;

      public MixerRanking() {
      }

      public MixerRanking(Info i, int r, boolean e, boolean mnsr, boolean mxsr, boolean lc, boolean gc, boolean pc, boolean src) {
         this.mixerInfo = i;
         this.rank = r;
         this.mixerExists = e;
         this.minSampleRateOK = mnsr;
         this.maxSampleRateOK = mxsr;
         this.lineCountOK = lc;
         this.gainControlOK = gc;
         this.panControlOK = pc;
         this.sampleRateControlOK = src;
      }

      public void rank(Info i) throws LibraryJavaSound.Exception {
         if(i == null) {
            throw new LibraryJavaSound.Exception("No Mixer info specified in method \'MixerRanking.rank\'", this);
         } else {
            this.mixerInfo = i;

            Mixer m;
            try {
               m = AudioSystem.getMixer(this.mixerInfo);
            } catch (java.lang.Exception var17) {
               throw new LibraryJavaSound.Exception("Unable to acquire the specified Mixer in method \'MixerRanking.rank\'", this);
            }

            if(m == null) {
               throw new LibraryJavaSound.Exception("Unable to acquire the specified Mixer in method \'MixerRanking.rank\'", this);
            } else {
               this.mixerExists = true;

               javax.sound.sampled.DataLine.Info lineInfo;
               try {
                  AudioFormat format = new AudioFormat((float)LibraryJavaSound.minSampleRate(false, 0), 16, 2, true, false);
                  lineInfo = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, format);
               } catch (java.lang.Exception var16) {
                  throw new LibraryJavaSound.Exception("Invalid minimum sample-rate specified in method \'MixerRanking.rank\'", this);
               }

               if(!AudioSystem.isLineSupported(lineInfo)) {
                  if(MIN_SAMPLE_RATE_PRIORITY == 1) {
                     throw new LibraryJavaSound.Exception("Specified minimum sample-rate not possible for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                  }
               } else {
                  this.minSampleRateOK = true;
               }

               try {
                  AudioFormat format = new AudioFormat((float)LibraryJavaSound.maxSampleRate(false, 0), 16, 2, true, false);
                  lineInfo = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, format);
               } catch (java.lang.Exception var15) {
                  throw new LibraryJavaSound.Exception("Invalid maximum sample-rate specified in method \'MixerRanking.rank\'", this);
               }

               if(!AudioSystem.isLineSupported(lineInfo)) {
                  if(MAX_SAMPLE_RATE_PRIORITY == 1) {
                     throw new LibraryJavaSound.Exception("Specified maximum sample-rate not possible for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                  }
               } else {
                  this.maxSampleRateOK = true;
               }

               if(this.minSampleRateOK) {
                  this.minSampleRatePossible = LibraryJavaSound.minSampleRate(false, 0);
               } else {
                  int lL = LibraryJavaSound.minSampleRate(false, 0);
                  int uL = LibraryJavaSound.maxSampleRate(false, 0);

                  while(uL - lL > 1) {
                     int testSampleRate = lL + (uL - lL) / 2;
                     AudioFormat var21 = new AudioFormat((float)testSampleRate, 16, 2, true, false);
                     lineInfo = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, var21);
                     if(AudioSystem.isLineSupported(lineInfo)) {
                        this.minSampleRatePossible = testSampleRate;
                        uL = testSampleRate;
                     } else {
                        lL = testSampleRate;
                     }
                  }
               }

               if(this.maxSampleRateOK) {
                  this.maxSampleRatePossible = LibraryJavaSound.maxSampleRate(false, 0);
               } else if(this.minSampleRatePossible != -1) {
                  int uL = LibraryJavaSound.maxSampleRate(false, 0);
                  int lL = this.minSampleRatePossible;

                  while(uL - lL > 1) {
                     int testSampleRate = lL + (uL - lL) / 2;
                     AudioFormat var22 = new AudioFormat((float)testSampleRate, 16, 2, true, false);
                     lineInfo = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, var22);
                     if(AudioSystem.isLineSupported(lineInfo)) {
                        this.maxSampleRatePossible = testSampleRate;
                        lL = testSampleRate;
                     } else {
                        uL = testSampleRate;
                     }
                  }
               }

               if(this.minSampleRatePossible != -1 && this.maxSampleRatePossible != -1) {
                  AudioFormat var23 = new AudioFormat((float)this.minSampleRatePossible, 16, 2, true, false);
                  Clip clip = null;

                  try {
                     javax.sound.sampled.DataLine.Info clipLineInfo = new javax.sound.sampled.DataLine.Info(Clip.class, var23);
                     clip = (Clip)m.getLine(clipLineInfo);
                     byte[] buffer = new byte[10];
                     clip.open(var23, buffer, 0, buffer.length);
                  } catch (java.lang.Exception var14) {
                     throw new LibraryJavaSound.Exception("Unable to attach an actual audio buffer to an actual Clip... Mixer \'" + this.mixerInfo.getName() + "\' is unuseable.", this);
                  }

                  this.maxLinesPossible = 1;
                  lineInfo = new javax.sound.sampled.DataLine.Info(SourceDataLine.class, var23);
                  SourceDataLine[] lines = new SourceDataLine[LibraryJavaSound.lineCount(false, 0) - 1];
                  int c = 0;

                  for(int x = 1; x < lines.length + 1; ++x) {
                     try {
                        lines[x - 1] = (SourceDataLine)m.getLine(lineInfo);
                     } catch (java.lang.Exception var18) {
                        if(x == 0) {
                           throw new LibraryJavaSound.Exception("No output lines possible for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                        }

                        if(LINE_COUNT_PRIORITY == 1) {
                           throw new LibraryJavaSound.Exception("Specified maximum number of lines not possible for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                        }
                        break;
                     }

                     this.maxLinesPossible = x + 1;
                  }

                  try {
                     clip.close();
                  } catch (java.lang.Exception var13) {
                     ;
                  }

                  clip = null;
                  if(this.maxLinesPossible == LibraryJavaSound.lineCount(false, 0)) {
                     this.lineCountOK = true;
                  }

                  if(!LibraryJavaSound.useGainControl(false, false)) {
                     GAIN_CONTROL_PRIORITY = 4;
                  } else if(!lines[0].isControlSupported(Type.MASTER_GAIN)) {
                     if(GAIN_CONTROL_PRIORITY == 1) {
                        throw new LibraryJavaSound.Exception("Gain control not available for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                     }
                  } else {
                     this.gainControlOK = true;
                  }

                  if(!LibraryJavaSound.usePanControl(false, false)) {
                     PAN_CONTROL_PRIORITY = 4;
                  } else if(!lines[0].isControlSupported(Type.PAN)) {
                     if(PAN_CONTROL_PRIORITY == 1) {
                        throw new LibraryJavaSound.Exception("Pan control not available for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                     }
                  } else {
                     this.panControlOK = true;
                  }

                  if(!LibraryJavaSound.useSampleRateControl(false, false)) {
                     SAMPLE_RATE_CONTROL_PRIORITY = 4;
                  } else if(!lines[0].isControlSupported(Type.SAMPLE_RATE)) {
                     if(SAMPLE_RATE_CONTROL_PRIORITY == 1) {
                        throw new LibraryJavaSound.Exception("Sample-rate control not available for Mixer \'" + this.mixerInfo.getName() + "\'", this);
                     }
                  } else {
                     this.sampleRateControlOK = true;
                  }

                  this.rank += this.getRankValue(this.mixerExists, MIXER_EXISTS_PRIORITY);
                  this.rank += this.getRankValue(this.minSampleRateOK, MIN_SAMPLE_RATE_PRIORITY);
                  this.rank += this.getRankValue(this.maxSampleRateOK, MAX_SAMPLE_RATE_PRIORITY);
                  this.rank += this.getRankValue(this.lineCountOK, LINE_COUNT_PRIORITY);
                  this.rank += this.getRankValue(this.gainControlOK, GAIN_CONTROL_PRIORITY);
                  this.rank += this.getRankValue(this.panControlOK, PAN_CONTROL_PRIORITY);
                  this.rank += this.getRankValue(this.sampleRateControlOK, SAMPLE_RATE_CONTROL_PRIORITY);
                  m = null;
                  var23 = null;
                  lineInfo = null;
                  Object lines = null;
               } else {
                  throw new LibraryJavaSound.Exception("No possible sample-rate found for Mixer \'" + this.mixerInfo.getName() + "\'", this);
               }
            }
         }
      }

      private int getRankValue(boolean property, int priority) {
         return property?2:(priority == 4?2:(priority == 3?1:0));
      }
   }
}
