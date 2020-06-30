package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Marshaller;

public interface MarshallerProvider {
   Marshaller getMarshaller(ChannelHandlerContext var1) throws Exception;
}
