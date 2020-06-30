package org.apache.http.protocol;

import java.util.List;
import org.apache.http.HttpResponseInterceptor;

/** @deprecated */
@Deprecated
public interface HttpResponseInterceptorList {
   void addResponseInterceptor(HttpResponseInterceptor var1);

   void addResponseInterceptor(HttpResponseInterceptor var1, int var2);

   int getResponseInterceptorCount();

   HttpResponseInterceptor getResponseInterceptor(int var1);

   void clearResponseInterceptors();

   void removeResponseInterceptorByClass(Class var1);

   void setInterceptors(List var1);
}
