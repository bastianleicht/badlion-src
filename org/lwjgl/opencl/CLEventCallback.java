package org.lwjgl.opencl;

import org.lwjgl.PointerWrapperAbstract;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLObjectRegistry;
import org.lwjgl.opencl.CallbackUtil;

public abstract class CLEventCallback extends PointerWrapperAbstract {
   private CLObjectRegistry eventRegistry;

   protected CLEventCallback() {
      super(CallbackUtil.getEventCallback());
   }

   void setRegistry(CLObjectRegistry eventRegistry) {
      this.eventRegistry = eventRegistry;
   }

   private void handleMessage(long event_address, int event_command_exec_status) {
      this.handleMessage((CLEvent)this.eventRegistry.getObject(event_address), event_command_exec_status);
   }

   protected abstract void handleMessage(CLEvent var1, int var2);
}
