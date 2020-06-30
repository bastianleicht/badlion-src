package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.CachingClassResolver;
import io.netty.handler.codec.serialization.ClassLoaderClassResolver;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.SoftReferenceMap;
import io.netty.handler.codec.serialization.WeakReferenceMap;
import io.netty.util.internal.PlatformDependent;
import java.util.HashMap;

public final class ClassResolvers {
   public static ClassResolver cacheDisabled(ClassLoader classLoader) {
      return new ClassLoaderClassResolver(defaultClassLoader(classLoader));
   }

   public static ClassResolver weakCachingResolver(ClassLoader classLoader) {
      return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new WeakReferenceMap(new HashMap()));
   }

   public static ClassResolver softCachingResolver(ClassLoader classLoader) {
      return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new SoftReferenceMap(new HashMap()));
   }

   public static ClassResolver weakCachingConcurrentResolver(ClassLoader classLoader) {
      return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new WeakReferenceMap(PlatformDependent.newConcurrentHashMap()));
   }

   public static ClassResolver softCachingConcurrentResolver(ClassLoader classLoader) {
      return new CachingClassResolver(new ClassLoaderClassResolver(defaultClassLoader(classLoader)), new SoftReferenceMap(PlatformDependent.newConcurrentHashMap()));
   }

   static ClassLoader defaultClassLoader(ClassLoader classLoader) {
      if(classLoader != null) {
         return classLoader;
      } else {
         ClassLoader contextClassLoader = PlatformDependent.getContextClassLoader();
         return contextClassLoader != null?contextClassLoader:PlatformDependent.getClassLoader(ClassResolvers.class);
      }
   }
}
