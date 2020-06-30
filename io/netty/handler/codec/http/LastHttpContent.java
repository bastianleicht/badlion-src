package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;

public interface LastHttpContent extends HttpContent {
   LastHttpContent EMPTY_LAST_CONTENT = new LastHttpContent() {
      public ByteBuf content() {
         return Unpooled.EMPTY_BUFFER;
      }

      public LastHttpContent copy() {
         return EMPTY_LAST_CONTENT;
      }

      public LastHttpContent duplicate() {
         return this;
      }

      public HttpHeaders trailingHeaders() {
         return HttpHeaders.EMPTY_HEADERS;
      }

      public DecoderResult getDecoderResult() {
         return DecoderResult.SUCCESS;
      }

      public void setDecoderResult(DecoderResult result) {
         throw new UnsupportedOperationException("read only");
      }

      public int refCnt() {
         return 1;
      }

      public LastHttpContent retain() {
         return this;
      }

      public LastHttpContent retain(int increment) {
         return this;
      }

      public boolean release() {
         return false;
      }

      public boolean release(int decrement) {
         return false;
      }

      public String toString() {
         return "EmptyLastHttpContent";
      }
   };

   HttpHeaders trailingHeaders();

   LastHttpContent copy();

   LastHttpContent retain(int var1);

   LastHttpContent retain();
}
