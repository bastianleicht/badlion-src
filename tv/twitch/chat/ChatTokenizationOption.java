package tv.twitch.chat;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum ChatTokenizationOption {
   TTV_CHAT_TOKENIZATION_OPTION_NONE(0),
   TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_URLS(1),
   TTV_CHAT_TOKENIZATION_OPTION_EMOTICON_TEXTURES(2);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static ChatTokenizationOption lookupValue(int var0) {
      ChatTokenizationOption var1 = (ChatTokenizationOption)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   public static int getNativeValue(HashSet var0) {
      if(var0 == null) {
         return TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();
      } else {
         int var1 = TTV_CHAT_TOKENIZATION_OPTION_NONE.getValue();

         for(ChatTokenizationOption var3 : var0) {
            if(var3 != null) {
               var1 |= var3.getValue();
            }
         }

         return var1;
      }
   }

   private ChatTokenizationOption(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(ChatTokenizationOption var2 : EnumSet.allOf(ChatTokenizationOption.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
