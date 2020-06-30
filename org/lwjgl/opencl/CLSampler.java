package org.lwjgl.opencl;

import org.lwjgl.opencl.CLContext;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.CLPlatform;
import org.lwjgl.opencl.InfoUtil;

public final class CLSampler extends CLObjectChild {
   private static final InfoUtil util = CLPlatform.getInfoUtilInstance(CLSampler.class, "CL_SAMPLER_UTIL");

   CLSampler(long pointer, CLContext context) {
      super(pointer, context);
      if(this.isValid()) {
         context.getCLSamplerRegistry().registerObject(this);
      }

   }

   public int getInfoInt(int param_name) {
      return util.getInfoInt(this, param_name);
   }

   public long getInfoLong(int param_name) {
      return util.getInfoLong(this, param_name);
   }

   int release() {
      int var1;
      try {
         var1 = super.release();
      } finally {
         if(!this.isValid()) {
            ((CLContext)this.getParent()).getCLSamplerRegistry().unregisterObject(this);
         }

      }

      return var1;
   }
}
