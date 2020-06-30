package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVOcclusionQuery {
   public static final int GL_OCCLUSION_TEST_HP = 33125;
   public static final int GL_OCCLUSION_TEST_RESULT_HP = 33126;
   public static final int GL_PIXEL_COUNTER_BITS_NV = 34916;
   public static final int GL_CURRENT_OCCLUSION_QUERY_ID_NV = 34917;
   public static final int GL_PIXEL_COUNT_NV = 34918;
   public static final int GL_PIXEL_COUNT_AVAILABLE_NV = 34919;

   public static void glGenOcclusionQueriesNV(IntBuffer piIDs) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenOcclusionQueriesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piIDs);
      nglGenOcclusionQueriesNV(piIDs.remaining(), MemoryUtil.getAddress(piIDs), function_pointer);
   }

   static native void nglGenOcclusionQueriesNV(int var0, long var1, long var3);

   public static int glGenOcclusionQueriesNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenOcclusionQueriesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer piIDs = APIUtil.getBufferInt(caps);
      nglGenOcclusionQueriesNV(1, MemoryUtil.getAddress(piIDs), function_pointer);
      return piIDs.get(0);
   }

   public static void glDeleteOcclusionQueriesNV(IntBuffer piIDs) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteOcclusionQueriesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piIDs);
      nglDeleteOcclusionQueriesNV(piIDs.remaining(), MemoryUtil.getAddress(piIDs), function_pointer);
   }

   static native void nglDeleteOcclusionQueriesNV(int var0, long var1, long var3);

   public static void glDeleteOcclusionQueriesNV(int piID) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteOcclusionQueriesNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteOcclusionQueriesNV(1, APIUtil.getInt(caps, piID), function_pointer);
   }

   public static boolean glIsOcclusionQueryNV(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsOcclusionQueryNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsOcclusionQueryNV(id, function_pointer);
      return __result;
   }

   static native boolean nglIsOcclusionQueryNV(int var0, long var1);

   public static void glBeginOcclusionQueryNV(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginOcclusionQueryNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginOcclusionQueryNV(id, function_pointer);
   }

   static native void nglBeginOcclusionQueryNV(int var0, long var1);

   public static void glEndOcclusionQueryNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndOcclusionQueryNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndOcclusionQueryNV(function_pointer);
   }

   static native void nglEndOcclusionQueryNV(long var0);

   public static void glGetOcclusionQueryuNV(int id, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetOcclusionQueryuivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetOcclusionQueryuivNV(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetOcclusionQueryuivNV(int var0, int var1, long var2, long var4);

   public static int glGetOcclusionQueryuiNV(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetOcclusionQueryuivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetOcclusionQueryuivNV(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetOcclusionQueryNV(int id, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetOcclusionQueryivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetOcclusionQueryivNV(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetOcclusionQueryivNV(int var0, int var1, long var2, long var4);

   public static int glGetOcclusionQueryiNV(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetOcclusionQueryivNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetOcclusionQueryivNV(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }
}
