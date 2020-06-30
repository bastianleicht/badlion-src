package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EncodingCpuUsage {
   TTV_ECU_LOW(0),
   TTV_ECU_MEDIUM(1),
   TTV_ECU_HIGH(2);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static EncodingCpuUsage lookupValue(int var0) {
      EncodingCpuUsage var1 = (EncodingCpuUsage)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private EncodingCpuUsage(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(EncodingCpuUsage var2 : EnumSet.allOf(EncodingCpuUsage.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
