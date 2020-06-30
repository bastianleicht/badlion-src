package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class AMDNameGenDelete {
   public static final int GL_DATA_BUFFER_AMD = 37201;
   public static final int GL_PERFORMANCE_MONITOR_AMD = 37202;
   public static final int GL_QUERY_OBJECT_AMD = 37203;
   public static final int GL_VERTEX_ARRAY_OBJECT_AMD = 37204;
   public static final int GL_SAMPLER_OBJECT_AMD = 37205;

   public static void glGenNamesAMD(int identifier, IntBuffer names) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenNamesAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(names);
      nglGenNamesAMD(identifier, names.remaining(), MemoryUtil.getAddress(names), function_pointer);
   }

   static native void nglGenNamesAMD(int var0, int var1, long var2, long var4);

   public static int glGenNamesAMD(int identifier) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenNamesAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer names = APIUtil.getBufferInt(caps);
      nglGenNamesAMD(identifier, 1, MemoryUtil.getAddress(names), function_pointer);
      return names.get(0);
   }

   public static void glDeleteNamesAMD(int identifier, IntBuffer names) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteNamesAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(names);
      nglDeleteNamesAMD(identifier, names.remaining(), MemoryUtil.getAddress(names), function_pointer);
   }

   static native void nglDeleteNamesAMD(int var0, int var1, long var2, long var4);

   public static void glDeleteNamesAMD(int identifier, int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteNamesAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteNamesAMD(identifier, 1, APIUtil.getInt(caps, name), function_pointer);
   }

   public static boolean glIsNameAMD(int identifier, int name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsNameAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsNameAMD(identifier, name, function_pointer);
      return __result;
   }

   static native boolean nglIsNameAMD(int var0, int var1, long var2);
}
