package org.apache.http.client;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

public interface RedirectStrategy {
   boolean isRedirected(HttpRequest var1, HttpResponse var2, HttpContext var3) throws ProtocolException;

   HttpUriRequest getRedirect(HttpRequest var1, HttpResponse var2, HttpContext var3) throws ProtocolException;
}
