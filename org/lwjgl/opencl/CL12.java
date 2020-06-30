package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLCompileProgramCallback;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLFunctionAddress;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLLinkProgramCallback;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLPrintfCallback;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.CallbackUtil;

public final class CL12 {
   public static final int CL_COMPILE_PROGRAM_FAILURE = -15;
   public static final int CL_LINKER_NOT_AVAILABLE = -16;
   public static final int CL_LINK_PROGRAM_FAILURE = -17;
   public static final int CL_DEVICE_PARTITION_FAILED = -18;
   public static final int CL_KERNEL_ARG_INFO_NOT_AVAILABLE = -19;
   public static final int CL_INVALID_IMAGE_DESCRIPTOR = -65;
   public static final int CL_INVALID_COMPILER_OPTIONS = -66;
   public static final int CL_INVALID_LINKER_OPTIONS = -67;
   public static final int CL_INVALID_DEVICE_PARTITION_COUNT = -68;
   public static final int CL_VERSION_1_2 = 1;
   public static final int CL_BLOCKING = 1;
   public static final int CL_NON_BLOCKING = 0;
   public static final int CL_DEVICE_TYPE_CUSTOM = 16;
   public static final int CL_DEVICE_DOUBLE_FP_CONFIG = 4146;
   public static final int CL_DEVICE_LINKER_AVAILABLE = 4158;
   public static final int CL_DEVICE_BUILT_IN_KERNELS = 4159;
   public static final int CL_DEVICE_IMAGE_MAX_BUFFER_SIZE = 4160;
   public static final int CL_DEVICE_IMAGE_MAX_ARRAY_SIZE = 4161;
   public static final int CL_DEVICE_PARENT_DEVICE = 4162;
   public static final int CL_DEVICE_PARTITION_MAX_SUB_DEVICES = 4163;
   public static final int CL_DEVICE_PARTITION_PROPERTIES = 4164;
   public static final int CL_DEVICE_PARTITION_AFFINITY_DOMAIN = 4165;
   public static final int CL_DEVICE_PARTITION_TYPE = 4166;
   public static final int CL_DEVICE_REFERENCE_COUNT = 4167;
   public static final int CL_DEVICE_PREFERRED_INTEROP_USER_SYNC = 4168;
   public static final int CL_DEVICE_PRINTF_BUFFER_SIZE = 4169;
   public static final int CL_FP_CORRECTLY_ROUNDED_DIVIDE_SQRT = 128;
   public static final int CL_CONTEXT_INTEROP_USER_SYNC = 4229;
   public static final int CL_DEVICE_PARTITION_EQUALLY = 4230;
   public static final int CL_DEVICE_PARTITION_BY_COUNTS = 4231;
   public static final int CL_DEVICE_PARTITION_BY_COUNTS_LIST_END = 0;
   public static final int CL_DEVICE_PARTITION_BY_AFFINITY_DOMAIN = 4232;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_NUMA = 1;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_L4_CACHE = 2;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_L3_CACHE = 4;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_L2_CACHE = 8;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_L1_CACHE = 16;
   public static final int CL_DEVICE_AFFINITY_DOMAIN_NEXT_PARTITIONABLE = 32;
   public static final int CL_MEM_HOST_WRITE_ONLY = 128;
   public static final int CL_MEM_HOST_READ_ONLY = 256;
   public static final int CL_MEM_HOST_NO_ACCESS = 512;
   public static final int CL_MIGRATE_MEM_OBJECT_HOST = 1;
   public static final int CL_MIGRATE_MEM_OBJECT_CONTENT_UNDEFINED = 2;
   public static final int CL_MEM_OBJECT_IMAGE2D_ARRAY = 4339;
   public static final int CL_MEM_OBJECT_IMAGE1D = 4340;
   public static final int CL_MEM_OBJECT_IMAGE1D_ARRAY = 4341;
   public static final int CL_MEM_OBJECT_IMAGE1D_BUFFER = 4342;
   public static final int CL_IMAGE_ARRAY_SIZE = 4375;
   public static final int CL_IMAGE_BUFFER = 4376;
   public static final int CL_IMAGE_NUM_MIP_LEVELS = 4377;
   public static final int CL_IMAGE_NUM_SAMPLES = 4378;
   public static final int CL_MAP_WRITE_INVALIDATE_REGION = 4;
   public static final int CL_PROGRAM_NUM_KERNELS = 4455;
   public static final int CL_PROGRAM_KERNEL_NAMES = 4456;
   public static final int CL_PROGRAM_BINARY_TYPE = 4484;
   public static final int CL_PROGRAM_BINARY_TYPE_NONE = 0;
   public static final int CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT = 1;
   public static final int CL_PROGRAM_BINARY_TYPE_LIBRARY = 2;
   public static final int CL_PROGRAM_BINARY_TYPE_EXECUTABLE = 4;
   public static final int CL_KERNEL_ATTRIBUTES = 4501;
   public static final int CL_KERNEL_ARG_ADDRESS_QUALIFIER = 4502;
   public static final int CL_KERNEL_ARG_ACCESS_QUALIFIER = 4503;
   public static final int CL_KERNEL_ARG_TYPE_NAME = 4504;
   public static final int CL_KERNEL_ARG_TYPE_QUALIFIER = 4505;
   public static final int CL_KERNEL_ARG_NAME = 4506;
   public static final int CL_KERNEL_ARG_ADDRESS_GLOBAL = 4506;
   public static final int CL_KERNEL_ARG_ADDRESS_LOCAL = 4507;
   public static final int CL_KERNEL_ARG_ADDRESS_CONSTANT = 4508;
   public static final int CL_KERNEL_ARG_ADDRESS_PRIVATE = 4509;
   public static final int CL_KERNEL_ARG_ACCESS_READ_ONLY = 4512;
   public static final int CL_KERNEL_ARG_ACCESS_WRITE_ONLY = 4513;
   public static final int CL_KERNEL_ARG_ACCESS_READ_WRITE = 4514;
   public static final int CL_KERNEL_ARG_ACCESS_NONE = 4515;
   public static final int CL_KERNEL_ARG_TYPE_NONE = 0;
   public static final int CL_KERNEL_ARG_TYPE_CONST = 1;
   public static final int CL_KERNEL_ARG_TYPE_RESTRICT = 2;
   public static final int CL_KERNEL_ARG_TYPE_VOLATILE = 4;
   public static final int CL_KERNEL_GLOBAL_WORK_SIZE = 4533;
   public static final int CL_COMMAND_BARRIER = 4613;
   public static final int CL_COMMAND_MIGRATE_MEM_OBJECTS = 4614;
   public static final int CL_COMMAND_FILL_BUFFER = 4615;
   public static final int CL_COMMAND_FILL_IMAGE = 4616;

   public static int clRetainDevice(CLDevice device) {
      long function_pointer = CLCapabilities.clRetainDevice;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainDevice(device.getPointer(), function_pointer);
      if(__result == 0) {
         device.retain();
      }

      return __result;
   }

   static native int nclRetainDevice(long var0, long var2);

   public static int clReleaseDevice(CLDevice device) {
      long function_pointer = CLCapabilities.clReleaseDevice;
      BufferChecks.checkFunctionAddress(function_pointer);
      APIUtil.releaseObjects(device);
      int __result = nclReleaseDevice(device.getPointer(), function_pointer);
      if(__result == 0) {
         device.release();
      }

      return __result;
   }

   static native int nclReleaseDevice(long var0, long var2);

   public static int clCreateSubDevices(CLDevice in_device, LongBuffer properties, PointerBuffer out_devices, IntBuffer num_devices_ret) {
      long function_pointer = CLCapabilities.clCreateSubDevices;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(properties);
      BufferChecks.checkNullTerminated(properties);
      if(out_devices != null) {
         BufferChecks.checkDirect(out_devices);
      }

      if(num_devices_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)num_devices_ret, 1);
      }

      int __result = nclCreateSubDevices(in_device.getPointer(), MemoryUtil.getAddress(properties), out_devices == null?0:out_devices.remaining(), MemoryUtil.getAddressSafe(out_devices), MemoryUtil.getAddressSafe(num_devices_ret), function_pointer);
      if(__result == 0 && out_devices != null) {
         in_device.registerSubCLDevices(out_devices);
      }

      return __result;
   }

   static native int nclCreateSubDevices(long var0, long var2, int var4, long var5, long var7, long var9);

   public static CLMem clCreateImage(CLContext context, long flags, ByteBuffer image_format, ByteBuffer image_desc, ByteBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      BufferChecks.checkBuffer(image_desc, 7 * PointerBuffer.getPointerSize() + 8 + PointerBuffer.getPointerSize());
      if(host_ptr != null) {
         BufferChecks.checkDirect(host_ptr);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage(context.getPointer(), flags, MemoryUtil.getAddress(image_format), MemoryUtil.getAddress(image_desc), MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage(CLContext context, long flags, ByteBuffer image_format, ByteBuffer image_desc, FloatBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      BufferChecks.checkBuffer(image_desc, 7 * PointerBuffer.getPointerSize() + 8 + PointerBuffer.getPointerSize());
      if(host_ptr != null) {
         BufferChecks.checkDirect(host_ptr);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage(context.getPointer(), flags, MemoryUtil.getAddress(image_format), MemoryUtil.getAddress(image_desc), MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage(CLContext context, long flags, ByteBuffer image_format, ByteBuffer image_desc, IntBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      BufferChecks.checkBuffer(image_desc, 7 * PointerBuffer.getPointerSize() + 8 + PointerBuffer.getPointerSize());
      if(host_ptr != null) {
         BufferChecks.checkDirect(host_ptr);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage(context.getPointer(), flags, MemoryUtil.getAddress(image_format), MemoryUtil.getAddress(image_desc), MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage(CLContext context, long flags, ByteBuffer image_format, ByteBuffer image_desc, ShortBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      BufferChecks.checkBuffer(image_desc, 7 * PointerBuffer.getPointerSize() + 8 + PointerBuffer.getPointerSize());
      if(host_ptr != null) {
         BufferChecks.checkDirect(host_ptr);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage(context.getPointer(), flags, MemoryUtil.getAddress(image_format), MemoryUtil.getAddress(image_desc), MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateImage(long var0, long var2, long var4, long var6, long var8, long var10, long var12);

   public static CLProgram clCreateProgramWithBuiltInKernels(CLContext context, PointerBuffer device_list, ByteBuffer kernel_names, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithBuiltInKernels;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)device_list, 1);
      BufferChecks.checkDirect(kernel_names);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithBuiltInKernels(context.getPointer(), device_list.remaining(), MemoryUtil.getAddress(device_list), MemoryUtil.getAddress(kernel_names), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithBuiltInKernels(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLProgram clCreateProgramWithBuiltInKernels(CLContext context, PointerBuffer device_list, CharSequence kernel_names, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithBuiltInKernels;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)device_list, 1);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithBuiltInKernels(context.getPointer(), device_list.remaining(), MemoryUtil.getAddress(device_list), APIUtil.getBuffer(kernel_names), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static int clCompileProgram(CLProgram program, PointerBuffer device_list, ByteBuffer options, PointerBuffer input_header, ByteBuffer header_include_name, CLCompileProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clCompileProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(options);
      BufferChecks.checkNullTerminated(options);
      BufferChecks.checkBuffer((PointerBuffer)input_header, 1);
      BufferChecks.checkDirect(header_include_name);
      BufferChecks.checkNullTerminated(header_include_name);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var11;
      try {
         __result = nclCompileProgram(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), MemoryUtil.getAddress(options), 1, MemoryUtil.getAddress(input_header), MemoryUtil.getAddress(header_include_name), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var11;
   }

   static native int nclCompileProgram(long var0, int var2, long var3, long var5, int var7, long var8, long var10, long var12, long var14, long var16);

   public static int clCompileProgramMulti(CLProgram program, PointerBuffer device_list, ByteBuffer options, PointerBuffer input_headers, ByteBuffer header_include_names, CLCompileProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clCompileProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(options);
      BufferChecks.checkNullTerminated(options);
      BufferChecks.checkBuffer((PointerBuffer)input_headers, 1);
      BufferChecks.checkDirect(header_include_names);
      BufferChecks.checkNullTerminated(header_include_names, input_headers.remaining());
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var11;
      try {
         __result = nclCompileProgramMulti(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), MemoryUtil.getAddress(options), input_headers.remaining(), MemoryUtil.getAddress(input_headers), MemoryUtil.getAddress(header_include_names), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var11;
   }

   static native int nclCompileProgramMulti(long var0, int var2, long var3, long var5, int var7, long var8, long var10, long var12, long var14, long var16);

   public static int clCompileProgram(CLProgram program, PointerBuffer device_list, ByteBuffer options, PointerBuffer input_headers, ByteBuffer[] header_include_names, CLCompileProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clCompileProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(options);
      BufferChecks.checkNullTerminated(options);
      BufferChecks.checkBuffer(input_headers, header_include_names.length);
      BufferChecks.checkArray((Object[])header_include_names, 1);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var11;
      try {
         __result = nclCompileProgram3(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), MemoryUtil.getAddress(options), header_include_names.length, MemoryUtil.getAddress(input_headers), header_include_names, pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var11;
   }

   static native int nclCompileProgram3(long var0, int var2, long var3, long var5, int var7, long var8, ByteBuffer[] var10, long var11, long var13, long var15);

   public static int clCompileProgram(CLProgram program, PointerBuffer device_list, CharSequence options, PointerBuffer input_header, CharSequence header_include_name, CLCompileProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clCompileProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkBuffer((PointerBuffer)input_header, 1);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var11;
      try {
         __result = nclCompileProgram(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), APIUtil.getBufferNT(options), 1, MemoryUtil.getAddress(input_header), APIUtil.getBufferNT(header_include_name), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var11;
   }

   public static int clCompileProgram(CLProgram program, PointerBuffer device_list, CharSequence options, PointerBuffer input_header, CharSequence[] header_include_name, CLCompileProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clCompileProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkBuffer((PointerBuffer)input_header, 1);
      BufferChecks.checkArray(header_include_name);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var11;
      try {
         __result = nclCompileProgramMulti(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), APIUtil.getBufferNT(options), input_header.remaining(), MemoryUtil.getAddress(input_header), APIUtil.getBufferNT(header_include_name), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var11;
   }

   public static CLProgram clLinkProgram(CLContext context, PointerBuffer device_list, ByteBuffer options, PointerBuffer input_programs, CLLinkProgramCallback pfn_notify, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clLinkProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(options);
      BufferChecks.checkNullTerminated(options);
      BufferChecks.checkDirect(input_programs);
      BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext(context);
      }

      CLProgram __result = null;

      CLProgram var11;
      try {
         __result = new CLProgram(nclLinkProgram(context.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), MemoryUtil.getAddress(options), input_programs.remaining(), MemoryUtil.getAddress(input_programs), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddress(errcode_ret), function_pointer), context);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(errcode_ret.get(errcode_ret.position()), user_data);
      }

      return var11;
   }

   static native long nclLinkProgram(long var0, int var2, long var3, long var5, int var7, long var8, long var10, long var12, long var14, long var16);

   public static CLProgram clLinkProgram(CLContext context, PointerBuffer device_list, CharSequence options, PointerBuffer input_programs, CLLinkProgramCallback pfn_notify, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clLinkProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(input_programs);
      BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext(context);
      }

      CLProgram __result = null;

      CLProgram var11;
      try {
         __result = new CLProgram(nclLinkProgram(context.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), APIUtil.getBufferNT(options), input_programs.remaining(), MemoryUtil.getAddress(input_programs), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddress(errcode_ret), function_pointer), context);
         var11 = __result;
      } finally {
         CallbackUtil.checkCallback(errcode_ret.get(errcode_ret.position()), user_data);
      }

      return var11;
   }

   public static int clUnloadPlatformCompiler(CLPlatform platform) {
      long function_pointer = CLCapabilities.clUnloadPlatformCompiler;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclUnloadPlatformCompiler(platform.getPointer(), function_pointer);
      return __result;
   }

   static native int nclUnloadPlatformCompiler(long var0, long var2);

   public static int clGetKernelArgInfo(CLKernel kernel, int arg_indx, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetKernelArgInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetKernelArgInfo(kernel.getPointer(), arg_indx, param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetKernelArgInfo(long var0, int var2, int var3, long var4, long var6, long var8, long var10);

   public static int clEnqueueFillBuffer(CLCommandQueue command_queue, CLMem buffer, ByteBuffer pattern, long offset, long size, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueFillBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(pattern);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueFillBuffer(command_queue.getPointer(), buffer.getPointer(), MemoryUtil.getAddress(pattern), (long)pattern.remaining(), offset, size, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      return __result;
   }

   static native int nclEnqueueFillBuffer(long var0, long var2, long var4, long var6, long var8, long var10, int var12, long var13, long var15, long var17);

   public static int clEnqueueFillImage(CLCommandQueue command_queue, CLMem image, ByteBuffer fill_color, PointerBuffer origin, PointerBuffer region, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueFillImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)fill_color, 16);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueFillImage(command_queue.getPointer(), image.getPointer(), MemoryUtil.getAddress(fill_color), MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      return __result;
   }

   static native int nclEnqueueFillImage(long var0, long var2, long var4, long var6, long var8, int var10, long var11, long var13, long var15);

   public static int clEnqueueMigrateMemObjects(CLCommandQueue command_queue, PointerBuffer mem_objects, long flags, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueMigrateMemObjects;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(mem_objects);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueMigrateMemObjects(command_queue.getPointer(), mem_objects.remaining(), MemoryUtil.getAddress(mem_objects), flags, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      return __result;
   }

   static native int nclEnqueueMigrateMemObjects(long var0, int var2, long var3, long var5, int var7, long var8, long var10, long var12);

   public static int clEnqueueMarkerWithWaitList(CLCommandQueue command_queue, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueMarkerWithWaitList;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueMarkerWithWaitList(command_queue.getPointer(), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      return __result;
   }

   static native int nclEnqueueMarkerWithWaitList(long var0, int var2, long var3, long var5, long var7);

   public static int clEnqueueBarrierWithWaitList(CLCommandQueue command_queue, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueBarrierWithWaitList;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueBarrierWithWaitList(command_queue.getPointer(), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      return __result;
   }

   static native int nclEnqueueBarrierWithWaitList(long var0, int var2, long var3, long var5, long var7);

   public static int clSetPrintfCallback(CLContext context, CLPrintfCallback pfn_notify) {
      long function_pointer = CLCapabilities.clSetPrintfCallback;
      BufferChecks.checkFunctionAddress(function_pointer);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      int __result = 0;

      int var7;
      try {
         __result = nclSetPrintfCallback(context.getPointer(), pfn_notify.getPointer(), user_data, function_pointer);
         var7 = __result;
      } finally {
         context.setPrintfCallback(user_data, __result);
      }

      return var7;
   }

   static native int nclSetPrintfCallback(long var0, long var2, long var4, long var6);

   static CLFunctionAddress clGetExtensionFunctionAddressForPlatform(CLPlatform platform, ByteBuffer func_name) {
      long function_pointer = CLCapabilities.clGetExtensionFunctionAddressForPlatform;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(func_name);
      BufferChecks.checkNullTerminated(func_name);
      CLFunctionAddress __result = new CLFunctionAddress(nclGetExtensionFunctionAddressForPlatform(platform.getPointer(), MemoryUtil.getAddress(func_name), function_pointer));
      return __result;
   }

   static native long nclGetExtensionFunctionAddressForPlatform(long var0, long var2, long var4);

   static CLFunctionAddress clGetExtensionFunctionAddressForPlatform(CLPlatform platform, CharSequence func_name) {
      long function_pointer = CLCapabilities.clGetExtensionFunctionAddressForPlatform;
      BufferChecks.checkFunctionAddress(function_pointer);
      CLFunctionAddress __result = new CLFunctionAddress(nclGetExtensionFunctionAddressForPlatform(platform.getPointer(), APIUtil.getBufferNT(func_name), function_pointer));
      return __result;
   }
}
