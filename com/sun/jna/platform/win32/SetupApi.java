package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface SetupApi extends StdCallLibrary {
   SetupApi INSTANCE = (SetupApi)Native.loadLibrary("setupapi", SetupApi.class, W32APIOptions.DEFAULT_OPTIONS);
   Guid.GUID GUID_DEVINTERFACE_DISK = new Guid.GUID(new byte[]{(byte)7, (byte)99, (byte)-11, (byte)83, (byte)-65, (byte)-74, (byte)-48, (byte)17, (byte)-108, (byte)-14, (byte)0, (byte)-96, (byte)-55, (byte)30, (byte)-5, (byte)-117});
   int DIGCF_DEFAULT = 1;
   int DIGCF_PRESENT = 2;
   int DIGCF_ALLCLASSES = 4;
   int DIGCF_PROFILE = 8;
   int DIGCF_DEVICEINTERFACE = 16;
   int SPDRP_REMOVAL_POLICY = 31;
   int CM_DEVCAP_REMOVABLE = 4;

   WinNT.HANDLE SetupDiGetClassDevs(Guid.GUID.ByReference var1, Pointer var2, Pointer var3, int var4);

   boolean SetupDiDestroyDeviceInfoList(WinNT.HANDLE var1);

   boolean SetupDiEnumDeviceInterfaces(WinNT.HANDLE var1, Pointer var2, Guid.GUID.ByReference var3, int var4, SetupApi.SP_DEVICE_INTERFACE_DATA.ByReference var5);

   boolean SetupDiGetDeviceInterfaceDetail(WinNT.HANDLE var1, SetupApi.SP_DEVICE_INTERFACE_DATA.ByReference var2, Pointer var3, int var4, IntByReference var5, SetupApi.SP_DEVINFO_DATA.ByReference var6);

   boolean SetupDiGetDeviceRegistryProperty(WinNT.HANDLE var1, SetupApi.SP_DEVINFO_DATA.ByReference var2, int var3, IntByReference var4, Pointer var5, int var6, IntByReference var7);

   public static class SP_DEVICE_INTERFACE_DATA extends Structure {
      public int cbSize;
      public Guid.GUID InterfaceClassGuid;
      public int Flags;
      public Pointer Reserved;

      public SP_DEVICE_INTERFACE_DATA() {
         this.cbSize = this.size();
      }

      public SP_DEVICE_INTERFACE_DATA(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends SetupApi.SP_DEVINFO_DATA implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   public static class SP_DEVINFO_DATA extends Structure {
      public int cbSize;
      public Guid.GUID InterfaceClassGuid;
      public int DevInst;
      public Pointer Reserved;

      public SP_DEVINFO_DATA() {
         this.cbSize = this.size();
      }

      public SP_DEVINFO_DATA(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends SetupApi.SP_DEVINFO_DATA implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }
}
