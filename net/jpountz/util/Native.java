package net.jpountz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public enum Native {
   private static boolean loaded = false;

   private static String arch() {
      return System.getProperty("os.arch");
   }

   private static Native.OS os() {
      String osName = System.getProperty("os.name");
      if(osName.contains("Linux")) {
         return Native.OS.LINUX;
      } else if(osName.contains("Mac")) {
         return Native.OS.MAC;
      } else if(osName.contains("Windows")) {
         return Native.OS.WINDOWS;
      } else if(!osName.contains("Solaris") && !osName.contains("SunOS")) {
         throw new UnsupportedOperationException("Unsupported operating system: " + osName);
      } else {
         return Native.OS.SOLARIS;
      }
   }

   private static String resourceName() {
      Native.OS os = os();
      return "/" + os.name + "/" + arch() + "/liblz4-java." + os.libExtension;
   }

   public static synchronized boolean isLoaded() {
      return loaded;
   }

   public static synchronized void load() {
      if(!loaded) {
         String resourceName = resourceName();
         InputStream is = Native.class.getResourceAsStream(resourceName);
         if(is == null) {
            throw new UnsupportedOperationException("Unsupported OS/arch, cannot find " + resourceName + ". Please try building from source.");
         } else {
            try {
               File tempLib = File.createTempFile("liblz4-java", "." + os().libExtension);
               FileOutputStream out = new FileOutputStream(tempLib);

               try {
                  byte[] buf = new byte[4096];

                  while(true) {
                     int read = is.read(buf);
                     if(read == -1) {
                        try {
                           out.close();
                           out = null;
                        } catch (IOException var14) {
                           ;
                        }

                        System.load(tempLib.getAbsolutePath());
                        loaded = true;
                        return;
                     }

                     out.write(buf, 0, read);
                  }
               } finally {
                  try {
                     if(out != null) {
                        out.close();
                     }
                  } catch (IOException var13) {
                     ;
                  }

                  if(tempLib != null && tempLib.exists()) {
                     if(!loaded) {
                        tempLib.delete();
                     } else {
                        tempLib.deleteOnExit();
                     }
                  }

               }
            } catch (IOException var16) {
               throw new ExceptionInInitializerError("Cannot unpack liblz4-java");
            }
         }
      }
   }

   private static enum OS {
      WINDOWS("win32", "so"),
      LINUX("linux", "so"),
      MAC("darwin", "dylib"),
      SOLARIS("solaris", "so");

      public final String name;
      public final String libExtension;

      private OS(String name, String libExtension) {
         this.name = name;
         this.libExtension = libExtension;
      }
   }
}
