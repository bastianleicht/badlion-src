package org.apache.http.impl;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpConnectionMetrics;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.HttpTransportMetrics;

@NotThreadSafe
public class HttpConnectionMetricsImpl implements HttpConnectionMetrics {
   public static final String REQUEST_COUNT = "http.request-count";
   public static final String RESPONSE_COUNT = "http.response-count";
   public static final String SENT_BYTES_COUNT = "http.sent-bytes-count";
   public static final String RECEIVED_BYTES_COUNT = "http.received-bytes-count";
   private final HttpTransportMetrics inTransportMetric;
   private final HttpTransportMetrics outTransportMetric;
   private long requestCount = 0L;
   private long responseCount = 0L;
   private Map metricsCache;

   public HttpConnectionMetricsImpl(HttpTransportMetrics inTransportMetric, HttpTransportMetrics outTransportMetric) {
      this.inTransportMetric = inTransportMetric;
      this.outTransportMetric = outTransportMetric;
   }

   public long getReceivedBytesCount() {
      return this.inTransportMetric != null?this.inTransportMetric.getBytesTransferred():-1L;
   }

   public long getSentBytesCount() {
      return this.outTransportMetric != null?this.outTransportMetric.getBytesTransferred():-1L;
   }

   public long getRequestCount() {
      return this.requestCount;
   }

   public void incrementRequestCount() {
      ++this.requestCount;
   }

   public long getResponseCount() {
      return this.responseCount;
   }

   public void incrementResponseCount() {
      ++this.responseCount;
   }

   public Object getMetric(String metricName) {
      Object value = null;
      if(this.metricsCache != null) {
         value = this.metricsCache.get(metricName);
      }

      if(value == null) {
         if("http.request-count".equals(metricName)) {
            value = Long.valueOf(this.requestCount);
         } else if("http.response-count".equals(metricName)) {
            value = Long.valueOf(this.responseCount);
         } else {
            if("http.received-bytes-count".equals(metricName)) {
               if(this.inTransportMetric != null) {
                  return Long.valueOf(this.inTransportMetric.getBytesTransferred());
               }

               return null;
            }

            if("http.sent-bytes-count".equals(metricName)) {
               if(this.outTransportMetric != null) {
                  return Long.valueOf(this.outTransportMetric.getBytesTransferred());
               }

               return null;
            }
         }
      }

      return value;
   }

   public void setMetric(String metricName, Object obj) {
      if(this.metricsCache == null) {
         this.metricsCache = new HashMap();
      }

      this.metricsCache.put(metricName, obj);
   }

   public void reset() {
      if(this.outTransportMetric != null) {
         this.outTransportMetric.reset();
      }

      if(this.inTransportMetric != null) {
         this.inTransportMetric.reset();
      }

      this.requestCount = 0L;
      this.responseCount = 0L;
      this.metricsCache = null;
   }
}
