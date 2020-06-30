package io.netty.handler.codec.serialization;

import io.netty.handler.codec.serialization.ClassResolver;

class ClassLoaderClassResolver implements ClassResolver {
   private final ClassLoader classLoader;

   ClassLoaderClassResolver(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public Class resolve(String className) throws ClassNotFoundException {
      try {
         return this.classLoader.loadClass(className);
      } catch (ClassNotFoundException var3) {
         return Class.forName(className, false, this.classLoader);
      }
   }
}
