package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.DefaultUnmarshallerProvider;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ContextBoundUnmarshallerProvider extends DefaultUnmarshallerProvider {
   private static final AttributeKey UNMARSHALLER = AttributeKey.valueOf(ContextBoundUnmarshallerProvider.class.getName() + ".UNMARSHALLER");

   public ContextBoundUnmarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
      super(factory, config);
   }

   public Unmarshaller getUnmarshaller(ChannelHandlerContext ctx) throws Exception {
      Attribute<Unmarshaller> attr = ctx.attr(UNMARSHALLER);
      Unmarshaller unmarshaller = (Unmarshaller)attr.get();
      if(unmarshaller == null) {
         unmarshaller = super.getUnmarshaller(ctx);
         attr.set(unmarshaller);
      }

      return unmarshaller;
   }
}
