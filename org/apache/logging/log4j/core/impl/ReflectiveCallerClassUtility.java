package org.apache.logging.log4j.core.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.status.StatusLogger;

public final class ReflectiveCallerClassUtility {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final boolean GET_CALLER_CLASS_SUPPORTED;
   private static final Method GET_CALLER_CLASS_METHOD;
   static final int JAVA_7U25_COMPENSATION_OFFSET;

   public static boolean isSupported() {
      return GET_CALLER_CLASS_SUPPORTED;
   }

   public static Class getCaller(int depth) {
      if(!GET_CALLER_CLASS_SUPPORTED) {
         return null;
      } else {
         try {
            return (Class)GET_CALLER_CLASS_METHOD.invoke((Object)null, new Object[]{Integer.valueOf(depth + 1 + JAVA_7U25_COMPENSATION_OFFSET)});
         } catch (IllegalAccessException var2) {
            LOGGER.warn("Should not have failed to call getCallerClass.");
         } catch (InvocationTargetException var3) {
            LOGGER.warn("Should not have failed to call getCallerClass.");
         }

         return null;
      }
   }

   static {
      Method getCallerClass = null;
      int java7u25CompensationOffset = 0;

      try {
         ClassLoader loader = Loader.getClassLoader();
         Class<?> clazz = loader.loadClass("sun.reflect.Reflection");
         Method[] methods = clazz.getMethods();

         for(Method method : methods) {
            int modifier = method.getModifiers();
            Class<?>[] parameterTypes = method.getParameterTypes();
            if(method.getName().equals("getCallerClass") && Modifier.isStatic(modifier) && parameterTypes.length == 1 && parameterTypes[0] == Integer.TYPE) {
               getCallerClass = method;
               break;
            }
         }

         if(getCallerClass == null) {
            LOGGER.info("sun.reflect.Reflection#getCallerClass does not exist.");
         } else {
            Object o = getCallerClass.invoke((Object)null, new Object[]{Integer.valueOf(0)});
            if(o != null && o == clazz) {
               o = getCallerClass.invoke((Object)null, new Object[]{Integer.valueOf(1)});
               if(o == clazz) {
                  java7u25CompensationOffset = 1;
                  LOGGER.warn("sun.reflect.Reflection#getCallerClass is broken in Java 7u25. You should upgrade to 7u40. Using alternate stack offset to compensate.");
               }
            } else {
               getCallerClass = null;
               LOGGER.warn("sun.reflect.Reflection#getCallerClass returned unexpected value of [{}] and is unusable. Will fall back to another option.", new Object[]{o});
            }
         }
      } catch (ClassNotFoundException var11) {
         LOGGER.info("sun.reflect.Reflection is not installed.");
      } catch (IllegalAccessException var12) {
         LOGGER.info("sun.reflect.Reflection#getCallerClass is not accessible.");
      } catch (InvocationTargetException var13) {
         LOGGER.info("sun.reflect.Reflection#getCallerClass is not supported.");
      }

      if(getCallerClass == null) {
         GET_CALLER_CLASS_SUPPORTED = false;
         GET_CALLER_CLASS_METHOD = null;
         JAVA_7U25_COMPENSATION_OFFSET = -1;
      } else {
         GET_CALLER_CLASS_SUPPORTED = true;
         GET_CALLER_CLASS_METHOD = getCallerClass;
         JAVA_7U25_COMPENSATION_OFFSET = java7u25CompensationOffset;
      }

   }
}
