package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Version extends StdCallLibrary {
   Version INSTANCE = (Version)Native.loadLibrary("version", Version.class, W32APIOptions.DEFAULT_OPTIONS);

   int GetFileVersionInfoSize(String var1, IntByReference var2);

   boolean GetFileVersionInfo(String var1, int var2, int var3, Pointer var4);

   boolean VerQueryValue(Pointer var1, String var2, PointerByReference var3, IntByReference var4);
}
