package com.google.common.eventbus;

import com.google.common.base.Objects;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventSubscriber;
import com.google.common.eventbus.Subscribe;
import com.google.common.eventbus.SubscriberFindingStrategy;
import com.google.common.eventbus.SynchronizedEventSubscriber;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;

class AnnotatedSubscriberFinder implements SubscriberFindingStrategy {
   private static final LoadingCache subscriberMethodsCache = CacheBuilder.newBuilder().weakKeys().build(new CacheLoader() {
      public ImmutableList load(Class concreteClass) throws Exception {
         return AnnotatedSubscriberFinder.getAnnotatedMethodsInternal(concreteClass);
      }
   });

   public Multimap findAllSubscribers(Object listener) {
      Multimap<Class<?>, EventSubscriber> methodsInListener = HashMultimap.create();
      Class<?> clazz = listener.getClass();

      for(Method method : getAnnotatedMethods(clazz)) {
         Class<?>[] parameterTypes = method.getParameterTypes();
         Class<?> eventType = parameterTypes[0];
         EventSubscriber subscriber = makeSubscriber(listener, method);
         methodsInListener.put(eventType, subscriber);
      }

      return methodsInListener;
   }

   private static ImmutableList getAnnotatedMethods(Class clazz) {
      try {
         return (ImmutableList)subscriberMethodsCache.getUnchecked(clazz);
      } catch (UncheckedExecutionException var2) {
         throw Throwables.propagate(var2.getCause());
      }
   }

   private static ImmutableList getAnnotatedMethodsInternal(Class clazz) {
      Set<? extends Class<?>> supers = TypeToken.of(clazz).getTypes().rawTypes();
      Map<AnnotatedSubscriberFinder.MethodIdentifier, Method> identifiers = Maps.newHashMap();

      for(Class<?> superClazz : supers) {
         for(Method superClazzMethod : superClazz.getMethods()) {
            if(superClazzMethod.isAnnotationPresent(Subscribe.class)) {
               Class<?>[] parameterTypes = superClazzMethod.getParameterTypes();
               if(parameterTypes.length != 1) {
                  throw new IllegalArgumentException("Method " + superClazzMethod + " has @Subscribe annotation, but requires " + parameterTypes.length + " arguments.  Event subscriber methods must require a single argument.");
               }

               AnnotatedSubscriberFinder.MethodIdentifier ident = new AnnotatedSubscriberFinder.MethodIdentifier(superClazzMethod);
               if(!identifiers.containsKey(ident)) {
                  identifiers.put(ident, superClazzMethod);
               }
            }
         }
      }

      return ImmutableList.copyOf(identifiers.values());
   }

   private static EventSubscriber makeSubscriber(Object listener, Method method) {
      EventSubscriber wrapper;
      if(methodIsDeclaredThreadSafe(method)) {
         wrapper = new EventSubscriber(listener, method);
      } else {
         wrapper = new SynchronizedEventSubscriber(listener, method);
      }

      return wrapper;
   }

   private static boolean methodIsDeclaredThreadSafe(Method method) {
      return method.getAnnotation(AllowConcurrentEvents.class) != null;
   }

   private static final class MethodIdentifier {
      private final String name;
      private final List parameterTypes;

      MethodIdentifier(Method method) {
         this.name = method.getName();
         this.parameterTypes = Arrays.asList(method.getParameterTypes());
      }

      public int hashCode() {
         return Objects.hashCode(new Object[]{this.name, this.parameterTypes});
      }

      public boolean equals(@Nullable Object o) {
         if(!(o instanceof AnnotatedSubscriberFinder.MethodIdentifier)) {
            return false;
         } else {
            AnnotatedSubscriberFinder.MethodIdentifier ident = (AnnotatedSubscriberFinder.MethodIdentifier)o;
            return this.name.equals(ident.name) && this.parameterTypes.equals(ident.parameterTypes);
         }
      }
   }
}
