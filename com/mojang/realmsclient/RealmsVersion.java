package com.mojang.realmsclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RealmsVersion {
   private static String version;

   public static String getVersion() {
      if(version != null) {
         return version;
      } else {
         BufferedReader reader = null;

         try {
            InputStream versionStream = RealmsVersion.class.getResourceAsStream("/version");
            reader = new BufferedReader(new InputStreamReader(versionStream));
            version = reader.readLine();
            reader.close();
            String var2 = version;
            return var2;
         } catch (Exception var12) {
            ;
         } finally {
            if(reader != null) {
               try {
                  reader.close();
               } catch (IOException var11) {
                  ;
               }
            }

         }

         return null;
      }
   }
}
