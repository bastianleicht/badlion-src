package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.NtDll;
import com.sun.jna.platform.win32.Wdm;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;

public abstract class NtDllUtil {
   public static String getKeyName(WinReg.HKEY hkey) {
      IntByReference resultLength = new IntByReference();
      int rc = NtDll.INSTANCE.ZwQueryKey(hkey, 0, (Structure)null, 0, resultLength);
      if(rc == -1073741789 && resultLength.getValue() > 0) {
         Wdm.KEY_BASIC_INFORMATION keyInformation = new Wdm.KEY_BASIC_INFORMATION(resultLength.getValue());
         rc = NtDll.INSTANCE.ZwQueryKey(hkey, 0, keyInformation, resultLength.getValue(), resultLength);
         if(rc != 0) {
            throw new Win32Exception(rc);
         } else {
            return keyInformation.getName();
         }
      } else {
         throw new Win32Exception(rc);
      }
   }
}
