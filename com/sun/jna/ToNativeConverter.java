package com.sun.jna;

import com.sun.jna.ToNativeContext;

public interface ToNativeConverter {
   Object toNative(Object var1, ToNativeContext var2);

   Class nativeType();
}
