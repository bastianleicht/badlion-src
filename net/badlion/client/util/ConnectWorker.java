package net.badlion.client.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.badlion.client.Wrapper;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class ConnectWorker implements Runnable {
   private String ip;
   private int port;
   private boolean error;
   private boolean done;
   private GuiConnecting parent;

   public boolean isDone() {
      return this.done;
   }

   public void setArgs(GuiConnecting parent, String ip, int port) {
      this.ip = ip;
      this.port = port;
      this.parent = parent;
   }

   public void run() {
      while(this.ip == null) {
         try {
            Thread.sleep(10L);
         } catch (InterruptedException var11) {
            var11.printStackTrace();
         }
      }

      Wrapper wrapper = Wrapper.getInstance();
      wrapper.premium = false;
      wrapper.currentPremiumConnection = null;
      wrapper.currentIp = null;
      this.parent.setMessage("Contacting client");
      String s = null;

      try {
         InetAddress inetaddress = InetAddress.getByName(this.ip);
         s = inetaddress.getHostAddress();
      } catch (UnknownHostException var10) {
         this.parent.setError(new GuiDisconnected(this.parent.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
         this.error = true;
         return;
      }

      this.parent.setMessage("Checking server");
      int i = wrapper.isServerBACEnabled(s, this.port);
      int j = 0;

      while(i < -1) {
         i = wrapper.isServerBACEnabled(s, this.port);
         ++j;
         if(j > 3 && i < -1) {
            this.parent.setError(new GuiDisconnected(this.parent.previousGuiScreen, "connect.failed", new ChatComponentText("Error with BAC connection, Try Again")));
            this.error = true;
            return;
         }

         if(i < -1) {
            try {
               Thread.sleep(1000L);
            } catch (InterruptedException var9) {
               var9.printStackTrace();
            }
         }
      }

      if(i == 0) {
         wrapper.premium = true;
         wrapper.currentPremiumConnection = s + ":" + this.port;
      } else {
         wrapper.premium = false;
         wrapper.currentPremiumConnection = null;
      }

      if(wrapper.isPremium()) {
         this.parent.setMessage("Enabling Badlion Anticheat (BAC) ");
         if(s == null) {
            this.parent.setError(new GuiDisconnected(this.parent.previousGuiScreen, "connect.failed", new ChatComponentText("Error with BAC connection")));
            this.error = true;
            return;
         }

         wrapper.currentIp = s + ":" + this.port;
         int k = 0;
         int l = 1337;

         while(l == 1337) {
            l = wrapper.sendHeartBeat(s, this.port);

            try {
               Thread.sleep(100L);
            } catch (Exception var8) {
               var8.printStackTrace();
            }

            if(k++ > 100) {
               break;
            }
         }

         if(l != 0) {
            String s1 = "Badlion Client Error " + l;
            switch(l) {
            case -25:
               s1 = "Error connecting to Badlion Servers. Check your internet.";
            case -24:
            case -17:
            case -16:
            case -15:
            case -14:
            case -13:
            case -12:
            case -11:
            case -10:
            case -8:
            case -7:
            case -6:
            case -5:
            case -4:
            case -3:
            case -2:
            default:
               break;
            case -23:
               s1 = "You have been banned via BAC.";
               break;
            case -22:
               s1 = "BAC is not running, try again later.";
               break;
            case -21:
               s1 = "BAC has encountered an error. Try restarting Minecraft.";
               break;
            case -20:
               s1 = "BAC wasn\'t ready due to an update. Please restart your computer.";
               break;
            case -19:
               s1 = "BAC was unable to start. Try restarting Minecraft.";
               break;
            case -18:
               s1 = "The Minecraft Client needs an update. Re-Launch your client.";
               break;
            case -9:
               s1 = "[BAC] Please wait before reconnecting!";
               break;
            case -1:
               s1 = "There is a Badlion Client update available. Restart the Badlion Client.";
            }

            this.parent.setError(new GuiDisconnected(this.parent.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{s1})));
            this.error = true;
            return;
         }
      } else {
         wrapper.currentPremiumConnection = null;
         wrapper.premium = false;
      }

      this.parent.setMessage((String)null);
      this.done = true;
   }
}
