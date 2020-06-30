package org.apache.http.impl.client;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.AuthenticationStrategyImpl;

@Immutable
public class ProxyAuthenticationStrategy extends AuthenticationStrategyImpl {
   public static final ProxyAuthenticationStrategy INSTANCE = new ProxyAuthenticationStrategy();

   public ProxyAuthenticationStrategy() {
      super(407, "Proxy-Authenticate");
   }

   Collection getPreferredAuthSchemes(RequestConfig config) {
      return config.getProxyPreferredAuthSchemes();
   }
}
