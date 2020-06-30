package org.apache.http.client;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.protocol.HttpContext;

/** @deprecated */
@Deprecated
public interface RedirectHandler {
   boolean isRedirectRequested(HttpResponse var1, HttpContext var2);

   URI getLocationURI(HttpResponse var1, HttpContext var2) throws ProtocolException;
}
