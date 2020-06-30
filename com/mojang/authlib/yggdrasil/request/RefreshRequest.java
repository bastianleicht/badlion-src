package com.mojang.authlib.yggdrasil.request;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;

public class RefreshRequest {
   private String clientToken;
   private String accessToken;
   private GameProfile selectedProfile;
   private boolean requestUser;

   public RefreshRequest(YggdrasilUserAuthentication authenticationService) {
      this(authenticationService, (GameProfile)null);
   }

   public RefreshRequest(YggdrasilUserAuthentication authenticationService, GameProfile profile) {
      this.requestUser = true;
      this.clientToken = authenticationService.getAuthenticationService().getClientToken();
      this.accessToken = authenticationService.getAuthenticatedToken();
      this.selectedProfile = profile;
   }
}
