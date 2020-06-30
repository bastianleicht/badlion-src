package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;

public final class AMDMultiDrawIndirect {
   public static void glMultiDrawArraysIndirectAMD(int mode, ByteBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?16:stride) * primcount);
      nglMultiDrawArraysIndirectAMD(mode, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirectAMD(int var0, long var1, int var3, int var4, long var5);

   public static void glMultiDrawArraysIndirectAMD(int mode, long indirect_buffer_offset, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawArraysIndirectAMDBO(mode, indirect_buffer_offset, primcount, stride, function_pointer);
   }

   static native void nglMultiDrawArraysIndirectAMDBO(int var0, long var1, int var3, int var4, long var5);

   public static void glMultiDrawArraysIndirectAMD(int mode, IntBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawArraysIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?4:stride >> 2) * primcount);
      nglMultiDrawArraysIndirectAMD(mode, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   public static void glMultiDrawElementsIndirectAMD(int mode, int type, ByteBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?20:stride) * primcount);
      nglMultiDrawElementsIndirectAMD(mode, type, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirectAMD(int var0, int var1, long var2, int var4, int var5, long var6);

   public static void glMultiDrawElementsIndirectAMD(int mode, int type, long indirect_buffer_offset, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOenabled(caps);
      nglMultiDrawElementsIndirectAMDBO(mode, type, indirect_buffer_offset, primcount, stride, function_pointer);
   }

   static native void nglMultiDrawElementsIndirectAMDBO(int var0, int var1, long var2, int var4, int var5, long var6);

   public static void glMultiDrawElementsIndirectAMD(int mode, int type, IntBuffer indirect, int primcount, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMultiDrawElementsIndirectAMD;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureIndirectBOdisabled(caps);
      BufferChecks.checkBuffer(indirect, (stride == 0?5:stride >> 2) * primcount);
      nglMultiDrawElementsIndirectAMD(mode, type, MemoryUtil.getAddress(indirect), primcount, stride, function_pointer);
   }
}
