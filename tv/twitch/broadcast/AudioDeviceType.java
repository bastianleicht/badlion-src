package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum AudioDeviceType {
   TTV_PLAYBACK_DEVICE(0),
   TTV_RECORDER_DEVICE(1),
   TTV_PASSTHROUGH_DEVICE(2),
   TTV_DEVICE_NUM(3);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static AudioDeviceType lookupValue(int var0) {
      AudioDeviceType var1 = (AudioDeviceType)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private AudioDeviceType(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(AudioDeviceType var2 : EnumSet.allOf(AudioDeviceType.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
