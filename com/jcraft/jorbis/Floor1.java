package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.CodeBook;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncFloor;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Util;

class Floor1 extends FuncFloor {
   static final int floor1_rangedb = 140;
   static final int VIF_POSIT = 63;
   private static float[] FLOOR_fromdB_LOOKUP = new float[]{1.0649863E-7F, 1.1341951E-7F, 1.2079015E-7F, 1.2863978E-7F, 1.369995E-7F, 1.459025E-7F, 1.5538409E-7F, 1.6548181E-7F, 1.7623574E-7F, 1.8768856E-7F, 1.998856E-7F, 2.128753E-7F, 2.2670913E-7F, 2.4144197E-7F, 2.5713223E-7F, 2.7384212E-7F, 2.9163792E-7F, 3.1059022E-7F, 3.307741E-7F, 3.5226967E-7F, 3.7516213E-7F, 3.995423E-7F, 4.255068E-7F, 4.5315863E-7F, 4.8260745E-7F, 5.1397E-7F, 5.4737063E-7F, 5.829419E-7F, 6.208247E-7F, 6.611694E-7F, 7.041359E-7F, 7.4989464E-7F, 7.98627E-7F, 8.505263E-7F, 9.057983E-7F, 9.646621E-7F, 1.0273513E-6F, 1.0941144E-6F, 1.1652161E-6F, 1.2409384E-6F, 1.3215816E-6F, 1.4074654E-6F, 1.4989305E-6F, 1.5963394E-6F, 1.7000785E-6F, 1.8105592E-6F, 1.9282195E-6F, 2.053526E-6F, 2.1869757E-6F, 2.3290977E-6F, 2.4804558E-6F, 2.6416496E-6F, 2.813319E-6F, 2.9961443E-6F, 3.1908505E-6F, 3.39821E-6F, 3.619045E-6F, 3.8542307E-6F, 4.1047006E-6F, 4.371447E-6F, 4.6555283E-6F, 4.958071E-6F, 5.280274E-6F, 5.623416E-6F, 5.988857E-6F, 6.3780467E-6F, 6.7925284E-6F, 7.2339453E-6F, 7.704048E-6F, 8.2047E-6F, 8.737888E-6F, 9.305725E-6F, 9.910464E-6F, 1.0554501E-5F, 1.1240392E-5F, 1.1970856E-5F, 1.2748789E-5F, 1.3577278E-5F, 1.4459606E-5F, 1.5399271E-5F, 1.6400005E-5F, 1.7465769E-5F, 1.8600793E-5F, 1.9809577E-5F, 2.1096914E-5F, 2.2467912E-5F, 2.3928002E-5F, 2.5482977E-5F, 2.7139005E-5F, 2.890265E-5F, 3.078091E-5F, 3.2781227E-5F, 3.4911533E-5F, 3.718028E-5F, 3.9596467E-5F, 4.2169668E-5F, 4.491009E-5F, 4.7828602E-5F, 5.0936775E-5F, 5.424693E-5F, 5.7772202E-5F, 6.152657E-5F, 6.552491E-5F, 6.9783084E-5F, 7.4317984E-5F, 7.914758E-5F, 8.429104E-5F, 8.976875E-5F, 9.560242E-5F, 1.0181521E-4F, 1.0843174E-4F, 1.1547824E-4F, 1.2298267E-4F, 1.3097477E-4F, 1.3948625E-4F, 1.4855085E-4F, 1.5820454E-4F, 1.6848555E-4F, 1.7943469E-4F, 1.9109536E-4F, 2.0351382E-4F, 2.167393E-4F, 2.3082423E-4F, 2.4582449E-4F, 2.6179955E-4F, 2.7881275E-4F, 2.9693157E-4F, 3.1622787E-4F, 3.3677815E-4F, 3.5866388E-4F, 3.8197188E-4F, 4.0679457E-4F, 4.3323037E-4F, 4.613841E-4F, 4.913675E-4F, 5.2329927E-4F, 5.573062E-4F, 5.935231E-4F, 6.320936E-4F, 6.731706E-4F, 7.16917E-4F, 7.635063E-4F, 8.1312325E-4F, 8.6596457E-4F, 9.2223985E-4F, 9.821722E-4F, 0.0010459992F, 0.0011139743F, 0.0011863665F, 0.0012634633F, 0.0013455702F, 0.0014330129F, 0.0015261382F, 0.0016253153F, 0.0017309374F, 0.0018434235F, 0.0019632196F, 0.0020908006F, 0.0022266726F, 0.0023713743F, 0.0025254795F, 0.0026895993F, 0.0028643848F, 0.0030505287F, 0.003248769F, 0.0034598925F, 0.0036847359F, 0.0039241905F, 0.0041792067F, 0.004450795F, 0.004740033F, 0.005048067F, 0.0053761187F, 0.005725489F, 0.0060975635F, 0.0064938175F, 0.0069158226F, 0.0073652514F, 0.007843887F, 0.008353627F, 0.008896492F, 0.009474637F, 0.010090352F, 0.01074608F, 0.011444421F, 0.012188144F, 0.012980198F, 0.013823725F, 0.014722068F, 0.015678791F, 0.016697686F, 0.017782796F, 0.018938422F, 0.020169148F, 0.021479854F, 0.022875736F, 0.02436233F, 0.025945531F, 0.027631618F, 0.029427277F, 0.031339627F, 0.03337625F, 0.035545226F, 0.037855156F, 0.0403152F, 0.042935107F, 0.045725275F, 0.048696756F, 0.05186135F, 0.05523159F, 0.05882085F, 0.062643364F, 0.06671428F, 0.07104975F, 0.075666964F, 0.08058423F, 0.08582105F, 0.09139818F, 0.097337745F, 0.1036633F, 0.11039993F, 0.11757434F, 0.12521498F, 0.13335215F, 0.14201812F, 0.15124726F, 0.16107617F, 0.1715438F, 0.18269168F, 0.19456401F, 0.20720787F, 0.22067343F, 0.23501402F, 0.25028655F, 0.26655158F, 0.28387362F, 0.3023213F, 0.32196787F, 0.34289113F, 0.36517414F, 0.3889052F, 0.41417846F, 0.44109413F, 0.4697589F, 0.50028646F, 0.53279793F, 0.5674221F, 0.6042964F, 0.64356697F, 0.6853896F, 0.72993004F, 0.777365F, 0.8278826F, 0.88168305F, 0.9389798F, 1.0F};

   void pack(Object i, Buffer opb) {
      Floor1.InfoFloor1 info = (Floor1.InfoFloor1)i;
      int count = 0;
      int maxposit = info.postlist[1];
      int maxclass = -1;
      opb.write(info.partitions, 5);

      for(int j = 0; j < info.partitions; ++j) {
         opb.write(info.partitionclass[j], 4);
         if(maxclass < info.partitionclass[j]) {
            maxclass = info.partitionclass[j];
         }
      }

      for(int j = 0; j < maxclass + 1; ++j) {
         opb.write(info.class_dim[j] - 1, 3);
         opb.write(info.class_subs[j], 2);
         if(info.class_subs[j] != 0) {
            opb.write(info.class_book[j], 8);
         }

         for(int k = 0; k < 1 << info.class_subs[j]; ++k) {
            opb.write(info.class_subbook[j][k] + 1, 8);
         }
      }

      opb.write(info.mult - 1, 2);
      opb.write(Util.ilog2(maxposit), 4);
      int rangebits = Util.ilog2(maxposit);
      int j = 0;

      for(int k = 0; j < info.partitions; ++j) {
         for(count += info.class_dim[info.partitionclass[j]]; k < count; ++k) {
            opb.write(info.postlist[k + 2], rangebits);
         }
      }

   }

   Object unpack(Info vi, Buffer opb) {
      int count = 0;
      int maxclass = -1;
      Floor1.InfoFloor1 info = new Floor1.InfoFloor1();
      info.partitions = opb.read(5);

      for(int j = 0; j < info.partitions; ++j) {
         info.partitionclass[j] = opb.read(4);
         if(maxclass < info.partitionclass[j]) {
            maxclass = info.partitionclass[j];
         }
      }

      for(int j = 0; j < maxclass + 1; ++j) {
         info.class_dim[j] = opb.read(3) + 1;
         info.class_subs[j] = opb.read(2);
         if(info.class_subs[j] < 0) {
            info.free();
            return null;
         }

         if(info.class_subs[j] != 0) {
            info.class_book[j] = opb.read(8);
         }

         if(info.class_book[j] < 0 || info.class_book[j] >= vi.books) {
            info.free();
            return null;
         }

         for(int k = 0; k < 1 << info.class_subs[j]; ++k) {
            info.class_subbook[j][k] = opb.read(8) - 1;
            if(info.class_subbook[j][k] < -1 || info.class_subbook[j][k] >= vi.books) {
               info.free();
               return null;
            }
         }
      }

      info.mult = opb.read(2) + 1;
      int rangebits = opb.read(4);
      int j = 0;

      for(int k = 0; j < info.partitions; ++j) {
         for(count += info.class_dim[info.partitionclass[j]]; k < count; ++k) {
            int t = info.postlist[k + 2] = opb.read(rangebits);
            if(t < 0 || t >= 1 << rangebits) {
               info.free();
               return null;
            }
         }
      }

      info.postlist[0] = 0;
      info.postlist[1] = 1 << rangebits;
      return info;
   }

   Object look(DspState vd, InfoMode mi, Object i) {
      int _n = 0;
      int[] sortpointer = new int[65];
      Floor1.InfoFloor1 info = (Floor1.InfoFloor1)i;
      Floor1.LookFloor1 look = new Floor1.LookFloor1();
      look.vi = info;
      look.n = info.postlist[1];

      for(int j = 0; j < info.partitions; ++j) {
         _n += info.class_dim[info.partitionclass[j]];
      }

      _n = _n + 2;
      look.posts = _n;

      for(int j = 0; j < _n; sortpointer[j] = j++) {
         ;
      }

      for(int j = 0; j < _n - 1; ++j) {
         for(int k = j; k < _n; ++k) {
            if(info.postlist[sortpointer[j]] > info.postlist[sortpointer[k]]) {
               int foo = sortpointer[k];
               sortpointer[k] = sortpointer[j];
               sortpointer[j] = foo;
            }
         }
      }

      for(int j = 0; j < _n; ++j) {
         look.forward_index[j] = sortpointer[j];
      }

      for(int j = 0; j < _n; look.reverse_index[look.forward_index[j]] = j++) {
         ;
      }

      for(int j = 0; j < _n; ++j) {
         look.sorted_index[j] = info.postlist[look.forward_index[j]];
      }

      switch(info.mult) {
      case 1:
         look.quant_q = 256;
         break;
      case 2:
         look.quant_q = 128;
         break;
      case 3:
         look.quant_q = 86;
         break;
      case 4:
         look.quant_q = 64;
         break;
      default:
         look.quant_q = -1;
      }

      for(int j = 0; j < _n - 2; ++j) {
         int lo = 0;
         int hi = 1;
         int lx = 0;
         int hx = look.n;
         int currentx = info.postlist[j + 2];

         for(int k = 0; k < j + 2; ++k) {
            int x = info.postlist[k];
            if(x > lx && x < currentx) {
               lo = k;
               lx = x;
            }

            if(x < hx && x > currentx) {
               hi = k;
               hx = x;
            }
         }

         look.loneighbor[j] = lo;
         look.hineighbor[j] = hi;
      }

      return look;
   }

   void free_info(Object i) {
   }

   void free_look(Object i) {
   }

   void free_state(Object vs) {
   }

   int forward(Block vb, Object i, float[] in, float[] out, Object vs) {
      return 0;
   }

   Object inverse1(Block vb, Object ii, Object memo) {
      Floor1.LookFloor1 look = (Floor1.LookFloor1)ii;
      Floor1.InfoFloor1 info = look.vi;
      CodeBook[] books = vb.vd.fullbooks;
      if(vb.opb.read(1) == 1) {
         int[] fit_value = null;
         if(memo instanceof int[]) {
            fit_value = (int[])((int[])memo);
         }

         if(fit_value != null && fit_value.length >= look.posts) {
            for(int i = 0; i < fit_value.length; ++i) {
               fit_value[i] = 0;
            }
         } else {
            fit_value = new int[look.posts];
         }

         fit_value[0] = vb.opb.read(Util.ilog(look.quant_q - 1));
         fit_value[1] = vb.opb.read(Util.ilog(look.quant_q - 1));
         int i = 0;

         for(int j = 2; i < info.partitions; ++i) {
            int clss = info.partitionclass[i];
            int cdim = info.class_dim[clss];
            int csubbits = info.class_subs[clss];
            int csub = 1 << csubbits;
            int cval = 0;
            if(csubbits != 0) {
               cval = books[info.class_book[clss]].decode(vb.opb);
               if(cval == -1) {
                  return null;
               }
            }

            for(int k = 0; k < cdim; ++k) {
               int book = info.class_subbook[clss][cval & csub - 1];
               cval >>>= csubbits;
               if(book >= 0) {
                  if((fit_value[j + k] = books[book].decode(vb.opb)) == -1) {
                     return null;
                  }
               } else {
                  fit_value[j + k] = 0;
               }
            }

            j += cdim;
         }

         for(i = 2; i < look.posts; ++i) {
            int predicted = render_point(info.postlist[look.loneighbor[i - 2]], info.postlist[look.hineighbor[i - 2]], fit_value[look.loneighbor[i - 2]], fit_value[look.hineighbor[i - 2]], info.postlist[i]);
            int hiroom = look.quant_q - predicted;
            int room = (hiroom < predicted?hiroom:predicted) << 1;
            int val = fit_value[i];
            if(val != 0) {
               if(val >= room) {
                  if(hiroom > predicted) {
                     val = val - predicted;
                  } else {
                     val = -1 - (val - hiroom);
                  }
               } else if((val & 1) != 0) {
                  val = -(val + 1 >>> 1);
               } else {
                  val = val >> 1;
               }

               fit_value[i] = val + predicted;
               fit_value[look.loneighbor[i - 2]] &= 32767;
               fit_value[look.hineighbor[i - 2]] &= 32767;
            } else {
               fit_value[i] = predicted | '耀';
            }
         }

         return fit_value;
      } else {
         return null;
      }
   }

   private static int render_point(int x0, int x1, int y0, int y1, int x) {
      y0 = y0 & 32767;
      y1 = y1 & 32767;
      int dy = y1 - y0;
      int adx = x1 - x0;
      int ady = Math.abs(dy);
      int err = ady * (x - x0);
      int off = err / adx;
      return dy < 0?y0 - off:y0 + off;
   }

   int inverse2(Block vb, Object i, Object memo, float[] out) {
      Floor1.LookFloor1 look = (Floor1.LookFloor1)i;
      Floor1.InfoFloor1 info = look.vi;
      int n = vb.vd.vi.blocksizes[vb.mode] / 2;
      if(memo != null) {
         int[] fit_value = (int[])((int[])memo);
         int hx = 0;
         int lx = 0;
         int ly = fit_value[0] * info.mult;

         for(int j = 1; j < look.posts; ++j) {
            int current = look.forward_index[j];
            int hy = fit_value[current] & 32767;
            if(hy == fit_value[current]) {
               hy = hy * info.mult;
               hx = info.postlist[current];
               render_line(lx, hx, ly, hy, out);
               lx = hx;
               ly = hy;
            }
         }

         for(int j = hx; j < n; ++j) {
            out[j] *= out[j - 1];
         }

         return 1;
      } else {
         for(int j = 0; j < n; ++j) {
            out[j] = 0.0F;
         }

         return 0;
      }
   }

   private static void render_line(int x0, int x1, int y0, int y1, float[] d) {
      int dy = y1 - y0;
      int adx = x1 - x0;
      int ady = Math.abs(dy);
      int base = dy / adx;
      int sy = dy < 0?base - 1:base + 1;
      int x = x0;
      int y = y0;
      int err = 0;
      ady = ady - Math.abs(base * adx);
      d[x0] *= FLOOR_fromdB_LOOKUP[y0];

      while(true) {
         ++x;
         if(x >= x1) {
            return;
         }

         err += ady;
         if(err >= adx) {
            err -= adx;
            y += sy;
         } else {
            y += base;
         }

         d[x] *= FLOOR_fromdB_LOOKUP[y];
      }
   }

   class EchstateFloor1 {
      int[] codewords;
      float[] curve;
      long frameno;
      long codes;
   }

   class InfoFloor1 {
      static final int VIF_POSIT = 63;
      static final int VIF_CLASS = 16;
      static final int VIF_PARTS = 31;
      int partitions;
      int[] partitionclass = new int[31];
      int[] class_dim = new int[16];
      int[] class_subs = new int[16];
      int[] class_book = new int[16];
      int[][] class_subbook = new int[16][];
      int mult;
      int[] postlist = new int[65];
      float maxover;
      float maxunder;
      float maxerr;
      int twofitminsize;
      int twofitminused;
      int twofitweight;
      float twofitatten;
      int unusedminsize;
      int unusedmin_n;
      int n;

      InfoFloor1() {
         for(int i = 0; i < this.class_subbook.length; ++i) {
            this.class_subbook[i] = new int[8];
         }

      }

      void free() {
         this.partitionclass = null;
         this.class_dim = null;
         this.class_subs = null;
         this.class_book = null;
         this.class_subbook = (int[][])null;
         this.postlist = null;
      }

      Object copy_info() {
         Floor1.InfoFloor1 info = this;
         Floor1.InfoFloor1 ret = Floor1.this.new InfoFloor1();
         ret.partitions = this.partitions;
         System.arraycopy(this.partitionclass, 0, ret.partitionclass, 0, 31);
         System.arraycopy(this.class_dim, 0, ret.class_dim, 0, 16);
         System.arraycopy(this.class_subs, 0, ret.class_subs, 0, 16);
         System.arraycopy(this.class_book, 0, ret.class_book, 0, 16);

         for(int j = 0; j < 16; ++j) {
            System.arraycopy(info.class_subbook[j], 0, ret.class_subbook[j], 0, 8);
         }

         ret.mult = info.mult;
         System.arraycopy(info.postlist, 0, ret.postlist, 0, 65);
         ret.maxover = info.maxover;
         ret.maxunder = info.maxunder;
         ret.maxerr = info.maxerr;
         ret.twofitminsize = info.twofitminsize;
         ret.twofitminused = info.twofitminused;
         ret.twofitweight = info.twofitweight;
         ret.twofitatten = info.twofitatten;
         ret.unusedminsize = info.unusedminsize;
         ret.unusedmin_n = info.unusedmin_n;
         ret.n = info.n;
         return ret;
      }
   }

   class LookFloor1 {
      static final int VIF_POSIT = 63;
      int[] sorted_index = new int[65];
      int[] forward_index = new int[65];
      int[] reverse_index = new int[65];
      int[] hineighbor = new int[63];
      int[] loneighbor = new int[63];
      int posts;
      int n;
      int quant_q;
      Floor1.InfoFloor1 vi;
      int phrasebits;
      int postbits;
      int frames;

      void free() {
         this.sorted_index = null;
         this.forward_index = null;
         this.reverse_index = null;
         this.hineighbor = null;
         this.loneighbor = null;
      }
   }

   class Lsfit_acc {
      long x0;
      long x1;
      long xa;
      long ya;
      long x2a;
      long y2a;
      long xya;
      long n;
      long an;
      long un;
      long edgey0;
      long edgey1;
   }
}
