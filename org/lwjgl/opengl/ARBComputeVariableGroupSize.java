package org.lwjgl.opengl;

import org.lwjgl.BufferChecks;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GLContext;

public final class ARBComputeVariableGroupSize {
   public static final int GL_MAX_COMPUTE_VARIABLE_GROUP_INVOCATIONS_ARB = 37700;
   public static final int GL_MAX_COMPUTE_FIXED_GROUP_INVOCATIONS_ARB = 37099;
   public static final int GL_MAX_COMPUTE_VARIABLE_GROUP_SIZE_ARB = 37701;
   public static final int GL_MAX_COMPUTE_FIXED_GROUP_SIZE_ARB = 37311;

   public static void glDispatchComputeGroupSizeARB(int num_groups_x, int num_groups_y, int num_groups_z, int group_size_x, int group_size_y, int group_size_z) {
      ContextCapabilities caps = GLContext.getCapabilities();
      long function_pointer = caps.glDispatchComputeGroupSizeARB;
      BufferChecks.checkFunctionAddress(function_pointer);
      nglDispatchComputeGroupSizeARB(num_groups_x, num_groups_y, num_groups_z, group_size_x, group_size_y, group_size_z, function_pointer);
   }

   static native void nglDispatchComputeGroupSizeARB(int var0, int var1, int var2, int var3, int var4, int var5, long var6);
}
