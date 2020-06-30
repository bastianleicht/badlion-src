package com.sun.jna.platform.wince;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.W32APIOptions;

public interface CoreDLL extends WinNT {
   CoreDLL INSTANCE = (CoreDLL)Native.loadLibrary("coredll", CoreDLL.class, W32APIOptions.UNICODE_OPTIONS);
}
