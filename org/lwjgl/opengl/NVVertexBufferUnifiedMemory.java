package org.lwjgl.opengl;

import java.nio.LongBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVVertexBufferUnifiedMemory {
   public static final int GL_VERTEX_ATTRIB_ARRAY_UNIFIED_NV = 36638;
   public static final int GL_ELEMENT_ARRAY_UNIFIED_NV = 36639;
   public static final int GL_VERTEX_ATTRIB_ARRAY_ADDRESS_NV = 36640;
   public static final int GL_TEXTURE_COORD_ARRAY_ADDRESS_NV = 36645;
   public static final int GL_VERTEX_ARRAY_ADDRESS_NV = 36641;
   public static final int GL_NORMAL_ARRAY_ADDRESS_NV = 36642;
   public static final int GL_COLOR_ARRAY_ADDRESS_NV = 36643;
   public static final int GL_INDEX_ARRAY_ADDRESS_NV = 36644;
   public static final int GL_EDGE_FLAG_ARRAY_ADDRESS_NV = 36646;
   public static final int GL_SECONDARY_COLOR_ARRAY_ADDRESS_NV = 36647;
   public static final int GL_FOG_COORD_ARRAY_ADDRESS_NV = 36648;
   public static final int GL_ELEMENT_ARRAY_ADDRESS_NV = 36649;
   public static final int GL_VERTEX_ATTRIB_ARRAY_LENGTH_NV = 36650;
   public static final int GL_TEXTURE_COORD_ARRAY_LENGTH_NV = 36655;
   public static final int GL_VERTEX_ARRAY_LENGTH_NV = 36651;
   public static final int GL_NORMAL_ARRAY_LENGTH_NV = 36652;
   public static final int GL_COLOR_ARRAY_LENGTH_NV = 36653;
   public static final int GL_INDEX_ARRAY_LENGTH_NV = 36654;
   public static final int GL_EDGE_FLAG_ARRAY_LENGTH_NV = 36656;
   public static final int GL_SECONDARY_COLOR_ARRAY_LENGTH_NV = 36657;
   public static final int GL_FOG_COORD_ARRAY_LENGTH_NV = 36658;
   public static final int GL_ELEMENT_ARRAY_LENGTH_NV = 36659;

   public static void glBufferAddressRangeNV(int pname, int index, long address, long length) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferAddressRangeNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBufferAddressRangeNV(pname, index, address, length, function_pointer);
   }

   static native void nglBufferAddressRangeNV(int var0, int var1, long var2, long var4, long var6);

   public static void glVertexFormatNV(int size, int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexFormatNV(size, type, stride, function_pointer);
   }

   static native void nglVertexFormatNV(int var0, int var1, int var2, long var3);

   public static void glNormalFormatNV(int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glNormalFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglNormalFormatNV(type, stride, function_pointer);
   }

   static native void nglNormalFormatNV(int var0, int var1, long var2);

   public static void glColorFormatNV(int size, int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glColorFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglColorFormatNV(size, type, stride, function_pointer);
   }

   static native void nglColorFormatNV(int var0, int var1, int var2, long var3);

   public static void glIndexFormatNV(int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIndexFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglIndexFormatNV(type, stride, function_pointer);
   }

   static native void nglIndexFormatNV(int var0, int var1, long var2);

   public static void glTexCoordFormatNV(int size, int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexCoordFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexCoordFormatNV(size, type, stride, function_pointer);
   }

   static native void nglTexCoordFormatNV(int var0, int var1, int var2, long var3);

   public static void glEdgeFlagFormatNV(int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEdgeFlagFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEdgeFlagFormatNV(stride, function_pointer);
   }

   static native void nglEdgeFlagFormatNV(int var0, long var1);

   public static void glSecondaryColorFormatNV(int size, int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSecondaryColorFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSecondaryColorFormatNV(size, type, stride, function_pointer);
   }

   static native void nglSecondaryColorFormatNV(int var0, int var1, int var2, long var3);

   public static void glFogCoordFormatNV(int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFogCoordFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFogCoordFormatNV(type, stride, function_pointer);
   }

   static native void nglFogCoordFormatNV(int var0, int var1, long var2);

   public static void glVertexAttribFormatNV(int index, int size, int type, boolean normalized, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribFormatNV(index, size, type, normalized, stride, function_pointer);
   }

   static native void nglVertexAttribFormatNV(int var0, int var1, int var2, boolean var3, int var4, long var5);

   public static void glVertexAttribIFormatNV(int index, int size, int type, int stride) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribIFormatNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttribIFormatNV(index, size, type, stride, function_pointer);
   }

   static native void nglVertexAttribIFormatNV(int var0, int var1, int var2, int var3, long var4);

   public static void glGetIntegeruNV(int value, int index, LongBuffer result) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerui64i_vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)result, 1);
      nglGetIntegerui64i_vNV(value, index, MemoryUtil.getAddress(result), function_pointer);
   }

   static native void nglGetIntegerui64i_vNV(int var0, int var1, long var2, long var4);

   public static long glGetIntegerui64NV(int value, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetIntegerui64i_vNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer result = APIUtil.getBufferLong(caps);
      nglGetIntegerui64i_vNV(value, index, MemoryUtil.getAddress(result), function_pointer);
      return result.get(0);
   }
}
