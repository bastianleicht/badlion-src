package oshi.software.os.mac.local;

import oshi.hardware.Memory;
import oshi.util.ExecutingCommand;

public class GlobalMemory implements Memory {
   private long totalMemory = 0L;

   public long getAvailable() {
      long returnCurrentUsageMemory = 0L;

      for(String line : ExecutingCommand.runNative("vm_stat")) {
         if(line.startsWith("Pages free:")) {
            String[] memorySplit = line.split(":\\s+");
            returnCurrentUsageMemory += (new Long(memorySplit[1].replace(".", ""))).longValue();
         } else if(line.startsWith("Pages speculative:")) {
            String[] memorySplit = line.split(":\\s+");
            returnCurrentUsageMemory += (new Long(memorySplit[1].replace(".", ""))).longValue();
         }
      }

      returnCurrentUsageMemory = returnCurrentUsageMemory * 4096L;
      return returnCurrentUsageMemory;
   }

   public long getTotal() {
      if(this.totalMemory == 0L) {
         this.totalMemory = (new Long(ExecutingCommand.getFirstAnswer("sysctl -n hw.memsize"))).longValue();
      }

      return this.totalMemory;
   }
}
