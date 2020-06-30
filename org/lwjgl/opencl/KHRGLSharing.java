package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLCapabilities;

public final class KHRGLSharing {
   public static final int CL_INVALID_GL_SHAREGROUP_REFERENCE_KHR = -1000;
   public static final int CL_CURRENT_DEVICE_FOR_GL_CONTEXT_KHR = 8198;
   public static final int CL_DEVICES_FOR_GL_CONTEXT_KHR = 8199;
   public static final int CL_GL_CONTEXT_KHR = 8200;
   public static final int CL_EGL_DISPLAY_KHR = 8201;
   public static final int CL_GLX_DISPLAY_KHR = 8202;
   public static final int CL_WGL_HDC_KHR = 8203;
   public static final int CL_CGL_SHAREGROUP_KHR = 8204;

   public static int clGetGLContextInfoKHR(PointerBuffer properties, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetGLContextInfoKHR;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(properties);
      BufferChecks.checkNullTerminated(properties);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      if(param_value_size_ret == null && APIUtil.isDevicesParam(param_name)) {
         param_value_size_ret = APIUtil.getBufferPointer();
      }

      int __result = nclGetGLContextInfoKHR(MemoryUtil.getAddress(properties), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      if(__result == 0 && param_value != null && APIUtil.isDevicesParam(param_name)) {
         APIUtil.getCLPlatform(properties).registerCLDevices(param_value, param_value_size_ret);
      }

      return __result;
   }

   static native int nclGetGLContextInfoKHR(long var0, int var2, long var3, long var5, long var7, long var9);
}
