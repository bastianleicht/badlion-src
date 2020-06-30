package io.netty.handler.codec.http.cors;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

public class CorsHandler extends ChannelDuplexHandler {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(CorsHandler.class);
   private final CorsConfig config;
   private HttpRequest request;

   public CorsHandler(CorsConfig config) {
      this.config = config;
   }

   public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
      if(this.config.isCorsSupportEnabled() && msg instanceof HttpRequest) {
         this.request = (HttpRequest)msg;
         if(isPreflightRequest(this.request)) {
            this.handlePreflight(ctx, this.request);
            return;
         }

         if(this.config.isShortCurcuit() && !this.validateOrigin()) {
            forbidden(ctx, this.request);
            return;
         }
      }

      ctx.fireChannelRead(msg);
   }

   private void handlePreflight(ChannelHandlerContext ctx, HttpRequest request) {
      HttpResponse response = new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
      if(this.setOrigin(response)) {
         this.setAllowMethods(response);
         this.setAllowHeaders(response);
         this.setAllowCredentials(response);
         this.setMaxAge(response);
         this.setPreflightHeaders(response);
      }

      ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
   }

   private void setPreflightHeaders(HttpResponse response) {
      response.headers().add(this.config.preflightResponseHeaders());
   }

   private boolean setOrigin(HttpResponse response) {
      String origin = this.request.headers().get("Origin");
      if(origin != null) {
         if("null".equals(origin) && this.config.isNullOriginAllowed()) {
            setAnyOrigin(response);
            return true;
         }

         if(this.config.isAnyOriginSupported()) {
            if(this.config.isCredentialsAllowed()) {
               this.echoRequestOrigin(response);
               setVaryHeader(response);
            } else {
               setAnyOrigin(response);
            }

            return true;
         }

         if(this.config.origins().contains(origin)) {
            setOrigin(response, origin);
            setVaryHeader(response);
            return true;
         }

         logger.debug("Request origin [" + origin + "] was not among the configured origins " + this.config.origins());
      }

      return false;
   }

   private boolean validateOrigin() {
      if(this.config.isAnyOriginSupported()) {
         return true;
      } else {
         String origin = this.request.headers().get("Origin");
         return origin == null?true:("null".equals(origin) && this.config.isNullOriginAllowed()?true:this.config.origins().contains(origin));
      }
   }

   private void echoRequestOrigin(HttpResponse response) {
      setOrigin(response, this.request.headers().get("Origin"));
   }

   private static void setVaryHeader(HttpResponse response) {
      response.headers().set((String)"Vary", (Object)"Origin");
   }

   private static void setAnyOrigin(HttpResponse response) {
      setOrigin(response, "*");
   }

   private static void setOrigin(HttpResponse response, String origin) {
      response.headers().set((String)"Access-Control-Allow-Origin", (Object)origin);
   }

   private void setAllowCredentials(HttpResponse response) {
      if(this.config.isCredentialsAllowed()) {
         response.headers().set((String)"Access-Control-Allow-Credentials", (Object)"true");
      }

   }

   private static boolean isPreflightRequest(HttpRequest request) {
      HttpHeaders headers = request.headers();
      return request.getMethod().equals(HttpMethod.OPTIONS) && headers.contains("Origin") && headers.contains("Access-Control-Request-Method");
   }

   private void setExposeHeaders(HttpResponse response) {
      if(!this.config.exposedHeaders().isEmpty()) {
         response.headers().set((String)"Access-Control-Expose-Headers", (Iterable)this.config.exposedHeaders());
      }

   }

   private void setAllowMethods(HttpResponse response) {
      response.headers().set((String)"Access-Control-Allow-Methods", (Iterable)this.config.allowedRequestMethods());
   }

   private void setAllowHeaders(HttpResponse response) {
      response.headers().set((String)"Access-Control-Allow-Headers", (Iterable)this.config.allowedRequestHeaders());
   }

   private void setMaxAge(HttpResponse response) {
      response.headers().set((String)"Access-Control-Max-Age", (Object)Long.valueOf(this.config.maxAge()));
   }

   public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
      if(this.config.isCorsSupportEnabled() && msg instanceof HttpResponse) {
         HttpResponse response = (HttpResponse)msg;
         if(this.setOrigin(response)) {
            this.setAllowCredentials(response);
            this.setAllowHeaders(response);
            this.setExposeHeaders(response);
         }
      }

      ctx.writeAndFlush(msg, promise);
   }

   public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
      logger.error("Caught error in CorsHandler", cause);
      ctx.fireExceptionCaught(cause);
   }

   private static void forbidden(ChannelHandlerContext ctx, HttpRequest request) {
      ctx.writeAndFlush(new DefaultFullHttpResponse(request.getProtocolVersion(), HttpResponseStatus.FORBIDDEN)).addListener(ChannelFutureListener.CLOSE);
   }
}
