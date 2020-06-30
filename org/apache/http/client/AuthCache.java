package org.apache.http.client;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;

public interface AuthCache {
   void put(HttpHost var1, AuthScheme var2);

   AuthScheme get(HttpHost var1);

   void remove(HttpHost var1);

   void clear();
}
