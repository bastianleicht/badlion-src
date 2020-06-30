package net.java.games.input;

import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.DummyWindow;
import net.java.games.input.WinTabContext;
import net.java.games.util.plugins.Plugin;

public class WinTabEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private static boolean supported = false;
   private final Controller[] controllers;
   private final List active_devices = new ArrayList();
   private final WinTabContext winTabContext;

   static void loadLibrary(final String lib_name) {
      AccessController.doPrivileged(new PrivilegedAction() {
         public final Object run() {
            try {
               String lib_path = System.getProperty("net.java.games.input.librarypath");
               if(lib_path != null) {
                  System.load(lib_path + File.separator + System.mapLibraryName(lib_name));
               } else {
                  System.loadLibrary(lib_name);
               }
            } catch (UnsatisfiedLinkError var2) {
               var2.printStackTrace();
               WinTabEnvironmentPlugin.supported = false;
            }

            return null;
         }
      });
   }

   static String getPrivilegedProperty(final String property) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(property);
         }
      });
   }

   static String getPrivilegedProperty(final String property, final String default_value) {
      return (String)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            return System.getProperty(property, default_value);
         }
      });
   }

   public WinTabEnvironmentPlugin() {
      if(this.isSupported()) {
         DummyWindow window = null;
         WinTabContext winTabContext = null;
         Controller[] controllers = new Controller[0];

         try {
            window = new DummyWindow();
            winTabContext = new WinTabContext(window);

            try {
               winTabContext.open();
               controllers = winTabContext.getControllers();
            } catch (Exception var5) {
               window.destroy();
               throw var5;
            }
         } catch (Exception var6) {
            logln("Failed to enumerate devices: " + var6.getMessage());
            var6.printStackTrace();
         }

         this.controllers = controllers;
         this.winTabContext = winTabContext;
         AccessController.doPrivileged(new PrivilegedAction() {
            public final Object run() {
               Runtime.getRuntime().addShutdownHook(WinTabEnvironmentPlugin.this.new ShutdownHook());
               return null;
            }
         });
      } else {
         this.winTabContext = null;
         this.controllers = new Controller[0];
      }

   }

   public boolean isSupported() {
      return supported;
   }

   public Controller[] getControllers() {
      return this.controllers;
   }

   static {
      String osName = getPrivilegedProperty("os.name", "").trim();
      if(osName.startsWith("Windows")) {
         supported = true;
         loadLibrary("jinput-wintab");
      }

   }

   private final class ShutdownHook extends Thread {
      private ShutdownHook() {
      }

      public final void run() {
         for(int i = 0; i < WinTabEnvironmentPlugin.this.active_devices.size(); ++i) {
            ;
         }

         WinTabEnvironmentPlugin.this.winTabContext.close();
      }
   }
}
