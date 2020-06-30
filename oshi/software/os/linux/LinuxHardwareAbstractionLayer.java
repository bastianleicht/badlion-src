package oshi.software.os.linux;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Memory;
import oshi.hardware.Processor;
import oshi.software.os.linux.proc.CentralProcessor;
import oshi.software.os.linux.proc.GlobalMemory;

public class LinuxHardwareAbstractionLayer implements HardwareAbstractionLayer {
   private static final String SEPARATOR = "\\s+:\\s";
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
         List<Processor> processors = new ArrayList();
         Scanner in = null;

         try {
            in = new Scanner(new FileReader("/proc/cpuinfo"));
         } catch (FileNotFoundException var11) {
            System.err.println("Problem with: /proc/cpuinfo");
            System.err.println(var11.getMessage());
            return null;
         }

         in.useDelimiter("\n");
         CentralProcessor cpu = null;

         while(in.hasNext()) {
            String toBeAnalyzed = in.next();
            if(toBeAnalyzed.equals("")) {
               if(cpu != null) {
                  processors.add(cpu);
               }

               cpu = null;
            } else {
               if(cpu == null) {
                  cpu = new CentralProcessor();
               }

               if(toBeAnalyzed.startsWith("model name\t")) {
                  cpu.setName(toBeAnalyzed.split("\\s+:\\s")[1]);
               } else if(!toBeAnalyzed.startsWith("flags\t")) {
                  if(toBeAnalyzed.startsWith("cpu family\t")) {
                     cpu.setFamily(toBeAnalyzed.split("\\s+:\\s")[1]);
                  } else if(toBeAnalyzed.startsWith("model\t")) {
                     cpu.setModel(toBeAnalyzed.split("\\s+:\\s")[1]);
                  } else if(toBeAnalyzed.startsWith("stepping\t")) {
                     cpu.setStepping(toBeAnalyzed.split("\\s+:\\s")[1]);
                  } else if(toBeAnalyzed.startsWith("vendor_id")) {
                     cpu.setVendor(toBeAnalyzed.split("\\s+:\\s")[1]);
                  }
               } else {
                  String[] flags = toBeAnalyzed.split("\\s+:\\s")[1].split(" ");
                  boolean found = false;

                  for(String flag : flags) {
                     if(flag.equalsIgnoreCase("LM")) {
                        found = true;
                        break;
                     }
                  }

                  cpu.setCpu64(found);
               }
            }
         }

         in.close();
         if(cpu != null) {
            processors.add(cpu);
         }

         this._processors = (Processor[])processors.toArray(new Processor[0]);
      }

      return this._processors;
   }
}
