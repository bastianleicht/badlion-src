package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import org.jboss.marshalling.Unmarshaller;

public interface UnmarshallerProvider {
   Unmarshaller getUnmarshaller(ChannelHandlerContext var1) throws Exception;
}
