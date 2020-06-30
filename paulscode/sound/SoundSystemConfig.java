package paulscode.sound;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Locale;
import paulscode.sound.ICodec;
import paulscode.sound.IStreamListener;
import paulscode.sound.Library;
import paulscode.sound.SoundSystemException;
import paulscode.sound.SoundSystemLogger;

public class SoundSystemConfig {
   public static final Object THREAD_SYNC = new Object();
   public static final int TYPE_NORMAL = 0;
   public static final int TYPE_STREAMING = 1;
   public static final int ATTENUATION_NONE = 0;
   public static final int ATTENUATION_ROLLOFF = 1;
   public static final int ATTENUATION_LINEAR = 2;
   public static String EXTENSION_MIDI = ".*[mM][iI][dD][iI]?$";
   public static String PREFIX_URL = "^[hH][tT][tT][pP]://.*";
   private static SoundSystemLogger logger = null;
   private static LinkedList libraries;
   private static LinkedList codecs = null;
   private static LinkedList streamListeners = null;
   private static final Object streamListenersLock = new Object();
   private static int numberNormalChannels = 28;
   private static int numberStreamingChannels = 4;
   private static float masterGain = 1.0F;
   private static int defaultAttenuationModel = 1;
   private static float defaultRolloffFactor = 0.03F;
   private static float dopplerFactor = 0.0F;
   private static float dopplerVelocity = 1.0F;
   private static float defaultFadeDistance = 1000.0F;
   private static String soundFilesPackage = "Sounds/";
   private static int streamingBufferSize = 131072;
   private static int numberStreamingBuffers = 3;
   private static boolean streamQueueFormatsMatch = false;
   private static int maxFileSize = 268435456;
   private static int fileChunkSize = 1048576;
   private static boolean midiCodec = false;
   private static String overrideMIDISynthesizer = "";

   public static void addLibrary(Class libraryClass) throws SoundSystemException {
      if(libraryClass == null) {
         throw new SoundSystemException("Parameter null in method \'addLibrary\'", 2);
      } else if(!Library.class.isAssignableFrom(libraryClass)) {
         throw new SoundSystemException("The specified class does not extend class \'Library\' in method \'addLibrary\'");
      } else {
         if(libraries == null) {
            libraries = new LinkedList();
         }

         if(!libraries.contains(libraryClass)) {
            libraries.add(libraryClass);
         }

      }
   }

   public static void removeLibrary(Class libraryClass) throws SoundSystemException {
      if(libraries != null && libraryClass != null) {
         libraries.remove(libraryClass);
      }
   }

   public static LinkedList getLibraries() {
      return libraries;
   }

   public static boolean libraryCompatible(Class libraryClass) {
      if(libraryClass == null) {
         errorMessage("Parameter \'libraryClass\' null in method\'librayCompatible\'");
         return false;
      } else if(!Library.class.isAssignableFrom(libraryClass)) {
         errorMessage("The specified class does not extend class \'Library\' in method \'libraryCompatible\'");
         return false;
      } else {
         Object o = runMethod(libraryClass, "libraryCompatible", new Class[0], new Object[0]);
         if(o == null) {
            errorMessage("Method \'Library.libraryCompatible\' returned \'null\' in method \'libraryCompatible\'");
            return false;
         } else {
            return ((Boolean)o).booleanValue();
         }
      }
   }

   public static String getLibraryTitle(Class libraryClass) {
      if(libraryClass == null) {
         errorMessage("Parameter \'libraryClass\' null in method\'getLibrayTitle\'");
         return null;
      } else if(!Library.class.isAssignableFrom(libraryClass)) {
         errorMessage("The specified class does not extend class \'Library\' in method \'getLibraryTitle\'");
         return null;
      } else {
         Object o = runMethod(libraryClass, "getTitle", new Class[0], new Object[0]);
         if(o == null) {
            errorMessage("Method \'Library.getTitle\' returned \'null\' in method \'getLibraryTitle\'");
            return null;
         } else {
            return (String)o;
         }
      }
   }

   public static String getLibraryDescription(Class libraryClass) {
      if(libraryClass == null) {
         errorMessage("Parameter \'libraryClass\' null in method\'getLibrayDescription\'");
         return null;
      } else if(!Library.class.isAssignableFrom(libraryClass)) {
         errorMessage("The specified class does not extend class \'Library\' in method \'getLibraryDescription\'");
         return null;
      } else {
         Object o = runMethod(libraryClass, "getDescription", new Class[0], new Object[0]);
         if(o == null) {
            errorMessage("Method \'Library.getDescription\' returned \'null\' in method \'getLibraryDescription\'");
            return null;
         } else {
            return (String)o;
         }
      }
   }

   public static boolean reverseByteOrder(Class libraryClass) {
      if(libraryClass == null) {
         errorMessage("Parameter \'libraryClass\' null in method\'reverseByteOrder\'");
         return false;
      } else if(!Library.class.isAssignableFrom(libraryClass)) {
         errorMessage("The specified class does not extend class \'Library\' in method \'reverseByteOrder\'");
         return false;
      } else {
         Object o = runMethod(libraryClass, "reversByteOrder", new Class[0], new Object[0]);
         if(o == null) {
            errorMessage("Method \'Library.reverseByteOrder\' returned \'null\' in method \'getLibraryDescription\'");
            return false;
         } else {
            return ((Boolean)o).booleanValue();
         }
      }
   }

   public static void setLogger(SoundSystemLogger l) {
      logger = l;
   }

   public static SoundSystemLogger getLogger() {
      return logger;
   }

   public static synchronized void setNumberNormalChannels(int number) {
      numberNormalChannels = number;
   }

   public static synchronized int getNumberNormalChannels() {
      return numberNormalChannels;
   }

   public static synchronized void setNumberStreamingChannels(int number) {
      numberStreamingChannels = number;
   }

   public static synchronized int getNumberStreamingChannels() {
      return numberStreamingChannels;
   }

   public static synchronized void setMasterGain(float value) {
      masterGain = value;
   }

   public static synchronized float getMasterGain() {
      return masterGain;
   }

   public static synchronized void setDefaultAttenuation(int model) {
      defaultAttenuationModel = model;
   }

   public static synchronized int getDefaultAttenuation() {
      return defaultAttenuationModel;
   }

   public static synchronized void setDefaultRolloff(float rolloff) {
      defaultRolloffFactor = rolloff;
   }

   public static synchronized float getDopplerFactor() {
      return dopplerFactor;
   }

   public static synchronized void setDopplerFactor(float factor) {
      dopplerFactor = factor;
   }

   public static synchronized float getDopplerVelocity() {
      return dopplerVelocity;
   }

   public static synchronized void setDopplerVelocity(float velocity) {
      dopplerVelocity = velocity;
   }

   public static synchronized float getDefaultRolloff() {
      return defaultRolloffFactor;
   }

   public static synchronized void setDefaultFadeDistance(float distance) {
      defaultFadeDistance = distance;
   }

   public static synchronized float getDefaultFadeDistance() {
      return defaultFadeDistance;
   }

   public static synchronized void setSoundFilesPackage(String location) {
      soundFilesPackage = location;
   }

   public static synchronized String getSoundFilesPackage() {
      return soundFilesPackage;
   }

   public static synchronized void setStreamingBufferSize(int size) {
      streamingBufferSize = size;
   }

   public static synchronized int getStreamingBufferSize() {
      return streamingBufferSize;
   }

   public static synchronized void setNumberStreamingBuffers(int num) {
      numberStreamingBuffers = num;
   }

   public static synchronized int getNumberStreamingBuffers() {
      return numberStreamingBuffers;
   }

   public static synchronized void setStreamQueueFormatsMatch(boolean val) {
      streamQueueFormatsMatch = val;
   }

   public static synchronized boolean getStreamQueueFormatsMatch() {
      return streamQueueFormatsMatch;
   }

   public static synchronized void setMaxFileSize(int size) {
      maxFileSize = size;
   }

   public static synchronized int getMaxFileSize() {
      return maxFileSize;
   }

   public static synchronized void setFileChunkSize(int size) {
      fileChunkSize = size;
   }

   public static synchronized int getFileChunkSize() {
      return fileChunkSize;
   }

   public static synchronized String getOverrideMIDISynthesizer() {
      return overrideMIDISynthesizer;
   }

   public static synchronized void setOverrideMIDISynthesizer(String name) {
      overrideMIDISynthesizer = name;
   }

   public static synchronized void setCodec(String extension, Class iCodecClass) throws SoundSystemException {
      if(extension == null) {
         throw new SoundSystemException("Parameter \'extension\' null in method \'setCodec\'.", 2);
      } else if(iCodecClass == null) {
         throw new SoundSystemException("Parameter \'iCodecClass\' null in method \'setCodec\'.", 2);
      } else if(!ICodec.class.isAssignableFrom(iCodecClass)) {
         throw new SoundSystemException("The specified class does not implement interface \'ICodec\' in method \'setCodec\'", 3);
      } else {
         if(codecs == null) {
            codecs = new LinkedList();
         }

         ListIterator<SoundSystemConfig.Codec> i = codecs.listIterator();

         while(i.hasNext()) {
            SoundSystemConfig.Codec codec = (SoundSystemConfig.Codec)i.next();
            if(extension.matches(codec.extensionRegX)) {
               i.remove();
            }
         }

         codecs.add(new SoundSystemConfig.Codec(extension, iCodecClass));
         if(extension.matches(EXTENSION_MIDI)) {
            midiCodec = true;
         }

      }
   }

   public static synchronized ICodec getCodec(String filename) {
      if(codecs == null) {
         return null;
      } else {
         for(SoundSystemConfig.Codec codec : codecs) {
            if(filename.matches(codec.extensionRegX)) {
               return codec.getInstance();
            }
         }

         return null;
      }
   }

   public static boolean midiCodec() {
      return midiCodec;
   }

   public static void addStreamListener(IStreamListener streamListener) {
      synchronized(streamListenersLock) {
         if(streamListeners == null) {
            streamListeners = new LinkedList();
         }

         if(!streamListeners.contains(streamListener)) {
            streamListeners.add(streamListener);
         }

      }
   }

   public static void removeStreamListener(IStreamListener streamListener) {
      synchronized(streamListenersLock) {
         if(streamListeners == null) {
            streamListeners = new LinkedList();
         }

         if(streamListeners.contains(streamListener)) {
            streamListeners.remove(streamListener);
         }

      }
   }

   public static void notifyEOS(final String sourcename, final int queueSize) {
      synchronized(streamListenersLock) {
         if(streamListeners == null) {
            return;
         }
      }

      (new Thread() {
         public void run() {
            synchronized(SoundSystemConfig.streamListenersLock) {
               if(SoundSystemConfig.streamListeners != null) {
                  ListIterator<IStreamListener> i = SoundSystemConfig.streamListeners.listIterator();

                  while(i.hasNext()) {
                     IStreamListener streamListener = (IStreamListener)i.next();
                     if(streamListener == null) {
                        i.remove();
                     } else {
                        streamListener.endOfStream(sourcename, queueSize);
                     }
                  }

               }
            }
         }
      }).start();
   }

   private static void errorMessage(String message) {
      if(logger != null) {
         logger.errorMessage("SoundSystemConfig", message, 0);
      }

   }

   private static Object runMethod(Class c, String method, Class[] paramTypes, Object[] params) {
      Method m = null;

      try {
         m = c.getMethod(method, paramTypes);
      } catch (NoSuchMethodException var12) {
         errorMessage("NoSuchMethodException thrown when attempting to call method \'" + method + "\' in " + "method \'runMethod\'");
         return null;
      } catch (SecurityException var13) {
         errorMessage("Access denied when attempting to call method \'" + method + "\' in method \'runMethod\'");
         return null;
      } catch (NullPointerException var14) {
         errorMessage("NullPointerException thrown when attempting to call method \'" + method + "\' in " + "method \'runMethod\'");
         return null;
      }

      if(m == null) {
         errorMessage("Method \'" + method + "\' not found for the class " + "specified in method \'runMethod\'");
         return null;
      } else {
         Object o = null;

         try {
            o = m.invoke((Object)null, params);
            return o;
         } catch (IllegalAccessException var7) {
            errorMessage("IllegalAccessException thrown when attempting to invoke method \'" + method + "\' in " + "method \'runMethod\'");
            return null;
         } catch (IllegalArgumentException var8) {
            errorMessage("IllegalArgumentException thrown when attempting to invoke method \'" + method + "\' in " + "method \'runMethod\'");
            return null;
         } catch (InvocationTargetException var9) {
            errorMessage("InvocationTargetException thrown while attempting to invoke method \'Library.getTitle\' in method \'getLibraryTitle\'");
            return null;
         } catch (NullPointerException var10) {
            errorMessage("NullPointerException thrown when attempting to invoke method \'" + method + "\' in " + "method \'runMethod\'");
            return null;
         } catch (ExceptionInInitializerError var11) {
            errorMessage("ExceptionInInitializerError thrown when attempting to invoke method \'" + method + "\' in " + "method \'runMethod\'");
            return null;
         }
      }
   }

   private static class Codec {
      public String extensionRegX = "";
      public Class iCodecClass;

      public Codec(String extension, Class iCodecClass) {
         if(extension != null && extension.length() > 0) {
            this.extensionRegX = ".*";

            for(int x = 0; x < extension.length(); ++x) {
               String c = extension.substring(x, x + 1);
               this.extensionRegX = this.extensionRegX + "[" + c.toLowerCase(Locale.ENGLISH) + c.toUpperCase(Locale.ENGLISH) + "]";
            }

            this.extensionRegX = this.extensionRegX + "$";
         }

         this.iCodecClass = iCodecClass;
      }

      public ICodec getInstance() {
         if(this.iCodecClass == null) {
            return null;
         } else {
            Object o = null;

            try {
               o = this.iCodecClass.newInstance();
            } catch (InstantiationException var3) {
               this.instantiationErrorMessage();
               return null;
            } catch (IllegalAccessException var4) {
               this.instantiationErrorMessage();
               return null;
            } catch (ExceptionInInitializerError var5) {
               this.instantiationErrorMessage();
               return null;
            } catch (SecurityException var6) {
               this.instantiationErrorMessage();
               return null;
            }

            if(o == null) {
               this.instantiationErrorMessage();
               return null;
            } else {
               return (ICodec)o;
            }
         }
      }

      private void instantiationErrorMessage() {
         SoundSystemConfig.errorMessage("Unrecognized ICodec implementation in method \'getInstance\'.  Ensure that the implementing class has one public, parameterless constructor.");
      }
   }
}
