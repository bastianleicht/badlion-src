package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class ARBDrawInstanced {
   public static void glDrawArraysInstancedARB(int mode, int first, int count, int primcount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawArraysInstancedARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDrawArraysInstancedARB(mode, first, count, primcount, function_pointer);
   }

   static native void nglDrawArraysInstancedARB(int var0, int var1, int var2, int var3, long var4);

   public static void glDrawElementsInstancedARB(int mode, ByteBuffer indices, int primcount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedARB(mode, indices.remaining(), 5121, MemoryUtil.getAddress(indices), primcount, function_pointer);
   }

   public static void glDrawElementsInstancedARB(int mode, IntBuffer indices, int primcount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedARB(mode, indices.remaining(), 5125, MemoryUtil.getAddress(indices), primcount, function_pointer);
   }

   public static void glDrawElementsInstancedARB(int mode, ShortBuffer indices, int primcount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedARB(mode, indices.remaining(), 5123, MemoryUtil.getAddress(indices), primcount, function_pointer);
   }

   static native void nglDrawElementsInstancedARB(int var0, int var1, int var2, long var3, int var5, long var6);

   public static void glDrawElementsInstancedARB(int mode, int indices_count, int type, long indices_buffer_offset, int primcount) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawElementsInstancedARBBO(mode, indices_count, type, indices_buffer_offset, primcount, function_pointer);
   }

   static native void nglDrawElementsInstancedARBBO(int var0, int var1, int var2, long var3, int var5, long var6);
}
