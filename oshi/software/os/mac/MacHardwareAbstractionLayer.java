package oshi.software.os.mac;

import java.util.ArrayList;
import java.util.List;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Memory;
import oshi.hardware.Processor;
import oshi.software.os.mac.local.CentralProcessor;
import oshi.software.os.mac.local.GlobalMemory;
import oshi.util.ExecutingCommand;

public class MacHardwareAbstractionLayer implements HardwareAbstractionLayer {
   private Processor[] _processors;
   private Memory _memory;

   public Processor[] getProcessors() {
      if(this._processors == null) {
         List<Processor> processors = new ArrayList();
         int nbCPU = (new Integer(ExecutingCommand.getFirstAnswer("sysctl -n hw.logicalcpu"))).intValue();

         for(int i = 0; i < nbCPU; ++i) {
            processors.add(new CentralProcessor());
         }

         this._processors = (Processor[])processors.toArray(new Processor[0]);
      }

      return this._processors;
   }

   public Memory getMemory() {
      if(this._memory == null) {
         this._memory = new GlobalMemory();
      }

      return this._memory;
   }
}
