package org.apache.logging.log4j.core.config.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.helpers.Charsets;
import org.apache.logging.log4j.core.helpers.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

public class ResolverUtil {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final String VFSZIP = "vfszip";
   private static final String BUNDLE_RESOURCE = "bundleresource";
   private final Set classMatches = new HashSet();
   private final Set resourceMatches = new HashSet();
   private ClassLoader classloader;

   public Set getClasses() {
      return this.classMatches;
   }

   public Set getResources() {
      return this.resourceMatches;
   }

   public ClassLoader getClassLoader() {
      return this.classloader != null?this.classloader:(this.classloader = Loader.getClassLoader(ResolverUtil.class, (Class)null));
   }

   public void setClassLoader(ClassLoader classloader) {
      this.classloader = classloader;
   }

   public void findImplementations(Class parent, String... packageNames) {
      if(packageNames != null) {
         ResolverUtil.Test test = new ResolverUtil.IsA(parent);

         for(String pkg : packageNames) {
            this.findInPackage(test, pkg);
         }

      }
   }

   public void findSuffix(String suffix, String... packageNames) {
      if(packageNames != null) {
         ResolverUtil.Test test = new ResolverUtil.NameEndsWith(suffix);

         for(String pkg : packageNames) {
            this.findInPackage(test, pkg);
         }

      }
   }

   public void findAnnotated(Class annotation, String... packageNames) {
      if(packageNames != null) {
         ResolverUtil.Test test = new ResolverUtil.AnnotatedWith(annotation);

         for(String pkg : packageNames) {
            this.findInPackage(test, pkg);
         }

      }
   }

   public void findNamedResource(String name, String... pathNames) {
      if(pathNames != null) {
         ResolverUtil.Test test = new ResolverUtil.NameIs(name);

         for(String pkg : pathNames) {
            this.findInPackage(test, pkg);
         }

      }
   }

   public void find(ResolverUtil.Test test, String... packageNames) {
      if(packageNames != null) {
         for(String pkg : packageNames) {
            this.findInPackage(test, pkg);
         }

      }
   }

   public void findInPackage(ResolverUtil.Test test, String packageName) {
      packageName = packageName.replace('.', '/');
      ClassLoader loader = this.getClassLoader();

      Enumeration<URL> urls;
      try {
         urls = loader.getResources(packageName);
      } catch (IOException var16) {
         LOGGER.warn((String)("Could not read package: " + packageName), (Throwable)var16);
         return;
      }

      while(urls.hasMoreElements()) {
         try {
            URL url = (URL)urls.nextElement();
            String urlPath = url.getFile();
            urlPath = URLDecoder.decode(urlPath, Charsets.UTF_8.name());
            if(urlPath.startsWith("file:")) {
               urlPath = urlPath.substring(5);
            }

            if(urlPath.indexOf(33) > 0) {
               urlPath = urlPath.substring(0, urlPath.indexOf(33));
            }

            LOGGER.info("Scanning for classes in [" + urlPath + "] matching criteria: " + test);
            if("vfszip".equals(url.getProtocol())) {
               String path = urlPath.substring(0, urlPath.length() - packageName.length() - 2);
               URL newURL = new URL(url.getProtocol(), url.getHost(), path);
               JarInputStream stream = new JarInputStream(newURL.openStream());

               try {
                  this.loadImplementationsInJar(test, packageName, path, stream);
               } finally {
                  this.close(stream, newURL);
               }
            } else if("bundleresource".equals(url.getProtocol())) {
               this.loadImplementationsInBundle(test, packageName);
            } else {
               File file = new File(urlPath);
               if(file.isDirectory()) {
                  this.loadImplementationsInDirectory(test, packageName, file);
               } else {
                  this.loadImplementationsInJar(test, packageName, file);
               }
            }
         } catch (IOException var15) {
            LOGGER.warn((String)"could not read entries", (Throwable)var15);
         }
      }

   }

   private void loadImplementationsInBundle(ResolverUtil.Test test, String packageName) {
      BundleWiring wiring = (BundleWiring)FrameworkUtil.getBundle(ResolverUtil.class).adapt(BundleWiring.class);

      for(String name : wiring.listResources(packageName, "*.class", 1)) {
         this.addIfMatching(test, name);
      }

   }

   private void loadImplementationsInDirectory(ResolverUtil.Test test, String parent, File location) {
      File[] files = location.listFiles();
      if(files != null) {
         for(File file : files) {
            StringBuilder builder = new StringBuilder();
            builder.append(parent).append("/").append(file.getName());
            String packageOrClass = parent == null?file.getName():builder.toString();
            if(file.isDirectory()) {
               this.loadImplementationsInDirectory(test, packageOrClass, file);
            } else if(this.isTestApplicable(test, file.getName())) {
               this.addIfMatching(test, packageOrClass);
            }
         }

      }
   }

   private boolean isTestApplicable(ResolverUtil.Test test, String path) {
      return test.doesMatchResource() || path.endsWith(".class") && test.doesMatchClass();
   }

   private void loadImplementationsInJar(ResolverUtil.Test test, String parent, File jarFile) {
      JarInputStream jarStream = null;

      try {
         jarStream = new JarInputStream(new FileInputStream(jarFile));
         this.loadImplementationsInJar(test, parent, jarFile.getPath(), jarStream);
      } catch (FileNotFoundException var10) {
         LOGGER.error("Could not search jar file \'" + jarFile + "\' for classes matching criteria: " + test + " file not found");
      } catch (IOException var11) {
         LOGGER.error((String)("Could not search jar file \'" + jarFile + "\' for classes matching criteria: " + test + " due to an IOException"), (Throwable)var11);
      } finally {
         this.close(jarStream, jarFile);
      }

   }

   private void close(JarInputStream jarStream, Object source) {
      if(jarStream != null) {
         try {
            jarStream.close();
         } catch (IOException var4) {
            LOGGER.error("Error closing JAR file stream for {}", new Object[]{source, var4});
         }
      }

   }

   private void loadImplementationsInJar(ResolverUtil.Test test, String parent, String path, JarInputStream stream) {
      while(true) {
         try {
            JarEntry entry;
            if((entry = stream.getNextJarEntry()) != null) {
               String name = entry.getName();
               if(!entry.isDirectory() && name.startsWith(parent) && this.isTestApplicable(test, name)) {
                  this.addIfMatching(test, name);
               }
               continue;
            }
         } catch (IOException var7) {
            LOGGER.error((String)("Could not search jar file \'" + path + "\' for classes matching criteria: " + test + " due to an IOException"), (Throwable)var7);
         }

         return;
      }
   }

   protected void addIfMatching(ResolverUtil.Test test, String fqn) {
      try {
         ClassLoader loader = this.getClassLoader();
         if(test.doesMatchClass()) {
            String externalName = fqn.substring(0, fqn.indexOf(46)).replace('/', '.');
            if(LOGGER.isDebugEnabled()) {
               LOGGER.debug("Checking to see if class " + externalName + " matches criteria [" + test + "]");
            }

            Class<?> type = loader.loadClass(externalName);
            if(test.matches(type)) {
               this.classMatches.add(type);
            }
         }

         if(test.doesMatchResource()) {
            URL url = loader.getResource(fqn);
            if(url == null) {
               url = loader.getResource(fqn.substring(1));
            }

            if(url != null && test.matches(url.toURI())) {
               this.resourceMatches.add(url.toURI());
            }
         }
      } catch (Throwable var6) {
         LOGGER.warn("Could not examine class \'" + fqn + "\' due to a " + var6.getClass().getName() + " with message: " + var6.getMessage());
      }

   }

   public static class AnnotatedWith extends ResolverUtil.ClassTest {
      private final Class annotation;

      public AnnotatedWith(Class annotation) {
         this.annotation = annotation;
      }

      public boolean matches(Class type) {
         return type != null && type.isAnnotationPresent(this.annotation);
      }

      public String toString() {
         return "annotated with @" + this.annotation.getSimpleName();
      }
   }

   public abstract static class ClassTest implements ResolverUtil.Test {
      public boolean matches(URI resource) {
         throw new UnsupportedOperationException();
      }

      public boolean doesMatchClass() {
         return true;
      }

      public boolean doesMatchResource() {
         return false;
      }
   }

   public static class IsA extends ResolverUtil.ClassTest {
      private final Class parent;

      public IsA(Class parentType) {
         this.parent = parentType;
      }

      public boolean matches(Class type) {
         return type != null && this.parent.isAssignableFrom(type);
      }

      public String toString() {
         return "is assignable to " + this.parent.getSimpleName();
      }
   }

   public static class NameEndsWith extends ResolverUtil.ClassTest {
      private final String suffix;

      public NameEndsWith(String suffix) {
         this.suffix = suffix;
      }

      public boolean matches(Class type) {
         return type != null && type.getName().endsWith(this.suffix);
      }

      public String toString() {
         return "ends with the suffix " + this.suffix;
      }
   }

   public static class NameIs extends ResolverUtil.ResourceTest {
      private final String name;

      public NameIs(String name) {
         this.name = "/" + name;
      }

      public boolean matches(URI resource) {
         return resource.getPath().endsWith(this.name);
      }

      public String toString() {
         return "named " + this.name;
      }
   }

   public abstract static class ResourceTest implements ResolverUtil.Test {
      public boolean matches(Class cls) {
         throw new UnsupportedOperationException();
      }

      public boolean doesMatchClass() {
         return false;
      }

      public boolean doesMatchResource() {
         return true;
      }
   }

   public interface Test {
      boolean matches(Class var1);

      boolean matches(URI var1);

      boolean doesMatchClass();

      boolean doesMatchResource();
   }
}
