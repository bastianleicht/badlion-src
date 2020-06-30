package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class GL14 {
   public static final int GL_GENERATE_MIPMAP = 33169;
   public static final int GL_GENERATE_MIPMAP_HINT = 33170;
   public static final int GL_DEPTH_COMPONENT16 = 33189;
   public static final int GL_DEPTH_COMPONENT24 = 33190;
   public static final int GL_DEPTH_COMPONENT32 = 33191;
   public static final int GL_TEXTURE_DEPTH_SIZE = 34890;
   public static final int GL_DEPTH_TEXTURE_MODE = 34891;
   public static final int GL_TEXTURE_COMPARE_MODE = 34892;
   public static final int GL_TEXTURE_COMPARE_FUNC = 34893;
   public static final int GL_COMPARE_R_TO_TEXTURE = 34894;
   public static final int GL_FOG_COORDINATE_SOURCE = 33872;
   public static final int GL_FOG_COORDINATE = 33873;
   public static final int GL_FRAGMENT_DEPTH = 33874;
   public static final int GL_CURRENT_FOG_COORDINATE = 33875;
   public static final int GL_FOG_COORDINATE_ARRAY_TYPE = 33876;
   public static final int GL_FOG_COORDINATE_ARRAY_STRIDE = 33877;
   public static final int GL_FOG_COORDINATE_ARRAY_POINTER = 33878;
   public static final int GL_FOG_COORDINATE_ARRAY = 33879;
   public static final int GL_POINT_SIZE_MIN = 33062;
   public static final int GL_POINT_SIZE_MAX = 33063;
   public static final int GL_POINT_FADE_THRESHOLD_SIZE = 33064;
   public static final int GL_POINT_DISTANCE_ATTENUATION = 33065;
   public static final int GL_COLOR_SUM = 33880;
   public static final int GL_CURRENT_SECONDARY_COLOR = 33881;
   public static final int GL_SECONDARY_COLOR_ARRAY_SIZE = 33882;
   public static final int GL_SECONDARY_COLOR_ARRAY_TYPE = 33883;
   public static final int GL_SECONDARY_COLOR_ARRAY_STRIDE = 33884;
   public static final int GL_SECONDARY_COLOR_ARRAY_POINTER = 33885;
   public static final int GL_SECONDARY_COLOR_ARRAY = 33886;
   public static final int GL_BLEND_DST_RGB = 32968;
   public static final int GL_BLEND_SRC_RGB = 32969;
   public static final int GL_BLEND_DST_ALPHA = 32970;
   public static final int GL_BLEND_SRC_ALPHA = 32971;
   public static final int GL_INCR_WRAP = 34055;
   public static final int GL_DECR_WRAP = 34056;
   public static final int GL_TEXTURE_FILTER_CONTROL = 34048;
   public static final int GL_TEXTURE_LOD_BIAS = 34049;
   public static final int GL_MAX_TEXTURE_LOD_BIAS = 34045;
   public static final int GL_MIRRORED_REPEAT = 33648;
   public static final int GL_BLEND_COLOR = 32773;
   public static final int GL_BLEND_EQUATION = 32777;
   public static final int GL_FUNC_ADD = 32774;
   public static final int GL_FUNC_SUBTRACT = 32778;
   public static final int GL_FUNC_REVERSE_SUBTRACT = 32779;
   public static final int GL_MIN = 32775;
   public static final int GL_MAX = 32776;

   public static void glBlendEquation(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBlendEquation;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBlendEquation(mode, function_pointer);
   }

   static native void nglBlendEquation(int var0, long var1);

   public static void glBlendColor(float red, float green, float blue, float alpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBlendColor;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBlendColor(red, green, blue, alpha, function_pointer);
   }

   static native void nglBlendColor(float var0, float var1, float var2, float var3, long var4);

   public static void glFogCoordf(float coord) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogCoordf(coord, function_pointer);
   }

   static native void nglFogCoordf(float var0, long var1);

   public static void glFogCoordd(double coord) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordd;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogCoordd(coord, function_pointer);
   }

   static native void nglFogCoordd(double var0, long var2);

   public static void glFogCoordPointer(int stride, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL14_glFogCoordPointer_data = data;
      }

      nglFogCoordPointer(5130, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glFogCoordPointer(int stride, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).GL14_glFogCoordPointer_data = data;
      }

      nglFogCoordPointer(5126, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglFogCoordPointer(int var0, int var1, long var2, long var4);

   public static void glFogCoordPointer(int type, int stride, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglFogCoordPointerBO(type, stride, data_buffer_offset, function_pointer);
   }

   static native void nglFogCoordPointerBO(int var0, int var1, long var2, long var4);

   public static void glMultiDrawArrays(int mode, IntBuffer piFirst, IntBuffer piCount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArrays;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(piFirst);
      BufferChecks.checkBuffer(piCount, piFirst.remaining());
      nglMultiDrawArrays(mode, MemoryUtil.getAddress(piFirst), MemoryUtil.getAddress(piCount), piFirst.remaining(), function_pointer);
   }

   static native void nglMultiDrawArrays(int var0, long var1, long var3, int var5, long var6);

   public static void glPointParameteri(int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameteri;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPointParameteri(pname, param, function_pointer);
   }

   static native void nglPointParameteri(int var0, int var1, long var2);

   public static void glPointParameterf(int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameterf;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPointParameterf(pname, param, function_pointer);
   }

   static native void nglPointParameterf(int var0, float var1, long var2);

   public static void glPointParameter(int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglPointParameteriv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglPointParameteriv(int var0, long var1, long var3);

   public static void glPointParameter(int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPointParameterfv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglPointParameterfv(pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglPointParameterfv(int var0, long var1, long var3);

   public static void glSecondaryColor3b(byte red, byte green, byte blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColor3b;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSecondaryColor3b(red, green, blue, function_pointer);
   }

   static native void nglSecondaryColor3b(byte var0, byte var1, byte var2, long var3);

   public static void glSecondaryColor3f(float red, float green, float blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColor3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSecondaryColor3f(red, green, blue, function_pointer);
   }

   static native void nglSecondaryColor3f(float var0, float var1, float var2, long var3);

   public static void glSecondaryColor3d(double red, double green, double blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColor3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSecondaryColor3d(red, green, blue, function_pointer);
   }

   static native void nglSecondaryColor3d(double var0, double var2, double var4, long var6);

   public static void glSecondaryColor3ub(byte red, byte green, byte blue) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColor3ub;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSecondaryColor3ub(red, green, blue, function_pointer);
   }

   static native void nglSecondaryColor3ub(byte var0, byte var1, byte var2, long var3);

   public static void glSecondaryColorPointer(int size, int stride, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglSecondaryColorPointer(size, 5130, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glSecondaryColorPointer(int size, int stride, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglSecondaryColorPointer(size, 5126, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glSecondaryColorPointer(int size, boolean unsigned, int stride, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglSecondaryColorPointer(size, unsigned?5121:5120, stride, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglSecondaryColorPointer(int var0, int var1, int var2, long var3, long var5);

   public static void glSecondaryColorPointer(int size, int type, int stride, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColorPointer;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglSecondaryColorPointerBO(size, type, stride, data_buffer_offset, function_pointer);
   }

   static native void nglSecondaryColorPointerBO(int var0, int var1, int var2, long var3, long var5);

   public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBlendFuncSeparate;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha, function_pointer);
   }

   static native void nglBlendFuncSeparate(int var0, int var1, int var2, int var3, long var4);

   public static void glWindowPos2f(float x, float y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos2f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos2f(x, y, function_pointer);
   }

   static native void nglWindowPos2f(float var0, float var1, long var2);

   public static void glWindowPos2d(double x, double y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos2d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos2d(x, y, function_pointer);
   }

   static native void nglWindowPos2d(double var0, double var2, long var4);

   public static void glWindowPos2i(int x, int y) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos2i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos2i(x, y, function_pointer);
   }

   static native void nglWindowPos2i(int var0, int var1, long var2);

   public static void glWindowPos3f(float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos3f;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos3f(x, y, z, function_pointer);
   }

   static native void nglWindowPos3f(float var0, float var1, float var2, long var3);

   public static void glWindowPos3d(double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos3d;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos3d(x, y, z, function_pointer);
   }

   static native void nglWindowPos3d(double var0, double var2, double var4, long var6);

   public static void glWindowPos3i(int x, int y, int z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWindowPos3i;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWindowPos3i(x, y, z, function_pointer);
   }

   static native void nglWindowPos3i(int var0, int var1, int var2, long var3);
}
