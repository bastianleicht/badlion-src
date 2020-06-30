package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;

public final class ARBUniformBufferObject {
   public static final int GL_UNIFORM_BUFFER = 35345;
   public static final int GL_UNIFORM_BUFFER_BINDING = 35368;
   public static final int GL_UNIFORM_BUFFER_START = 35369;
   public static final int GL_UNIFORM_BUFFER_SIZE = 35370;
   public static final int GL_MAX_VERTEX_UNIFORM_BLOCKS = 35371;
   public static final int GL_MAX_GEOMETRY_UNIFORM_BLOCKS = 35372;
   public static final int GL_MAX_FRAGMENT_UNIFORM_BLOCKS = 35373;
   public static final int GL_MAX_COMBINED_UNIFORM_BLOCKS = 35374;
   public static final int GL_MAX_UNIFORM_BUFFER_BINDINGS = 35375;
   public static final int GL_MAX_UNIFORM_BLOCK_SIZE = 35376;
   public static final int GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS = 35377;
   public static final int GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS = 35378;
   public static final int GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS = 35379;
   public static final int GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT = 35380;
   public static final int GL_ACTIVE_UNIFORM_BLOCK_MAX_NAME_LENGTH = 35381;
   public static final int GL_ACTIVE_UNIFORM_BLOCKS = 35382;
   public static final int GL_UNIFORM_TYPE = 35383;
   public static final int GL_UNIFORM_SIZE = 35384;
   public static final int GL_UNIFORM_NAME_LENGTH = 35385;
   public static final int GL_UNIFORM_BLOCK_INDEX = 35386;
   public static final int GL_UNIFORM_OFFSET = 35387;
   public static final int GL_UNIFORM_ARRAY_STRIDE = 35388;
   public static final int GL_UNIFORM_MATRIX_STRIDE = 35389;
   public static final int GL_UNIFORM_IS_ROW_MAJOR = 35390;
   public static final int GL_UNIFORM_BLOCK_BINDING = 35391;
   public static final int GL_UNIFORM_BLOCK_DATA_SIZE = 35392;
   public static final int GL_UNIFORM_BLOCK_NAME_LENGTH = 35393;
   public static final int GL_UNIFORM_BLOCK_ACTIVE_UNIFORMS = 35394;
   public static final int GL_UNIFORM_BLOCK_ACTIVE_UNIFORM_INDICES = 35395;
   public static final int GL_UNIFORM_BLOCK_REFERENCED_BY_VERTEX_SHADER = 35396;
   public static final int GL_UNIFORM_BLOCK_REFERENCED_BY_GEOMETRY_SHADER = 35397;
   public static final int GL_UNIFORM_BLOCK_REFERENCED_BY_FRAGMENT_SHADER = 35398;
   public static final int GL_INVALID_INDEX = -1;

   public static void glGetUniformIndices(int program, ByteBuffer uniformNames, IntBuffer uniformIndices) {
      GL31.glGetUniformIndices(program, uniformNames, uniformIndices);
   }

   public static void glGetUniformIndices(int program, CharSequence[] uniformNames, IntBuffer uniformIndices) {
      GL31.glGetUniformIndices(program, uniformNames, uniformIndices);
   }

   public static void glGetActiveUniforms(int program, IntBuffer uniformIndices, int pname, IntBuffer params) {
      GL31.glGetActiveUniforms(program, uniformIndices, pname, params);
   }

   /** @deprecated */
   @Deprecated
   public static int glGetActiveUniforms(int program, int uniformIndex, int pname) {
      return GL31.glGetActiveUniformsi(program, uniformIndex, pname);
   }

   public static int glGetActiveUniformsi(int program, int uniformIndex, int pname) {
      return GL31.glGetActiveUniformsi(program, uniformIndex, pname);
   }

   public static void glGetActiveUniformName(int program, int uniformIndex, IntBuffer length, ByteBuffer uniformName) {
      GL31.glGetActiveUniformName(program, uniformIndex, length, uniformName);
   }

   public static String glGetActiveUniformName(int program, int uniformIndex, int bufSize) {
      return GL31.glGetActiveUniformName(program, uniformIndex, bufSize);
   }

   public static int glGetUniformBlockIndex(int program, ByteBuffer uniformBlockName) {
      return GL31.glGetUniformBlockIndex(program, uniformBlockName);
   }

   public static int glGetUniformBlockIndex(int program, CharSequence uniformBlockName) {
      return GL31.glGetUniformBlockIndex(program, uniformBlockName);
   }

   public static void glGetActiveUniformBlock(int program, int uniformBlockIndex, int pname, IntBuffer params) {
      GL31.glGetActiveUniformBlock(program, uniformBlockIndex, pname, params);
   }

   /** @deprecated */
   @Deprecated
   public static int glGetActiveUniformBlock(int program, int uniformBlockIndex, int pname) {
      return GL31.glGetActiveUniformBlocki(program, uniformBlockIndex, pname);
   }

   public static int glGetActiveUniformBlocki(int program, int uniformBlockIndex, int pname) {
      return GL31.glGetActiveUniformBlocki(program, uniformBlockIndex, pname);
   }

   public static void glGetActiveUniformBlockName(int program, int uniformBlockIndex, IntBuffer length, ByteBuffer uniformBlockName) {
      GL31.glGetActiveUniformBlockName(program, uniformBlockIndex, length, uniformBlockName);
   }

   public static String glGetActiveUniformBlockName(int program, int uniformBlockIndex, int bufSize) {
      return GL31.glGetActiveUniformBlockName(program, uniformBlockIndex, bufSize);
   }

   public static void glBindBufferRange(int target, int index, int buffer, long offset, long size) {
      GL30.glBindBufferRange(target, index, buffer, offset, size);
   }

   public static void glBindBufferBase(int target, int index, int buffer) {
      GL30.glBindBufferBase(target, index, buffer);
   }

   public static void glGetInteger(int value, int index, IntBuffer data) {
      GL30.glGetInteger(value, index, data);
   }

   public static int glGetInteger(int value, int index) {
      return GL30.glGetInteger(value, index);
   }

   public static void glUniformBlockBinding(int program, int uniformBlockIndex, int uniformBlockBinding) {
      GL31.glUniformBlockBinding(program, uniformBlockIndex, uniformBlockBinding);
   }
}
