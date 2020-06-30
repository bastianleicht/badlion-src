package org.lwjgl.opengl;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class EXTFogCoord {
   public static final int GL_FOG_COORDINATE_SOURCE_EXT = 33872;
   public static final int GL_FOG_COORDINATE_EXT = 33873;
   public static final int GL_FRAGMENT_DEPTH_EXT = 33874;
   public static final int GL_CURRENT_FOG_COORDINATE_EXT = 33875;
   public static final int GL_FOG_COORDINATE_ARRAY_TYPE_EXT = 33876;
   public static final int GL_FOG_COORDINATE_ARRAY_STRIDE_EXT = 33877;
   public static final int GL_FOG_COORDINATE_ARRAY_POINTER_EXT = 33878;
   public static final int GL_FOG_COORDINATE_ARRAY_EXT = 33879;

   public static void glFogCoordfEXT(float coord) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogCoordfEXT(coord, function_pointer);
   }

   static native void nglFogCoordfEXT(float var0, long var1);

   public static void glFogCoorddEXT(double coord) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoorddEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogCoorddEXT(coord, function_pointer);
   }

   static native void nglFogCoorddEXT(double var0, long var2);

   public static void glFogCoordPointerEXT(int stride, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_fog_coord_glFogCoordPointerEXT_data = data;
      }

      nglFogCoordPointerEXT(5130, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glFogCoordPointerEXT(int stride, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).EXT_fog_coord_glFogCoordPointerEXT_data = data;
      }

      nglFogCoordPointerEXT(5126, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglFogCoordPointerEXT(int var0, int var1, long var2, long var4);

   public static void glFogCoordPointerEXT(int type, int stride, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglFogCoordPointerEXTBO(type, stride, data_buffer_offset, function_pointer);
   }

   static native void nglFogCoordPointerEXTBO(int var0, int var1, long var2, long var4);
}
