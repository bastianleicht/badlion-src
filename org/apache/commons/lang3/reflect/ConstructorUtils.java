package org.apache.commons.lang3.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.MemberUtils;

public class ConstructorUtils {
   public static Object invokeConstructor(Class cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeConstructor(cls, args, parameterTypes);
   }

   public static Object invokeConstructor(Class cls, Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      args = ArrayUtils.nullToEmpty(args);
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      Constructor<T> ctor = getMatchingAccessibleConstructor(cls, parameterTypes);
      if(ctor == null) {
         throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
      } else {
         return ctor.newInstance(args);
      }
   }

   public static Object invokeExactConstructor(Class cls, Object... args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      args = ArrayUtils.nullToEmpty(args);
      Class<?>[] parameterTypes = ClassUtils.toClass(args);
      return invokeExactConstructor(cls, args, parameterTypes);
   }

   public static Object invokeExactConstructor(Class cls, Object[] args, Class[] parameterTypes) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
      args = ArrayUtils.nullToEmpty(args);
      parameterTypes = ArrayUtils.nullToEmpty(parameterTypes);
      Constructor<T> ctor = getAccessibleConstructor(cls, parameterTypes);
      if(ctor == null) {
         throw new NoSuchMethodException("No such accessible constructor on object: " + cls.getName());
      } else {
         return ctor.newInstance(args);
      }
   }

   public static Constructor getAccessibleConstructor(Class cls, Class... parameterTypes) {
      Validate.notNull(cls, "class cannot be null", new Object[0]);

      try {
         return getAccessibleConstructor(cls.getConstructor(parameterTypes));
      } catch (NoSuchMethodException var3) {
         return null;
      }
   }

   public static Constructor getAccessibleConstructor(Constructor ctor) {
      Validate.notNull(ctor, "constructor cannot be null", new Object[0]);
      return MemberUtils.isAccessible(ctor) && isAccessible(ctor.getDeclaringClass())?ctor:null;
   }

   public static Constructor getMatchingAccessibleConstructor(Class cls, Class... parameterTypes) {
      Validate.notNull(cls, "class cannot be null", new Object[0]);

      try {
         Constructor<T> ctor = cls.getConstructor(parameterTypes);
         MemberUtils.setAccessibleWorkaround(ctor);
         return ctor;
      } catch (NoSuchMethodException var9) {
         Constructor<T> result = null;
         Constructor<?>[] ctors = cls.getConstructors();

         for(Constructor<?> ctor : ctors) {
            if(ClassUtils.isAssignable(parameterTypes, ctor.getParameterTypes(), true)) {
               ctor = getAccessibleConstructor(ctor);
               if(ctor != null) {
                  MemberUtils.setAccessibleWorkaround(ctor);
                  if(result == null || MemberUtils.compareParameterTypes(ctor.getParameterTypes(), result.getParameterTypes(), parameterTypes) < 0) {
                     result = ctor;
                  }
               }
            }
         }

         return result;
      }
   }

   private static boolean isAccessible(Class type) {
      for(Class<?> cls = type; cls != null; cls = cls.getEnclosingClass()) {
         if(!Modifier.isPublic(cls.getModifiers())) {
            return false;
         }
      }

      return true;
   }
}
