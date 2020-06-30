package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBSparseBuffer {
   public static final int GL_SPARSE_STORAGE_BIT_ARB = 1024;
   public static final int GL_SPARSE_BUFFER_PAGE_SIZE_ARB = 33528;

   public static void glBufferPageCommitmentARB(int target, long offset, long size, boolean commit) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glBufferPageCommitmentARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglBufferPageCommitmentARB(target, offset, size, commit, function_pointer);
   }

   static native void nglBufferPageCommitmentARB(int var0, long var1, long var3, boolean var5, long var6);
}
