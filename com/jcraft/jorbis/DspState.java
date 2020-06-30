package com.jcraft.jorbis;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.CodeBook;
import com.jcraft.jorbis.FuncMapping;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.Mdct;
import com.jcraft.jorbis.Util;

public class DspState {
   static final float M_PI = 3.1415927F;
   static final int VI_TRANSFORMB = 1;
   static final int VI_WINDOWB = 1;
   int analysisp;
   Info vi;
   int modebits;
   float[][] pcm;
   int pcm_storage;
   int pcm_current;
   int pcm_returned;
   float[] multipliers;
   int envelope_storage;
   int envelope_current;
   int eofflag;
   int lW;
   int W;
   int nW;
   int centerW;
   long granulepos;
   long sequence;
   long glue_bits;
   long time_bits;
   long floor_bits;
   long res_bits;
   float[][][][][] window;
   Object[][] transform;
   CodeBook[] fullbooks;
   Object[] mode;
   byte[] header;
   byte[] header1;
   byte[] header2;

   public DspState() {
      this.transform = new Object[2][];
      this.window = new float[2][][][][];
      this.window[0] = new float[2][][][];
      this.window[0][0] = new float[2][][];
      this.window[0][1] = new float[2][][];
      this.window[0][0][0] = new float[2][];
      this.window[0][0][1] = new float[2][];
      this.window[0][1][0] = new float[2][];
      this.window[0][1][1] = new float[2][];
      this.window[1] = new float[2][][][];
      this.window[1][0] = new float[2][][];
      this.window[1][1] = new float[2][][];
      this.window[1][0][0] = new float[2][];
      this.window[1][0][1] = new float[2][];
      this.window[1][1][0] = new float[2][];
      this.window[1][1][1] = new float[2][];
   }

   static float[] window(int type, int window, int left, int right) {
      float[] ret = new float[window];
      switch(type) {
      case 0:
         int leftbegin = window / 4 - left / 2;
         int rightbegin = window - window / 4 - right / 2;

         for(int i = 0; i < left; ++i) {
            float x = (float)(((double)i + 0.5D) / (double)left * 3.1415927410125732D / 2.0D);
            x = (float)Math.sin((double)x);
            x = x * x;
            x = (float)((double)x * 1.5707963705062866D);
            x = (float)Math.sin((double)x);
            ret[i + leftbegin] = x;
         }

         for(int i = leftbegin + left; i < rightbegin; ++i) {
            ret[i] = 1.0F;
         }

         for(int i = 0; i < right; ++i) {
            float x = (float)(((double)(right - i) - 0.5D) / (double)right * 3.1415927410125732D / 2.0D);
            x = (float)Math.sin((double)x);
            x = x * x;
            x = (float)((double)x * 1.5707963705062866D);
            x = (float)Math.sin((double)x);
            ret[i + rightbegin] = x;
         }

         return ret;
      default:
         return null;
      }
   }

   int init(Info vi, boolean encp) {
      this.vi = vi;
      this.modebits = Util.ilog2(vi.modes);
      this.transform[0] = new Object[1];
      this.transform[1] = new Object[1];
      this.transform[0][0] = new Mdct();
      this.transform[1][0] = new Mdct();
      ((Mdct)this.transform[0][0]).init(vi.blocksizes[0]);
      ((Mdct)this.transform[1][0]).init(vi.blocksizes[1]);
      this.window[0][0][0] = new float[1][];
      this.window[0][0][1] = this.window[0][0][0];
      this.window[0][1][0] = this.window[0][0][0];
      this.window[0][1][1] = this.window[0][0][0];
      this.window[1][0][0] = new float[1][];
      this.window[1][0][1] = new float[1][];
      this.window[1][1][0] = new float[1][];
      this.window[1][1][1] = new float[1][];

      for(int i = 0; i < 1; ++i) {
         this.window[0][0][0][i] = window(i, vi.blocksizes[0], vi.blocksizes[0] / 2, vi.blocksizes[0] / 2);
         this.window[1][0][0][i] = window(i, vi.blocksizes[1], vi.blocksizes[0] / 2, vi.blocksizes[0] / 2);
         this.window[1][0][1][i] = window(i, vi.blocksizes[1], vi.blocksizes[0] / 2, vi.blocksizes[1] / 2);
         this.window[1][1][0][i] = window(i, vi.blocksizes[1], vi.blocksizes[1] / 2, vi.blocksizes[0] / 2);
         this.window[1][1][1][i] = window(i, vi.blocksizes[1], vi.blocksizes[1] / 2, vi.blocksizes[1] / 2);
      }

      this.fullbooks = new CodeBook[vi.books];

      for(int i = 0; i < vi.books; ++i) {
         this.fullbooks[i] = new CodeBook();
         this.fullbooks[i].init_decode(vi.book_param[i]);
      }

      this.pcm_storage = 8192;
      this.pcm = new float[vi.channels][];

      for(int i = 0; i < vi.channels; ++i) {
         this.pcm[i] = new float[this.pcm_storage];
      }

      this.lW = 0;
      this.W = 0;
      this.centerW = vi.blocksizes[1] / 2;
      this.pcm_current = this.centerW;
      this.mode = new Object[vi.modes];

      for(int i = 0; i < vi.modes; ++i) {
         int mapnum = vi.mode_param[i].mapping;
         int maptype = vi.map_type[mapnum];
         this.mode[i] = FuncMapping.mapping_P[maptype].look(this, vi.mode_param[i], vi.map_param[mapnum]);
      }

      return 0;
   }

   public int synthesis_init(Info vi) {
      this.init(vi, false);
      this.pcm_returned = this.centerW;
      this.centerW -= vi.blocksizes[this.W] / 4 + vi.blocksizes[this.lW] / 4;
      this.granulepos = -1L;
      this.sequence = -1L;
      return 0;
   }

   DspState(Info vi) {
      this();
      this.init(vi, false);
      this.pcm_returned = this.centerW;
      this.centerW -= vi.blocksizes[this.W] / 4 + vi.blocksizes[this.lW] / 4;
      this.granulepos = -1L;
      this.sequence = -1L;
   }

   public int synthesis_blockin(Block vb) {
      if(this.centerW > this.vi.blocksizes[1] / 2 && this.pcm_returned > 8192) {
         int shiftPCM = this.centerW - this.vi.blocksizes[1] / 2;
         shiftPCM = this.pcm_returned < shiftPCM?this.pcm_returned:shiftPCM;
         this.pcm_current -= shiftPCM;
         this.centerW -= shiftPCM;
         this.pcm_returned -= shiftPCM;
         if(shiftPCM != 0) {
            for(int i = 0; i < this.vi.channels; ++i) {
               System.arraycopy(this.pcm[i], shiftPCM, this.pcm[i], 0, this.pcm_current);
            }
         }
      }

      this.lW = this.W;
      this.W = vb.W;
      this.nW = -1;
      this.glue_bits += (long)vb.glue_bits;
      this.time_bits += (long)vb.time_bits;
      this.floor_bits += (long)vb.floor_bits;
      this.res_bits += (long)vb.res_bits;
      if(this.sequence + 1L != vb.sequence) {
         this.granulepos = -1L;
      }

      this.sequence = vb.sequence;
      int sizeW = this.vi.blocksizes[this.W];
      int _centerW = this.centerW + this.vi.blocksizes[this.lW] / 4 + sizeW / 4;
      int beginW = _centerW - sizeW / 2;
      int endW = beginW + sizeW;
      int beginSl = 0;
      int endSl = 0;
      if(endW > this.pcm_storage) {
         this.pcm_storage = endW + this.vi.blocksizes[1];

         for(int i = 0; i < this.vi.channels; ++i) {
            float[] foo = new float[this.pcm_storage];
            System.arraycopy(this.pcm[i], 0, foo, 0, this.pcm[i].length);
            this.pcm[i] = foo;
         }
      }

      switch(this.W) {
      case 0:
         beginSl = 0;
         endSl = this.vi.blocksizes[0] / 2;
         break;
      case 1:
         beginSl = this.vi.blocksizes[1] / 4 - this.vi.blocksizes[this.lW] / 4;
         endSl = beginSl + this.vi.blocksizes[this.lW] / 2;
      }

      for(int j = 0; j < this.vi.channels; ++j) {
         int _pcm = beginW;
         int i = 0;

         for(i = beginSl; i < endSl; ++i) {
            this.pcm[j][_pcm + i] += vb.pcm[j][i];
         }

         while(i < sizeW) {
            this.pcm[j][_pcm + i] = vb.pcm[j][i];
            ++i;
         }
      }

      if(this.granulepos == -1L) {
         this.granulepos = vb.granulepos;
      } else {
         this.granulepos += (long)(_centerW - this.centerW);
         if(vb.granulepos != -1L && this.granulepos != vb.granulepos) {
            if(this.granulepos > vb.granulepos && vb.eofflag != 0) {
               _centerW = (int)((long)_centerW - (this.granulepos - vb.granulepos));
            }

            this.granulepos = vb.granulepos;
         }
      }

      this.centerW = _centerW;
      this.pcm_current = endW;
      if(vb.eofflag != 0) {
         this.eofflag = 1;
      }

      return 0;
   }

   public int synthesis_pcmout(float[][][] _pcm, int[] index) {
      if(this.pcm_returned >= this.centerW) {
         return 0;
      } else {
         if(_pcm != null) {
            for(int i = 0; i < this.vi.channels; ++i) {
               index[i] = this.pcm_returned;
            }

            _pcm[0] = this.pcm;
         }

         return this.centerW - this.pcm_returned;
      }
   }

   public int synthesis_read(int bytes) {
      if(bytes != 0 && this.pcm_returned + bytes > this.centerW) {
         return -1;
      } else {
         this.pcm_returned += bytes;
         return 0;
      }
   }

   public void clear() {
   }
}
