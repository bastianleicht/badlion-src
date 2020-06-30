package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jogg.Packet;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncMapping;
import com.jcraft.jorbis.Info;

public class Block {
   float[][] pcm = new float[0][];
   Buffer opb = new Buffer();
   int lW;
   int W;
   int nW;
   int pcmend;
   int mode;
   int eofflag;
   long granulepos;
   long sequence;
   DspState vd;
   int glue_bits;
   int time_bits;
   int floor_bits;
   int res_bits;

   public Block(DspState vd) {
      this.vd = vd;
      if(vd.analysisp != 0) {
         this.opb.writeinit();
      }

   }

   public void init(DspState vd) {
      this.vd = vd;
   }

   public int clear() {
      if(this.vd != null && this.vd.analysisp != 0) {
         this.opb.writeclear();
      }

      return 0;
   }

   public int synthesis(Packet op) {
      Info vi = this.vd.vi;
      this.opb.readinit(op.packet_base, op.packet, op.bytes);
      if(this.opb.read(1) != 0) {
         return -1;
      } else {
         int _mode = this.opb.read(this.vd.modebits);
         if(_mode == -1) {
            return -1;
         } else {
            this.mode = _mode;
            this.W = vi.mode_param[this.mode].blockflag;
            if(this.W != 0) {
               this.lW = this.opb.read(1);
               this.nW = this.opb.read(1);
               if(this.nW == -1) {
                  return -1;
               }
            } else {
               this.lW = 0;
               this.nW = 0;
            }

            this.granulepos = op.granulepos;
            this.sequence = op.packetno - 3L;
            this.eofflag = op.e_o_s;
            this.pcmend = vi.blocksizes[this.W];
            if(this.pcm.length < vi.channels) {
               this.pcm = new float[vi.channels][];
            }

            for(int i = 0; i < vi.channels; ++i) {
               if(this.pcm[i] != null && this.pcm[i].length >= this.pcmend) {
                  for(int j = 0; j < this.pcmend; ++j) {
                     this.pcm[i][j] = 0.0F;
                  }
               } else {
                  this.pcm[i] = new float[this.pcmend];
               }
            }

            int type = vi.map_type[vi.mode_param[this.mode].mapping];
            return FuncMapping.mapping_P[type].inverse(this, this.vd.mode[this.mode]);
         }
      }
   }
}
