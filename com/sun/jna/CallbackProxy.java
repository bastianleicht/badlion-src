package com.sun.jna;

import com.sun.jna.Callback;

public interface CallbackProxy extends Callback {
   Object callback(Object[] var1);

   Class[] getParameterTypes();

   Class getReturnType();
}
