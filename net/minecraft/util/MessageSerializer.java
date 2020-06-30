package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import java.net.InetAddress;
import net.badlion.client.Wrapper;
import net.jpountz.xxhash.XXHashFactory;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder {
   private static final Logger logger = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
   private final EnumPacketDirection direction;
   private int packet = 0;
   private StringBuilder packetHash = new StringBuilder();
   private XXHashFactory factory = XXHashFactory.fastestInstance();
   private int currentIndex;

   public MessageSerializer(EnumPacketDirection direction) {
      this.direction = direction;
   }

   protected void encode(ChannelHandlerContext p_encode_1_, Packet p_encode_2_, ByteBuf p_encode_3_) throws IOException, Exception {
      Integer integer = ((EnumConnectionState)p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getPacketId(this.direction, p_encode_2_);
      if(logger.isDebugEnabled()) {
         logger.debug(RECEIVED_PACKET_MARKER, "OUT: [{}:{}] {}", new Object[]{p_encode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), integer, p_encode_2_.getClass().getName()});
      }

      if(integer == null) {
         throw new IOException("Can\'t serialize unregistered packet");
      } else {
         PacketBuffer packetbuffer = new PacketBuffer(p_encode_3_);
         packetbuffer.writeVarIntToBuffer(integer.intValue());

         try {
            p_encode_2_.writePacketData(packetbuffer);
         } catch (Throwable var10) {
            logger.error((Object)var10);
         }

         if(Wrapper.getInstance().currentPremiumConnection != null && Wrapper.getInstance().isPremium()) {
            String s1 = InetAddress.getByName(Wrapper.getInstance().currentPremiumConnection.split(":")[0]).getHostAddress() + ":" + Wrapper.getInstance().currentPremiumConnection.split(":")[1];
            if(p_encode_1_.channel().remoteAddress().toString().split("/")[1].equalsIgnoreCase(s1) && this.isPlayPacket(p_encode_2_)) {
               ByteBuf bytebuf = p_encode_3_.copy();
               int i = this.factory.hash32().hash(bytebuf.nioBuffer(), 0);
               String s = String.valueOf(i);
               this.packetHash.append(s);
               if(++this.packet >= 2000) {
                  Wrapper.getInstance().sendClientToServer(this.packetHash.toString(), this.currentIndex);
                  ++this.currentIndex;
                  this.packetHash = new StringBuilder();
                  this.packet = 0;
               }

               bytebuf.release();
            }
         }

      }
   }

   public boolean isPlayPacket(Packet p_isPlayPacket_1_) {
      return p_isPlayPacket_1_ instanceof C00PacketKeepAlive || p_isPlayPacket_1_ instanceof C01PacketChatMessage || p_isPlayPacket_1_ instanceof C02PacketUseEntity || p_isPlayPacket_1_ instanceof C03PacketPlayer || p_isPlayPacket_1_ instanceof C07PacketPlayerDigging || p_isPlayPacket_1_ instanceof C08PacketPlayerBlockPlacement || p_isPlayPacket_1_ instanceof C09PacketHeldItemChange || p_isPlayPacket_1_ instanceof C0APacketAnimation || p_isPlayPacket_1_ instanceof C0BPacketEntityAction || p_isPlayPacket_1_ instanceof C0CPacketInput || p_isPlayPacket_1_ instanceof C0DPacketCloseWindow || p_isPlayPacket_1_ instanceof C0EPacketClickWindow || p_isPlayPacket_1_ instanceof C0FPacketConfirmTransaction || p_isPlayPacket_1_ instanceof C10PacketCreativeInventoryAction || p_isPlayPacket_1_ instanceof C11PacketEnchantItem || p_isPlayPacket_1_ instanceof C12PacketUpdateSign || p_isPlayPacket_1_ instanceof C13PacketPlayerAbilities || p_isPlayPacket_1_ instanceof C14PacketTabComplete || p_isPlayPacket_1_ instanceof C15PacketClientSettings || p_isPlayPacket_1_ instanceof C16PacketClientStatus || p_isPlayPacket_1_ instanceof C17PacketCustomPayload || p_isPlayPacket_1_ instanceof C18PacketSpectate || p_isPlayPacket_1_ instanceof C19PacketResourcePackStatus;
   }
}
