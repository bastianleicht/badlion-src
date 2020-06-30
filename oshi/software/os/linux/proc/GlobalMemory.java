package oshi.software.os.linux.proc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import oshi.hardware.Memory;

public class GlobalMemory implements Memory {
   private long totalMemory = 0L;

   public long getAvailable() {
      long returnCurrentUsageMemory = 0L;
      Scanner in = null;

      try {
         in = new Scanner(new FileReader("/proc/meminfo"));
      } catch (FileNotFoundException var6) {
         return returnCurrentUsageMemory;
      }

      in.useDelimiter("\n");

      while(in.hasNext()) {
         String checkLine = in.next();
         if(checkLine.startsWith("MemFree:") || checkLine.startsWith("MemAvailable:")) {
            String[] memorySplit = checkLine.split("\\s+");
            returnCurrentUsageMemory = (new Long(memorySplit[1])).longValue();
            if(memorySplit[2].equals("kB")) {
               returnCurrentUsageMemory *= 1024L;
            }

            if(memorySplit[0].equals("MemAvailable:")) {
               break;
            }
         }
      }

      in.close();
      return returnCurrentUsageMemory;
   }

   public long getTotal() {
      if(this.totalMemory == 0L) {
         Scanner in = null;

         try {
            in = new Scanner(new FileReader("/proc/meminfo"));
         } catch (FileNotFoundException var4) {
            this.totalMemory = 0L;
            return this.totalMemory;
         }

         in.useDelimiter("\n");

         while(in.hasNext()) {
            String checkLine = in.next();
            if(checkLine.startsWith("MemTotal:")) {
               String[] memorySplit = checkLine.split("\\s+");
               this.totalMemory = (new Long(memorySplit[1])).longValue();
               if(memorySplit[2].equals("kB")) {
                  this.totalMemory *= 1024L;
               }
               break;
            }
         }

         in.close();
      }

      return this.totalMemory;
   }
}
