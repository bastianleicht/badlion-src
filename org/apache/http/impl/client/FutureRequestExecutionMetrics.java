package org.apache.http.impl.client;

import java.util.concurrent.atomic.AtomicLong;

public final class FutureRequestExecutionMetrics {
   private final AtomicLong activeConnections = new AtomicLong();
   private final AtomicLong scheduledConnections = new AtomicLong();
   private final FutureRequestExecutionMetrics.DurationCounter successfulConnections = new FutureRequestExecutionMetrics.DurationCounter();
   private final FutureRequestExecutionMetrics.DurationCounter failedConnections = new FutureRequestExecutionMetrics.DurationCounter();
   private final FutureRequestExecutionMetrics.DurationCounter requests = new FutureRequestExecutionMetrics.DurationCounter();
   private final FutureRequestExecutionMetrics.DurationCounter tasks = new FutureRequestExecutionMetrics.DurationCounter();

   AtomicLong getActiveConnections() {
      return this.activeConnections;
   }

   AtomicLong getScheduledConnections() {
      return this.scheduledConnections;
   }

   FutureRequestExecutionMetrics.DurationCounter getSuccessfulConnections() {
      return this.successfulConnections;
   }

   FutureRequestExecutionMetrics.DurationCounter getFailedConnections() {
      return this.failedConnections;
   }

   FutureRequestExecutionMetrics.DurationCounter getRequests() {
      return this.requests;
   }

   FutureRequestExecutionMetrics.DurationCounter getTasks() {
      return this.tasks;
   }

   public long getActiveConnectionCount() {
      return this.activeConnections.get();
   }

   public long getScheduledConnectionCount() {
      return this.scheduledConnections.get();
   }

   public long getSuccessfulConnectionCount() {
      return this.successfulConnections.count();
   }

   public long getSuccessfulConnectionAverageDuration() {
      return this.successfulConnections.averageDuration();
   }

   public long getFailedConnectionCount() {
      return this.failedConnections.count();
   }

   public long getFailedConnectionAverageDuration() {
      return this.failedConnections.averageDuration();
   }

   public long getRequestCount() {
      return this.requests.count();
   }

   public long getRequestAverageDuration() {
      return this.requests.averageDuration();
   }

   public long getTaskCount() {
      return this.tasks.count();
   }

   public long getTaskAverageDuration() {
      return this.tasks.averageDuration();
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("[activeConnections=").append(this.activeConnections).append(", scheduledConnections=").append(this.scheduledConnections).append(", successfulConnections=").append(this.successfulConnections).append(", failedConnections=").append(this.failedConnections).append(", requests=").append(this.requests).append(", tasks=").append(this.tasks).append("]");
      return builder.toString();
   }

   static class DurationCounter {
      private final AtomicLong count = new AtomicLong(0L);
      private final AtomicLong cumulativeDuration = new AtomicLong(0L);

      public void increment(long startTime) {
         this.count.incrementAndGet();
         this.cumulativeDuration.addAndGet(System.currentTimeMillis() - startTime);
      }

      public long count() {
         return this.count.get();
      }

      public long averageDuration() {
         long counter = this.count.get();
         return counter > 0L?this.cumulativeDuration.get() / counter:0L;
      }

      public String toString() {
         StringBuilder builder = new StringBuilder();
         builder.append("[count=").append(this.count()).append(", averageDuration=").append(this.averageDuration()).append("]");
         return builder.toString();
      }
   }
}
