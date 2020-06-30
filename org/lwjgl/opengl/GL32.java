package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLChecks;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLSync;

public final class GL32 {
   public static final int GL_CONTEXT_PROFILE_MASK = 37158;
   public static final int GL_CONTEXT_CORE_PROFILE_BIT = 1;
   public static final int GL_CONTEXT_COMPATIBILITY_PROFILE_BIT = 2;
   public static final int GL_MAX_VERTEX_OUTPUT_COMPONENTS = 37154;
   public static final int GL_MAX_GEOMETRY_INPUT_COMPONENTS = 37155;
   public static final int GL_MAX_GEOMETRY_OUTPUT_COMPONENTS = 37156;
   public static final int GL_MAX_FRAGMENT_INPUT_COMPONENTS = 37157;
   public static final int GL_FIRST_VERTEX_CONVENTION = 36429;
   public static final int GL_LAST_VERTEX_CONVENTION = 36430;
   public static final int GL_PROVOKING_VERTEX = 36431;
   public static final int GL_QUADS_FOLLOW_PROVOKING_VERTEX_CONVENTION = 36428;
   public static final int GL_TEXTURE_CUBE_MAP_SEAMLESS = 34895;
   public static final int GL_SAMPLE_POSITION = 36432;
   public static final int GL_SAMPLE_MASK = 36433;
   public static final int GL_SAMPLE_MASK_VALUE = 36434;
   public static final int GL_TEXTURE_2D_MULTISAMPLE = 37120;
   public static final int GL_PROXY_TEXTURE_2D_MULTISAMPLE = 37121;
   public static final int GL_TEXTURE_2D_MULTISAMPLE_ARRAY = 37122;
   public static final int GL_PROXY_TEXTURE_2D_MULTISAMPLE_ARRAY = 37123;
   public static final int GL_MAX_SAMPLE_MASK_WORDS = 36441;
   public static final int GL_MAX_COLOR_TEXTURE_SAMPLES = 37134;
   public static final int GL_MAX_DEPTH_TEXTURE_SAMPLES = 37135;
   public static final int GL_MAX_INTEGER_SAMPLES = 37136;
   public static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE = 37124;
   public static final int GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY = 37125;
   public static final int GL_TEXTURE_SAMPLES = 37126;
   public static final int GL_TEXTURE_FIXED_SAMPLE_LOCATIONS = 37127;
   public static final int GL_SAMPLER_2D_MULTISAMPLE = 37128;
   public static final int GL_INT_SAMPLER_2D_MULTISAMPLE = 37129;
   public static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE = 37130;
   public static final int GL_SAMPLER_2D_MULTISAMPLE_ARRAY = 37131;
   public static final int GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 37132;
   public static final int GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY = 37133;
   public static final int GL_DEPTH_CLAMP = 34383;
   public static final int GL_GEOMETRY_SHADER = 36313;
   public static final int GL_GEOMETRY_VERTICES_OUT = 36314;
   public static final int GL_GEOMETRY_INPUT_TYPE = 36315;
   public static final int GL_GEOMETRY_OUTPUT_TYPE = 36316;
   public static final int GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS = 35881;
   public static final int GL_MAX_GEOMETRY_UNIFORM_COMPONENTS = 36319;
   public static final int GL_MAX_GEOMETRY_OUTPUT_VERTICES = 36320;
   public static final int GL_MAX_GEOMETRY_TOTAL_OUTPUT_COMPONENTS = 36321;
   public static final int GL_LINES_ADJACENCY = 10;
   public static final int GL_LINE_STRIP_ADJACENCY = 11;
   public static final int GL_TRIANGLES_ADJACENCY = 12;
   public static final int GL_TRIANGLE_STRIP_ADJACENCY = 13;
   public static final int GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS = 36264;
   public static final int GL_FRAMEBUFFER_ATTACHMENT_LAYERED = 36263;
   public static final int GL_PROGRAM_POINT_SIZE = 34370;
   public static final int GL_MAX_SERVER_WAIT_TIMEOUT = 37137;
   public static final int GL_OBJECT_TYPE = 37138;
   public static final int GL_SYNC_CONDITION = 37139;
   public static final int GL_SYNC_STATUS = 37140;
   public static final int GL_SYNC_FLAGS = 37141;
   public static final int GL_SYNC_FENCE = 37142;
   public static final int GL_SYNC_GPU_COMMANDS_COMPLETE = 37143;
   public static final int GL_UNSIGNALED = 37144;
   public static final int GL_SIGNALED = 37145;
   public static final int GL_SYNC_FLUSH_COMMANDS_BIT = 1;
   public static final long GL_TIMEOUT_IGNORED = -1L;
   public static final int GL_ALREADY_SIGNALED = 37146;
   public static final int GL_TIMEOUT_EXPIRED = 37147;
   public static final int GL_CONDITION_SATISFIED = 37148;
   public static final int GL_WAIT_FAILED = 37149;

   public static void glGetBufferParameter(int target, int pname, LongBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameteri64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)params, 4);
      nglGetBufferParameteri64v(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetBufferParameteri64v(int var0, int var1, long var2, long var4);

   /** @deprecated */
   @Deprecated
   public static long glGetBufferParameter(int target, int pname) {
      return glGetBufferParameteri64(target, pname);
   }

   public static long glGetBufferParameteri64(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameteri64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer params = APIUtil.getBufferLong(caps);
      nglGetBufferParameteri64v(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glDrawElementsBaseVertex(int mode, ByteBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsBaseVertex(mode, indices.remaining(), 5121, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   public static void glDrawElementsBaseVertex(int mode, IntBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsBaseVertex(mode, indices.remaining(), 5125, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   public static void glDrawElementsBaseVertex(int mode, ShortBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsBaseVertex(mode, indices.remaining(), 5123, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   static native void nglDrawElementsBaseVertex(int var0, int var1, int var2, long var3, int var5, long var6);

   public static void glDrawElementsBaseVertex(int mode, int indices_count, int type, long indices_buffer_offset, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawElementsBaseVertexBO(mode, indices_count, type, indices_buffer_offset, basevertex, function_pointer);
   }

   static native void nglDrawElementsBaseVertexBO(int var0, int var1, int var2, long var3, int var5, long var6);

   public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, ByteBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElementsBaseVertex(mode, start, end, indices.remaining(), 5121, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, IntBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElementsBaseVertex(mode, start, end, indices.remaining(), 5125, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, ShortBuffer indices, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawRangeElementsBaseVertex(mode, start, end, indices.remaining(), 5123, MemoryUtil.getAddress(indices), basevertex, function_pointer);
   }

   static native void nglDrawRangeElementsBaseVertex(int var0, int var1, int var2, int var3, int var4, long var5, int var7, long var8);

   public static void glDrawRangeElementsBaseVertex(int mode, int start, int end, int indices_count, int type, long indices_buffer_offset, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawRangeElementsBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawRangeElementsBaseVertexBO(mode, start, end, indices_count, type, indices_buffer_offset, basevertex, function_pointer);
   }

   static native void nglDrawRangeElementsBaseVertexBO(int var0, int var1, int var2, int var3, int var4, long var5, int var7, long var8);

   public static void glDrawElementsInstancedBaseVertex(int mode, ByteBuffer indices, int primcount, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedBaseVertex(mode, indices.remaining(), 5121, MemoryUtil.getAddress(indices), primcount, basevertex, function_pointer);
   }

   public static void glDrawElementsInstancedBaseVertex(int mode, IntBuffer indices, int primcount, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedBaseVertex(mode, indices.remaining(), 5125, MemoryUtil.getAddress(indices), primcount, basevertex, function_pointer);
   }

   public static void glDrawElementsInstancedBaseVertex(int mode, ShortBuffer indices, int primcount, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOdisabled(caps);
      BufferChecks.checkDirect(indices);
      nglDrawElementsInstancedBaseVertex(mode, indices.remaining(), 5123, MemoryUtil.getAddress(indices), primcount, basevertex, function_pointer);
   }

   static native void nglDrawElementsInstancedBaseVertex(int var0, int var1, int var2, long var3, int var5, int var6, long var7);

   public static void glDrawElementsInstancedBaseVertex(int mode, int indices_count, int type, long indices_buffer_offset, int primcount, int basevertex) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDrawElementsInstancedBaseVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLChecks.ensureElementVBOenabled(caps);
      nglDrawElementsInstancedBaseVertexBO(mode, indices_count, type, indices_buffer_offset, primcount, basevertex, function_pointer);
   }

   static native void nglDrawElementsInstancedBaseVertexBO(int var0, int var1, int var2, long var3, int var5, int var6, long var7);

   public static void glProvokingVertex(int mode) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glProvokingVertex;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglProvokingVertex(mode, function_pointer);
   }

   static native void nglProvokingVertex(int var0, long var1);

   public static void glTexImage2DMultisample(int target, int samples, int internalformat, int width, int height, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage2DMultisample;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexImage2DMultisample(target, samples, internalformat, width, height, fixedsamplelocations, function_pointer);
   }

   static native void nglTexImage2DMultisample(int var0, int var1, int var2, int var3, int var4, boolean var5, long var6);

   public static void glTexImage3DMultisample(int target, int samples, int internalformat, int width, int height, int depth, boolean fixedsamplelocations) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glTexImage3DMultisample;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglTexImage3DMultisample(target, samples, internalformat, width, height, depth, fixedsamplelocations, function_pointer);
   }

   static native void nglTexImage3DMultisample(int var0, int var1, int var2, int var3, int var4, int var5, boolean var6, long var7);

   public static void glGetMultisample(int pname, int index, FloatBuffer val) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetMultisamplefv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((FloatBuffer)val, 2);
      nglGetMultisamplefv(pname, index, MemoryUtil.getAddress(val), function_pointer);
   }

   static native void nglGetMultisamplefv(int var0, int var1, long var2, long var4);

   public static void glSampleMaski(int index, int mask) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glSampleMaski;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglSampleMaski(index, mask, function_pointer);
   }

   static native void nglSampleMaski(int var0, int var1, long var2);

   public static void glFramebufferTexture(int target, int attachment, int texture, int level) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFramebufferTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglFramebufferTexture(target, attachment, texture, level, function_pointer);
   }

   static native void nglFramebufferTexture(int var0, int var1, int var2, int var3, long var4);

   public static GLSync glFenceSync(int condition, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glFenceSync;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLSync __result = new GLSync(nglFenceSync(condition, flags, function_pointer));
      return __result;
   }

   static native long nglFenceSync(int var0, int var1, long var2);

   public static boolean glIsSync(GLSync sync) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsSync;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsSync(sync.getPointer(), function_pointer);
      return __result;
   }

   static native boolean nglIsSync(long var0, long var2);

   public static void glDeleteSync(GLSync sync) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteSync;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteSync(sync.getPointer(), function_pointer);
   }

   static native void nglDeleteSync(long var0, long var2);

   public static int glClientWaitSync(GLSync sync, int flags, long timeout) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glClientWaitSync;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglClientWaitSync(sync.getPointer(), flags, timeout, function_pointer);
      return __result;
   }

   static native int nglClientWaitSync(long var0, int var2, long var3, long var5);

   public static void glWaitSync(GLSync sync, int flags, long timeout) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glWaitSync;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglWaitSync(sync.getPointer(), flags, timeout, function_pointer);
   }

   static native void nglWaitSync(long var0, int var2, long var3, long var5);

   public static void glGetInteger64(int pname, LongBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInteger64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)data, 1);
      nglGetInteger64v(pname, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetInteger64v(int var0, long var1, long var3);

   public static long glGetInteger64(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInteger64v;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer data = APIUtil.getBufferLong(caps);
      nglGetInteger64v(pname, MemoryUtil.getAddress(data), function_pointer);
      return data.get(0);
   }

   public static void glGetInteger64(int value, int index, LongBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInteger64i_v;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((LongBuffer)data, 4);
      nglGetInteger64i_v(value, index, MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetInteger64i_v(int var0, int var1, long var2, long var4);

   public static long glGetInteger64(int value, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInteger64i_v;
      BufferChecks.checkFunctionAddress(function_pointer);
      LongBuffer data = APIUtil.getBufferLong(caps);
      nglGetInteger64i_v(value, index, MemoryUtil.getAddress(data), function_pointer);
      return data.get(0);
   }

   public static void glGetSync(GLSync sync, int pname, IntBuffer length, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetSynciv;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(values);
      nglGetSynciv(sync.getPointer(), pname, values.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglGetSynciv(long var0, int var2, int var3, long var4, long var6, long var8);

   /** @deprecated */
   @Deprecated
   public static int glGetSync(GLSync sync, int pname) {
      return glGetSynci(sync, pname);
   }

   public static int glGetSynci(GLSync sync, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetSynciv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer values = APIUtil.getBufferInt(caps);
      nglGetSynciv(sync.getPointer(), pname, 1, 0L, MemoryUtil.getAddress(values), function_pointer);
      return values.get(0);
   }
}
