package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.udt.nio.NioUdtByteConnectorChannel;
import io.netty.channel.udt.nio.NioUdtProvider;

public class NioUdtByteRendezvousChannel extends NioUdtByteConnectorChannel {
   public NioUdtByteRendezvousChannel() {
      super((SocketChannelUDT)NioUdtProvider.newRendezvousChannelUDT(TypeUDT.STREAM));
   }
}
