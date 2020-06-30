package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpObject;

public class DefaultHttpObject implements HttpObject {
   private DecoderResult decoderResult = DecoderResult.SUCCESS;

   public DecoderResult getDecoderResult() {
      return this.decoderResult;
   }

   public void setDecoderResult(DecoderResult decoderResult) {
      if(decoderResult == null) {
         throw new NullPointerException("decoderResult");
      } else {
         this.decoderResult = decoderResult;
      }
   }
}
