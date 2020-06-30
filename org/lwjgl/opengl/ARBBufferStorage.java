package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL44;
import org.lwjgl.opengl.GLContext;

public final class ARBBufferStorage {
   public static final int GL_MAP_PERSISTENT_BIT = 64;
   public static final int GL_MAP_COHERENT_BIT = 128;
   public static final int GL_DYNAMIC_STORAGE_BIT = 256;
   public static final int GL_CLIENT_STORAGE_BIT = 512;
   public static final int GL_BUFFER_IMMUTABLE_STORAGE = 33311;
   public static final int GL_BUFFER_STORAGE_FLAGS = 33312;
   public static final int GL_CLIENT_MAPPED_BUFFER_BARRIER_BIT = 16384;

   public static void glBufferStorage(int target, ByteBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, DoubleBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, FloatBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, IntBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, ShortBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, LongBuffer data, int flags) {
      GL44.glBufferStorage(target, data, flags);
   }

   public static void glBufferStorage(int target, long size, int flags) {
      GL44.glBufferStorage(target, size, flags);
   }

   public static void glNamedBufferStorageEXT(int buffer, ByteBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)data.remaining(), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glNamedBufferStorageEXT(int buffer, DoubleBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glNamedBufferStorageEXT(int buffer, FloatBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glNamedBufferStorageEXT(int buffer, IntBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glNamedBufferStorageEXT(int buffer, ShortBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   public static void glNamedBufferStorageEXT(int buffer, LongBuffer data, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglNamedBufferStorageEXT(buffer, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), flags, function_pointer);
   }

   static native void nglNamedBufferStorageEXT(int var0, long var1, long var3, int var5, long var6);

   public static void glNamedBufferStorageEXT(int buffer, long size, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNamedBufferStorageEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNamedBufferStorageEXT(buffer, size, 0L, flags, function_pointer);
   }
}
