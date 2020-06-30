package org.lwjgl;

import java.awt.Toolkit;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.lwjgl.J2SESysImplementation;
import org.lwjgl.LWJGLUtil;

final class LinuxSysImplementation extends J2SESysImplementation {
   private static final int JNI_VERSION = 19;

   public int getRequiredJNIVersion() {
      return 19;
   }

   public boolean openURL(String url) {
      String[] browsers = new String[]{"sensible-browser", "xdg-open", "google-chrome", "chromium", "firefox", "iceweasel", "mozilla", "opera", "konqueror", "nautilus", "galeon", "netscape"};

      for(String browser : browsers) {
         try {
            LWJGLUtil.execPrivileged(new String[]{browser, url});
            return true;
         } catch (Exception var8) {
            var8.printStackTrace(System.err);
         }
      }

      return false;
   }

   public boolean has64Bit() {
      return true;
   }

   static {
      Toolkit.getDefaultToolkit();
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            try {
               System.loadLibrary("jawt");
            } catch (UnsatisfiedLinkError var2) {
               ;
            }

            return null;
         }
      });
   }
}
