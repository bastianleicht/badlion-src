package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpRequestInterceptor;

/** @deprecated */
@Deprecated
public interface HttpRequestInterceptorList {
   void addRequestInterceptor(HttpRequestInterceptor var1);

   void addRequestInterceptor(HttpRequestInterceptor var1, int var2);

   int getRequestInterceptorCount();

   HttpRequestInterceptor getRequestInterceptor(int var1);

   void clearRequestInterceptors();

   void removeRequestInterceptorByClass(Class var1);

   void setInterceptors(List var1);
}
