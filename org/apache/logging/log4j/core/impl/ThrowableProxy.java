package org.apache.logging.log4j.core.impl;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.net.URL;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.impl.ReflectiveCallerClassUtility;
import org.apache.logging.log4j.core.impl.StackTracePackageElement;
import org.apache.logging.log4j.status.StatusLogger;

public class ThrowableProxy implements Serializable {
   private static final long serialVersionUID = -2752771578252251910L;
   private static final Logger LOGGER = StatusLogger.getLogger();
   private static final ThrowableProxy.PrivateSecurityManager SECURITY_MANAGER;
   private static final Method GET_SUPPRESSED;
   private static final Method ADD_SUPPRESSED;
   private final ThrowableProxy proxyCause;
   private final Throwable throwable;
   private final String name;
   private final StackTracePackageElement[] callerPackageData;
   private int commonElementCount;

   public ThrowableProxy(Throwable throwable) {
      this.throwable = throwable;
      this.name = throwable.getClass().getName();
      Map<String, ThrowableProxy.CacheEntry> map = new HashMap();
      Stack<Class<?>> stack = this.getCurrentStack();
      this.callerPackageData = this.resolvePackageData(stack, map, (StackTraceElement[])null, throwable.getStackTrace());
      this.proxyCause = throwable.getCause() == null?null:new ThrowableProxy(throwable, stack, map, throwable.getCause());
      this.setSuppressed(throwable);
   }

   private ThrowableProxy(Throwable parent, Stack stack, Map map, Throwable cause) {
      this.throwable = cause;
      this.name = cause.getClass().getName();
      this.callerPackageData = this.resolvePackageData(stack, map, parent.getStackTrace(), cause.getStackTrace());
      this.proxyCause = cause.getCause() == null?null:new ThrowableProxy(parent, stack, map, cause.getCause());
      this.setSuppressed(cause);
   }

   public Throwable getThrowable() {
      return this.throwable;
   }

   public ThrowableProxy getCause() {
      return this.proxyCause;
   }

   public String getName() {
      return this.name;
   }

   public int getCommonElementCount() {
      return this.commonElementCount;
   }

   public StackTracePackageElement[] getPackageData() {
      return this.callerPackageData;
   }

   public String toString() {
      String msg = this.throwable.getMessage();
      return msg != null?this.name + ": " + msg:this.name;
   }

   public String getRootCauseStackTrace() {
      return this.getRootCauseStackTrace((List)null);
   }

   public String getRootCauseStackTrace(List packages) {
      StringBuilder sb = new StringBuilder();
      if(this.proxyCause != null) {
         this.formatWrapper(sb, this.proxyCause);
         sb.append("Wrapped by: ");
      }

      sb.append(this.toString());
      sb.append("\n");
      this.formatElements(sb, 0, this.throwable.getStackTrace(), this.callerPackageData, packages);
      return sb.toString();
   }

   public void formatWrapper(StringBuilder sb, ThrowableProxy cause) {
      this.formatWrapper(sb, cause, (List)null);
   }

   public void formatWrapper(StringBuilder sb, ThrowableProxy cause, List packages) {
      Throwable caused = cause.getCause() != null?cause.getCause().getThrowable():null;
      if(caused != null) {
         this.formatWrapper(sb, cause.proxyCause);
         sb.append("Wrapped by: ");
      }

      sb.append(cause).append("\n");
      this.formatElements(sb, cause.commonElementCount, cause.getThrowable().getStackTrace(), cause.callerPackageData, packages);
   }

   public String getExtendedStackTrace() {
      return this.getExtendedStackTrace((List)null);
   }

   public String getExtendedStackTrace(List packages) {
      StringBuilder sb = new StringBuilder(this.name);
      String msg = this.throwable.getMessage();
      if(msg != null) {
         sb.append(": ").append(this.throwable.getMessage());
      }

      sb.append("\n");
      this.formatElements(sb, 0, this.throwable.getStackTrace(), this.callerPackageData, packages);
      if(this.proxyCause != null) {
         this.formatCause(sb, this.proxyCause, packages);
      }

      return sb.toString();
   }

   public String getSuppressedStackTrace() {
      ThrowableProxy[] suppressed = this.getSuppressed();
      if(suppressed != null && suppressed.length != 0) {
         StringBuilder sb = new StringBuilder("Suppressed Stack Trace Elements:\n");

         for(ThrowableProxy proxy : suppressed) {
            sb.append(proxy.getExtendedStackTrace());
         }

         return sb.toString();
      } else {
         return "";
      }
   }

   private void formatCause(StringBuilder sb, ThrowableProxy cause, List packages) {
      sb.append("Caused by: ").append(cause).append("\n");
      this.formatElements(sb, cause.commonElementCount, cause.getThrowable().getStackTrace(), cause.callerPackageData, packages);
      if(cause.getCause() != null) {
         this.formatCause(sb, cause.proxyCause, packages);
      }

   }

   private void formatElements(StringBuilder sb, int commonCount, StackTraceElement[] causedTrace, StackTracePackageElement[] packageData, List packages) {
      if(packages != null && packages.size() != 0) {
         int count = 0;

         for(int i = 0; i < packageData.length; ++i) {
            if(!this.isSuppressed(causedTrace[i], packages)) {
               if(count > 0) {
                  if(count == 1) {
                     sb.append("\t....\n");
                  } else {
                     sb.append("\t... suppressed ").append(count).append(" lines\n");
                  }

                  count = 0;
               }

               this.formatEntry(causedTrace[i], packageData[i], sb);
            } else {
               ++count;
            }
         }

         if(count > 0) {
            if(count == 1) {
               sb.append("\t...\n");
            } else {
               sb.append("\t... suppressed ").append(count).append(" lines\n");
            }
         }
      } else {
         for(int i = 0; i < packageData.length; ++i) {
            this.formatEntry(causedTrace[i], packageData[i], sb);
         }
      }

      if(commonCount != 0) {
         sb.append("\t... ").append(commonCount).append(" more").append("\n");
      }

   }

   private void formatEntry(StackTraceElement element, StackTracePackageElement packageData, StringBuilder sb) {
      sb.append("\tat ");
      sb.append(element);
      sb.append(" ");
      sb.append(packageData);
      sb.append("\n");
   }

   private boolean isSuppressed(StackTraceElement element, List packages) {
      String className = element.getClassName();

      for(String pkg : packages) {
         if(className.startsWith(pkg)) {
            return true;
         }
      }

      return false;
   }

   private Stack getCurrentStack() {
      if(ReflectiveCallerClassUtility.isSupported()) {
         Stack<Class<?>> classes = new Stack();
         int index = 1;

         for(Class<?> clazz = ReflectiveCallerClassUtility.getCaller(index); clazz != null; clazz = ReflectiveCallerClassUtility.getCaller(index)) {
            classes.push(clazz);
            ++index;
         }

         return classes;
      } else if(SECURITY_MANAGER == null) {
         return new Stack();
      } else {
         Class<?>[] array = SECURITY_MANAGER.getClasses();
         Stack<Class<?>> classes = new Stack();

         for(Class<?> clazz : array) {
            classes.push(clazz);
         }

         return classes;
      }
   }

   StackTracePackageElement[] resolvePackageData(Stack stack, Map map, StackTraceElement[] rootTrace, StackTraceElement[] stackTrace) {
      int stackLength;
      if(rootTrace != null) {
         int rootIndex = rootTrace.length - 1;

         int stackIndex;
         for(stackIndex = stackTrace.length - 1; rootIndex >= 0 && stackIndex >= 0 && rootTrace[rootIndex].equals(stackTrace[stackIndex]); --stackIndex) {
            --rootIndex;
         }

         this.commonElementCount = stackTrace.length - 1 - stackIndex;
         stackLength = stackIndex + 1;
      } else {
         this.commonElementCount = 0;
         stackLength = stackTrace.length;
      }

      StackTracePackageElement[] packageArray = new StackTracePackageElement[stackLength];
      Class<?> clazz = stack.isEmpty()?null:(Class)stack.peek();
      ClassLoader lastLoader = null;

      for(int i = stackLength - 1; i >= 0; --i) {
         String className = stackTrace[i].getClassName();
         if(clazz != null && className.equals(clazz.getName())) {
            ThrowableProxy.CacheEntry entry = this.resolvePackageElement(clazz, true);
            packageArray[i] = entry.element;
            lastLoader = entry.loader;
            stack.pop();
            clazz = stack.isEmpty()?null:(Class)stack.peek();
         } else if(map.containsKey(className)) {
            ThrowableProxy.CacheEntry entry = (ThrowableProxy.CacheEntry)map.get(className);
            packageArray[i] = entry.element;
            if(entry.loader != null) {
               lastLoader = entry.loader;
            }
         } else {
            ThrowableProxy.CacheEntry entry = this.resolvePackageElement(this.loadClass(lastLoader, className), false);
            packageArray[i] = entry.element;
            map.put(className, entry);
            if(entry.loader != null) {
               lastLoader = entry.loader;
            }
         }
      }

      return packageArray;
   }

   private ThrowableProxy.CacheEntry resolvePackageElement(Class callerClass, boolean exact) {
      String location = "?";
      String version = "?";
      ClassLoader lastLoader = null;
      if(callerClass != null) {
         try {
            CodeSource source = callerClass.getProtectionDomain().getCodeSource();
            if(source != null) {
               URL locationURL = source.getLocation();
               if(locationURL != null) {
                  String str = locationURL.toString().replace('\\', '/');
                  int index = str.lastIndexOf("/");
                  if(index >= 0 && index == str.length() - 1) {
                     index = str.lastIndexOf("/", index - 1);
                     location = str.substring(index + 1);
                  } else {
                     location = str.substring(index + 1);
                  }
               }
            }
         } catch (Exception var10) {
            ;
         }

         Package pkg = callerClass.getPackage();
         if(pkg != null) {
            String ver = pkg.getImplementationVersion();
            if(ver != null) {
               version = ver;
            }
         }

         lastLoader = callerClass.getClassLoader();
      }

      return new ThrowableProxy.CacheEntry(new StackTracePackageElement(location, version, exact), lastLoader);
   }

   private Class loadClass(ClassLoader lastLoader, String className) {
      if(lastLoader != null) {
         try {
            Class<?> clazz = lastLoader.loadClass(className);
            if(clazz != null) {
               return clazz;
            }
         } catch (Exception var10) {
            ;
         }
      }

      Class var11;
      try {
         var11 = Thread.currentThread().getContextClassLoader().loadClass(className);
      } catch (ClassNotFoundException var9) {
         try {
            var11 = Class.forName(className);
         } catch (ClassNotFoundException var8) {
            try {
               var11 = this.getClass().getClassLoader().loadClass(className);
            } catch (ClassNotFoundException var7) {
               return null;
            }
         }
      }

      return var11;
   }

   public ThrowableProxy[] getSuppressed() {
      if(GET_SUPPRESSED != null) {
         try {
            return (ThrowableProxy[])((ThrowableProxy[])GET_SUPPRESSED.invoke(this.throwable, new Object[0]));
         } catch (Exception var2) {
            return null;
         }
      } else {
         return null;
      }
   }

   private void setSuppressed(Throwable throwable) {
      if(GET_SUPPRESSED != null && ADD_SUPPRESSED != null) {
         try {
            Throwable[] array = (Throwable[])((Throwable[])GET_SUPPRESSED.invoke(throwable, new Object[0]));

            for(Throwable t : array) {
               ADD_SUPPRESSED.invoke(this, new Object[]{new ThrowableProxy(t)});
            }
         } catch (Exception var7) {
            ;
         }
      }

   }

   static {
      if(ReflectiveCallerClassUtility.isSupported()) {
         SECURITY_MANAGER = null;
      } else {
         ThrowableProxy.PrivateSecurityManager securityManager;
         try {
            securityManager = new ThrowableProxy.PrivateSecurityManager();
            if(securityManager.getClasses() == null) {
               securityManager = null;
               LOGGER.error("Unable to obtain call stack from security manager.");
            }
         } catch (Exception var7) {
            securityManager = null;
            LOGGER.debug((String)"Unable to install security manager.", (Throwable)var7);
         }

         SECURITY_MANAGER = securityManager;
      }

      Method getSuppressed = null;
      Method addSuppressed = null;
      Method[] methods = Throwable.class.getMethods();

      for(Method method : methods) {
         if(method.getName().equals("getSuppressed")) {
            getSuppressed = method;
         } else if(method.getName().equals("addSuppressed")) {
            addSuppressed = method;
         }
      }

      GET_SUPPRESSED = getSuppressed;
      ADD_SUPPRESSED = addSuppressed;
   }

   class CacheEntry {
      private final StackTracePackageElement element;
      private final ClassLoader loader;

      public CacheEntry(StackTracePackageElement element, ClassLoader loader) {
         this.element = element;
         this.loader = loader;
      }
   }

   private static class PrivateSecurityManager extends SecurityManager {
      private PrivateSecurityManager() {
      }

      public Class[] getClasses() {
         return this.getClassContext();
      }
   }
}
