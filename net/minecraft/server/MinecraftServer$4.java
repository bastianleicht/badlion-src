package net.minecraft.server;

import java.util.concurrent.Callable;
import net.minecraft.server.MinecraftServer;

class MinecraftServer$4 implements Callable {
   // $FF: synthetic field
   final MinecraftServer a;

   MinecraftServer$4(MinecraftServer var1) {
      this.a = var1;
   }

   public String a() {
      return MinecraftServer.a(this.a).o() + " / " + MinecraftServer.a(this.a).p() + "; " + MinecraftServer.a(this.a).v();
   }

   // $FF: synthetic method
   public Object call() throws Exception {
      return this.a();
   }
}
