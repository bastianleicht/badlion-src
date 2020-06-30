package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.opencl.CLCapabilities;

final class APPLEContextLoggingFunctions {
   static void clLogMessagesToSystemLogAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
      long function_pointer = CLCapabilities.clLogMessagesToSystemLogAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(errstr);
      BufferChecks.checkDirect(private_info);
      BufferChecks.checkDirect(user_data);
      nclLogMessagesToSystemLogAPPLE(MemoryUtil.getAddress(errstr), MemoryUtil.getAddress(private_info), (long)private_info.remaining(), MemoryUtil.getAddress(user_data), function_pointer);
   }

   static native void nclLogMessagesToSystemLogAPPLE(long var0, long var2, long var4, long var6, long var8);

   static void clLogMessagesToStdoutAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
      long function_pointer = CLCapabilities.clLogMessagesToStdoutAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(errstr);
      BufferChecks.checkDirect(private_info);
      BufferChecks.checkDirect(user_data);
      nclLogMessagesToStdoutAPPLE(MemoryUtil.getAddress(errstr), MemoryUtil.getAddress(private_info), (long)private_info.remaining(), MemoryUtil.getAddress(user_data), function_pointer);
   }

   static native void nclLogMessagesToStdoutAPPLE(long var0, long var2, long var4, long var6, long var8);

   static void clLogMessagesToStderrAPPLE(ByteBuffer errstr, ByteBuffer private_info, ByteBuffer user_data) {
      long function_pointer = CLCapabilities.clLogMessagesToStderrAPPLE;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(errstr);
      BufferChecks.checkDirect(private_info);
      BufferChecks.checkDirect(user_data);
      nclLogMessagesToStderrAPPLE(MemoryUtil.getAddress(errstr), MemoryUtil.getAddress(private_info), (long)private_info.remaining(), MemoryUtil.getAddress(user_data), function_pointer);
   }

   static native void nclLogMessagesToStderrAPPLE(long var0, long var2, long var4, long var6, long var8);
}
