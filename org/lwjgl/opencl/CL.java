package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.Sys;
import org.lwjgl.opencl.CLCapabilities;

public final class CL {
   private static boolean created;

   private static native void nCreate(String var0) throws LWJGLException;

   private static native void nCreateDefault() throws LWJGLException;

   private static native void nDestroy();

   public static boolean isCreated() {
      return created;
   }

   public static void create() throws LWJGLException {
      if(!created) {
         String libname;
         String[] library_names;
         switch(LWJGLUtil.getPlatform()) {
         case 1:
            libname = "OpenCL";
            library_names = new String[]{"libOpenCL64.so", "libOpenCL.so"};
            break;
         case 2:
            libname = "OpenCL";
            library_names = new String[]{"OpenCL.dylib"};
            break;
         case 3:
            libname = "OpenCL";
            library_names = new String[]{"OpenCL.dll"};
            break;
         default:
            throw new LWJGLException("Unknown platform: " + LWJGLUtil.getPlatform());
         }

         String[] oclPaths = LWJGLUtil.getLibraryPaths(libname, library_names, CL.class.getClassLoader());
         LWJGLUtil.log("Found " + oclPaths.length + " OpenCL paths");

         for(String oclPath : oclPaths) {
            try {
               nCreate(oclPath);
               created = true;
               break;
            } catch (LWJGLException var8) {
               LWJGLUtil.log("Failed to load " + oclPath + ": " + var8.getMessage());
            }
         }

         if(!created && LWJGLUtil.getPlatform() == 2) {
            nCreateDefault();
            created = true;
         }

         if(!created) {
            throw new LWJGLException("Could not locate OpenCL library.");
         } else if(!CLCapabilities.OpenCL10) {
            throw new RuntimeException("OpenCL 1.0 not supported.");
         }
      }
   }

   public static void destroy() {
   }

   static long getFunctionAddress(String[] aliases) {
      for(String aliase : aliases) {
         long address = getFunctionAddress(aliase);
         if(address != 0L) {
            return address;
         }
      }

      return 0L;
   }

   static long getFunctionAddress(String name) {
      ByteBuffer buffer = MemoryUtil.encodeASCII(name);
      return ngetFunctionAddress(MemoryUtil.getAddress(buffer));
   }

   private static native long ngetFunctionAddress(long var0);

   static native ByteBuffer getHostBuffer(long var0, int var2);

   private static native void resetNativeStubs(Class var0);

   static {
      Sys.initialize();
   }
}
