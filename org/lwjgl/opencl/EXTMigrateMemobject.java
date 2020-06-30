package org.lwjgl.opencl;

import org.lwjgl.BufferChecks;
import org.lwjgl.MemoryUtil;
import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.APIUtil;
import org.lwjgl.opencl.CLCapabilities;
import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLMem;

public final class EXTMigrateMemobject {
   public static final int CL_MIGRATE_MEM_OBJECT_HOST_EXT = 1;
   public static final int CL_COMMAND_MIGRATE_MEM_OBJECT_EXT = 16448;

   public static int clEnqueueMigrateMemObjectEXT(CLCommandQueue command_queue, PointerBuffer mem_objects, long flags, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueMigrateMemObjectEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      BufferChecks.checkBuffer((PointerBuffer)mem_objects, 1);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueMigrateMemObjectEXT(command_queue.getPointer(), mem_objects.remaining(), MemoryUtil.getAddress(mem_objects), flags, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }

   static native int nclEnqueueMigrateMemObjectEXT(long var0, int var2, long var3, long var5, int var7, long var8, long var10, long var12);

   public static int clEnqueueMigrateMemObjectEXT(CLCommandQueue command_queue, CLMem mem_object, long flags, PointerBuffer event_wait_list, PointerBuffer event) {
      long function_pointer = CLCapabilities.clEnqueueMigrateMemObjectEXT;
      BufferChecks.checkFunctionAddress(function_pointer);
      if(event_wait_list != null) {
         BufferChecks.checkDirect(event_wait_list);
      }

      if(event != null) {
         BufferChecks.checkBuffer((PointerBuffer)event, 1);
      }

      int __result = nclEnqueueMigrateMemObjectEXT(command_queue.getPointer(), 1, APIUtil.getPointer(mem_object), flags, event_wait_list == null?0:event_wait_list.remaining(), MemoryUtil.getAddressSafe(event_wait_list), MemoryUtil.getAddressSafe(event), function_pointer);
      if(__result == 0) {
         command_queue.registerCLEvent(event);
      }

      return __result;
   }
}
