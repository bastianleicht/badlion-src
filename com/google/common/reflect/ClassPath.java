package com.google.common.reflect;

import com.google.common.annotations.Beta;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.reflect.Reflection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import java.util.logging.Logger;
import javax.annotation.Nullable;

@Beta
public final class ClassPath {
   private static final Logger logger = Logger.getLogger(ClassPath.class.getName());
   private static final Predicate IS_TOP_LEVEL = new Predicate() {
      public boolean apply(ClassPath.ClassInfo info) {
         return info.className.indexOf(36) == -1;
      }
   };
   private static final Splitter CLASS_PATH_ATTRIBUTE_SEPARATOR = Splitter.on(" ").omitEmptyStrings();
   private static final String CLASS_FILE_NAME_EXTENSION = ".class";
   private final ImmutableSet resources;

   private ClassPath(ImmutableSet resources) {
      this.resources = resources;
   }

   public static ClassPath from(ClassLoader classloader) throws IOException {
      ClassPath.Scanner scanner = new ClassPath.Scanner();

      for(Entry<URI, ClassLoader> entry : getClassPathEntries(classloader).entrySet()) {
         scanner.scan((URI)entry.getKey(), (ClassLoader)entry.getValue());
      }

      return new ClassPath(scanner.getResources());
   }

   public ImmutableSet getResources() {
      return this.resources;
   }

   public ImmutableSet getAllClasses() {
      return FluentIterable.from((Iterable)this.resources).filter(ClassPath.ClassInfo.class).toSet();
   }

   public ImmutableSet getTopLevelClasses() {
      return FluentIterable.from((Iterable)this.resources).filter(ClassPath.ClassInfo.class).filter(IS_TOP_LEVEL).toSet();
   }

   public ImmutableSet getTopLevelClasses(String packageName) {
      Preconditions.checkNotNull(packageName);
      ImmutableSet.Builder<ClassPath.ClassInfo> builder = ImmutableSet.builder();

      for(ClassPath.ClassInfo classInfo : this.getTopLevelClasses()) {
         if(classInfo.getPackageName().equals(packageName)) {
            builder.add((Object)classInfo);
         }
      }

      return builder.build();
   }

   public ImmutableSet getTopLevelClassesRecursive(String packageName) {
      Preconditions.checkNotNull(packageName);
      String packagePrefix = packageName + '.';
      ImmutableSet.Builder<ClassPath.ClassInfo> builder = ImmutableSet.builder();

      for(ClassPath.ClassInfo classInfo : this.getTopLevelClasses()) {
         if(classInfo.getName().startsWith(packagePrefix)) {
            builder.add((Object)classInfo);
         }
      }

      return builder.build();
   }

   @VisibleForTesting
   static ImmutableMap getClassPathEntries(ClassLoader classloader) {
      LinkedHashMap<URI, ClassLoader> entries = Maps.newLinkedHashMap();
      ClassLoader parent = classloader.getParent();
      if(parent != null) {
         entries.putAll(getClassPathEntries(parent));
      }

      if(classloader instanceof URLClassLoader) {
         URLClassLoader urlClassLoader = (URLClassLoader)classloader;

         for(URL entry : urlClassLoader.getURLs()) {
            URI uri;
            try {
               uri = entry.toURI();
            } catch (URISyntaxException var10) {
               throw new IllegalArgumentException(var10);
            }

            if(!entries.containsKey(uri)) {
               entries.put(uri, classloader);
            }
         }
      }

      return ImmutableMap.copyOf(entries);
   }

   @VisibleForTesting
   static String getClassName(String filename) {
      int classNameEnd = filename.length() - ".class".length();
      return filename.substring(0, classNameEnd).replace('/', '.');
   }

   @Beta
   public static final class ClassInfo extends ClassPath.ResourceInfo {
      private final String className;

      ClassInfo(String resourceName, ClassLoader loader) {
         super(resourceName, loader);
         this.className = ClassPath.getClassName(resourceName);
      }

      public String getPackageName() {
         return Reflection.getPackageName(this.className);
      }

      public String getSimpleName() {
         int lastDollarSign = this.className.lastIndexOf(36);
         if(lastDollarSign != -1) {
            String innerClassName = this.className.substring(lastDollarSign + 1);
            return CharMatcher.DIGIT.trimLeadingFrom(innerClassName);
         } else {
            String packageName = this.getPackageName();
            return packageName.isEmpty()?this.className:this.className.substring(packageName.length() + 1);
         }
      }

      public String getName() {
         return this.className;
      }

      public Class load() {
         try {
            return this.loader.loadClass(this.className);
         } catch (ClassNotFoundException var2) {
            throw new IllegalStateException(var2);
         }
      }

      public String toString() {
         return this.className;
      }
   }

   @Beta
   public static class ResourceInfo {
      private final String resourceName;
      final ClassLoader loader;

      static ClassPath.ResourceInfo of(String resourceName, ClassLoader loader) {
         return (ClassPath.ResourceInfo)(resourceName.endsWith(".class")?new ClassPath.ClassInfo(resourceName, loader):new ClassPath.ResourceInfo(resourceName, loader));
      }

      ResourceInfo(String resourceName, ClassLoader loader) {
         this.resourceName = (String)Preconditions.checkNotNull(resourceName);
         this.loader = (ClassLoader)Preconditions.checkNotNull(loader);
      }

      public final URL url() {
         return (URL)Preconditions.checkNotNull(this.loader.getResource(this.resourceName), "Failed to load resource: %s", new Object[]{this.resourceName});
      }

      public final String getResourceName() {
         return this.resourceName;
      }

      public int hashCode() {
         return this.resourceName.hashCode();
      }

      public boolean equals(Object obj) {
         if(!(obj instanceof ClassPath.ResourceInfo)) {
            return false;
         } else {
            ClassPath.ResourceInfo that = (ClassPath.ResourceInfo)obj;
            return this.resourceName.equals(that.resourceName) && this.loader == that.loader;
         }
      }

      public String toString() {
         return this.resourceName;
      }
   }

   @VisibleForTesting
   static final class Scanner {
      private final ImmutableSortedSet.Builder resources = new ImmutableSortedSet.Builder(Ordering.usingToString());
      private final Set scannedUris = Sets.newHashSet();

      ImmutableSortedSet getResources() {
         return this.resources.build();
      }

      void scan(URI uri, ClassLoader classloader) throws IOException {
         if(uri.getScheme().equals("file") && this.scannedUris.add(uri)) {
            this.scanFrom(new File(uri), classloader);
         }

      }

      @VisibleForTesting
      void scanFrom(File file, ClassLoader classloader) throws IOException {
         if(file.exists()) {
            if(file.isDirectory()) {
               this.scanDirectory(file, classloader);
            } else {
               this.scanJar(file, classloader);
            }

         }
      }

      private void scanDirectory(File directory, ClassLoader classloader) throws IOException {
         this.scanDirectory(directory, classloader, "", ImmutableSet.of());
      }

      private void scanDirectory(File directory, ClassLoader classloader, String packagePrefix, ImmutableSet ancestors) throws IOException {
         File canonical = directory.getCanonicalFile();
         if(!ancestors.contains(canonical)) {
            File[] files = directory.listFiles();
            if(files == null) {
               ClassPath.logger.warning("Cannot read directory " + directory);
            } else {
               ImmutableSet<File> newAncestors = ImmutableSet.builder().addAll((Iterable)ancestors).add((Object)canonical).build();

               for(File f : files) {
                  String name = f.getName();
                  if(f.isDirectory()) {
                     this.scanDirectory(f, classloader, packagePrefix + name + "/", newAncestors);
                  } else {
                     String resourceName = packagePrefix + name;
                     if(!resourceName.equals("META-INF/MANIFEST.MF")) {
                        this.resources.add((Object)ClassPath.ResourceInfo.of(resourceName, classloader));
                     }
                  }
               }

            }
         }
      }

      private void scanJar(File file, ClassLoader classloader) throws IOException {
         JarFile jarFile;
         try {
            jarFile = new JarFile(file);
         } catch (IOException var13) {
            return;
         }

         try {
            for(URI uri : getClassPathFromManifest(file, jarFile.getManifest())) {
               this.scan(uri, classloader);
            }

            Enumeration<JarEntry> entries = jarFile.entries();

            while(entries.hasMoreElements()) {
               JarEntry entry = (JarEntry)entries.nextElement();
               if(!entry.isDirectory() && !entry.getName().equals("META-INF/MANIFEST.MF")) {
                  this.resources.add((Object)ClassPath.ResourceInfo.of(entry.getName(), classloader));
               }
            }
         } finally {
            try {
               jarFile.close();
            } catch (IOException var12) {
               ;
            }

         }

      }

      @VisibleForTesting
      static ImmutableSet getClassPathFromManifest(File jarFile, @Nullable Manifest manifest) {
         if(manifest == null) {
            return ImmutableSet.of();
         } else {
            ImmutableSet.Builder<URI> builder = ImmutableSet.builder();
            String classpathAttribute = manifest.getMainAttributes().getValue(Name.CLASS_PATH.toString());
            if(classpathAttribute != null) {
               for(String path : ClassPath.CLASS_PATH_ATTRIBUTE_SEPARATOR.split(classpathAttribute)) {
                  URI uri;
                  try {
                     uri = getClassPathEntry(jarFile, path);
                  } catch (URISyntaxException var8) {
                     ClassPath.logger.warning("Invalid Class-Path entry: " + path);
                     continue;
                  }

                  builder.add((Object)uri);
               }
            }

            return builder.build();
         }
      }

      @VisibleForTesting
      static URI getClassPathEntry(File jarFile, String path) throws URISyntaxException {
         URI uri = new URI(path);
         return uri.isAbsolute()?uri:(new File(jarFile.getParentFile(), path.replace('/', File.separatorChar))).toURI();
      }
   }
}
