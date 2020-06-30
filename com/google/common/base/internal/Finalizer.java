package com.google.common.base.internal;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Finalizer implements Runnable {
   private static final Logger logger = Logger.getLogger(Finalizer.class.getName());
   private static final String FINALIZABLE_REFERENCE = "com.google.common.base.FinalizableReference";
   private final WeakReference finalizableReferenceClassReference;
   private final PhantomReference frqReference;
   private final ReferenceQueue queue;
   private static final Field inheritableThreadLocals = getInheritableThreadLocalsField();

   public static void startFinalizer(Class finalizableReferenceClass, ReferenceQueue queue, PhantomReference frqReference) {
      if(!finalizableReferenceClass.getName().equals("com.google.common.base.FinalizableReference")) {
         throw new IllegalArgumentException("Expected com.google.common.base.FinalizableReference.");
      } else {
         Finalizer finalizer = new Finalizer(finalizableReferenceClass, queue, frqReference);
         Thread thread = new Thread(finalizer);
         thread.setName(Finalizer.class.getName());
         thread.setDaemon(true);

         try {
            if(inheritableThreadLocals != null) {
               inheritableThreadLocals.set(thread, (Object)null);
            }
         } catch (Throwable var6) {
            logger.log(Level.INFO, "Failed to clear thread local values inherited by reference finalizer thread.", var6);
         }

         thread.start();
      }
   }

   private Finalizer(Class finalizableReferenceClass, ReferenceQueue queue, PhantomReference frqReference) {
      this.queue = queue;
      this.finalizableReferenceClassReference = new WeakReference(finalizableReferenceClass);
      this.frqReference = frqReference;
   }

   public void run() {
      while(true) {
         try {
            if(!this.cleanUp(this.queue.remove())) {
               return;
            }
         } catch (InterruptedException var2) {
            ;
         }
      }
   }

   private boolean cleanUp(Reference reference) {
      Method finalizeReferentMethod = this.getFinalizeReferentMethod();
      if(finalizeReferentMethod == null) {
         return false;
      } else {
         while(true) {
            reference.clear();
            if(reference == this.frqReference) {
               return false;
            }

            try {
               finalizeReferentMethod.invoke(reference, new Object[0]);
            } catch (Throwable var4) {
               logger.log(Level.SEVERE, "Error cleaning up after reference.", var4);
            }

            if((reference = this.queue.poll()) == null) {
               break;
            }
         }

         return true;
      }
   }

   private Method getFinalizeReferentMethod() {
      Class<?> finalizableReferenceClass = (Class)this.finalizableReferenceClassReference.get();
      if(finalizableReferenceClass == null) {
         return null;
      } else {
         try {
            return finalizableReferenceClass.getMethod("finalizeReferent", new Class[0]);
         } catch (NoSuchMethodException var3) {
            throw new AssertionError(var3);
         }
      }
   }

   public static Field getInheritableThreadLocalsField() {
      try {
         Field inheritableThreadLocals = Thread.class.getDeclaredField("inheritableThreadLocals");
         inheritableThreadLocals.setAccessible(true);
         return inheritableThreadLocals;
      } catch (Throwable var1) {
         logger.log(Level.INFO, "Couldn\'t access Thread.inheritableThreadLocals. Reference finalizer threads will inherit thread local values.");
         return null;
      }
   }
}
