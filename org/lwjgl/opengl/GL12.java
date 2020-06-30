package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class GL12 {
   public static final int GL_TEXTURE_BINDING_3D = 32874;
   public static final int GL_PACK_SKIP_IMAGES = 32875;
   public static final int GL_PACK_IMAGE_HEIGHT = 32876;
   public static final int GL_UNPACK_SKIP_IMAGES = 32877;
   public static final int GL_UNPACK_IMAGE_HEIGHT = 32878;
   public static final int GL_TEXTURE_3D = 32879;
   public static final int GL_PROXY_TEXTURE_3D = 32880;
   public static final int GL_TEXTURE_DEPTH = 32881;
   public static final int GL_TEXTURE_WRAP_R = 32882;
   public static final int GL_MAX_3D_TEXTURE_SIZE = 32883;
   public static final int GL_BGR = 32992;
   public static final int GL_BGRA = 32993;
   public static final int GL_UNSIGNED_BYTE_3_3_2 = 32818;
   public static final int GL_UNSIGNED_BYTE_2_3_3_REV = 33634;
   public static final int GL_UNSIGNED_SHORT_5_6_5 = 33635;
   public static final int GL_UNSIGNED_SHORT_5_6_5_REV = 33636;
   public static final int GL_UNSIGNED_SHORT_4_4_4_4 = 32819;
   public static final int GL_UNSIGNED_SHORT_4_4_4_4_REV = 33637;
   public static final int GL_UNSIGNED_SHORT_5_5_5_1 = 32820;
   public static final int GL_UNSIGNED_SHORT_1_5_5_5_REV = 33638;
   public static final int GL_UNSIGNED_INT_8_8_8_8 = 32821;
   public static final int GL_UNSIGNED_INT_8_8_8_8_REV = 33639;
   public static final int GL_UNSIGNED_INT_10_10_10_2 = 32822;
   public static final int GL_UNSIGNED_INT_2_10_10_10_REV = 33640;
   public static final int GL_RESCALE_NORMAL = 32826;
   public static final int GL_LIGHT_MODEL_COLOR_CONTROL = 33272;
   public static final int GL_SINGLE_COLOR = 33273;
   public static final int GL_SEPARATE_SPECULAR_COLOR = 33274;
   public static final int GL_CLAMP_TO_EDGE = 33071;
   public static final int GL_TEXTURE_MIN_LOD = 33082;
   public static final int GL_TEXTURE_MAX_LOD = 33083;
   public static final int GL_TEXTURE_BASE_LEVEL = 33084;
   public static final int GL_TEXTURE_MAX_LEVEL = 33085;
   public static final int GL_MAX_ELEMENTS_VERTICES = 33000;
   public static final int GL_MAX_ELEMENTS_INDICES = 33001;
   public static final int GL_ALIASED_POINT_SIZE_RANGE = 33901;
   public static final int GL_ALIASED_LINE_WIDTH_RANGE = 33902;
   public static final int GL_SMOOTH_POINT_SIZE_RANGE = 2834;
   public static final int GL_SMOOTH_POINT_SIZE_GRANULARITY = 2835;
   public static final int GL_SMOOTH_LINE_WIDTH_RANGE = 2850;
   public static final int GL_SMOOTH_LINE_WIDTH_GRANULARITY = 2851;

   public static void glDrawRangeElements(int mode, int start, int end, ByteBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElements(mode, start, end, indices.remaining(), 5121, MemoryUtil.getAddress(indices), function_pointer);
   }

   public static void glDrawRangeElements(int mode, int start, int end, IntBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElements(mode, start, end, indices.remaining(), 5125, MemoryUtil.getAddress(indices), function_pointer);
   }

   public static void glDrawRangeElements(int mode, int start, int end, ShortBuffer indices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElements(mode, start, end, indices.remaining(), 5123, MemoryUtil.getAddress(indices), function_pointer);
   }

   static native void nglDrawRangeElements(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glDrawRangeElements(int mode, int start, int end, int indices_count, int type, long indices_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElements;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawRangeElementsBO(mode, start, end, indices_count, type, indices_buffer_offset, function_pointer);
   }

   static native void nglDrawRangeElementsBO(int var0, int var1, int var2, int var3, int var4, long var5, long var7);

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      if(pixels != null) {
         BufferChecks.checkBuffer(pixels, GLChecks.calculateTexImage3DStorage(pixels, format, type, width, height, depth));
      }

      nglTexImage3D(target, level, internalFormat, width, height, depth, border, format, type, MemoryUtil.getAddressSafe(pixels), function_pointer);
   }

   static native void nglTexImage3D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glTexImage3D(int target, int level, int internalFormat, int width, int height, int depth, int border, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexImage3DBO(target, level, internalFormat, width, height, depth, border, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexImage3DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9, long var11);

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, DoubleBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, FloatBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, IntBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ShortBuffer pixels) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOdisabled(caps);
      BufferChecks.checkBuffer(pixels, GLChecks.calculateImageStorage(pixels, format, type, width, height, depth));
      nglTexSubImage3D(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddress(pixels), function_pointer);
   }

   static native void nglTexSubImage3D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, long pixels_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureUnpackPBOenabled(caps);
      nglTexSubImage3DBO(target, level, xoffset, yoffset, zoffset, width, height, depth, format, type, pixels_buffer_offset, function_pointer);
   }

   static native void nglTexSubImage3DBO(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glCopyTexSubImage3D(int target, int level, int xoffset, int yoffset, int zoffset, int x, int y, int width, int height) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCopyTexSubImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCopyTexSubImage3D(target, level, xoffset, yoffset, zoffset, x, y, width, height, function_pointer);
   }

   static native void nglCopyTexSubImage3D(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, long var9);
}
