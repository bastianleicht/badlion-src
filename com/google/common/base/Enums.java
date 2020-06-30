package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.Converter;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Platform;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import javax.annotation.Nullable;

@GwtCompatible(
   emulated = true
)
@Beta
public final class Enums {
   @GwtIncompatible("java.lang.ref.WeakReference")
   private static final Map enumConstantCache = new WeakHashMap();

   @GwtIncompatible("reflection")
   public static Field getField(Enum enumValue) {
      Class<?> clazz = enumValue.getDeclaringClass();

      try {
         return clazz.getDeclaredField(enumValue.name());
      } catch (NoSuchFieldException var3) {
         throw new AssertionError(var3);
      }
   }

   /** @deprecated */
   @Deprecated
   public static Function valueOfFunction(Class enumClass) {
      return new Enums.ValueOfFunction(enumClass);
   }

   public static Optional getIfPresent(Class enumClass, String value) {
      Preconditions.checkNotNull(enumClass);
      Preconditions.checkNotNull(value);
      return Platform.getEnumIfPresent(enumClass, value);
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   private static Map populateCache(Class enumClass) {
      Map<String, WeakReference<? extends Enum<?>>> result = new HashMap();

      for(T enumInstance : EnumSet.allOf(enumClass)) {
         result.put(enumInstance.name(), new WeakReference(enumInstance));
      }

      enumConstantCache.put(enumClass, result);
      return result;
   }

   @GwtIncompatible("java.lang.ref.WeakReference")
   static Map getEnumConstants(Class enumClass) {
      synchronized(enumConstantCache) {
         Map<String, WeakReference<? extends Enum<?>>> constants = (Map)enumConstantCache.get(enumClass);
         if(constants == null) {
            constants = populateCache(enumClass);
         }

         return constants;
      }
   }

   public static Converter stringConverter(Class enumClass) {
      return new Enums.StringConverter(enumClass);
   }

   private static final class StringConverter extends Converter implements Serializable {
      private final Class enumClass;
      private static final long serialVersionUID = 0L;

      StringConverter(Class enumClass) {
         this.enumClass = (Class)Preconditions.checkNotNull(enumClass);
      }

      protected Enum doForward(String value) {
         return Enum.valueOf(this.enumClass, value);
      }

      protected String doBackward(Enum enumValue) {
         return enumValue.name();
      }

      public boolean equals(@Nullable Object object) {
         if(object instanceof Enums.StringConverter) {
            Enums.StringConverter<?> that = (Enums.StringConverter)object;
            return this.enumClass.equals(that.enumClass);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.enumClass.hashCode();
      }

      public String toString() {
         return "Enums.stringConverter(" + this.enumClass.getName() + ".class)";
      }
   }

   private static final class ValueOfFunction implements Function, Serializable {
      private final Class enumClass;
      private static final long serialVersionUID = 0L;

      private ValueOfFunction(Class enumClass) {
         this.enumClass = (Class)Preconditions.checkNotNull(enumClass);
      }

      public Enum apply(String value) {
         try {
            return Enum.valueOf(this.enumClass, value);
         } catch (IllegalArgumentException var3) {
            return null;
         }
      }

      public boolean equals(@Nullable Object obj) {
         return obj instanceof Enums.ValueOfFunction && this.enumClass.equals(((Enums.ValueOfFunction)obj).enumClass);
      }

      public int hashCode() {
         return this.enumClass.hashCode();
      }

      public String toString() {
         return "Enums.valueOf(" + this.enumClass + ")";
      }
   }
}
