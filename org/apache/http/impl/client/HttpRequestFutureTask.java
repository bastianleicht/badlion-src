package org.apache.http.impl.client;

import java.util.concurrent.FutureTask;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpRequestTaskCallable;

public class HttpRequestFutureTask extends FutureTask {
   private final HttpUriRequest request;
   private final HttpRequestTaskCallable callable;

   public HttpRequestFutureTask(HttpUriRequest request, HttpRequestTaskCallable httpCallable) {
      super(httpCallable);
      this.request = request;
      this.callable = httpCallable;
   }

   public boolean cancel(boolean mayInterruptIfRunning) {
      this.callable.cancel();
      if(mayInterruptIfRunning) {
         this.request.abort();
      }

      return super.cancel(mayInterruptIfRunning);
   }

   public long scheduledTime() {
      return this.callable.getScheduled();
   }

   public long startedTime() {
      return this.callable.getStarted();
   }

   public long endedTime() {
      if(this.isDone()) {
         return this.callable.getEnded();
      } else {
         throw new IllegalStateException("Task is not done yet");
      }
   }

   public long requestDuration() {
      if(this.isDone()) {
         return this.endedTime() - this.startedTime();
      } else {
         throw new IllegalStateException("Task is not done yet");
      }
   }

   public long taskDuration() {
      if(this.isDone()) {
         return this.endedTime() - this.scheduledTime();
      } else {
         throw new IllegalStateException("Task is not done yet");
      }
   }

   public String toString() {
      return this.request.getRequestLine().getUri();
   }
}
