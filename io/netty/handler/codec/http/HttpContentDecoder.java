package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.ComposedLastHttpContent;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.util.ReferenceCountUtil;
import java.util.List;

public abstract class HttpContentDecoder extends MessageToMessageDecoder {
   private EmbeddedChannel decoder;
   private HttpMessage message;
   private boolean decodeStarted;
   private boolean continueResponse;

   protected void decode(ChannelHandlerContext ctx, HttpObject msg, List out) throws Exception {
      if(msg instanceof HttpResponse && ((HttpResponse)msg).getStatus().code() == 100) {
         if(!(msg instanceof LastHttpContent)) {
            this.continueResponse = true;
         }

         out.add(ReferenceCountUtil.retain(msg));
      } else if(this.continueResponse) {
         if(msg instanceof LastHttpContent) {
            this.continueResponse = false;
         }

         out.add(ReferenceCountUtil.retain(msg));
      } else {
         if(msg instanceof HttpMessage) {
            assert this.message == null;

            this.message = (HttpMessage)msg;
            this.decodeStarted = false;
            this.cleanup();
         }

         if(msg instanceof HttpContent) {
            HttpContent c = (HttpContent)msg;
            if(!this.decodeStarted) {
               this.decodeStarted = true;
               HttpMessage message = this.message;
               HttpHeaders headers = message.headers();
               this.message = null;
               String contentEncoding = headers.get("Content-Encoding");
               if(contentEncoding != null) {
                  contentEncoding = contentEncoding.trim();
               } else {
                  contentEncoding = "identity";
               }

               if((this.decoder = this.newContentDecoder(contentEncoding)) != null) {
                  String targetContentEncoding = this.getTargetContentEncoding(contentEncoding);
                  if("identity".equals(targetContentEncoding)) {
                     headers.remove("Content-Encoding");
                  } else {
                     headers.set((String)"Content-Encoding", (Object)targetContentEncoding);
                  }

                  out.add(message);
                  this.decodeContent(c, out);
                  if(headers.contains("Content-Length")) {
                     int contentLength = 0;
                     int size = out.size();

                     for(int i = 0; i < size; ++i) {
                        Object o = out.get(i);
                        if(o instanceof HttpContent) {
                           contentLength += ((HttpContent)o).content().readableBytes();
                        }
                     }

                     headers.set((String)"Content-Length", (Object)Integer.toString(contentLength));
                  }

                  return;
               }

               if(c instanceof LastHttpContent) {
                  this.decodeStarted = false;
               }

               out.add(message);
               out.add(c.retain());
               return;
            }

            if(this.decoder != null) {
               this.decodeContent(c, out);
            } else {
               if(c instanceof LastHttpContent) {
                  this.decodeStarted = false;
               }

               out.add(c.retain());
            }
         }

      }
   }

   private void decodeContent(HttpContent c, List out) {
      ByteBuf content = c.content();
      this.decode(content, out);
      if(c instanceof LastHttpContent) {
         this.finishDecode(out);
         LastHttpContent last = (LastHttpContent)c;
         HttpHeaders headers = last.trailingHeaders();
         if(headers.isEmpty()) {
            out.add(LastHttpContent.EMPTY_LAST_CONTENT);
         } else {
            out.add(new ComposedLastHttpContent(headers));
         }
      }

   }

   protected abstract EmbeddedChannel newContentDecoder(String var1) throws Exception;

   protected String getTargetContentEncoding(String contentEncoding) throws Exception {
      return "identity";
   }

   public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
      this.cleanup();
      super.handlerRemoved(ctx);
   }

   public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      this.cleanup();
      super.channelInactive(ctx);
   }

   private void cleanup() {
      if(this.decoder != null) {
         if(this.decoder.finish()) {
            while(true) {
               ByteBuf buf = (ByteBuf)this.decoder.readOutbound();
               if(buf == null) {
                  break;
               }

               buf.release();
            }
         }

         this.decoder = null;
      }

   }

   private void decode(ByteBuf in, List out) {
      this.decoder.writeInbound(new Object[]{in.retain()});
      this.fetchDecoderOutput(out);
   }

   private void finishDecode(List out) {
      if(this.decoder.finish()) {
         this.fetchDecoderOutput(out);
      }

      this.decodeStarted = false;
      this.decoder = null;
   }

   private void fetchDecoderOutput(List out) {
      while(true) {
         ByteBuf buf = (ByteBuf)this.decoder.readInbound();
         if(buf == null) {
            return;
         }

         if(!buf.isReadable()) {
            buf.release();
         } else {
            out.add(new DefaultHttpContent(buf));
         }
      }
   }
}
