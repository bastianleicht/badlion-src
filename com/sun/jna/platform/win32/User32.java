package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends StdCallLibrary, WinUser {
   User32 INSTANCE = (User32)Native.loadLibrary("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);

   WinDef.HDC GetDC(WinDef.HWND var1);

   int ReleaseDC(WinDef.HWND var1, WinDef.HDC var2);

   WinDef.HWND FindWindow(String var1, String var2);

   int GetClassName(WinDef.HWND var1, char[] var2, int var3);

   boolean GetGUIThreadInfo(int var1, WinUser.GUITHREADINFO var2);

   boolean GetWindowInfo(WinDef.HWND var1, WinUser.WINDOWINFO var2);

   boolean GetWindowRect(WinDef.HWND var1, WinDef.RECT var2);

   int GetWindowText(WinDef.HWND var1, char[] var2, int var3);

   int GetWindowTextLength(WinDef.HWND var1);

   int GetWindowModuleFileName(WinDef.HWND var1, char[] var2, int var3);

   int GetWindowThreadProcessId(WinDef.HWND var1, IntByReference var2);

   boolean EnumWindows(WinUser.WNDENUMPROC var1, Pointer var2);

   boolean EnumChildWindows(WinDef.HWND var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean EnumThreadWindows(int var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean FlashWindowEx(WinUser.FLASHWINFO var1);

   WinDef.HICON LoadIcon(WinDef.HINSTANCE var1, String var2);

   WinNT.HANDLE LoadImage(WinDef.HINSTANCE var1, String var2, int var3, int var4, int var5, int var6);

   boolean DestroyIcon(WinDef.HICON var1);

   int GetWindowLong(WinDef.HWND var1, int var2);

   int SetWindowLong(WinDef.HWND var1, int var2, int var3);

   Pointer SetWindowLong(WinDef.HWND var1, int var2, Pointer var3);

   BaseTSD.LONG_PTR GetWindowLongPtr(WinDef.HWND var1, int var2);

   BaseTSD.LONG_PTR SetWindowLongPtr(WinDef.HWND var1, int var2, BaseTSD.LONG_PTR var3);

   Pointer SetWindowLongPtr(WinDef.HWND var1, int var2, Pointer var3);

   boolean SetLayeredWindowAttributes(WinDef.HWND var1, int var2, byte var3, int var4);

   boolean GetLayeredWindowAttributes(WinDef.HWND var1, IntByReference var2, ByteByReference var3, IntByReference var4);

   boolean UpdateLayeredWindow(WinDef.HWND var1, WinDef.HDC var2, WinUser.POINT var3, WinUser.SIZE var4, WinDef.HDC var5, WinUser.POINT var6, int var7, WinUser.BLENDFUNCTION var8, int var9);

   int SetWindowRgn(WinDef.HWND var1, WinDef.HRGN var2, boolean var3);

   boolean GetKeyboardState(byte[] var1);

   short GetAsyncKeyState(int var1);

   WinUser.HHOOK SetWindowsHookEx(int var1, WinUser.HOOKPROC var2, WinDef.HINSTANCE var3, int var4);

   WinDef.LRESULT CallNextHookEx(WinUser.HHOOK var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   WinDef.LRESULT CallNextHookEx(WinUser.HHOOK var1, int var2, WinDef.WPARAM var3, Pointer var4);

   boolean UnhookWindowsHookEx(WinUser.HHOOK var1);

   int GetMessage(WinUser.MSG var1, WinDef.HWND var2, int var3, int var4);

   boolean PeekMessage(WinUser.MSG var1, WinDef.HWND var2, int var3, int var4, int var5);

   boolean TranslateMessage(WinUser.MSG var1);

   WinDef.LRESULT DispatchMessage(WinUser.MSG var1);

   void PostMessage(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   void PostQuitMessage(int var1);

   int GetSystemMetrics(int var1);

   WinDef.HWND SetParent(WinDef.HWND var1, WinDef.HWND var2);

   boolean IsWindowVisible(WinDef.HWND var1);

   boolean MoveWindow(WinDef.HWND var1, int var2, int var3, int var4, int var5, boolean var6);

   boolean SetWindowPos(WinDef.HWND var1, WinDef.HWND var2, int var3, int var4, int var5, int var6, int var7);

   boolean AttachThreadInput(WinDef.DWORD var1, WinDef.DWORD var2, boolean var3);

   boolean SetForegroundWindow(WinDef.HWND var1);

   WinDef.HWND GetForegroundWindow();

   WinDef.HWND SetFocus(WinDef.HWND var1);

   WinDef.DWORD SendInput(WinDef.DWORD var1, WinUser.INPUT[] var2, int var3);

   WinDef.DWORD WaitForInputIdle(WinNT.HANDLE var1, WinDef.DWORD var2);

   boolean InvalidateRect(WinDef.HWND var1, Structure.ByReference var2, boolean var3);

   boolean RedrawWindow(WinDef.HWND var1, Structure.ByReference var2, WinDef.HRGN var3, WinDef.DWORD var4);

   WinDef.HWND GetWindow(WinDef.HWND var1, WinDef.DWORD var2);

   boolean UpdateWindow(WinDef.HWND var1);

   boolean ShowWindow(WinDef.HWND var1, int var2);

   boolean CloseWindow(WinDef.HWND var1);

   boolean RegisterHotKey(WinDef.HWND var1, int var2, int var3, int var4);

   boolean UnregisterHotKey(Pointer var1, int var2);
}
