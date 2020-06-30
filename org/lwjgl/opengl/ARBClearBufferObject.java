package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLContext;

public final class ARBClearBufferObject {
   public static void glClearBufferData(int target, int internalformat, int format, int type, ByteBuffer data) {
      GL43.glClearBufferData(target, internalformat, format, type, data);
   }

   public static void glClearBufferSubData(int target, int internalformat, long offset, long size, int format, int type, ByteBuffer data) {
      GL43.glClearBufferSubData(target, internalformat, offset, size, format, type, data);
   }

   public static void glClearNamedBufferDataEXT(int buffer, int internalformat, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearNamedBufferDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)data, 1);
      nglClearNamedBufferDataEXT(buffer, internalformat, format, type, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglClearNamedBufferDataEXT(int var0, int var1, int var2, int var3, long var4, long var6);

   public static void glClearNamedBufferSubDataEXT(int buffer, int internalformat, long offset, long size, int format, int type, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClearNamedBufferSubDataEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)data, 1);
      nglClearNamedBufferSubDataEXT(buffer, internalformat, offset, size, format, type, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglClearNamedBufferSubDataEXT(int var0, int var1, long var2, long var4, int var6, int var7, long var8, long var10);
}
