package org.lwjgl.opencl;

import org.lwjgl.PointerBuffer;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLDevice;
import org.lwjgl.opencl.CLEvent;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.CLObjectRegistry;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.InfoUtil;

public final class CLCommandQueue extends CLObjectChild {
   private static final InfoUtil util = CLPlatform.getInfoUtilInstance(CLCommandQueue.class, "CL_COMMAND_QUEUE_UTIL");
   private final CLDevice device;
   private final CLObjectRegistry clEvents;

   CLCommandQueue(long pointer, CLContext context, CLDevice device) {
      super(pointer, context);
      if(this.isValid()) {
         this.device = device;
         this.clEvents = new CLObjectRegistry();
         context.getCLCommandQueueRegistry().registerObject(this);
      } else {
         this.device = null;
         this.clEvents = null;
      }

   }

   public CLDevice getCLDevice() {
      return this.device;
   }

   public CLEvent getCLEvent(long id) {
      return (CLEvent)this.clEvents.getObject(id);
   }

   public int getInfoInt(int param_name) {
      return util.getInfoInt(this, param_name);
   }

   CLObjectRegistry getCLEventRegistry() {
      return this.clEvents;
   }

   void registerCLEvent(PointerBuffer event) {
      if(event != null) {
         new CLEvent(event.get(event.position()), this);
      }

   }

   int release() {
      int var1;
      try {
         var1 = super.release();
      } finally {
         if(!this.isValid()) {
            ((CLContext)this.getParent()).getCLCommandQueueRegistry().unregisterObject(this);
         }

      }

      return var1;
   }
}
