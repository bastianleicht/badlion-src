package org.apache.http.impl.execchain;

import java.io.IOException;
import java.io.InterruptedIOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpExecutionAware;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.execchain.ClientExecChain;
import org.apache.http.util.Args;

@Immutable
public class ServiceUnavailableRetryExec implements ClientExecChain {
   private final Log log = LogFactory.getLog(this.getClass());
   private final ClientExecChain requestExecutor;
   private final ServiceUnavailableRetryStrategy retryStrategy;

   public ServiceUnavailableRetryExec(ClientExecChain requestExecutor, ServiceUnavailableRetryStrategy retryStrategy) {
      Args.notNull(requestExecutor, "HTTP request executor");
      Args.notNull(retryStrategy, "Retry strategy");
      this.requestExecutor = requestExecutor;
      this.retryStrategy = retryStrategy;
   }

   public CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
      Header[] origheaders = request.getAllHeaders();
      int c = 1;

      while(true) {
         CloseableHttpResponse response = this.requestExecutor.execute(route, request, context, execAware);

         try {
            if(!this.retryStrategy.retryRequest(response, c, context)) {
               return response;
            }

            response.close();
            long nextInterval = this.retryStrategy.getRetryInterval();
            if(nextInterval > 0L) {
               try {
                  this.log.trace("Wait for " + nextInterval);
                  Thread.sleep(nextInterval);
               } catch (InterruptedException var11) {
                  Thread.currentThread().interrupt();
                  throw new InterruptedIOException();
               }
            }

            request.setHeaders(origheaders);
         } catch (RuntimeException var12) {
            response.close();
            throw var12;
         }

         ++c;
      }
   }
}
