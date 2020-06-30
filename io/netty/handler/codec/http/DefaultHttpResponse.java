package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.StringUtil;

public class DefaultHttpResponse extends DefaultHttpMessage implements HttpResponse {
   private HttpResponseStatus status;

   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status) {
      this(version, status, true);
   }

   public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
      super(version, validateHeaders);
      if(status == null) {
         throw new NullPointerException("status");
      } else {
         this.status = status;
      }
   }

   public HttpResponseStatus getStatus() {
      return this.status;
   }

   public HttpResponse setStatus(HttpResponseStatus status) {
      if(status == null) {
         throw new NullPointerException("status");
      } else {
         this.status = status;
         return this;
      }
   }

   public HttpResponse setProtocolVersion(HttpVersion version) {
      super.setProtocolVersion(version);
      return this;
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(StringUtil.simpleClassName((Object)this));
      buf.append("(decodeResult: ");
      buf.append(this.getDecoderResult());
      buf.append(')');
      buf.append(StringUtil.NEWLINE);
      buf.append(this.getProtocolVersion().text());
      buf.append(' ');
      buf.append(this.getStatus());
      buf.append(StringUtil.NEWLINE);
      this.appendHeaders(buf);
      buf.setLength(buf.length() - StringUtil.NEWLINE.length());
      return buf.toString();
   }
}
