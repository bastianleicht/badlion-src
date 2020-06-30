package org.apache.http.protocol;

import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.protocol.ChainBuilder;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.ImmutableHttpProcessor;

public class HttpProcessorBuilder {
   private ChainBuilder requestChainBuilder;
   private ChainBuilder responseChainBuilder;

   public static HttpProcessorBuilder create() {
      return new HttpProcessorBuilder();
   }

   private ChainBuilder getRequestChainBuilder() {
      if(this.requestChainBuilder == null) {
         this.requestChainBuilder = new ChainBuilder();
      }

      return this.requestChainBuilder;
   }

   private ChainBuilder getResponseChainBuilder() {
      if(this.responseChainBuilder == null) {
         this.responseChainBuilder = new ChainBuilder();
      }

      return this.responseChainBuilder;
   }

   public HttpProcessorBuilder addFirst(HttpRequestInterceptor e) {
      if(e == null) {
         return this;
      } else {
         this.getRequestChainBuilder().addFirst(e);
         return this;
      }
   }

   public HttpProcessorBuilder addLast(HttpRequestInterceptor e) {
      if(e == null) {
         return this;
      } else {
         this.getRequestChainBuilder().addLast(e);
         return this;
      }
   }

   public HttpProcessorBuilder add(HttpRequestInterceptor e) {
      return this.addLast(e);
   }

   public HttpProcessorBuilder addAllFirst(HttpRequestInterceptor... e) {
      if(e == null) {
         return this;
      } else {
         this.getRequestChainBuilder().addAllFirst((Object[])e);
         return this;
      }
   }

   public HttpProcessorBuilder addAllLast(HttpRequestInterceptor... e) {
      if(e == null) {
         return this;
      } else {
         this.getRequestChainBuilder().addAllLast((Object[])e);
         return this;
      }
   }

   public HttpProcessorBuilder addAll(HttpRequestInterceptor... e) {
      return this.addAllLast(e);
   }

   public HttpProcessorBuilder addFirst(HttpResponseInterceptor e) {
      if(e == null) {
         return this;
      } else {
         this.getResponseChainBuilder().addFirst(e);
         return this;
      }
   }

   public HttpProcessorBuilder addLast(HttpResponseInterceptor e) {
      if(e == null) {
         return this;
      } else {
         this.getResponseChainBuilder().addLast(e);
         return this;
      }
   }

   public HttpProcessorBuilder add(HttpResponseInterceptor e) {
      return this.addLast(e);
   }

   public HttpProcessorBuilder addAllFirst(HttpResponseInterceptor... e) {
      if(e == null) {
         return this;
      } else {
         this.getResponseChainBuilder().addAllFirst((Object[])e);
         return this;
      }
   }

   public HttpProcessorBuilder addAllLast(HttpResponseInterceptor... e) {
      if(e == null) {
         return this;
      } else {
         this.getResponseChainBuilder().addAllLast((Object[])e);
         return this;
      }
   }

   public HttpProcessorBuilder addAll(HttpResponseInterceptor... e) {
      return this.addAllLast(e);
   }

   public HttpProcessor build() {
      return new ImmutableHttpProcessor(this.requestChainBuilder != null?this.requestChainBuilder.build():null, this.responseChainBuilder != null?this.responseChainBuilder.build():null);
   }
}
