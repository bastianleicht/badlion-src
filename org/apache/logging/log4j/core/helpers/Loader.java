package org.apache.logging.log4j.core.helpers;

import java.io.InputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.OptionConverter;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class Loader {
   private static boolean ignoreTCL = false;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String TSTR = "Caught Exception while in Loader.getResource. This may be innocuous.";

   public static ClassLoader getClassLoader() {
      return getClassLoader(Loader.class, (Class)null);
   }

   public static ClassLoader getClassLoader(Class class1, Class class2) {
      ClassLoader loader1 = null;

      try {
         loader1 = getTCL();
      } catch (Exception var5) {
         LOGGER.warn("Caught exception locating thread ClassLoader {}", new Object[]{var5.getMessage()});
      }

      ClassLoader loader2 = class1 == null?null:class1.getClassLoader();
      ClassLoader loader3 = class2 == null?null:class2.getClass().getClassLoader();
      return isChild(loader1, loader2)?(isChild(loader1, loader3)?loader1:loader3):(isChild(loader2, loader3)?loader2:loader3);
   }

   public static URL getResource(String resource, ClassLoader defaultLoader) {
      try {
         ClassLoader classLoader = getTCL();
         if(classLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using context classloader " + classLoader + '.');
            URL url = classLoader.getResource(resource);
            if(url != null) {
               return url;
            }
         }

         classLoader = Loader.class.getClassLoader();
         if(classLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using " + classLoader + " class loader.");
            URL url = classLoader.getResource(resource);
            if(url != null) {
               return url;
            }
         }

         if(defaultLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using " + defaultLoader + " class loader.");
            URL url = defaultLoader.getResource(resource);
            if(url != null) {
               return url;
            }
         }
      } catch (Throwable var4) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var4);
      }

      LOGGER.trace("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
      return ClassLoader.getSystemResource(resource);
   }

   public static InputStream getResourceAsStream(String resource, ClassLoader defaultLoader) {
      try {
         ClassLoader classLoader = getTCL();
         if(classLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using context classloader " + classLoader + '.');
            InputStream is = classLoader.getResourceAsStream(resource);
            if(is != null) {
               return is;
            }
         }

         classLoader = Loader.class.getClassLoader();
         if(classLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using " + classLoader + " class loader.");
            InputStream is = classLoader.getResourceAsStream(resource);
            if(is != null) {
               return is;
            }
         }

         if(defaultLoader != null) {
            LOGGER.trace("Trying to find [" + resource + "] using " + defaultLoader + " class loader.");
            InputStream is = defaultLoader.getResourceAsStream(resource);
            if(is != null) {
               return is;
            }
         }
      } catch (Throwable var5) {
         LOGGER.warn("Caught Exception while in Loader.getResource. This may be innocuous.", var5);
      }

      LOGGER.trace("Trying to find [" + resource + "] using ClassLoader.getSystemResource().");
      return ClassLoader.getSystemResourceAsStream(resource);
   }

   private static ClassLoader getTCL() {
      ClassLoader cl;
      if(System.getSecurityManager() == null) {
         cl = Thread.currentThread().getContextClassLoader();
      } else {
         cl = (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public ClassLoader run() {
               return Thread.currentThread().getContextClassLoader();
            }
         });
      }

      return cl;
   }

   private static boolean isChild(ClassLoader loader1, ClassLoader loader2) {
      if(loader1 != null && loader2 != null) {
         ClassLoader parent;
         for(parent = loader1.getParent(); parent != null && parent != loader2; parent = parent.getParent()) {
            ;
         }

         return parent != null;
      } else {
         return loader1 != null;
      }
   }

   public static Class loadClass(String className) throws ClassNotFoundException {
      if(ignoreTCL) {
         return Class.forName(className);
      } else {
         try {
            return getTCL().loadClass(className);
         } catch (Throwable var2) {
            return Class.forName(className);
         }
      }
   }

   static {
      String ignoreTCLProp = PropertiesUtil.getProperties().getStringProperty("log4j.ignoreTCL", (String)null);
      if(ignoreTCLProp != null) {
         ignoreTCL = OptionConverter.toBoolean(ignoreTCLProp, true);
      }

   }
}
