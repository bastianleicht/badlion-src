package org.lwjgl.opencl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContext;

public final class KHRTerminateContext {
   public static final int CL_DEVICE_TERMINATE_CAPABILITY_KHR = 8207;
   public static final int CL_CONTEXT_TERMINATE_KHR = 8208;

   public static int clTerminateContextKHR(CLContext context) {
      long function_pointer = CLCapabilities.clTerminateContextKHR;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclTerminateContextKHR(context.getPointer(), function_pointer);
      return __result;
   }

   static native int nclTerminateContextKHR(long var0, long var2);
}
