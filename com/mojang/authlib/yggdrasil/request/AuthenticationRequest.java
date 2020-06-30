package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class AuthenticationRequest {
   private Agent agent;
   private String username;
   private String password;
   private String clientToken;
   private boolean requestUser = true;

   public AuthenticationRequest(YggdrasilUserAuthentication authenticationService, String username, String password) {
      this.agent = authenticationService.getAgent();
      this.username = username;
      this.clientToken = authenticationService.getAuthenticationService().getClientToken();
      this.password = password;
   }
}
