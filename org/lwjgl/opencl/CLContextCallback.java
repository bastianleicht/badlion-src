package org.lwjgl.opencl;

import java.nio.ByteBuffer;
import org.lwjgl.PointerWrapperAbstract;
import org.lwjgl.opencl.CallbackUtil;

public abstract class CLContextCallback extends PointerWrapperAbstract {
   private final boolean custom;

   protected CLContextCallback() {
      super(CallbackUtil.getContextCallback());
      this.custom = false;
   }

   protected CLContextCallback(long pointer) {
      super(pointer);
      if(pointer == 0L) {
         throw new RuntimeException("Invalid callback function pointer specified.");
      } else {
         this.custom = true;
      }
   }

   final boolean isCustom() {
      return this.custom;
   }

   protected abstract void handleMessage(String var1, ByteBuffer var2);
}
