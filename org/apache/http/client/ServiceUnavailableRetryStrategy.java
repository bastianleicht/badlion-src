package org.apache.http.client;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface ServiceUnavailableRetryStrategy {
   boolean retryRequest(HttpResponse var1, int var2, HttpContext var3);

   long getRetryInterval();
}
