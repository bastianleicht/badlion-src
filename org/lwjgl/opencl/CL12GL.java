package org.lwjgl.opencl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLMem;

public final class CL12GL {
   public static final int CL_GL_OBJECT_TEXTURE2D_ARRAY = 8206;
   public static final int CL_GL_OBJECT_TEXTURE1D = 8207;
   public static final int CL_GL_OBJECT_TEXTURE1D_ARRAY = 8208;
   public static final int CL_GL_OBJECT_TEXTURE_BUFFER = 8209;

   public static CLMem clCreateFromGLTexture(CLContext context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateFromGLTexture;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateFromGLTexture(context.getPointer(), flags, target, miplevel, texture, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateFromGLTexture(long var0, long var2, int var4, int var5, int var6, long var7, long var9);
}
