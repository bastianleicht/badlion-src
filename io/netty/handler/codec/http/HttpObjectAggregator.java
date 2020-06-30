package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import java.util.List;

public class HttpObjectAggregator extends MessageToMessageDecoder {
   public static final int DEFAULT_MAX_COMPOSITEBUFFER_COMPONENTS = 1024;
   private static final FullHttpResponse CONTINUE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);
   private final int maxContentLength;
   private HttpObjectAggregator.AggregatedFullHttpMessage currentMessage;
   private boolean tooLongFrameFound;
   private int maxCumulationBufferComponents = 1024;
   private ChannelHandlerContext ctx;

   public HttpObjectAggregator(int maxContentLength) {
      if(maxContentLength <= 0) {
         throw new IllegalArgumentException("maxContentLength must be a positive integer: " + maxContentLength);
      } else {
         this.maxContentLength = maxContentLength;
      }
   }

   public final int getMaxCumulationBufferComponents() {
      return this.maxCumulationBufferComponents;
   }

   public final void setMaxCumulationBufferComponents(int maxCumulationBufferComponents) {
      if(maxCumulationBufferComponents < 2) {
         throw new IllegalArgumentException("maxCumulationBufferComponents: " + maxCumulationBufferComponents + " (expected: >= 2)");
      } else if(this.ctx == null) {
         this.maxCumulationBufferComponents = maxCumulationBufferComponents;
      } else {
         throw new IllegalStateException("decoder properties cannot be changed once the decoder is added to a pipeline.");
      }
   }

   protected void decode(final ChannelHandlerContext ctx, HttpObject msg, List out) throws Exception {
      HttpObjectAggregator.AggregatedFullHttpMessage currentMessage = this.currentMessage;
      if(msg instanceof HttpMessage) {
         this.tooLongFrameFound = false;

         assert currentMessage == null;

         HttpMessage m = (HttpMessage)msg;
         if(HttpHeaders.is100ContinueExpected(m)) {
            ctx.writeAndFlush(CONTINUE).addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if(!future.isSuccess()) {
                     ctx.fireExceptionCaught(future.cause());
                  }

               }
            });
         }

         if(!m.getDecoderResult().isSuccess()) {
            HttpHeaders.removeTransferEncodingChunked(m);
            out.add(toFullMessage(m));
            this.currentMessage = null;
            return;
         }

         if(msg instanceof HttpRequest) {
            HttpRequest header = (HttpRequest)msg;
            this.currentMessage = currentMessage = new HttpObjectAggregator.AggregatedFullHttpRequest(header, ctx.alloc().compositeBuffer(this.maxCumulationBufferComponents), (HttpHeaders)null);
         } else {
            if(!(msg instanceof HttpResponse)) {
               throw new Error();
            }

            HttpResponse header = (HttpResponse)msg;
            this.currentMessage = currentMessage = new HttpObjectAggregator.AggregatedFullHttpResponse(header, Unpooled.compositeBuffer(this.maxCumulationBufferComponents), (HttpHeaders)null);
         }

         HttpHeaders.removeTransferEncodingChunked(currentMessage);
      } else {
         if(!(msg instanceof HttpContent)) {
            throw new Error();
         }

         if(this.tooLongFrameFound) {
            if(msg instanceof LastHttpContent) {
               this.currentMessage = null;
            }

            return;
         }

         assert currentMessage != null;

         HttpContent chunk = (HttpContent)msg;
         CompositeByteBuf content = (CompositeByteBuf)currentMessage.content();
         if(content.readableBytes() > this.maxContentLength - chunk.content().readableBytes()) {
            this.tooLongFrameFound = true;
            currentMessage.release();
            this.currentMessage = null;
            throw new TooLongFrameException("HTTP content length exceeded " + this.maxContentLength + " bytes.");
         }

         if(chunk.content().isReadable()) {
            chunk.retain();
            content.addComponent(chunk.content());
            content.writerIndex(content.writerIndex() + chunk.content().readableBytes());
         }

         boolean last;
         if(!chunk.getDecoderResult().isSuccess()) {
            currentMessage.setDecoderResult(DecoderResult.failure(chunk.getDecoderResult().cause()));
            last = true;
         } else {
            last = chunk instanceof LastHttpContent;
         }

         if(last) {
            this.currentMessage = null;
            if(chunk instanceof LastHttpContent) {
               LastHttpContent trailer = (LastHttpContent)chunk;
               currentMessage.setTrailingHeaders(trailer.trailingHeaders());
            } else {
               currentMessage.setTrailingHeaders(new DefaultHttpHeaders());
            }

            currentMessage.headers().set((String)"Content-Length", (Object)String.valueOf(content.readableBytes()));
            out.add(currentMessage);
         }
      }

   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      super.channelInactive(ctx);
      if(this.currentMessage != null) {
         this.currentMessage.release();
         this.currentMessage = null;
      }

   }

   public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
      this.ctx = ctx;
   }

   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      super.handlerRemoved(ctx);
      if(this.currentMessage != null) {
         this.currentMessage.release();
         this.currentMessage = null;
      }

   }

   private static FullHttpMessage toFullMessage(HttpMessage msg) {
      if(msg instanceof FullHttpMessage) {
         return ((FullHttpMessage)msg).retain();
      } else {
         FullHttpMessage fullMsg;
         if(msg instanceof HttpRequest) {
            fullMsg = new HttpObjectAggregator.AggregatedFullHttpRequest((HttpRequest)msg, Unpooled.EMPTY_BUFFER, new DefaultHttpHeaders());
         } else {
            if(!(msg instanceof HttpResponse)) {
               throw new IllegalStateException();
            }

            fullMsg = new HttpObjectAggregator.AggregatedFullHttpResponse((HttpResponse)msg, Unpooled.EMPTY_BUFFER, new DefaultHttpHeaders());
         }

         return fullMsg;
      }
   }

   private abstract static class AggregatedFullHttpMessage extends DefaultByteBufHolder implements FullHttpMessage {
      protected final HttpMessage message;
      private HttpHeaders trailingHeaders;

      private AggregatedFullHttpMessage(HttpMessage message, ByteBuf content, HttpHeaders trailingHeaders) {
         super(content);
         this.message = message;
         this.trailingHeaders = trailingHeaders;
      }

      public HttpHeaders trailingHeaders() {
         return this.trailingHeaders;
      }

      public void setTrailingHeaders(HttpHeaders trailingHeaders) {
         this.trailingHeaders = trailingHeaders;
      }

      public HttpVersion getProtocolVersion() {
         return this.message.getProtocolVersion();
      }

      public FullHttpMessage setProtocolVersion(HttpVersion version) {
         this.message.setProtocolVersion(version);
         return this;
      }

      public HttpHeaders headers() {
         return this.message.headers();
      }

      public DecoderResult getDecoderResult() {
         return this.message.getDecoderResult();
      }

      public void setDecoderResult(DecoderResult result) {
         this.message.setDecoderResult(result);
      }

      public FullHttpMessage retain(int increment) {
         super.retain(increment);
         return this;
      }

      public FullHttpMessage retain() {
         super.retain();
         return this;
      }

      public abstract FullHttpMessage copy();

      public abstract FullHttpMessage duplicate();
   }

   private static final class AggregatedFullHttpRequest extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpRequest {
      private AggregatedFullHttpRequest(HttpRequest request, ByteBuf content, HttpHeaders trailingHeaders) {
         super(request, content, trailingHeaders, null);
      }

      public FullHttpRequest copy() {
         DefaultFullHttpRequest copy = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().copy());
         copy.headers().set(this.headers());
         copy.trailingHeaders().set(this.trailingHeaders());
         return copy;
      }

      public FullHttpRequest duplicate() {
         DefaultFullHttpRequest duplicate = new DefaultFullHttpRequest(this.getProtocolVersion(), this.getMethod(), this.getUri(), this.content().duplicate());
         duplicate.headers().set(this.headers());
         duplicate.trailingHeaders().set(this.trailingHeaders());
         return duplicate;
      }

      public FullHttpRequest retain(int increment) {
         super.retain(increment);
         return this;
      }

      public FullHttpRequest retain() {
         super.retain();
         return this;
      }

      public FullHttpRequest setMethod(HttpMethod method) {
         ((HttpRequest)this.message).setMethod(method);
         return this;
      }

      public FullHttpRequest setUri(String uri) {
         ((HttpRequest)this.message).setUri(uri);
         return this;
      }

      public HttpMethod getMethod() {
         return ((HttpRequest)this.message).getMethod();
      }

      public String getUri() {
         return ((HttpRequest)this.message).getUri();
      }

      public FullHttpRequest setProtocolVersion(HttpVersion version) {
         super.setProtocolVersion(version);
         return this;
      }
   }

   private static final class AggregatedFullHttpResponse extends HttpObjectAggregator.AggregatedFullHttpMessage implements FullHttpResponse {
      private AggregatedFullHttpResponse(HttpResponse message, ByteBuf content, HttpHeaders trailingHeaders) {
         super(message, content, trailingHeaders, null);
      }

      public FullHttpResponse copy() {
         DefaultFullHttpResponse copy = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().copy());
         copy.headers().set(this.headers());
         copy.trailingHeaders().set(this.trailingHeaders());
         return copy;
      }

      public FullHttpResponse duplicate() {
         DefaultFullHttpResponse duplicate = new DefaultFullHttpResponse(this.getProtocolVersion(), this.getStatus(), this.content().duplicate());
         duplicate.headers().set(this.headers());
         duplicate.trailingHeaders().set(this.trailingHeaders());
         return duplicate;
      }

      public FullHttpResponse setStatus(HttpResponseStatus status) {
         ((HttpResponse)this.message).setStatus(status);
         return this;
      }

      public HttpResponseStatus getStatus() {
         return ((HttpResponse)this.message).getStatus();
      }

      public FullHttpResponse setProtocolVersion(HttpVersion version) {
         super.setProtocolVersion(version);
         return this;
      }

      public FullHttpResponse retain(int increment) {
         super.retain(increment);
         return this;
      }

      public FullHttpResponse retain() {
         super.retain();
         return this;
      }
   }
}
