package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

@Beta
public final class Reflection {
   public static String getPackageName(Class clazz) {
      return getPackageName(clazz.getName());
   }

   public static String getPackageName(String classFullName) {
      int lastDot = classFullName.lastIndexOf(46);
      return lastDot < 0?"":classFullName.substring(0, lastDot);
   }

   public static void initialize(Class... classes) {
      for(Class<?> clazz : classes) {
         try {
            Class.forName(clazz.getName(), true, clazz.getClassLoader());
         } catch (ClassNotFoundException var6) {
            throw new AssertionError(var6);
         }
      }

   }

   public static Object newProxy(Class interfaceType, InvocationHandler handler) {
      Preconditions.checkNotNull(handler);
      Preconditions.checkArgument(interfaceType.isInterface(), "%s is not an interface", new Object[]{interfaceType});
      Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class[]{interfaceType}, handler);
      return interfaceType.cast(object);
   }
}
