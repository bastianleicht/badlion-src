package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AudioEncoder {
   TTV_AUD_ENC_DEFAULT(-1),
   TTV_AUD_ENC_LAMEMP3(0),
   TTV_AUD_ENC_APPLEAAC(1);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static AudioEncoder lookupValue(int var0) {
      AudioEncoder var1 = (AudioEncoder)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private AudioEncoder(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(AudioEncoder var2 : EnumSet.allOf(AudioEncoder.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
