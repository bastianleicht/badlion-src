package org.apache.http.impl.client;

import org.apache.http.HttpResponse;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class DefaultServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {
   private final int maxRetries;
   private final long retryInterval;

   public DefaultServiceUnavailableRetryStrategy(int maxRetries, int retryInterval) {
      Args.positive(maxRetries, "Max retries");
      Args.positive(retryInterval, "Retry interval");
      this.maxRetries = maxRetries;
      this.retryInterval = (long)retryInterval;
   }

   public DefaultServiceUnavailableRetryStrategy() {
      this(1, 1000);
   }

   public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
      return executionCount <= this.maxRetries && response.getStatusLine().getStatusCode() == 503;
   }

   public long getRetryInterval() {
      return this.retryInterval;
   }
}
