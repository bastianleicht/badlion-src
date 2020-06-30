package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL41;

public final class ARBGetProgramBinary {
   public static final int GL_PROGRAM_BINARY_RETRIEVABLE_HINT = 33367;
   public static final int GL_PROGRAM_BINARY_LENGTH = 34625;
   public static final int GL_NUM_PROGRAM_BINARY_FORMATS = 34814;
   public static final int GL_PROGRAM_BINARY_FORMATS = 34815;

   public static void glGetProgramBinary(int program, IntBuffer length, IntBuffer binaryFormat, ByteBuffer binary) {
      GL41.glGetProgramBinary(program, length, binaryFormat, binary);
   }

   public static void glProgramBinary(int program, int binaryFormat, ByteBuffer binary) {
      GL41.glProgramBinary(program, binaryFormat, binary);
   }

   public static void glProgramParameteri(int program, int pname, int value) {
      GL41.glProgramParameteri(program, pname, value);
   }
}
