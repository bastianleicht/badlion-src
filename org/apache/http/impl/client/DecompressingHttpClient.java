package org.apache.http.impl.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.EntityEnclosingRequestWrapper;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

/** @deprecated */
@Deprecated
public class DecompressingHttpClient implements HttpClient {
   private final HttpClient backend;
   private final HttpRequestInterceptor acceptEncodingInterceptor;
   private final HttpResponseInterceptor contentEncodingInterceptor;

   public DecompressingHttpClient() {
      this(new DefaultHttpClient());
   }

   public DecompressingHttpClient(HttpClient backend) {
      this(backend, new RequestAcceptEncoding(), new ResponseContentEncoding());
   }

   DecompressingHttpClient(HttpClient backend, HttpRequestInterceptor requestInterceptor, HttpResponseInterceptor responseInterceptor) {
      this.backend = backend;
      this.acceptEncodingInterceptor = requestInterceptor;
      this.contentEncodingInterceptor = responseInterceptor;
   }

   public HttpParams getParams() {
      return this.backend.getParams();
   }

   public ClientConnectionManager getConnectionManager() {
      return this.backend.getConnectionManager();
   }

   public HttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
      return this.execute((HttpHost)this.getHttpHost(request), (HttpRequest)request, (HttpContext)((HttpContext)null));
   }

   public HttpClient getHttpClient() {
      return this.backend;
   }

   HttpHost getHttpHost(HttpUriRequest request) {
      URI uri = request.getURI();
      return URIUtils.extractHost(uri);
   }

   public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException, ClientProtocolException {
      return this.execute((HttpHost)this.getHttpHost(request), (HttpRequest)request, (HttpContext)context);
   }

   public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException, ClientProtocolException {
      return this.execute(target, request, (HttpContext)null);
   }

   public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException, ClientProtocolException {
      try {
         HttpContext localContext = (HttpContext)(context != null?context:new BasicHttpContext());
         HttpRequest wrapped;
         if(request instanceof HttpEntityEnclosingRequest) {
            wrapped = new EntityEnclosingRequestWrapper((HttpEntityEnclosingRequest)request);
         } else {
            wrapped = new RequestWrapper(request);
         }

         this.acceptEncodingInterceptor.process(wrapped, localContext);
         HttpResponse response = this.backend.execute(target, wrapped, localContext);

         try {
            this.contentEncodingInterceptor.process(response, localContext);
            if(Boolean.TRUE.equals(localContext.getAttribute("http.client.response.uncompressed"))) {
               response.removeHeaders("Content-Length");
               response.removeHeaders("Content-Encoding");
               response.removeHeaders("Content-MD5");
            }

            return response;
         } catch (HttpException var8) {
            EntityUtils.consume(response.getEntity());
            throw var8;
         } catch (IOException var9) {
            EntityUtils.consume(response.getEntity());
            throw var9;
         } catch (RuntimeException var10) {
            EntityUtils.consume(response.getEntity());
            throw var10;
         }
      } catch (HttpException var11) {
         throw new ClientProtocolException(var11);
      }
   }

   public Object execute(HttpUriRequest request, ResponseHandler responseHandler) throws IOException, ClientProtocolException {
      return this.execute((HttpHost)this.getHttpHost(request), (HttpRequest)request, (ResponseHandler)responseHandler);
   }

   public Object execute(HttpUriRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException, ClientProtocolException {
      return this.execute(this.getHttpHost(request), request, responseHandler, context);
   }

   public Object execute(HttpHost target, HttpRequest request, ResponseHandler responseHandler) throws IOException, ClientProtocolException {
      return this.execute(target, request, responseHandler, (HttpContext)null);
   }

   public Object execute(HttpHost target, HttpRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException, ClientProtocolException {
      HttpResponse response = this.execute(target, request, context);
      boolean var11 = false;

      Object var6;
      try {
         var11 = true;
         var6 = responseHandler.handleResponse(response);
         var11 = false;
      } finally {
         if(var11) {
            HttpEntity entity = response.getEntity();
            if(entity != null) {
               EntityUtils.consume(entity);
            }

         }
      }

      HttpEntity entity = response.getEntity();
      if(entity != null) {
         EntityUtils.consume(entity);
      }

      return var6;
   }
}
