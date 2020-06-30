package org.apache.http.client;

import org.apache.http.HttpResponse;

public interface ConnectionBackoffStrategy {
   boolean shouldBackoff(Throwable var1);

   boolean shouldBackoff(HttpResponse var1);
}
