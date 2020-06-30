package org.lwjgl.opengl;

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
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.StateTracker;

public final class GL15 {
   public static final int GL_ARRAY_BUFFER = 34962;
   public static final int GL_ELEMENT_ARRAY_BUFFER = 34963;
   public static final int GL_ARRAY_BUFFER_BINDING = 34964;
   public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 34965;
   public static final int GL_VERTEX_ARRAY_BUFFER_BINDING = 34966;
   public static final int GL_NORMAL_ARRAY_BUFFER_BINDING = 34967;
   public static final int GL_COLOR_ARRAY_BUFFER_BINDING = 34968;
   public static final int GL_INDEX_ARRAY_BUFFER_BINDING = 34969;
   public static final int GL_TEXTURE_COORD_ARRAY_BUFFER_BINDING = 34970;
   public static final int GL_EDGE_FLAG_ARRAY_BUFFER_BINDING = 34971;
   public static final int GL_SECONDARY_COLOR_ARRAY_BUFFER_BINDING = 34972;
   public static final int GL_FOG_COORDINATE_ARRAY_BUFFER_BINDING = 34973;
   public static final int GL_WEIGHT_ARRAY_BUFFER_BINDING = 34974;
   public static final int GL_VERTEX_ATTRIB_ARRAY_BUFFER_BINDING = 34975;
   public static final int GL_STREAM_DRAW = 35040;
   public static final int GL_STREAM_READ = 35041;
   public static final int GL_STREAM_COPY = 35042;
   public static final int GL_STATIC_DRAW = 35044;
   public static final int GL_STATIC_READ = 35045;
   public static final int GL_STATIC_COPY = 35046;
   public static final int GL_DYNAMIC_DRAW = 35048;
   public static final int GL_DYNAMIC_READ = 35049;
   public static final int GL_DYNAMIC_COPY = 35050;
   public static final int GL_READ_ONLY = 35000;
   public static final int GL_WRITE_ONLY = 35001;
   public static final int GL_READ_WRITE = 35002;
   public static final int GL_BUFFER_SIZE = 34660;
   public static final int GL_BUFFER_USAGE = 34661;
   public static final int GL_BUFFER_ACCESS = 35003;
   public static final int GL_BUFFER_MAPPED = 35004;
   public static final int GL_BUFFER_MAP_POINTER = 35005;
   public static final int GL_FOG_COORD_SRC = 33872;
   public static final int GL_FOG_COORD = 33873;
   public static final int GL_CURRENT_FOG_COORD = 33875;
   public static final int GL_FOG_COORD_ARRAY_TYPE = 33876;
   public static final int GL_FOG_COORD_ARRAY_STRIDE = 33877;
   public static final int GL_FOG_COORD_ARRAY_POINTER = 33878;
   public static final int GL_FOG_COORD_ARRAY = 33879;
   public static final int GL_FOG_COORD_ARRAY_BUFFER_BINDING = 34973;
   public static final int GL_SRC0_RGB = 34176;
   public static final int GL_SRC1_RGB = 34177;
   public static final int GL_SRC2_RGB = 34178;
   public static final int GL_SRC0_ALPHA = 34184;
   public static final int GL_SRC1_ALPHA = 34185;
   public static final int GL_SRC2_ALPHA = 34186;
   public static final int GL_SAMPLES_PASSED = 35092;
   public static final int GL_QUERY_COUNTER_BITS = 34916;
   public static final int GL_CURRENT_QUERY = 34917;
   public static final int GL_QUERY_RESULT = 34918;
   public static final int GL_QUERY_RESULT_AVAILABLE = 34919;

   public static void glBindBuffer(int target, int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBindBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      StateTracker.bindBuffer(caps, target, buffer);
      nglBindBuffer(target, buffer, function_pointer);
   }

   static native void nglBindBuffer(int var0, int var1, long var2);

   public static void glDeleteBuffers(IntBuffer buffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteBuffers;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buffers);
      nglDeleteBuffers(buffers.remaining(), MemoryUtil.getAddress(buffers), function_pointer);
   }

   static native void nglDeleteBuffers(int var0, long var1, long var3);

   public static void glDeleteBuffers(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteBuffers;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteBuffers(1, APIUtil.getInt(caps, buffer), function_pointer);
   }

   public static void glGenBuffers(IntBuffer buffers) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenBuffers;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(buffers);
      nglGenBuffers(buffers.remaining(), MemoryUtil.getAddress(buffers), function_pointer);
   }

   static native void nglGenBuffers(int var0, long var1, long var3);

   public static int glGenBuffers() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenBuffers;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer buffers = APIUtil.getBufferInt(caps);
      nglGenBuffers(1, MemoryUtil.getAddress(buffers), function_pointer);
      return buffers.get(0);
   }

   public static boolean glIsBuffer(int buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsBuffer(buffer, function_pointer);
      return __result;
   }

   static native boolean nglIsBuffer(int var0, long var1);

   public static void glBufferData(int target, long data_size, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBufferData(target, data_size, 0L, usage, function_pointer);
   }

   public static void glBufferData(int target, ByteBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferData(target, (long)data.remaining(), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glBufferData(int target, DoubleBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferData(target, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glBufferData(int target, FloatBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferData(target, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glBufferData(int target, IntBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferData(target, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   public static void glBufferData(int target, ShortBuffer data, int usage) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferData(target, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), usage, function_pointer);
   }

   static native void nglBufferData(int var0, long var1, long var3, int var5, long var6);

   public static void glBufferSubData(int target, long offset, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferSubData(target, offset, (long)data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glBufferSubData(int target, long offset, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferSubData(target, offset, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glBufferSubData(int target, long offset, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferSubData(target, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glBufferSubData(int target, long offset, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferSubData(target, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glBufferSubData(int target, long offset, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglBufferSubData(target, offset, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglBufferSubData(int var0, long var1, long var3, long var5, long var7);

   public static void glGetBufferSubData(int target, long offset, ByteBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetBufferSubData(target, offset, (long)data.remaining(), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetBufferSubData(int target, long offset, DoubleBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetBufferSubData(target, offset, (long)(data.remaining() << 3), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetBufferSubData(int target, long offset, FloatBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetBufferSubData(target, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetBufferSubData(int target, long offset, IntBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetBufferSubData(target, offset, (long)(data.remaining() << 2), MemoryUtil.getAddress(data), function_pointer);
   }

   public static void glGetBufferSubData(int target, long offset, ShortBuffer data) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferSubData;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(data);
      nglGetBufferSubData(target, offset, (long)(data.remaining() << 1), MemoryUtil.getAddress(data), function_pointer);
   }

   static native void nglGetBufferSubData(int var0, long var1, long var3, long var5, long var7);

   public static ByteBuffer glMapBuffer(int target, int access, ByteBuffer old_buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(old_buffer != null) {
         BufferChecks.checkDirect(old_buffer);
      }

      ByteBuffer __result = nglMapBuffer(target, access, (long)glGetBufferParameteri(target, '蝤'), old_buffer, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   public static ByteBuffer glMapBuffer(int target, int access, long length, ByteBuffer old_buffer) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glMapBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(old_buffer != null) {
         BufferChecks.checkDirect(old_buffer);
      }

      ByteBuffer __result = nglMapBuffer(target, access, length, old_buffer, function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglMapBuffer(int var0, int var1, long var2, ByteBuffer var4, long var5);

   public static boolean glUnmapBuffer(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUnmapBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglUnmapBuffer(target, function_pointer);
      return __result;
   }

   static native boolean nglUnmapBuffer(int var0, long var1);

   public static void glGetBufferParameter(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 4);
      nglGetBufferParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetBufferParameteriv(int var0, int var1, long var2, long var4);

   /** @deprecated */
   @Deprecated
   public static int glGetBufferParameter(int target, int pname) {
      return glGetBufferParameteri(target, pname);
   }

   public static int glGetBufferParameteri(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferParameteriv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetBufferParameteriv(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static ByteBuffer glGetBufferPointer(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetBufferPointerv;
      BufferChecks.checkFunctionAddress(function_pointer);
      ByteBuffer __result = nglGetBufferPointerv(target, pname, (long)glGetBufferParameteri(target, '蝤'), function_pointer);
      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nglGetBufferPointerv(int var0, int var1, long var2, long var4);

   public static void glGenQueries(IntBuffer ids) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenQueries;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ids);
      nglGenQueries(ids.remaining(), MemoryUtil.getAddress(ids), function_pointer);
   }

   static native void nglGenQueries(int var0, long var1, long var3);

   public static int glGenQueries() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGenQueries;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer ids = APIUtil.getBufferInt(caps);
      nglGenQueries(1, MemoryUtil.getAddress(ids), function_pointer);
      return ids.get(0);
   }

   public static void glDeleteQueries(IntBuffer ids) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteQueries;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ids);
      nglDeleteQueries(ids.remaining(), MemoryUtil.getAddress(ids), function_pointer);
   }

   static native void nglDeleteQueries(int var0, long var1, long var3);

   public static void glDeleteQueries(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteQueries;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteQueries(1, APIUtil.getInt(caps, id), function_pointer);
   }

   public static boolean glIsQuery(int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glIsQuery;
      BufferChecks.checkFunctionAddress(function_pointer);
      boolean __result = nglIsQuery(id, function_pointer);
      return __result;
   }

   static native boolean nglIsQuery(int var0, long var1);

   public static void glBeginQuery(int target, int id) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBeginQuery;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBeginQuery(target, id, function_pointer);
   }

   static native void nglBeginQuery(int var0, int var1, long var2);

   public static void glEndQuery(int target) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glEndQuery;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglEndQuery(target, function_pointer);
   }

   static native void nglEndQuery(int var0, long var1);

   public static void glGetQuery(int target, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetQueryiv(target, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetQueryiv(int var0, int var1, long var2, long var4);

   /** @deprecated */
   @Deprecated
   public static int glGetQuery(int target, int pname) {
      return glGetQueryi(target, pname);
   }

   public static int glGetQueryi(int target, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetQueryiv(target, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetQueryObject(int id, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetQueryObjectiv(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetQueryObjectiv(int var0, int var1, long var2, long var4);

   public static int glGetQueryObjecti(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetQueryObjectiv(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetQueryObjectu(int id, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)params, 1);
      nglGetQueryObjectuiv(id, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetQueryObjectuiv(int var0, int var1, long var2, long var4);

   public static int glGetQueryObjectui(int id, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetQueryObjectuiv;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetQueryObjectuiv(id, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }
}
