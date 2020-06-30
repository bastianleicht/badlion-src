package com.google.gson.internal;

import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class Primitives {
   private static final Map PRIMITIVE_TO_WRAPPER_TYPE;
   private static final Map WRAPPER_TO_PRIMITIVE_TYPE;

   private static void add(Map forward, Map backward, Class key, Class value) {
      forward.put(key, value);
      backward.put(value, key);
   }

   public static boolean isPrimitive(Type type) {
      return PRIMITIVE_TO_WRAPPER_TYPE.containsKey(type);
   }

   public static boolean isWrapperType(Type type) {
      return WRAPPER_TO_PRIMITIVE_TYPE.containsKey($Gson$Preconditions.checkNotNull(type));
   }

   public static Class wrap(Class type) {
      Class<T> wrapped = (Class)PRIMITIVE_TO_WRAPPER_TYPE.get($Gson$Preconditions.checkNotNull(type));
      return wrapped == null?type:wrapped;
   }

   public static Class unwrap(Class type) {
      Class<T> unwrapped = (Class)WRAPPER_TO_PRIMITIVE_TYPE.get($Gson$Preconditions.checkNotNull(type));
      return unwrapped == null?type:unwrapped;
   }

   static {
      Map<Class<?>, Class<?>> primToWrap = new HashMap(16);
      Map<Class<?>, Class<?>> wrapToPrim = new HashMap(16);
      add(primToWrap, wrapToPrim, Boolean.TYPE, Boolean.class);
      add(primToWrap, wrapToPrim, Byte.TYPE, Byte.class);
      add(primToWrap, wrapToPrim, Character.TYPE, Character.class);
      add(primToWrap, wrapToPrim, Double.TYPE, Double.class);
      add(primToWrap, wrapToPrim, Float.TYPE, Float.class);
      add(primToWrap, wrapToPrim, Integer.TYPE, Integer.class);
      add(primToWrap, wrapToPrim, Long.TYPE, Long.class);
      add(primToWrap, wrapToPrim, Short.TYPE, Short.class);
      add(primToWrap, wrapToPrim, Void.TYPE, Void.class);
      PRIMITIVE_TO_WRAPPER_TYPE = Collections.unmodifiableMap(primToWrap);
      WRAPPER_TO_PRIMITIVE_TYPE = Collections.unmodifiableMap(wrapToPrim);
   }
}
