package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Shell32;
import com.sun.jna.platform.win32.ShlObj;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public abstract class Shell32Util {
   public static String getFolderPath(WinDef.HWND hwnd, int nFolder, WinDef.DWORD dwFlags) {
      char[] pszPath = new char[260];
      WinNT.HRESULT hr = Shell32.INSTANCE.SHGetFolderPath(hwnd, nFolder, (WinNT.HANDLE)null, dwFlags, pszPath);
      if(!hr.equals(W32Errors.S_OK)) {
         throw new Win32Exception(hr);
      } else {
         return Native.toString(pszPath);
      }
   }

   public static String getFolderPath(int nFolder) {
      return getFolderPath((WinDef.HWND)null, nFolder, ShlObj.SHGFP_TYPE_CURRENT);
   }
}
