package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ConnectionReuseStrategy;
import org.apache.http.HttpClientConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.protocol.RequestClientConnControl;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.ConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.ConnectionShutdownException;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.impl.execchain.ConnectionHolder;
import org.apache.http.impl.execchain.Proxies;
import org.apache.http.impl.execchain.RequestAbortedException;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.ImmutableHttpProcessor;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.apache.http.util.Args;
import org.apache.http.util.VersionInfo;

@Immutable
public class MinimalClientExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final HttpRequestExecutor requestExecutor;
   private final HttpClientConnectionManager connManager;
   private final ConnectionReuseStrategy reuseStrategy;
   private final ConnectionKeepAliveStrategy keepAliveStrategy;
   private final HttpProcessor httpProcessor;

   public MinimalClientExec(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy) {
      Args.notNull(requestExecutor, "HTTP request executor");
      Args.notNull(connManager, "Client connection manager");
      Args.notNull(reuseStrategy, "Connection reuse strategy");
      Args.notNull(keepAliveStrategy, "Connection keep alive strategy");
      this.httpProcessor = new ImmutableHttpProcessor(new HttpRequestInterceptor[]{new RequestContent(), new RequestTargetHost(), new RequestClientConnControl(), new RequestUserAgent(VersionInfo.getUserAgent("Apache-HttpClient", "org.apache.http.client", this.getClass()))});
      this.requestExecutor = requestExecutor;
      this.connManager = connManager;
      this.reuseStrategy = reuseStrategy;
      this.keepAliveStrategy = keepAliveStrategy;
   }

   static void rewriteRequestURI(HttpRequestWrapper request, HttpRoute route) throws ProtocolException {
      try {
         URI uri = request.getURI();
         if(uri != null) {
            if(uri.isAbsolute()) {
               uri = URIUtils.rewriteURI(uri, (HttpHost)null, true);
            } else {
               uri = URIUtils.rewriteURI(uri);
            }

            request.setURI(uri);
         }

      } catch (URISyntaxException var3) {
         throw new ProtocolException("Invalid URI: " + request.getRequestLine().getUri(), var3);
      }
   }

   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
      Args.notNull(route, "HTTP route");
      Args.notNull(request, "HTTP request");
      Args.notNull(context, "HTTP context");
      rewriteRequestURI(request, route);
      ConnectionRequest connRequest = this.connManager.requestConnection(route, (Object)null);
      if(execAware != null) {
         if(execAware.isAborted()) {
            connRequest.cancel();
            throw new RequestAbortedException("Request aborted");
         }

         execAware.setCancellable(connRequest);
      }

      RequestConfig config = context.getRequestConfig();

      HttpClientConnection managedConn;
      try {
         int timeout = config.getConnectionRequestTimeout();
         managedConn = connRequest.get(timeout > 0?(long)timeout:0L, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var19) {
         Thread.currentThread().interrupt();
         throw new RequestAbortedException("Request aborted", var19);
      } catch (ExecutionException var20) {
         Throwable cause = var20.getCause();
         if(cause == null) {
            cause = var20;
         }

         throw new RequestAbortedException("Request execution failed", cause);
      }

      ConnectionHolder releaseTrigger = new ConnectionHolder(this.log, this.connManager, managedConn);

      try {
         if(execAware != null) {
            if(execAware.isAborted()) {
               releaseTrigger.close();
               throw new RequestAbortedException("Request aborted");
            }

            execAware.setCancellable(releaseTrigger);
         }

         if(!managedConn.isOpen()) {
            int timeout = config.getConnectTimeout();
            this.connManager.connect(managedConn, route, timeout > 0?timeout:0, context);
            this.connManager.routeComplete(managedConn, route, context);
         }

         int timeout = config.getSocketTimeout();
         if(timeout >= 0) {
            managedConn.setSocketTimeout(timeout);
         }

         HttpHost target = null;
         HttpRequest original = request.getOriginal();
         if(original instanceof HttpUriRequest) {
            URI uri = ((HttpUriRequest)original).getURI();
            if(uri.isAbsolute()) {
               target = new HttpHost(uri.getHost(), uri.getPort(), uri.getScheme());
            }
         }

         if(target == null) {
            target = route.getTargetHost();
         }

         context.setAttribute("http.target_host", target);
         context.setAttribute("http.request", request);
         context.setAttribute("http.connection", managedConn);
         context.setAttribute("http.route", route);
         this.httpProcessor.process(request, context);
         HttpResponse response = this.requestExecutor.execute(request, managedConn, context);
         this.httpProcessor.process(response, context);
         if(this.reuseStrategy.keepAlive(response, context)) {
            long duration = this.keepAliveStrategy.getKeepAliveDuration(response, context);
            releaseTrigger.setValidFor(duration, TimeUnit.MILLISECONDS);
            releaseTrigger.markReusable();
         } else {
            releaseTrigger.markNonReusable();
         }

         HttpEntity entity = response.getEntity();
         if(entity != null && entity.isStreaming()) {
            return Proxies.enhanceResponse(response, releaseTrigger);
         } else {
            releaseTrigger.releaseConnection();
            return Proxies.enhanceResponse(response, (ConnectionHolder)null);
         }
      } catch (ConnectionShutdownException var15) {
         InterruptedIOException ioex = new InterruptedIOException("Connection has been shut down");
         ioex.initCause(var15);
         throw ioex;
      } catch (HttpException var16) {
         releaseTrigger.abortConnection();
         throw var16;
      } catch (IOException var17) {
         releaseTrigger.abortConnection();
         throw var17;
      } catch (RuntimeException var18) {
         releaseTrigger.abortConnection();
         throw var18;
      }
   }
}
