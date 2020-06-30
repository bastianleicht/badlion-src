package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class NVTransformFeedback {
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_NV = 35982;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_START_NV = 35972;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_SIZE_NV = 35973;
   public static final int GL_TRANSFORM_FEEDBACK_RECORD_NV = 35974;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING_NV = 35983;
   public static final int GL_INTERLEAVED_ATTRIBS_NV = 35980;
   public static final int GL_SEPARATE_ATTRIBS_NV = 35981;
   public static final int GL_PRIMITIVES_GENERATED_NV = 35975;
   public static final int GL_TRANSFORM_FEEDBACK_PRIMITIVES_WRITTEN_NV = 35976;
   public static final int GL_RASTERIZER_DISCARD_NV = 35977;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_INTERLEAVED_COMPONENTS_NV = 35978;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_ATTRIBS_NV = 35979;
   public static final int GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS_NV = 35968;
   public static final int GL_TRANSFORM_FEEDBACK_ATTRIBS_NV = 35966;
   public static final int GL_ACTIVE_VARYINGS_NV = 35969;
   public static final int GL_ACTIVE_VARYING_MAX_LENGTH_NV = 35970;
   public static final int GL_TRANSFORM_FEEDBACK_VARYINGS_NV = 35971;
   public static final int GL_TRANSFORM_FEEDBACK_BUFFER_MODE_NV = 35967;
   public static final int GL_BACK_PRIMARY_COLOR_NV = 35959;
   public static final int GL_BACK_SECONDARY_COLOR_NV = 35960;
   public static final int GL_TEXTURE_COORD_NV = 35961;
   public static final int GL_CLIP_DISTANCE_NV = 35962;
   public static final int GL_VERTEX_ID_NV = 35963;
   public static final int GL_PRIMITIVE_ID_NV = 35964;
   public static final int GL_GENERIC_ATTRIB_NV = 35965;
   public static final int GL_LAYER_NV = 36266;

   public static void glBindBufferRangeNV(int target, int index, int buffer, long offset, long size) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferRangeNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferRangeNV(target, index, buffer, offset, size, function_pointer);
   }

   static native void nglBindBufferRangeNV(int var0, int var1, int var2, long var3, long var5, long var7);

   public static void glBindBufferOffsetNV(int target, int index, int buffer, long offset) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferOffsetNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferOffsetNV(target, index, buffer, offset, function_pointer);
   }

   static native void nglBindBufferOffsetNV(int var0, int var1, int var2, long var3, long var5);

   public static void glBindBufferBaseNV(int target, int index, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBufferBaseNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBindBufferBaseNV(target, index, buffer, function_pointer);
   }

   static native void nglBindBufferBaseNV(int var0, int var1, int var2, long var3);

   public static void glTransformFeedbackAttribsNV(IntBuffer attribs, int bufferMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTransformFeedbackAttribsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)attribs, 3);
      nglTransformFeedbackAttribsNV(attribs.remaining() / 3, MemoryUtil.getAddress(attribs), bufferMode, function_pointer);
   }

   static native void nglTransformFeedbackAttribsNV(int var0, long var1, int var3, long var4);

   public static void glTransformFeedbackVaryingsNV(int program, IntBuffer locations, int bufferMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTransformFeedbackVaryingsNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(locations);
      nglTransformFeedbackVaryingsNV(program, locations.remaining(), MemoryUtil.getAddress(locations), bufferMode, function_pointer);
   }

   static native void nglTransformFeedbackVaryingsNV(int var0, int var1, long var2, int var4, long var5);

   public static void glBeginTransformFeedbackNV(int primitiveMode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginTransformFeedbackNV(primitiveMode, function_pointer);
   }

   static native void nglBeginTransformFeedbackNV(int var0, long var1);

   public static void glEndTransformFeedbackNV() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndTransformFeedbackNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndTransformFeedbackNV(function_pointer);
   }

   static native void nglEndTransformFeedbackNV(long var0);

   public static int glGetVaryingLocationNV(int program, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVaryingLocationNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetVaryingLocationNV(program, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetVaryingLocationNV(int var0, long var1, long var3);

   public static int glGetVaryingLocationNV(int program, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetVaryingLocationNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetVaryingLocationNV(program, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static void glGetActiveVaryingNV(int program, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkBuffer((IntBuffer)size, 1);
      BufferChecks.checkBuffer((IntBuffer)type, 1);
      BufferChecks.checkDirect(name);
      nglGetActiveVaryingNV(program, index, name.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(size), MemoryUtil.getAddress(type), MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglGetActiveVaryingNV(int var0, int var1, int var2, long var3, long var5, long var7, long var9, long var11);

   public static String glGetActiveVaryingNV(int program, int index, int bufSize, IntBuffer sizeType) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)sizeType, 2);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, bufSize);
      nglGetActiveVaryingNV(program, index, bufSize, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress(sizeType), MemoryUtil.getAddress(sizeType, sizeType.position() + 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static String glGetActiveVaryingNV(int program, int index, int bufSize) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, bufSize);
      nglGetActiveVaryingNV(program, index, bufSize, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress0((Buffer)APIUtil.getBufferInt(caps)), MemoryUtil.getAddress((IntBuffer)APIUtil.getBufferInt(caps), 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static int glGetActiveVaryingSizeNV(int program, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer size = APIUtil.getBufferInt(caps);
      nglGetActiveVaryingNV(program, index, 0, 0L, MemoryUtil.getAddress(size), MemoryUtil.getAddress((IntBuffer)size, 1), APIUtil.getBufferByte0(caps), function_pointer);
      return size.get(0);
   }

   public static int glGetActiveVaryingTypeNV(int program, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer type = APIUtil.getBufferInt(caps);
      nglGetActiveVaryingNV(program, index, 0, 0L, MemoryUtil.getAddress((IntBuffer)type, 1), MemoryUtil.getAddress(type), APIUtil.getBufferByte0(caps), function_pointer);
      return type.get(0);
   }

   public static void glActiveVaryingNV(int program, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      nglActiveVaryingNV(program, MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglActiveVaryingNV(int var0, long var1, long var3);

   public static void glActiveVaryingNV(int program, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glActiveVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglActiveVaryingNV(program, APIUtil.getBufferNT(caps, name), function_pointer);
   }

   public static void glGetTransformFeedbackVaryingNV(int program, int index, IntBuffer location) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTransformFeedbackVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)location, 1);
      nglGetTransformFeedbackVaryingNV(program, index, MemoryUtil.getAddress(location), function_pointer);
   }

   static native void nglGetTransformFeedbackVaryingNV(int var0, int var1, long var2, long var4);

   public static int glGetTransformFeedbackVaryingNV(int program, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetTransformFeedbackVaryingNV;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer location = APIUtil.getBufferInt(caps);
      nglGetTransformFeedbackVaryingNV(program, index, MemoryUtil.getAddress(location), function_pointer);
      return location.get(0);
   }
}
