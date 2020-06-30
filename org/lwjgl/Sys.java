package org.lwjgl;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.LinuxSysImplementation;
import org.lwjgl.MacOSXSysImplementation;
import org.lwjgl.SysImplementation;
import org.lwjgl.WindowsSysImplementation;
import org.lwjgl.input.Mouse;

public final class Sys {
   private static final String JNI_LIBRARY_NAME = "lwjgl";
   private static final String VERSION = "2.9.4";
   private static final String POSTFIX64BIT = "64";
   private static final SysImplementation implementation = createImplementation();
   private static final boolean is64Bit = implementation.getPointerSize() == 8;

   private static void doLoadLibrary(final String lib_name) {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String library_path = System.getProperty("org.lwjgl.librarypath");
            if(library_path != null) {
               System.load(library_path + File.separator + LWJGLUtil.mapLibraryName(lib_name));
            } else {
               System.loadLibrary(lib_name);
            }

            return null;
         }
      });
   }

   private static void loadLibrary(String lib_name) {
      String osArch = System.getProperty("os.arch");
      boolean try64First = LWJGLUtil.getPlatform() != 2 && ("amd64".equals(osArch) || "x86_64".equals(osArch));
      Error err = null;
      if(try64First) {
         try {
            doLoadLibrary(lib_name + "64");
            return;
         } catch (UnsatisfiedLinkError var8) {
            err = var8;
         }
      }

      try {
         doLoadLibrary(lib_name);
      } catch (UnsatisfiedLinkError var7) {
         if(try64First) {
            throw err;
         } else {
            if(implementation.has64Bit()) {
               try {
                  doLoadLibrary(lib_name + "64");
                  return;
               } catch (UnsatisfiedLinkError var6) {
                  LWJGLUtil.log("Failed to load 64 bit library: " + var6.getMessage());
               }
            }

            throw var7;
         }
      }
   }

   private static SysImplementation createImplementation() {
      switch(LWJGLUtil.getPlatform()) {
      case 1:
         return new LinuxSysImplementation();
      case 2:
         return new MacOSXSysImplementation();
      case 3:
         return new WindowsSysImplementation();
      default:
         throw new IllegalStateException("Unsupported platform");
      }
   }

   public static String getVersion() {
      return "2.9.4";
   }

   public static void initialize() {
   }

   public static boolean is64Bit() {
      return is64Bit;
   }

   public static long getTimerResolution() {
      return implementation.getTimerResolution();
   }

   public static long getTime() {
      return implementation.getTime() & Long.MAX_VALUE;
   }

   public static void alert(String title, String message) {
      boolean grabbed = Mouse.isGrabbed();
      if(grabbed) {
         Mouse.setGrabbed(false);
      }

      if(title == null) {
         title = "";
      }

      if(message == null) {
         message = "";
      }

      implementation.alert(title, message);
      if(grabbed) {
         Mouse.setGrabbed(true);
      }

   }

   public static boolean openURL(String url) {
      try {
         final Class<?> serviceManagerClass = Class.forName("javax.jnlp.ServiceManager");
         Method lookupMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Method run() throws Exception {
               return serviceManagerClass.getMethod("lookup", new Class[]{String.class});
            }
         });
         Object basicService = lookupMethod.invoke(serviceManagerClass, new Object[]{"javax.jnlp.BasicService"});
         final Class<?> basicServiceClass = Class.forName("javax.jnlp.BasicService");
         Method showDocumentMethod = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
            public Method run() throws Exception {
               return basicServiceClass.getMethod("showDocument", new Class[]{URL.class});
            }
         });

         try {
            Boolean ret = (Boolean)showDocumentMethod.invoke(basicService, new Object[]{new URL(url)});
            return ret.booleanValue();
         } catch (MalformedURLException var7) {
            var7.printStackTrace(System.err);
            return false;
         }
      } catch (Exception var8) {
         return implementation.openURL(url);
      }
   }

   public static String getClipboard() {
      return implementation.getClipboard();
   }

   static {
      loadLibrary("lwjgl");
      int native_jni_version = implementation.getJNIVersion();
      int required_version = implementation.getRequiredJNIVersion();
      if(native_jni_version != required_version) {
         throw new LinkageError("Version mismatch: jar version is \'" + required_version + "\', native library version is \'" + native_jni_version + "\'");
      } else {
         implementation.setDebug(LWJGLUtil.DEBUG);
      }
   }
}
