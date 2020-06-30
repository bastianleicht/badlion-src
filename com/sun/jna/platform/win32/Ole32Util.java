package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.W32Errors;
import com.sun.jna.platform.win32.WinNT;

public abstract class Ole32Util {
   public static Guid.GUID getGUIDFromString(String guidString) {
      Guid.GUID.ByReference lpiid = new Guid.GUID.ByReference();
      WinNT.HRESULT hr = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
      if(!hr.equals(W32Errors.S_OK)) {
         throw new RuntimeException(hr.toString());
      } else {
         return lpiid;
      }
   }

   public static String getStringFromGUID(Guid.GUID guid) {
      Guid.GUID.ByReference pguid = new Guid.GUID.ByReference(guid.getPointer());
      int max = 39;
      char[] lpsz = new char[max];
      int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, max);
      if(len == 0) {
         throw new RuntimeException("StringFromGUID2");
      } else {
         lpsz[len - 1] = 0;
         return Native.toString(lpsz);
      }
   }

   public static Guid.GUID generateGUID() {
      Guid.GUID.ByReference pguid = new Guid.GUID.ByReference();
      WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
      if(!hr.equals(W32Errors.S_OK)) {
         throw new RuntimeException(hr.toString());
      } else {
         return pguid;
      }
   }
}
