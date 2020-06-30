package org.apache.http.protocol;

import org.apache.http.HttpConnection;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

@NotThreadSafe
public class HttpCoreContext implements HttpContext {
   public static final String HTTP_CONNECTION = "http.connection";
   public static final String HTTP_REQUEST = "http.request";
   public static final String HTTP_RESPONSE = "http.response";
   public static final String HTTP_TARGET_HOST = "http.target_host";
   public static final String HTTP_REQ_SENT = "http.request_sent";
   private final HttpContext context;

   public static HttpCoreContext create() {
      return new HttpCoreContext(new BasicHttpContext());
   }

   public static HttpCoreContext adapt(HttpContext context) {
      Args.notNull(context, "HTTP context");
      return context instanceof HttpCoreContext?(HttpCoreContext)context:new HttpCoreContext(context);
   }

   public HttpCoreContext(HttpContext context) {
      this.context = context;
   }

   public HttpCoreContext() {
      this.context = new BasicHttpContext();
   }

   public Object getAttribute(String id) {
      return this.context.getAttribute(id);
   }

   public void setAttribute(String id, Object obj) {
      this.context.setAttribute(id, obj);
   }

   public Object removeAttribute(String id) {
      return this.context.removeAttribute(id);
   }

   public Object getAttribute(String attribname, Class clazz) {
      Args.notNull(clazz, "Attribute class");
      Object obj = this.getAttribute(attribname);
      return obj == null?null:clazz.cast(obj);
   }

   public HttpConnection getConnection(Class clazz) {
      return (HttpConnection)this.getAttribute("http.connection", clazz);
   }

   public HttpConnection getConnection() {
      return (HttpConnection)this.getAttribute("http.connection", HttpConnection.class);
   }

   public HttpRequest getRequest() {
      return (HttpRequest)this.getAttribute("http.request", HttpRequest.class);
   }

   public boolean isRequestSent() {
      Boolean b = (Boolean)this.getAttribute("http.request_sent", Boolean.class);
      return b != null && b.booleanValue();
   }

   public HttpResponse getResponse() {
      return (HttpResponse)this.getAttribute("http.response", HttpResponse.class);
   }

   public void setTargetHost(HttpHost host) {
      this.setAttribute("http.target_host", host);
   }

   public HttpHost getTargetHost() {
      return (HttpHost)this.getAttribute("http.target_host", HttpHost.class);
   }
}
