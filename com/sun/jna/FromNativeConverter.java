package com.sun.jna;

import com.sun.jna.FromNativeContext;

public interface FromNativeConverter {
   Object fromNative(Object var1, FromNativeContext var2);

   Class nativeType();
}
