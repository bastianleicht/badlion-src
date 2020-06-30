package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AudioSampleFormat {
   TTV_ASF_PCM_S16(0);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static AudioSampleFormat lookupValue(int var0) {
      AudioSampleFormat var1 = (AudioSampleFormat)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private AudioSampleFormat(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(AudioSampleFormat var2 : EnumSet.allOf(AudioSampleFormat.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
