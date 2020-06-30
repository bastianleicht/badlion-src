package com.jcraft.jorbis;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;
import com.jcraft.jogg.StreamState;
import com.jcraft.jogg.SyncState;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import java.io.FileInputStream;
import java.io.InputStream;

class DecodeExample {
   static int convsize = 8192;
   static byte[] convbuffer = new byte[convsize];

   public static void main(String[] arg) {
      InputStream input = System.in;
      if(arg.length > 0) {
         try {
            input = new FileInputStream(arg[0]);
         } catch (Exception var28) {
            System.err.println(var28);
         }
      }

      SyncState oy = new SyncState();
      StreamState os = new StreamState();
      Page og = new Page();
      Packet op = new Packet();
      Info vi = new Info();
      Comment vc = new Comment();
      DspState vd = new DspState();
      Block vb = new Block(vd);
      int bytes = 0;
      oy.init();

      while(true) {
         int eos = 0;
         int index = oy.buffer(4096);
         byte[] buffer = oy.data;

         try {
            bytes = input.read(buffer, index, 4096);
         } catch (Exception var26) {
            System.err.println(var26);
            System.exit(-1);
         }

         oy.wrote(bytes);
         if(oy.pageout(og) != 1) {
            if(bytes < 4096) {
               oy.clear();
               System.err.println("Done.");
               return;
            }

            System.err.println("Input does not appear to be an Ogg bitstream.");
            System.exit(1);
         }

         os.init(og.serialno());
         vi.init();
         vc.init();
         if(os.pagein(og) < 0) {
            System.err.println("Error reading first page of Ogg bitstream data.");
            System.exit(1);
         }

         if(os.packetout(op) != 1) {
            System.err.println("Error reading initial header packet.");
            System.exit(1);
         }

         if(vi.synthesis_headerin(vc, op) < 0) {
            System.err.println("This Ogg bitstream does not contain Vorbis audio data.");
            System.exit(1);
         }

         for(int i = 0; i < 2; oy.wrote(bytes)) {
            while(i < 2) {
               int result = oy.pageout(og);
               if(result == 0) {
                  break;
               }

               if(result == 1) {
                  os.pagein(og);

                  while(i < 2) {
                     result = os.packetout(op);
                     if(result == 0) {
                        break;
                     }

                     if(result == -1) {
                        System.err.println("Corrupt secondary header.  Exiting.");
                        System.exit(1);
                     }

                     vi.synthesis_headerin(vc, op);
                     ++i;
                  }
               }
            }

            index = oy.buffer(4096);
            buffer = oy.data;

            try {
               bytes = input.read(buffer, index, 4096);
            } catch (Exception var25) {
               System.err.println(var25);
               System.exit(1);
            }

            if(bytes == 0 && i < 2) {
               System.err.println("End of file before finding all Vorbis headers!");
               System.exit(1);
            }
         }

         byte[][] ptr = vc.user_comments;

         for(int j = 0; j < ptr.length && ptr[j] != null; ++j) {
            System.err.println(new String(ptr[j], 0, ptr[j].length - 1));
         }

         System.err.println("\nBitstream is " + vi.channels + " channel, " + vi.rate + "Hz");
         System.err.println("Encoded by: " + new String(vc.vendor, 0, vc.vendor.length - 1) + "\n");
         convsize = 4096 / vi.channels;
         vd.synthesis_init(vi);
         vb.init(vd);
         float[][][] _pcm = new float[1][][];
         int[] _index = new int[vi.channels];

         while(eos == 0) {
            while(eos == 0) {
               int result = oy.pageout(og);
               if(result == 0) {
                  break;
               }

               if(result == -1) {
                  System.err.println("Corrupt or missing data in bitstream; continuing...");
               } else {
                  os.pagein(og);

                  while(true) {
                     result = os.packetout(op);
                     if(result == 0) {
                        if(og.eos() != 0) {
                           eos = 1;
                        }
                        break;
                     }

                     if(result != -1) {
                        if(vb.synthesis(op) == 0) {
                           vd.synthesis_blockin(vb);
                        }

                        int samples;
                        while((samples = vd.synthesis_pcmout(_pcm, _index)) > 0) {
                           float[][] pcm = _pcm[0];
                           int bout = samples < convsize?samples:convsize;

                           for(int i = 0; i < vi.channels; ++i) {
                              int ptr = i * 2;
                              int mono = _index[i];

                              for(int j = 0; j < bout; ++j) {
                                 int val = (int)((double)pcm[i][mono + j] * 32767.0D);
                                 if(val > 32767) {
                                    val = 32767;
                                 }

                                 if(val < -32768) {
                                    val = -32768;
                                 }

                                 if(val < 0) {
                                    val |= '耀';
                                 }

                                 convbuffer[ptr] = (byte)val;
                                 convbuffer[ptr + 1] = (byte)(val >>> 8);
                                 ptr += 2 * vi.channels;
                              }
                           }

                           System.out.write(convbuffer, 0, 2 * vi.channels * bout);
                           vd.synthesis_read(bout);
                        }
                     }
                  }
               }
            }

            if(eos == 0) {
               index = oy.buffer(4096);
               buffer = oy.data;

               try {
                  bytes = input.read(buffer, index, 4096);
               } catch (Exception var27) {
                  System.err.println(var27);
                  System.exit(1);
               }

               oy.wrote(bytes);
               if(bytes == 0) {
                  eos = 1;
               }
            }
         }

         os.clear();
         vb.clear();
         vd.clear();
         vi.clear();
      }
   }
}
