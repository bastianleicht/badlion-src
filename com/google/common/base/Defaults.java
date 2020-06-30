package com.google.common.base;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Defaults {
   private static final Map DEFAULTS;

   private static void put(Map map, Class type, Object value) {
      map.put(type, value);
   }

   public static Object defaultValue(Class type) {
      T t = DEFAULTS.get(Preconditions.checkNotNull(type));
      return t;
   }

   static {
      Map<Class<?>, Object> map = new HashMap();
      put(map, Boolean.TYPE, Boolean.valueOf(false));
      put(map, Character.TYPE, Character.valueOf('\u0000'));
      put(map, Byte.TYPE, Byte.valueOf((byte)0));
      put(map, Short.TYPE, Short.valueOf((short)0));
      put(map, Integer.TYPE, Integer.valueOf(0));
      put(map, Long.TYPE, Long.valueOf(0L));
      put(map, Float.TYPE, Float.valueOf(0.0F));
      put(map, Double.TYPE, Double.valueOf(0.0D));
      DEFAULTS = Collections.unmodifiableMap(map);
   }
}
