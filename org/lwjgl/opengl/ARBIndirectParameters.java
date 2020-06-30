package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class ARBIndirectParameters {
   public static final int GL_PARAMETER_BUFFER_ARB = 33006;
   public static final int GL_PARAMETER_BUFFER_BINDING_ARB = 33007;

   public static void glMultiDrawArraysIndirectCountARB(int mode, ByteBuffer indirect, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?16:stride) * maxdrawcount);
      nglMultiDrawArraysIndirectCountARB(mode, MemoryUtil.getAddress(indirect), drawcount, maxdrawcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirectCountARB(int var0, long var1, long var3, int var5, int var6, long var7);

   public static void glMultiDrawArraysIndirectCountARB(int mode, long indirect_buffer_offset, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawArraysIndirectCountARBBO(mode, indirect_buffer_offset, drawcount, maxdrawcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirectCountARBBO(int var0, long var1, long var3, int var5, int var6, long var7);

   public static void glMultiDrawArraysIndirectCountARB(int mode, IntBuffer indirect, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?4:stride >> 2) * maxdrawcount);
      nglMultiDrawArraysIndirectCountARB(mode, MemoryUtil.getAddress(indirect), drawcount, maxdrawcount, stride, function_pointer);
   }

   public static void glMultiDrawElementsIndirectCountARB(int mode, int type, ByteBuffer indirect, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?20:stride) * maxdrawcount);
      nglMultiDrawElementsIndirectCountARB(mode, type, MemoryUtil.getAddress(indirect), drawcount, maxdrawcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirectCountARB(int var0, int var1, long var2, long var4, int var6, int var7, long var8);

   public static void glMultiDrawElementsIndirectCountARB(int mode, int type, long indirect_buffer_offset, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawElementsIndirectCountARBBO(mode, type, indirect_buffer_offset, drawcount, maxdrawcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirectCountARBBO(int var0, int var1, long var2, long var4, int var6, int var7, long var8);

   public static void glMultiDrawElementsIndirectCountARB(int mode, int type, IntBuffer indirect, long drawcount, int maxdrawcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectCountARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?5:stride >> 2) * maxdrawcount);
      nglMultiDrawElementsIndirectCountARB(mode, type, MemoryUtil.getAddress(indirect), drawcount, maxdrawcount, stride, function_pointer);
   }
}
