package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatUserSubscription {
   TTV_CHAT_USERSUB_NONE(0),
   TTV_CHAT_USERSUB_SUBSCRIBER(1),
   TTV_CHAT_USERSUB_TURBO(2);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static ChatUserSubscription lookupValue(int var0) {
      ChatUserSubscription var1 = (ChatUserSubscription)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private ChatUserSubscription(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(ChatUserSubscription var2 : EnumSet.allOf(ChatUserSubscription.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
