package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLMem;

public final class CL10GL {
   public static final int CL_GL_OBJECT_BUFFER = 8192;
   public static final int CL_GL_OBJECT_TEXTURE2D = 8193;
   public static final int CL_GL_OBJECT_TEXTURE3D = 8194;
   public static final int CL_GL_OBJECT_RENDERBUFFER = 8195;
   public static final int CL_GL_TEXTURE_TARGET = 8196;
   public static final int CL_GL_MIPMAP_LEVEL = 8197;

   public static CLMem clCreateFromGLBuffer(CLContext context, long flags, int bufobj, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateFromGLBuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateFromGLBuffer(context.getPointer(), flags, bufobj, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateFromGLBuffer(long var0, long var2, int var4, long var5, long var7);

   public static CLMem clCreateFromGLTexture2D(CLContext context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateFromGLTexture2D;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateFromGLTexture2D(context.getPointer(), flags, target, miplevel, texture, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateFromGLTexture2D(long var0, long var2, int var4, int var5, int var6, long var7, long var9);

   public static CLMem clCreateFromGLTexture3D(CLContext context, long flags, int target, int miplevel, int texture, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateFromGLTexture3D;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateFromGLTexture3D(context.getPointer(), flags, target, miplevel, texture, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateFromGLTexture3D(long var0, long var2, int var4, int var5, int var6, long var7, long var9);

   public static CLMem clCreateFromGLRenderbuffer(CLContext context, long flags, int renderbuffer, IntBuffer errcode_ret) {
      long function_pointer = CLCapabilities.clCreateFromGLRenderbuffer;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(errcode_ret != null) {
         BufferChecks.checkBuffer((IntBuffer)errcode_ret, 1);
      }

      CLMem __result = new CLMem(nclCreateFromGLRenderbuffer(context.getPointer(), flags, renderbuffer, MemoryUtil.getAddressSafe(errcode_ret), function_pointer), context);
      return __result;
   }

   static native long nclCreateFromGLRenderbuffer(long var0, long var2, int var4, long var5, long var7);

   public static int clGetGLObjectInfo(CLMem memobj, IntBuffer gl_object_type, IntBuffer gl_object_name) {
      long function_pointer = CLCapabilities.clGetGLObjectInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(gl_object_type != null) {
         BufferChecks.checkBuffer((IntBuffer)gl_object_type, 1);
      }

      if(gl_object_name != null) {
         BufferChecks.checkBuffer((IntBuffer)gl_object_name, 1);
      }

      int __result = nclGetGLObjectInfo(memobj.getPointer(), MemoryUtil.getAddressSafe(gl_object_type), MemoryUtil.getAddressSafe(gl_object_name), function_pointer);
      return __result;
   }

   static native int nclGetGLObjectInfo(long var0, long var2, long var4, long var6);

   public static int clGetGLTextureInfo(CLMem memobj, int param_name, ByteBuffer param_value, PointerBuffer param_value_size_ret) {
      long function_pointer = CLCapabilities.clGetGLTextureInfo;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(param_value != null) {
         BufferChecks.checkDirect(param_value);
      }

      if(param_value_size_ret != null) {
         BufferChecks.checkBuffer((PointerBuffer)param_value_size_ret, 1);
      }

      int __result = nclGetGLTextureInfo(memobj.getPointer(), param_name, (long)(param_value == null?0:param_value.remaining()), MemoryUtil.getAddressSafe(param_value), MemoryUtil.getAddressSafe(param_value_size_ret), function_pointer);
      return __result;
   }

   static native int nclGetGLTextureInfo(long var0, int var2, long var3, long var5, long var7, long var9);

   public static int clEnqueueAcquireGLObjects(CLCommandQueue command_queue, PointerBuffer mem_objects, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueAcquireGLObjects;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)mem_objects, 1);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueAcquireGLObjects(command_queue.getPointer(), mem_objects.remaining(), MemoryUtil.getAddress(mem_objects), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueAcquireGLObjects(long var0, int var2, long var3, int var5, long var6, long var8, long var10);

   public static int clEnqueueAcquireGLObjects(CLCommandQueue command_queue, CLMem mem_object, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueAcquireGLObjects;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueAcquireGLObjects(command_queue.getPointer(), 1, APIUtil.getPointer(mem_object), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   public static int clEnqueueReleaseGLObjects(CLCommandQueue command_queue, PointerBuffer mem_objects, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReleaseGLObjects;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)mem_objects, 1);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReleaseGLObjects(command_queue.getPointer(), mem_objects.remaining(), MemoryUtil.getAddress(mem_objects), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueReleaseGLObjects(long var0, int var2, long var3, int var5, long var6, long var8, long var10);

   public static int clEnqueueReleaseGLObjects(CLCommandQueue command_queue, CLMem mem_object, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueReleaseGLObjects;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueReleaseGLObjects(command_queue.getPointer(), 1, APIUtil.getPointer(mem_object), event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }
}
