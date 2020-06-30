package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opengl.APIUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBShaderObjects {
   public static final int GL_PROGRAM_OBJECT_ARB = 35648;
   public static final int GL_OBJECT_TYPE_ARB = 35662;
   public static final int GL_OBJECT_SUBTYPE_ARB = 35663;
   public static final int GL_OBJECT_DELETE_STATUS_ARB = 35712;
   public static final int GL_OBJECT_COMPILE_STATUS_ARB = 35713;
   public static final int GL_OBJECT_LINK_STATUS_ARB = 35714;
   public static final int GL_OBJECT_VALIDATE_STATUS_ARB = 35715;
   public static final int GL_OBJECT_INFO_LOG_LENGTH_ARB = 35716;
   public static final int GL_OBJECT_ATTACHED_OBJECTS_ARB = 35717;
   public static final int GL_OBJECT_ACTIVE_UNIFORMS_ARB = 35718;
   public static final int GL_OBJECT_ACTIVE_UNIFORM_MAX_LENGTH_ARB = 35719;
   public static final int GL_OBJECT_SHADER_SOURCE_LENGTH_ARB = 35720;
   public static final int GL_SHADER_OBJECT_ARB = 35656;
   public static final int GL_FLOAT_VEC2_ARB = 35664;
   public static final int GL_FLOAT_VEC3_ARB = 35665;
   public static final int GL_FLOAT_VEC4_ARB = 35666;
   public static final int GL_INT_VEC2_ARB = 35667;
   public static final int GL_INT_VEC3_ARB = 35668;
   public static final int GL_INT_VEC4_ARB = 35669;
   public static final int GL_BOOL_ARB = 35670;
   public static final int GL_BOOL_VEC2_ARB = 35671;
   public static final int GL_BOOL_VEC3_ARB = 35672;
   public static final int GL_BOOL_VEC4_ARB = 35673;
   public static final int GL_FLOAT_MAT2_ARB = 35674;
   public static final int GL_FLOAT_MAT3_ARB = 35675;
   public static final int GL_FLOAT_MAT4_ARB = 35676;
   public static final int GL_SAMPLER_1D_ARB = 35677;
   public static final int GL_SAMPLER_2D_ARB = 35678;
   public static final int GL_SAMPLER_3D_ARB = 35679;
   public static final int GL_SAMPLER_CUBE_ARB = 35680;
   public static final int GL_SAMPLER_1D_SHADOW_ARB = 35681;
   public static final int GL_SAMPLER_2D_SHADOW_ARB = 35682;
   public static final int GL_SAMPLER_2D_RECT_ARB = 35683;
   public static final int GL_SAMPLER_2D_RECT_SHADOW_ARB = 35684;

   public static void glDeleteObjectARB(int obj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDeleteObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDeleteObjectARB(obj, function_pointer);
   }

   static native void nglDeleteObjectARB(int var0, long var1);

   public static int glGetHandleARB(int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetHandleARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetHandleARB(pname, function_pointer);
      return __result;
   }

   static native int nglGetHandleARB(int var0, long var1);

   public static void glDetachObjectARB(int containerObj, int attachedObj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDetachObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDetachObjectARB(containerObj, attachedObj, function_pointer);
   }

   static native void nglDetachObjectARB(int var0, int var1, long var2);

   public static int glCreateShaderObjectARB(int shaderType) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCreateShaderObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglCreateShaderObjectARB(shaderType, function_pointer);
      return __result;
   }

   static native int nglCreateShaderObjectARB(int var0, long var1);

   public static void glShaderSourceARB(int shader, ByteBuffer string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderSourceARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(string);
      nglShaderSourceARB(shader, 1, MemoryUtil.getAddress(string), string.remaining(), function_pointer);
   }

   static native void nglShaderSourceARB(int var0, int var1, long var2, int var4, long var5);

   public static void glShaderSourceARB(int shader, CharSequence string) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderSourceARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglShaderSourceARB(shader, 1, APIUtil.getBuffer(caps, string), string.length(), function_pointer);
   }

   public static void glShaderSourceARB(int shader, CharSequence[] strings) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glShaderSourceARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkArray(strings);
      nglShaderSourceARB3(shader, strings.length, APIUtil.getBuffer(caps, strings), APIUtil.getLengths(caps, strings), function_pointer);
   }

   static native void nglShaderSourceARB3(int var0, int var1, long var2, long var4, long var6);

   public static void glCompileShaderARB(int shaderObj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCompileShaderARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglCompileShaderARB(shaderObj, function_pointer);
   }

   static native void nglCompileShaderARB(int var0, long var1);

   public static int glCreateProgramObjectARB() {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCreateProgramObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglCreateProgramObjectARB(function_pointer);
      return __result;
   }

   static native int nglCreateProgramObjectARB(long var0);

   public static void glAttachObjectARB(int containerObj, int obj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glAttachObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglAttachObjectARB(containerObj, obj, function_pointer);
   }

   static native void nglAttachObjectARB(int var0, int var1, long var2);

   public static void glLinkProgramARB(int programObj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glLinkProgramARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglLinkProgramARB(programObj, function_pointer);
   }

   static native void nglLinkProgramARB(int var0, long var1);

   public static void glUseProgramObjectARB(int programObj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUseProgramObjectARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUseProgramObjectARB(programObj, function_pointer);
   }

   static native void nglUseProgramObjectARB(int var0, long var1);

   public static void glValidateProgramARB(int programObj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glValidateProgramARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglValidateProgramARB(programObj, function_pointer);
   }

   static native void nglValidateProgramARB(int var0, long var1);

   public static void glUniform1fARB(int location, float v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform1fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform1fARB(location, v0, function_pointer);
   }

   static native void nglUniform1fARB(int var0, float var1, long var2);

   public static void glUniform2fARB(int location, float v0, float v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform2fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform2fARB(location, v0, v1, function_pointer);
   }

   static native void nglUniform2fARB(int var0, float var1, float var2, long var3);

   public static void glUniform3fARB(int location, float v0, float v1, float v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform3fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform3fARB(location, v0, v1, v2, function_pointer);
   }

   static native void nglUniform3fARB(int var0, float var1, float var2, float var3, long var4);

   public static void glUniform4fARB(int location, float v0, float v1, float v2, float v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform4fARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform4fARB(location, v0, v1, v2, v3, function_pointer);
   }

   static native void nglUniform4fARB(int var0, float var1, float var2, float var3, float var4, long var5);

   public static void glUniform1iARB(int location, int v0) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform1iARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform1iARB(location, v0, function_pointer);
   }

   static native void nglUniform1iARB(int var0, int var1, long var2);

   public static void glUniform2iARB(int location, int v0, int v1) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform2iARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform2iARB(location, v0, v1, function_pointer);
   }

   static native void nglUniform2iARB(int var0, int var1, int var2, long var3);

   public static void glUniform3iARB(int location, int v0, int v1, int v2) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform3iARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform3iARB(location, v0, v1, v2, function_pointer);
   }

   static native void nglUniform3iARB(int var0, int var1, int var2, int var3, long var4);

   public static void glUniform4iARB(int location, int v0, int v1, int v2, int v3) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform4iARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglUniform4iARB(location, v0, v1, v2, v3, function_pointer);
   }

   static native void nglUniform4iARB(int var0, int var1, int var2, int var3, int var4, long var5);

   public static void glUniform1ARB(int location, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform1fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform1fvARB(location, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform1fvARB(int var0, int var1, long var2, long var4);

   public static void glUniform2ARB(int location, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform2fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform2fvARB(location, values.remaining() >> 1, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform2fvARB(int var0, int var1, long var2, long var4);

   public static void glUniform3ARB(int location, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform3fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform3fvARB(location, values.remaining() / 3, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform3fvARB(int var0, int var1, long var2, long var4);

   public static void glUniform4ARB(int location, FloatBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform4fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform4fvARB(location, values.remaining() >> 2, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform4fvARB(int var0, int var1, long var2, long var4);

   public static void glUniform1ARB(int location, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform1ivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform1ivARB(location, values.remaining(), MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform1ivARB(int var0, int var1, long var2, long var4);

   public static void glUniform2ARB(int location, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform2ivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform2ivARB(location, values.remaining() >> 1, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform2ivARB(int var0, int var1, long var2, long var4);

   public static void glUniform3ARB(int location, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform3ivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform3ivARB(location, values.remaining() / 3, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform3ivARB(int var0, int var1, long var2, long var4);

   public static void glUniform4ARB(int location, IntBuffer values) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniform4ivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(values);
      nglUniform4ivARB(location, values.remaining() >> 2, MemoryUtil.getAddress(values), function_pointer);
   }

   static native void nglUniform4ivARB(int var0, int var1, long var2, long var4);

   public static void glUniformMatrix2ARB(int location, boolean transpose, FloatBuffer matrices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformMatrix2fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(matrices);
      nglUniformMatrix2fvARB(location, matrices.remaining() >> 2, transpose, MemoryUtil.getAddress(matrices), function_pointer);
   }

   static native void nglUniformMatrix2fvARB(int var0, int var1, boolean var2, long var3, long var5);

   public static void glUniformMatrix3ARB(int location, boolean transpose, FloatBuffer matrices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformMatrix3fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(matrices);
      nglUniformMatrix3fvARB(location, matrices.remaining() / 9, transpose, MemoryUtil.getAddress(matrices), function_pointer);
   }

   static native void nglUniformMatrix3fvARB(int var0, int var1, boolean var2, long var3, long var5);

   public static void glUniformMatrix4ARB(int location, boolean transpose, FloatBuffer matrices) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glUniformMatrix4fvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(matrices);
      nglUniformMatrix4fvARB(location, matrices.remaining() >> 4, transpose, MemoryUtil.getAddress(matrices), function_pointer);
   }

   static native void nglUniformMatrix4fvARB(int var0, int var1, boolean var2, long var3, long var5);

   public static void glGetObjectParameterARB(int obj, int pname, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectParameterfvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglGetObjectParameterfvARB(obj, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetObjectParameterfvARB(int var0, int var1, long var2, long var4);

   public static float glGetObjectParameterfARB(int obj, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectParameterfvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      FloatBuffer params = APIUtil.getBufferFloat(caps);
      nglGetObjectParameterfvARB(obj, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetObjectParameterARB(int obj, int pname, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectParameterivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglGetObjectParameterivARB(obj, pname, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetObjectParameterivARB(int var0, int var1, long var2, long var4);

   public static int glGetObjectParameteriARB(int obj, int pname) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetObjectParameterivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer params = APIUtil.getBufferInt(caps);
      nglGetObjectParameterivARB(obj, pname, MemoryUtil.getAddress(params), function_pointer);
      return params.get(0);
   }

   public static void glGetInfoLogARB(int obj, IntBuffer length, ByteBuffer infoLog) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInfoLogARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(infoLog);
      nglGetInfoLogARB(obj, infoLog.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(infoLog), function_pointer);
   }

   static native void nglGetInfoLogARB(int var0, int var1, long var2, long var4, long var6);

   public static String glGetInfoLogARB(int obj, int maxLength) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetInfoLogARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer infoLog_length = APIUtil.getLengths(caps);
      ByteBuffer infoLog = APIUtil.getBufferByte(caps, maxLength);
      nglGetInfoLogARB(obj, maxLength, MemoryUtil.getAddress0((Buffer)infoLog_length), MemoryUtil.getAddress(infoLog), function_pointer);
      infoLog.limit(infoLog_length.get(0));
      return APIUtil.getString(caps, infoLog);
   }

   public static void glGetAttachedObjectsARB(int containerObj, IntBuffer count, IntBuffer obj) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetAttachedObjectsARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(count != null) {
         BufferChecks.checkBuffer((IntBuffer)count, 1);
      }

      BufferChecks.checkDirect(obj);
      nglGetAttachedObjectsARB(containerObj, obj.remaining(), MemoryUtil.getAddressSafe(count), MemoryUtil.getAddress(obj), function_pointer);
   }

   static native void nglGetAttachedObjectsARB(int var0, int var1, long var2, long var4, long var6);

   public static int glGetUniformLocationARB(int programObj, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(name);
      BufferChecks.checkNullTerminated(name);
      int __result = nglGetUniformLocationARB(programObj, MemoryUtil.getAddress(name), function_pointer);
      return __result;
   }

   static native int nglGetUniformLocationARB(int var0, long var1, long var3);

   public static int glGetUniformLocationARB(int programObj, CharSequence name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformLocationARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nglGetUniformLocationARB(programObj, APIUtil.getBufferNT(caps, name), function_pointer);
      return __result;
   }

   public static void glGetActiveUniformARB(int programObj, int index, IntBuffer length, IntBuffer size, IntBuffer type, ByteBuffer name) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveUniformARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkBuffer((IntBuffer)size, 1);
      BufferChecks.checkBuffer((IntBuffer)type, 1);
      BufferChecks.checkDirect(name);
      nglGetActiveUniformARB(programObj, index, name.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(size), MemoryUtil.getAddress(type), MemoryUtil.getAddress(name), function_pointer);
   }

   static native void nglGetActiveUniformARB(int var0, int var1, int var2, long var3, long var5, long var7, long var9, long var11);

   public static String glGetActiveUniformARB(int programObj, int index, int maxLength, IntBuffer sizeType) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveUniformARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((IntBuffer)sizeType, 2);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, maxLength);
      nglGetActiveUniformARB(programObj, index, maxLength, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress(sizeType), MemoryUtil.getAddress(sizeType, sizeType.position() + 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static String glGetActiveUniformARB(int programObj, int index, int maxLength) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveUniformARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer name_length = APIUtil.getLengths(caps);
      ByteBuffer name = APIUtil.getBufferByte(caps, maxLength);
      nglGetActiveUniformARB(programObj, index, maxLength, MemoryUtil.getAddress0((Buffer)name_length), MemoryUtil.getAddress0((Buffer)APIUtil.getBufferInt(caps)), MemoryUtil.getAddress((IntBuffer)APIUtil.getBufferInt(caps), 1), MemoryUtil.getAddress(name), function_pointer);
      name.limit(name_length.get(0));
      return APIUtil.getString(caps, name);
   }

   public static int glGetActiveUniformSizeARB(int programObj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveUniformARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer size = APIUtil.getBufferInt(caps);
      nglGetActiveUniformARB(programObj, index, 0, 0L, MemoryUtil.getAddress(size), MemoryUtil.getAddress((IntBuffer)size, 1), APIUtil.getBufferByte0(caps), function_pointer);
      return size.get(0);
   }

   public static int glGetActiveUniformTypeARB(int programObj, int index) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetActiveUniformARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer type = APIUtil.getBufferInt(caps);
      nglGetActiveUniformARB(programObj, index, 0, 0L, MemoryUtil.getAddress((IntBuffer)type, 1), MemoryUtil.getAddress(type), APIUtil.getBufferByte0(caps), function_pointer);
      return type.get(0);
   }

   public static void glGetUniformARB(int programObj, int location, FloatBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformfvARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglGetUniformfvARB(programObj, location, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetUniformfvARB(int var0, int var1, long var2, long var4);

   public static void glGetUniformARB(int programObj, int location, IntBuffer params) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetUniformivARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(params);
      nglGetUniformivARB(programObj, location, MemoryUtil.getAddress(params), function_pointer);
   }

   static native void nglGetUniformivARB(int var0, int var1, long var2, long var4);

   public static void glGetShaderSourceARB(int obj, IntBuffer length, ByteBuffer source) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetShaderSourceARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(length != null) {
         BufferChecks.checkBuffer((IntBuffer)length, 1);
      }

      BufferChecks.checkDirect(source);
      nglGetShaderSourceARB(obj, source.remaining(), MemoryUtil.getAddressSafe(length), MemoryUtil.getAddress(source), function_pointer);
   }

   static native void nglGetShaderSourceARB(int var0, int var1, long var2, long var4, long var6);

   public static String glGetShaderSourceARB(int obj, int maxLength) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glGetShaderSourceARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      IntBuffer source_length = APIUtil.getLengths(caps);
      ByteBuffer source = APIUtil.getBufferByte(caps, maxLength);
      nglGetShaderSourceARB(obj, maxLength, MemoryUtil.getAddress0((Buffer)source_length), MemoryUtil.getAddress(source), function_pointer);
      source.limit(source_length.get(0));
      return APIUtil.getString(caps, source);
   }
}
