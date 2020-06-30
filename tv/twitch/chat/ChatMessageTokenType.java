package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ChatMessageTokenType {
   TTV_CHAT_MSGTOKEN_TEXT(0),
   TTV_CHAT_MSGTOKEN_TEXTURE_IMAGE(1),
   TTV_CHAT_MSGTOKEN_URL_IMAGE(2);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static ChatMessageTokenType lookupValue(int var0) {
      ChatMessageTokenType var1 = (ChatMessageTokenType)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private ChatMessageTokenType(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(ChatMessageTokenType var2 : EnumSet.allOf(ChatMessageTokenType.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
