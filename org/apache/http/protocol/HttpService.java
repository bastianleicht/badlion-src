package org.apache.http.protocol;

import java.io.IOException;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpServerConnection;
import org.apache.http.HttpVersion;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.ProtocolException;
import org.apache.http.UnsupportedHttpVersionException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpExpectationVerifier;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.protocol.HttpRequestHandlerMapper;
import org.apache.http.protocol.HttpRequestHandlerResolver;
import org.apache.http.util.Args;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;

@Immutable
public class HttpService {
   private volatile HttpParams params;
   private volatile HttpProcessor processor;
   private volatile HttpRequestHandlerMapper handlerMapper;
   private volatile ConnectionReuseStrategy connStrategy;
   private volatile HttpResponseFactory responseFactory;
   private volatile HttpExpectationVerifier expectationVerifier;

   /** @deprecated */
   @Deprecated
   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpExpectationVerifier expectationVerifier, HttpParams params) {
      this(processor, connStrategy, responseFactory, (HttpRequestHandlerMapper)(new HttpService.HttpRequestHandlerResolverAdapter(handlerResolver)), (HttpExpectationVerifier)expectationVerifier);
      this.params = params;
   }

   /** @deprecated */
   @Deprecated
   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerResolver handlerResolver, HttpParams params) {
      this(processor, connStrategy, responseFactory, (HttpRequestHandlerMapper)(new HttpService.HttpRequestHandlerResolverAdapter(handlerResolver)), (HttpExpectationVerifier)null);
      this.params = params;
   }

   /** @deprecated */
   @Deprecated
   public HttpService(HttpProcessor proc, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory) {
      this.params = null;
      this.processor = null;
      this.handlerMapper = null;
      this.connStrategy = null;
      this.responseFactory = null;
      this.expectationVerifier = null;
      this.setHttpProcessor(proc);
      this.setConnReuseStrategy(connStrategy);
      this.setResponseFactory(responseFactory);
   }

   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerMapper handlerMapper, HttpExpectationVerifier expectationVerifier) {
      this.params = null;
      this.processor = null;
      this.handlerMapper = null;
      this.connStrategy = null;
      this.responseFactory = null;
      this.expectationVerifier = null;
      this.processor = (HttpProcessor)Args.notNull(processor, "HTTP processor");
      this.connStrategy = (ConnectionReuseStrategy)(connStrategy != null?connStrategy:DefaultConnectionReuseStrategy.INSTANCE);
      this.responseFactory = (HttpResponseFactory)(responseFactory != null?responseFactory:DefaultHttpResponseFactory.INSTANCE);
      this.handlerMapper = handlerMapper;
      this.expectationVerifier = expectationVerifier;
   }

   public HttpService(HttpProcessor processor, ConnectionReuseStrategy connStrategy, HttpResponseFactory responseFactory, HttpRequestHandlerMapper handlerMapper) {
      this(processor, connStrategy, responseFactory, (HttpRequestHandlerMapper)handlerMapper, (HttpExpectationVerifier)null);
   }

   public HttpService(HttpProcessor processor, HttpRequestHandlerMapper handlerMapper) {
      this(processor, (ConnectionReuseStrategy)null, (HttpResponseFactory)null, (HttpRequestHandlerMapper)handlerMapper, (HttpExpectationVerifier)null);
   }

   /** @deprecated */
   @Deprecated
   public void setHttpProcessor(HttpProcessor processor) {
      Args.notNull(processor, "HTTP processor");
      this.processor = processor;
   }

   /** @deprecated */
   @Deprecated
   public void setConnReuseStrategy(ConnectionReuseStrategy connStrategy) {
      Args.notNull(connStrategy, "Connection reuse strategy");
      this.connStrategy = connStrategy;
   }

   /** @deprecated */
   @Deprecated
   public void setResponseFactory(HttpResponseFactory responseFactory) {
      Args.notNull(responseFactory, "Response factory");
      this.responseFactory = responseFactory;
   }

   /** @deprecated */
   @Deprecated
   public void setParams(HttpParams params) {
      this.params = params;
   }

   /** @deprecated */
   @Deprecated
   public void setHandlerResolver(HttpRequestHandlerResolver handlerResolver) {
      this.handlerMapper = new HttpService.HttpRequestHandlerResolverAdapter(handlerResolver);
   }

   /** @deprecated */
   @Deprecated
   public void setExpectationVerifier(HttpExpectationVerifier expectationVerifier) {
      this.expectationVerifier = expectationVerifier;
   }

   /** @deprecated */
   @Deprecated
   public HttpParams getParams() {
      return this.params;
   }

   public void handleRequest(HttpServerConnection conn, HttpContext context) throws IOException, HttpException {
      context.setAttribute("http.connection", conn);
      HttpResponse response = null;

      try {
         HttpRequest request = conn.receiveRequestHeader();
         if(request instanceof HttpEntityEnclosingRequest) {
            if(((HttpEntityEnclosingRequest)request).expectContinue()) {
               response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 100, context);
               if(this.expectationVerifier != null) {
                  try {
                     this.expectationVerifier.verify(request, response, context);
                  } catch (HttpException var6) {
                     response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, context);
                     this.handleException(var6, response);
                  }
               }

               if(response.getStatusLine().getStatusCode() < 200) {
                  conn.sendResponseHeader(response);
                  conn.flush();
                  response = null;
                  conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
               }
            } else {
               conn.receiveRequestEntity((HttpEntityEnclosingRequest)request);
            }
         }

         context.setAttribute("http.request", request);
         if(response == null) {
            response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_1, 200, context);
            this.processor.process(request, context);
            this.doService(request, response, context);
         }

         if(request instanceof HttpEntityEnclosingRequest) {
            HttpEntity entity = ((HttpEntityEnclosingRequest)request).getEntity();
            EntityUtils.consume(entity);
         }
      } catch (HttpException var7) {
         response = this.responseFactory.newHttpResponse(HttpVersion.HTTP_1_0, 500, context);
         this.handleException(var7, response);
      }

      context.setAttribute("http.response", response);
      this.processor.process(response, context);
      conn.sendResponseHeader(response);
      conn.sendResponseEntity(response);
      conn.flush();
      if(!this.connStrategy.keepAlive(response, context)) {
         conn.close();
      }

   }

   protected void handleException(HttpException ex, HttpResponse response) {
      if(ex instanceof MethodNotSupportedException) {
         response.setStatusCode(501);
      } else if(ex instanceof UnsupportedHttpVersionException) {
         response.setStatusCode(505);
      } else if(ex instanceof ProtocolException) {
         response.setStatusCode(400);
      } else {
         response.setStatusCode(500);
      }

      String message = ex.getMessage();
      if(message == null) {
         message = ex.toString();
      }

      byte[] msg = EncodingUtils.getAsciiBytes(message);
      ByteArrayEntity entity = new ByteArrayEntity(msg);
      entity.setContentType("text/plain; charset=US-ASCII");
      response.setEntity(entity);
   }

   protected void doService(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
      HttpRequestHandler handler = null;
      if(this.handlerMapper != null) {
         handler = this.handlerMapper.lookup(request);
      }

      if(handler != null) {
         handler.handle(request, response, context);
      } else {
         response.setStatusCode(501);
      }

   }

   /** @deprecated */
   @Deprecated
   private static class HttpRequestHandlerResolverAdapter implements HttpRequestHandlerMapper {
      private final HttpRequestHandlerResolver resolver;

      public HttpRequestHandlerResolverAdapter(HttpRequestHandlerResolver resolver) {
         this.resolver = resolver;
      }

      public HttpRequestHandler lookup(HttpRequest request) {
         return this.resolver.lookup(request.getRequestLine().getUri());
      }
   }
}
