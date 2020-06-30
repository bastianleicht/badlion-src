package org.apache.http;

public interface HttpConnectionMetrics {
   long getRequestCount();

   long getResponseCount();

   long getSentBytesCount();

   long getReceivedBytesCount();

   Object getMetric(String var1);

   void reset();
}
