package org.apache.http.impl.client;

import java.util.Collection;
import org.apache.http.annotation.Immutable;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.AuthenticationStrategyImpl;

@Immutable
public class TargetAuthenticationStrategy extends AuthenticationStrategyImpl {
   public static final TargetAuthenticationStrategy INSTANCE = new TargetAuthenticationStrategy();

   public TargetAuthenticationStrategy() {
      super(401, "WWW-Authenticate");
   }

   Collection getPreferredAuthSchemes(RequestConfig config) {
      return config.getTargetPreferredAuthSchemes();
   }
}
