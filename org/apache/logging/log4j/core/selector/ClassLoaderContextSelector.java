package org.apache.logging.log4j.core.selector;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.core.impl.ContextAnchor;
import org.apache.logging.log4j.core.impl.ReflectiveCallerClassUtility;
import org.apache.logging.log4j.core.selector.ContextSelector;
import org.apache.logging.log4j.status.StatusLogger;

public class ClassLoaderContextSelector implements ContextSelector {
   private static final AtomicReference CONTEXT = new AtomicReference();
   private static final ClassLoaderContextSelector.PrivateSecurityManager SECURITY_MANAGER;
   private static final StatusLogger LOGGER = StatusLogger.getLogger();
   private static final ConcurrentMap CONTEXT_MAP = new ConcurrentHashMap();

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext) {
      return this.getContext(fqcn, loader, currentContext, (URI)null);
   }

   public LoggerContext getContext(String fqcn, ClassLoader loader, boolean currentContext, URI configLocation) {
      if(currentContext) {
         LoggerContext ctx = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
         return ctx != null?ctx:this.getDefault();
      } else if(loader != null) {
         return this.locateContext(loader, configLocation);
      } else {
         if(ReflectiveCallerClassUtility.isSupported()) {
            try {
               Class<?> clazz = Class.class;
               boolean next = false;

               for(int index = 2; clazz != null; ++index) {
                  clazz = ReflectiveCallerClassUtility.getCaller(index);
                  if(clazz == null) {
                     break;
                  }

                  if(clazz.getName().equals(fqcn)) {
                     next = true;
                  } else if(next) {
                     break;
                  }
               }

               if(clazz != null) {
                  return this.locateContext(clazz.getClassLoader(), configLocation);
               }
            } catch (Exception var13) {
               ;
            }
         }

         if(SECURITY_MANAGER != null) {
            Class<?> clazz = SECURITY_MANAGER.getCaller(fqcn);
            if(clazz != null) {
               ClassLoader ldr = clazz.getClassLoader() != null?clazz.getClassLoader():ClassLoader.getSystemClassLoader();
               return this.locateContext(ldr, configLocation);
            }
         }

         Throwable t = new Throwable();
         boolean next = false;
         String name = null;

         for(StackTraceElement element : t.getStackTrace()) {
            if(element.getClassName().equals(fqcn)) {
               next = true;
            } else if(next) {
               name = element.getClassName();
               break;
            }
         }

         if(name != null) {
            try {
               return this.locateContext(Loader.loadClass(name).getClassLoader(), configLocation);
            } catch (ClassNotFoundException var12) {
               ;
            }
         }

         LoggerContext lc = (LoggerContext)ContextAnchor.THREAD_CONTEXT.get();
         return lc != null?lc:this.getDefault();
      }
   }

   public void removeContext(LoggerContext context) {
      for(Entry<String, AtomicReference<WeakReference<LoggerContext>>> entry : CONTEXT_MAP.entrySet()) {
         LoggerContext ctx = (LoggerContext)((WeakReference)((AtomicReference)entry.getValue()).get()).get();
         if(ctx == context) {
            CONTEXT_MAP.remove(entry.getKey());
         }
      }

   }

   public List getLoggerContexts() {
      List<LoggerContext> list = new ArrayList();

      for(AtomicReference<WeakReference<LoggerContext>> ref : CONTEXT_MAP.values()) {
         LoggerContext ctx = (LoggerContext)((WeakReference)ref.get()).get();
         if(ctx != null) {
            list.add(ctx);
         }
      }

      return Collections.unmodifiableList(list);
   }

   private LoggerContext locateContext(ClassLoader loader, URI configLocation) {
      String name = loader.toString();
      AtomicReference<WeakReference<LoggerContext>> ref = (AtomicReference)CONTEXT_MAP.get(name);
      if(ref == null) {
         if(configLocation == null) {
            for(ClassLoader parent = loader.getParent(); parent != null; parent = parent.getParent()) {
               ref = (AtomicReference)CONTEXT_MAP.get(parent.toString());
               if(ref != null) {
                  WeakReference<LoggerContext> r = (WeakReference)ref.get();
                  LoggerContext ctx = (LoggerContext)r.get();
                  if(ctx != null) {
                     return ctx;
                  }
               }
            }
         }

         LoggerContext ctx = new LoggerContext(name, (Object)null, configLocation);
         AtomicReference<WeakReference<LoggerContext>> r = new AtomicReference();
         r.set(new WeakReference(ctx));
         CONTEXT_MAP.putIfAbsent(loader.toString(), r);
         ctx = (LoggerContext)((WeakReference)((AtomicReference)CONTEXT_MAP.get(name)).get()).get();
         return ctx;
      } else {
         WeakReference<LoggerContext> r = (WeakReference)ref.get();
         LoggerContext ctx = (LoggerContext)r.get();
         if(ctx == null) {
            ctx = new LoggerContext(name, (Object)null, configLocation);
            ref.compareAndSet(r, new WeakReference(ctx));
            return ctx;
         } else {
            if(ctx.getConfigLocation() == null && configLocation != null) {
               LOGGER.debug("Setting configuration to {}", new Object[]{configLocation});
               ctx.setConfigLocation(configLocation);
            } else if(ctx.getConfigLocation() != null && configLocation != null && !ctx.getConfigLocation().equals(configLocation)) {
               LOGGER.warn("locateContext called with URI {}. Existing LoggerContext has URI {}", new Object[]{configLocation, ctx.getConfigLocation()});
            }

            return ctx;
         }
      }
   }

   private LoggerContext getDefault() {
      LoggerContext ctx = (LoggerContext)CONTEXT.get();
      if(ctx != null) {
         return ctx;
      } else {
         CONTEXT.compareAndSet((Object)null, new LoggerContext("Default"));
         return (LoggerContext)CONTEXT.get();
      }
   }

   static {
      if(ReflectiveCallerClassUtility.isSupported()) {
         SECURITY_MANAGER = null;
      } else {
         ClassLoaderContextSelector.PrivateSecurityManager securityManager;
         try {
            securityManager = new ClassLoaderContextSelector.PrivateSecurityManager();
            if(securityManager.getCaller(ClassLoaderContextSelector.class.getName()) == null) {
               securityManager = null;
               LOGGER.error("Unable to obtain call stack from security manager.");
            }
         } catch (Exception var2) {
            securityManager = null;
            LOGGER.debug("Unable to install security manager", var2);
         }

         SECURITY_MANAGER = securityManager;
      }

   }

   private static class PrivateSecurityManager extends SecurityManager {
      private PrivateSecurityManager() {
      }

      public Class getCaller(String fqcn) {
         Class<?>[] classes = this.getClassContext();
         boolean next = false;

         for(Class<?> clazz : classes) {
            if(clazz.getName().equals(fqcn)) {
               next = true;
            } else if(next) {
               return clazz;
            }
         }

         return null;
      }
   }
}
