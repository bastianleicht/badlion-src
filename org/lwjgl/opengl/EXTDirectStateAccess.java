package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTDrawBuffers2;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class EXTDirectStateAccess {
   public static final int GL_PROGRAM_MATRIX_EXT = 36397;
   public static final int GL_TRANSPOSE_PROGRAM_MATRIX_EXT = 36398;
   public static final int GL_PROGRAM_MATRIX_STACK_DEPTH_EXT = 36399;

   public static void glClientAttribDefaultEXT(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClientAttribDefaultEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglClientAttribDefaultEXT(mask, function_pointer);
   }

   static native void nglClientAttribDefaultEXT(int var0, long var1);

   public static void glPushClientAttribDefaultEXT(int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glPushClientAttribDefaultEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglPushClientAttribDefaultEXT(mask, function_pointer);
   }

   static native void nglPushClientAttribDefaultEXT(int var0, long var1);

   public static void glMatrixLoadEXT(int matrixMode, FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixLoadfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMatrixLoadfEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixLoadfEXT(int var0, long var1, long var3);

   public static void glMatrixLoadEXT(int matrixMode, DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixLoaddEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMatrixLoaddEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixLoaddEXT(int var0, long var1, long var3);

   public static void glMatrixMultEXT(int matrixMode, FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixMultfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMatrixMultfEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixMultfEXT(int var0, long var1, long var3);

   public static void glMatrixMultEXT(int matrixMode, DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixMultdEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMatrixMultdEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixMultdEXT(int var0, long var1, long var3);

   public static void glMatrixLoadIdentityEXT(int matrixMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixLoadIdentityEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixLoadIdentityEXT(matrixMode, function_pointer);
   }

   static native void nglMatrixLoadIdentityEXT(int var0, long var1);

   public static void glMatrixRotatefEXT(int matrixMode, float angle, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixRotatefEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixRotatefEXT(matrixMode, angle, x, y, z, function_pointer);
   }

   static native void nglMatrixRotatefEXT(int var0, float var1, float var2, float var3, float var4, long var5);

   public static void glMatrixRotatedEXT(int matrixMode, double angle, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixRotatedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixRotatedEXT(matrixMode, angle, x, y, z, function_pointer);
   }

   static native void nglMatrixRotatedEXT(int var0, double var1, double var3, double var5, double var7, long var9);

   public static void glMatrixScalefEXT(int matrixMode, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixScalefEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixScalefEXT(matrixMode, x, y, z, function_pointer);
   }

   static native void nglMatrixScalefEXT(int var0, float var1, float var2, float var3, long var4);

   public static void glMatrixScaledEXT(int matrixMode, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixScaledEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixScaledEXT(matrixMode, x, y, z, function_pointer);
   }

   static native void nglMatrixScaledEXT(int var0, double var1, double var3, double var5, long var7);

   public static void glMatrixTranslatefEXT(int matrixMode, float x, float y, float z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixTranslatefEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixTranslatefEXT(matrixMode, x, y, z, function_pointer);
   }

   static native void nglMatrixTranslatefEXT(int var0, float var1, float var2, float var3, long var4);

   public static void glMatrixTranslatedEXT(int matrixMode, double x, double y, double z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixTranslatedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixTranslatedEXT(matrixMode, x, y, z, function_pointer);
   }

   static native void nglMatrixTranslatedEXT(int var0, double var1, double var3, double var5, long var7);

   public static void glMatrixOrthoEXT(int matrixMode, double l, double r, double b, double t, double n, double f) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixOrthoEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixOrthoEXT(matrixMode, l, r, b, t, n, f, function_pointer);
   }

   static native void nglMatrixOrthoEXT(int var0, double var1, double var3, double var5, double var7, double var9, double var11, long var13);

   public static void glMatrixFrustumEXT(int matrixMode, double l, double r, double b, double t, double n, double f) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixFrustumEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixFrustumEXT(matrixMode, l, r, b, t, n, f, function_pointer);
   }

   static native void nglMatrixFrustumEXT(int var0, double var1, double var3, double var5, double var7, double var9, double var11, long var13);

   public static void glMatrixPushEXT(int matrixMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixPushEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixPushEXT(matrixMode, function_pointer);
   }

   static native void nglMatrixPushEXT(int var0, long var1);

   public static void glMatrixPopEXT(int matrixMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixPopEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMatrixPopEXT(matrixMode, function_pointer);
   }

   static native void nglMatrixPopEXT(int var0, long var1);

   public static void glTextureParameteriEXT(int texture, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameteriEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureParameteriEXT(texture, target, pname, param, function_pointer);
   }

   static native void nglTextureParameteriEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glTextureParameterEXT(int texture, int target, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 4);
      nglTextureParameterivEXT(texture, target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglTextureParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glTextureParameterfEXT(int texture, int target, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureParameterfEXT(texture, target, pname, param, function_pointer);
   }

   static native void nglTextureParameterfEXT(int var0, int var1, int var2, float var3, long var4);

   public static void glTextureParameterEXT(int texture, int target, int pname, FloatBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)param, 4);
      nglTextureParameterfvEXT(texture, target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglTextureParameterfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglTextureImage1DEXT(texture, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTextureImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureImage1DEXTBO(texture, target, level, internalformat, width, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglTextureImage2DEXT(texture, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTextureImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureImage2DEXTBO(texture, target, level, internalformat, width, height, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglTextureSubImage1DEXT(texture, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTextureSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureSubImage1DEXTBO(texture, target, level, xoffset, width, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureSubImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTextureSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureSubImage2DEXTBO(texture, target, level, xoffset, yoffset, width, height, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureSubImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCopyTextureImage1DEXT(int texture, int target, int level, int internalformat, int x, int y, int width, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTextureImage1DEXT(texture, target, level, internalformat, x, y, width, border, function_pointer);
   }

   static native void nglCopyTextureImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glCopyTextureImage2DEXT(int texture, int target, int level, int internalformat, int x, int y, int width, int height, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTextureImage2DEXT(texture, target, level, internalformat, x, y, width, height, border, function_pointer);
   }

   static native void nglCopyTextureImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);

   public static void glCopyTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int x, int y, int width) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTextureSubImage1DEXT(texture, target, level, xoffset, x, y, width, function_pointer);
   }

   static native void nglCopyTextureSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7);

   public static void glCopyTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyTextureSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTextureImageEXT(texture, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTextureImageEXT(texture, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTextureImageEXT(texture, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTextureImageEXT(texture, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetTextureImageEXT(texture, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglGetTextureImageEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glGetTextureImageEXT(int texture, int target, int level, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetTextureImageEXTBO(texture, target, level, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglGetTextureImageEXTBO(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glGetTextureParameterEXT(int texture, int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTextureParameterfvEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureParameterfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static float glGetTextureParameterfEXT(int texture, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTextureParameterfvEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTextureParameterEXT(int texture, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTextureParameterivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetTextureParameteriEXT(int texture, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTextureParameterivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTextureLevelParameterEXT(int texture, int target, int level, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureLevelParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetTextureLevelParameterfvEXT(texture, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureLevelParameterfvEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static float glGetTextureLevelParameterfEXT(int texture, int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureLevelParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetTextureLevelParameterfvEXT(texture, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTextureLevelParameterEXT(int texture, int target, int level, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureLevelParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTextureLevelParameterivEXT(texture, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureLevelParameterivEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static int glGetTextureLevelParameteriEXT(int texture, int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureLevelParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTextureLevelParameterivEXT(texture, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTextureImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureImage3DEXTBO(texture, target, level, internalformat, width, height, depth, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTextureSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTextureSubImage3DEXTBO(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTextureSubImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCopyTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyTextureSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10);

   public static void glBindMultiTextureEXT(int texunit, int target, int texture) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindMultiTextureEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindMultiTextureEXT(texunit, target, texture, function_pointer);
   }

   static native void nglBindMultiTextureEXT(int var0, int var1, int var2, long var3);

   public static void glMultiTexCoordPointerEXT(int texunit, int size, int stride, DoubleBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglMultiTexCoordPointerEXT(texunit, size, 5130, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   public static void glMultiTexCoordPointerEXT(int texunit, int size, int stride, FloatBuffer pointer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(pointer);
      nglMultiTexCoordPointerEXT(texunit, size, 5126, stride, MemoryUtil.getAddress(pointer), function_pointer);
   }

   static native void nglMultiTexCoordPointerEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glMultiTexCoordPointerEXT(int texunit, int size, int type, int stride, long pointer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexCoordPointerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglMultiTexCoordPointerEXTBO(texunit, size, type, stride, pointer_buffer_offset, function_pointer);
   }

   static native void nglMultiTexCoordPointerEXTBO(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glMultiTexEnvfEXT(int texunit, int target, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexEnvfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexEnvfEXT(texunit, target, pname, param, function_pointer);
   }

   static native void nglMultiTexEnvfEXT(int var0, int var1, int var2, float var3, long var4);

   public static void glMultiTexEnvEXT(int texunit, int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexEnvfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglMultiTexEnvfvEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexEnvfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexEnviEXT(int texunit, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexEnviEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexEnviEXT(texunit, target, pname, param, function_pointer);
   }

   static native void nglMultiTexEnviEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glMultiTexEnvEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexEnvivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMultiTexEnvivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexEnvivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexGendEXT(int texunit, int coord, int pname, double param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGendEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexGendEXT(texunit, coord, pname, param, function_pointer);
   }

   static native void nglMultiTexGendEXT(int var0, int var1, int var2, double var3, long var5);

   public static void glMultiTexGenEXT(int texunit, int coord, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGendvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglMultiTexGendvEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexGendvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexGenfEXT(int texunit, int coord, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGenfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexGenfEXT(texunit, coord, pname, param, function_pointer);
   }

   static native void nglMultiTexGenfEXT(int var0, int var1, int var2, float var3, long var4);

   public static void glMultiTexGenEXT(int texunit, int coord, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGenfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglMultiTexGenfvEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexGenfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexGeniEXT(int texunit, int coord, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGeniEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexGeniEXT(texunit, coord, pname, param, function_pointer);
   }

   static native void nglMultiTexGeniEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glMultiTexGenEXT(int texunit, int coord, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexGenivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMultiTexGenivEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexGenivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMultiTexEnvEXT(int texunit, int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexEnvfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMultiTexEnvfvEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexEnvfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMultiTexEnvEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexEnvivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexEnvivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexEnvivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMultiTexGenEXT(int texunit, int coord, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexGendvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetMultiTexGendvEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexGendvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMultiTexGenEXT(int texunit, int coord, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexGenfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMultiTexGenfvEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexGenfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetMultiTexGenEXT(int texunit, int coord, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexGenivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexGenivEXT(texunit, coord, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexGenivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexParameteriEXT(int texunit, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameteriEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexParameteriEXT(texunit, target, pname, param, function_pointer);
   }

   static native void nglMultiTexParameteriEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glMultiTexParameterEXT(int texunit, int target, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 4);
      nglMultiTexParameterivEXT(texunit, target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglMultiTexParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexParameterfEXT(int texunit, int target, int pname, float param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterfEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexParameterfEXT(texunit, target, pname, param, function_pointer);
   }

   static native void nglMultiTexParameterfEXT(int var0, int var1, int var2, float var3, long var4);

   public static void glMultiTexParameterEXT(int texunit, int target, int pname, FloatBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)param, 4);
      nglMultiTexParameterfvEXT(texunit, target, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglMultiTexParameterfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage1DStorage(pixels, format, type, width));
      }

      nglMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglMultiTexImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexImage1DEXTBO(texunit, target, level, internalformat, width, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage2DStorage(pixels, format, type, width, height));
      }

      nglMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglMultiTexImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexImage2DEXTBO(texunit, target, level, internalformat, width, height, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, 1, 1));
      nglMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglMultiTexSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexSubImage1DEXTBO(texunit, target, level, xoffset, width, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexSubImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, 1));
      nglMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglMultiTexSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexSubImage2DEXTBO(texunit, target, level, xoffset, yoffset, width, height, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexSubImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCopyMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int x, int y, int width, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyMultiTexImage1DEXT(texunit, target, level, internalformat, x, y, width, border, function_pointer);
   }

   static native void nglCopyMultiTexImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8);

   public static void glCopyMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int x, int y, int width, int height, int border) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyMultiTexImage2DEXT(texunit, target, level, internalformat, x, y, width, height, border, function_pointer);
   }

   static native void nglCopyMultiTexImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);

   public static void glCopyMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int x, int y, int width) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyMultiTexSubImage1DEXT(texunit, target, level, xoffset, x, y, width, function_pointer);
   }

   static native void nglCopyMultiTexSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7);

   public static void glCopyMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyMultiTexSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetMultiTexImageEXT(texunit, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetMultiTexImageEXT(texunit, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetMultiTexImageEXT(texunit, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetMultiTexImageEXT(texunit, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, 1, 1, 1));
      nglGetMultiTexImageEXT(texunit, target, level, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglGetMultiTexImageEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glGetMultiTexImageEXT(int texunit, int target, int level, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetMultiTexImageEXTBO(texunit, target, level, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglGetMultiTexImageEXTBO(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glGetMultiTexParameterEXT(int texunit, int target, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMultiTexParameterfvEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexParameterfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static float glGetMultiTexParameterfEXT(int texunit, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetMultiTexParameterfvEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetMultiTexParameterEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexParameterivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetMultiTexParameteriEXT(int texunit, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetMultiTexParameterivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetMultiTexLevelParameterEXT(int texunit, int target, int level, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexLevelParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetMultiTexLevelParameterfvEXT(texunit, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexLevelParameterfvEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static float glGetMultiTexLevelParameterfEXT(int texunit, int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexLevelParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetMultiTexLevelParameterfvEXT(texunit, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetMultiTexLevelParameterEXT(int texunit, int target, int level, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexLevelParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexLevelParameterivEXT(texunit, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexLevelParameterivEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static int glGetMultiTexLevelParameteriEXT(int texunit, int target, int level, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexLevelParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetMultiTexLevelParameterivEXT(texunit, target, level, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglMultiTexImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexImage3DEXTBO(texunit, target, level, internalformat, width, height, depth, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglMultiTexSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglMultiTexSubImage3DEXTBO(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglMultiTexSubImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCopyMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyMultiTexSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10);

   public static void glEnableClientStateIndexedEXT(int array, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableClientStateIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableClientStateIndexedEXT(array, index, function_pointer);
   }

   static native void nglEnableClientStateIndexedEXT(int var0, int var1, long var2);

   public static void glDisableClientStateIndexedEXT(int array, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableClientStateIndexedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableClientStateIndexedEXT(array, index, function_pointer);
   }

   static native void nglDisableClientStateIndexedEXT(int var0, int var1, long var2);

   public static void glEnableClientStateiEXT(int array, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableClientStateiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableClientStateiEXT(array, index, function_pointer);
   }

   static native void nglEnableClientStateiEXT(int var0, int var1, long var2);

   public static void glDisableClientStateiEXT(int array, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableClientStateiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableClientStateiEXT(array, index, function_pointer);
   }

   static native void nglDisableClientStateiEXT(int var0, int var1, long var2);

   public static void glGetFloatIndexedEXT(int pname, int index, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloatIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 16);
      nglGetFloatIndexedvEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetFloatIndexedvEXT(int var0, int var1, long var2, long var4);

   public static float glGetFloatIndexedEXT(int pname, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloatIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetFloatIndexedvEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetDoubleIndexedEXT(int pname, int index, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoubleIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 16);
      nglGetDoubleIndexedvEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetDoubleIndexedvEXT(int var0, int var1, long var2, long var4);

   public static double glGetDoubleIndexedEXT(int pname, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoubleIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      DoubleBuffer params = APIUtil.getBufferDouble(caps);
      nglGetDoubleIndexedvEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static ByteBuffer glGetPointerIndexedEXT(int pname, int index, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPointerIndexedvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetPointerIndexedvEXT(pname, index, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetPointerIndexedvEXT(int var0, int var1, long var2, long var4);

   public static void glGetFloatEXT(int pname, int index, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloati_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 16);
      nglGetFloati_vEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetFloati_vEXT(int var0, int var1, long var2, long var4);

   public static float glGetFloatEXT(int pname, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFloati_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetFloati_vEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetDoubleEXT(int pname, int index, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoublei_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 16);
      nglGetDoublei_vEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetDoublei_vEXT(int var0, int var1, long var2, long var4);

   public static double glGetDoubleEXT(int pname, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetDoublei_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      DoubleBuffer params = APIUtil.getBufferDouble(caps);
      nglGetDoublei_vEXT(pname, index, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static ByteBuffer glGetPointerEXT(int pname, int index, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetPointeri_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetPointeri_vEXT(pname, index, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetPointeri_vEXT(int var0, int var1, long var2, long var4);

   public static void glEnableIndexedEXT(int cap, int index) {
      EXTDrawBuffers2.glEnableIndexedEXT(cap, index);
   }

   public static void glDisableIndexedEXT(int cap, int index) {
      EXTDrawBuffers2.glDisableIndexedEXT(cap, index);
   }

   public static boolean glIsEnabledIndexedEXT(int cap, int index) {
      return EXTDrawBuffers2.glIsEnabledIndexedEXT(cap, index);
   }

   public static void glGetIntegerIndexedEXT(int pname, int index, IntBuffer params) {
      EXTDrawBuffers2.glGetIntegerIndexedEXT(pname, index, params);
   }

   public static int glGetIntegerIndexedEXT(int pname, int index) {
      return EXTDrawBuffers2.glGetIntegerIndexedEXT(pname, index);
   }

   public static void glGetBooleanIndexedEXT(int pname, int index, ByteBuffer params) {
      EXTDrawBuffers2.glGetBooleanIndexedEXT(pname, index, params);
   }

   public static boolean glGetBooleanIndexedEXT(int pname, int index) {
      return EXTDrawBuffers2.glGetBooleanIndexedEXT(pname, index);
   }

   public static void glNamedProgramStringEXT(int program, int target, int format, ByteBuffer string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramStringEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(string);
      nglNamedProgramStringEXT(program, target, format, string.remaining(), MemoryUtil.getAddress(string), function_pointer);
   }

   static native void nglNamedProgramStringEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glNamedProgramStringEXT(int program, int target, int format, CharSequence string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramStringEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedProgramStringEXT(program, target, format, string.length(), APIUtil.getBuffer(caps, string), function_pointer);
   }

   public static void glNamedProgramLocalParameter4dEXT(int program, int target, int index, double x, double y, double z, double w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameter4dEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedProgramLocalParameter4dEXT(program, target, index, x, y, z, w, function_pointer);
   }

   static native void nglNamedProgramLocalParameter4dEXT(int var0, int var1, int var2, double var3, double var5, double var7, double var9, long var11);

   public static void glNamedProgramLocalParameter4EXT(int program, int target, int index, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameter4dvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglNamedProgramLocalParameter4dvEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParameter4dvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glNamedProgramLocalParameter4fEXT(int program, int target, int index, float x, float y, float z, float w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameter4fEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedProgramLocalParameter4fEXT(program, target, index, x, y, z, w, function_pointer);
   }

   static native void nglNamedProgramLocalParameter4fEXT(int var0, int var1, int var2, float var3, float var4, float var5, float var6, long var7);

   public static void glNamedProgramLocalParameter4EXT(int program, int target, int index, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameter4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglNamedProgramLocalParameter4fvEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParameter4fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetNamedProgramLocalParameterEXT(int program, int target, int index, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramLocalParameterdvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetNamedProgramLocalParameterdvEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedProgramLocalParameterdvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetNamedProgramLocalParameterEXT(int program, int target, int index, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramLocalParameterfvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetNamedProgramLocalParameterfvEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedProgramLocalParameterfvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetNamedProgramEXT(int program, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedProgramivEXT(program, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedProgramivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetNamedProgramEXT(int program, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetNamedProgramivEXT(program, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetNamedProgramStringEXT(int program, int target, int pname, ByteBuffer string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramStringEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(string);
      nglGetNamedProgramStringEXT(program, target, pname, MemoryUtil.getAddress(string), function_pointer);
   }

   static native void nglGetNamedProgramStringEXT(int var0, int var1, int var2, long var3, long var5);

   public static String glGetNamedProgramStringEXT(int program, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramStringEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int programLength = glGetNamedProgramEXT(program, target, '蘧');
      ByteBuffer paramString = APIUtil.getBufferByte(caps, programLength);
      nglGetNamedProgramStringEXT(program, target, pname, MemoryUtil.getAddress(paramString), function_pointer);
      paramString.limit(programLength);
      return APIUtil.getString(caps, paramString);
   }

   public static void glCompressedTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureImage3DEXT(texture, target, level, internalformat, width, height, depth, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedTextureImage3DEXT(int texture, int target, int level, int internalformat, int width, int height, int depth, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureImage3DEXTBO(texture, target, level, internalformat, width, height, depth, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureImage2DEXT(texture, target, level, internalformat, width, height, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTextureImage2DEXT(int texture, int target, int level, int internalformat, int width, int height, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureImage2DEXTBO(texture, target, level, internalformat, width, height, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureImage1DEXT(texture, target, level, internalformat, width, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedTextureImage1DEXT(int texture, int target, int level, int internalformat, int width, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureImage1DEXTBO(texture, target, level, internalformat, width, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureSubImage3DEXT(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCompressedTextureSubImage3DEXT(int texture, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureSubImage3DEXTBO(texture, target, level, xoffset, yoffset, zoffset, width, height, depth, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureSubImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCompressedTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureSubImage2DEXT(texture, target, level, xoffset, yoffset, width, height, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedTextureSubImage2DEXT(int texture, int target, int level, int xoffset, int yoffset, int width, int height, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureSubImage2DEXTBO(texture, target, level, xoffset, yoffset, width, height, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureSubImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedTextureSubImage1DEXT(texture, target, level, xoffset, width, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedTextureSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedTextureSubImage1DEXT(int texture, int target, int level, int xoffset, int width, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedTextureSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedTextureSubImage1DEXTBO(texture, target, level, xoffset, width, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedTextureSubImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glGetCompressedTextureImageEXT(int texture, int target, int level, ByteBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTextureImageEXT(texture, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedTextureImageEXT(int texture, int target, int level, IntBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTextureImageEXT(texture, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedTextureImageEXT(int texture, int target, int level, ShortBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedTextureImageEXT(texture, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   static native void nglGetCompressedTextureImageEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetCompressedTextureImageEXT(int texture, int target, int level, long img_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedTextureImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetCompressedTextureImageEXTBO(texture, target, level, img_buffer_offset, function_pointer);
   }

   static native void nglGetCompressedTextureImageEXTBO(int var0, int var1, int var2, long var3, long var5);

   public static void glCompressedMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexImage3DEXT(texunit, target, level, internalformat, width, height, depth, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedMultiTexImage3DEXT(int texunit, int target, int level, int internalformat, int width, int height, int depth, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexImage3DEXTBO(texunit, target, level, internalformat, width, height, depth, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexImage2DEXT(texunit, target, level, internalformat, width, height, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedMultiTexImage2DEXT(int texunit, int target, int level, int internalformat, int width, int height, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexImage2DEXTBO(texunit, target, level, internalformat, width, height, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, long var8, long var10);

   public static void glCompressedMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexImage1DEXT(texunit, target, level, internalformat, width, border, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedMultiTexImage1DEXT(int texunit, int target, int level, int internalformat, int width, int border, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexImage1DEXTBO(texunit, target, level, internalformat, width, border, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexSubImage3DEXT(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexSubImage3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCompressedMultiTexSubImage3DEXT(int texunit, int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexSubImage3DEXTBO(texunit, target, level, xoffset, yoffset, zoffset, width, height, depth, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexSubImage3DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, long var11, long var13);

   public static void glCompressedMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexSubImage2DEXT(texunit, target, level, xoffset, yoffset, width, height, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexSubImage2DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedMultiTexSubImage2DEXT(int texunit, int target, int level, int xoffset, int yoffset, int width, int height, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexSubImage2DEXTBO(texunit, target, level, xoffset, yoffset, width, height, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexSubImage2DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glCompressedMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkDirect(data);
      nglCompressedMultiTexSubImage1DEXT(texunit, target, level, xoffset, width, format, data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglCompressedMultiTexSubImage1DEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glCompressedMultiTexSubImage1DEXT(int texunit, int target, int level, int xoffset, int width, int format, int data_imageSize, long data_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompressedMultiTexSubImage1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglCompressedMultiTexSubImage1DEXTBO(texunit, target, level, xoffset, width, format, data_imageSize, data_buffer_offset, function_pointer);
   }

   static native void nglCompressedMultiTexSubImage1DEXTBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7, long var9);

   public static void glGetCompressedMultiTexImageEXT(int texunit, int target, int level, ByteBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedMultiTexImageEXT(texunit, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedMultiTexImageEXT(int texunit, int target, int level, IntBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedMultiTexImageEXT(texunit, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   public static void glGetCompressedMultiTexImageEXT(int texunit, int target, int level, ShortBuffer img) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOdisabled(caps);
      BufferChecks.checkDirect(img);
      nglGetCompressedMultiTexImageEXT(texunit, target, level, MemoryUtil.getAddress(img), function_pointer);
   }

   static native void nglGetCompressedMultiTexImageEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetCompressedMultiTexImageEXT(int texunit, int target, int level, long img_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetCompressedMultiTexImageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensurePackPBOenabled(caps);
      nglGetCompressedMultiTexImageEXTBO(texunit, target, level, img_buffer_offset, function_pointer);
   }

   static native void nglGetCompressedMultiTexImageEXTBO(int var0, int var1, int var2, long var3, long var5);

   public static void glMatrixLoadTransposeEXT(int matrixMode, FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixLoadTransposefEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMatrixLoadTransposefEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixLoadTransposefEXT(int var0, long var1, long var3);

   public static void glMatrixLoadTransposeEXT(int matrixMode, DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixLoadTransposedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMatrixLoadTransposedEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixLoadTransposedEXT(int var0, long var1, long var3);

   public static void glMatrixMultTransposeEXT(int matrixMode, FloatBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixMultTransposefEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)m, 16);
      nglMatrixMultTransposefEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixMultTransposefEXT(int var0, long var1, long var3);

   public static void glMatrixMultTransposeEXT(int matrixMode, DoubleBuffer m) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMatrixMultTransposedEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)m, 16);
      nglMatrixMultTransposedEXT(matrixMode, MemoryUtil.getAddress(m), function_pointer);
   }

   static native void nglMatrixMultTransposedEXT(int var0, long var1, long var3);

   public static void glNamedBufferDataEXT(int buffer, long data_size, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedBufferDataEXT(buffer, data_size, 0L, usage, function_pointer);
   }

   public static void glNamedBufferDataEXT(int buffer, ByteBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferDataEXT(buffer, (long)data.remaining(), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glNamedBufferDataEXT(int buffer, DoubleBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferDataEXT(buffer, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glNamedBufferDataEXT(int buffer, FloatBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferDataEXT(buffer, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glNamedBufferDataEXT(int buffer, IntBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferDataEXT(buffer, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glNamedBufferDataEXT(int buffer, ShortBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferDataEXT(buffer, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   static native void nglNamedBufferDataEXT(int var0, long var1, long var3, int var5, long var6);

   public static void glNamedBufferSubDataEXT(int buffer, long offset, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferSubDataEXT(buffer, offset, (long)data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glNamedBufferSubDataEXT(int buffer, long offset, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glNamedBufferSubDataEXT(int buffer, long offset, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glNamedBufferSubDataEXT(int buffer, long offset, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glNamedBufferSubDataEXT(int buffer, long offset, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglNamedBufferSubDataEXT(int var0, long var1, long var3, long var5, long var7);

   public static ByteBuffer glMapNamedBufferEXT(int buffer, int access, ByteBuffer old_buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapNamedBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(old_buffer != null) {
         BufferChecks.checkDirect(old_buffer);
      }

      ByteBuffer __result = nglMapNamedBufferEXT(buffer, access, (long)glGetNamedBufferParameterEXT(buffer, '蝤'), old_buffer, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   public static ByteBuffer glMapNamedBufferEXT(int buffer, int access, long length, ByteBuffer old_buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapNamedBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(old_buffer != null) {
         BufferChecks.checkDirect(old_buffer);
      }

      ByteBuffer __result = nglMapNamedBufferEXT(buffer, access, length, old_buffer, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglMapNamedBufferEXT(int var0, int var1, long var2, ByteBuffer var4, long var5);

   public static boolean glUnmapNamedBufferEXT(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUnmapNamedBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglUnmapNamedBufferEXT(buffer, function_pointer);
      return __result;
   }

   static native boolean nglUnmapNamedBufferEXT(int var0, long var1);

   public static void glGetNamedBufferParameterEXT(int buffer, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedBufferParameterivEXT(buffer, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedBufferParameterivEXT(int var0, int var1, long var2, long var4);

   public static int glGetNamedBufferParameterEXT(int buffer, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetNamedBufferParameterivEXT(buffer, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static ByteBuffer glGetNamedBufferPointerEXT(int buffer, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferPointervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetNamedBufferPointervEXT(buffer, pname, (long)glGetNamedBufferParameterEXT(buffer, '蝤'), function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetNamedBufferPointervEXT(int var0, int var1, long var2, long var4);

   public static void glGetNamedBufferSubDataEXT(int buffer, long offset, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetNamedBufferSubDataEXT(buffer, offset, (long)data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetNamedBufferSubDataEXT(int buffer, long offset, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetNamedBufferSubDataEXT(int buffer, long offset, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetNamedBufferSubDataEXT(int buffer, long offset, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetNamedBufferSubDataEXT(int buffer, long offset, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetNamedBufferSubDataEXT(buffer, offset, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetNamedBufferSubDataEXT(int var0, long var1, long var3, long var5, long var7);

   public static void glProgramUniform1fEXT(int program, int location, float v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1fEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform1fEXT(program, location, v0, function_pointer);
   }

   static native void nglProgramUniform1fEXT(int var0, int var1, float var2, long var3);

   public static void glProgramUniform2fEXT(int program, int location, float v0, float v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2fEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform2fEXT(program, location, v0, v1, function_pointer);
   }

   static native void nglProgramUniform2fEXT(int var0, int var1, float var2, float var3, long var4);

   public static void glProgramUniform3fEXT(int program, int location, float v0, float v1, float v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3fEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform3fEXT(program, location, v0, v1, v2, function_pointer);
   }

   static native void nglProgramUniform3fEXT(int var0, int var1, float var2, float var3, float var4, long var5);

   public static void glProgramUniform4fEXT(int program, int location, float v0, float v1, float v2, float v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4fEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform4fEXT(program, location, v0, v1, v2, v3, function_pointer);
   }

   static native void nglProgramUniform4fEXT(int var0, int var1, float var2, float var3, float var4, float var5, long var6);

   public static void glProgramUniform1iEXT(int program, int location, int v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1iEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform1iEXT(program, location, v0, function_pointer);
   }

   static native void nglProgramUniform1iEXT(int var0, int var1, int var2, long var3);

   public static void glProgramUniform2iEXT(int program, int location, int v0, int v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2iEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform2iEXT(program, location, v0, v1, function_pointer);
   }

   static native void nglProgramUniform2iEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glProgramUniform3iEXT(int program, int location, int v0, int v1, int v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3iEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform3iEXT(program, location, v0, v1, v2, function_pointer);
   }

   static native void nglProgramUniform3iEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glProgramUniform4iEXT(int program, int location, int v0, int v1, int v2, int v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4iEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform4iEXT(program, location, v0, v1, v2, v3, function_pointer);
   }

   static native void nglProgramUniform4iEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramUniform1EXT(int program, int location, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform1fvEXT(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform1fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform2EXT(int program, int location, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform2fvEXT(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform2fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform3EXT(int program, int location, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform3fvEXT(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform3fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform4EXT(int program, int location, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform4fvEXT(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform4fvEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform1EXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform1ivEXT(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform1ivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform2EXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform2ivEXT(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform2ivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform3EXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform3ivEXT(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform3ivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform4EXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform4ivEXT(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform4ivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniformMatrix2EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2fvEXT(program, location, value.remaining() >> 2, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3fvEXT(program, location, value.remaining() / 9, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4fvEXT(program, location, value.remaining() >> 4, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix2x3EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2x3fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2x3fvEXT(program, location, value.remaining() / 6, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2x3fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3x2EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3x2fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3x2fvEXT(program, location, value.remaining() / 6, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3x2fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix2x4EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix2x4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix2x4fvEXT(program, location, value.remaining() >> 3, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix2x4fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4x2EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4x2fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4x2fvEXT(program, location, value.remaining() >> 3, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4x2fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix3x4EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix3x4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix3x4fvEXT(program, location, value.remaining() / 12, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix3x4fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glProgramUniformMatrix4x3EXT(int program, int location, boolean transpose, FloatBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniformMatrix4x3fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniformMatrix4x3fvEXT(program, location, value.remaining() / 12, transpose, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniformMatrix4x3fvEXT(int var0, int var1, int var2, boolean var3, long var4, long var6);

   public static void glTextureBufferEXT(int texture, int target, int internalformat, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureBufferEXT(texture, target, internalformat, buffer, function_pointer);
   }

   static native void nglTextureBufferEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glMultiTexBufferEXT(int texunit, int target, int internalformat, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexBufferEXT(texunit, target, internalformat, buffer, function_pointer);
   }

   static native void nglMultiTexBufferEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glTextureParameterIEXT(int texture, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTextureParameterIivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTextureParameterIivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glTextureParameterIEXT(int texture, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureParameterIivEXT(texture, target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glTextureParameterIuEXT(int texture, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglTextureParameterIuivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglTextureParameterIuivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glTextureParameterIuEXT(int texture, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureParameterIuivEXT(texture, target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glGetTextureParameterIEXT(int texture, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTextureParameterIivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureParameterIivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetTextureParameterIiEXT(int texture, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTextureParameterIivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetTextureParameterIuEXT(int texture, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetTextureParameterIuivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetTextureParameterIuivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetTextureParameterIuiEXT(int texture, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTextureParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetTextureParameterIuivEXT(texture, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glMultiTexParameterIEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMultiTexParameterIivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexParameterIivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexParameterIEXT(int texunit, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexParameterIivEXT(texunit, target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glMultiTexParameterIuEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglMultiTexParameterIuivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglMultiTexParameterIuivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glMultiTexParameterIuEXT(int texunit, int target, int pname, int param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexParameterIuivEXT(texunit, target, pname, APIUtil.getInt(caps, param), function_pointer);
   }

   public static void glGetMultiTexParameterIEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexParameterIivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexParameterIivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetMultiTexParameterIiEXT(int texunit, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetMultiTexParameterIivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetMultiTexParameterIuEXT(int texunit, int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetMultiTexParameterIuivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetMultiTexParameterIuivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetMultiTexParameterIuiEXT(int texunit, int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultiTexParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetMultiTexParameterIuivEXT(texunit, target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glProgramUniform1uiEXT(int program, int location, int v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1uiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform1uiEXT(program, location, v0, function_pointer);
   }

   static native void nglProgramUniform1uiEXT(int var0, int var1, int var2, long var3);

   public static void glProgramUniform2uiEXT(int program, int location, int v0, int v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2uiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform2uiEXT(program, location, v0, v1, function_pointer);
   }

   static native void nglProgramUniform2uiEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glProgramUniform3uiEXT(int program, int location, int v0, int v1, int v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3uiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform3uiEXT(program, location, v0, v1, v2, function_pointer);
   }

   static native void nglProgramUniform3uiEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glProgramUniform4uiEXT(int program, int location, int v0, int v1, int v2, int v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4uiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProgramUniform4uiEXT(program, location, v0, v1, v2, v3, function_pointer);
   }

   static native void nglProgramUniform4uiEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glProgramUniform1uEXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform1uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform1uivEXT(program, location, value.remaining(), MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform1uivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform2uEXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform2uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform2uivEXT(program, location, value.remaining() >> 1, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform2uivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform3uEXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform3uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform3uivEXT(program, location, value.remaining() / 3, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform3uivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glProgramUniform4uEXT(int program, int location, IntBuffer value) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProgramUniform4uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(value);
      nglProgramUniform4uivEXT(program, location, value.remaining() >> 2, MemoryUtil.getAddress(value), function_pointer);
   }

   static native void nglProgramUniform4uivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glNamedProgramLocalParameters4EXT(int program, int target, int index, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameters4fvEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglNamedProgramLocalParameters4fvEXT(program, target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParameters4fvEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glNamedProgramLocalParameterI4iEXT(int program, int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameterI4iEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedProgramLocalParameterI4iEXT(program, target, index, x, y, z, w, function_pointer);
   }

   static native void nglNamedProgramLocalParameterI4iEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7);

   public static void glNamedProgramLocalParameterI4EXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameterI4ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglNamedProgramLocalParameterI4ivEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParameterI4ivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glNamedProgramLocalParametersI4EXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParametersI4ivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglNamedProgramLocalParametersI4ivEXT(program, target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParametersI4ivEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glNamedProgramLocalParameterI4uiEXT(int program, int target, int index, int x, int y, int z, int w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameterI4uiEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedProgramLocalParameterI4uiEXT(program, target, index, x, y, z, w, function_pointer);
   }

   static native void nglNamedProgramLocalParameterI4uiEXT(int var0, int var1, int var2, int var3, int var4, int var5, int var6, long var7);

   public static void glNamedProgramLocalParameterI4uEXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParameterI4uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglNamedProgramLocalParameterI4uivEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParameterI4uivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glNamedProgramLocalParametersI4uEXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedProgramLocalParametersI4uivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglNamedProgramLocalParametersI4uivEXT(program, target, index, params.remaining() >> 2, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglNamedProgramLocalParametersI4uivEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glGetNamedProgramLocalParameterIEXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramLocalParameterIivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedProgramLocalParameterIivEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedProgramLocalParameterIivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glGetNamedProgramLocalParameterIuEXT(int program, int target, int index, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedProgramLocalParameterIuivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedProgramLocalParameterIuivEXT(program, target, index, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedProgramLocalParameterIuivEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glNamedRenderbufferStorageEXT(int renderbuffer, int internalformat, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedRenderbufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedRenderbufferStorageEXT(renderbuffer, internalformat, width, height, function_pointer);
   }

   static native void nglNamedRenderbufferStorageEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glGetNamedRenderbufferParameterEXT(int renderbuffer, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedRenderbufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedRenderbufferParameterivEXT(renderbuffer, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedRenderbufferParameterivEXT(int var0, int var1, long var2, long var4);

   public static int glGetNamedRenderbufferParameterEXT(int renderbuffer, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedRenderbufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetNamedRenderbufferParameterivEXT(renderbuffer, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glNamedRenderbufferStorageMultisampleEXT(int renderbuffer, int samples, int internalformat, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedRenderbufferStorageMultisampleEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedRenderbufferStorageMultisampleEXT(renderbuffer, samples, internalformat, width, height, function_pointer);
   }

   static native void nglNamedRenderbufferStorageMultisampleEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glNamedRenderbufferStorageMultisampleCoverageEXT(int renderbuffer, int coverageSamples, int colorSamples, int internalformat, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedRenderbufferStorageMultisampleCoverageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedRenderbufferStorageMultisampleCoverageEXT(renderbuffer, coverageSamples, colorSamples, internalformat, width, height, function_pointer);
   }

   static native void nglNamedRenderbufferStorageMultisampleCoverageEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static int glCheckNamedFramebufferStatusEXT(int framebuffer, int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCheckNamedFramebufferStatusEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglCheckNamedFramebufferStatusEXT(framebuffer, target, function_pointer);
      return __result;
   }

   static native int nglCheckNamedFramebufferStatusEXT(int var0, int var1, long var2);

   public static void glNamedFramebufferTexture1DEXT(int framebuffer, int attachment, int textarget, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTexture1DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTexture1DEXT(framebuffer, attachment, textarget, texture, level, function_pointer);
   }

   static native void nglNamedFramebufferTexture1DEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glNamedFramebufferTexture2DEXT(int framebuffer, int attachment, int textarget, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTexture2DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTexture2DEXT(framebuffer, attachment, textarget, texture, level, function_pointer);
   }

   static native void nglNamedFramebufferTexture2DEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glNamedFramebufferTexture3DEXT(int framebuffer, int attachment, int textarget, int texture, int level, int zoffset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTexture3DEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTexture3DEXT(framebuffer, attachment, textarget, texture, level, zoffset, function_pointer);
   }

   static native void nglNamedFramebufferTexture3DEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6);

   public static void glNamedFramebufferRenderbufferEXT(int framebuffer, int attachment, int renderbuffertarget, int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferRenderbufferEXT(framebuffer, attachment, renderbuffertarget, renderbuffer, function_pointer);
   }

   static native void nglNamedFramebufferRenderbufferEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glGetNamedFramebufferAttachmentParameterEXT(int framebuffer, int attachment, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedFramebufferAttachmentParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetNamedFramebufferAttachmentParameterivEXT(framebuffer, attachment, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetNamedFramebufferAttachmentParameterivEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetNamedFramebufferAttachmentParameterEXT(int framebuffer, int attachment, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetNamedFramebufferAttachmentParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetNamedFramebufferAttachmentParameterivEXT(framebuffer, attachment, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGenerateTextureMipmapEXT(int texture, int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenerateTextureMipmapEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglGenerateTextureMipmapEXT(texture, target, function_pointer);
   }

   static native void nglGenerateTextureMipmapEXT(int var0, int var1, long var2);

   public static void glGenerateMultiTexMipmapEXT(int texunit, int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenerateMultiTexMipmapEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglGenerateMultiTexMipmapEXT(texunit, target, function_pointer);
   }

   static native void nglGenerateMultiTexMipmapEXT(int var0, int var1, long var2);

   public static void glFramebufferDrawBufferEXT(int framebuffer, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferDrawBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferDrawBufferEXT(framebuffer, mode, function_pointer);
   }

   static native void nglFramebufferDrawBufferEXT(int var0, int var1, long var2);

   public static void glFramebufferDrawBuffersEXT(int framebuffer, IntBuffer bufs) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferDrawBuffersEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(bufs);
      nglFramebufferDrawBuffersEXT(framebuffer, bufs.remaining(), MemoryUtil.getAddress(bufs), function_pointer);
   }

   static native void nglFramebufferDrawBuffersEXT(int var0, int var1, long var2, long var4);

   public static void glFramebufferReadBufferEXT(int framebuffer, int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferReadBufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferReadBufferEXT(framebuffer, mode, function_pointer);
   }

   static native void nglFramebufferReadBufferEXT(int var0, int var1, long var2);

   public static void glGetFramebufferParameterEXT(int framebuffer, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 4);
      nglGetFramebufferParameterivEXT(framebuffer, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglGetFramebufferParameterivEXT(int var0, int var1, long var2, long var4);

   public static int glGetFramebufferParameterEXT(int framebuffer, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetFramebufferParameterivEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer param = APIUtil.getBufferInt(caps);
      nglGetFramebufferParameterivEXT(framebuffer, pname, MemoryUtil.getAddress(param), function_pointer);
      return param.get(0);
   }

   public static void glNamedCopyBufferSubDataEXT(int readBuffer, int writeBuffer, long readoffset, long writeoffset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedCopyBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedCopyBufferSubDataEXT(readBuffer, writeBuffer, readoffset, writeoffset, size, function_pointer);
   }

   static native void nglNamedCopyBufferSubDataEXT(int var0, int var1, long var2, long var4, long var6, long var8);

   public static void glNamedFramebufferTextureEXT(int framebuffer, int attachment, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTextureEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTextureEXT(framebuffer, attachment, texture, level, function_pointer);
   }

   static native void nglNamedFramebufferTextureEXT(int var0, int var1, int var2, int var3, long var4);

   public static void glNamedFramebufferTextureLayerEXT(int framebuffer, int attachment, int texture, int level, int layer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTextureLayerEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTextureLayerEXT(framebuffer, attachment, texture, level, layer, function_pointer);
   }

   static native void nglNamedFramebufferTextureLayerEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glNamedFramebufferTextureFaceEXT(int framebuffer, int attachment, int texture, int level, int face) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedFramebufferTextureFaceEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedFramebufferTextureFaceEXT(framebuffer, attachment, texture, level, face, function_pointer);
   }

   static native void nglNamedFramebufferTextureFaceEXT(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glTextureRenderbufferEXT(int texture, int target, int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTextureRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTextureRenderbufferEXT(texture, target, renderbuffer, function_pointer);
   }

   static native void nglTextureRenderbufferEXT(int var0, int var1, int var2, long var3);

   public static void glMultiTexRenderbufferEXT(int texunit, int target, int renderbuffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiTexRenderbufferEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglMultiTexRenderbufferEXT(texunit, target, renderbuffer, function_pointer);
   }

   static native void nglMultiTexRenderbufferEXT(int var0, int var1, int var2, long var3);

   public static void glVertexArrayVertexOffsetEXT(int vaobj, int buffer, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayVertexOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayVertexOffsetEXT(vaobj, buffer, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayVertexOffsetEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glVertexArrayColorOffsetEXT(int vaobj, int buffer, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayColorOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayColorOffsetEXT(vaobj, buffer, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayColorOffsetEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glVertexArrayEdgeFlagOffsetEXT(int vaobj, int buffer, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayEdgeFlagOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayEdgeFlagOffsetEXT(vaobj, buffer, stride, offset, function_pointer);
   }

   static native void nglVertexArrayEdgeFlagOffsetEXT(int var0, int var1, int var2, long var3, long var5);

   public static void glVertexArrayIndexOffsetEXT(int vaobj, int buffer, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayIndexOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayIndexOffsetEXT(vaobj, buffer, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayIndexOffsetEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glVertexArrayNormalOffsetEXT(int vaobj, int buffer, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayNormalOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayNormalOffsetEXT(vaobj, buffer, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayNormalOffsetEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glVertexArrayTexCoordOffsetEXT(int vaobj, int buffer, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayTexCoordOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayTexCoordOffsetEXT(vaobj, buffer, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayTexCoordOffsetEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glVertexArrayMultiTexCoordOffsetEXT(int vaobj, int buffer, int texunit, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayMultiTexCoordOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayMultiTexCoordOffsetEXT(vaobj, buffer, texunit, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayMultiTexCoordOffsetEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glVertexArrayFogCoordOffsetEXT(int vaobj, int buffer, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayFogCoordOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayFogCoordOffsetEXT(vaobj, buffer, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayFogCoordOffsetEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glVertexArraySecondaryColorOffsetEXT(int vaobj, int buffer, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArraySecondaryColorOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArraySecondaryColorOffsetEXT(vaobj, buffer, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArraySecondaryColorOffsetEXT(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glVertexArrayVertexAttribOffsetEXT(int vaobj, int buffer, int index, int size, int type, boolean normalized, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayVertexAttribOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayVertexAttribOffsetEXT(vaobj, buffer, index, size, type, normalized, stride, offset, function_pointer);
   }

   static native void nglVertexArrayVertexAttribOffsetEXT(int var0, int var1, int var2, int var3, int var4, boolean var5, int var6, long var7, long var9);

   public static void glVertexArrayVertexAttribIOffsetEXT(int vaobj, int buffer, int index, int size, int type, int stride, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexArrayVertexAttribIOffsetEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexArrayVertexAttribIOffsetEXT(vaobj, buffer, index, size, type, stride, offset, function_pointer);
   }

   static native void nglVertexArrayVertexAttribIOffsetEXT(int var0, int var1, int var2, int var3, int var4, int var5, long var6, long var8);

   public static void glEnableVertexArrayEXT(int vaobj, int array) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableVertexArrayEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableVertexArrayEXT(vaobj, array, function_pointer);
   }

   static native void nglEnableVertexArrayEXT(int var0, int var1, long var2);

   public static void glDisableVertexArrayEXT(int vaobj, int array) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableVertexArrayEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableVertexArrayEXT(vaobj, array, function_pointer);
   }

   static native void nglDisableVertexArrayEXT(int var0, int var1, long var2);

   public static void glEnableVertexArrayAttribEXT(int vaobj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableVertexArrayAttribEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableVertexArrayAttribEXT(vaobj, index, function_pointer);
   }

   static native void nglEnableVertexArrayAttribEXT(int var0, int var1, long var2);

   public static void glDisableVertexArrayAttribEXT(int vaobj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableVertexArrayAttribEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableVertexArrayAttribEXT(vaobj, index, function_pointer);
   }

   static native void nglDisableVertexArrayAttribEXT(int var0, int var1, long var2);

   public static void glGetVertexArrayIntegerEXT(int vaobj, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayIntegervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 16);
      nglGetVertexArrayIntegervEXT(vaobj, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglGetVertexArrayIntegervEXT(int var0, int var1, long var2, long var4);

   public static int glGetVertexArrayIntegerEXT(int vaobj, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayIntegervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer param = APIUtil.getBufferInt(caps);
      nglGetVertexArrayIntegervEXT(vaobj, pname, MemoryUtil.getAddress(param), function_pointer);
      return param.get(0);
   }

   public static ByteBuffer glGetVertexArrayPointerEXT(int vaobj, int pname, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayPointervEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetVertexArrayPointervEXT(vaobj, pname, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetVertexArrayPointervEXT(int var0, int var1, long var2, long var4);

   public static void glGetVertexArrayIntegerEXT(int vaobj, int index, int pname, IntBuffer param) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayIntegeri_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)param, 16);
      nglGetVertexArrayIntegeri_vEXT(vaobj, index, pname, MemoryUtil.getAddress(param), function_pointer);
   }

   static native void nglGetVertexArrayIntegeri_vEXT(int var0, int var1, int var2, long var3, long var5);

   public static int glGetVertexArrayIntegeriEXT(int vaobj, int index, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayIntegeri_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer param = APIUtil.getBufferInt(caps);
      nglGetVertexArrayIntegeri_vEXT(vaobj, index, pname, MemoryUtil.getAddress(param), function_pointer);
      return param.get(0);
   }

   public static ByteBuffer glGetVertexArrayPointerEXT(int vaobj, int index, int pname, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexArrayPointeri_vEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetVertexArrayPointeri_vEXT(vaobj, index, pname, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetVertexArrayPointeri_vEXT(int var0, int var1, int var2, long var3, long var5);

   public static ByteBuffer glMapNamedBufferRangeEXT(int buffer, long offset, long length, int access, ByteBuffer old_buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapNamedBufferRangeEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(old_buffer != null) {
         BufferChecks.checkDirect(old_buffer);
      }

      ByteBuffer __result = nglMapNamedBufferRangeEXT(buffer, offset, length, access, old_buffer, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglMapNamedBufferRangeEXT(int var0, long var1, long var3, int var5, ByteBuffer var6, long var7);

   public static void glFlushMappedNamedBufferRangeEXT(int buffer, long offset, long length) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFlushMappedNamedBufferRangeEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFlushMappedNamedBufferRangeEXT(buffer, offset, length, function_pointer);
   }

   static native void nglFlushMappedNamedBufferRangeEXT(int var0, long var1, long var3, long var5);
}
