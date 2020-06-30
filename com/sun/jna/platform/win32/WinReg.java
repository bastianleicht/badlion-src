package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.ByReference;
import com.sun.jna.win32.StdCallLibrary;

public interface WinReg extends StdCallLibrary {
   WinReg.HKEY HKEY_CLASSES_ROOT = new WinReg.HKEY(Integer.MIN_VALUE);
   WinReg.HKEY HKEY_CURRENT_USER = new WinReg.HKEY(-2147483647);
   WinReg.HKEY HKEY_LOCAL_MACHINE = new WinReg.HKEY(-2147483646);
   WinReg.HKEY HKEY_USERS = new WinReg.HKEY(-2147483645);
   WinReg.HKEY HKEY_PERFORMANCE_DATA = new WinReg.HKEY(-2147483644);
   WinReg.HKEY HKEY_PERFORMANCE_TEXT = new WinReg.HKEY(-2147483568);
   WinReg.HKEY HKEY_PERFORMANCE_NLSTEXT = new WinReg.HKEY(-2147483552);
   WinReg.HKEY HKEY_CURRENT_CONFIG = new WinReg.HKEY(-2147483643);
   WinReg.HKEY HKEY_DYN_DATA = new WinReg.HKEY(-2147483642);

   public static class HKEY extends WinNT.HANDLE {
      public HKEY() {
      }

      public HKEY(Pointer p) {
         super(p);
      }

      public HKEY(int value) {
         super(new Pointer((long)value));
      }
   }

   public static class HKEYByReference extends ByReference {
      public HKEYByReference() {
         this((WinReg.HKEY)null);
      }

      public HKEYByReference(WinReg.HKEY h) {
         super(Pointer.SIZE);
         this.setValue(h);
      }

      public void setValue(WinReg.HKEY h) {
         this.getPointer().setPointer(0L, h != null?h.getPointer():null);
      }

      public WinReg.HKEY getValue() {
         Pointer p = this.getPointer().getPointer(0L);
         if(p == null) {
            return null;
         } else if(WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p)) {
            return (WinReg.HKEY)WinBase.INVALID_HANDLE_VALUE;
         } else {
            WinReg.HKEY h = new WinReg.HKEY();
            h.setPointer(p);
            return h;
         }
      }
   }
}
