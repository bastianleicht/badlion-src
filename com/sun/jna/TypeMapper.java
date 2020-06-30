package com.sun.jna;

import com.sun.jna.FromNativeConverter;
import com.sun.jna.ToNativeConverter;

public interface TypeMapper {
   FromNativeConverter getFromNativeConverter(Class var1);

   ToNativeConverter getToNativeConverter(Class var1);
}
