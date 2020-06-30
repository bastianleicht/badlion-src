package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLKernel;

public final class KHRSubgroups {
   public static int clGetKernelSubGroupInfoKHR(CLKernel kernel, CLDevice device, int param_name, ByteBuffer input_value, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetKernelSubGroupInfoKHR;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(input_value != null) {
         BufferChecks.checkDirect(input_value);
      }

      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetKernelSubGroupInfoKHR(kernel.getPointer(), device == null?0L:device.getPointer(), param_name, (long)(input_value == null?0:input_value.remaining()), MemoryUtil.getAddressSafe(input_value), (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetKernelSubGroupInfoKHR(long var0, long var2, int var4, long var5, long var7, long var9, long var11, long var13, long var15);
}
