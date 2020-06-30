package org.apache.commons.logging.impl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.LogFactory;

public class ServletContextCleaner implements ServletContextListener {
   private static final Class[] RELEASE_SIGNATURE;
   // $FF: synthetic field
   static Class class$java$lang$ClassLoader;

   public void contextDestroyed(ServletContextEvent sce) {
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      Object[] params = new Object[]{tccl};
      ClassLoader loader = tccl;

      while(loader != null) {
         try {
            Class logFactoryClass = loader.loadClass("org.apache.commons.logging.LogFactory");
            Method releaseMethod = logFactoryClass.getMethod("release", RELEASE_SIGNATURE);
            releaseMethod.invoke((Object)null, params);
            loader = logFactoryClass.getClassLoader().getParent();
         } catch (ClassNotFoundException var7) {
            loader = null;
         } catch (NoSuchMethodException var8) {
            System.err.println("LogFactory instance found which does not support release method!");
            loader = null;
         } catch (IllegalAccessException var9) {
            System.err.println("LogFactory instance found which is not accessable!");
            loader = null;
         } catch (InvocationTargetException var10) {
            System.err.println("LogFactory instance release method failed!");
            loader = null;
         }
      }

      LogFactory.release(tccl);
   }

   public void contextInitialized(ServletContextEvent sce) {
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   static {
      RELEASE_SIGNATURE = new Class[]{class$java$lang$ClassLoader == null?(class$java$lang$ClassLoader = class$("java.lang.ClassLoader")):class$java$lang$ClassLoader};
   }
}
