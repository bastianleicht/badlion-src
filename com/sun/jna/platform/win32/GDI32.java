package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface GDI32 extends StdCallLibrary {
   GDI32 INSTANCE = (GDI32)Native.loadLibrary("gdi32", GDI32.class, W32APIOptions.DEFAULT_OPTIONS);

   WinDef.HRGN ExtCreateRegion(Pointer var1, int var2, WinGDI.RGNDATA var3);

   int CombineRgn(WinDef.HRGN var1, WinDef.HRGN var2, WinDef.HRGN var3, int var4);

   WinDef.HRGN CreateRectRgn(int var1, int var2, int var3, int var4);

   WinDef.HRGN CreateRoundRectRgn(int var1, int var2, int var3, int var4, int var5, int var6);

   WinDef.HRGN CreatePolyPolygonRgn(WinUser.POINT[] var1, int[] var2, int var3, int var4);

   boolean SetRectRgn(WinDef.HRGN var1, int var2, int var3, int var4, int var5);

   int SetPixel(WinDef.HDC var1, int var2, int var3, int var4);

   WinDef.HDC CreateCompatibleDC(WinDef.HDC var1);

   boolean DeleteDC(WinDef.HDC var1);

   WinDef.HBITMAP CreateDIBitmap(WinDef.HDC var1, WinGDI.BITMAPINFOHEADER var2, int var3, Pointer var4, WinGDI.BITMAPINFO var5, int var6);

   WinDef.HBITMAP CreateDIBSection(WinDef.HDC var1, WinGDI.BITMAPINFO var2, int var3, PointerByReference var4, Pointer var5, int var6);

   WinDef.HBITMAP CreateCompatibleBitmap(WinDef.HDC var1, int var2, int var3);

   WinNT.HANDLE SelectObject(WinDef.HDC var1, WinNT.HANDLE var2);

   boolean DeleteObject(WinNT.HANDLE var1);

   int GetDeviceCaps(WinDef.HDC var1, int var2);

   int GetDIBits(WinDef.HDC var1, WinDef.HBITMAP var2, int var3, int var4, Pointer var5, WinGDI.BITMAPINFO var6, int var7);
}
