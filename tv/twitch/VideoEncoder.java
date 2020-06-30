package tv.twitch;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum VideoEncoder {
   TTV_VID_ENC_DISABLE(-2),
   TTV_VID_ENC_DEFAULT(-1),
   TTV_VID_ENC_INTEL(0),
   TTV_VID_ENC_APPLE(2),
   TTV_VID_ENC_PLUGIN(100);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static VideoEncoder lookupValue(int var0) {
      VideoEncoder var1 = (VideoEncoder)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private VideoEncoder(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(VideoEncoder var2 : EnumSet.allOf(VideoEncoder.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
