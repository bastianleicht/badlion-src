package org.lwjgl.opencl;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opencl.CLObject;
import org.lwjgl.opencl.CLObjectRetainable;

abstract class CLObjectChild extends CLObjectRetainable {
   private final CLObject parent;

   protected CLObjectChild(long pointer, CLObject parent) {
      super(pointer);
      if(LWJGLUtil.DEBUG && parent != null && !parent.isValid()) {
         throw new IllegalStateException("The parent specified is not a valid CL object.");
      } else {
         this.parent = parent;
      }
   }

   public CLObject getParent() {
      return this.parent;
   }
}
