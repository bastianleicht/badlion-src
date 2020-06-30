package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import net.badlion.client.Wrapper;
import net.jpountz.xxhash.XXHashFactory;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
   private static final Logger logger = LogManager.getLogger();
   private static final Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
   private final EnumPacketDirection direction;
   private StringBuilder packetHash = new StringBuilder();
   private int packet;
   private XXHashFactory factory = XXHashFactory.fastestInstance();
   private int currentIndex;

   public MessageDeserializer(EnumPacketDirection direction) {
      this.direction = direction;
   }

   protected void decode(ChannelHandlerContext p_decode_1_, ByteBuf p_decode_2_, List p_decode_3_) throws IOException, InstantiationException, IllegalAccessException, Exception {
      if(p_decode_2_.readableBytes() != 0) {
         String s = "";
         if(Wrapper.getInstance().currentPremiumConnection != null && Wrapper.getInstance().isPremium()) {
            ByteBuf bytebuf = p_decode_2_.copy();
            int i = this.factory.hash32().hash(bytebuf.nioBuffer(), 0);
            s = String.valueOf(i);
            bytebuf.release();
         }

         PacketBuffer packetbuffer = new PacketBuffer(p_decode_2_);
         int j = packetbuffer.readVarIntFromBuffer();
         Packet packet = ((EnumConnectionState)p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getPacket(this.direction, j);
         if(Wrapper.getInstance().currentPremiumConnection != null && Wrapper.getInstance().isPremium()) {
            String s1 = InetAddress.getByName(Wrapper.getInstance().currentPremiumConnection.split(":")[0]).getHostAddress() + ":" + Wrapper.getInstance().currentPremiumConnection.split(":")[1];
            if(p_decode_1_.channel().remoteAddress().toString().split("/")[1].equalsIgnoreCase(s1) && this.isPlayPacket(packet)) {
               this.packetHash.append(s);
               if(++this.packet >= 2000) {
                  this.packet = 0;
                  Wrapper.getInstance().sendServerToClient(this.packetHash.toString(), this.currentIndex);
                  ++this.currentIndex;
                  this.packetHash = new StringBuilder();
               }
            }
         }

         if(packet == null) {
            throw new IOException("Bad packet id " + j);
         }

         packet.readPacketData(packetbuffer);
         if(packetbuffer.readableBytes() > 0) {
            throw new IOException("Packet " + ((EnumConnectionState)p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get()).getId() + "/" + j + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + packetbuffer.readableBytes() + " bytes extra whilst reading packet " + j);
         }

         p_decode_3_.add(packet);
         if(logger.isDebugEnabled()) {
            logger.debug(RECEIVED_PACKET_MARKER, " IN: [{}:{}] {}", new Object[]{p_decode_1_.channel().attr(NetworkManager.attrKeyConnectionState).get(), Integer.valueOf(j), packet.getClass().getName()});
         }
      }

   }

   public boolean isPlayPacket(Packet p_isPlayPacket_1_) {
      return p_isPlayPacket_1_ instanceof S00PacketKeepAlive || p_isPlayPacket_1_ instanceof S01PacketJoinGame || p_isPlayPacket_1_ instanceof S02PacketChat || p_isPlayPacket_1_ instanceof S03PacketTimeUpdate || p_isPlayPacket_1_ instanceof S04PacketEntityEquipment || p_isPlayPacket_1_ instanceof S05PacketSpawnPosition || p_isPlayPacket_1_ instanceof S06PacketUpdateHealth || p_isPlayPacket_1_ instanceof S07PacketRespawn || p_isPlayPacket_1_ instanceof S08PacketPlayerPosLook || p_isPlayPacket_1_ instanceof S09PacketHeldItemChange || p_isPlayPacket_1_ instanceof S0APacketUseBed || p_isPlayPacket_1_ instanceof S0BPacketAnimation || p_isPlayPacket_1_ instanceof S0CPacketSpawnPlayer || p_isPlayPacket_1_ instanceof S0DPacketCollectItem || p_isPlayPacket_1_ instanceof S0EPacketSpawnObject || p_isPlayPacket_1_ instanceof S0FPacketSpawnMob || p_isPlayPacket_1_ instanceof S10PacketSpawnPainting || p_isPlayPacket_1_ instanceof S11PacketSpawnExperienceOrb || p_isPlayPacket_1_ instanceof S12PacketEntityVelocity || p_isPlayPacket_1_ instanceof S13PacketDestroyEntities || p_isPlayPacket_1_ instanceof S14PacketEntity || p_isPlayPacket_1_ instanceof S18PacketEntityTeleport || p_isPlayPacket_1_ instanceof S19PacketEntityHeadLook || p_isPlayPacket_1_ instanceof S19PacketEntityStatus || p_isPlayPacket_1_ instanceof S1BPacketEntityAttach || p_isPlayPacket_1_ instanceof S1CPacketEntityMetadata || p_isPlayPacket_1_ instanceof S1DPacketEntityEffect || p_isPlayPacket_1_ instanceof S1EPacketRemoveEntityEffect || p_isPlayPacket_1_ instanceof S1FPacketSetExperience || p_isPlayPacket_1_ instanceof S20PacketEntityProperties || p_isPlayPacket_1_ instanceof S21PacketChunkData || p_isPlayPacket_1_ instanceof S22PacketMultiBlockChange || p_isPlayPacket_1_ instanceof S23PacketBlockChange || p_isPlayPacket_1_ instanceof S24PacketBlockAction || p_isPlayPacket_1_ instanceof S25PacketBlockBreakAnim || p_isPlayPacket_1_ instanceof S26PacketMapChunkBulk || p_isPlayPacket_1_ instanceof S27PacketExplosion || p_isPlayPacket_1_ instanceof S28PacketEffect || p_isPlayPacket_1_ instanceof S29PacketSoundEffect || p_isPlayPacket_1_ instanceof S2APacketParticles || p_isPlayPacket_1_ instanceof S2BPacketChangeGameState || p_isPlayPacket_1_ instanceof S2CPacketSpawnGlobalEntity || p_isPlayPacket_1_ instanceof S2DPacketOpenWindow || p_isPlayPacket_1_ instanceof S2EPacketCloseWindow || p_isPlayPacket_1_ instanceof S2FPacketSetSlot || p_isPlayPacket_1_ instanceof S30PacketWindowItems || p_isPlayPacket_1_ instanceof S31PacketWindowProperty || p_isPlayPacket_1_ instanceof S32PacketConfirmTransaction || p_isPlayPacket_1_ instanceof S33PacketUpdateSign || p_isPlayPacket_1_ instanceof S34PacketMaps || p_isPlayPacket_1_ instanceof S35PacketUpdateTileEntity || p_isPlayPacket_1_ instanceof S36PacketSignEditorOpen || p_isPlayPacket_1_ instanceof S37PacketStatistics || p_isPlayPacket_1_ instanceof S38PacketPlayerListItem || p_isPlayPacket_1_ instanceof S39PacketPlayerAbilities || p_isPlayPacket_1_ instanceof S3APacketTabComplete || p_isPlayPacket_1_ instanceof S3BPacketScoreboardObjective || p_isPlayPacket_1_ instanceof S3CPacketUpdateScore || p_isPlayPacket_1_ instanceof S3DPacketDisplayScoreboard || p_isPlayPacket_1_ instanceof S3EPacketTeams || p_isPlayPacket_1_ instanceof S3FPacketCustomPayload || p_isPlayPacket_1_ instanceof S40PacketDisconnect || p_isPlayPacket_1_ instanceof S41PacketServerDifficulty || p_isPlayPacket_1_ instanceof S42PacketCombatEvent || p_isPlayPacket_1_ instanceof S43PacketCamera || p_isPlayPacket_1_ instanceof S44PacketWorldBorder || p_isPlayPacket_1_ instanceof S45PacketTitle || p_isPlayPacket_1_ instanceof S47PacketPlayerListHeaderFooter || p_isPlayPacket_1_ instanceof S48PacketResourcePackSend || p_isPlayPacket_1_ instanceof S49PacketUpdateEntityNBT;
   }
}
