package tv.twitch.broadcast;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum PixelFormat {
   TTV_PF_BGRA(66051),
   TTV_PF_ABGR(16909056),
   TTV_PF_RGBA(33619971),
   TTV_PF_ARGB(50462976);

   private static Map s_Map = new HashMap();
   private int m_Value;

   public static PixelFormat lookupValue(int var0) {
      PixelFormat var1 = (PixelFormat)s_Map.get(Integer.valueOf(var0));
      return var1;
   }

   private PixelFormat(int var3) {
      this.m_Value = var3;
   }

   public int getValue() {
      return this.m_Value;
   }

   static {
      for(PixelFormat var2 : EnumSet.allOf(PixelFormat.class)) {
         s_Map.put(Integer.valueOf(var2.getValue()), var2);
      }

   }
}
