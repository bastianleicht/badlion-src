package org.apache.http.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.config.Lookup;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@ThreadSafe
public final class AuthSchemeRegistry implements Lookup {
   private final ConcurrentHashMap registeredSchemes = new ConcurrentHashMap();

   public void register(String name, AuthSchemeFactory factory) {
      Args.notNull(name, "Name");
      Args.notNull(factory, "Authentication scheme factory");
      this.registeredSchemes.put(name.toLowerCase(Locale.ENGLISH), factory);
   }

   public void unregister(String name) {
      Args.notNull(name, "Name");
      this.registeredSchemes.remove(name.toLowerCase(Locale.ENGLISH));
   }

   public AuthScheme getAuthScheme(String name, HttpParams params) throws IllegalStateException {
      Args.notNull(name, "Name");
      AuthSchemeFactory factory = (AuthSchemeFactory)this.registeredSchemes.get(name.toLowerCase(Locale.ENGLISH));
      if(factory != null) {
         return factory.newInstance(params);
      } else {
         throw new IllegalStateException("Unsupported authentication scheme: " + name);
      }
   }

   public List getSchemeNames() {
      return new ArrayList(this.registeredSchemes.keySet());
   }

   public void setItems(Map map) {
      if(map != null) {
         this.registeredSchemes.clear();
         this.registeredSchemes.putAll(map);
      }
   }

   public AuthSchemeProvider lookup(final String name) {
      return new AuthSchemeProvider() {
         public AuthScheme create(HttpContext context) {
            HttpRequest request = (HttpRequest)context.getAttribute("http.request");
            return AuthSchemeRegistry.this.getAuthScheme(name, request.getParams());
         }
      };
   }
}
