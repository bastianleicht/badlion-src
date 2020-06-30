package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.border.WorldBorder;

public class S44PacketWorldBorder implements Packet {
   private S44PacketWorldBorder.Action action;
   private int size;
   private double centerX;
   private double centerZ;
   private double targetSize;
   private double diameter;
   private long timeUntilTarget;
   private int warningTime;
   private int warningDistance;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action;

   public S44PacketWorldBorder() {
   }

   public S44PacketWorldBorder(WorldBorder border, S44PacketWorldBorder.Action actionIn) {
      this.action = actionIn;
      this.centerX = border.getCenterX();
      this.centerZ = border.getCenterZ();
      this.diameter = border.getDiameter();
      this.targetSize = border.getTargetSize();
      this.timeUntilTarget = border.getTimeUntilTarget();
      this.size = border.getSize();
      this.warningDistance = border.getWarningDistance();
      this.warningTime = border.getWarningTime();
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = (S44PacketWorldBorder.Action)buf.readEnumValue(S44PacketWorldBorder.Action.class);
      switch($SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action()[this.action.ordinal()]) {
      case 1:
         this.targetSize = buf.readDouble();
         break;
      case 2:
         this.diameter = buf.readDouble();
         this.targetSize = buf.readDouble();
         this.timeUntilTarget = buf.readVarLong();
         break;
      case 3:
         this.centerX = buf.readDouble();
         this.centerZ = buf.readDouble();
         break;
      case 4:
         this.centerX = buf.readDouble();
         this.centerZ = buf.readDouble();
         this.diameter = buf.readDouble();
         this.targetSize = buf.readDouble();
         this.timeUntilTarget = buf.readVarLong();
         this.size = buf.readVarIntFromBuffer();
         this.warningDistance = buf.readVarIntFromBuffer();
         this.warningTime = buf.readVarIntFromBuffer();
         break;
      case 5:
         this.warningTime = buf.readVarIntFromBuffer();
         break;
      case 6:
         this.warningDistance = buf.readVarIntFromBuffer();
      }

   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
      switch($SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action()[this.action.ordinal()]) {
      case 1:
         buf.writeDouble(this.targetSize);
         break;
      case 2:
         buf.writeDouble(this.diameter);
         buf.writeDouble(this.targetSize);
         buf.writeVarLong(this.timeUntilTarget);
         break;
      case 3:
         buf.writeDouble(this.centerX);
         buf.writeDouble(this.centerZ);
         break;
      case 4:
         buf.writeDouble(this.centerX);
         buf.writeDouble(this.centerZ);
         buf.writeDouble(this.diameter);
         buf.writeDouble(this.targetSize);
         buf.writeVarLong(this.timeUntilTarget);
         buf.writeVarIntToBuffer(this.size);
         buf.writeVarIntToBuffer(this.warningDistance);
         buf.writeVarIntToBuffer(this.warningTime);
         break;
      case 5:
         buf.writeVarIntToBuffer(this.warningTime);
         break;
      case 6:
         buf.writeVarIntToBuffer(this.warningDistance);
      }

   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleWorldBorder(this);
   }

   public void func_179788_a(WorldBorder border) {
      switch($SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action()[this.action.ordinal()]) {
      case 1:
         border.setTransition(this.targetSize);
         break;
      case 2:
         border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
         break;
      case 3:
         border.setCenter(this.centerX, this.centerZ);
         break;
      case 4:
         border.setCenter(this.centerX, this.centerZ);
         if(this.timeUntilTarget > 0L) {
            border.setTransition(this.diameter, this.targetSize, this.timeUntilTarget);
         } else {
            border.setTransition(this.targetSize);
         }

         border.setSize(this.size);
         border.setWarningDistance(this.warningDistance);
         border.setWarningTime(this.warningTime);
         break;
      case 5:
         border.setWarningTime(this.warningTime);
         break;
      case 6:
         border.setWarningDistance(this.warningDistance);
      }

   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action;
      if($SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action != null) {
         return var10000;
      } else {
         int[] var0 = new int[S44PacketWorldBorder.Action.values().length];

         try {
            var0[S44PacketWorldBorder.Action.INITIALIZE.ordinal()] = 4;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[S44PacketWorldBorder.Action.LERP_SIZE.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[S44PacketWorldBorder.Action.SET_CENTER.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[S44PacketWorldBorder.Action.SET_SIZE.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[S44PacketWorldBorder.Action.SET_WARNING_BLOCKS.ordinal()] = 6;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[S44PacketWorldBorder.Action.SET_WARNING_TIME.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$network$play$server$S44PacketWorldBorder$Action = var0;
         return var0;
      }
   }

   public static enum Action {
      SET_SIZE,
      LERP_SIZE,
      SET_CENTER,
      INITIALIZE,
      SET_WARNING_TIME,
      SET_WARNING_BLOCKS;
   }
}
