package org.apache.http.client.methods;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.CloneUtils;

@NotThreadSafe
public abstract class HttpEntityEnclosingRequestBase extends HttpRequestBase implements HttpEntityEnclosingRequest {
   private HttpEntity entity;

   public HttpEntity getEntity() {
      return this.entity;
   }

   public void setEntity(HttpEntity entity) {
      this.entity = entity;
   }

   public boolean expectContinue() {
      Header expect = this.getFirstHeader("Expect");
      return expect != null && "100-continue".equalsIgnoreCase(expect.getValue());
   }

   public Object clone() throws CloneNotSupportedException {
      HttpEntityEnclosingRequestBase clone = (HttpEntityEnclosingRequestBase)super.clone();
      if(this.entity != null) {
         clone.entity = (HttpEntity)CloneUtils.cloneObject(this.entity);
      }

      return clone;
   }
}
