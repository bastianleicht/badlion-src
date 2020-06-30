package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public enum AuthFlag {
   TTV_AuthOption_None(0),
   TTV_AuthOption_Broadcast(1),
   TTV_AuthOption_Chat(2);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static AuthFlag lookupValue(int var0) {
      AuthFlag var1 = (AuthFlag)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   public static int getNativeValue(HashSet var0) {
      if(var0 == null) {
         return TTV_AuthOption_None.getValue();
      } else {
         int var1 = 0;

         for(AuthFlag var3 : var0) {
            if(var3 != null) {
               var1 |= var3.getValue();
            }
         }

         return var1;
      }
   }

   private AuthFlag(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(AuthFlag var2 : EnumSet.allOf(AuthFlag.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
