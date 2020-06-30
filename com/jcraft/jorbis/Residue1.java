package com.jcraft.jorbis;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Residue0;

class Residue1 extends Residue0 {
   int inverse(Block vb, Object vl, float[][] in, int[] nonzero, int ch) {
      int used = 0;

      for(int i = 0; i < ch; ++i) {
         if(nonzero[i] != 0) {
            in[used++] = in[i];
         }
      }

      if(used != 0) {
         return _01inverse(vb, vl, in, used, 1);
      } else {
         return 0;
      }
   }
}
