package com.sun.jna;

import com.sun.jna.NativeLibrary;
import java.lang.reflect.Method;

public interface FunctionMapper {
   String getFunctionName(NativeLibrary var1, Method var2);
}
