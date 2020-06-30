package io.netty.channel.udt.nio;

import com.barchart.udt.TypeUDT;
import com.barchart.udt.nio.SocketChannelUDT;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.udt.nio.NioUdtAcceptorChannel;
import io.netty.channel.udt.nio.NioUdtMessageConnectorChannel;
import java.util.List;

public class NioUdtMessageAcceptorChannel extends NioUdtAcceptorChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);

   public NioUdtMessageAcceptorChannel() {
      super(TypeUDT.DATAGRAM);
   }

   protected int doReadMessages(List buf) throws Exception {
      SocketChannelUDT channelUDT = this.javaChannel().accept();
      if(channelUDT == null) {
         return 0;
      } else {
         buf.add(new NioUdtMessageConnectorChannel(this, channelUDT));
         return 1;
      }
   }

   public ChannelMetadata metadata() {
      return METADATA;
   }
}
