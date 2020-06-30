package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.GLSync;

public final class ARBCLEvent {
   public static final int GL_SYNC_CL_EVENT_ARB = 33344;
   public static final int GL_SYNC_CL_EVENT_COMPLETE_ARB = 33345;

   public static GLSync glCreateSyncFromCLeventARB(CLContext context, CLEvent event, int flags) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glCreateSyncFromCLeventARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      GLSync __result = new GLSync(nglCreateSyncFromCLeventARB(context.getPointer(), event.getPointer(), flags, function_pointer));
      return __result;
   }

   static native long nglCreateSyncFromCLeventARB(long var0, long var2, int var4, long var5);
}
