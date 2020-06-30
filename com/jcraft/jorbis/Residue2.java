package com.jcraft.jorbis;

import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Residue0;

class Residue2 extends Residue0 {
   int inverse(Block vb, Object vl, float[][] in, int[] nonzero, int ch) {
      int i = 0;

      for(i = 0; i < ch && nonzero[i] == 0; ++i) {
         ;
      }

      return i == ch?0:_2inverse(vb, vl, in, ch);
   }
}
