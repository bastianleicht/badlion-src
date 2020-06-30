package com.jcraft.jorbis;

import com.jcraft.jogg.Buffer;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;
import com.jcraft.jorbis.InfoMode;
import com.jcraft.jorbis.Mapping0;

abstract class FuncMapping {
   public static FuncMapping[] mapping_P = new FuncMapping[]{new Mapping0()};

   abstract void pack(Info var1, Object var2, Buffer var3);

   abstract Object unpack(Info var1, Buffer var2);

   abstract Object look(DspState var1, InfoMode var2, Object var3);

   abstract void free_info(Object var1);

   abstract void free_look(Object var1);

   abstract int inverse(Block var1, Object var2);
}
