package org.apache.http.protocol;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HttpContext;

public interface HttpExpectationVerifier {
   void verify(HttpRequest var1, HttpResponse var2, HttpContext var3) throws HttpException;
}
