package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Level;
import javax.annotation.Nullable;

@Beta
public final class Closer implements Closeable {
   private static final Closer.Suppressor SUPPRESSOR = Closer.SuppressingSuppressor.isAvailable()?Closer.SuppressingSuppressor.INSTANCE:Closer.LoggingSuppressor.INSTANCE;
   @VisibleForTesting
   final Closer.Suppressor suppressor;
   private final Deque stack = new ArrayDeque(4);
   private Throwable thrown;

   public static Closer create() {
      return new Closer(SUPPRESSOR);
   }

   @VisibleForTesting
   Closer(Closer.Suppressor suppressor) {
      this.suppressor = (Closer.Suppressor)Preconditions.checkNotNull(suppressor);
   }

   public Closeable register(@Nullable Closeable closeable) {
      if(closeable != null) {
         this.stack.addFirst(closeable);
      }

      return closeable;
   }

   public RuntimeException rethrow(Throwable e) throws IOException {
      Preconditions.checkNotNull(e);
      this.thrown = e;
      Throwables.propagateIfPossible(e, IOException.class);
      throw new RuntimeException(e);
   }

   public RuntimeException rethrow(Throwable e, Class declaredType) throws IOException, Exception {
      Preconditions.checkNotNull(e);
      this.thrown = e;
      Throwables.propagateIfPossible(e, IOException.class);
      Throwables.propagateIfPossible(e, declaredType);
      throw new RuntimeException(e);
   }

   public RuntimeException rethrow(Throwable e, Class declaredType1, Class declaredType2) throws IOException, Exception, Exception {
      Preconditions.checkNotNull(e);
      this.thrown = e;
      Throwables.propagateIfPossible(e, IOException.class);
      Throwables.propagateIfPossible(e, declaredType1, declaredType2);
      throw new RuntimeException(e);
   }

   public void close() throws IOException {
      Throwable throwable = this.thrown;

      while(!this.stack.isEmpty()) {
         Closeable closeable = (Closeable)this.stack.removeFirst();

         try {
            closeable.close();
         } catch (Throwable var4) {
            if(throwable == null) {
               throwable = var4;
            } else {
               this.suppressor.suppress(closeable, throwable, var4);
            }
         }
      }

      if(this.thrown == null && throwable != null) {
         Throwables.propagateIfPossible(throwable, IOException.class);
         throw new AssertionError(throwable);
      }
   }

   @VisibleForTesting
   static final class LoggingSuppressor implements Closer.Suppressor {
      static final Closer.LoggingSuppressor INSTANCE = new Closer.LoggingSuppressor();

      public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
         Closeables.logger.log(Level.WARNING, "Suppressing exception thrown when closing " + closeable, suppressed);
      }
   }

   @VisibleForTesting
   static final class SuppressingSuppressor implements Closer.Suppressor {
      static final Closer.SuppressingSuppressor INSTANCE = new Closer.SuppressingSuppressor();
      static final Method addSuppressed = getAddSuppressed();

      static boolean isAvailable() {
         return addSuppressed != null;
      }

      private static Method getAddSuppressed() {
         try {
            return Throwable.class.getMethod("addSuppressed", new Class[]{Throwable.class});
         } catch (Throwable var1) {
            return null;
         }
      }

      public void suppress(Closeable closeable, Throwable thrown, Throwable suppressed) {
         if(thrown != suppressed) {
            try {
               addSuppressed.invoke(thrown, new Object[]{suppressed});
            } catch (Throwable var5) {
               Closer.LoggingSuppressor.INSTANCE.suppress(closeable, thrown, suppressed);
            }

         }
      }
   }

   @VisibleForTesting
   interface Suppressor {
      void suppress(Closeable var1, Throwable var2, Throwable var3);
   }
}
