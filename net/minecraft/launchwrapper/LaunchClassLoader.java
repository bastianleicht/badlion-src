package net.minecraft.launchwrapper;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.jar.Attributes.Name;
import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LogWrapper;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class LaunchClassLoader extends URLClassLoader {
   public static final int BUFFER_SIZE = 4096;
   private List sources;
   private ClassLoader parent = this.getClass().getClassLoader();
   private List transformers = new ArrayList(2);
   private Map cachedClasses = new ConcurrentHashMap();
   private Set invalidClasses = new HashSet(1000);
   private Set classLoaderExceptions = new HashSet();
   private Set transformerExceptions = new HashSet();
   private Map resourceCache = new ConcurrentHashMap(1000);
   private Set negativeResourceCache = Collections.newSetFromMap(new ConcurrentHashMap());
   private IClassNameTransformer renameTransformer;
   private final ThreadLocal loadBuffer = new ThreadLocal();
   private static final String[] RESERVED_NAMES = new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
   private static final boolean DEBUG = Boolean.parseBoolean(System.getProperty("legacy.debugClassLoading", "false"));
   private static final boolean DEBUG_FINER = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingFiner", "false"));
   private static final boolean DEBUG_SAVE = DEBUG && Boolean.parseBoolean(System.getProperty("legacy.debugClassLoadingSave", "false"));
   private static File tempFolder = null;

   public LaunchClassLoader(URL[] sources) {
      super(sources, (ClassLoader)null);
      this.sources = new ArrayList(Arrays.asList(sources));
      this.addClassLoaderExclusion("java.");
      this.addClassLoaderExclusion("sun.");
      this.addClassLoaderExclusion("org.lwjgl.");
      this.addClassLoaderExclusion("org.apache.logging.");
      this.addClassLoaderExclusion("net.minecraft.launchwrapper.");
      this.addTransformerExclusion("javax.");
      this.addTransformerExclusion("argo.");
      this.addTransformerExclusion("org.objectweb.asm.");
      this.addTransformerExclusion("com.google.common.");
      this.addTransformerExclusion("org.bouncycastle.");
      this.addTransformerExclusion("net.minecraft.launchwrapper.injector.");
      if(DEBUG_SAVE) {
         int x = 1;

         for(tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP"); tempFolder.exists() && x <= 10; tempFolder = new File(Launch.minecraftHome, "CLASSLOADER_TEMP" + x++)) {
            ;
         }

         if(tempFolder.exists()) {
            LogWrapper.info("DEBUG_SAVE enabled, but 10 temp directories already exist, clean them and try again.", new Object[0]);
            tempFolder = null;
         } else {
            LogWrapper.info("DEBUG_SAVE Enabled, saving all classes to \"%s\"", new Object[]{tempFolder.getAbsolutePath().replace('\\', '/')});
            tempFolder.mkdirs();
         }
      }

   }

   public void registerTransformer(String transformerClassName) {
      try {
         IClassTransformer transformer = (IClassTransformer)this.loadClass(transformerClassName).newInstance();
         this.transformers.add(transformer);
         if(transformer instanceof IClassNameTransformer && this.renameTransformer == null) {
            this.renameTransformer = (IClassNameTransformer)transformer;
         }
      } catch (Exception var3) {
         LogWrapper.log((Level)Level.ERROR, (Throwable)var3, "A critical problem occurred registering the ASM transformer class %s", new Object[]{transformerClassName});
      }

   }

   public Class findClass(String name) throws ClassNotFoundException {
      if(this.invalidClasses.contains(name)) {
         throw new ClassNotFoundException(name);
      } else {
         for(String exception : this.classLoaderExceptions) {
            if(name.startsWith(exception)) {
               return this.parent.loadClass(name);
            }
         }

         if(this.cachedClasses.containsKey(name)) {
            return (Class)this.cachedClasses.get(name);
         } else {
            for(String exception : this.transformerExceptions) {
               if(name.startsWith(exception)) {
                  try {
                     Class<?> clazz = super.findClass(name);
                     this.cachedClasses.put(name, clazz);
                     return clazz;
                  } catch (ClassNotFoundException var14) {
                     this.invalidClasses.add(name);
                     throw var14;
                  }
               }
            }

            try {
               String transformedName = this.transformName(name);
               if(this.cachedClasses.containsKey(transformedName)) {
                  return (Class)this.cachedClasses.get(transformedName);
               } else {
                  String untransformedName = this.untransformName(name);
                  int lastDot = untransformedName.lastIndexOf(46);
                  String packageName = lastDot == -1?"":untransformedName.substring(0, lastDot);
                  String fileName = untransformedName.replace('.', '/').concat(".class");
                  URLConnection urlConnection = this.findCodeSourceConnectionFor(fileName);
                  CodeSigner[] signers = null;
                  if(lastDot > -1 && !untransformedName.startsWith("net.minecraft.")) {
                     if(urlConnection instanceof JarURLConnection) {
                        JarURLConnection jarURLConnection = (JarURLConnection)urlConnection;
                        JarFile jarFile = jarURLConnection.getJarFile();
                        if(jarFile != null && jarFile.getManifest() != null) {
                           Manifest manifest = jarFile.getManifest();
                           JarEntry entry = jarFile.getJarEntry(fileName);
                           Package pkg = this.getPackage(packageName);
                           this.getClassBytes(untransformedName);
                           signers = entry.getCodeSigners();
                           if(pkg == null) {
                              this.definePackage(packageName, manifest, jarURLConnection.getJarFileURL());
                           } else if(pkg.isSealed() && !pkg.isSealed(jarURLConnection.getJarFileURL())) {
                              LogWrapper.severe("The jar file %s is trying to seal already secured path %s", new Object[]{jarFile.getName(), packageName});
                           } else if(this.isSealed(packageName, manifest)) {
                              LogWrapper.severe("The jar file %s has a security seal for path %s, but that path is defined and not secure", new Object[]{jarFile.getName(), packageName});
                           }
                        }
                     } else {
                        Package pkg = this.getPackage(packageName);
                        if(pkg == null) {
                           this.definePackage(packageName, (String)null, (String)null, (String)null, (String)null, (String)null, (String)null, (URL)null);
                        } else if(pkg.isSealed()) {
                           LogWrapper.severe("The URL %s is defining elements for sealed path %s", new Object[]{urlConnection.getURL(), packageName});
                        }
                     }
                  }

                  byte[] transformedClass = this.runTransformers(untransformedName, transformedName, this.getClassBytes(untransformedName));
                  if(DEBUG_SAVE) {
                     this.saveTransformedClass(transformedClass, transformedName);
                  }

                  CodeSource codeSource = urlConnection == null?null:new CodeSource(urlConnection.getURL(), signers);
                  Class<?> clazz = this.defineClass(transformedName, transformedClass, 0, transformedClass.length, codeSource);
                  this.cachedClasses.put(transformedName, clazz);
                  return clazz;
               }
            } catch (Throwable var15) {
               this.invalidClasses.add(name);
               if(DEBUG) {
                  LogWrapper.log(Level.TRACE, var15, "Exception encountered attempting classloading of %s", new Object[]{name});
                  LogManager.getLogger("LaunchWrapper").log(Level.ERROR, "Exception encountered attempting classloading of %s", var15);
               }

               throw new ClassNotFoundException(name, var15);
            }
         }
      }
   }

   private void saveTransformedClass(byte[] data, String transformedName) {
      if(tempFolder != null) {
         File outFile = new File(tempFolder, transformedName.replace('.', File.separatorChar) + ".class");
         File outDir = outFile.getParentFile();
         if(!outDir.exists()) {
            outDir.mkdirs();
         }

         if(outFile.exists()) {
            outFile.delete();
         }

         try {
            LogWrapper.fine("Saving transformed class \"%s\" to \"%s\"", new Object[]{transformedName, outFile.getAbsolutePath().replace('\\', '/')});
            OutputStream output = new FileOutputStream(outFile);
            output.write(data);
            output.close();
         } catch (IOException var6) {
            LogWrapper.log((Level)Level.WARN, (Throwable)var6, "Could not save transformed class \"%s\"", new Object[]{transformedName});
         }

      }
   }

   private String untransformName(String name) {
      return this.renameTransformer != null?this.renameTransformer.unmapClassName(name):name;
   }

   private String transformName(String name) {
      return this.renameTransformer != null?this.renameTransformer.remapClassName(name):name;
   }

   private boolean isSealed(String path, Manifest manifest) {
      Attributes attributes = manifest.getAttributes(path);
      String sealed = null;
      if(attributes != null) {
         sealed = attributes.getValue(Name.SEALED);
      }

      if(sealed == null) {
         attributes = manifest.getMainAttributes();
         if(attributes != null) {
            sealed = attributes.getValue(Name.SEALED);
         }
      }

      return "true".equalsIgnoreCase(sealed);
   }

   private URLConnection findCodeSourceConnectionFor(String name) {
      URL resource = this.findResource(name);
      if(resource != null) {
         try {
            return resource.openConnection();
         } catch (IOException var4) {
            throw new RuntimeException(var4);
         }
      } else {
         return null;
      }
   }

   private byte[] runTransformers(String name, String transformedName, byte[] basicClass) {
      if(DEBUG_FINER) {
         LogWrapper.finest("Beginning transform of {%s (%s)} Start Length: %d", new Object[]{name, transformedName, Integer.valueOf(basicClass == null?0:basicClass.length)});

         for(IClassTransformer transformer : this.transformers) {
            String transName = transformer.getClass().getName();
            LogWrapper.finest("Before Transformer {%s (%s)} %s: %d", new Object[]{name, transformedName, transName, Integer.valueOf(basicClass == null?0:basicClass.length)});
            basicClass = transformer.transform(name, transformedName, basicClass);
            LogWrapper.finest("After  Transformer {%s (%s)} %s: %d", new Object[]{name, transformedName, transName, Integer.valueOf(basicClass == null?0:basicClass.length)});
         }

         LogWrapper.finest("Ending transform of {%s (%s)} Start Length: %d", new Object[]{name, transformedName, Integer.valueOf(basicClass == null?0:basicClass.length)});
      } else {
         for(IClassTransformer transformer : this.transformers) {
            basicClass = transformer.transform(name, transformedName, basicClass);
         }
      }

      return basicClass;
   }

   public void addURL(URL url) {
      super.addURL(url);
      this.sources.add(url);
   }

   public List getSources() {
      return this.sources;
   }

   private byte[] readFully(InputStream stream) {
      try {
         byte[] buffer = this.getOrCreateBuffer();
         int totalLength = 0;

         int read;
         while((read = stream.read(buffer, totalLength, buffer.length - totalLength)) != -1) {
            totalLength += read;
            if(totalLength >= buffer.length - 1) {
               byte[] newBuffer = new byte[buffer.length + 4096];
               System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
               buffer = newBuffer;
            }
         }

         byte[] result = new byte[totalLength];
         System.arraycopy(buffer, 0, result, 0, totalLength);
         return result;
      } catch (Throwable var6) {
         LogWrapper.log(Level.WARN, var6, "Problem loading class", new Object[0]);
         return new byte[0];
      }
   }

   private byte[] getOrCreateBuffer() {
      byte[] buffer = (byte[])this.loadBuffer.get();
      if(buffer == null) {
         this.loadBuffer.set(new byte[4096]);
         buffer = (byte[])this.loadBuffer.get();
      }

      return buffer;
   }

   public List getTransformers() {
      return Collections.unmodifiableList(this.transformers);
   }

   public void addClassLoaderExclusion(String toExclude) {
      this.classLoaderExceptions.add(toExclude);
   }

   public void addTransformerExclusion(String toExclude) {
      this.transformerExceptions.add(toExclude);
   }

   public byte[] getClassBytes(String name) throws IOException {
      if(this.negativeResourceCache.contains(name)) {
         return null;
      } else if(this.resourceCache.containsKey(name)) {
         return (byte[])this.resourceCache.get(name);
      } else {
         if(name.indexOf(46) == -1) {
            for(String reservedName : RESERVED_NAMES) {
               if(name.toUpperCase(Locale.ENGLISH).startsWith(reservedName)) {
                  byte[] data = this.getClassBytes("_" + name);
                  if(data != null) {
                     this.resourceCache.put(name, data);
                     return data;
                  }
               }
            }
         }

         InputStream classStream = null;

         try {
            String resourcePath = name.replace('.', '/').concat(".class");
            URL classResource = this.findResource(resourcePath);
            if(classResource != null) {
               classStream = classResource.openStream();
               if(DEBUG) {
                  LogWrapper.finest("Loading class %s from resource %s", new Object[]{name, classResource.toString()});
               }

               byte[] data = this.readFully(classStream);
               this.resourceCache.put(name, data);
               byte[] var7 = data;
               return var7;
            }

            if(DEBUG) {
               LogWrapper.finest("Failed to find class resource %s", new Object[]{resourcePath});
            }

            this.negativeResourceCache.add(name);
         } finally {
            closeSilently(classStream);
         }

         return null;
      }
   }

   private static void closeSilently(Closeable closeable) {
      if(closeable != null) {
         try {
            closeable.close();
         } catch (IOException var2) {
            ;
         }
      }

   }

   public void clearNegativeEntries(Set entriesToClear) {
      this.negativeResourceCache.removeAll(entriesToClear);
   }
}
