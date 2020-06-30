package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Oleaut32 extends StdCallLibrary {
   Oleaut32 INSTANCE = (Oleaut32)Native.loadLibrary("Oleaut32", Oleaut32.class, W32APIOptions.UNICODE_OPTIONS);

   Pointer SysAllocString(String var1);

   void SysFreeString(Pointer var1);
}
