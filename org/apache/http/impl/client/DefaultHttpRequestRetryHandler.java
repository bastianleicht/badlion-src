package org.apache.http.impl.client;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import javax.net.ssl.SSLException;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@Immutable
public class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {
   public static final DefaultHttpRequestRetryHandler INSTANCE = new DefaultHttpRequestRetryHandler();
   private final int retryCount;
   private final boolean requestSentRetryEnabled;
   private final Set nonRetriableClasses;

   protected DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled, Collection clazzes) {
      this.retryCount = retryCount;
      this.requestSentRetryEnabled = requestSentRetryEnabled;
      this.nonRetriableClasses = new HashSet();

      for(Class<? extends IOException> clazz : clazzes) {
         this.nonRetriableClasses.add(clazz);
      }

   }

   public DefaultHttpRequestRetryHandler(int retryCount, boolean requestSentRetryEnabled) {
      this(retryCount, requestSentRetryEnabled, Arrays.asList(new Class[]{InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class}));
   }

   public DefaultHttpRequestRetryHandler() {
      this(3, false);
   }

   public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
      Args.notNull(exception, "Exception parameter");
      Args.notNull(context, "HTTP context");
      if(executionCount > this.retryCount) {
         return false;
      } else if(this.nonRetriableClasses.contains(exception.getClass())) {
         return false;
      } else {
         for(Class<? extends IOException> rejectException : this.nonRetriableClasses) {
            if(rejectException.isInstance(exception)) {
               return false;
            }
         }

         HttpClientContext clientContext = HttpClientContext.adapt(context);
         HttpRequest request = clientContext.getRequest();
         if(this.requestIsAborted(request)) {
            return false;
         } else if(this.handleAsIdempotent(request)) {
            return true;
         } else if(clientContext.isRequestSent() && !this.requestSentRetryEnabled) {
            return false;
         } else {
            return true;
         }
      }
   }

   public boolean isRequestSentRetryEnabled() {
      return this.requestSentRetryEnabled;
   }

   public int getRetryCount() {
      return this.retryCount;
   }

   protected boolean handleAsIdempotent(HttpRequest request) {
      return !(request instanceof HttpEntityEnclosingRequest);
   }

   /** @deprecated */
   @Deprecated
   protected boolean requestIsAborted(HttpRequest request) {
      HttpRequest req = request;
      if(request instanceof RequestWrapper) {
         req = ((RequestWrapper)request).getOriginal();
      }

      return req instanceof HttpUriRequest && ((HttpUriRequest)req).isAborted();
   }
}
