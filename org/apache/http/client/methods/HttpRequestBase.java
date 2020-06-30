package org.apache.http.client.methods;

import java.net.URI;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.AbstractExecutionAwareRequest;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.params.HttpProtocolParams;

@NotThreadSafe
public abstract class HttpRequestBase extends AbstractExecutionAwareRequest implements HttpUriRequest, Configurable {
   private ProtocolVersion version;
   private URI uri;
   private RequestConfig config;

   public abstract String getMethod();

   public void setProtocolVersion(ProtocolVersion version) {
      this.version = version;
   }

   public ProtocolVersion getProtocolVersion() {
      return this.version != null?this.version:HttpProtocolParams.getVersion(this.getParams());
   }

   public URI getURI() {
      return this.uri;
   }

   public RequestLine getRequestLine() {
      String method = this.getMethod();
      ProtocolVersion ver = this.getProtocolVersion();
      URI uri = this.getURI();
      String uritext = null;
      if(uri != null) {
         uritext = uri.toASCIIString();
      }

      if(uritext == null || uritext.length() == 0) {
         uritext = "/";
      }

      return new BasicRequestLine(method, uritext, ver);
   }

   public RequestConfig getConfig() {
      return this.config;
   }

   public void setConfig(RequestConfig config) {
      this.config = config;
   }

   public void setURI(URI uri) {
      this.uri = uri;
   }

   public void started() {
   }

   public void releaseConnection() {
      this.reset();
   }

   public String toString() {
      return this.getMethod() + " " + this.getURI() + " " + this.getProtocolVersion();
   }
}
