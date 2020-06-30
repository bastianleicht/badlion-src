package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncFloor;
import com.jcraft.jorbis.FuncMapping;
import com.jcraft.jorbis.FuncResidue;
import com.jcraft.jorbis.FuncTime;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Mdct;
import com.jcraft.jorbis.PsyLook;
import com.jcraft.jorbis.Util;

class Mapping0 extends FuncMapping {
   static int seq = 0;
   float[][] pcmbundle = (float[][])null;
   int[] zerobundle = null;
   int[] nonzero = null;
   Object[] floormemo = null;

   void free_info(Object imap) {
   }

   void free_look(Object imap) {
   }

   Object look(DspState vd, InfoMode vm, Object m) {
      Info vi = vd.vi;
      Mapping0.LookMapping0 look = new Mapping0.LookMapping0();
      Mapping0.InfoMapping0 info = look.map = (Mapping0.InfoMapping0)m;
      look.mode = vm;
      look.time_look = new Object[info.submaps];
      look.floor_look = new Object[info.submaps];
      look.residue_look = new Object[info.submaps];
      look.time_func = new FuncTime[info.submaps];
      look.floor_func = new FuncFloor[info.submaps];
      look.residue_func = new FuncResidue[info.submaps];

      for(int i = 0; i < info.submaps; ++i) {
         int timenum = info.timesubmap[i];
         int floornum = info.floorsubmap[i];
         int resnum = info.residuesubmap[i];
         look.time_func[i] = FuncTime.time_P[vi.time_type[timenum]];
         look.time_look[i] = look.time_func[i].look(vd, vm, vi.time_param[timenum]);
         look.floor_func[i] = FuncFloor.floor_P[vi.floor_type[floornum]];
         look.floor_look[i] = look.floor_func[i].look(vd, vm, vi.floor_param[floornum]);
         look.residue_func[i] = FuncResidue.residue_P[vi.residue_type[resnum]];
         look.residue_look[i] = look.residue_func[i].look(vd, vm, vi.residue_param[resnum]);
      }

      if(vi.psys != 0 && vd.analysisp != 0) {
         ;
      }

      look.ch = vi.channels;
      return look;
   }

   void pack(Info vi, Object imap, Buffer opb) {
      Mapping0.InfoMapping0 info = (Mapping0.InfoMapping0)imap;
      if(info.submaps > 1) {
         opb.write(1, 1);
         opb.write(info.submaps - 1, 4);
      } else {
         opb.write(0, 1);
      }

      if(info.coupling_steps > 0) {
         opb.write(1, 1);
         opb.write(info.coupling_steps - 1, 8);

         for(int i = 0; i < info.coupling_steps; ++i) {
            opb.write(info.coupling_mag[i], Util.ilog2(vi.channels));
            opb.write(info.coupling_ang[i], Util.ilog2(vi.channels));
         }
      } else {
         opb.write(0, 1);
      }

      opb.write(0, 2);
      if(info.submaps > 1) {
         for(int i = 0; i < vi.channels; ++i) {
            opb.write(info.chmuxlist[i], 4);
         }
      }

      for(int i = 0; i < info.submaps; ++i) {
         opb.write(info.timesubmap[i], 8);
         opb.write(info.floorsubmap[i], 8);
         opb.write(info.residuesubmap[i], 8);
      }

   }

   Object unpack(Info vi, Buffer opb) {
      Mapping0.InfoMapping0 info = new Mapping0.InfoMapping0();
      if(opb.read(1) != 0) {
         info.submaps = opb.read(4) + 1;
      } else {
         info.submaps = 1;
      }

      if(opb.read(1) != 0) {
         info.coupling_steps = opb.read(8) + 1;

         for(int i = 0; i < info.coupling_steps; ++i) {
            int testM = info.coupling_mag[i] = opb.read(Util.ilog2(vi.channels));
            int testA = info.coupling_ang[i] = opb.read(Util.ilog2(vi.channels));
            if(testM < 0 || testA < 0 || testM == testA || testM >= vi.channels || testA >= vi.channels) {
               info.free();
               return null;
            }
         }
      }

      if(opb.read(2) > 0) {
         info.free();
         return null;
      } else {
         if(info.submaps > 1) {
            for(int i = 0; i < vi.channels; ++i) {
               info.chmuxlist[i] = opb.read(4);
               if(info.chmuxlist[i] >= info.submaps) {
                  info.free();
                  return null;
               }
            }
         }

         for(int i = 0; i < info.submaps; ++i) {
            info.timesubmap[i] = opb.read(8);
            if(info.timesubmap[i] >= vi.times) {
               info.free();
               return null;
            }

            info.floorsubmap[i] = opb.read(8);
            if(info.floorsubmap[i] >= vi.floors) {
               info.free();
               return null;
            }

            info.residuesubmap[i] = opb.read(8);
            if(info.residuesubmap[i] >= vi.residues) {
               info.free();
               return null;
            }
         }

         return info;
      }
   }

   synchronized int inverse(Block vb, Object l) {
      DspState vd = vb.vd;
      Info vi = vd.vi;
      Mapping0.LookMapping0 look = (Mapping0.LookMapping0)l;
      Mapping0.InfoMapping0 info = look.map;
      InfoMode mode = look.mode;
      int n = vb.pcmend = vi.blocksizes[vb.W];
      float[] window = vd.window[vb.W][vb.lW][vb.nW][mode.windowtype];
      if(this.pcmbundle == null || this.pcmbundle.length < vi.channels) {
         this.pcmbundle = new float[vi.channels][];
         this.nonzero = new int[vi.channels];
         this.zerobundle = new int[vi.channels];
         this.floormemo = new Object[vi.channels];
      }

      for(int i = 0; i < vi.channels; ++i) {
         float[] pcm = vb.pcm[i];
         int submap = info.chmuxlist[i];
         this.floormemo[i] = look.floor_func[submap].inverse1(vb, look.floor_look[submap], this.floormemo[i]);
         if(this.floormemo[i] != null) {
            this.nonzero[i] = 1;
         } else {
            this.nonzero[i] = 0;
         }

         for(int j = 0; j < n / 2; ++j) {
            pcm[j] = 0.0F;
         }
      }

      for(int i = 0; i < info.coupling_steps; ++i) {
         if(this.nonzero[info.coupling_mag[i]] != 0 || this.nonzero[info.coupling_ang[i]] != 0) {
            this.nonzero[info.coupling_mag[i]] = 1;
            this.nonzero[info.coupling_ang[i]] = 1;
         }
      }

      for(int i = 0; i < info.submaps; ++i) {
         int ch_in_bundle = 0;

         for(int j = 0; j < vi.channels; ++j) {
            if(info.chmuxlist[j] == i) {
               if(this.nonzero[j] != 0) {
                  this.zerobundle[ch_in_bundle] = 1;
               } else {
                  this.zerobundle[ch_in_bundle] = 0;
               }

               this.pcmbundle[ch_in_bundle++] = vb.pcm[j];
            }
         }

         look.residue_func[i].inverse(vb, look.residue_look[i], this.pcmbundle, this.zerobundle, ch_in_bundle);
      }

      for(int i = info.coupling_steps - 1; i >= 0; --i) {
         float[] pcmM = vb.pcm[info.coupling_mag[i]];
         float[] pcmA = vb.pcm[info.coupling_ang[i]];

         for(int j = 0; j < n / 2; ++j) {
            float mag = pcmM[j];
            float ang = pcmA[j];
            if(mag > 0.0F) {
               if(ang > 0.0F) {
                  pcmM[j] = mag;
                  pcmA[j] = mag - ang;
               } else {
                  pcmA[j] = mag;
                  pcmM[j] = mag + ang;
               }
            } else if(ang > 0.0F) {
               pcmM[j] = mag;
               pcmA[j] = mag + ang;
            } else {
               pcmA[j] = mag;
               pcmM[j] = mag - ang;
            }
         }
      }

      for(int i = 0; i < vi.channels; ++i) {
         float[] pcm = vb.pcm[i];
         int submap = info.chmuxlist[i];
         look.floor_func[submap].inverse2(vb, look.floor_look[submap], this.floormemo[i], pcm);
      }

      for(int i = 0; i < vi.channels; ++i) {
         float[] pcm = vb.pcm[i];
         ((Mdct)vd.transform[vb.W][0]).backward(pcm, pcm);
      }

      for(int i = 0; i < vi.channels; ++i) {
         float[] pcm = vb.pcm[i];
         if(this.nonzero[i] != 0) {
            for(int j = 0; j < n; ++j) {
               pcm[j] *= window[j];
            }
         } else {
            for(int j = 0; j < n; ++j) {
               pcm[j] = 0.0F;
            }
         }
      }

      return 0;
   }

   class InfoMapping0 {
      int submaps;
      int[] chmuxlist = new int[256];
      int[] timesubmap = new int[16];
      int[] floorsubmap = new int[16];
      int[] residuesubmap = new int[16];
      int[] psysubmap = new int[16];
      int coupling_steps;
      int[] coupling_mag = new int[256];
      int[] coupling_ang = new int[256];

      void free() {
         this.chmuxlist = null;
         this.timesubmap = null;
         this.floorsubmap = null;
         this.residuesubmap = null;
         this.psysubmap = null;
         this.coupling_mag = null;
         this.coupling_ang = null;
      }
   }

   class LookMapping0 {
      InfoMode mode;
      Mapping0.InfoMapping0 map;
      Object[] time_look;
      Object[] floor_look;
      Object[] floor_state;
      Object[] residue_look;
      PsyLook[] psy_look;
      FuncTime[] time_func;
      FuncFloor[] floor_func;
      FuncResidue[] residue_func;
      int ch;
      float[][] decay;
      int lastframe;
   }
}
