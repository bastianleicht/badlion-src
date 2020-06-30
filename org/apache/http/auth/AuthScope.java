package org.apache.http.auth;

import java.util.Locale;
import org.apache.http.HttpHost;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Immutable
public class AuthScope {
   public static final String ANY_HOST = null;
   public static final int ANY_PORT = -1;
   public static final String ANY_REALM = null;
   public static final String ANY_SCHEME = null;
   public static final AuthScope ANY = new AuthScope(ANY_HOST, -1, ANY_REALM, ANY_SCHEME);
   private final String scheme;
   private final String realm;
   private final String host;
   private final int port;

   public AuthScope(String host, int port, String realm, String scheme) {
      this.host = host == null?ANY_HOST:host.toLowerCase(Locale.ENGLISH);
      this.port = port < 0?-1:port;
      this.realm = realm == null?ANY_REALM:realm;
      this.scheme = scheme == null?ANY_SCHEME:scheme.toUpperCase(Locale.ENGLISH);
   }

   public AuthScope(HttpHost host, String realm, String schemeName) {
      this(host.getHostName(), host.getPort(), realm, schemeName);
   }

   public AuthScope(HttpHost host) {
      this(host, ANY_REALM, ANY_SCHEME);
   }

   public AuthScope(String host, int port, String realm) {
      this(host, port, realm, ANY_SCHEME);
   }

   public AuthScope(String host, int port) {
      this(host, port, ANY_REALM, ANY_SCHEME);
   }

   public AuthScope(AuthScope authscope) {
      Args.notNull(authscope, "Scope");
      this.host = authscope.getHost();
      this.port = authscope.getPort();
      this.realm = authscope.getRealm();
      this.scheme = authscope.getScheme();
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public String getRealm() {
      return this.realm;
   }

   public String getScheme() {
      return this.scheme;
   }

   public int match(AuthScope that) {
      int factor = 0;
      if(LangUtils.equals((Object)this.scheme, (Object)that.scheme)) {
         ++factor;
      } else if(this.scheme != ANY_SCHEME && that.scheme != ANY_SCHEME) {
         return -1;
      }

      if(LangUtils.equals((Object)this.realm, (Object)that.realm)) {
         factor += 2;
      } else if(this.realm != ANY_REALM && that.realm != ANY_REALM) {
         return -1;
      }

      if(this.port == that.port) {
         factor += 4;
      } else if(this.port != -1 && that.port != -1) {
         return -1;
      }

      if(LangUtils.equals((Object)this.host, (Object)that.host)) {
         factor += 8;
      } else if(this.host != ANY_HOST && that.host != ANY_HOST) {
         return -1;
      }

      return factor;
   }

   public boolean equals(Object o) {
      if(o == null) {
         return false;
      } else if(o == this) {
         return true;
      } else if(!(o instanceof AuthScope)) {
         return super.equals(o);
      } else {
         AuthScope that = (AuthScope)o;
         return LangUtils.equals((Object)this.host, (Object)that.host) && this.port == that.port && LangUtils.equals((Object)this.realm, (Object)that.realm) && LangUtils.equals((Object)this.scheme, (Object)that.scheme);
      }
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      if(this.scheme != null) {
         buffer.append(this.scheme.toUpperCase(Locale.ENGLISH));
         buffer.append(' ');
      }

      if(this.realm != null) {
         buffer.append('\'');
         buffer.append(this.realm);
         buffer.append('\'');
      } else {
         buffer.append("<any realm>");
      }

      if(this.host != null) {
         buffer.append('@');
         buffer.append(this.host);
         if(this.port >= 0) {
            buffer.append(':');
            buffer.append(this.port);
         }
      }

      return buffer.toString();
   }

   public int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.host);
      hash = LangUtils.hashCode(hash, this.port);
      hash = LangUtils.hashCode(hash, this.realm);
      hash = LangUtils.hashCode(hash, this.scheme);
      return hash;
   }
}
