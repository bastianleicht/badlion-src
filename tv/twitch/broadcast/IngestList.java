package tv.twitch.broadcast;

import tv.twitch.broadcast.IngestServer;

public class IngestList {
   protected IngestServer[] servers = null;
   protected IngestServer defaultServer = null;

   public IngestServer[] getServers() {
      return this.servers;
   }

   public IngestServer getDefaultServer() {
      return this.defaultServer;
   }

   public IngestList(IngestServer[] var1) {
      if(var1 == null) {
         this.servers = new IngestServer[0];
      } else {
         this.servers = new IngestServer[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            this.servers[var2] = var1[var2];
            if(this.servers[var2].defaultServer) {
               this.defaultServer = this.servers[var2];
            }
         }

         if(this.defaultServer == null && this.servers.length > 0) {
            this.defaultServer = this.servers[0];
         }
      }

   }

   public IngestServer getBestServer() {
      if(this.servers != null && this.servers.length != 0) {
         IngestServer var1 = this.servers[0];

         for(int var2 = 1; var2 < this.servers.length; ++var2) {
            if(var1.bitrateKbps < this.servers[var2].bitrateKbps) {
               var1 = this.servers[var2];
            }
         }

         return var1;
      } else {
         return null;
      }
   }
}
