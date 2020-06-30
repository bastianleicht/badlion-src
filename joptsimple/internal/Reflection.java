package joptsimple.internal;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import joptsimple.ValueConverter;
import joptsimple.internal.Classes;
import joptsimple.internal.ConstructorInvokingValueConverter;
import joptsimple.internal.MethodInvokingValueConverter;
import joptsimple.internal.ReflectionException;

public final class Reflection {
   private Reflection() {
      throw new UnsupportedOperationException();
   }

   public static ValueConverter findConverter(Class clazz) {
      Class<V> maybeWrapper = Classes.wrapperOf(clazz);
      ValueConverter<V> valueOf = valueOfConverter(maybeWrapper);
      if(valueOf != null) {
         return valueOf;
      } else {
         ValueConverter<V> constructor = constructorConverter(maybeWrapper);
         if(constructor != null) {
            return constructor;
         } else {
            throw new IllegalArgumentException(clazz + " is not a value type");
         }
      }
   }

   private static ValueConverter valueOfConverter(Class clazz) {
      try {
         Method valueOf = clazz.getDeclaredMethod("valueOf", new Class[]{String.class});
         return meetsConverterRequirements(valueOf, clazz)?new MethodInvokingValueConverter(valueOf, clazz):null;
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   private static ValueConverter constructorConverter(Class clazz) {
      try {
         return new ConstructorInvokingValueConverter(clazz.getConstructor(new Class[]{String.class}));
      } catch (NoSuchMethodException var2) {
         return null;
      }
   }

   public static Object instantiate(Constructor constructor, Object... args) {
      try {
         return constructor.newInstance(args);
      } catch (Exception var3) {
         throw reflectionException(var3);
      }
   }

   public static Object invoke(Method method, Object... args) {
      try {
         return method.invoke((Object)null, args);
      } catch (Exception var3) {
         throw reflectionException(var3);
      }
   }

   public static Object convertWith(ValueConverter converter, String raw) {
      return converter == null?raw:converter.convert(raw);
   }

   private static boolean meetsConverterRequirements(Method method, Class expectedReturnType) {
      int modifiers = method.getModifiers();
      return Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && expectedReturnType.equals(method.getReturnType());
   }

   private static RuntimeException reflectionException(Exception ex) {
      return (RuntimeException)(ex instanceof IllegalArgumentException?new ReflectionException(ex):(ex instanceof InvocationTargetException?new ReflectionException(ex.getCause()):(ex instanceof RuntimeException?(RuntimeException)ex:new ReflectionException(ex))));
   }
}
