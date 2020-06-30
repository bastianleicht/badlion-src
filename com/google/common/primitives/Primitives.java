package com.google.common.primitives;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public final class Primitives {
   private static final Map PRIMITIVE_TO_WRAPPER_TYPE;
   private static final Map WRAPPER_TO_PRIMITIVE_TYPE;

   private static void add(Map forward, Map backward, Class key, Class value) {
      forward.put(key, value);
      backward.put(value, key);
   }

   public static Set allPrimitiveTypes() {
      return PRIMITIVE_TO_WRAPPER_TYPE.keySet();
   }

   public static Set allWrapperTypes() {
      return WRAPPER_TO_PRIMITIVE_TYPE.keySet();
   }

   public static boolean isWrapperType(Class type) {
      return WRAPPER_TO_PRIMITIVE_TYPE.containsKey(Preconditions.checkNotNull(type));
   }

   public static Class wrap(Class type) {
      Preconditions.checkNotNull(type);
      Class<T> wrapped = (Class)PRIMITIVE_TO_WRAPPER_TYPE.get(type);
      return wrapped == null?type:wrapped;
   }

   public static Class unwrap(Class type) {
      Preconditions.checkNotNull(type);
      Class<T> unwrapped = (Class)WRAPPER_TO_PRIMITIVE_TYPE.get(type);
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
