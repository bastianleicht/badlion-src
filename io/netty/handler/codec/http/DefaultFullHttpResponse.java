package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class DefaultFullHttpResponse extends DefaultHttpResponse implements FullHttpResponse {
   private final ByteBuf content;
   private final HttpHeaders trailingHeaders;
   private final boolean validateHeaders;

   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status) {
      this(version, status, Unpooled.buffer(0));
   }

   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content) {
      this(version, status, content, true);
   }

   public DefaultFullHttpResponse(HttpVersion version, HttpResponseStatus status, ByteBuf content, boolean validateHeaders) {
      super(version, status, validateHeaders);
      if(content == null) {
         throw new NullPointerException("content");
      } else {
         this.content = content;
         this.trailingHeaders = new DefaultHttpHeaders(validateHeaders);
         this.validateHeaders = validateHeaders;
      }
   }

   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   public ByteBuf content() {
      return this.content;
   }

   public int refCnt() {
      return this.content.refCnt();
   }

   public FullHttpResponse retain() {
      this.content.retain();
      return this;
   }

   public FullHttpResponse retain(int increment) {
      this.content.retain(increment);
      return this;
   }

   public boolean release() {
      return this.content.release();
   }

   public boolean release(int decrement) {
      return this.content.release(decrement);
   }

   public FullHttpResponse setProtocolVersion(HttpVersion version) {
      super.setProtocolVersion(version);
      return this;
   }

   public FullHttpResponse setStatus(HttpResponseStatus status) {
      super.setStatus(status);
      return this;
   }

   public FullHttpResponse copy() {
      DefaultFullHttpResponse copy = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().copy(), this.validateHeaders);
      copy.headers().set(this.headers());
      copy.trailingHeaders().set(this.trailingHeaders());
      return copy;
   }

   public FullHttpResponse duplicate() {
      DefaultFullHttpResponse duplicate = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().duplicate(), this.validateHeaders);
      duplicate.headers().set(this.headers());
      duplicate.trailingHeaders().set(this.trailingHeaders());
      return duplicate;
   }
}
