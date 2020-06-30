package oshi.hardware;

import oshi.hardware.Memory;
import oshi.hardware.Processor;

public interface HardwareAbstractionLayer {
   Processor[] getProcessors();

   Memory getMemory();
}
