package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum StartFlags {
   None(0),
   TTV_Start_BandwidthTest(1);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static StartFlags lookupValue(int var0) {
      StartFlags var1 = (StartFlags)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private StartFlags(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(StartFlags var2 : EnumSet.allOf(StartFlags.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
