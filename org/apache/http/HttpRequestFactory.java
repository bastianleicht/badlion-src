package org.apache.http;

import org.apache.http.HttpRequest;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;

public interface HttpRequestFactory {
   HttpRequest newHttpRequest(RequestLine var1) throws MethodNotSupportedException;

   HttpRequest newHttpRequest(String var1, String var2) throws MethodNotSupportedException;
}
