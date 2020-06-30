package org.apache.http.impl;

import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.RequestLine;
import org.apache.http.annotation.Immutable;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.util.Args;

@Immutable
public class DefaultHttpRequestFactory implements HttpRequestFactory {
   public static final DefaultHttpRequestFactory INSTANCE = new DefaultHttpRequestFactory();
   private static final String[] RFC2616_COMMON_METHODS = new String[]{"GET"};
   private static final String[] RFC2616_ENTITY_ENC_METHODS = new String[]{"POST", "PUT"};
   private static final String[] RFC2616_SPECIAL_METHODS = new String[]{"HEAD", "OPTIONS", "DELETE", "TRACE", "CONNECT"};

   private static boolean isOneOf(String[] methods, String method) {
      for(String method2 : methods) {
         if(method2.equalsIgnoreCase(method)) {
            return true;
         }
      }

      return false;
   }

   public HttpRequest newHttpRequest(RequestLine requestline) throws MethodNotSupportedException {
      Args.notNull(requestline, "Request line");
      String method = requestline.getMethod();
      if(isOneOf(RFC2616_COMMON_METHODS, method)) {
         return new BasicHttpRequest(requestline);
      } else if(isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
         return new BasicHttpEntityEnclosingRequest(requestline);
      } else if(isOneOf(RFC2616_SPECIAL_METHODS, method)) {
         return new BasicHttpRequest(requestline);
      } else {
         throw new MethodNotSupportedException(method + " method not supported");
      }
   }

   public HttpRequest newHttpRequest(String method, String uri) throws MethodNotSupportedException {
      if(isOneOf(RFC2616_COMMON_METHODS, method)) {
         return new BasicHttpRequest(method, uri);
      } else if(isOneOf(RFC2616_ENTITY_ENC_METHODS, method)) {
         return new BasicHttpEntityEnclosingRequest(method, uri);
      } else if(isOneOf(RFC2616_SPECIAL_METHODS, method)) {
         return new BasicHttpRequest(method, uri);
      } else {
         throw new MethodNotSupportedException(method + " method not supported");
      }
   }
}
