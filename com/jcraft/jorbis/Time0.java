package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.FuncTime;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;

class Time0 extends FuncTime {
   void pack(Object i, Buffer opb) {
   }

   Object unpack(Info vi, Buffer opb) {
      return "";
   }

   Object look(DspState vd, InfoMode mi, Object i) {
      return "";
   }

   void free_info(Object i) {
   }

   void free_look(Object i) {
   }

   int inverse(Block vb, Object i, float[] in, float[] out) {
      return 0;
   }
}
