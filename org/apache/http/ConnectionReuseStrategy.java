package org.apache.http;

import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface ConnectionReuseStrategy {
   boolean keepAlive(HttpResponse var1, HttpContext var2);
}
