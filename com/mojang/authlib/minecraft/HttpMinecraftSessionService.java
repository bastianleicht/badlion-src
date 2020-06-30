package com.mojang.authlib.minecraft;

import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.minecraft.BaseMinecraftSessionService;

public abstract class HttpMinecraftSessionService extends BaseMinecraftSessionService {
   protected HttpMinecraftSessionService(HttpAuthenticationService authenticationService) {
      super(authenticationService);
   }

   public HttpAuthenticationService getAuthenticationService() {
      return (HttpAuthenticationService)super.getAuthenticationService();
   }
}
