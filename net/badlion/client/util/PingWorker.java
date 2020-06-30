package net.badlion.client.util;

import net.badlion.client.util.PingerCallable;

public class PingWorker implements Runnable {
   private PingerCallable callable;
   private int ping;
   private boolean runOnce;
   private boolean run = true;
   private String currentConnection;

   public PingerCallable getCallable() {
      return this.callable;
   }

   public PingWorker(String connection) {
      this.currentConnection = connection;
   }

   public PingWorker() {
   }

   public int getPing() {
      if(this.callable == null) {
         this.ping = 0;
      }

      return this.ping;
   }

   public void setRunOnce(boolean runOnce) {
      this.runOnce = runOnce;
   }

   public boolean shouldRunOnce() {
      return this.runOnce;
   }

   public void setCallable(PingerCallable callable) {
      this.callable = callable;
   }

   public void setRun(boolean run) {
      this.run = run;
   }

   public void run() {
      while(this.run) {
         try {
            if(this.currentConnection != null) {
               this.callable = new PingerCallable(this.currentConnection);
               this.currentConnection = null;
            }

            if(this.callable != null) {
               this.ping = Integer.parseInt(String.valueOf(this.callable.getPing()));
            }

            Thread.sleep(5000L);
         } catch (Exception var2) {
            var2.printStackTrace();
         }

         if(this.runOnce) {
            this.run = false;
         }
      }

   }

   public void setCurrentConnection(String currentConnection) {
      this.currentConnection = currentConnection;
   }

   public String getCurrentConnection() {
      return this.currentConnection;
   }
}
