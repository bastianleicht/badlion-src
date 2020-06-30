package io.netty.handler.codec.sctp;

import io.netty.channel.sctp.SctpMessage;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.sctp.SctpMessageCompletionHandler;

public abstract class SctpMessageToMessageDecoder extends MessageToMessageDecoder {
   public boolean acceptInboundMessage(Object msg) throws Exception {
      if(msg instanceof SctpMessage) {
         SctpMessage sctpMsg = (SctpMessage)msg;
         if(sctpMsg.isComplete()) {
            return true;
         } else {
            throw new CodecException(String.format("Received SctpMessage is not complete, please add %s in the pipeline before this handler", new Object[]{SctpMessageCompletionHandler.class.getSimpleName()}));
         }
      } else {
         return false;
      }
   }
}
