package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;

public interface HttpObject {
   DecoderResult getDecoderResult();

   void setDecoderResult(DecoderResult var1);
}
