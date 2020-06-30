package org.apache.commons.lang3.text;

import java.util.Map;

public abstract class StrLookup {
   private static final StrLookup NONE_LOOKUP = new StrLookup.MapStrLookup((Map)null);
   private static final StrLookup SYSTEM_PROPERTIES_LOOKUP;

   public static StrLookup noneLookup() {
      return NONE_LOOKUP;
   }

   public static StrLookup systemPropertiesLookup() {
      return SYSTEM_PROPERTIES_LOOKUP;
   }

   public static StrLookup mapLookup(Map map) {
      return new StrLookup.MapStrLookup(map);
   }

   public abstract String lookup(String var1);

   static {
      StrLookup<String> lookup = null;

      try {
         Map<?, ?> propMap = System.getProperties();
         lookup = new StrLookup.MapStrLookup(propMap);
      } catch (SecurityException var3) {
         lookup = NONE_LOOKUP;
      }

      SYSTEM_PROPERTIES_LOOKUP = lookup;
   }

   static class MapStrLookup extends StrLookup {
      private final Map map;

      MapStrLookup(Map map) {
         this.map = map;
      }

      public String lookup(String key) {
         if(this.map == null) {
            return null;
         } else {
            Object obj = this.map.get(key);
            return obj == null?null:obj.toString();
         }
      }
   }
}
