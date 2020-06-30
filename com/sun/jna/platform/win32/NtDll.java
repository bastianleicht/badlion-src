package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface NtDll extends StdCallLibrary {
   NtDll INSTANCE = (NtDll)Native.loadLibrary("NtDll", NtDll.class, W32APIOptions.UNICODE_OPTIONS);

   int ZwQueryKey(WinNT.HANDLE var1, int var2, Structure var3, int var4, IntByReference var5);
}
