package io.netty.channel;

import io.netty.util.ReferenceCounted;
import java.net.SocketAddress;

public interface AddressedEnvelope extends ReferenceCounted {
   Object content();

   SocketAddress sender();

   SocketAddress recipient();
}
