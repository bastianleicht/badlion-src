package org.apache.http.client;

import java.io.IOException;
import org.apache.http.protocol.HttpContext;

public interface HttpRequestRetryHandler {
   boolean retryRequest(IOException var1, int var2, HttpContext var3);
}
