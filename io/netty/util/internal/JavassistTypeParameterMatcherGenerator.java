package io.netty.util.internal;

import io.netty.util.internal.NoOpTypeParameterMatcher;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.TypeParameterMatcher;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.Method;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public final class JavassistTypeParameterMatcherGenerator {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(JavassistTypeParameterMatcherGenerator.class);
   private static final ClassPool classPool = new ClassPool(true);

   public static void appendClassPath(ClassPath classpath) {
      classPool.appendClassPath(classpath);
   }

   public static void appendClassPath(String pathname) throws NotFoundException {
      classPool.appendClassPath(pathname);
   }

   public static TypeParameterMatcher generate(Class type) {
      ClassLoader classLoader = PlatformDependent.getContextClassLoader();
      if(classLoader == null) {
         classLoader = PlatformDependent.getSystemClassLoader();
      }

      return generate(type, classLoader);
   }

   public static TypeParameterMatcher generate(Class type, ClassLoader classLoader) {
      String typeName = typeName(type);
      String className = "io.netty.util.internal.__matchers__." + typeName + "Matcher";

      try {
         try {
            return (TypeParameterMatcher)Class.forName(className, true, classLoader).newInstance();
         } catch (Exception var8) {
            CtClass c = classPool.getAndRename(NoOpTypeParameterMatcher.class.getName(), className);
            c.setModifiers(c.getModifiers() | 16);
            c.getDeclaredMethod("match").setBody("{ return $1 instanceof " + typeName + "; }");
            byte[] byteCode = c.toBytecode();
            c.detach();
            Method method = ClassLoader.class.getDeclaredMethod("defineClass", new Class[]{String.class, byte[].class, Integer.TYPE, Integer.TYPE});
            method.setAccessible(true);
            Class<?> generated = (Class)method.invoke(classLoader, new Object[]{className, byteCode, Integer.valueOf(0), Integer.valueOf(byteCode.length)});
            if(type != Object.class) {
               logger.debug("Generated: {}", (Object)generated.getName());
            }

            return (TypeParameterMatcher)generated.newInstance();
         }
      } catch (RuntimeException var9) {
         throw var9;
      } catch (Exception var10) {
         throw new RuntimeException(var10);
      }
   }

   private static String typeName(Class type) {
      return type.isArray()?typeName(type.getComponentType()) + "[]":type.getName();
   }

   static {
      classPool.appendClassPath(new ClassClassPath(NoOpTypeParameterMatcher.class));
   }
}
