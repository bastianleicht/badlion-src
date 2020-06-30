package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatUserMode {
   TTV_CHAT_USERMODE_VIEWER(0),
   TTV_CHAT_USERMODE_MODERATOR(1),
   TTV_CHAT_USERMODE_BROADCASTER(2),
   TTV_CHAT_USERMODE_ADMINSTRATOR(4),
   TTV_CHAT_USERMODE_STAFF(8),
   TTV_CHAT_USERMODE_BANNED(1073741824);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static ChatUserMode lookupValue(int var0) {
      ChatUserMode var1 = (ChatUserMode)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private ChatUserMode(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(ChatUserMode var2 : EnumSet.allOf(ChatUserMode.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
