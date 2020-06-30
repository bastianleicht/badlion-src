package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.EntityUtils;

/** @deprecated */
@Deprecated
@ThreadSafe
public class AutoRetryHttpClient implements HttpClient {
   private final HttpClient backend;
   private final ServiceUnavailableRetryStrategy retryStrategy;
   private final Log log;

   public AutoRetryHttpClient(HttpClient client, ServiceUnavailableRetryStrategy retryStrategy) {
      this.log = LogFactory.getLog(this.getClass());
      Args.notNull(client, "HttpClient");
      Args.notNull(retryStrategy, "ServiceUnavailableRetryStrategy");
      this.backend = client;
      this.retryStrategy = retryStrategy;
   }

   public AutoRetryHttpClient() {
      this(new DefaultHttpClient(), new DefaultServiceUnavailableRetryStrategy());
   }

   public AutoRetryHttpClient(ServiceUnavailableRetryStrategy config) {
      this(new DefaultHttpClient(), config);
   }

   public AutoRetryHttpClient(HttpClient client) {
      this(client, new DefaultServiceUnavailableRetryStrategy());
   }

   public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException {
      HttpContext defaultContext = null;
      return this.execute(target, request, defaultContext);
   }

   public Object execute(HttpHost target, HttpRequest request, ResponseHandler responseHandler) throws IOException {
      return this.execute(target, request, responseHandler, (HttpContext)null);
   }

   public Object execute(HttpHost target, HttpRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException {
      HttpResponse resp = this.execute(target, request, context);
      return responseHandler.handleResponse(resp);
   }

   public HttpResponse execute(HttpUriRequest request) throws IOException {
      HttpContext context = null;
      return this.execute(request, context);
   }

   public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException {
      URI uri = request.getURI();
      HttpHost httpHost = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
      return this.execute((HttpHost)httpHost, (HttpRequest)request, (HttpContext)context);
   }

   public Object execute(HttpUriRequest request, ResponseHandler responseHandler) throws IOException {
      return this.execute((HttpUriRequest)request, (ResponseHandler)responseHandler, (HttpContext)null);
   }

   public Object execute(HttpUriRequest request, ResponseHandler responseHandler, HttpContext context) throws IOException {
      HttpResponse resp = this.execute(request, context);
      return responseHandler.handleResponse(resp);
   }

   public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context) throws IOException {
      int c = 1;

      while(true) {
         HttpResponse response = this.backend.execute(target, request, context);

         try {
            if(!this.retryStrategy.retryRequest(response, c, context)) {
               return response;
            }

            EntityUtils.consume(response.getEntity());
            long nextInterval = this.retryStrategy.getRetryInterval();

            try {
               this.log.trace("Wait for " + nextInterval);
               Thread.sleep(nextInterval);
            } catch (InterruptedException var10) {
               Thread.currentThread().interrupt();
               throw new InterruptedIOException();
            }
         } catch (RuntimeException var11) {
            try {
               EntityUtils.consume(response.getEntity());
            } catch (IOException var9) {
               this.log.warn("I/O error consuming response content", var9);
            }

            throw var11;
         }

         ++c;
      }
   }

   public ClientConnectionManager getConnectionManager() {
      return this.backend.getConnectionManager();
   }

   public HttpParams getParams() {
      return this.backend.getParams();
   }
}
