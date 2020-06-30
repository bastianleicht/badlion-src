package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Time0;

abstract class FuncTime {
   public static FuncTime[] time_P = new FuncTime[]{new Time0()};

   abstract void pack(Object var1, Buffer var2);

   abstract Object unpack(Info var1, Buffer var2);

   abstract Object look(DspState var1, InfoMode var2, Object var3);

   abstract void free_info(Object var1);

   abstract void free_look(Object var1);

   abstract int inverse(Block var1, Object var2, float[] var3, float[] var4);
}
