package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StatType {
   TTV_ST_RTMPSTATE(0),
   TTV_ST_RTMPDATASENT(1);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static StatType lookupValue(int var0) {
      StatType var1 = (StatType)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private StatType(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(StatType var2 : EnumSet.allOf(StatType.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
