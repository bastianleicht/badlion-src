package org.lwjgl.opengl;

import java.nio.Buffer;
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
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class ARBVertexShader {
   public static final int GL_VERTEX_SHADER_ARB = 35633;
   public static final int GL_MAX_VERTEX_UNIFORM_COMPONENTS_ARB = 35658;
   public static final int GL_MAX_VARYING_FLOATS_ARB = 35659;
   public static final int GL_MAX_VERTEX_ATTRIBS_ARB = 34921;
   public static final int GL_MAX_TEXTURE_IMAGE_UNITS_ARB = 34930;
   public static final int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS_ARB = 35660;
   public static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS_ARB = 35661;
   public static final int GL_MAX_TEXTURE_COORDS_ARB = 34929;
   public static final int GL_VERTEX_PROGRAM_POINT_SIZE_ARB = 34370;
   public static final int GL_VERTEX_PROGRAM_TWO_SIDE_ARB = 34371;
   public static final int GL_OBJECT_ACTIVE_ATTRIBUTES_ARB = 35721;
   public static final int GL_OBJECT_ACTIVE_ATTRIBUTE_MAX_LENGTH_ARB = 35722;
   public static final int GL_VERTEX_ATTRIB_ARRAY_ENABLED_ARB = 34338;
   public static final int GL_VERTEX_ATTRIB_ARRAY_SIZE_ARB = 34339;
   public static final int GL_VERTEX_ATTRIB_ARRAY_STRIDE_ARB = 34340;
   public static final int GL_VERTEX_ATTRIB_ARRAY_TYPE_ARB = 34341;
   public static final int GL_VERTEX_ATTRIB_ARRAY_NORMALIZED_ARB = 34922;
   public static final int GL_CURRENT_VERTEX_ATTRIB_ARB = 34342;
   public static final int GL_VERTEX_ATTRIB_ARRAY_POINTER_ARB = 34373;
   public static final int GL_FLOAT_VEC2_ARB = 35664;
   public static final int GL_FLOAT_VEC3_ARB = 35665;
   public static final int GL_FLOAT_VEC4_ARB = 35666;
   public static final int GL_FLOAT_MAT2_ARB = 35674;
   public static final int GL_FLOAT_MAT3_ARB = 35675;
   public static final int GL_FLOAT_MAT4_ARB = 35676;

   public static void glVertexAttrib1sARB(int index, short v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib1sARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib1sARB(index, v0, function_pointer);
   }

   static native void nglVertexAttrib1sARB(int var0, short var1, long var2);

   public static void glVertexAttrib1fARB(int index, float v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib1fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib1fARB(index, v0, function_pointer);
   }

   static native void nglVertexAttrib1fARB(int var0, float var1, long var2);

   public static void glVertexAttrib1dARB(int index, double v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib1dARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib1dARB(index, v0, function_pointer);
   }

   static native void nglVertexAttrib1dARB(int var0, double var1, long var3);

   public static void glVertexAttrib2sARB(int index, short v0, short v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib2sARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib2sARB(index, v0, v1, function_pointer);
   }

   static native void nglVertexAttrib2sARB(int var0, short var1, short var2, long var3);

   public static void glVertexAttrib2fARB(int index, float v0, float v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib2fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib2fARB(index, v0, v1, function_pointer);
   }

   static native void nglVertexAttrib2fARB(int var0, float var1, float var2, long var3);

   public static void glVertexAttrib2dARB(int index, double v0, double v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib2dARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib2dARB(index, v0, v1, function_pointer);
   }

   static native void nglVertexAttrib2dARB(int var0, double var1, double var3, long var5);

   public static void glVertexAttrib3sARB(int index, short v0, short v1, short v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib3sARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib3sARB(index, v0, v1, v2, function_pointer);
   }

   static native void nglVertexAttrib3sARB(int var0, short var1, short var2, short var3, long var4);

   public static void glVertexAttrib3fARB(int index, float v0, float v1, float v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib3fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib3fARB(index, v0, v1, v2, function_pointer);
   }

   static native void nglVertexAttrib3fARB(int var0, float var1, float var2, float var3, long var4);

   public static void glVertexAttrib3dARB(int index, double v0, double v1, double v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib3dARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib3dARB(index, v0, v1, v2, function_pointer);
   }

   static native void nglVertexAttrib3dARB(int var0, double var1, double var3, double var5, long var7);

   public static void glVertexAttrib4sARB(int index, short v0, short v1, short v2, short v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib4sARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib4sARB(index, v0, v1, v2, v3, function_pointer);
   }

   static native void nglVertexAttrib4sARB(int var0, short var1, short var2, short var3, short var4, long var5);

   public static void glVertexAttrib4fARB(int index, float v0, float v1, float v2, float v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib4fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib4fARB(index, v0, v1, v2, v3, function_pointer);
   }

   static native void nglVertexAttrib4fARB(int var0, float var1, float var2, float var3, float var4, long var5);

   public static void glVertexAttrib4dARB(int index, double v0, double v1, double v2, double v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib4dARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib4dARB(index, v0, v1, v2, v3, function_pointer);
   }

   static native void nglVertexAttrib4dARB(int var0, double var1, double var3, double var5, double var7, long var9);

   public static void glVertexAttrib4NubARB(int index, byte x, byte y, byte z, byte w) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttrib4NubARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglVertexAttrib4NubARB(index, x, y, z, w, function_pointer);
   }

   static native void nglVertexAttrib4NubARB(int var0, byte var1, byte var2, byte var3, byte var4, long var5);

   public static void glVertexAttribPointerARB(int index, int size, boolean normalized, int stride, DoubleBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, 5130, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   public static void glVertexAttribPointerARB(int index, int size, boolean normalized, int stride, FloatBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, 5126, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, ByteBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, unsigned?5121:5120, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, IntBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, unsigned?5125:5124, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   public static void glVertexAttribPointerARB(int index, int size, boolean unsigned, boolean normalized, int stride, ShortBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, unsigned?5123:5122, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   static native void nglVertexAttribPointerARB(int var0, int var1, int var2, boolean var3, int var4, long var5, long var7);

   public static void glVertexAttribPointerARB(int index, int size, int type, boolean normalized, int stride, long buffer_buffer_offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOenabled(caps);
      nglVertexAttribPointerARBBO(index, size, type, normalized, stride, buffer_buffer_offset, function_pointer);
   }

   static native void nglVertexAttribPointerARBBO(int var0, int var1, int var2, boolean var3, int var4, long var5, long var7);

   public static void glVertexAttribPointerARB(int index, int size, int type, boolean normalized, int stride, ByteBuffer buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glVertexAttribPointerARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureArrayVBOdisabled(caps);
      BufferChecks.checkDirect(buffer);
      if(LWJGLUtil.CHECKS) {
         StateTracker.getReferences(caps).glVertexAttribPointer_buffer[index] = buffer;
      }

      nglVertexAttribPointerARB(index, size, type, normalized, stride, MemoryUtil.getAddress(buffer), function_pointer);
   }

   public static void glEnableVertexAttribArrayARB(int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEnableVertexAttribArrayARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEnableVertexAttribArrayARB(index, function_pointer);
   }

   static native void nglEnableVertexAttribArrayARB(int var0, long var1);

   public static void glDisableVertexAttribArrayARB(int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDisableVertexAttribArrayARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDisableVertexAttribArrayARB(index, function_pointer);
   }

   static native void nglDisableVertexAttribArrayARB(int var0, long var1);

   public static void glBindAttribLocationARB(int programObj, int index, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindAttribLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      nglBindAttribLocationARB(programObj, index, MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglBindAttribLocationARB(int var0, int var1, long var2, long var4);

   public static void glBindAttribLocationARB(int programObj, int index, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindAttribLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindAttribLocationARB(programObj, index, APIUtil.getBufferNT(caps, name), function_pointer);
   }

   public static void glGetActiveAttribARB(int programObj, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveAttribARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkBuffer((IntBuffer)size, 1);
      BufferChecks.checkBuffer((IntBuffer)type, 1);
      BufferChecks.checkDirect(name);
      nglGetActiveAttribARB(programObj, index, name.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(size), MemoryUtil.getAddress(type), MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglGetActiveAttribARB(int var0, int var1, int var2, long var3, long var5, long var7, long var9, long var11);

   public static String glGetActiveAttribARB(int programObj, int index, int maxLength, IntBuffer sizeType) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveAttribARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)sizeType, 2);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, maxLength);
      nglGetActiveAttribARB(programObj, index, maxLength, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress(sizeType), MemoryUtil.getAddress(sizeType, sizeType.position() + 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static String glGetActiveAttribARB(int programObj, int index, int maxLength) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveAttribARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, maxLength);
      nglGetActiveAttribARB(programObj, index, maxLength, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress0((Buffer)APIUtil.getBufferInt(caps)), MemoryUtil.getAddress((IntBuffer)APIUtil.getBufferInt(caps), 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static int glGetActiveAttribSizeARB(int programObj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveAttribARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer size = APIUtil.getBufferInt(caps);
      nglGetActiveAttribARB(programObj, index, 0, 0L, MemoryUtil.getAddress(size), MemoryUtil.getAddress((IntBuffer)size, 1), APIUtil.getBufferByte0(caps), function_pointer);
      return size.get(0);
   }

   public static int glGetActiveAttribTypeARB(int programObj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveAttribARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer type = APIUtil.getBufferInt(caps);
      nglGetActiveAttribARB(programObj, index, 0, 0L, MemoryUtil.getAddress((IntBuffer)type, 1), MemoryUtil.getAddress(type), APIUtil.getBufferByte0(caps), function_pointer);
      return type.get(0);
   }

   public static int glGetAttribLocationARB(int programObj, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetAttribLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetAttribLocationARB(programObj, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetAttribLocationARB(int var0, long var1, long var3);

   public static int glGetAttribLocationARB(int programObj, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetAttribLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetAttribLocationARB(programObj, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static void glGetVertexAttribARB(int index, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribfvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)params, 4);
      nglGetVertexAttribfvARB(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribfvARB(int var0, int var1, long var2, long var4);

   public static void glGetVertexAttribARB(int index, int pname, DoubleBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribdvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((DoubleBuffer)params, 4);
      nglGetVertexAttribdvARB(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribdvARB(int var0, int var1, long var2, long var4);

   public static void glGetVertexAttribARB(int index, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetVertexAttribivARB(index, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetVertexAttribivARB(int var0, int var1, long var2, long var4);

   public static ByteBuffer glGetVertexAttribPointerARB(int index, int pname, long result_size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVertexAttribPointervARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetVertexAttribPointervARB(index, pname, result_size, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetVertexAttribPointervARB(int var0, int var1, long var2, long var4);
}
