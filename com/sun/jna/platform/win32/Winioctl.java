package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

public interface Winioctl extends StdCallLibrary {
   int IOCTL_STORAGE_GET_DEVICE_NUMBER = 2953344;

   public static class STORAGE_DEVICE_NUMBER extends Structure {
      public int DeviceType;
      public int DeviceNumber;
      public int PartitionNumber;

      public STORAGE_DEVICE_NUMBER() {
      }

      public STORAGE_DEVICE_NUMBER(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends Winioctl.STORAGE_DEVICE_NUMBER implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }
}
