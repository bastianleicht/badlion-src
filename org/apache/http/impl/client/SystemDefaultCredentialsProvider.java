package org.apache.http.impl.client;

import java.net.Authenticator;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.Authenticator.RequestorType;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.util.Args;

@ThreadSafe
public class SystemDefaultCredentialsProvider implements CredentialsProvider {
   private static final Map SCHEME_MAP = new ConcurrentHashMap();
   private final BasicCredentialsProvider internal = new BasicCredentialsProvider();

   private static String translateScheme(String key) {
      if(key == null) {
         return null;
      } else {
         String s = (String)SCHEME_MAP.get(key);
         return s != null?s:key;
      }
   }

   public void setCredentials(AuthScope authscope, Credentials credentials) {
      this.internal.setCredentials(authscope, credentials);
   }

   private static PasswordAuthentication getSystemCreds(AuthScope authscope, RequestorType requestorType) {
      String hostname = authscope.getHost();
      int port = authscope.getPort();
      String protocol = port == 443?"https":"http";
      return Authenticator.requestPasswordAuthentication(hostname, (InetAddress)null, port, protocol, (String)null, translateScheme(authscope.getScheme()), (URL)null, requestorType);
   }

   public Credentials getCredentials(AuthScope authscope) {
      Args.notNull(authscope, "Auth scope");
      Credentials localcreds = this.internal.getCredentials(authscope);
      if(localcreds != null) {
         return localcreds;
      } else {
         if(authscope.getHost() != null) {
            PasswordAuthentication systemcreds = getSystemCreds(authscope, RequestorType.SERVER);
            if(systemcreds == null) {
               systemcreds = getSystemCreds(authscope, RequestorType.PROXY);
            }

            if(systemcreds != null) {
               String domain = System.getProperty("http.auth.ntlm.domain");
               if(domain != null) {
                  return new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), (String)null, domain);
               }

               if("NTLM".equalsIgnoreCase(authscope.getScheme())) {
                  return new NTCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()), (String)null, (String)null);
               }

               return new UsernamePasswordCredentials(systemcreds.getUserName(), new String(systemcreds.getPassword()));
            }
         }

         return null;
      }
   }

   public void clear() {
      this.internal.clear();
   }

   static {
      SCHEME_MAP.put("Basic".toUpperCase(Locale.ENGLISH), "Basic");
      SCHEME_MAP.put("Digest".toUpperCase(Locale.ENGLISH), "Digest");
      SCHEME_MAP.put("NTLM".toUpperCase(Locale.ENGLISH), "NTLM");
      SCHEME_MAP.put("negotiate".toUpperCase(Locale.ENGLISH), "SPNEGO");
      SCHEME_MAP.put("Kerberos".toUpperCase(Locale.ENGLISH), "Kerberos");
   }
}
