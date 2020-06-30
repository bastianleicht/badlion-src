package org.apache.commons.logging;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;

public abstract class LogFactory {
   public static final String PRIORITY_KEY = "priority";
   public static final String TCCL_KEY = "use_tccl";
   public static final String FACTORY_PROPERTY = "org.apache.commons.logging.LogFactory";
   public static final String FACTORY_DEFAULT = "org.apache.commons.logging.impl.LogFactoryImpl";
   public static final String FACTORY_PROPERTIES = "commons-logging.properties";
   protected static final String SERVICE_ID = "META-INF/services/org.apache.commons.logging.LogFactory";
   public static final String DIAGNOSTICS_DEST_PROPERTY = "org.apache.commons.logging.diagnostics.dest";
   private static PrintStream diagnosticsStream = null;
   private static final String diagnosticPrefix;
   public static final String HASHTABLE_IMPLEMENTATION_PROPERTY = "org.apache.commons.logging.LogFactory.HashtableImpl";
   private static final String WEAK_HASHTABLE_CLASSNAME = "org.apache.commons.logging.impl.WeakHashtable";
   private static final ClassLoader thisClassLoader;
   protected static Hashtable factories = null;
   /** @deprecated */
   protected static volatile LogFactory nullClassLoaderFactory = null;
   // $FF: synthetic field
   static Class class$java$lang$Thread;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$LogFactory;

   public abstract Object getAttribute(String var1);

   public abstract String[] getAttributeNames();

   public abstract Log getInstance(Class var1) throws LogConfigurationException;

   public abstract Log getInstance(String var1) throws LogConfigurationException;

   public abstract void release();

   public abstract void removeAttribute(String var1);

   public abstract void setAttribute(String var1, Object var2);

   private static final Hashtable createFactoryStore() {
      Hashtable result = null;

      String storeImplementationClass;
      try {
         storeImplementationClass = getSystemProperty("org.apache.commons.logging.LogFactory.HashtableImpl", (String)null);
      } catch (SecurityException var3) {
         storeImplementationClass = null;
      }

      if(storeImplementationClass == null) {
         storeImplementationClass = "org.apache.commons.logging.impl.WeakHashtable";
      }

      try {
         Class implementationClass = Class.forName(storeImplementationClass);
         result = (Hashtable)implementationClass.newInstance();
      } catch (Throwable var4) {
         handleThrowable(var4);
         if(!"org.apache.commons.logging.impl.WeakHashtable".equals(storeImplementationClass)) {
            if(isDiagnosticsEnabled()) {
               logDiagnostic("[ERROR] LogFactory: Load of custom hashtable failed");
            } else {
               System.err.println("[ERROR] LogFactory: Load of custom hashtable failed");
            }
         }
      }

      if(result == null) {
         result = new Hashtable();
      }

      return result;
   }

   private static String trim(String src) {
      return src == null?null:src.trim();
   }

   protected static void handleThrowable(Throwable t) {
      if(t instanceof ThreadDeath) {
         throw (ThreadDeath)t;
      } else if(t instanceof VirtualMachineError) {
         throw (VirtualMachineError)t;
      }
   }

   public static LogFactory getFactory() throws LogConfigurationException {
      ClassLoader contextClassLoader = getContextClassLoaderInternal();
      if(contextClassLoader == null && isDiagnosticsEnabled()) {
         logDiagnostic("Context classloader is null.");
      }

      LogFactory factory = getCachedFactory(contextClassLoader);
      if(factory != null) {
         return factory;
      } else {
         if(isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] LogFactory implementation requested for the first time for context classloader " + objectId(contextClassLoader));
            logHierarchy("[LOOKUP] ", contextClassLoader);
         }

         Properties props = getConfigurationFile(contextClassLoader, "commons-logging.properties");
         ClassLoader baseClassLoader = contextClassLoader;
         if(props != null) {
            String useTCCLStr = props.getProperty("use_tccl");
            if(useTCCLStr != null && !Boolean.valueOf(useTCCLStr).booleanValue()) {
               baseClassLoader = thisClassLoader;
            }
         }

         if(isDiagnosticsEnabled()) {
            logDiagnostic("[LOOKUP] Looking for system property [org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
         }

         try {
            String factoryClass = getSystemProperty("org.apache.commons.logging.LogFactory", (String)null);
            if(factoryClass != null) {
               if(isDiagnosticsEnabled()) {
                  logDiagnostic("[LOOKUP] Creating an instance of LogFactory class \'" + factoryClass + "\' as specified by system property " + "org.apache.commons.logging.LogFactory");
               }

               factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
            } else if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] No system property [org.apache.commons.logging.LogFactory] defined.");
            }
         } catch (SecurityException var9) {
            if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(var9.getMessage()) + "]. Trying alternative implementations...");
            }
         } catch (RuntimeException var10) {
            if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] An exception occurred while trying to create an instance of the custom factory class: [" + trim(var10.getMessage()) + "] as specified by a system property.");
            }

            throw var10;
         }

         if(factory == null) {
            if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] Looking for a resource file of name [META-INF/services/org.apache.commons.logging.LogFactory] to define the LogFactory subclass to use...");
            }

            try {
               InputStream is = getResourceAsStream(contextClassLoader, "META-INF/services/org.apache.commons.logging.LogFactory");
               if(is != null) {
                  BufferedReader rd;
                  try {
                     rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                  } catch (UnsupportedEncodingException var7) {
                     rd = new BufferedReader(new InputStreamReader(is));
                  }

                  String factoryClassName = rd.readLine();
                  rd.close();
                  if(factoryClassName != null && !"".equals(factoryClassName)) {
                     if(isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP]  Creating an instance of LogFactory class " + factoryClassName + " as specified by file \'" + "META-INF/services/org.apache.commons.logging.LogFactory" + "\' which was present in the path of the context classloader.");
                     }

                     factory = newFactory(factoryClassName, baseClassLoader, contextClassLoader);
                  }
               } else if(isDiagnosticsEnabled()) {
                  logDiagnostic("[LOOKUP] No resource file with name \'META-INF/services/org.apache.commons.logging.LogFactory\' found.");
               }
            } catch (Exception var8) {
               if(isDiagnosticsEnabled()) {
                  logDiagnostic("[LOOKUP] A security exception occurred while trying to create an instance of the custom factory class: [" + trim(var8.getMessage()) + "]. Trying alternative implementations...");
               }
            }
         }

         if(factory == null) {
            if(props != null) {
               if(isDiagnosticsEnabled()) {
                  logDiagnostic("[LOOKUP] Looking in properties file for entry with key \'org.apache.commons.logging.LogFactory\' to define the LogFactory subclass to use...");
               }

               String factoryClass = props.getProperty("org.apache.commons.logging.LogFactory");
               if(factoryClass != null) {
                  if(isDiagnosticsEnabled()) {
                     logDiagnostic("[LOOKUP] Properties file specifies LogFactory subclass \'" + factoryClass + "\'");
                  }

                  factory = newFactory(factoryClass, baseClassLoader, contextClassLoader);
               } else if(isDiagnosticsEnabled()) {
                  logDiagnostic("[LOOKUP] Properties file has no entry specifying LogFactory subclass.");
               }
            } else if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] No properties file available to determine LogFactory subclass from..");
            }
         }

         if(factory == null) {
            if(isDiagnosticsEnabled()) {
               logDiagnostic("[LOOKUP] Loading the default LogFactory implementation \'org.apache.commons.logging.impl.LogFactoryImpl\' via the same classloader that loaded this LogFactory class (ie not looking in the context classloader).");
            }

            factory = newFactory("org.apache.commons.logging.impl.LogFactoryImpl", thisClassLoader, contextClassLoader);
         }

         if(factory != null) {
            cacheFactory(contextClassLoader, factory);
            if(props != null) {
               Enumeration names = props.propertyNames();

               while(names.hasMoreElements()) {
                  String name = (String)names.nextElement();
                  String value = props.getProperty(name);
                  factory.setAttribute(name, value);
               }
            }
         }

         return factory;
      }
   }

   public static Log getLog(Class clazz) throws LogConfigurationException {
      return getFactory().getInstance(clazz);
   }

   public static Log getLog(String name) throws LogConfigurationException {
      return getFactory().getInstance(name);
   }

   public static void release(ClassLoader classLoader) {
      if(isDiagnosticsEnabled()) {
         logDiagnostic("Releasing factory for classloader " + objectId(classLoader));
      }

      Hashtable factories = factories;
      synchronized(factories) {
         if(classLoader == null) {
            if(nullClassLoaderFactory != null) {
               nullClassLoaderFactory.release();
               nullClassLoaderFactory = null;
            }
         } else {
            LogFactory factory = (LogFactory)factories.get(classLoader);
            if(factory != null) {
               factory.release();
               factories.remove(classLoader);
            }
         }

      }
   }

   public static void releaseAll() {
      if(isDiagnosticsEnabled()) {
         logDiagnostic("Releasing factory for all classloaders.");
      }

      Hashtable factories = factories;
      synchronized(factories) {
         Enumeration elements = factories.elements();

         while(elements.hasMoreElements()) {
            LogFactory element = (LogFactory)elements.nextElement();
            element.release();
         }

         factories.clear();
         if(nullClassLoaderFactory != null) {
            nullClassLoaderFactory.release();
            nullClassLoaderFactory = null;
         }

      }
   }

   protected static ClassLoader getClassLoader(Class clazz) {
      try {
         return clazz.getClassLoader();
      } catch (SecurityException var2) {
         if(isDiagnosticsEnabled()) {
            logDiagnostic("Unable to get classloader for class \'" + clazz + "\' due to security restrictions - " + var2.getMessage());
         }

         throw var2;
      }
   }

   protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
      return directGetContextClassLoader();
   }

   private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return LogFactory.directGetContextClassLoader();
         }
      });
   }

   protected static ClassLoader directGetContextClassLoader() throws LogConfigurationException {
      ClassLoader classLoader = null;

      try {
         Method method = (class$java$lang$Thread == null?(class$java$lang$Thread = class$("java.lang.Thread")):class$java$lang$Thread).getMethod("getContextClassLoader", (Class[])null);

         try {
            classLoader = (ClassLoader)method.invoke(Thread.currentThread(), (Object[])null);
         } catch (IllegalAccessException var3) {
            throw new LogConfigurationException("Unexpected IllegalAccessException", var3);
         } catch (InvocationTargetException var4) {
            if(!(var4.getTargetException() instanceof SecurityException)) {
               throw new LogConfigurationException("Unexpected InvocationTargetException", var4.getTargetException());
            }
         }
      } catch (NoSuchMethodException var5) {
         classLoader = getClassLoader(class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory);
      }

      return classLoader;
   }

   private static LogFactory getCachedFactory(ClassLoader contextClassLoader) {
      return contextClassLoader == null?nullClassLoaderFactory:(LogFactory)factories.get(contextClassLoader);
   }

   private static void cacheFactory(ClassLoader classLoader, LogFactory factory) {
      if(factory != null) {
         if(classLoader == null) {
            nullClassLoaderFactory = factory;
         } else {
            factories.put(classLoader, factory);
         }
      }

   }

   protected static LogFactory newFactory(final String factoryClass, final ClassLoader classLoader, ClassLoader contextClassLoader) throws LogConfigurationException {
      Object result = AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return LogFactory.createFactory(factoryClass, classLoader);
         }
      });
      if(result instanceof LogConfigurationException) {
         LogConfigurationException ex = (LogConfigurationException)result;
         if(isDiagnosticsEnabled()) {
            logDiagnostic("An error occurred while loading the factory class:" + ex.getMessage());
         }

         throw ex;
      } else {
         if(isDiagnosticsEnabled()) {
            logDiagnostic("Created object " + objectId(result) + " to manage classloader " + objectId(contextClassLoader));
         }

         return (LogFactory)result;
      }
   }

   protected static LogFactory newFactory(String factoryClass, ClassLoader classLoader) {
      return newFactory(factoryClass, classLoader, (ClassLoader)null);
   }

   protected static Object createFactory(String factoryClass, ClassLoader classLoader) {
      Class logFactoryClass = null;

      try {
         if(classLoader != null) {
            try {
               logFactoryClass = classLoader.loadClass(factoryClass);
               if((class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory).isAssignableFrom(logFactoryClass)) {
                  if(isDiagnosticsEnabled()) {
                     logDiagnostic("Loaded class " + logFactoryClass.getName() + " from classloader " + objectId(classLoader));
                  }
               } else if(isDiagnosticsEnabled()) {
                  logDiagnostic("Factory class " + logFactoryClass.getName() + " loaded from classloader " + objectId(logFactoryClass.getClassLoader()) + " does not extend \'" + (class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory).getName() + "\' as loaded by this classloader.");
                  logHierarchy("[BAD CL TREE] ", classLoader);
               }

               return (LogFactory)logFactoryClass.newInstance();
            } catch (ClassNotFoundException var6) {
               if(classLoader == thisClassLoader) {
                  if(isDiagnosticsEnabled()) {
                     logDiagnostic("Unable to locate any class called \'" + factoryClass + "\' via classloader " + objectId(classLoader));
                  }

                  throw var6;
               }
            } catch (NoClassDefFoundError var7) {
               if(classLoader == thisClassLoader) {
                  if(isDiagnosticsEnabled()) {
                     logDiagnostic("Class \'" + factoryClass + "\' cannot be loaded" + " via classloader " + objectId(classLoader) + " - it depends on some other class that cannot be found.");
                  }

                  throw var7;
               }
            } catch (ClassCastException var8) {
               if(classLoader == thisClassLoader) {
                  boolean implementsLogFactory = implementsLogFactory(logFactoryClass);
                  StringBuffer msg = new StringBuffer();
                  msg.append("The application has specified that a custom LogFactory implementation ");
                  msg.append("should be used but Class \'");
                  msg.append(factoryClass);
                  msg.append("\' cannot be converted to \'");
                  msg.append((class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory).getName());
                  msg.append("\'. ");
                  if(implementsLogFactory) {
                     msg.append("The conflict is caused by the presence of multiple LogFactory classes ");
                     msg.append("in incompatible classloaders. ");
                     msg.append("Background can be found in http://commons.apache.org/logging/tech.html. ");
                     msg.append("If you have not explicitly specified a custom LogFactory then it is likely ");
                     msg.append("that the container has set one without your knowledge. ");
                     msg.append("In this case, consider using the commons-logging-adapters.jar file or ");
                     msg.append("specifying the standard LogFactory from the command line. ");
                  } else {
                     msg.append("Please check the custom implementation. ");
                  }

                  msg.append("Help can be found @http://commons.apache.org/logging/troubleshooting.html.");
                  if(isDiagnosticsEnabled()) {
                     logDiagnostic(msg.toString());
                  }

                  throw new ClassCastException(msg.toString());
               }
            }
         }

         if(isDiagnosticsEnabled()) {
            logDiagnostic("Unable to load factory class via classloader " + objectId(classLoader) + " - trying the classloader associated with this LogFactory.");
         }

         logFactoryClass = Class.forName(factoryClass);
         return (LogFactory)logFactoryClass.newInstance();
      } catch (Exception var9) {
         if(isDiagnosticsEnabled()) {
            logDiagnostic("Unable to create LogFactory instance.");
         }

         return logFactoryClass != null && !(class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory).isAssignableFrom(logFactoryClass)?new LogConfigurationException("The chosen LogFactory implementation does not extend LogFactory. Please check your configuration.", var9):new LogConfigurationException(var9);
      }
   }

   private static boolean implementsLogFactory(Class logFactoryClass) {
      boolean implementsLogFactory = false;
      if(logFactoryClass != null) {
         try {
            ClassLoader logFactoryClassLoader = logFactoryClass.getClassLoader();
            if(logFactoryClassLoader == null) {
               logDiagnostic("[CUSTOM LOG FACTORY] was loaded by the boot classloader");
            } else {
               logHierarchy("[CUSTOM LOG FACTORY] ", logFactoryClassLoader);
               Class factoryFromCustomLoader = Class.forName("org.apache.commons.logging.LogFactory", false, logFactoryClassLoader);
               implementsLogFactory = factoryFromCustomLoader.isAssignableFrom(logFactoryClass);
               if(implementsLogFactory) {
                  logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " implements LogFactory but was loaded by an incompatible classloader.");
               } else {
                  logDiagnostic("[CUSTOM LOG FACTORY] " + logFactoryClass.getName() + " does not implement LogFactory.");
               }
            }
         } catch (SecurityException var4) {
            logDiagnostic("[CUSTOM LOG FACTORY] SecurityException thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + var4.getMessage());
         } catch (LinkageError var5) {
            logDiagnostic("[CUSTOM LOG FACTORY] LinkageError thrown whilst trying to determine whether the compatibility was caused by a classloader conflict: " + var5.getMessage());
         } catch (ClassNotFoundException var6) {
            logDiagnostic("[CUSTOM LOG FACTORY] LogFactory class cannot be loaded by classloader which loaded the custom LogFactory implementation. Is the custom factory in the right classloader?");
         }
      }

      return implementsLogFactory;
   }

   private static InputStream getResourceAsStream(final ClassLoader loader, final String name) {
      return (InputStream)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return loader != null?loader.getResourceAsStream(name):ClassLoader.getSystemResourceAsStream(name);
         }
      });
   }

   private static Enumeration getResources(final ClassLoader loader, final String name) {
      PrivilegedAction action = new PrivilegedAction() {
         public Object run() {
            try {
               return loader != null?loader.getResources(name):ClassLoader.getSystemResources(name);
            } catch (IOException var2) {
               if(LogFactory.isDiagnosticsEnabled()) {
                  LogFactory.logDiagnostic("Exception while trying to find configuration file " + name + ":" + var2.getMessage());
               }

               return null;
            } catch (NoSuchMethodError var3) {
               return null;
            }
         }
      };
      Object result = AccessController.doPrivileged(action);
      return (Enumeration)result;
   }

   private static Properties getProperties(final URL url) {
      PrivilegedAction action = new PrivilegedAction() {
         public Object run() {
            InputStream stream = null;

            Properties var4;
            try {
               URLConnection connection = url.openConnection();
               connection.setUseCaches(false);
               stream = connection.getInputStream();
               if(stream == null) {
                  return null;
               }

               Properties props = new Properties();
               props.load(stream);
               stream.close();
               stream = null;
               var4 = props;
            } catch (IOException var15) {
               if(LogFactory.isDiagnosticsEnabled()) {
                  LogFactory.logDiagnostic("Unable to read URL " + url);
               }

               return null;
            } finally {
               if(stream != null) {
                  try {
                     stream.close();
                  } catch (IOException var14) {
                     if(LogFactory.isDiagnosticsEnabled()) {
                        LogFactory.logDiagnostic("Unable to close stream for URL " + url);
                     }
                  }
               }

            }

            return var4;
         }
      };
      return (Properties)AccessController.doPrivileged(action);
   }

   private static final Properties getConfigurationFile(ClassLoader classLoader, String fileName) {
      Properties props = null;
      double priority = 0.0D;
      URL propsUrl = null;

      try {
         Enumeration urls = getResources(classLoader, fileName);
         if(urls == null) {
            return null;
         }

         while(urls.hasMoreElements()) {
            URL url = (URL)urls.nextElement();
            Properties newProps = getProperties(url);
            if(newProps != null) {
               if(props == null) {
                  propsUrl = url;
                  props = newProps;
                  String priorityStr = newProps.getProperty("priority");
                  priority = 0.0D;
                  if(priorityStr != null) {
                     priority = Double.parseDouble(priorityStr);
                  }

                  if(isDiagnosticsEnabled()) {
                     logDiagnostic("[LOOKUP] Properties file found at \'" + url + "\'" + " with priority " + priority);
                  }
               } else {
                  String newPriorityStr = newProps.getProperty("priority");
                  double newPriority = 0.0D;
                  if(newPriorityStr != null) {
                     newPriority = Double.parseDouble(newPriorityStr);
                  }

                  if(newPriority > priority) {
                     if(isDiagnosticsEnabled()) {
                        logDiagnostic("[LOOKUP] Properties file at \'" + url + "\'" + " with priority " + newPriority + " overrides file at \'" + propsUrl + "\'" + " with priority " + priority);
                     }

                     propsUrl = url;
                     props = newProps;
                     priority = newPriority;
                  } else if(isDiagnosticsEnabled()) {
                     logDiagnostic("[LOOKUP] Properties file at \'" + url + "\'" + " with priority " + newPriority + " does not override file at \'" + propsUrl + "\'" + " with priority " + priority);
                  }
               }
            }
         }
      } catch (SecurityException var12) {
         if(isDiagnosticsEnabled()) {
            logDiagnostic("SecurityException thrown while trying to find/read config files.");
         }
      }

      if(isDiagnosticsEnabled()) {
         if(props == null) {
            logDiagnostic("[LOOKUP] No properties file of name \'" + fileName + "\' found.");
         } else {
            logDiagnostic("[LOOKUP] Properties file of name \'" + fileName + "\' found at \'" + propsUrl + '\"');
         }
      }

      return props;
   }

   private static String getSystemProperty(final String key, final String def) throws SecurityException {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(key, def);
         }
      });
   }

   private static PrintStream initDiagnostics() {
      String dest;
      try {
         dest = getSystemProperty("org.apache.commons.logging.diagnostics.dest", (String)null);
         if(dest == null) {
            return null;
         }
      } catch (SecurityException var3) {
         return null;
      }

      if(dest.equals("STDOUT")) {
         return System.out;
      } else if(dest.equals("STDERR")) {
         return System.err;
      } else {
         try {
            FileOutputStream fos = new FileOutputStream(dest, true);
            return new PrintStream(fos);
         } catch (IOException var2) {
            return null;
         }
      }
   }

   protected static boolean isDiagnosticsEnabled() {
      return diagnosticsStream != null;
   }

   private static final void logDiagnostic(String msg) {
      if(diagnosticsStream != null) {
         diagnosticsStream.print(diagnosticPrefix);
         diagnosticsStream.println(msg);
         diagnosticsStream.flush();
      }

   }

   protected static final void logRawDiagnostic(String msg) {
      if(diagnosticsStream != null) {
         diagnosticsStream.println(msg);
         diagnosticsStream.flush();
      }

   }

   private static void logClassLoaderEnvironment(Class clazz) {
      if(isDiagnosticsEnabled()) {
         try {
            logDiagnostic("[ENV] Extension directories (java.ext.dir): " + System.getProperty("java.ext.dir"));
            logDiagnostic("[ENV] Application classpath (java.class.path): " + System.getProperty("java.class.path"));
         } catch (SecurityException var5) {
            logDiagnostic("[ENV] Security setting prevent interrogation of system classpaths.");
         }

         String className = clazz.getName();

         ClassLoader classLoader;
         try {
            classLoader = getClassLoader(clazz);
         } catch (SecurityException var4) {
            logDiagnostic("[ENV] Security forbids determining the classloader for " + className);
            return;
         }

         logDiagnostic("[ENV] Class " + className + " was loaded via classloader " + objectId(classLoader));
         logHierarchy("[ENV] Ancestry of classloader which loaded " + className + " is ", classLoader);
      }
   }

   private static void logHierarchy(String prefix, ClassLoader classLoader) {
      if(isDiagnosticsEnabled()) {
         if(classLoader != null) {
            String classLoaderString = classLoader.toString();
            logDiagnostic(prefix + objectId(classLoader) + " == \'" + classLoaderString + "\'");
         }

         ClassLoader systemClassLoader;
         try {
            systemClassLoader = ClassLoader.getSystemClassLoader();
         } catch (SecurityException var5) {
            logDiagnostic(prefix + "Security forbids determining the system classloader.");
            return;
         }

         if(classLoader != null) {
            StringBuffer buf = new StringBuffer(prefix + "ClassLoader tree:");

            while(true) {
               buf.append(objectId(classLoader));
               if(classLoader == systemClassLoader) {
                  buf.append(" (SYSTEM) ");
               }

               try {
                  classLoader = classLoader.getParent();
               } catch (SecurityException var6) {
                  buf.append(" --> SECRET");
                  break;
               }

               buf.append(" --> ");
               if(classLoader == null) {
                  buf.append("BOOT");
                  break;
               }
            }

            logDiagnostic(buf.toString());
         }

      }
   }

   public static String objectId(Object o) {
      return o == null?"null":o.getClass().getName() + "@" + System.identityHashCode(o);
   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }

   static {
      thisClassLoader = getClassLoader(class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory);

      String classLoaderName;
      try {
         ClassLoader classLoader = thisClassLoader;
         if(thisClassLoader == null) {
            classLoaderName = "BOOTLOADER";
         } else {
            classLoaderName = objectId(classLoader);
         }
      } catch (SecurityException var2) {
         classLoaderName = "UNKNOWN";
      }

      diagnosticPrefix = "[LogFactory from " + classLoaderName + "] ";
      diagnosticsStream = initDiagnostics();
      logClassLoaderEnvironment(class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory);
      factories = createFactoryStore();
      if(isDiagnosticsEnabled()) {
         logDiagnostic("BOOTSTRAP COMPLETED");
      }

   }
}
