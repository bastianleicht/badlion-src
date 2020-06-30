package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class GL44 {
   public static final int GL_MAX_VERTEX_ATTRIB_STRIDE = 33509;
   public static final int GL_MAP_PERSISTENT_BIT = 64;
   public static final int GL_MAP_COHERENT_BIT = 128;
   public static final int GL_DYNAMIC_STORAGE_BIT = 256;
   public static final int GL_CLIENT_STORAGE_BIT = 512;
   public static final int GL_BUFFER_IMMUTABLE_STORAGE = 33311;
   public static final int GL_BUFFER_STORAGE_FLAGS = 33312;
   public static final int GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT = 16384;
   public static final int GL_CLEAR_TEXTURE = 37733;
   public static final int GL_LOCATION_COMPONENT = 37706;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_INDEX = 37707;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_STRIDE = 37708;
   public static final int GL_QUERY_RESULT_NO_WAIT = 37268;
   public static final int GL_QUERY_BUFFER = 37266;
   public static final int GL_QUERY_BUFFER_BINDING = 37267;
   public static final int GL_QUERY_BUFFER_BARRIER_BIT = 32768;
   public static final int GL_MIRROR_CLAMP_TO_EDGE = 34627;

   public static void glBufferStorage(int target, ByteBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)data.remaining(), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glBufferStorage(int target, DoubleBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glBufferStorage(int target, FloatBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glBufferStorage(int target, IntBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glBufferStorage(int target, ShortBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glBufferStorage(int target, LongBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferStorage(target, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   static native void nglBufferStorage(int var0, long var1, long var3, int var5, long var6);

   public static void glBufferStorage(int target, long size, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferStorage;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBufferStorage(target, size, 0L, flags, function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((ByteBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((DoubleBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((FloatBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((IntBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((ShortBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexImage(int texture, int level, int format, int type, LongBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((LongBuffer)data, 1);
      }

      nglClearTexImage(texture, level, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   static native void nglClearTexImage(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((ByteBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((DoubleBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((FloatBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((IntBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((ShortBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   public static void glClearTexSubImage(int texture, int level, int xoffset, int yoffset, int zoffset, int width, int height, int depth, int format, int type, LongBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearTexSubImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(data != null) {
         BufferChecks.checkBuffer((LongBuffer)data, 1);
      }

      nglClearTexSubImage(texture, level, xoffset, yoffset, zoffset, width, height, depth, format, type, MemoryUtil.getAddressSafe(data), function_pointer);
   }

   static native void nglClearTexSubImage(int var0, int var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8, int var9, long var10, long var12);

   public static void glBindBuffersBase(int target, int first, int count, IntBuffer buffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBuffersBase;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(buffers != null) {
         BufferChecks.checkBuffer(buffers, count);
      }

      nglBindBuffersBase(target, first, count, MemoryUtil.getAddressSafe(buffers), function_pointer);
   }

   static native void nglBindBuffersBase(int var0, int var1, int var2, long var3, long var5);

   public static void glBindBuffersRange(int target, int first, int count, IntBuffer buffers, PointerBuffer offsets, PointerBuffer sizes) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBuffersRange;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(buffers != null) {
         BufferChecks.checkBuffer(buffers, count);
      }

      if(offsets != null) {
         BufferChecks.checkBuffer(offsets, count);
      }

      if(sizes != null) {
         BufferChecks.checkBuffer(sizes, count);
      }

      nglBindBuffersRange(target, first, count, MemoryUtil.getAddressSafe(buffers), MemoryUtil.getAddressSafe(offsets), MemoryUtil.getAddressSafe(sizes), function_pointer);
   }

   static native void nglBindBuffersRange(int var0, int var1, int var2, long var3, long var5, long var7, long var9);

   public static void glBindTextures(int first, int count, IntBuffer textures) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(textures != null) {
         BufferChecks.checkBuffer(textures, count);
      }

      nglBindTextures(first, count, MemoryUtil.getAddressSafe(textures), function_pointer);
   }

   static native void nglBindTextures(int var0, int var1, long var2, long var4);

   public static void glBindSamplers(int first, int count, IntBuffer samplers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindSamplers;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(samplers != null) {
         BufferChecks.checkBuffer(samplers, count);
      }

      nglBindSamplers(first, count, MemoryUtil.getAddressSafe(samplers), function_pointer);
   }

   static native void nglBindSamplers(int var0, int var1, long var2, long var4);

   public static void glBindImageTextures(int first, int count, IntBuffer textures) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindImageTextures;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(textures != null) {
         BufferChecks.checkBuffer(textures, count);
      }

      nglBindImageTextures(first, count, MemoryUtil.getAddressSafe(textures), function_pointer);
   }

   static native void nglBindImageTextures(int var0, int var1, long var2, long var4);

   public static void glBindVertexBuffers(int first, int count, IntBuffer buffers, PointerBuffer offsets, IntBuffer strides) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindVertexBuffers;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(buffers != null) {
         BufferChecks.checkBuffer(buffers, count);
      }

      if(offsets != null) {
         BufferChecks.checkBuffer(offsets, count);
      }

      if(strides != null) {
         BufferChecks.checkBuffer(strides, count);
      }

      nglBindVertexBuffers(first, count, MemoryUtil.getAddressSafe(buffers), MemoryUtil.getAddressSafe(offsets), MemoryUtil.getAddressSafe(strides), function_pointer);
   }

   static native void nglBindVertexBuffers(int var0, int var1, long var2, long var4, long var6, long var8);
}
