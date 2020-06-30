package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinCrypt;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Crypt32 extends StdCallLibrary {
   Crypt32 INSTANCE = (Crypt32)Native.loadLibrary("Crypt32", Crypt32.class, W32APIOptions.UNICODE_OPTIONS);

   boolean CryptProtectData(WinCrypt.DATA_BLOB var1, String var2, WinCrypt.DATA_BLOB var3, Pointer var4, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT var5, int var6, WinCrypt.DATA_BLOB var7);

   boolean CryptUnprotectData(WinCrypt.DATA_BLOB var1, PointerByReference var2, WinCrypt.DATA_BLOB var3, Pointer var4, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT var5, int var6, WinCrypt.DATA_BLOB var7);
}
