package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.badlion.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   private static final Logger logger = LogManager.getLogger();
   private final Minecraft mc;
   private final List servers = Lists.newArrayList();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$Wrapper$Region;

   public ServerList(Minecraft mcIn) {
      this.mc = mcIn;
      this.loadServerList();
   }

   public void loadServerList() {
      try {
         this.servers.clear();
         NBTTagCompound nbttagcompound = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
         if(Wrapper.getInstance().getRegion() == null) {
            this.addServerData(new ServerData("Badlion", "na.badlion.net", false));
         } else {
            switch($SWITCH_TABLE$net$badlion$client$Wrapper$Region()[Wrapper.getInstance().getRegion().ordinal()]) {
            case 1:
               this.addServerData(new ServerData("Badlion", "na.badlion.net", false));
               break;
            case 2:
               this.addServerData(new ServerData("Badlion", "sa.badlion.net", false));
               break;
            case 3:
               this.addServerData(new ServerData("Badlion", "eu.badlion.net", false));
               break;
            default:
               this.addServerData(new ServerData("Badlion", "na.badlion.net", false));
            }
         }

         if(nbttagcompound != null) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("servers", 10);

            for(int i = 0; i < nbttaglist.tagCount(); ++i) {
               this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompoundTagAt(i)));
            }
         }
      } catch (Exception var4) {
         logger.error((String)"Couldn\'t load server list", (Throwable)var4);
      }

   }

   public void saveServerList() {
      try {
         NBTTagList nbttaglist = new NBTTagList();
         int i = 0;

         for(ServerData serverdata : this.servers) {
            if(i++ > 0) {
               nbttaglist.appendTag(serverdata.getNBTCompound());
            }
         }

         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.setTag("servers", nbttaglist);
         CompressedStreamTools.safeWrite(nbttagcompound, new File(this.mc.mcDataDir, "servers.dat"));
      } catch (Exception var5) {
         logger.error((String)"Couldn\'t save server list", (Throwable)var5);
      }

   }

   public ServerData getServerData(int p_78850_1_) {
      return (ServerData)this.servers.get(p_78850_1_);
   }

   public void removeServerData(int p_78851_1_) {
      this.servers.remove(p_78851_1_);
   }

   public void addServerData(ServerData p_78849_1_) {
      this.servers.add(p_78849_1_);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int p_78857_1_, int p_78857_2_) {
      ServerData serverdata = this.getServerData(p_78857_1_);
      this.servers.set(p_78857_1_, this.getServerData(p_78857_2_));
      this.servers.set(p_78857_2_, serverdata);
      this.saveServerList();
   }

   public void func_147413_a(int p_147413_1_, ServerData p_147413_2_) {
      this.servers.set(p_147413_1_, p_147413_2_);
   }

   public static void func_147414_b(ServerData p_147414_0_) {
      ServerList serverlist = new ServerList(Minecraft.getMinecraft());
      serverlist.loadServerList();

      for(int i = 0; i < serverlist.countServers(); ++i) {
         ServerData serverdata = serverlist.getServerData(i);
         if(serverdata.serverName.equals(p_147414_0_.serverName) && serverdata.serverIP.equals(p_147414_0_.serverIP)) {
            serverlist.func_147413_a(i, p_147414_0_);
            break;
         }
      }

      serverlist.saveServerList();
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$Wrapper$Region() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$Wrapper$Region;
      if($SWITCH_TABLE$net$badlion$client$Wrapper$Region != null) {
         return var10000;
      } else {
         int[] var0 = new int[Wrapper.Region.values().length];

         try {
            var0[Wrapper.Region.EU.ordinal()] = 3;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[Wrapper.Region.NA.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[Wrapper.Region.SA.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$Wrapper$Region = var0;
         return var0;
      }
   }
}
