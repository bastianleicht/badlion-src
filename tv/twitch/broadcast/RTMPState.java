package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum RTMPState {
   Invalid(-1),
   Idle(0),
   Initialize(1),
   Handshake(2),
   Connect(3),
   CreateStream(4),
   Publish(5),
   SendVideo(6),
   Shutdown(7),
   Error(8);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static RTMPState lookupValue(int var0) {
      RTMPState var1 = (RTMPState)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private RTMPState(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(RTMPState var2 : EnumSet.allOf(RTMPState.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
