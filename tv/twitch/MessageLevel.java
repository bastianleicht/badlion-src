package tv.twitch;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum MessageLevel {
   TTV_ML_DEBUG(0),
   TTV_ML_INFO(1),
   TTV_ML_WARNING(2),
   TTV_ML_ERROR(3),
   TTV_ML_CHAT(4),
   TTV_ML_NONE(5);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static MessageLevel lookupValue(int var0) {
      MessageLevel var1 = (MessageLevel)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private MessageLevel(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(MessageLevel var2 : EnumSet.allOf(MessageLevel.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
