package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.UnmarshallerProvider;
import io.netty.util.concurrent.FastThreadLocal;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

public class ThreadLocalUnmarshallerProvider implements UnmarshallerProvider {
   private final FastThreadLocal unmarshallers = new FastThreadLocal();
   private final MarshallerFactory factory;
   private final MarshallingConfiguration config;

   public ThreadLocalUnmarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
      this.factory = factory;
      this.config = config;
   }

   public Unmarshaller getUnmarshaller(ChannelHandlerContext ctx) throws Exception {
      Unmarshaller unmarshaller = (Unmarshaller)this.unmarshallers.get();
      if(unmarshaller == null) {
         unmarshaller = this.factory.createUnmarshaller(this.config);
         this.unmarshallers.set(unmarshaller);
      }

      return unmarshaller;
   }
}
