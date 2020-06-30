package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.Winspool;
import com.sun.jna.ptr.IntByReference;

public abstract class WinspoolUtil {
   public static Winspool.PRINTER_INFO_1[] getPrinterInfo1() {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumPrinters(2, (String)null, 1, (Pointer)null, 0, pcbNeeded, pcReturned);
      if(pcbNeeded.getValue() <= 0) {
         return new Winspool.PRINTER_INFO_1[0];
      } else {
         Winspool.PRINTER_INFO_1 pPrinterEnum = new Winspool.PRINTER_INFO_1(pcbNeeded.getValue());
         if(!Winspool.INSTANCE.EnumPrinters(2, (String)null, 1, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return (Winspool.PRINTER_INFO_1[])((Winspool.PRINTER_INFO_1[])pPrinterEnum.toArray(pcReturned.getValue()));
         }
      }
   }

   public static Winspool.PRINTER_INFO_4[] getPrinterInfo4() {
      IntByReference pcbNeeded = new IntByReference();
      IntByReference pcReturned = new IntByReference();
      Winspool.INSTANCE.EnumPrinters(2, (String)null, 4, (Pointer)null, 0, pcbNeeded, pcReturned);
      if(pcbNeeded.getValue() <= 0) {
         return new Winspool.PRINTER_INFO_4[0];
      } else {
         Winspool.PRINTER_INFO_4 pPrinterEnum = new Winspool.PRINTER_INFO_4(pcbNeeded.getValue());
         if(!Winspool.INSTANCE.EnumPrinters(2, (String)null, 4, pPrinterEnum.getPointer(), pcbNeeded.getValue(), pcbNeeded, pcReturned)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            return (Winspool.PRINTER_INFO_4[])((Winspool.PRINTER_INFO_4[])pPrinterEnum.toArray(pcReturned.getValue()));
         }
      }
   }
}
