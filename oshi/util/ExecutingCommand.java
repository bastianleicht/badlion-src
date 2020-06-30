package oshi.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ExecutingCommand {
   public static ArrayList runNative(String cmdToRun) {
      Process p = null;

      try {
         p = Runtime.getRuntime().exec(cmdToRun);
         p.waitFor();
      } catch (IOException var6) {
         return null;
      } catch (InterruptedException var7) {
         return null;
      }

      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = "";
      ArrayList<String> sa = new ArrayList();

      try {
         while((line = reader.readLine()) != null) {
            sa.add(line);
         }

         return sa;
      } catch (IOException var8) {
         return null;
      }
   }

   public static String getFirstAnswer(String cmd2launch) {
      ArrayList<String> sa = runNative(cmd2launch);
      return sa != null?(String)sa.get(0):null;
   }
}
