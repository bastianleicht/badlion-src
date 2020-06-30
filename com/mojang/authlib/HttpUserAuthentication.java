package com.mojang.authlib;

import com.mojang.authlib.BaseUserAuthentication;
import com.mojang.authlib.HttpAuthenticationService;

public abstract class HttpUserAuthentication extends BaseUserAuthentication {
   protected HttpUserAuthentication(HttpAuthenticationService authenticationService) {
      super(authenticationService);
   }

   public HttpAuthenticationService getAuthenticationService() {
      return (HttpAuthenticationService)super.getAuthenticationService();
   }
}
