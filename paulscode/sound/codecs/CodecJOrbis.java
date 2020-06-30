package paulscode.sound.codecs;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;
import javax.sound.sampled.AudioFormat;
import paulscode.sound.ICodec;
import paulscode.sound.SoundBuffer;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemLogger;

public class CodecJOrbis implements ICodec {
   private static final boolean GET = false;
   private static final boolean SET = true;
   private static final boolean XXX = false;
   private URL url;
   private URLConnection urlConnection = null;
   private InputStream inputStream;
   private AudioFormat audioFormat;
   private boolean endOfStream = false;
   private boolean initialized = false;
   private byte[] buffer = null;
   private int bufferSize;
   private int count = 0;
   private int index = 0;
   private int convertedBufferSize;
   private byte[] convertedBuffer = null;
   private float[][][] pcmInfo;
   private int[] pcmIndex;
   private Packet joggPacket = new Packet();
   private Page joggPage = new Page();
   private StreamState joggStreamState = new StreamState();
   private SyncState joggSyncState = new SyncState();
   private DspState jorbisDspState = new DspState();
   private Block jorbisBlock;
   private Comment jorbisComment;
   private Info jorbisInfo;
   private SoundSystemLogger logger;

   public CodecJOrbis() {
      this.jorbisBlock = new Block(this.jorbisDspState);
      this.jorbisComment = new Comment();
      this.jorbisInfo = new Info();
      this.logger = SoundSystemConfig.getLogger();
   }

   public void reverseByteOrder(boolean b) {
   }

   public boolean initialize(URL url) {
      this.initialized(true, false);
      if(this.joggStreamState != null) {
         this.joggStreamState.clear();
      }

      if(this.jorbisBlock != null) {
         this.jorbisBlock.clear();
      }

      if(this.jorbisDspState != null) {
         this.jorbisDspState.clear();
      }

      if(this.jorbisInfo != null) {
         this.jorbisInfo.clear();
      }

      if(this.joggSyncState != null) {
         this.joggSyncState.clear();
      }

      if(this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var7) {
            ;
         }
      }

      this.url = url;
      this.bufferSize = 8192;
      this.buffer = null;
      this.count = 0;
      this.index = 0;
      this.joggStreamState = new StreamState();
      this.jorbisBlock = new Block(this.jorbisDspState);
      this.jorbisDspState = new DspState();
      this.jorbisInfo = new Info();
      this.joggSyncState = new SyncState();

      try {
         this.urlConnection = url.openConnection();
      } catch (UnknownServiceException var5) {
         this.errorMessage("Unable to create a UrlConnection in method \'initialize\'.");
         this.printStackTrace(var5);
         this.cleanup();
         return false;
      } catch (IOException var6) {
         this.errorMessage("Unable to create a UrlConnection in method \'initialize\'.");
         this.printStackTrace(var6);
         this.cleanup();
         return false;
      }

      if(this.urlConnection != null) {
         try {
            this.inputStream = this.urlConnection.getInputStream();
         } catch (IOException var4) {
            this.errorMessage("Unable to acquire inputstream in method \'initialize\'.");
            this.printStackTrace(var4);
            this.cleanup();
            return false;
         }
      }

      this.endOfStream(true, false);
      this.joggSyncState.init();
      this.joggSyncState.buffer(this.bufferSize);
      this.buffer = this.joggSyncState.data;

      try {
         if(!this.readHeader()) {
            this.errorMessage("Error reading the header");
            return false;
         }
      } catch (IOException var8) {
         this.errorMessage("Error reading the header");
         return false;
      }

      this.convertedBufferSize = this.bufferSize * 2;
      this.jorbisDspState.synthesis_init(this.jorbisInfo);
      this.jorbisBlock.init(this.jorbisDspState);
      int channels = this.jorbisInfo.channels;
      int rate = this.jorbisInfo.rate;
      this.audioFormat = new AudioFormat((float)rate, 16, channels, true, false);
      this.pcmInfo = new float[1][][];
      this.pcmIndex = new int[this.jorbisInfo.channels];
      this.initialized(true, true);
      return true;
   }

   public boolean initialized() {
      return this.initialized(false, false);
   }

   public SoundBuffer read() {
      byte[] returnBuffer = null;

      while(!this.endOfStream(false, false) && (returnBuffer == null || returnBuffer.length < SoundSystemConfig.getStreamingBufferSize())) {
         if(returnBuffer == null) {
            returnBuffer = this.readBytes();
         } else {
            returnBuffer = appendByteArrays(returnBuffer, this.readBytes());
         }
      }

      return returnBuffer == null?null:new SoundBuffer(returnBuffer, this.audioFormat);
   }

   public SoundBuffer readAll() {
      byte[] returnBuffer = null;

      while(!this.endOfStream(false, false)) {
         if(returnBuffer == null) {
            returnBuffer = this.readBytes();
         } else {
            returnBuffer = appendByteArrays(returnBuffer, this.readBytes());
         }
      }

      if(returnBuffer == null) {
         return null;
      } else {
         return new SoundBuffer(returnBuffer, this.audioFormat);
      }
   }

   public boolean endOfStream() {
      return this.endOfStream(false, false);
   }

   public void cleanup() {
      this.joggStreamState.clear();
      this.jorbisBlock.clear();
      this.jorbisDspState.clear();
      this.jorbisInfo.clear();
      this.joggSyncState.clear();
      if(this.inputStream != null) {
         try {
            this.inputStream.close();
         } catch (IOException var2) {
            ;
         }
      }

      this.joggStreamState = null;
      this.jorbisBlock = null;
      this.jorbisDspState = null;
      this.jorbisInfo = null;
      this.joggSyncState = null;
      this.inputStream = null;
   }

   public AudioFormat getAudioFormat() {
      return this.audioFormat;
   }

   private boolean readHeader() throws IOException {
      this.index = this.joggSyncState.buffer(this.bufferSize);
      int bytes = this.inputStream.read(this.joggSyncState.data, this.index, this.bufferSize);
      if(bytes < 0) {
         bytes = 0;
      }

      this.joggSyncState.wrote(bytes);
      if(this.joggSyncState.pageout(this.joggPage) != 1) {
         if(bytes < this.bufferSize) {
            return true;
         } else {
            this.errorMessage("Ogg header not recognized in method \'readHeader\'.");
            return false;
         }
      } else {
         this.joggStreamState.init(this.joggPage.serialno());
         this.jorbisInfo.init();
         this.jorbisComment.init();
         if(this.joggStreamState.pagein(this.joggPage) < 0) {
            this.errorMessage("Problem with first Ogg header page in method \'readHeader\'.");
            return false;
         } else if(this.joggStreamState.packetout(this.joggPacket) != 1) {
            this.errorMessage("Problem with first Ogg header packet in method \'readHeader\'.");
            return false;
         } else if(this.jorbisInfo.synthesis_headerin(this.jorbisComment, this.joggPacket) < 0) {
            this.errorMessage("File does not contain Vorbis header in method \'readHeader\'.");
            return false;
         } else {
            int i = 0;

            while(i < 2) {
               while(i < 2) {
                  int result = this.joggSyncState.pageout(this.joggPage);
                  if(result == 0) {
                     break;
                  }

                  if(result == 1) {
                     this.joggStreamState.pagein(this.joggPage);

                     while(i < 2) {
                        result = this.joggStreamState.packetout(this.joggPacket);
                        if(result == 0) {
                           break;
                        }

                        if(result == -1) {
                           this.errorMessage("Secondary Ogg header corrupt in method \'readHeader\'.");
                           return false;
                        }

                        this.jorbisInfo.synthesis_headerin(this.jorbisComment, this.joggPacket);
                        ++i;
                     }
                  }
               }

               this.index = this.joggSyncState.buffer(this.bufferSize);
               bytes = this.inputStream.read(this.joggSyncState.data, this.index, this.bufferSize);
               if(bytes < 0) {
                  bytes = 0;
               }

               if(bytes == 0 && i < 2) {
                  this.errorMessage("End of file reached before finished readingOgg header in method \'readHeader\'");
                  return false;
               }

               this.joggSyncState.wrote(bytes);
            }

            this.index = this.joggSyncState.buffer(this.bufferSize);
            this.buffer = this.joggSyncState.data;
            return true;
         }
      }
   }

   private byte[] readBytes() {
      if(!this.initialized(false, false)) {
         return null;
      } else if(this.endOfStream(false, false)) {
         return null;
      } else {
         if(this.convertedBuffer == null) {
            this.convertedBuffer = new byte[this.convertedBufferSize];
         }

         byte[] returnBuffer = null;
         switch(this.joggSyncState.pageout(this.joggPage)) {
         default:
            this.joggStreamState.pagein(this.joggPage);
            if(this.joggPage.granulepos() == 0L) {
               this.endOfStream(true, true);
               return null;
            } else {
               label116:
               while(true) {
                  switch(this.joggStreamState.packetout(this.joggPacket)) {
                  case -1:
                     continue;
                  case 0:
                     if(this.joggPage.eos() != 0) {
                        this.endOfStream(true, true);
                     }
                     break label116;
                  }

                  if(this.jorbisBlock.synthesis(this.joggPacket) == 0) {
                     this.jorbisDspState.synthesis_blockin(this.jorbisBlock);
                  }

                  int samples;
                  while((samples = this.jorbisDspState.synthesis_pcmout(this.pcmInfo, this.pcmIndex)) > 0) {
                     float[][] pcmf = this.pcmInfo[0];
                     int bout = samples < this.convertedBufferSize?samples:this.convertedBufferSize;

                     for(int i = 0; i < this.jorbisInfo.channels; ++i) {
                        int ptr = i * 2;
                        int mono = this.pcmIndex[i];

                        for(int j = 0; j < bout; ++j) {
                           int val = (int)((double)pcmf[i][mono + j] * 32767.0D);
                           if(val > 32767) {
                              val = 32767;
                           }

                           if(val < -32768) {
                              val = -32768;
                           }

                           if(val < 0) {
                              val |= '耀';
                           }

                           this.convertedBuffer[ptr] = (byte)val;
                           this.convertedBuffer[ptr + 1] = (byte)(val >>> 8);
                           ptr += 2 * this.jorbisInfo.channels;
                        }
                     }

                     this.jorbisDspState.synthesis_read(bout);
                     returnBuffer = appendByteArrays(returnBuffer, this.convertedBuffer, 2 * this.jorbisInfo.channels * bout);
                  }
               }
            }
         case -1:
         case 0:
            if(!this.endOfStream(false, false)) {
               this.index = this.joggSyncState.buffer(this.bufferSize);
               this.buffer = this.joggSyncState.data;

               try {
                  this.count = this.inputStream.read(this.buffer, this.index, this.bufferSize);
               } catch (Exception var11) {
                  this.printStackTrace(var11);
                  return null;
               }

               if(this.count == -1) {
                  return returnBuffer;
               }

               this.joggSyncState.wrote(this.count);
               if(this.count == 0) {
                  this.endOfStream(true, true);
               }
            }

            return returnBuffer;
         }
      }
   }

   private synchronized boolean initialized(boolean action, boolean value) {
      if(action) {
         this.initialized = value;
      }

      return this.initialized;
   }

   private synchronized boolean endOfStream(boolean action, boolean value) {
      if(action) {
         this.endOfStream = value;
      }

      return this.endOfStream;
   }

   private static byte[] trimArray(byte[] array, int maxLength) {
      byte[] trimmedArray = null;
      if(array != null && array.length > maxLength) {
         trimmedArray = new byte[maxLength];
         System.arraycopy(array, 0, trimmedArray, 0, maxLength);
      }

      return trimmedArray;
   }

   private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo, int arrayTwoBytes) {
      int bytes = arrayTwoBytes;
      if(arrayTwo != null && arrayTwo.length != 0) {
         if(arrayTwo.length < arrayTwoBytes) {
            bytes = arrayTwo.length;
         }
      } else {
         bytes = 0;
      }

      if(arrayOne != null || arrayTwo != null && bytes > 0) {
         byte[] newArray;
         if(arrayOne == null) {
            newArray = new byte[bytes];
            System.arraycopy(arrayTwo, 0, newArray, 0, bytes);
            Object var7 = null;
         } else if(arrayTwo != null && bytes > 0) {
            newArray = new byte[arrayOne.length + bytes];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, bytes);
            Object var6 = null;
            Object var8 = null;
         } else {
            newArray = new byte[arrayOne.length];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            Object var5 = null;
         }

         return newArray;
      } else {
         return null;
      }
   }

   private static byte[] appendByteArrays(byte[] arrayOne, byte[] arrayTwo) {
      if(arrayOne == null && arrayTwo == null) {
         return null;
      } else {
         byte[] newArray;
         if(arrayOne == null) {
            newArray = new byte[arrayTwo.length];
            System.arraycopy(arrayTwo, 0, newArray, 0, arrayTwo.length);
            Object var5 = null;
         } else if(arrayTwo == null) {
            newArray = new byte[arrayOne.length];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            Object var3 = null;
         } else {
            newArray = new byte[arrayOne.length + arrayTwo.length];
            System.arraycopy(arrayOne, 0, newArray, 0, arrayOne.length);
            System.arraycopy(arrayTwo, 0, newArray, arrayOne.length, arrayTwo.length);
            Object var4 = null;
            Object var6 = null;
         }

         return newArray;
      }
   }

   private void errorMessage(String message) {
      this.logger.errorMessage("CodecJOrbis", message, 0);
   }

   private void printStackTrace(Exception e) {
      this.logger.printStackTrace(e, 1);
   }
}
