package org.lwjgl.opencl;

import org.lwjgl.opencl.CLCommandQueue;
import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.CLObjectRegistry;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.InfoUtil;

public final class CLEvent extends CLObjectChild {
   private static final CLEvent.CLEventUtil util = (CLEvent.CLEventUtil)CLPlatform.getInfoUtilInstance(CLEvent.class, "CL_EVENT_UTIL");
   private final CLCommandQueue queue;

   CLEvent(long pointer, CLContext context) {
      this(pointer, context, (CLCommandQueue)null);
   }

   CLEvent(long pointer, CLCommandQueue queue) {
      this(pointer, (CLContext)queue.getParent(), queue);
   }

   CLEvent(long pointer, CLContext context, CLCommandQueue queue) {
      super(pointer, context);
      if(this.isValid()) {
         this.queue = queue;
         if(queue == null) {
            context.getCLEventRegistry().registerObject(this);
         } else {
            queue.getCLEventRegistry().registerObject(this);
         }
      } else {
         this.queue = null;
      }

   }

   public CLCommandQueue getCLCommandQueue() {
      return this.queue;
   }

   public int getInfoInt(int param_name) {
      return util.getInfoInt(this, param_name);
   }

   public long getProfilingInfoLong(int param_name) {
      return util.getProfilingInfoLong(this, param_name);
   }

   CLObjectRegistry getParentRegistry() {
      return this.queue == null?((CLContext)this.getParent()).getCLEventRegistry():this.queue.getCLEventRegistry();
   }

   int release() {
      int var1;
      try {
         var1 = super.release();
      } finally {
         if(!this.isValid()) {
            if(this.queue == null) {
               ((CLContext)this.getParent()).getCLEventRegistry().unregisterObject(this);
            } else {
               this.queue.getCLEventRegistry().unregisterObject(this);
            }
         }

      }

      return var1;
   }

   interface CLEventUtil extends InfoUtil {
      long getProfilingInfoLong(CLEvent var1, int var2);
   }
}
