package io.netty.util.internal.chmv8;

import io.netty.util.internal.chmv8.ForkJoinTask;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import sun.misc.Unsafe;

public abstract class CountedCompleter extends ForkJoinTask {
   private static final long serialVersionUID = 5232453752276485070L;
   final CountedCompleter completer;
   volatile int pending;
   private static final Unsafe U;
   private static final long PENDING;

   protected CountedCompleter(CountedCompleter completer, int initialPendingCount) {
      this.completer = completer;
      this.pending = initialPendingCount;
   }

   protected CountedCompleter(CountedCompleter completer) {
      this.completer = completer;
   }

   protected CountedCompleter() {
      this.completer = null;
   }

   public abstract void compute();

   public void onCompletion(CountedCompleter caller) {
   }

   public boolean onExceptionalCompletion(Throwable ex, CountedCompleter caller) {
      return true;
   }

   public final CountedCompleter getCompleter() {
      return this.completer;
   }

   public final int getPendingCount() {
      return this.pending;
   }

   public final void setPendingCount(int count) {
      this.pending = count;
   }

   public final void addToPendingCount(int delta) {
      while(true) {
         int c = this.pending;
         if(U.compareAndSwapInt(this, PENDING, this.pending, c + delta)) {
            break;
         }
      }

   }

   public final boolean compareAndSetPendingCount(int expected, int count) {
      return U.compareAndSwapInt(this, PENDING, expected, count);
   }

   public final int decrementPendingCountUnlessZero() {
      int c;
      while(true) {
         c = this.pending;
         if(this.pending == 0 || U.compareAndSwapInt(this, PENDING, c, c - 1)) {
            break;
         }
      }

      return c;
   }

   public final CountedCompleter getRoot() {
      CountedCompleter<?> a = this;

      while(true) {
         CountedCompleter<?> p = a.completer;
         if(a.completer == null) {
            return a;
         }

         a = p;
      }
   }

   public final void tryComplete() {
      CountedCompleter<?> a = this;
      CountedCompleter<?> s = this;

      while(true) {
         int c = a.pending;
         if(a.pending == 0) {
            a.onCompletion(s);
            s = a;
            if((a = a.completer) == null) {
               break;
            }
         } else if(U.compareAndSwapInt(a, PENDING, c, c - 1)) {
            return;
         }
      }

      s.quietlyComplete();
   }

   public final void propagateCompletion() {
      CountedCompleter<?> a = this;

      CountedCompleter s;
      while(true) {
         int c = a.pending;
         if(a.pending == 0) {
            s = a;
            if((a = a.completer) == null) {
               break;
            }
         } else if(U.compareAndSwapInt(a, PENDING, c, c - 1)) {
            return;
         }
      }

      s.quietlyComplete();
   }

   public void complete(Object rawResult) {
      this.setRawResult(rawResult);
      this.onCompletion(this);
      this.quietlyComplete();
      CountedCompleter<?> p = this.completer;
      if(this.completer != null) {
         p.tryComplete();
      }

   }

   public final CountedCompleter firstComplete() {
      while(true) {
         int c = this.pending;
         if(this.pending == 0) {
            return this;
         }

         if(U.compareAndSwapInt(this, PENDING, c, c - 1)) {
            break;
         }
      }

      return null;
   }

   public final CountedCompleter nextComplete() {
      CountedCompleter<?> p = this.completer;
      if(this.completer != null) {
         return p.firstComplete();
      } else {
         this.quietlyComplete();
         return null;
      }
   }

   public final void quietlyCompleteRoot() {
      CountedCompleter<?> a = this;

      while(true) {
         CountedCompleter<?> p = a.completer;
         if(a.completer == null) {
            a.quietlyComplete();
            return;
         }

         a = p;
      }
   }

   void internalPropagateException(Throwable ex) {
      CountedCompleter<?> a = this;
      CountedCompleter<?> s = this;

      while(a.onExceptionalCompletion(ex, s)) {
         s = a;
         if((a = a.completer) == null || a.status < 0 || a.recordExceptionalCompletion(ex) != Integer.MIN_VALUE) {
            break;
         }
      }

   }

   protected final boolean exec() {
      this.compute();
      return false;
   }

   public Object getRawResult() {
      return null;
   }

   protected void setRawResult(Object t) {
   }

   private static Unsafe getUnsafe() {
      try {
         return Unsafe.getUnsafe();
      } catch (SecurityException var2) {
         try {
            return (Unsafe)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Unsafe run() throws Exception {
                  Class<Unsafe> k = Unsafe.class;

                  for(Field f : k.getDeclaredFields()) {
                     f.setAccessible(true);
                     Object x = f.get((Object)null);
                     if(k.isInstance(x)) {
                        return (Unsafe)k.cast(x);
                     }
                  }

                  throw new NoSuchFieldError("the Unsafe");
               }
            });
         } catch (PrivilegedActionException var1) {
            throw new RuntimeException("Could not initialize intrinsics", var1.getCause());
         }
      }
   }

   static {
      try {
         U = getUnsafe();
         PENDING = U.objectFieldOffset(CountedCompleter.class.getDeclaredField("pending"));
      } catch (Exception var1) {
         throw new Error(var1);
      }
   }
}
