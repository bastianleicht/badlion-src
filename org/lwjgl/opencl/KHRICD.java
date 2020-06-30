package org.lwjgl.opencl;

import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLCapabilities;

public final class KHRICD {
   public static final int CL_PLATFORM_ICD_SUFFIX_KHR = 2336;
   public static final int CL_PLATFORM_NOT_FOUND_KHR = -1001;

   public static int clIcdGetPlatformIDsKHR(PointerBuffer platforms, IntBuffer num_platforms) {
      long function_pointer = CLCapabilities.clIcdGetPlatformIDsKHR;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(platforms != null) {
         BufferChecks.checkDirect(platforms);
      }

      if(num_platforms != null) {
         BufferChecks.checkBuffer((IntBuffer)num_platforms, 1);
      }

      int __result = nclIcdGetPlatformIDsKHR(platforms == null?0:platforms.remaining(), MemoryUtil.getAddressSafe(platforms), MemoryUtil.getAddressSafe(num_platforms), function_pointer);
      return __result;
   }

   static native int nclIcdGetPlatformIDsKHR(int var0, long var1, long var3, long var5);
}
