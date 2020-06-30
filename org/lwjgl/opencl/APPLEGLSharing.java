package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLPlatform;

public final class APPLEGLSharing {
   public static final int CL_CONTEXT_PROPERTY_USE_CGL_SHAREGROUP_APPLE = 268435456;
   public static final int CL_CGL_DEVICE_FOR_CURRENT_VIRTUAL_SCREEN_APPLE = 268435458;
   public static final int CL_CGL_DEVICES_FOR_SUPPORTED_VIRTUAL_SCREENS_APPLE = 268435459;
   public static final int CL_INVALID_GL_CONTEXT_APPLE = -1000;

   public static int clGetGLContextInfoAPPLE(CLContext context, PointerBuffer platform_gl_ctx, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetGLContextInfoAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)platform_gl_ctx, 1);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      if(param_value_size_ret == null && APIUtil.isDevicesParam(param_name)) {
         param_value_size_ret = APIUtil.getBufferPointer();
      }

      int __result = nclGetGLContextInfoAPPLE(context.getPointer(), MemoryUtil.getAddress(platform_gl_ctx), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      if(__result == 0 && param_value != null && APIUtil.isDevicesParam(param_name)) {
         ((CLPlatform)context.getParent()).registerCLDevices(param_value, param_value_size_ret);
      }

      return __result;
   }

   static native int nclGetGLContextInfoAPPLE(long var0, long var2, int var4, long var5, long var7, long var9, long var11);
}
