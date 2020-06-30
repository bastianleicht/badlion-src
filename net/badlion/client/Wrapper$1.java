package net.badlion.client;

import java.util.concurrent.Callable;
import net.badlion.client.Wrapper;
import org.apache.logging.log4j.LogManager;

class Wrapper$1 implements Callable {
   // $FF: synthetic field
   final Wrapper this$0;

   Wrapper$1(Wrapper this$0) {
      this.this$0 = this$0;
   }

   public Boolean call() {
      try {
         return Boolean.valueOf(Wrapper.getInstance().CacheJNIInformation());
      } catch (LinkageError var2) {
         LogManager.getLogger().info("JNIC: Linkage ERROR NOT LOADED: " + var2.getMessage());
         return Boolean.valueOf(false);
      }
   }
}
