package org.apache.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;

public interface HttpEntityEnclosingRequest extends HttpRequest {
   boolean expectContinue();

   void setEntity(HttpEntity var1);

   HttpEntity getEntity();
}
