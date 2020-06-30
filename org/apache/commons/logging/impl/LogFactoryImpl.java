package org.apache.commons.logging.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

public class LogFactoryImpl extends LogFactory {
   private static final String LOGGING_IMPL_LOG4J_LOGGER = "org.apache.commons.logging.impl.Log4JLogger";
   private static final String LOGGING_IMPL_JDK14_LOGGER = "org.apache.commons.logging.impl.Jdk14Logger";
   private static final String LOGGING_IMPL_LUMBERJACK_LOGGER = "org.apache.commons.logging.impl.Jdk13LumberjackLogger";
   private static final String LOGGING_IMPL_SIMPLE_LOGGER = "org.apache.commons.logging.impl.SimpleLog";
   private static final String PKG_IMPL = "org.apache.commons.logging.impl.";
   private static final int PKG_LEN = "org.apache.commons.logging.impl.".length();
   public static final String LOG_PROPERTY = "org.apache.commons.logging.Log";
   protected static final String LOG_PROPERTY_OLD = "org.apache.commons.logging.log";
   public static final String ALLOW_FLAWED_CONTEXT_PROPERTY = "org.apache.commons.logging.Log.allowFlawedContext";
   public static final String ALLOW_FLAWED_DISCOVERY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedDiscovery";
   public static final String ALLOW_FLAWED_HIERARCHY_PROPERTY = "org.apache.commons.logging.Log.allowFlawedHierarchy";
   private static final String[] classesToDiscover = new String[]{"org.apache.commons.logging.impl.Log4JLogger", "org.apache.commons.logging.impl.Jdk14Logger", "org.apache.commons.logging.impl.Jdk13LumberjackLogger", "org.apache.commons.logging.impl.SimpleLog"};
   private boolean useTCCL = true;
   private String diagnosticPrefix;
   protected Hashtable attributes = new Hashtable();
   protected Hashtable instances = new Hashtable();
   private String logClassName;
   protected Constructor logConstructor = null;
   protected Class[] logConstructorSignature;
   protected Method logMethod;
   protected Class[] logMethodSignature;
   private boolean allowFlawedContext;
   private boolean allowFlawedDiscovery;
   private boolean allowFlawedHierarchy;
   // $FF: synthetic field
   static Class class$java$lang$String;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$LogFactory;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$impl$LogFactoryImpl;
   // $FF: synthetic field
   static Class class$org$apache$commons$logging$Log;

   public LogFactoryImpl() {
      this.logConstructorSignature = new Class[]{class$java$lang$String == null?(class$java$lang$String = class$("java.lang.String")):class$java$lang$String};
      this.logMethod = null;
      this.logMethodSignature = new Class[]{class$org$apache$commons$logging$LogFactory == null?(class$org$apache$commons$logging$LogFactory = class$("org.apache.commons.logging.LogFactory")):class$org$apache$commons$logging$LogFactory};
      this.initDiagnostics();
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Instance created.");
      }

   }

   public Object getAttribute(String name) {
      return this.attributes.get(name);
   }

   public String[] getAttributeNames() {
      return (String[])((String[])this.attributes.keySet().toArray(new String[this.attributes.size()]));
   }

   public Log getInstance(Class clazz) throws LogConfigurationException {
      return this.getInstance(clazz.getName());
   }

   public Log getInstance(String name) throws LogConfigurationException {
      Log instance = (Log)this.instances.get(name);
      if(instance == null) {
         instance = this.newInstance(name);
         this.instances.put(name, instance);
      }

      return instance;
   }

   public void release() {
      this.logDiagnostic("Releasing all known loggers");
      this.instances.clear();
   }

   public void removeAttribute(String name) {
      this.attributes.remove(name);
   }

   public void setAttribute(String name, Object value) {
      if(this.logConstructor != null) {
         this.logDiagnostic("setAttribute: call too late; configuration already performed.");
      }

      if(value == null) {
         this.attributes.remove(name);
      } else {
         this.attributes.put(name, value);
      }

      if(name.equals("use_tccl")) {
         this.useTCCL = value != null && Boolean.valueOf(value.toString()).booleanValue();
      }

   }

   protected static ClassLoader getContextClassLoader() throws LogConfigurationException {
      return LogFactory.getContextClassLoader();
   }

   protected static boolean isDiagnosticsEnabled() {
      return LogFactory.isDiagnosticsEnabled();
   }

   protected static ClassLoader getClassLoader(Class clazz) {
      return LogFactory.getClassLoader(clazz);
   }

   private void initDiagnostics() {
      Class clazz = this.getClass();
      ClassLoader classLoader = getClassLoader(clazz);

      String classLoaderName;
      try {
         if(classLoader == null) {
            classLoaderName = "BOOTLOADER";
         } else {
            classLoaderName = LogFactory.objectId(classLoader);
         }
      } catch (SecurityException var5) {
         classLoaderName = "UNKNOWN";
      }

      this.diagnosticPrefix = "[LogFactoryImpl@" + System.identityHashCode(this) + " from " + classLoaderName + "] ";
   }

   protected void logDiagnostic(String msg) {
      if(isDiagnosticsEnabled()) {
         LogFactory.logRawDiagnostic(this.diagnosticPrefix + msg);
      }

   }

   /** @deprecated */
   protected String getLogClassName() {
      if(this.logClassName == null) {
         this.discoverLogImplementation(this.getClass().getName());
      }

      return this.logClassName;
   }

   /** @deprecated */
   protected Constructor getLogConstructor() throws LogConfigurationException {
      if(this.logConstructor == null) {
         this.discoverLogImplementation(this.getClass().getName());
      }

      return this.logConstructor;
   }

   /** @deprecated */
   protected boolean isJdk13LumberjackAvailable() {
      return this.isLogLibraryAvailable("Jdk13Lumberjack", "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
   }

   /** @deprecated */
   protected boolean isJdk14Available() {
      return this.isLogLibraryAvailable("Jdk14", "org.apache.commons.logging.impl.Jdk14Logger");
   }

   /** @deprecated */
   protected boolean isLog4JAvailable() {
      return this.isLogLibraryAvailable("Log4J", "org.apache.commons.logging.impl.Log4JLogger");
   }

   protected Log newInstance(String name) throws LogConfigurationException {
      try {
         Log instance;
         if(this.logConstructor == null) {
            instance = this.discoverLogImplementation(name);
         } else {
            Object[] params = new Object[]{name};
            instance = (Log)this.logConstructor.newInstance(params);
         }

         if(this.logMethod != null) {
            Object[] params = new Object[]{this};
            this.logMethod.invoke(instance, params);
         }

         return instance;
      } catch (LogConfigurationException var5) {
         throw var5;
      } catch (InvocationTargetException var6) {
         Throwable c = var6.getTargetException();
         throw new LogConfigurationException((Throwable)(c == null?var6:c));
      } catch (Throwable var7) {
         LogFactory.handleThrowable(var7);
         throw new LogConfigurationException(var7);
      }
   }

   private static ClassLoader getContextClassLoaderInternal() throws LogConfigurationException {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return LogFactory.directGetContextClassLoader();
         }
      });
   }

   private static String getSystemProperty(final String key, final String def) throws SecurityException {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(key, def);
         }
      });
   }

   private ClassLoader getParentClassLoader(final ClassLoader cl) {
      try {
         return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               return cl.getParent();
            }
         });
      } catch (SecurityException var3) {
         this.logDiagnostic("[SECURITY] Unable to obtain parent classloader");
         return null;
      }
   }

   private boolean isLogLibraryAvailable(String name, String classname) {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Checking for \'" + name + "\'.");
      }

      try {
         Log log = this.createLogFromClass(classname, this.getClass().getName(), false);
         if(log == null) {
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("Did not find \'" + name + "\'.");
            }

            return false;
         } else {
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("Found \'" + name + "\'.");
            }

            return true;
         }
      } catch (LogConfigurationException var4) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("Logging system \'" + name + "\' is available but not useable.");
         }

         return false;
      }
   }

   private String getConfigurationValue(String property) {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("[ENV] Trying to get configuration for item " + property);
      }

      Object valueObj = this.getAttribute(property);
      if(valueObj != null) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] Found LogFactory attribute [" + valueObj + "] for " + property);
         }

         return valueObj.toString();
      } else {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] No LogFactory attribute found for " + property);
         }

         try {
            String value = getSystemProperty(property, (String)null);
            if(value != null) {
               if(isDiagnosticsEnabled()) {
                  this.logDiagnostic("[ENV] Found system property [" + value + "] for " + property);
               }

               return value;
            }

            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("[ENV] No system property found for property " + property);
            }
         } catch (SecurityException var4) {
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("[ENV] Security prevented reading system property " + property);
            }
         }

         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("[ENV] No configuration defined for item " + property);
         }

         return null;
      }
   }

   private boolean getBooleanConfiguration(String key, boolean dflt) {
      String val = this.getConfigurationValue(key);
      return val == null?dflt:Boolean.valueOf(val).booleanValue();
   }

   private void initConfiguration() {
      this.allowFlawedContext = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedContext", true);
      this.allowFlawedDiscovery = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedDiscovery", true);
      this.allowFlawedHierarchy = this.getBooleanConfiguration("org.apache.commons.logging.Log.allowFlawedHierarchy", true);
   }

   private Log discoverLogImplementation(String logCategory) throws LogConfigurationException {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Discovering a Log implementation...");
      }

      this.initConfiguration();
      Log result = null;
      String specifiedLogClassName = this.findUserSpecifiedLogClassName();
      if(specifiedLogClassName != null) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("Attempting to load user-specified log class \'" + specifiedLogClassName + "\'...");
         }

         result = this.createLogFromClass(specifiedLogClassName, logCategory, true);
         if(result == null) {
            StringBuffer messageBuffer = new StringBuffer("User-specified log class \'");
            messageBuffer.append(specifiedLogClassName);
            messageBuffer.append("\' cannot be found or is not useable.");
            this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Log4JLogger");
            this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk14Logger");
            this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.Jdk13LumberjackLogger");
            this.informUponSimilarName(messageBuffer, specifiedLogClassName, "org.apache.commons.logging.impl.SimpleLog");
            throw new LogConfigurationException(messageBuffer.toString());
         } else {
            return result;
         }
      } else {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("No user-specified Log implementation; performing discovery using the standard supported logging implementations...");
         }

         for(int i = 0; i < classesToDiscover.length && result == null; ++i) {
            result = this.createLogFromClass(classesToDiscover[i], logCategory, true);
         }

         if(result == null) {
            throw new LogConfigurationException("No suitable Log implementation");
         } else {
            return result;
         }
      }
   }

   private void informUponSimilarName(StringBuffer messageBuffer, String name, String candidate) {
      if(!name.equals(candidate)) {
         if(name.regionMatches(true, 0, candidate, 0, PKG_LEN + 5)) {
            messageBuffer.append(" Did you mean \'");
            messageBuffer.append(candidate);
            messageBuffer.append("\'?");
         }

      }
   }

   private String findUserSpecifiedLogClassName() {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Trying to get log class from attribute \'org.apache.commons.logging.Log\'");
      }

      String specifiedClass = (String)this.getAttribute("org.apache.commons.logging.Log");
      if(specifiedClass == null) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("Trying to get log class from attribute \'org.apache.commons.logging.log\'");
         }

         specifiedClass = (String)this.getAttribute("org.apache.commons.logging.log");
      }

      if(specifiedClass == null) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("Trying to get log class from system property \'org.apache.commons.logging.Log\'");
         }

         try {
            specifiedClass = getSystemProperty("org.apache.commons.logging.Log", (String)null);
         } catch (SecurityException var4) {
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("No access allowed to system property \'org.apache.commons.logging.Log\' - " + var4.getMessage());
            }
         }
      }

      if(specifiedClass == null) {
         if(isDiagnosticsEnabled()) {
            this.logDiagnostic("Trying to get log class from system property \'org.apache.commons.logging.log\'");
         }

         try {
            specifiedClass = getSystemProperty("org.apache.commons.logging.log", (String)null);
         } catch (SecurityException var3) {
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic("No access allowed to system property \'org.apache.commons.logging.log\' - " + var3.getMessage());
            }
         }
      }

      if(specifiedClass != null) {
         specifiedClass = specifiedClass.trim();
      }

      return specifiedClass;
   }

   private Log createLogFromClass(String logAdapterClassName, String logCategory, boolean affectState) throws LogConfigurationException {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Attempting to instantiate \'" + logAdapterClassName + "\'");
      }

      Object[] params = new Object[]{logCategory};
      Log logAdapter = null;
      Constructor constructor = null;
      Class logAdapterClass = null;
      ClassLoader currentCL = this.getBaseClassLoader();

      while(true) {
         this.logDiagnostic("Trying to load \'" + logAdapterClassName + "\' from classloader " + LogFactory.objectId(currentCL));

         try {
            if(isDiagnosticsEnabled()) {
               String resourceName = logAdapterClassName.replace('.', '/') + ".class";
               URL url;
               if(currentCL != null) {
                  url = currentCL.getResource(resourceName);
               } else {
                  url = ClassLoader.getSystemResource(resourceName + ".class");
               }

               if(url == null) {
                  this.logDiagnostic("Class \'" + logAdapterClassName + "\' [" + resourceName + "] cannot be found.");
               } else {
                  this.logDiagnostic("Class \'" + logAdapterClassName + "\' was found at \'" + url + "\'");
               }
            }

            Class c;
            try {
               c = Class.forName(logAdapterClassName, true, currentCL);
            } catch (ClassNotFoundException var15) {
               String msg = var15.getMessage();
               this.logDiagnostic("The log adapter \'" + logAdapterClassName + "\' is not available via classloader " + LogFactory.objectId(currentCL) + ": " + msg.trim());

               try {
                  c = Class.forName(logAdapterClassName);
               } catch (ClassNotFoundException var14) {
                  msg = var14.getMessage();
                  this.logDiagnostic("The log adapter \'" + logAdapterClassName + "\' is not available via the LogFactoryImpl class classloader: " + msg.trim());
                  break;
               }
            }

            constructor = c.getConstructor(this.logConstructorSignature);
            Object o = constructor.newInstance(params);
            if(o instanceof Log) {
               logAdapterClass = c;
               logAdapter = (Log)o;
               break;
            }

            this.handleFlawedHierarchy(currentCL, c);
         } catch (NoClassDefFoundError var16) {
            String msg = var16.getMessage();
            this.logDiagnostic("The log adapter \'" + logAdapterClassName + "\' is missing dependencies when loaded via classloader " + LogFactory.objectId(currentCL) + ": " + msg.trim());
            break;
         } catch (ExceptionInInitializerError var17) {
            String msg = var17.getMessage();
            this.logDiagnostic("The log adapter \'" + logAdapterClassName + "\' is unable to initialize itself when loaded via classloader " + LogFactory.objectId(currentCL) + ": " + msg.trim());
            break;
         } catch (LogConfigurationException var18) {
            throw var18;
         } catch (Throwable var19) {
            LogFactory.handleThrowable(var19);
            this.handleFlawedDiscovery(logAdapterClassName, currentCL, var19);
         }

         if(currentCL == null) {
            break;
         }

         currentCL = this.getParentClassLoader(currentCL);
      }

      if(logAdapterClass != null && affectState) {
         this.logClassName = logAdapterClassName;
         this.logConstructor = constructor;

         try {
            this.logMethod = logAdapterClass.getMethod("setLogFactory", this.logMethodSignature);
            this.logDiagnostic("Found method setLogFactory(LogFactory) in \'" + logAdapterClassName + "\'");
         } catch (Throwable var13) {
            LogFactory.handleThrowable(var13);
            this.logMethod = null;
            this.logDiagnostic("[INFO] \'" + logAdapterClassName + "\' from classloader " + LogFactory.objectId(currentCL) + " does not declare optional method " + "setLogFactory(LogFactory)");
         }

         this.logDiagnostic("Log adapter \'" + logAdapterClassName + "\' from classloader " + LogFactory.objectId(logAdapterClass.getClassLoader()) + " has been selected for use.");
      }

      return logAdapter;
   }

   private ClassLoader getBaseClassLoader() throws LogConfigurationException {
      ClassLoader thisClassLoader = getClassLoader(class$org$apache$commons$logging$impl$LogFactoryImpl == null?(class$org$apache$commons$logging$impl$LogFactoryImpl = class$("org.apache.commons.logging.impl.LogFactoryImpl")):class$org$apache$commons$logging$impl$LogFactoryImpl);
      if(!this.useTCCL) {
         return thisClassLoader;
      } else {
         ClassLoader contextClassLoader = getContextClassLoaderInternal();
         ClassLoader baseClassLoader = this.getLowestClassLoader(contextClassLoader, thisClassLoader);
         if(baseClassLoader == null) {
            if(this.allowFlawedContext) {
               if(isDiagnosticsEnabled()) {
                  this.logDiagnostic("[WARNING] the context classloader is not part of a parent-child relationship with the classloader that loaded LogFactoryImpl.");
               }

               return contextClassLoader;
            } else {
               throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
            }
         } else {
            if(baseClassLoader != contextClassLoader) {
               if(!this.allowFlawedContext) {
                  throw new LogConfigurationException("Bad classloader hierarchy; LogFactoryImpl was loaded via a classloader that is not related to the current context classloader.");
               }

               if(isDiagnosticsEnabled()) {
                  this.logDiagnostic("Warning: the context classloader is an ancestor of the classloader that loaded LogFactoryImpl; it should be the same or a descendant. The application using commons-logging should ensure the context classloader is used correctly.");
               }
            }

            return baseClassLoader;
         }
      }
   }

   private ClassLoader getLowestClassLoader(ClassLoader c1, ClassLoader c2) {
      if(c1 == null) {
         return c2;
      } else if(c2 == null) {
         return c1;
      } else {
         for(ClassLoader current = c1; current != null; current = this.getParentClassLoader(current)) {
            if(current == c2) {
               return c1;
            }
         }

         for(ClassLoader var4 = c2; var4 != null; var4 = this.getParentClassLoader(var4)) {
            if(var4 == c1) {
               return c2;
            }
         }

         return null;
      }
   }

   private void handleFlawedDiscovery(String logAdapterClassName, ClassLoader classLoader, Throwable discoveryFlaw) {
      if(isDiagnosticsEnabled()) {
         this.logDiagnostic("Could not instantiate Log \'" + logAdapterClassName + "\' -- " + discoveryFlaw.getClass().getName() + ": " + discoveryFlaw.getLocalizedMessage());
         if(discoveryFlaw instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException)discoveryFlaw;
            Throwable cause = ite.getTargetException();
            if(cause != null) {
               this.logDiagnostic("... InvocationTargetException: " + cause.getClass().getName() + ": " + cause.getLocalizedMessage());
               if(cause instanceof ExceptionInInitializerError) {
                  ExceptionInInitializerError eiie = (ExceptionInInitializerError)cause;
                  Throwable cause2 = eiie.getException();
                  if(cause2 != null) {
                     StringWriter sw = new StringWriter();
                     cause2.printStackTrace(new PrintWriter(sw, true));
                     this.logDiagnostic("... ExceptionInInitializerError: " + sw.toString());
                  }
               }
            }
         }
      }

      if(!this.allowFlawedDiscovery) {
         throw new LogConfigurationException(discoveryFlaw);
      }
   }

   private void handleFlawedHierarchy(ClassLoader badClassLoader, Class badClass) throws LogConfigurationException {
      boolean implementsLog = false;
      String logInterfaceName = (class$org$apache$commons$logging$Log == null?(class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log")):class$org$apache$commons$logging$Log).getName();
      Class[] interfaces = badClass.getInterfaces();

      for(int i = 0; i < interfaces.length; ++i) {
         if(logInterfaceName.equals(interfaces[i].getName())) {
            implementsLog = true;
            break;
         }
      }

      if(implementsLog) {
         if(isDiagnosticsEnabled()) {
            try {
               ClassLoader logInterfaceClassLoader = getClassLoader(class$org$apache$commons$logging$Log == null?(class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log")):class$org$apache$commons$logging$Log);
               this.logDiagnostic("Class \'" + badClass.getName() + "\' was found in classloader " + LogFactory.objectId(badClassLoader) + ". It is bound to a Log interface which is not" + " the one loaded from classloader " + LogFactory.objectId(logInterfaceClassLoader));
            } catch (Throwable var7) {
               LogFactory.handleThrowable(var7);
               this.logDiagnostic("Error while trying to output diagnostics about bad class \'" + badClass + "\'");
            }
         }

         if(!this.allowFlawedHierarchy) {
            StringBuffer msg = new StringBuffer();
            msg.append("Terminating logging for this context ");
            msg.append("due to bad log hierarchy. ");
            msg.append("You have more than one version of \'");
            msg.append((class$org$apache$commons$logging$Log == null?(class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log")):class$org$apache$commons$logging$Log).getName());
            msg.append("\' visible.");
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic(msg.toString());
            }

            throw new LogConfigurationException(msg.toString());
         }

         if(isDiagnosticsEnabled()) {
            StringBuffer msg = new StringBuffer();
            msg.append("Warning: bad log hierarchy. ");
            msg.append("You have more than one version of \'");
            msg.append((class$org$apache$commons$logging$Log == null?(class$org$apache$commons$logging$Log = class$("org.apache.commons.logging.Log")):class$org$apache$commons$logging$Log).getName());
            msg.append("\' visible.");
            this.logDiagnostic(msg.toString());
         }
      } else {
         if(!this.allowFlawedDiscovery) {
            StringBuffer msg = new StringBuffer();
            msg.append("Terminating logging for this context. ");
            msg.append("Log class \'");
            msg.append(badClass.getName());
            msg.append("\' does not implement the Log interface.");
            if(isDiagnosticsEnabled()) {
               this.logDiagnostic(msg.toString());
            }

            throw new LogConfigurationException(msg.toString());
         }

         if(isDiagnosticsEnabled()) {
            StringBuffer msg = new StringBuffer();
            msg.append("[WARNING] Log class \'");
            msg.append(badClass.getName());
            msg.append("\' does not implement the Log interface.");
            this.logDiagnostic(msg.toString());
         }
      }

   }

   // $FF: synthetic method
   static Class class$(String x0) {
      try {
         return Class.forName(x0);
      } catch (ClassNotFoundException var2) {
         throw new NoClassDefFoundError(var2.getMessage());
      }
   }
}
