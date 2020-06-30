package org.apache.commons.lang3.concurrent;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.ConcurrentInitializer;
import org.apache.commons.lang3.concurrent.ConcurrentRuntimeException;

public class ConcurrentUtils {
   public static ConcurrentException extractCause(ExecutionException ex) {
      if(ex != null && ex.getCause() != null) {
         throwCause(ex);
         return new ConcurrentException(ex.getMessage(), ex.getCause());
      } else {
         return null;
      }
   }

   public static ConcurrentRuntimeException extractCauseUnchecked(ExecutionException ex) {
      if(ex != null && ex.getCause() != null) {
         throwCause(ex);
         return new ConcurrentRuntimeException(ex.getMessage(), ex.getCause());
      } else {
         return null;
      }
   }

   public static void handleCause(ExecutionException ex) throws ConcurrentException {
      ConcurrentException cex = extractCause(ex);
      if(cex != null) {
         throw cex;
      }
   }

   public static void handleCauseUnchecked(ExecutionException ex) {
      ConcurrentRuntimeException crex = extractCauseUnchecked(ex);
      if(crex != null) {
         throw crex;
      }
   }

   static Throwable checkedException(Throwable ex) {
      if(ex != null && !(ex instanceof RuntimeException) && !(ex instanceof Error)) {
         return ex;
      } else {
         throw new IllegalArgumentException("Not a checked exception: " + ex);
      }
   }

   private static void throwCause(ExecutionException ex) {
      if(ex.getCause() instanceof RuntimeException) {
         throw (RuntimeException)ex.getCause();
      } else if(ex.getCause() instanceof Error) {
         throw (Error)ex.getCause();
      }
   }

   public static Object initialize(ConcurrentInitializer initializer) throws ConcurrentException {
      return initializer != null?initializer.get():null;
   }

   public static Object initializeUnchecked(ConcurrentInitializer initializer) {
      try {
         return initialize(initializer);
      } catch (ConcurrentException var2) {
         throw new ConcurrentRuntimeException(var2.getCause());
      }
   }

   public static Object putIfAbsent(ConcurrentMap map, Object key, Object value) {
      if(map == null) {
         return null;
      } else {
         V result = map.putIfAbsent(key, value);
         return result != null?result:value;
      }
   }

   public static Object createIfAbsent(ConcurrentMap map, Object key, ConcurrentInitializer init) throws ConcurrentException {
      if(map != null && init != null) {
         V value = map.get(key);
         return value == null?putIfAbsent(map, key, init.get()):value;
      } else {
         return null;
      }
   }

   public static Object createIfAbsentUnchecked(ConcurrentMap map, Object key, ConcurrentInitializer init) {
      try {
         return createIfAbsent(map, key, init);
      } catch (ConcurrentException var4) {
         throw new ConcurrentRuntimeException(var4.getCause());
      }
   }

   public static Future constantFuture(Object value) {
      return new ConcurrentUtils.ConstantFuture(value);
   }

   static final class ConstantFuture implements Future {
      private final Object value;

      ConstantFuture(Object value) {
         this.value = value;
      }

      public boolean isDone() {
         return true;
      }

      public Object get() {
         return this.value;
      }

      public Object get(long timeout, TimeUnit unit) {
         return this.value;
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }
   }
}
