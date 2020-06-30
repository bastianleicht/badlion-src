package org.apache.http.client;

import java.io.IOException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

public interface HttpClient {
   /** @deprecated */
   @Deprecated
   HttpParams getParams();

   /** @deprecated */
   @Deprecated
   ClientConnectionManager getConnectionManager();

   HttpResponse execute(HttpUriRequest var1) throws IOException, ClientProtocolException;

   HttpResponse execute(HttpUriRequest var1, HttpContext var2) throws IOException, ClientProtocolException;

   HttpResponse execute(HttpHost var1, HttpRequest var2) throws IOException, ClientProtocolException;

   HttpResponse execute(HttpHost var1, HttpRequest var2, HttpContext var3) throws IOException, ClientProtocolException;

   Object execute(HttpUriRequest var1, ResponseHandler var2) throws IOException, ClientProtocolException;

   Object execute(HttpUriRequest var1, ResponseHandler var2, HttpContext var3) throws IOException, ClientProtocolException;

   Object execute(HttpHost var1, HttpRequest var2, ResponseHandler var3) throws IOException, ClientProtocolException;

   Object execute(HttpHost var1, HttpRequest var2, ResponseHandler var3, HttpContext var4) throws IOException, ClientProtocolException;
}
