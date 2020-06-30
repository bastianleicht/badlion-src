package org.lwjgl.opengl;

import org.lwjgl.opengl.GL31;

public final class ARBCopyBuffer {
   public static final int GL_COPY_READ_BUFFER = 36662;
   public static final int GL_COPY_WRITE_BUFFER = 36663;

   public static void glCopyBufferSubData(int readTarget, int writeTarget, long readOffset, long writeOffset, long size) {
      GL31.glCopyBufferSubData(readTarget, writeTarget, readOffset, writeOffset, size);
   }
}
