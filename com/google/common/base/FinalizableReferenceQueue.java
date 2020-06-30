package com.google.common.base;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.FinalizableReference;
import java.io.Closeable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FinalizableReferenceQueue implements Closeable {
   private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());
   private static final String FINALIZER_CLASS_NAME = "com.google.common.base.internal.Finalizer";
   private static final Method startFinalizer;
   final ReferenceQueue queue = new ReferenceQueue();
   final PhantomReference frqRef;
   final boolean threadStarted;

   public FinalizableReferenceQueue() {
      this.frqRef = new PhantomReference(this, this.queue);
      boolean threadStarted = false;

      try {
         startFinalizer.invoke((Object)null, new Object[]{FinalizableReference.class, this.queue, this.frqRef});
         threadStarted = true;
      } catch (IllegalAccessException var3) {
         throw new AssertionError(var3);
      } catch (Throwable var4) {
         logger.log(Level.INFO, "Failed to start reference finalizer thread. Reference cleanup will only occur when new references are created.", var4);
      }

      this.threadStarted = threadStarted;
   }

   public void close() {
      this.frqRef.enqueue();
      this.cleanUp();
   }

   void cleanUp() {
      if(!this.threadStarted) {
         Reference<?> reference;
         while((reference = this.queue.poll()) != null) {
            reference.clear();

            try {
               ((FinalizableReference)reference).finalizeReferent();
            } catch (Throwable var3) {
               logger.log(Level.SEVERE, "Error cleaning up after reference.", var3);
            }
         }

      }
   }

   private static Class loadFinalizer(FinalizableReferenceQueue.FinalizerLoader... loaders) {
      for(FinalizableReferenceQueue.FinalizerLoader loader : loaders) {
         Class<?> finalizer = loader.loadFinalizer();
         if(finalizer != null) {
            return finalizer;
         }
      }

      throw new AssertionError();
   }

   static Method getStartFinalizer(Class finalizer) {
      try {
         return finalizer.getMethod("startFinalizer", new Class[]{Class.class, ReferenceQueue.class, PhantomReference.class});
      } catch (NoSuchMethodException var2) {
         throw new AssertionError(var2);
      }
   }

   static {
      Class<?> finalizer = loadFinalizer(new FinalizableReferenceQueue.FinalizerLoader[]{new FinalizableReferenceQueue.SystemLoader(), new FinalizableReferenceQueue.DecoupledLoader(), new FinalizableReferenceQueue.DirectLoader()});
      startFinalizer = getStartFinalizer(finalizer);
   }

   static class DecoupledLoader implements FinalizableReferenceQueue.FinalizerLoader {
      private static final String LOADING_ERROR = "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.";

      public Class loadFinalizer() {
         try {
            ClassLoader finalizerLoader = this.newLoader(this.getBaseUrl());
            return finalizerLoader.loadClass("com.google.common.base.internal.Finalizer");
         } catch (Exception var2) {
            FinalizableReferenceQueue.logger.log(Level.WARNING, "Could not load Finalizer in its own class loader.Loading Finalizer in the current class loader instead. As a result, you will not be ableto garbage collect this class loader. To support reclaiming this class loader, eitherresolve the underlying issue, or move Google Collections to your system class path.", var2);
            return null;
         }
      }

      URL getBaseUrl() throws IOException {
         String finalizerPath = "com.google.common.base.internal.Finalizer".replace('.', '/') + ".class";
         URL finalizerUrl = this.getClass().getClassLoader().getResource(finalizerPath);
         if(finalizerUrl == null) {
            throw new FileNotFoundException(finalizerPath);
         } else {
            String urlString = finalizerUrl.toString();
            if(!urlString.endsWith(finalizerPath)) {
               throw new IOException("Unsupported path style: " + urlString);
            } else {
               urlString = urlString.substring(0, urlString.length() - finalizerPath.length());
               return new URL(finalizerUrl, urlString);
            }
         }
      }

      URLClassLoader newLoader(URL base) {
         return new URLClassLoader(new URL[]{base}, (ClassLoader)null);
      }
   }

   static class DirectLoader implements FinalizableReferenceQueue.FinalizerLoader {
      public Class loadFinalizer() {
         try {
            return Class.forName("com.google.common.base.internal.Finalizer");
         } catch (ClassNotFoundException var2) {
            throw new AssertionError(var2);
         }
      }
   }

   interface FinalizerLoader {
      Class loadFinalizer();
   }

   static class SystemLoader implements FinalizableReferenceQueue.FinalizerLoader {
      @VisibleForTesting
      static boolean disabled;

      public Class loadFinalizer() {
         if(disabled) {
            return null;
         } else {
            ClassLoader systemLoader;
            try {
               systemLoader = ClassLoader.getSystemClassLoader();
            } catch (SecurityException var4) {
               FinalizableReferenceQueue.logger.info("Not allowed to access system class loader.");
               return null;
            }

            if(systemLoader != null) {
               try {
                  return systemLoader.loadClass("com.google.common.base.internal.Finalizer");
               } catch (ClassNotFoundException var3) {
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }
}
