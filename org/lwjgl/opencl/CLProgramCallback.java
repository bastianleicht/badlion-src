package org.lwjgl.opencl;

import org.lwjgl.PointerWrapperAbstract;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLProgram;
import org.lwjgl.opencl.CallbackUtil;

abstract class CLProgramCallback extends PointerWrapperAbstract {
   private CLContext context;

   protected CLProgramCallback() {
      super(CallbackUtil.getProgramCallback());
   }

   final void setContext(CLContext context) {
      this.context = context;
   }

   private void handleMessage(long program_address) {
      this.handleMessage(this.context.getCLProgram(program_address));
   }

   protected abstract void handleMessage(CLProgram var1);
}
