package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Crypt32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinCrypt;
import com.sun.jna.ptr.PointerByReference;

public abstract class Crypt32Util {
   public static byte[] cryptProtectData(byte[] data) {
      return cryptProtectData(data, 0);
   }

   public static byte[] cryptProtectData(byte[] data, int flags) {
      return cryptProtectData(data, (byte[])null, flags, "", (WinCrypt.CRYPTPROTECT_PROMPTSTRUCT)null);
   }

   public static byte[] cryptProtectData(byte[] data, byte[] entropy, int flags, String description, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
      WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
      WinCrypt.DATA_BLOB pDataProtected = new WinCrypt.DATA_BLOB();
      WinCrypt.DATA_BLOB pEntropy = entropy == null?null:new WinCrypt.DATA_BLOB(entropy);

      byte[] var8;
      try {
         if(!Crypt32.INSTANCE.CryptProtectData(pDataIn, description, pEntropy, (Pointer)null, prompt, flags, pDataProtected)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         var8 = pDataProtected.getData();
      } finally {
         if(pDataProtected.pbData != null) {
            Kernel32.INSTANCE.LocalFree(pDataProtected.pbData);
         }

      }

      return var8;
   }

   public static byte[] cryptUnprotectData(byte[] data) {
      return cryptUnprotectData(data, 0);
   }

   public static byte[] cryptUnprotectData(byte[] data, int flags) {
      return cryptUnprotectData(data, (byte[])null, flags, (WinCrypt.CRYPTPROTECT_PROMPTSTRUCT)null);
   }

   public static byte[] cryptUnprotectData(byte[] data, byte[] entropy, int flags, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
      WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
      WinCrypt.DATA_BLOB pDataUnprotected = new WinCrypt.DATA_BLOB();
      WinCrypt.DATA_BLOB pEntropy = entropy == null?null:new WinCrypt.DATA_BLOB(entropy);
      PointerByReference pDescription = new PointerByReference();

      byte[] var8;
      try {
         if(!Crypt32.INSTANCE.CryptUnprotectData(pDataIn, pDescription, pEntropy, (Pointer)null, prompt, flags, pDataUnprotected)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         var8 = pDataUnprotected.getData();
      } finally {
         if(pDataUnprotected.pbData != null) {
            Kernel32.INSTANCE.LocalFree(pDataUnprotected.pbData);
         }

         if(pDescription.getValue() != null) {
            Kernel32.INSTANCE.LocalFree(pDescription.getValue());
         }

      }

      return var8;
   }
}
