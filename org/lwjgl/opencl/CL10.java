package org.lwjgl.opencl;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLBuildProgramCallback;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLChecks;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLContextCallback;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLFunctionAddress;
import org.lwjgl.opencl.CLKernel;
import org.lwjgl.opencl.CLMem;
import org.lwjgl.opencl.CLNativeKernel;
import org.lwjgl.opencl.CLObject;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.CLSampler;
import org.lwjgl.opencl.CallbackUtil;

public final class CL10 {
   public static final int CL_SUCCESS = 0;
   public static final int CL_DEVICE_NOT_FOUND = -1;
   public static final int CL_DEVICE_NOT_AVAILABLE = -2;
   public static final int CL_COMPILER_NOT_AVAILABLE = -3;
   public static final int CL_MEM_OBJECT_ALLOCATION_FAILURE = -4;
   public static final int CL_OUT_OF_RESOURCES = -5;
   public static final int CL_OUT_OF_HOST_MEMORY = -6;
   public static final int CL_PROFILING_INFO_NOT_AVAILABLE = -7;
   public static final int CL_MEM_COPY_OVERLAP = -8;
   public static final int CL_IMAGE_FORMAT_MISMATCH = -9;
   public static final int CL_IMAGE_FORMAT_NOT_SUPPORTED = -10;
   public static final int CL_BUILD_PROGRAM_FAILURE = -11;
   public static final int CL_MAP_FAILURE = -12;
   public static final int CL_INVALID_VALUE = -30;
   public static final int CL_INVALID_DEVICE_TYPE = -31;
   public static final int CL_INVALID_PLATFORM = -32;
   public static final int CL_INVALID_DEVICE = -33;
   public static final int CL_INVALID_CONTEXT = -34;
   public static final int CL_INVALID_QUEUE_PROPERTIES = -35;
   public static final int CL_INVALID_COMMAND_QUEUE = -36;
   public static final int CL_INVALID_HOST_PTR = -37;
   public static final int CL_INVALID_MEM_OBJECT = -38;
   public static final int CL_INVALID_IMAGE_FORMAT_DESCRIPTOR = -39;
   public static final int CL_INVALID_IMAGE_SIZE = -40;
   public static final int CL_INVALID_SAMPLER = -41;
   public static final int CL_INVALID_BINARY = -42;
   public static final int CL_INVALID_BUILD_OPTIONS = -43;
   public static final int CL_INVALID_PROGRAM = -44;
   public static final int CL_INVALID_PROGRAM_EXECUTABLE = -45;
   public static final int CL_INVALID_KERNEL_NAME = -46;
   public static final int CL_INVALID_KERNEL_DEFINITION = -47;
   public static final int CL_INVALID_KERNEL = -48;
   public static final int CL_INVALID_ARG_INDEX = -49;
   public static final int CL_INVALID_ARG_VALUE = -50;
   public static final int CL_INVALID_ARG_SIZE = -51;
   public static final int CL_INVALID_KERNEL_ARGS = -52;
   public static final int CL_INVALID_WORK_DIMENSION = -53;
   public static final int CL_INVALID_WORK_GROUP_SIZE = -54;
   public static final int CL_INVALID_WORK_ITEM_SIZE = -55;
   public static final int CL_INVALID_GLOBAL_OFFSET = -56;
   public static final int CL_INVALID_EVENT_WAIT_LIST = -57;
   public static final int CL_INVALID_EVENT = -58;
   public static final int CL_INVALID_OPERATION = -59;
   public static final int CL_INVALID_GL_OBJECT = -60;
   public static final int CL_INVALID_BUFFER_SIZE = -61;
   public static final int CL_INVALID_MIP_LEVEL = -62;
   public static final int CL_INVALID_GLOBAL_WORK_SIZE = -63;
   public static final int CL_VERSION_1_0 = 1;
   public static final int CL_FALSE = 0;
   public static final int CL_TRUE = 1;
   public static final int CL_PLATFORM_PROFILE = 2304;
   public static final int CL_PLATFORM_VERSION = 2305;
   public static final int CL_PLATFORM_NAME = 2306;
   public static final int CL_PLATFORM_VENDOR = 2307;
   public static final int CL_PLATFORM_EXTENSIONS = 2308;
   public static final int CL_DEVICE_TYPE_DEFAULT = 1;
   public static final int CL_DEVICE_TYPE_CPU = 2;
   public static final int CL_DEVICE_TYPE_GPU = 4;
   public static final int CL_DEVICE_TYPE_ACCELERATOR = 8;
   public static final int CL_DEVICE_TYPE_ALL = -1;
   public static final int CL_DEVICE_TYPE = 4096;
   public static final int CL_DEVICE_VENDOR_ID = 4097;
   public static final int CL_DEVICE_MAX_COMPUTE_UNITS = 4098;
   public static final int CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS = 4099;
   public static final int CL_DEVICE_MAX_WORK_GROUP_SIZE = 4100;
   public static final int CL_DEVICE_MAX_WORK_ITEM_SIZES = 4101;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR = 4102;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT = 4103;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_ = 4104;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG = 4105;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT = 4106;
   public static final int CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE = 4107;
   public static final int CL_DEVICE_MAX_CLOCK_FREQUENCY = 4108;
   public static final int CL_DEVICE_ADDRESS_BITS = 4109;
   public static final int CL_DEVICE_MAX_READ_IMAGE_ARGS = 4110;
   public static final int CL_DEVICE_MAX_WRITE_IMAGE_ARGS = 4111;
   public static final int CL_DEVICE_MAX_MEM_ALLOC_SIZE = 4112;
   public static final int CL_DEVICE_IMAGE2D_MAX_WIDTH = 4113;
   public static final int CL_DEVICE_IMAGE2D_MAX_HEIGHT = 4114;
   public static final int CL_DEVICE_IMAGE3D_MAX_WIDTH = 4115;
   public static final int CL_DEVICE_IMAGE3D_MAX_HEIGHT = 4116;
   public static final int CL_DEVICE_IMAGE3D_MAX_DEPTH = 4117;
   public static final int CL_DEVICE_IMAGE_SUPPORT = 4118;
   public static final int CL_DEVICE_MAX_PARAMETER_SIZE = 4119;
   public static final int CL_DEVICE_MAX_SAMPLERS = 4120;
   public static final int CL_DEVICE_MEM_BASE_ADDR_ALIGN = 4121;
   public static final int CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE = 4122;
   public static final int CL_DEVICE_SINGLE_FP_CONFIG = 4123;
   public static final int CL_DEVICE_GLOBAL_MEM_CACHE_TYPE = 4124;
   public static final int CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE = 4125;
   public static final int CL_DEVICE_GLOBAL_MEM_CACHE_SIZE = 4126;
   public static final int CL_DEVICE_GLOBAL_MEM_SIZE = 4127;
   public static final int CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE = 4128;
   public static final int CL_DEVICE_MAX_CONSTANT_ARGS = 4129;
   public static final int CL_DEVICE_LOCAL_MEM_TYPE = 4130;
   public static final int CL_DEVICE_LOCAL_MEM_SIZE = 4131;
   public static final int CL_DEVICE_ERROR_CORRECTION_SUPPORT = 4132;
   public static final int CL_DEVICE_PROFILING_TIMER_RESOLUTION = 4133;
   public static final int CL_DEVICE_ENDIAN_LITTLE = 4134;
   public static final int CL_DEVICE_AVAILABLE = 4135;
   public static final int CL_DEVICE_COMPILER_AVAILABLE = 4136;
   public static final int CL_DEVICE_EXECUTION_CAPABILITIES = 4137;
   public static final int CL_DEVICE_QUEUE_PROPERTIES = 4138;
   public static final int CL_DEVICE_NAME = 4139;
   public static final int CL_DEVICE_VENDOR = 4140;
   public static final int CL_DRIVER_VERSION = 4141;
   public static final int CL_DEVICE_PROFILE = 4142;
   public static final int CL_DEVICE_VERSION = 4143;
   public static final int CL_DEVICE_EXTENSIONS = 4144;
   public static final int CL_DEVICE_PLATFORM = 4145;
   public static final int CL_FP_DENORM = 1;
   public static final int CL_FP_INF_NAN = 2;
   public static final int CL_FP_ROUND_TO_NEAREST = 4;
   public static final int CL_FP_ROUND_TO_ZERO = 8;
   public static final int CL_FP_ROUND_TO_INF = 16;
   public static final int CL_FP_FMA = 32;
   public static final int CL_NONE = 0;
   public static final int CL_READ_ONLY_CACHE = 1;
   public static final int CL_READ_WRITE_CACHE = 2;
   public static final int CL_LOCAL = 1;
   public static final int CL_GLOBAL = 2;
   public static final int CL_EXEC_KERNEL = 1;
   public static final int CL_EXEC_NATIVE_KERNEL = 2;
   public static final int CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE = 1;
   public static final int CL_QUEUE_PROFILING_ENABLE = 2;
   public static final int CL_CONTEXT_REFERENCE_COUNT = 4224;
   public static final int CL_CONTEXT_DEVICES = 4225;
   public static final int CL_CONTEXT_PROPERTIES = 4226;
   public static final int CL_CONTEXT_PLATFORM = 4228;
   public static final int CL_QUEUE_CONTEXT = 4240;
   public static final int CL_QUEUE_DEVICE = 4241;
   public static final int CL_QUEUE_REFERENCE_COUNT = 4242;
   public static final int CL_QUEUE_PROPERTIES = 4243;
   public static final int CL_MEM_READ_WRITE = 1;
   public static final int CL_MEM_WRITE_ONLY = 2;
   public static final int CL_MEM_READ_ONLY = 4;
   public static final int CL_MEM_USE_HOST_PTR = 8;
   public static final int CL_MEM_ALLOC_HOST_PTR = 16;
   public static final int CL_MEM_COPY_HOST_PTR = 32;
   public static final int CL_R = 4272;
   public static final int CL_A = 4273;
   public static final int CL_RG = 4274;
   public static final int CL_RA = 4275;
   public static final int CL_RGB = 4276;
   public static final int CL_RGBA = 4277;
   public static final int CL_BGRA = 4278;
   public static final int CL_ARGB = 4279;
   public static final int CL_INTENSITY = 4280;
   public static final int CL_LUMINANCE = 4281;
   public static final int CL_SNORM_INT8 = 4304;
   public static final int CL_SNORM_INT16 = 4305;
   public static final int CL_UNORM_INT8 = 4306;
   public static final int CL_UNORM_INT16 = 4307;
   public static final int CL_UNORM_SHORT_565 = 4308;
   public static final int CL_UNORM_SHORT_555 = 4309;
   public static final int CL_UNORM_INT_101010 = 4310;
   public static final int CL_SIGNED_INT8 = 4311;
   public static final int CL_SIGNED_INT16 = 4312;
   public static final int CL_SIGNED_INT32 = 4313;
   public static final int CL_UNSIGNED_INT8 = 4314;
   public static final int CL_UNSIGNED_INT16 = 4315;
   public static final int CL_UNSIGNED_INT32 = 4316;
   public static final int CL_HALF_FLOAT = 4317;
   public static final int CL_FLOAT = 4318;
   public static final int CL_MEM_OBJECT_BUFFER = 4336;
   public static final int CL_MEM_OBJECT_IMAGE2D = 4337;
   public static final int CL_MEM_OBJECT_IMAGE3D = 4338;
   public static final int CL_MEM_TYPE = 4352;
   public static final int CL_MEM_FLAGS = 4353;
   public static final int CL_MEM_SIZE = 4354;
   public static final int CL_MEM_HOST_PTR = 4355;
   public static final int CL_MEM_MAP_COUNT = 4356;
   public static final int CL_MEM_REFERENCE_COUNT = 4357;
   public static final int CL_MEM_CONTEXT = 4358;
   public static final int CL_IMAGE_FORMAT = 4368;
   public static final int CL_IMAGE_ELEMENT_SIZE = 4369;
   public static final int CL_IMAGE_ROW_PITCH = 4370;
   public static final int CL_IMAGE_SLICE_PITCH = 4371;
   public static final int CL_IMAGE_WIDTH = 4372;
   public static final int CL_IMAGE_HEIGHT = 4373;
   public static final int CL_IMAGE_DEPTH = 4374;
   public static final int CL_ADDRESS_NONE = 4400;
   public static final int CL_ADDRESS_CLAMP_TO_EDGE = 4401;
   public static final int CL_ADDRESS_CLAMP = 4402;
   public static final int CL_ADDRESS_REPEAT = 4403;
   public static final int CL_FILTER_NEAREST = 4416;
   public static final int CL_FILTER_LINEAR = 4417;
   public static final int CL_SAMPLER_REFERENCE_COUNT = 4432;
   public static final int CL_SAMPLER_CONTEXT = 4433;
   public static final int CL_SAMPLER_NORMALIZED_COORDS = 4434;
   public static final int CL_SAMPLER_ADDRESSING_MODE = 4435;
   public static final int CL_SAMPLER_FILTER_MODE = 4436;
   public static final int CL_MAP_READ = 1;
   public static final int CL_MAP_WRITE = 2;
   public static final int CL_PROGRAM_REFERENCE_COUNT = 4448;
   public static final int CL_PROGRAM_CONTEXT = 4449;
   public static final int CL_PROGRAM_NUM_DEVICES = 4450;
   public static final int CL_PROGRAM_DEVICES = 4451;
   public static final int CL_PROGRAM_SOURCE = 4452;
   public static final int CL_PROGRAM_BINARY_SIZES = 4453;
   public static final int CL_PROGRAM_BINARIES = 4454;
   public static final int CL_PROGRAM_BUILD_STATUS = 4481;
   public static final int CL_PROGRAM_BUILD_OPTIONS = 4482;
   public static final int CL_PROGRAM_BUILD_LOG = 4483;
   public static final int CL_BUILD_SUCCESS = 0;
   public static final int CL_BUILD_NONE = -1;
   public static final int CL_BUILD_ERROR = -2;
   public static final int CL_BUILD_IN_PROGRESS = -3;
   public static final int CL_KERNEL_FUNCTION_NAME = 4496;
   public static final int CL_KERNEL_NUM_ARGS = 4497;
   public static final int CL_KERNEL_REFERENCE_COUNT = 4498;
   public static final int CL_KERNEL_CONTEXT = 4499;
   public static final int CL_KERNEL_PROGRAM = 4500;
   public static final int CL_KERNEL_WORK_GROUP_SIZE = 4528;
   public static final int CL_KERNEL_COMPILE_WORK_GROUP_SIZE = 4529;
   public static final int CL_KERNEL_LOCAL_MEM_SIZE = 4530;
   public static final int CL_EVENT_COMMAND_QUEUE = 4560;
   public static final int CL_EVENT_COMMAND_TYPE = 4561;
   public static final int CL_EVENT_REFERENCE_COUNT = 4562;
   public static final int CL_EVENT_COMMAND_EXECUTION_STATUS = 4563;
   public static final int CL_COMMAND_NDRANGE_KERNEL = 4592;
   public static final int CL_COMMAND_TASK = 4593;
   public static final int CL_COMMAND_NATIVE_KERNEL = 4594;
   public static final int CL_COMMAND_READ_BUFFER = 4595;
   public static final int CL_COMMAND_WRITE_BUFFER = 4596;
   public static final int CL_COMMAND_COPY_BUFFER = 4597;
   public static final int CL_COMMAND_READ_IMAGE = 4598;
   public static final int CL_COMMAND_WRITE_IMAGE = 4599;
   public static final int CL_COMMAND_COPY_IMAGE = 4600;
   public static final int CL_COMMAND_COPY_IMAGE_TO_BUFFER = 4601;
   public static final int CL_COMMAND_COPY_BUFFER_TO_IMAGE = 4602;
   public static final int CL_COMMAND_MAP_BUFFER = 4603;
   public static final int CL_COMMAND_MAP_IMAGE = 4604;
   public static final int CL_COMMAND_UNMAP_MEM_OBJECT = 4605;
   public static final int CL_COMMAND_MARKER = 4606;
   public static final int CL_COMMAND_ACQUIRE_GL_OBJECTS = 4607;
   public static final int CL_COMMAND_RELEASE_GL_OBJECTS = 4608;
   public static final int CL_COMPLETE = 0;
   public static final int CL_RUNNING = 1;
   public static final int CL_SUBMITTED = 2;
   public static final int CL_QUEUED = 3;
   public static final int CL_PROFILING_COMMAND_QUEUED = 4736;
   public static final int CL_PROFILING_COMMAND_SUBMIT = 4737;
   public static final int CL_PROFILING_COMMAND_START = 4738;
   public static final int CL_PROFILING_COMMAND_END = 4739;

   public static int clGetPlatformIDs(PointerBuffer platforms, IntBuffer num_platforms) {
      long function_pointer = CLCapabilities.clGetPlatformIDs;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(platforms != null) {
         BufferChecks.checkDirect(platforms);
      }

      if(num_platforms != null) {
         BufferChecks.checkBuffer((IntBuffer)num_platforms, 1);
      }

      if(num_platforms == null) {
         num_platforms = APIUtil.getBufferInt();
      }

      int __result = nclGetPlatformIDs(platforms == null?0:platforms.remaining(), MemoryUtil.getAddressSafe(platforms), MemoryUtil.getAddressSafe(num_platforms), function_pointer);
      if(__result == 0 && platforms != null) {
         CLPlatform.registerCLPlatforms(platforms, num_platforms);
      }

      return __result;
   }

   static native int nclGetPlatformIDs(int var0, long var1, long var3, long var5);

   public static int clGetPlatformInfo(CLPlatform platform, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetPlatformInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetPlatformInfo(platform == null?0L:platform.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetPlatformInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clGetDeviceIDs(CLPlatform platform, long device_type, PointerBuffer devices, IntBuffer num_devices) {
      long function_pointer = CLCapabilities.clGetDeviceIDs;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(devices != null) {
         BufferChecks.checkDirect(devices);
      }

      if(num_devices != null) {
         BufferChecks.checkBuffer((IntBuffer)num_devices, 1);
      } else {
         num_devices = APIUtil.getBufferInt();
      }

      int __result = nclGetDeviceIDs(platform.getPointer(), device_type, devices == null?0:devices.remaining(), MemoryUtil.getAddressSafe(devices), MemoryUtil.getAddressSafe(num_devices), function_pointer);
      if(__result == 0 && devices != null) {
         platform.registerCLDevices(devices, num_devices);
      }

      return __result;
   }

   static native int nclGetDeviceIDs(long var0, long var2, int var4, long var5, long var7, long var9);

   public static int clGetDeviceInfo(CLDevice device, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetDeviceInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetDeviceInfo(device.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetDeviceInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLContext clCreateContext(PointerBuffer properties, PointerBuffer devices, CLContextCallback pfn_notify, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateContext;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)properties, 3);
      BufferChecks.checkNullTerminated(properties);
      BufferChecks.checkBuffer((PointerBuffer)devices, 1);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      long user_data = pfn_notify != null && !pfn_notify.isCustom()?CallbackUtil.createGlobalRef(pfn_notify):0L;
      CLContext __result = null;

      CLContext var9;
      try {
         __result = new CLContext(nclCreateContext(MemoryUtil.getAddress(properties), devices.remaining(), MemoryUtil.getAddress(devices), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), APIUtil.getCLPlatform(properties));
         var9 = __result;
      } finally {
         if(__result != null) {
            __result.setContextCallback(user_data);
         }

      }

      return var9;
   }

   static native long nclCreateContext(long var0, int var2, long var3, long var5, long var7, long var9, long var11);

   public static CLContext clCreateContext(PointerBuffer properties, CLDevice device, CLContextCallback pfn_notify, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateContext;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)properties, 3);
      BufferChecks.checkNullTerminated(properties);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      long user_data = pfn_notify != null && !pfn_notify.isCustom()?CallbackUtil.createGlobalRef(pfn_notify):0L;
      CLContext __result = null;

      CLContext var9;
      try {
         __result = new CLContext(nclCreateContext(MemoryUtil.getAddress(properties), 1, APIUtil.getPointer(device), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), APIUtil.getCLPlatform(properties));
         var9 = __result;
      } finally {
         if(__result != null) {
            __result.setContextCallback(user_data);
         }

      }

      return var9;
   }

   public static CLContext clCreateContextFromType(PointerBuffer properties, long device_type, CLContextCallback pfn_notify, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateContextFromType;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)properties, 3);
      BufferChecks.checkNullTerminated(properties);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      long user_data = pfn_notify != null && !pfn_notify.isCustom()?CallbackUtil.createGlobalRef(pfn_notify):0L;
      CLContext __result = null;

      CLContext var10;
      try {
         __result = new CLContext(nclCreateContextFromType(MemoryUtil.getAddress(properties), device_type, pfn_notify == null?0L:pfn_notify.getPointer(), user_data, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), APIUtil.getCLPlatform(properties));
         var10 = __result;
      } finally {
         if(__result != null) {
            __result.setContextCallback(user_data);
         }

      }

      return var10;
   }

   static native long nclCreateContextFromType(long var0, long var2, long var4, long var6, long var8, long var10);

   public static int clRetainContext(CLContext context) {
      long function_pointer = CLCapabilities.clRetainContext;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainContext(context.getPointer(), function_pointer);
      if(__result == 0) {
         context.retain();
      }

      return __result;
   }

   static native int nclRetainContext(long var0, long var2);

   public static int clReleaseContext(CLContext context) {
      long function_pointer = CLCapabilities.clReleaseContext;
      BufferChecks.checkFunctionAddress(function_pointer);
      APIUtil.releaseObjects(context);
      int __result = nclReleaseContext(context.getPointer(), function_pointer);
      if(__result == 0) {
         context.releaseImpl();
      }

      return __result;
   }

   static native int nclReleaseContext(long var0, long var2);

   public static int clGetContextInfo(CLContext context, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetContextInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      if(param_value_size_ret == null && APIUtil.isDevicesParam(param_name)) {
         param_value_size_ret = APIUtil.getBufferPointer();
      }

      int __result = nclGetContextInfo(context.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      if(__result == 0 && param_value != null && APIUtil.isDevicesParam(param_name)) {
         ((CLPlatform)context.getParent()).registerCLDevices(param_value, param_value_size_ret);
      }

      return __result;
   }

   static native int nclGetContextInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLCommandQueue clCreateCommandQueue(CLContext context, CLDevice device, long properties, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateCommandQueue;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLCommandQueue __result = new CLCommandQueue(nclCreateCommandQueue(context.getPointer(), device.getPointer(), properties, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context, device);
      return __result;
   }

   static native long nclCreateCommandQueue(long var0, long var2, long var4, long var6, long var8);

   public static int clRetainCommandQueue(CLCommandQueue command_queue) {
      long function_pointer = CLCapabilities.clRetainCommandQueue;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainCommandQueue(command_queue.getPointer(), function_pointer);
      if(__result == 0) {
         command_queue.retain();
      }

      return __result;
   }

   static native int nclRetainCommandQueue(long var0, long var2);

   public static int clReleaseCommandQueue(CLCommandQueue command_queue) {
      long function_pointer = CLCapabilities.clReleaseCommandQueue;
      BufferChecks.checkFunctionAddress(function_pointer);
      APIUtil.releaseObjects(command_queue);
      int __result = nclReleaseCommandQueue(command_queue.getPointer(), function_pointer);
      if(__result == 0) {
         command_queue.release();
      }

      return __result;
   }

   static native int nclReleaseCommandQueue(long var0, long var2);

   public static int clGetCommandQueueInfo(CLCommandQueue command_queue, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetCommandQueueInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetCommandQueueInfo(command_queue.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetCommandQueueInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLMem clCreateBuffer(CLContext context, long flags, long host_ptr_size, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, host_ptr_size, 0L, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, ByteBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)host_ptr.remaining(), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, DoubleBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)(host_ptr.remaining() << 3), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, FloatBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)(host_ptr.remaining() << 2), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, IntBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)(host_ptr.remaining() << 2), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, LongBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)(host_ptr.remaining() << 3), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateBuffer(CLContext context, long flags, ShortBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(host_ptr);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateBuffer(context.getPointer(), flags, (long)(host_ptr.remaining() << 1), MemoryUtil.getAddress(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateBuffer(long var0, long var2, long var4, long var6, long var8, long var10);

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, ByteBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)ptr.remaining(), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, DoubleBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)(ptr.remaining() << 3), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, FloatBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)(ptr.remaining() << 2), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, IntBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)(ptr.remaining() << 2), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, LongBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)(ptr.remaining() << 3), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_read, long offset, ShortBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_read, offset, (long)(ptr.remaining() << 1), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueReadBuffer(long var0, long var2, int var4, long var5, long var7, long var9, int var11, long var12, long var14, long var16);

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, ByteBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)ptr.remaining(), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, DoubleBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)(ptr.remaining() << 3), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, FloatBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)(ptr.remaining() << 2), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, IntBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)(ptr.remaining() << 2), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, LongBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)(ptr.remaining() << 3), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_write, long offset, ShortBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_write, offset, (long)(ptr.remaining() << 1), MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueWriteBuffer(long var0, long var2, int var4, long var5, long var7, long var9, int var11, long var12, long var14, long var16);

   public static int clEnqueueCopyBuffer(CLCommandQueue command_queue, CLMem src_buffer, CLMem dst_buffer, long src_offset, long dst_offset, long size, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueCopyBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueCopyBuffer(command_queue.getPointer(), src_buffer.getPointer(), dst_buffer.getPointer(), src_offset, dst_offset, size, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueCopyBuffer(long var0, long var2, long var4, long var6, long var8, long var10, int var12, long var13, long var15, long var17);

   public static ByteBuffer clEnqueueMapBuffer(CLCommandQueue command_queue, CLMem buffer, int blocking_map, long map_flags, long offset, long size, PointerBuffer event_wait_list, PointerBuffer event, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clEnqueueMapBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      ByteBuffer __result = nclEnqueueMapBuffer(command_queue.getPointer(), buffer.getPointer(), blocking_map, map_flags, offset, size, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), MemoryUtil.getAddressSafe(errcode_ret), size, function_pointer);
      if(__result != null) {
         command_queue.registerCLEvent(event);
      }

      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nclEnqueueMapBuffer(long var0, long var2, int var4, long var5, long var7, long var9, int var11, long var12, long var14, long var16, long var18, long var20);

   public static CLMem clCreateImage2D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_row_pitch, ByteBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage2DSize(image_format, image_width, image_height, image_row_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage2D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_row_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage2D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_row_pitch, FloatBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage2DSize(image_format, image_width, image_height, image_row_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage2D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_row_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage2D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_row_pitch, IntBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage2DSize(image_format, image_width, image_height, image_row_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage2D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_row_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage2D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_row_pitch, ShortBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage2DSize(image_format, image_width, image_height, image_row_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage2D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_row_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateImage2D(long var0, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16);

   public static CLMem clCreateImage3D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, ByteBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage3DSize(image_format, image_width, image_height, image_height, image_row_pitch, image_slice_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage3D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage3D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, FloatBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage3DSize(image_format, image_width, image_height, image_height, image_row_pitch, image_slice_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage3D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage3D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, IntBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage3DSize(image_format, image_width, image_height, image_height, image_row_pitch, image_slice_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage3D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLMem clCreateImage3D(CLContext context, long flags, ByteBuffer image_format, long image_width, long image_height, long image_depth, long image_row_pitch, long image_slice_pitch, ShortBuffer host_ptr, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateImage3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((ByteBuffer)image_format, 8);
      if(host_ptr != null) {
         BufferChecks.checkBuffer(host_ptr, CLChecks.calculateImage3DSize(image_format, image_width, image_height, image_height, image_row_pitch, image_slice_pitch));
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateImage3D(context.getPointer(), flags, MemoryUtil.getAddress(image_format), image_width, image_height, image_depth, image_row_pitch, image_slice_pitch, MemoryUtil.getAddressSafe(host_ptr), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateImage3D(long var0, long var2, long var4, long var6, long var8, long var10, long var12, long var14, long var16, long var18, long var20);

   public static int clGetSupportedImageFormats(CLContext context, long flags, int image_type, ByteBuffer image_formats, IntBuffer num_image_formats) {
      long function_pointer = CLCapabilities.clGetSupportedImageFormats;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(image_formats != null) {
         BufferChecks.checkDirect(image_formats);
      }

      if(num_image_formats != null) {
         BufferChecks.checkBuffer((IntBuffer)num_image_formats, 1);
      }

      int __result = nclGetSupportedImageFormats(context.getPointer(), flags, image_type, (image_formats == null?0:image_formats.remaining()) / 8, MemoryUtil.getAddressSafe(image_formats), MemoryUtil.getAddressSafe(num_image_formats), function_pointer);
      return __result;
   }

   static native int nclGetSupportedImageFormats(long var0, long var2, int var4, int var5, long var6, long var8, long var10);

   public static int clEnqueueReadImage(CLCommandQueue command_queue, CLMem image, int blocking_read, PointerBuffer origin, PointerBuffer region, long row_pitch, long slice_pitch, ByteBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, row_pitch, slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadImage(command_queue.getPointer(), image.getPointer(), blocking_read, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), row_pitch, slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadImage(CLCommandQueue command_queue, CLMem image, int blocking_read, PointerBuffer origin, PointerBuffer region, long row_pitch, long slice_pitch, FloatBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, row_pitch, slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadImage(command_queue.getPointer(), image.getPointer(), blocking_read, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), row_pitch, slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadImage(CLCommandQueue command_queue, CLMem image, int blocking_read, PointerBuffer origin, PointerBuffer region, long row_pitch, long slice_pitch, IntBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, row_pitch, slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadImage(command_queue.getPointer(), image.getPointer(), blocking_read, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), row_pitch, slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReadImage(CLCommandQueue command_queue, CLMem image, int blocking_read, PointerBuffer origin, PointerBuffer region, long row_pitch, long slice_pitch, ShortBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReadImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, row_pitch, slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReadImage(command_queue.getPointer(), image.getPointer(), blocking_read, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), row_pitch, slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueReadImage(long var0, long var2, int var4, long var5, long var7, long var9, long var11, long var13, int var15, long var16, long var18, long var20);

   public static int clEnqueueWriteImage(CLCommandQueue command_queue, CLMem image, int blocking_write, PointerBuffer origin, PointerBuffer region, long input_row_pitch, long input_slice_pitch, ByteBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, input_row_pitch, input_slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteImage(command_queue.getPointer(), image.getPointer(), blocking_write, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), input_row_pitch, input_slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteImage(CLCommandQueue command_queue, CLMem image, int blocking_write, PointerBuffer origin, PointerBuffer region, long input_row_pitch, long input_slice_pitch, FloatBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, input_row_pitch, input_slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteImage(command_queue.getPointer(), image.getPointer(), blocking_write, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), input_row_pitch, input_slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteImage(CLCommandQueue command_queue, CLMem image, int blocking_write, PointerBuffer origin, PointerBuffer region, long input_row_pitch, long input_slice_pitch, IntBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, input_row_pitch, input_slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteImage(command_queue.getPointer(), image.getPointer(), blocking_write, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), input_row_pitch, input_slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueWriteImage(CLCommandQueue command_queue, CLMem image, int blocking_write, PointerBuffer origin, PointerBuffer region, long input_row_pitch, long input_slice_pitch, ShortBuffer ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueWriteImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer(ptr, CLChecks.calculateImageSize(region, input_row_pitch, input_slice_pitch));
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueWriteImage(command_queue.getPointer(), image.getPointer(), blocking_write, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), input_row_pitch, input_slice_pitch, MemoryUtil.getAddress(ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueWriteImage(long var0, long var2, int var4, long var5, long var7, long var9, long var11, long var13, int var15, long var16, long var18, long var20);

   public static int clEnqueueCopyImage(CLCommandQueue command_queue, CLMem src_image, CLMem dst_image, PointerBuffer src_origin, PointerBuffer dst_origin, PointerBuffer region, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueCopyImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)src_origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)dst_origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueCopyImage(command_queue.getPointer(), src_image.getPointer(), dst_image.getPointer(), MemoryUtil.getAddress(src_origin), MemoryUtil.getAddress(dst_origin), MemoryUtil.getAddress(region), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueCopyImage(long var0, long var2, long var4, long var6, long var8, long var10, int var12, long var13, long var15, long var17);

   public static int clEnqueueCopyImageToBuffer(CLCommandQueue command_queue, CLMem src_image, CLMem dst_buffer, PointerBuffer src_origin, PointerBuffer region, long dst_offset, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueCopyImageToBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)src_origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueCopyImageToBuffer(command_queue.getPointer(), src_image.getPointer(), dst_buffer.getPointer(), MemoryUtil.getAddress(src_origin), MemoryUtil.getAddress(region), dst_offset, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueCopyImageToBuffer(long var0, long var2, long var4, long var6, long var8, long var10, int var12, long var13, long var15, long var17);

   public static int clEnqueueCopyBufferToImage(CLCommandQueue command_queue, CLMem src_buffer, CLMem dst_image, long src_offset, PointerBuffer dst_origin, PointerBuffer region, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueCopyBufferToImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)dst_origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueCopyBufferToImage(command_queue.getPointer(), src_buffer.getPointer(), dst_image.getPointer(), src_offset, MemoryUtil.getAddress(dst_origin), MemoryUtil.getAddress(region), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueCopyBufferToImage(long var0, long var2, long var4, long var6, long var8, long var10, int var12, long var13, long var15, long var17);

   public static ByteBuffer clEnqueueMapImage(CLCommandQueue command_queue, CLMem image, int blocking_map, long map_flags, PointerBuffer origin, PointerBuffer region, PointerBuffer image_row_pitch, PointerBuffer image_slice_pitch, PointerBuffer event_wait_list, PointerBuffer event, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clEnqueueMapImage;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)origin, 3);
      BufferChecks.checkBuffer((PointerBuffer)region, 3);
      BufferChecks.checkBuffer((PointerBuffer)image_row_pitch, 1);
      if(image_slice_pitch != null) {
         BufferChecks.checkBuffer((PointerBuffer)image_slice_pitch, 1);
      }

      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      ByteBuffer __result = nclEnqueueMapImage(command_queue.getPointer(), image.getPointer(), blocking_map, map_flags, MemoryUtil.getAddress(origin), MemoryUtil.getAddress(region), MemoryUtil.getAddress(image_row_pitch), MemoryUtil.getAddressSafe(image_slice_pitch), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), MemoryUtil.getAddressSafe(errcode_ret), function_pointer);
      if(__result != null) {
         command_queue.registerCLEvent(event);
      }

      return LWJGLUtil.CHECKS && __result == null?null:__result.order(ByteOrder.nativeOrder());
   }

   static native ByteBuffer nclEnqueueMapImage(long var0, long var2, int var4, long var5, long var7, long var9, long var11, long var13, int var15, long var16, long var18, long var20, long var22);

   public static int clGetImageInfo(CLMem image, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetImageInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetImageInfo(image.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetImageInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clRetainMemObject(CLMem memobj) {
      long function_pointer = CLCapabilities.clRetainMemObject;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainMemObject(memobj.getPointer(), function_pointer);
      if(__result == 0) {
         memobj.retain();
      }

      return __result;
   }

   static native int nclRetainMemObject(long var0, long var2);

   public static int clReleaseMemObject(CLMem memobj) {
      long function_pointer = CLCapabilities.clReleaseMemObject;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclReleaseMemObject(memobj.getPointer(), function_pointer);
      if(__result == 0) {
         memobj.release();
      }

      return __result;
   }

   static native int nclReleaseMemObject(long var0, long var2);

   public static int clEnqueueUnmapMemObject(CLCommandQueue command_queue, CLMem memobj, ByteBuffer mapped_ptr, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueUnmapMemObject;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(mapped_ptr);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueUnmapMemObject(command_queue.getPointer(), memobj.getPointer(), MemoryUtil.getAddress(mapped_ptr), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueUnmapMemObject(long var0, long var2, long var4, int var6, long var7, long var9, long var11);

   public static int clGetMemObjectInfo(CLMem memobj, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetMemObjectInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetMemObjectInfo(memobj.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetMemObjectInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLSampler clCreateSampler(CLContext context, int normalized_coords, int addressing_mode, int filter_mode, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateSampler;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLSampler __result = new CLSampler(nclCreateSampler(context.getPointer(), normalized_coords, addressing_mode, filter_mode, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateSampler(long var0, int var2, int var3, int var4, long var5, long var7);

   public static int clRetainSampler(CLSampler sampler) {
      long function_pointer = CLCapabilities.clRetainSampler;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainSampler(sampler.getPointer(), function_pointer);
      if(__result == 0) {
         sampler.retain();
      }

      return __result;
   }

   static native int nclRetainSampler(long var0, long var2);

   public static int clReleaseSampler(CLSampler sampler) {
      long function_pointer = CLCapabilities.clReleaseSampler;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclReleaseSampler(sampler.getPointer(), function_pointer);
      if(__result == 0) {
         sampler.release();
      }

      return __result;
   }

   static native int nclReleaseSampler(long var0, long var2);

   public static int clGetSamplerInfo(CLSampler sampler, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetSamplerInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetSamplerInfo(sampler.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetSamplerInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLProgram clCreateProgramWithSource(CLContext context, ByteBuffer string, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithSource;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(string);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithSource(context.getPointer(), 1, MemoryUtil.getAddress(string), (long)string.remaining(), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithSource(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLProgram clCreateProgramWithSource(CLContext context, ByteBuffer strings, PointerBuffer lengths, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithSource;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer(strings, APIUtil.getSize(lengths));
      BufferChecks.checkBuffer((PointerBuffer)lengths, 1);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithSource2(context.getPointer(), lengths.remaining(), MemoryUtil.getAddress(strings), MemoryUtil.getAddress(lengths), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithSource2(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLProgram clCreateProgramWithSource(CLContext context, ByteBuffer[] strings, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithSource;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkArray((Object[])strings, 1);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithSource3(context.getPointer(), strings.length, strings, APIUtil.getLengths(strings), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithSource3(long var0, int var2, ByteBuffer[] var3, long var4, long var6, long var8);

   public static CLProgram clCreateProgramWithSource(CLContext context, CharSequence string, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithSource;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithSource(context.getPointer(), 1, APIUtil.getBuffer(string), (long)string.length(), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   public static CLProgram clCreateProgramWithSource(CLContext context, CharSequence[] strings, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithSource;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkArray(strings);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithSource4(context.getPointer(), strings.length, APIUtil.getBuffer(strings), APIUtil.getLengths(strings), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithSource4(long var0, int var2, long var3, long var5, long var7, long var9);

   public static CLProgram clCreateProgramWithBinary(CLContext context, CLDevice device, ByteBuffer binary, IntBuffer binary_status, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithBinary;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(binary);
      BufferChecks.checkBuffer((IntBuffer)binary_status, 1);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithBinary(context.getPointer(), 1, device.getPointer(), (long)binary.remaining(), MemoryUtil.getAddress(binary), MemoryUtil.getAddress(binary_status), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithBinary(long var0, int var2, long var3, long var5, long var7, long var9, long var11, long var13);

   public static CLProgram clCreateProgramWithBinary(CLContext context, PointerBuffer device_list, PointerBuffer lengths, ByteBuffer binaries, IntBuffer binary_status, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithBinary;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)device_list, 1);
      BufferChecks.checkBuffer(lengths, device_list.remaining());
      BufferChecks.checkBuffer(binaries, APIUtil.getSize(lengths));
      BufferChecks.checkBuffer(binary_status, device_list.remaining());
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithBinary2(context.getPointer(), device_list.remaining(), MemoryUtil.getAddress(device_list), MemoryUtil.getAddress(lengths), MemoryUtil.getAddress(binaries), MemoryUtil.getAddress(binary_status), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithBinary2(long var0, int var2, long var3, long var5, long var7, long var9, long var11, long var13);

   public static CLProgram clCreateProgramWithBinary(CLContext context, PointerBuffer device_list, ByteBuffer[] binaries, IntBuffer binary_status, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateProgramWithBinary;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer(device_list, binaries.length);
      BufferChecks.checkArray((Object[])binaries, 1);
      BufferChecks.checkBuffer(binary_status, binaries.length);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLProgram __result = new CLProgram(nclCreateProgramWithBinary3(context.getPointer(), binaries.length, MemoryUtil.getAddress(device_list), APIUtil.getLengths(binaries), binaries, MemoryUtil.getAddress(binary_status), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateProgramWithBinary3(long var0, int var2, long var3, long var5, ByteBuffer[] var7, long var8, long var10, long var12);

   public static int clRetainProgram(CLProgram program) {
      long function_pointer = CLCapabilities.clRetainProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainProgram(program.getPointer(), function_pointer);
      if(__result == 0) {
         program.retain();
      }

      return __result;
   }

   static native int nclRetainProgram(long var0, long var2);

   public static int clReleaseProgram(CLProgram program) {
      long function_pointer = CLCapabilities.clReleaseProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      APIUtil.releaseObjects(program);
      int __result = nclReleaseProgram(program.getPointer(), function_pointer);
      if(__result == 0) {
         program.release();
      }

      return __result;
   }

   static native int nclReleaseProgram(long var0, long var2);

   public static int clBuildProgram(CLProgram program, PointerBuffer device_list, ByteBuffer options, CLBuildProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clBuildProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      BufferChecks.checkDirect(options);
      BufferChecks.checkNullTerminated(options);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var9;
      try {
         __result = nclBuildProgram(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), MemoryUtil.getAddress(options), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var9 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var9;
   }

   static native int nclBuildProgram(long var0, int var2, long var3, long var5, long var7, long var9, long var11);

   public static int clBuildProgram(CLProgram program, PointerBuffer device_list, CharSequence options, CLBuildProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clBuildProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(device_list != null) {
         BufferChecks.checkDirect(device_list);
      }

      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var9;
      try {
         __result = nclBuildProgram(program.getPointer(), device_list == null?0:device_list.remaining(), MemoryUtil.getAddressSafe(device_list), APIUtil.getBufferNT(options), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var9 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var9;
   }

   public static int clBuildProgram(CLProgram program, CLDevice device, CharSequence options, CLBuildProgramCallback pfn_notify) {
      long function_pointer = CLCapabilities.clBuildProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      long user_data = CallbackUtil.createGlobalRef(pfn_notify);
      if(pfn_notify != null) {
         pfn_notify.setContext((CLContext)program.getParent());
      }

      int __result = 0;

      int var9;
      try {
         __result = nclBuildProgram(program.getPointer(), 1, APIUtil.getPointer(device), APIUtil.getBufferNT(options), pfn_notify == null?0L:pfn_notify.getPointer(), user_data, function_pointer);
         var9 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_data);
      }

      return var9;
   }

   public static int clUnloadCompiler() {
      long function_pointer = CLCapabilities.clUnloadCompiler;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclUnloadCompiler(function_pointer);
      return __result;
   }

   static native int nclUnloadCompiler(long var0);

   public static int clGetProgramInfo(CLProgram program, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetProgramInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetProgramInfo(program.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetProgramInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clGetProgramInfo(CLProgram program, PointerBuffer sizes, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetProgramInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)sizes, 1);
      BufferChecks.checkBuffer(param_value, APIUtil.getSize(sizes));
      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetProgramInfo2(program.getPointer(), 4454, (long)sizes.remaining(), MemoryUtil.getAddress(sizes), MemoryUtil.getAddress(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetProgramInfo2(long var0, int var2, long var3, long var5, long var7, long var9, long var11);

   public static int clGetProgramInfo(CLProgram program, ByteBuffer[] param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetProgramInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkArray(param_value);
      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetProgramInfo3(program.getPointer(), 4454, (long)param_value.length, param_value, MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetProgramInfo3(long var0, int var2, long var3, ByteBuffer[] var5, long var6, long var8);

   public static int clGetProgramBuildInfo(CLProgram program, CLDevice device, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetProgramBuildInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetProgramBuildInfo(program.getPointer(), device.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetProgramBuildInfo(long var0, long var2, int var4, long var5, long var7, long var9, long var11);

   public static CLKernel clCreateKernel(CLProgram program, ByteBuffer kernel_name, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(kernel_name);
      BufferChecks.checkNullTerminated(kernel_name);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLKernel __result = new CLKernel(nclCreateKernel(program.getPointer(), MemoryUtil.getAddress(kernel_name), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), program);
      return __result;
   }

   static native long nclCreateKernel(long var0, long var2, long var4, long var6);

   public static CLKernel clCreateKernel(CLProgram program, CharSequence kernel_name, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLKernel __result = new CLKernel(nclCreateKernel(program.getPointer(), APIUtil.getBufferNT(kernel_name), MemoryUtil.getAddressSafe(errcode_ret), function_pointer), program);
      return __result;
   }

   public static int clCreateKernelsInProgram(CLProgram program, PointerBuffer kernels, IntBuffer num_kernels_ret) {
      long function_pointer = CLCapabilities.clCreateKernelsInProgram;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(kernels != null) {
         BufferChecks.checkDirect(kernels);
      }

      if(num_kernels_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)num_kernels_ret, 1);
      }

      int __result = nclCreateKernelsInProgram(program.getPointer(), kernels == null?0:kernels.remaining(), MemoryUtil.getAddressSafe(kernels), MemoryUtil.getAddressSafe(num_kernels_ret), function_pointer);
      if(__result == 0 && kernels != null) {
         program.registerCLKernels(kernels);
      }

      return __result;
   }

   static native int nclCreateKernelsInProgram(long var0, int var2, long var3, long var5, long var7);

   public static int clRetainKernel(CLKernel kernel) {
      long function_pointer = CLCapabilities.clRetainKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainKernel(kernel.getPointer(), function_pointer);
      if(__result == 0) {
         kernel.retain();
      }

      return __result;
   }

   static native int nclRetainKernel(long var0, long var2);

   public static int clReleaseKernel(CLKernel kernel) {
      long function_pointer = CLCapabilities.clReleaseKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclReleaseKernel(kernel.getPointer(), function_pointer);
      if(__result == 0) {
         kernel.release();
      }

      return __result;
   }

   static native int nclReleaseKernel(long var0, long var2);

   public static int clSetKernelArg(CLKernel kernel, int arg_index, long arg_value_arg_size) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, arg_value_arg_size, 0L, function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, ByteBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)arg_value.remaining(), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, DoubleBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)(arg_value.remaining() << 3), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, FloatBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)(arg_value.remaining() << 2), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, IntBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)(arg_value.remaining() << 2), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, LongBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)(arg_value.remaining() << 3), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   public static int clSetKernelArg(CLKernel kernel, int arg_index, ShortBuffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(arg_value);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)(arg_value.remaining() << 1), MemoryUtil.getAddress(arg_value), function_pointer);
      return __result;
   }

   static native int nclSetKernelArg(long var0, int var2, long var3, long var5, long var7);

   public static int clSetKernelArg(CLKernel kernel, int arg_index, CLObject arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, (long)PointerBuffer.getPointerSize(), APIUtil.getPointerSafe(arg_value), function_pointer);
      return __result;
   }

   static int clSetKernelArg(CLKernel kernel, int arg_index, long arg_size, Buffer arg_value) {
      long function_pointer = CLCapabilities.clSetKernelArg;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclSetKernelArg(kernel.getPointer(), arg_index, arg_size, MemoryUtil.getAddress0(arg_value), function_pointer);
      return __result;
   }

   public static int clGetKernelInfo(CLKernel kernel, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetKernelInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetKernelInfo(kernel.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetKernelInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clGetKernelWorkGroupInfo(CLKernel kernel, CLDevice device, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetKernelWorkGroupInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetKernelWorkGroupInfo(kernel.getPointer(), device == null?0L:device.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetKernelWorkGroupInfo(long var0, long var2, int var4, long var5, long var7, long var9, long var11);

   public static int clEnqueueNDRangeKernel(CLCommandQueue command_queue, CLKernel kernel, int work_dim, PointerBuffer global_work_offset, PointerBuffer global_work_size, PointerBuffer local_work_size, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueNDRangeKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(global_work_offset != null) {
         BufferChecks.checkBuffer(global_work_offset, work_dim);
      }

      if(global_work_size != null) {
         BufferChecks.checkBuffer(global_work_size, work_dim);
      }

      if(local_work_size != null) {
         BufferChecks.checkBuffer(local_work_size, work_dim);
      }

      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueNDRangeKernel(command_queue.getPointer(), kernel.getPointer(), work_dim, MemoryUtil.getAddressSafe(global_work_offset), MemoryUtil.getAddressSafe(global_work_size), MemoryUtil.getAddressSafe(local_work_size), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueNDRangeKernel(long var0, long var2, int var4, long var5, long var7, long var9, int var11, long var12, long var14, long var16);

   public static int clEnqueueTask(CLCommandQueue command_queue, CLKernel kernel, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueTask;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueTask(command_queue.getPointer(), kernel.getPointer(), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueTask(long var0, long var2, int var4, long var5, long var7, long var9);

   public static int clEnqueueNativeKernel(CLCommandQueue command_queue, CLNativeKernel user_func, CLMem[] mem_list, long[] sizes, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueNativeKernel;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(mem_list != null) {
         BufferChecks.checkArray((Object[])mem_list, 1);
      }

      if(sizes != null) {
         BufferChecks.checkArray(sizes, mem_list.length);
      }

      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      long user_func_ref = CallbackUtil.createGlobalRef(user_func);
      ByteBuffer args = APIUtil.getNativeKernelArgs(user_func_ref, mem_list, sizes);
      int __result = 0;

      int var12;
      try {
         __result = nclEnqueueNativeKernel(command_queue.getPointer(), user_func.getPointer(), MemoryUtil.getAddress0((Buffer)args), (long)args.remaining(), mem_list == null?0:mem_list.length, mem_list, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
         if(__result == 0) {
            command_queue.registerCLEvent(event);
         }

         var12 = __result;
      } finally {
         CallbackUtil.checkCallback(__result, user_func_ref);
      }

      return var12;
   }

   static native int nclEnqueueNativeKernel(long var0, long var2, long var4, long var6, int var8, CLMem[] var9, int var10, long var11, long var13, long var15);

   public static int clWaitForEvents(PointerBuffer event_list) {
      long function_pointer = CLCapabilities.clWaitForEvents;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)event_list, 1);
      int __result = nclWaitForEvents(event_list.remaining(), MemoryUtil.getAddress(event_list), function_pointer);
      return __result;
   }

   static native int nclWaitForEvents(int var0, long var1, long var3);

   public static int clWaitForEvents(CLEvent event) {
      long function_pointer = CLCapabilities.clWaitForEvents;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclWaitForEvents(1, APIUtil.getPointer(event), function_pointer);
      return __result;
   }

   public static int clGetEventInfo(CLEvent event, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetEventInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetEventInfo(event.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetEventInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clRetainEvent(CLEvent event) {
      long function_pointer = CLCapabilities.clRetainEvent;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclRetainEvent(event.getPointer(), function_pointer);
      if(__result == 0) {
         event.retain();
      }

      return __result;
   }

   static native int nclRetainEvent(long var0, long var2);

   public static int clReleaseEvent(CLEvent event) {
      long function_pointer = CLCapabilities.clReleaseEvent;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclReleaseEvent(event.getPointer(), function_pointer);
      if(__result == 0) {
         event.release();
      }

      return __result;
   }

   static native int nclReleaseEvent(long var0, long var2);

   public static int clEnqueueMarker(CLCommandQueue command_queue, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueMarker;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)event, 1);
      int __result = nclEnqueueMarker(command_queue.getPointer(), MemoryUtil.getAddress(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueMarker(long var0, long var2, long var4);

   public static int clEnqueueBarrier(CLCommandQueue command_queue) {
      long function_pointer = CLCapabilities.clEnqueueBarrier;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclEnqueueBarrier(command_queue.getPointer(), function_pointer);
      return __result;
   }

   static native int nclEnqueueBarrier(long var0, long var2);

   public static int clEnqueueWaitForEvents(CLCommandQueue command_queue, PointerBuffer event_list) {
      long function_pointer = CLCapabilities.clEnqueueWaitForEvents;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)event_list, 1);
      int __result = nclEnqueueWaitForEvents(command_queue.getPointer(), event_list.remaining(), MemoryUtil.getAddress(event_list), function_pointer);
      return __result;
   }

   static native int nclEnqueueWaitForEvents(long var0, int var2, long var3, long var5);

   public static int clEnqueueWaitForEvents(CLCommandQueue command_queue, CLEvent event) {
      long function_pointer = CLCapabilities.clEnqueueWaitForEvents;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclEnqueueWaitForEvents(command_queue.getPointer(), 1, APIUtil.getPointer(event), function_pointer);
      return __result;
   }

   public static int clGetEventProfilingInfo(CLEvent event, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetEventProfilingInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetEventProfilingInfo(event.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetEventProfilingInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clFlush(CLCommandQueue command_queue) {
      long function_pointer = CLCapabilities.clFlush;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclFlush(command_queue.getPointer(), function_pointer);
      return __result;
   }

   static native int nclFlush(long var0, long var2);

   public static int clFinish(CLCommandQueue command_queue) {
      long function_pointer = CLCapabilities.clFinish;
      BufferChecks.checkFunctionAddress(function_pointer);
      int __result = nclFinish(command_queue.getPointer(), function_pointer);
      return __result;
   }

   static native int nclFinish(long var0, long var2);

   static CLFunctionAddress clGetExtensionFunctionAddress(ByteBuffer func_name) {
      long function_pointer = CLCapabilities.clGetExtensionFunctionAddress;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkDirect(func_name);
      BufferChecks.checkNullTerminated(func_name);
      CLFunctionAddress __result = new CLFunctionAddress(nclGetExtensionFunctionAddress(MemoryUtil.getAddress(func_name), function_pointer));
      return __result;
   }

   static native long nclGetExtensionFunctionAddress(long var0, long var2);

   static CLFunctionAddress clGetExtensionFunctionAddress(CharSequence func_name) {
      long function_pointer = CLCapabilities.clGetExtensionFunctionAddress;
      BufferChecks.checkFunctionAddress(function_pointer);
      CLFunctionAddress __result = new CLFunctionAddress(nclGetExtensionFunctionAddress(APIUtil.getBufferNT(func_name), function_pointer));
      return __result;
   }
}
