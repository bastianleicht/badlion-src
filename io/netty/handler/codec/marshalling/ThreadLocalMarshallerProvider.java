package io.netty.handler.codec.marshalling;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.marshalling.MarshallerProvider;
import io.netty.util.concurrent.FastThreadLocal;
import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.MarshallingConfiguration;

public class ThreadLocalMarshallerProvider implements MarshallerProvider {
   private final FastThreadLocal marshallers = new FastThreadLocal();
   private final MarshallerFactory factory;
   private final MarshallingConfiguration config;

   public ThreadLocalMarshallerProvider(MarshallerFactory factory, MarshallingConfiguration config) {
      this.factory = factory;
      this.config = config;
   }

   public Marshaller getMarshaller(ChannelHandlerContext ctx) throws Exception {
      Marshaller marshaller = (Marshaller)this.marshallers.get();
      if(marshaller == null) {
         marshaller = this.factory.createMarshaller(this.config);
         this.marshallers.set(marshaller);
      }

      return marshaller;
   }
}
