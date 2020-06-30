package net.minecraft.network;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.PacketBuffer;

public interface Packet {
   void readPacketData(PacketBuffer var1) throws IOException;

   void writePacketData(PacketBuffer var1) throws IOException;

   void processPacket(INetHandler var1);
}
