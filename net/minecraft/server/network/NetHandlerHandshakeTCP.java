package net.minecraft.server.network;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerLoginServer;
import net.minecraft.server.network.NetHandlerStatusServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class NetHandlerHandshakeTCP implements INetHandlerHandshakeServer {
   private final MinecraftServer server;
   private final NetworkManager networkManager;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$network$EnumConnectionState;

   public NetHandlerHandshakeTCP(MinecraftServer serverIn, NetworkManager netManager) {
      this.server = serverIn;
      this.networkManager = netManager;
   }

   public void processHandshake(C00Handshake packetIn) {
      switch($SWITCH_TABLE$net$minecraft$network$EnumConnectionState()[packetIn.getRequestedState().ordinal()]) {
      case 3:
         this.networkManager.setConnectionState(EnumConnectionState.STATUS);
         this.networkManager.setNetHandler(new NetHandlerStatusServer(this.server, this.networkManager));
         break;
      case 4:
         this.networkManager.setConnectionState(EnumConnectionState.LOGIN);
         if(packetIn.getProtocolVersion() > 47) {
            ChatComponentText chatcomponenttext = new ChatComponentText("Outdated server! I\'m still on 1.8.9");
            this.networkManager.sendPacket(new S00PacketDisconnect(chatcomponenttext));
            this.networkManager.closeChannel(chatcomponenttext);
         } else if(packetIn.getProtocolVersion() < 47) {
            ChatComponentText chatcomponenttext1 = new ChatComponentText("Outdated client! Please use 1.8.9");
            this.networkManager.sendPacket(new S00PacketDisconnect(chatcomponenttext1));
            this.networkManager.closeChannel(chatcomponenttext1);
         } else {
            this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
         }
         break;
      default:
         throw new UnsupportedOperationException("Invalid intention " + packetIn.getRequestedState());
      }

   }

   public void onDisconnect(IChatComponent reason) {
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$network$EnumConnectionState() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$network$EnumConnectionState;
      if($SWITCH_TABLE$net$minecraft$network$EnumConnectionState != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumConnectionState.values().length];

         try {
            var0[EnumConnectionState.HANDSHAKING.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[EnumConnectionState.LOGIN.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[EnumConnectionState.PLAY.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[EnumConnectionState.STATUS.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$network$EnumConnectionState = var0;
         return var0;
      }
   }
}
