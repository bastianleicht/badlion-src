package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Msi extends StdCallLibrary {
   Msi INSTANCE = (Msi)Native.loadLibrary("msi", Msi.class, W32APIOptions.UNICODE_OPTIONS);
   int INSTALLSTATE_NOTUSED = -7;
   int INSTALLSTATE_BADCONFIG = -6;
   int INSTALLSTATE_INCOMPLETE = -5;
   int INSTALLSTATE_SOURCEABSENT = -4;
   int INSTALLSTATE_MOREDATA = -3;
   int INSTALLSTATE_INVALIDARG = -2;
   int INSTALLSTATE_UNKNOWN = -1;
   int INSTALLSTATE_BROKEN = 0;
   int INSTALLSTATE_ADVERTISED = 1;
   int INSTALLSTATE_REMOVED = 1;
   int INSTALLSTATE_ABSENT = 2;
   int INSTALLSTATE_LOCAL = 3;
   int INSTALLSTATE_SOURCE = 4;
   int INSTALLSTATE_DEFAULT = 5;

   int MsiGetComponentPath(String var1, String var2, char[] var3, IntByReference var4);

   int MsiLocateComponent(String var1, char[] var2, IntByReference var3);

   int MsiGetProductCode(String var1, char[] var2);

   int MsiEnumComponents(WinDef.DWORD var1, char[] var2);
}
