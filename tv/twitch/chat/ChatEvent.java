package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatEvent {
   TTV_CHAT_JOINED_CHANNEL(0),
   TTV_CHAT_LEFT_CHANNEL(1);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static ChatEvent lookupValue(int var0) {
      ChatEvent var1 = (ChatEvent)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private ChatEvent(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(ChatEvent var2 : EnumSet.allOf(ChatEvent.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
