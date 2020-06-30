package com.sun.jna;

import com.sun.jna.FromNativeContext;

public interface NativeMapped {
   Object fromNative(Object var1, FromNativeContext var2);

   Object toNative();

   Class nativeType();
}
