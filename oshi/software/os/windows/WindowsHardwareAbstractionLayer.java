package oshi.software.os.windows;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.List;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Memory;
import oshi.hardware.Processor;
import oshi.software.os.windows.nt.CentralProcessor;
import oshi.software.os.windows.nt.GlobalMemory;

public class WindowsHardwareAbstractionLayer implements HardwareAbstractionLayer {
   private Processor[] _processors = null;
   private Memory _memory = null;

   public Memory getMemory() {
      if(this._memory == null) {
         this._memory = new GlobalMemory();
      }

      return this._memory;
   }

   public Processor[] getProcessors() {
      if(this._processors == null) {
         String cpuRegistryRoot = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor";
         List<Processor> processors = new ArrayList();
         String[] processorIds = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "HARDWARE\\DESCRIPTION\\System\\CentralProcessor");

         for(String processorId : processorIds) {
            String cpuRegistryPath = "HARDWARE\\DESCRIPTION\\System\\CentralProcessor\\" + processorId;
            CentralProcessor cpu = new CentralProcessor();
            cpu.setIdentifier(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "Identifier"));
            cpu.setName(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "ProcessorNameString"));
            cpu.setVendor(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, cpuRegistryPath, "VendorIdentifier"));
            processors.add(cpu);
         }

         this._processors = (Processor[])processors.toArray(new Processor[0]);
      }

      return this._processors;
   }
}
