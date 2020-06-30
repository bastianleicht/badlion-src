package com.google.common.util.concurrent;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractCheckedFuture;
import com.google.common.util.concurrent.AbstractFuture;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.AsyncSettableFuture;
import com.google.common.util.concurrent.CheckedFuture;
import com.google.common.util.concurrent.ExecutionError;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.FutureFallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.SerializingExecutor;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.common.util.concurrent.Uninterruptibles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class Futures {
   private static final AsyncFunction DEREFERENCER = new AsyncFunction() {
      public ListenableFuture apply(ListenableFuture input) {
         return input;
      }
   };
   private static final Ordering WITH_STRING_PARAM_FIRST = Ordering.natural().onResultOf(new Function() {
      public Boolean apply(Constructor input) {
         return Boolean.valueOf(Arrays.asList(input.getParameterTypes()).contains(String.class));
      }
   }).reverse();

   public static CheckedFuture makeChecked(ListenableFuture future, Function mapper) {
      return new Futures.MappingCheckedFuture((ListenableFuture)Preconditions.checkNotNull(future), mapper);
   }

   public static ListenableFuture immediateFuture(@Nullable Object value) {
      return new Futures.ImmediateSuccessfulFuture(value);
   }

   public static CheckedFuture immediateCheckedFuture(@Nullable Object value) {
      return new Futures.ImmediateSuccessfulCheckedFuture(value);
   }

   public static ListenableFuture immediateFailedFuture(Throwable throwable) {
      Preconditions.checkNotNull(throwable);
      return new Futures.ImmediateFailedFuture(throwable);
   }

   public static ListenableFuture immediateCancelledFuture() {
      return new Futures.ImmediateCancelledFuture();
   }

   public static CheckedFuture immediateFailedCheckedFuture(Exception exception) {
      Preconditions.checkNotNull(exception);
      return new Futures.ImmediateFailedCheckedFuture(exception);
   }

   public static ListenableFuture withFallback(ListenableFuture input, FutureFallback fallback) {
      return withFallback(input, fallback, MoreExecutors.sameThreadExecutor());
   }

   public static ListenableFuture withFallback(ListenableFuture input, FutureFallback fallback, Executor executor) {
      Preconditions.checkNotNull(fallback);
      return new Futures.FallbackFuture(input, fallback, executor);
   }

   public static ListenableFuture transform(ListenableFuture input, AsyncFunction function) {
      return transform(input, (AsyncFunction)function, MoreExecutors.sameThreadExecutor());
   }

   public static ListenableFuture transform(ListenableFuture input, AsyncFunction function, Executor executor) {
      Futures.ChainingListenableFuture<I, O> output = new Futures.ChainingListenableFuture(function, input);
      input.addListener(output, executor);
      return output;
   }

   public static ListenableFuture transform(ListenableFuture input, Function function) {
      return transform(input, (Function)function, MoreExecutors.sameThreadExecutor());
   }

   public static ListenableFuture transform(ListenableFuture input, final Function function, Executor executor) {
      Preconditions.checkNotNull(function);
      AsyncFunction<I, O> wrapperFunction = new AsyncFunction() {
         public ListenableFuture apply(Object input) {
            O output = function.apply(input);
            return Futures.immediateFuture(output);
         }
      };
      return transform(input, wrapperFunction, executor);
   }

   public static Future lazyTransform(final Future input, final Function function) {
      Preconditions.checkNotNull(input);
      Preconditions.checkNotNull(function);
      return new Future() {
         public boolean cancel(boolean mayInterruptIfRunning) {
            return input.cancel(mayInterruptIfRunning);
         }

         public boolean isCancelled() {
            return input.isCancelled();
         }

         public boolean isDone() {
            return input.isDone();
         }

         public Object get() throws InterruptedException, ExecutionException {
            return this.applyTransformation(input.get());
         }

         public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            return this.applyTransformation(input.get(timeout, unit));
         }

         private Object applyTransformation(Object inputx) throws ExecutionException {
            try {
               return function.apply(inputx);
            } catch (Throwable var3) {
               throw new ExecutionException(var3);
            }
         }
      };
   }

   public static ListenableFuture dereference(ListenableFuture nested) {
      return transform(nested, DEREFERENCER);
   }

   @Beta
   public static ListenableFuture allAsList(ListenableFuture... futures) {
      return listFuture(ImmutableList.copyOf((Object[])futures), true, MoreExecutors.sameThreadExecutor());
   }

   @Beta
   public static ListenableFuture allAsList(Iterable futures) {
      return listFuture(ImmutableList.copyOf(futures), true, MoreExecutors.sameThreadExecutor());
   }

   public static ListenableFuture nonCancellationPropagating(ListenableFuture future) {
      return new Futures.NonCancellationPropagatingFuture(future);
   }

   @Beta
   public static ListenableFuture successfulAsList(ListenableFuture... futures) {
      return listFuture(ImmutableList.copyOf((Object[])futures), false, MoreExecutors.sameThreadExecutor());
   }

   @Beta
   public static ListenableFuture successfulAsList(Iterable futures) {
      return listFuture(ImmutableList.copyOf(futures), false, MoreExecutors.sameThreadExecutor());
   }

   @Beta
   public static ImmutableList inCompletionOrder(Iterable futures) {
      final ConcurrentLinkedQueue<AsyncSettableFuture<T>> delegates = Queues.newConcurrentLinkedQueue();
      ImmutableList.Builder<ListenableFuture<T>> listBuilder = ImmutableList.builder();
      SerializingExecutor executor = new SerializingExecutor(MoreExecutors.sameThreadExecutor());

      for(final ListenableFuture<? extends T> future : futures) {
         AsyncSettableFuture<T> delegate = AsyncSettableFuture.create();
         delegates.add(delegate);
         future.addListener(new Runnable() {
            public void run() {
               ((AsyncSettableFuture)delegates.remove()).setFuture(future);
            }
         }, executor);
         listBuilder.add((Object)delegate);
      }

      return listBuilder.build();
   }

   public static void addCallback(ListenableFuture future, FutureCallback callback) {
      addCallback(future, callback, MoreExecutors.sameThreadExecutor());
   }

   public static void addCallback(final ListenableFuture future, final FutureCallback callback, Executor executor) {
      Preconditions.checkNotNull(callback);
      Runnable callbackListener = new Runnable() {
         public void run() {
            V value;
            try {
               value = Uninterruptibles.getUninterruptibly(future);
            } catch (ExecutionException var3) {
               callback.onFailure(var3.getCause());
               return;
            } catch (RuntimeException var4) {
               callback.onFailure(var4);
               return;
            } catch (Error var5) {
               callback.onFailure(var5);
               return;
            }

            callback.onSuccess(value);
         }
      };
      future.addListener(callbackListener, executor);
   }

   public static Object get(Future future, Class exceptionClass) throws Exception {
      Preconditions.checkNotNull(future);
      Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[]{exceptionClass});

      try {
         return future.get();
      } catch (InterruptedException var3) {
         Thread.currentThread().interrupt();
         throw newWithCause(exceptionClass, var3);
      } catch (ExecutionException var4) {
         wrapAndThrowExceptionOrError(var4.getCause(), exceptionClass);
         throw new AssertionError();
      }
   }

   public static Object get(Future future, long timeout, TimeUnit unit, Class exceptionClass) throws Exception {
      Preconditions.checkNotNull(future);
      Preconditions.checkNotNull(unit);
      Preconditions.checkArgument(!RuntimeException.class.isAssignableFrom(exceptionClass), "Futures.get exception type (%s) must not be a RuntimeException", new Object[]{exceptionClass});

      try {
         return future.get(timeout, unit);
      } catch (InterruptedException var6) {
         Thread.currentThread().interrupt();
         throw newWithCause(exceptionClass, var6);
      } catch (TimeoutException var7) {
         throw newWithCause(exceptionClass, var7);
      } catch (ExecutionException var8) {
         wrapAndThrowExceptionOrError(var8.getCause(), exceptionClass);
         throw new AssertionError();
      }
   }

   private static void wrapAndThrowExceptionOrError(Throwable cause, Class exceptionClass) throws Exception {
      if(cause instanceof Error) {
         throw new ExecutionError((Error)cause);
      } else if(cause instanceof RuntimeException) {
         throw new UncheckedExecutionException(cause);
      } else {
         throw newWithCause(exceptionClass, cause);
      }
   }

   public static Object getUnchecked(Future future) {
      Preconditions.checkNotNull(future);

      try {
         return Uninterruptibles.getUninterruptibly(future);
      } catch (ExecutionException var2) {
         wrapAndThrowUnchecked(var2.getCause());
         throw new AssertionError();
      }
   }

   private static void wrapAndThrowUnchecked(Throwable cause) {
      if(cause instanceof Error) {
         throw new ExecutionError((Error)cause);
      } else {
         throw new UncheckedExecutionException(cause);
      }
   }

   private static Exception newWithCause(Class exceptionClass, Throwable cause) {
      List<Constructor<X>> constructors = Arrays.asList(exceptionClass.getConstructors());

      for(Constructor<X> constructor : preferringStrings(constructors)) {
         X instance = (Exception)newFromConstructor(constructor, cause);
         if(instance != null) {
            if(instance.getCause() == null) {
               instance.initCause(cause);
            }

            return instance;
         }
      }

      throw new IllegalArgumentException("No appropriate constructor for exception of type " + exceptionClass + " in response to chained exception", cause);
   }

   private static List preferringStrings(List constructors) {
      return WITH_STRING_PARAM_FIRST.sortedCopy(constructors);
   }

   @Nullable
   private static Object newFromConstructor(Constructor constructor, Throwable cause) {
      Class<?>[] paramTypes = constructor.getParameterTypes();
      Object[] params = new Object[paramTypes.length];

      for(int i = 0; i < paramTypes.length; ++i) {
         Class<?> paramType = paramTypes[i];
         if(paramType.equals(String.class)) {
            params[i] = cause.toString();
         } else {
            if(!paramType.equals(Throwable.class)) {
               return null;
            }

            params[i] = cause;
         }
      }

      try {
         return constructor.newInstance(params);
      } catch (IllegalArgumentException var6) {
         return null;
      } catch (InstantiationException var7) {
         return null;
      } catch (IllegalAccessException var8) {
         return null;
      } catch (InvocationTargetException var9) {
         return null;
      }
   }

   private static ListenableFuture listFuture(ImmutableList futures, boolean allMustSucceed, Executor listenerExecutor) {
      return new Futures.CombinedFuture(futures, allMustSucceed, listenerExecutor, new Futures.FutureCombiner() {
         public List combine(List values) {
            List<V> result = Lists.newArrayList();

            for(Optional<V> element : values) {
               result.add(element != null?element.orNull():null);
            }

            return Collections.unmodifiableList(result);
         }
      });
   }

   private static class ChainingListenableFuture extends AbstractFuture implements Runnable {
      private AsyncFunction function;
      private ListenableFuture inputFuture;
      private volatile ListenableFuture outputFuture;
      private final CountDownLatch outputCreated;

      private ChainingListenableFuture(AsyncFunction function, ListenableFuture inputFuture) {
         this.outputCreated = new CountDownLatch(1);
         this.function = (AsyncFunction)Preconditions.checkNotNull(function);
         this.inputFuture = (ListenableFuture)Preconditions.checkNotNull(inputFuture);
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         if(super.cancel(mayInterruptIfRunning)) {
            this.cancel(this.inputFuture, mayInterruptIfRunning);
            this.cancel(this.outputFuture, mayInterruptIfRunning);
            return true;
         } else {
            return false;
         }
      }

      private void cancel(@Nullable Future future, boolean mayInterruptIfRunning) {
         if(future != null) {
            future.cancel(mayInterruptIfRunning);
         }

      }

      public void run() {
         try {
            try {
               I sourceResult;
               try {
                  sourceResult = Uninterruptibles.getUninterruptibly(this.inputFuture);
               } catch (CancellationException var9) {
                  this.cancel(false);
                  return;
               } catch (ExecutionException var10) {
                  this.setException(var10.getCause());
                  return;
               }

               final ListenableFuture<? extends O> outputFuture = this.outputFuture = (ListenableFuture)Preconditions.checkNotNull(this.function.apply(sourceResult), "AsyncFunction may not return null.");
               if(this.isCancelled()) {
                  outputFuture.cancel(this.wasInterrupted());
                  this.outputFuture = null;
                  return;
               }

               outputFuture.addListener(new Runnable() {
                  public void run() {
                     try {
                        ChainingListenableFuture.this.set(Uninterruptibles.getUninterruptibly(outputFuture));
                        return;
                     } catch (CancellationException var6) {
                        ChainingListenableFuture.this.cancel(false);
                     } catch (ExecutionException var7) {
                        ChainingListenableFuture.this.setException(var7.getCause());
                        return;
                     } finally {
                        ChainingListenableFuture.this.outputFuture = null;
                     }

                  }
               }, MoreExecutors.sameThreadExecutor());
            } catch (UndeclaredThrowableException var11) {
               this.setException(var11.getCause());
            } catch (Throwable var12) {
               this.setException(var12);
            }

         } finally {
            this.function = null;
            this.inputFuture = null;
            this.outputCreated.countDown();
         }
      }
   }

   private static class CombinedFuture extends AbstractFuture {
      private static final Logger logger = Logger.getLogger(Futures.CombinedFuture.class.getName());
      ImmutableCollection futures;
      final boolean allMustSucceed;
      final AtomicInteger remaining;
      Futures.FutureCombiner combiner;
      List values;
      final Object seenExceptionsLock = new Object();
      Set seenExceptions;

      CombinedFuture(ImmutableCollection futures, boolean allMustSucceed, Executor listenerExecutor, Futures.FutureCombiner combiner) {
         this.futures = futures;
         this.allMustSucceed = allMustSucceed;
         this.remaining = new AtomicInteger(futures.size());
         this.combiner = combiner;
         this.values = Lists.newArrayListWithCapacity(futures.size());
         this.init(listenerExecutor);
      }

      protected void init(Executor listenerExecutor) {
         this.addListener(new Runnable() {
            public void run() {
               if(CombinedFuture.this.isCancelled()) {
                  for(ListenableFuture<?> future : CombinedFuture.this.futures) {
                     future.cancel(CombinedFuture.this.wasInterrupted());
                  }
               }

               CombinedFuture.this.futures = null;
               CombinedFuture.this.values = null;
               CombinedFuture.this.combiner = null;
            }
         }, MoreExecutors.sameThreadExecutor());
         if(this.futures.isEmpty()) {
            this.set(this.combiner.combine(ImmutableList.of()));
         } else {
            for(int i = 0; i < this.futures.size(); ++i) {
               this.values.add((Object)null);
            }

            int i = 0;

            for(final ListenableFuture<? extends V> listenable : this.futures) {
               final int index = i++;
               listenable.addListener(new Runnable() {
                  public void run() {
                     CombinedFuture.this.setOneValue(index, listenable);
                  }
               }, listenerExecutor);
            }

         }
      }

      private void setExceptionAndMaybeLog(Throwable throwable) {
         boolean visibleFromOutputFuture = false;
         boolean firstTimeSeeingThisException = true;
         if(this.allMustSucceed) {
            visibleFromOutputFuture = super.setException(throwable);
            synchronized(this.seenExceptionsLock) {
               if(this.seenExceptions == null) {
                  this.seenExceptions = Sets.newHashSet();
               }

               firstTimeSeeingThisException = this.seenExceptions.add(throwable);
            }
         }

         if(throwable instanceof Error || this.allMustSucceed && !visibleFromOutputFuture && firstTimeSeeingThisException) {
            logger.log(Level.SEVERE, "input future failed.", throwable);
         }

      }

      private void setOneValue(int index, Future future) {
         // $FF: Couldn't be decompiled
      }
   }

   private static class FallbackFuture extends AbstractFuture {
      private volatile ListenableFuture running;

      FallbackFuture(ListenableFuture input, final FutureFallback fallback, Executor executor) {
         this.running = input;
         Futures.addCallback(this.running, new FutureCallback() {
            public void onSuccess(Object value) {
               FallbackFuture.this.set(value);
            }

            public void onFailure(Throwable t) {
               if(!FallbackFuture.this.isCancelled()) {
                  try {
                     FallbackFuture.this.running = fallback.create(t);
                     if(FallbackFuture.this.isCancelled()) {
                        FallbackFuture.this.running.cancel(FallbackFuture.this.wasInterrupted());
                        return;
                     }

                     Futures.addCallback(FallbackFuture.this.running, new FutureCallback() {
                        public void onSuccess(Object value) {
                           FallbackFuture.this.set(value);
                        }

                        public void onFailure(Throwable t) {
                           if(FallbackFuture.this.running.isCancelled()) {
                              FallbackFuture.this.cancel(false);
                           } else {
                              FallbackFuture.this.setException(t);
                           }

                        }
                     }, MoreExecutors.sameThreadExecutor());
                  } catch (Throwable var3) {
                     FallbackFuture.this.setException(var3);
                  }

               }
            }
         }, executor);
      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         if(super.cancel(mayInterruptIfRunning)) {
            this.running.cancel(mayInterruptIfRunning);
            return true;
         } else {
            return false;
         }
      }
   }

   private interface FutureCombiner {
      Object combine(List var1);
   }

   private static class ImmediateCancelledFuture extends Futures.ImmediateFuture {
      private final CancellationException thrown = new CancellationException("Immediate cancelled future.");

      ImmediateCancelledFuture() {
         super(null);
      }

      public boolean isCancelled() {
         return true;
      }

      public Object get() {
         throw AbstractFuture.cancellationExceptionWithCause("Task was cancelled.", this.thrown);
      }
   }

   private static class ImmediateFailedCheckedFuture extends Futures.ImmediateFuture implements CheckedFuture {
      private final Exception thrown;

      ImmediateFailedCheckedFuture(Exception thrown) {
         super(null);
         this.thrown = thrown;
      }

      public Object get() throws ExecutionException {
         throw new ExecutionException(this.thrown);
      }

      public Object checkedGet() throws Exception {
         throw this.thrown;
      }

      public Object checkedGet(long timeout, TimeUnit unit) throws Exception {
         Preconditions.checkNotNull(unit);
         throw this.thrown;
      }
   }

   private static class ImmediateFailedFuture extends Futures.ImmediateFuture {
      private final Throwable thrown;

      ImmediateFailedFuture(Throwable thrown) {
         super(null);
         this.thrown = thrown;
      }

      public Object get() throws ExecutionException {
         throw new ExecutionException(this.thrown);
      }
   }

   private abstract static class ImmediateFuture implements ListenableFuture {
      private static final Logger log = Logger.getLogger(Futures.ImmediateFuture.class.getName());

      private ImmediateFuture() {
      }

      public void addListener(Runnable listener, Executor executor) {
         Preconditions.checkNotNull(listener, "Runnable was null.");
         Preconditions.checkNotNull(executor, "Executor was null.");

         try {
            executor.execute(listener);
         } catch (RuntimeException var4) {
            log.log(Level.SEVERE, "RuntimeException while executing runnable " + listener + " with executor " + executor, var4);
         }

      }

      public boolean cancel(boolean mayInterruptIfRunning) {
         return false;
      }

      public abstract Object get() throws ExecutionException;

      public Object get(long timeout, TimeUnit unit) throws ExecutionException {
         Preconditions.checkNotNull(unit);
         return this.get();
      }

      public boolean isCancelled() {
         return false;
      }

      public boolean isDone() {
         return true;
      }
   }

   private static class ImmediateSuccessfulCheckedFuture extends Futures.ImmediateFuture implements CheckedFuture {
      @Nullable
      private final Object value;

      ImmediateSuccessfulCheckedFuture(@Nullable Object value) {
         super(null);
         this.value = value;
      }

      public Object get() {
         return this.value;
      }

      public Object checkedGet() {
         return this.value;
      }

      public Object checkedGet(long timeout, TimeUnit unit) {
         Preconditions.checkNotNull(unit);
         return this.value;
      }
   }

   private static class ImmediateSuccessfulFuture extends Futures.ImmediateFuture {
      @Nullable
      private final Object value;

      ImmediateSuccessfulFuture(@Nullable Object value) {
         super(null);
         this.value = value;
      }

      public Object get() {
         return this.value;
      }
   }

   private static class MappingCheckedFuture extends AbstractCheckedFuture {
      final Function mapper;

      MappingCheckedFuture(ListenableFuture delegate, Function mapper) {
         super(delegate);
         this.mapper = (Function)Preconditions.checkNotNull(mapper);
      }

      protected Exception mapException(Exception e) {
         return (Exception)this.mapper.apply(e);
      }
   }

   private static class NonCancellationPropagatingFuture extends AbstractFuture {
      NonCancellationPropagatingFuture(final ListenableFuture delegate) {
         Preconditions.checkNotNull(delegate);
         Futures.addCallback(delegate, new FutureCallback() {
            public void onSuccess(Object result) {
               NonCancellationPropagatingFuture.this.set(result);
            }

            public void onFailure(Throwable t) {
               if(delegate.isCancelled()) {
                  NonCancellationPropagatingFuture.this.cancel(false);
               } else {
                  NonCancellationPropagatingFuture.this.setException(t);
               }

            }
         }, MoreExecutors.sameThreadExecutor());
      }
   }
}
