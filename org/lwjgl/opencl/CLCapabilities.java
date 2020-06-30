package org.lwjgl.opencl;

import org.lwjgl.opencl.CL;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLDeviceCapabilities;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.CLPlatformCapabilities;

public final class CLCapabilities {
   static final boolean CL_APPLE_ContextLoggingFunctions = isAPPLE_ContextLoggingFunctionsSupported();
   static final long clLogMessagesToSystemLogAPPLE = CL.getFunctionAddress("clLogMessagesToSystemLogAPPLE");
   static final long clLogMessagesToStdoutAPPLE = CL.getFunctionAddress("clLogMessagesToStdoutAPPLE");
   static final long clLogMessagesToStderrAPPLE = CL.getFunctionAddress("clLogMessagesToStderrAPPLE");
   static final boolean CL_APPLE_SetMemObjectDestructor = isAPPLE_SetMemObjectDestructorSupported();
   static final long clSetMemObjectDestructorAPPLE = CL.getFunctionAddress("clSetMemObjectDestructorAPPLE");
   static final boolean CL_APPLE_gl_sharing = isAPPLE_gl_sharingSupported();
   static final long clGetGLContextInfoAPPLE = CL.getFunctionAddress("clGetGLContextInfoAPPLE");
   static final boolean OpenCL10 = isCL10Supported();
   static final long clGetPlatformIDs = CL.getFunctionAddress("clGetPlatformIDs");
   static final long clGetPlatformInfo = CL.getFunctionAddress("clGetPlatformInfo");
   static final long clGetDeviceIDs = CL.getFunctionAddress("clGetDeviceIDs");
   static final long clGetDeviceInfo = CL.getFunctionAddress("clGetDeviceInfo");
   static final long clCreateContext = CL.getFunctionAddress("clCreateContext");
   static final long clCreateContextFromType = CL.getFunctionAddress("clCreateContextFromType");
   static final long clRetainContext = CL.getFunctionAddress("clRetainContext");
   static final long clReleaseContext = CL.getFunctionAddress("clReleaseContext");
   static final long clGetContextInfo = CL.getFunctionAddress("clGetContextInfo");
   static final long clCreateCommandQueue = CL.getFunctionAddress("clCreateCommandQueue");
   static final long clRetainCommandQueue = CL.getFunctionAddress("clRetainCommandQueue");
   static final long clReleaseCommandQueue = CL.getFunctionAddress("clReleaseCommandQueue");
   static final long clGetCommandQueueInfo = CL.getFunctionAddress("clGetCommandQueueInfo");
   static final long clCreateBuffer = CL.getFunctionAddress("clCreateBuffer");
   static final long clEnqueueReadBuffer = CL.getFunctionAddress("clEnqueueReadBuffer");
   static final long clEnqueueWriteBuffer = CL.getFunctionAddress("clEnqueueWriteBuffer");
   static final long clEnqueueCopyBuffer = CL.getFunctionAddress("clEnqueueCopyBuffer");
   static final long clEnqueueMapBuffer = CL.getFunctionAddress("clEnqueueMapBuffer");
   static final long clCreateImage2D = CL.getFunctionAddress("clCreateImage2D");
   static final long clCreateImage3D = CL.getFunctionAddress("clCreateImage3D");
   static final long clGetSupportedImageFormats = CL.getFunctionAddress("clGetSupportedImageFormats");
   static final long clEnqueueReadImage = CL.getFunctionAddress("clEnqueueReadImage");
   static final long clEnqueueWriteImage = CL.getFunctionAddress("clEnqueueWriteImage");
   static final long clEnqueueCopyImage = CL.getFunctionAddress("clEnqueueCopyImage");
   static final long clEnqueueCopyImageToBuffer = CL.getFunctionAddress("clEnqueueCopyImageToBuffer");
   static final long clEnqueueCopyBufferToImage = CL.getFunctionAddress("clEnqueueCopyBufferToImage");
   static final long clEnqueueMapImage = CL.getFunctionAddress("clEnqueueMapImage");
   static final long clGetImageInfo = CL.getFunctionAddress("clGetImageInfo");
   static final long clRetainMemObject = CL.getFunctionAddress("clRetainMemObject");
   static final long clReleaseMemObject = CL.getFunctionAddress("clReleaseMemObject");
   static final long clEnqueueUnmapMemObject = CL.getFunctionAddress("clEnqueueUnmapMemObject");
   static final long clGetMemObjectInfo = CL.getFunctionAddress("clGetMemObjectInfo");
   static final long clCreateSampler = CL.getFunctionAddress("clCreateSampler");
   static final long clRetainSampler = CL.getFunctionAddress("clRetainSampler");
   static final long clReleaseSampler = CL.getFunctionAddress("clReleaseSampler");
   static final long clGetSamplerInfo = CL.getFunctionAddress("clGetSamplerInfo");
   static final long clCreateProgramWithSource = CL.getFunctionAddress("clCreateProgramWithSource");
   static final long clCreateProgramWithBinary = CL.getFunctionAddress("clCreateProgramWithBinary");
   static final long clRetainProgram = CL.getFunctionAddress("clRetainProgram");
   static final long clReleaseProgram = CL.getFunctionAddress("clReleaseProgram");
   static final long clBuildProgram = CL.getFunctionAddress("clBuildProgram");
   static final long clUnloadCompiler = CL.getFunctionAddress("clUnloadCompiler");
   static final long clGetProgramInfo = CL.getFunctionAddress("clGetProgramInfo");
   static final long clGetProgramBuildInfo = CL.getFunctionAddress("clGetProgramBuildInfo");
   static final long clCreateKernel = CL.getFunctionAddress("clCreateKernel");
   static final long clCreateKernelsInProgram = CL.getFunctionAddress("clCreateKernelsInProgram");
   static final long clRetainKernel = CL.getFunctionAddress("clRetainKernel");
   static final long clReleaseKernel = CL.getFunctionAddress("clReleaseKernel");
   static final long clSetKernelArg = CL.getFunctionAddress("clSetKernelArg");
   static final long clGetKernelInfo = CL.getFunctionAddress("clGetKernelInfo");
   static final long clGetKernelWorkGroupInfo = CL.getFunctionAddress("clGetKernelWorkGroupInfo");
   static final long clEnqueueNDRangeKernel = CL.getFunctionAddress("clEnqueueNDRangeKernel");
   static final long clEnqueueTask = CL.getFunctionAddress("clEnqueueTask");
   static final long clEnqueueNativeKernel = CL.getFunctionAddress("clEnqueueNativeKernel");
   static final long clWaitForEvents = CL.getFunctionAddress("clWaitForEvents");
   static final long clGetEventInfo = CL.getFunctionAddress("clGetEventInfo");
   static final long clRetainEvent = CL.getFunctionAddress("clRetainEvent");
   static final long clReleaseEvent = CL.getFunctionAddress("clReleaseEvent");
   static final long clEnqueueMarker = CL.getFunctionAddress("clEnqueueMarker");
   static final long clEnqueueBarrier = CL.getFunctionAddress("clEnqueueBarrier");
   static final long clEnqueueWaitForEvents = CL.getFunctionAddress("clEnqueueWaitForEvents");
   static final long clGetEventProfilingInfo = CL.getFunctionAddress("clGetEventProfilingInfo");
   static final long clFlush = CL.getFunctionAddress("clFlush");
   static final long clFinish = CL.getFunctionAddress("clFinish");
   static final long clGetExtensionFunctionAddress = CL.getFunctionAddress("clGetExtensionFunctionAddress");
   static final boolean OpenCL10GL = isCL10GLSupported();
   static final long clCreateFromGLBuffer = CL.getFunctionAddress("clCreateFromGLBuffer");
   static final long clCreateFromGLTexture2D = CL.getFunctionAddress("clCreateFromGLTexture2D");
   static final long clCreateFromGLTexture3D = CL.getFunctionAddress("clCreateFromGLTexture3D");
   static final long clCreateFromGLRenderbuffer = CL.getFunctionAddress("clCreateFromGLRenderbuffer");
   static final long clGetGLObjectInfo = CL.getFunctionAddress("clGetGLObjectInfo");
   static final long clGetGLTextureInfo = CL.getFunctionAddress("clGetGLTextureInfo");
   static final long clEnqueueAcquireGLObjects = CL.getFunctionAddress("clEnqueueAcquireGLObjects");
   static final long clEnqueueReleaseGLObjects = CL.getFunctionAddress("clEnqueueReleaseGLObjects");
   static final boolean OpenCL11 = isCL11Supported();
   static final long clCreateSubBuffer = CL.getFunctionAddress("clCreateSubBuffer");
   static final long clSetMemObjectDestructorCallback = CL.getFunctionAddress("clSetMemObjectDestructorCallback");
   static final long clEnqueueReadBufferRect = CL.getFunctionAddress("clEnqueueReadBufferRect");
   static final long clEnqueueWriteBufferRect = CL.getFunctionAddress("clEnqueueWriteBufferRect");
   static final long clEnqueueCopyBufferRect = CL.getFunctionAddress("clEnqueueCopyBufferRect");
   static final long clCreateUserEvent = CL.getFunctionAddress("clCreateUserEvent");
   static final long clSetUserEventStatus = CL.getFunctionAddress("clSetUserEventStatus");
   static final long clSetEventCallback = CL.getFunctionAddress("clSetEventCallback");
   static final boolean OpenCL12 = isCL12Supported();
   static final long clRetainDevice = CL.getFunctionAddress("clRetainDevice");
   static final long clReleaseDevice = CL.getFunctionAddress("clReleaseDevice");
   static final long clCreateSubDevices = CL.getFunctionAddress("clCreateSubDevices");
   static final long clCreateImage = CL.getFunctionAddress("clCreateImage");
   static final long clCreateProgramWithBuiltInKernels = CL.getFunctionAddress("clCreateProgramWithBuiltInKernels");
   static final long clCompileProgram = CL.getFunctionAddress("clCompileProgram");
   static final long clLinkProgram = CL.getFunctionAddress("clLinkProgram");
   static final long clUnloadPlatformCompiler = CL.getFunctionAddress("clUnloadPlatformCompiler");
   static final long clGetKernelArgInfo = CL.getFunctionAddress("clGetKernelArgInfo");
   static final long clEnqueueFillBuffer = CL.getFunctionAddress("clEnqueueFillBuffer");
   static final long clEnqueueFillImage = CL.getFunctionAddress("clEnqueueFillImage");
   static final long clEnqueueMigrateMemObjects = CL.getFunctionAddress("clEnqueueMigrateMemObjects");
   static final long clEnqueueMarkerWithWaitList = CL.getFunctionAddress("clEnqueueMarkerWithWaitList");
   static final long clEnqueueBarrierWithWaitList = CL.getFunctionAddress("clEnqueueBarrierWithWaitList");
   static final long clSetPrintfCallback = CL.getFunctionAddress("clSetPrintfCallback");
   static final long clGetExtensionFunctionAddressForPlatform = CL.getFunctionAddress("clGetExtensionFunctionAddressForPlatform");
   static final boolean OpenCL12GL = isCL12GLSupported();
   static final long clCreateFromGLTexture = CL.getFunctionAddress("clCreateFromGLTexture");
   static final boolean CL_EXT_device_fission = isEXT_device_fissionSupported();
   static final long clRetainDeviceEXT = CL.getFunctionAddress("clRetainDeviceEXT");
   static final long clReleaseDeviceEXT = CL.getFunctionAddress("clReleaseDeviceEXT");
   static final long clCreateSubDevicesEXT = CL.getFunctionAddress("clCreateSubDevicesEXT");
   static final boolean CL_EXT_migrate_memobject = isEXT_migrate_memobjectSupported();
   static final long clEnqueueMigrateMemObjectEXT = CL.getFunctionAddress("clEnqueueMigrateMemObjectEXT");
   static final boolean CL_KHR_gl_event = isKHR_gl_eventSupported();
   static final long clCreateEventFromGLsyncKHR = CL.getFunctionAddress("clCreateEventFromGLsyncKHR");
   static final boolean CL_KHR_gl_sharing = isKHR_gl_sharingSupported();
   static final long clGetGLContextInfoKHR = CL.getFunctionAddress("clGetGLContextInfoKHR");
   static final boolean CL_KHR_icd = isKHR_icdSupported();
   static final long clIcdGetPlatformIDsKHR = CL.getFunctionAddress("clIcdGetPlatformIDsKHR");
   static final boolean CL_KHR_subgroups = isKHR_subgroupsSupported();
   static final long clGetKernelSubGroupInfoKHR = CL.getFunctionAddress("clGetKernelSubGroupInfoKHR");
   static final boolean CL_KHR_terminate_context = isKHR_terminate_contextSupported();
   static final long clTerminateContextKHR = CL.getFunctionAddress("clTerminateContextKHR");

   public static CLPlatformCapabilities getPlatformCapabilities(CLPlatform platform) {
      platform.checkValid();
      CLPlatformCapabilities caps = (CLPlatformCapabilities)platform.getCapabilities();
      if(caps == null) {
         platform.setCapabilities(caps = new CLPlatformCapabilities(platform));
      }

      return caps;
   }

   public static CLDeviceCapabilities getDeviceCapabilities(CLDevice device) {
      device.checkValid();
      CLDeviceCapabilities caps = (CLDeviceCapabilities)device.getCapabilities();
      if(caps == null) {
         device.setCapabilities(caps = new CLDeviceCapabilities(device));
      }

      return caps;
   }

   private static boolean isAPPLE_ContextLoggingFunctionsSupported() {
      return clLogMessagesToSystemLogAPPLE != 0L & clLogMessagesToStdoutAPPLE != 0L & clLogMessagesToStderrAPPLE != 0L;
   }

   private static boolean isAPPLE_SetMemObjectDestructorSupported() {
      return clSetMemObjectDestructorAPPLE != 0L;
   }

   private static boolean isAPPLE_gl_sharingSupported() {
      return clGetGLContextInfoAPPLE != 0L;
   }

   private static boolean isCL10Supported() {
      return clGetPlatformIDs != 0L & clGetPlatformInfo != 0L & clGetDeviceIDs != 0L & clGetDeviceInfo != 0L & clCreateContext != 0L & clCreateContextFromType != 0L & clRetainContext != 0L & clReleaseContext != 0L & clGetContextInfo != 0L & clCreateCommandQueue != 0L & clRetainCommandQueue != 0L & clReleaseCommandQueue != 0L & clGetCommandQueueInfo != 0L & clCreateBuffer != 0L & clEnqueueReadBuffer != 0L & clEnqueueWriteBuffer != 0L & clEnqueueCopyBuffer != 0L & clEnqueueMapBuffer != 0L & clCreateImage2D != 0L & clCreateImage3D != 0L & clGetSupportedImageFormats != 0L & clEnqueueReadImage != 0L & clEnqueueWriteImage != 0L & clEnqueueCopyImage != 0L & clEnqueueCopyImageToBuffer != 0L & clEnqueueCopyBufferToImage != 0L & clEnqueueMapImage != 0L & clGetImageInfo != 0L & clRetainMemObject != 0L & clReleaseMemObject != 0L & clEnqueueUnmapMemObject != 0L & clGetMemObjectInfo != 0L & clCreateSampler != 0L & clRetainSampler != 0L & clReleaseSampler != 0L & clGetSamplerInfo != 0L & clCreateProgramWithSource != 0L & clCreateProgramWithBinary != 0L & clRetainProgram != 0L & clReleaseProgram != 0L & clBuildProgram != 0L & clUnloadCompiler != 0L & clGetProgramInfo != 0L & clGetProgramBuildInfo != 0L & clCreateKernel != 0L & clCreateKernelsInProgram != 0L & clRetainKernel != 0L & clReleaseKernel != 0L & clSetKernelArg != 0L & clGetKernelInfo != 0L & clGetKernelWorkGroupInfo != 0L & clEnqueueNDRangeKernel != 0L & clEnqueueTask != 0L & clEnqueueNativeKernel != 0L & clWaitForEvents != 0L & clGetEventInfo != 0L & clRetainEvent != 0L & clReleaseEvent != 0L & clEnqueueMarker != 0L & clEnqueueBarrier != 0L & clEnqueueWaitForEvents != 0L & clGetEventProfilingInfo != 0L & clFlush != 0L & clFinish != 0L & clGetExtensionFunctionAddress != 0L;
   }

   private static boolean isCL10GLSupported() {
      return clCreateFromGLBuffer != 0L & clCreateFromGLTexture2D != 0L & clCreateFromGLTexture3D != 0L & clCreateFromGLRenderbuffer != 0L & clGetGLObjectInfo != 0L & clGetGLTextureInfo != 0L & clEnqueueAcquireGLObjects != 0L & clEnqueueReleaseGLObjects != 0L;
   }

   private static boolean isCL11Supported() {
      return clCreateSubBuffer != 0L & clSetMemObjectDestructorCallback != 0L & clEnqueueReadBufferRect != 0L & clEnqueueWriteBufferRect != 0L & clEnqueueCopyBufferRect != 0L & clCreateUserEvent != 0L & clSetUserEventStatus != 0L & clSetEventCallback != 0L;
   }

   private static boolean isCL12Supported() {
      boolean var10000 = clRetainDevice != 0L & clReleaseDevice != 0L & clCreateSubDevices != 0L & clCreateImage != 0L & clCreateProgramWithBuiltInKernels != 0L & clCompileProgram != 0L & clLinkProgram != 0L & clUnloadPlatformCompiler != 0L & clGetKernelArgInfo != 0L & clEnqueueFillBuffer != 0L & clEnqueueFillImage != 0L & clEnqueueMigrateMemObjects != 0L & clEnqueueMarkerWithWaitList != 0L & clEnqueueBarrierWithWaitList != 0L;
      if(clSetPrintfCallback == 0L) {
         ;
      }

      return var10000 & true & clGetExtensionFunctionAddressForPlatform != 0L;
   }

   private static boolean isCL12GLSupported() {
      return clCreateFromGLTexture != 0L;
   }

   private static boolean isEXT_device_fissionSupported() {
      return clRetainDeviceEXT != 0L & clReleaseDeviceEXT != 0L & clCreateSubDevicesEXT != 0L;
   }

   private static boolean isEXT_migrate_memobjectSupported() {
      return clEnqueueMigrateMemObjectEXT != 0L;
   }

   private static boolean isKHR_gl_eventSupported() {
      return clCreateEventFromGLsyncKHR != 0L;
   }

   private static boolean isKHR_gl_sharingSupported() {
      return clGetGLContextInfoKHR != 0L;
   }

   private static boolean isKHR_icdSupported() {
      if(clIcdGetPlatformIDsKHR == 0L) {
         ;
      }

      return true;
   }

   private static boolean isKHR_subgroupsSupported() {
      return clGetKernelSubGroupInfoKHR != 0L;
   }

   private static boolean isKHR_terminate_contextSupported() {
      return clTerminateContextKHR != 0L;
   }
}
