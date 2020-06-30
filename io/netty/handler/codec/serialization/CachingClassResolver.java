package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;
import java.util.Map;

class CachingClassResolver implements ClassResolver {
   private final Map classCache;
   private final ClassResolver delegate;

   CachingClassResolver(ClassResolver delegate, Map classCache) {
      this.delegate = delegate;
      this.classCache = classCache;
   }

   public Class resolve(String className) throws ClassNotFoundException {
      Class<?> clazz = (Class)this.classCache.get(className);
      if(clazz != null) {
         return clazz;
      } else {
         clazz = this.delegate.resolve(className);
         this.classCache.put(className, clazz);
         return clazz;
      }
   }
}
