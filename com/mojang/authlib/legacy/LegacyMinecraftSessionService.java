package com.mojang.authlib.legacy;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.legacy.LegacyAuthenticationService;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LegacyMinecraftSessionService extends HttpMinecraftSessionService {
   private static final String BASE_URL = "http://session.minecraft.net/game/";
   private static final URL JOIN_URL = HttpAuthenticationService.constantURL("http://session.minecraft.net/game/joinserver.jsp");
   private static final URL CHECK_URL = HttpAuthenticationService.constantURL("http://session.minecraft.net/game/checkserver.jsp");

   protected LegacyMinecraftSessionService(LegacyAuthenticationService authenticationService) {
      super(authenticationService);
   }

   public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
      Map<String, Object> arguments = new HashMap();
      arguments.put("user", profile.getName());
      arguments.put("sessionId", authenticationToken);
      arguments.put("serverId", serverId);
      URL url = HttpAuthenticationService.concatenateURL(JOIN_URL, HttpAuthenticationService.buildQuery(arguments));

      try {
         String response = this.getAuthenticationService().performGetRequest(url);
         if(!response.equals("OK")) {
            throw new AuthenticationException(response);
         }
      } catch (IOException var7) {
         throw new AuthenticationUnavailableException(var7);
      }
   }

   public GameProfile hasJoinedServer(GameProfile user, String serverId) throws AuthenticationUnavailableException {
      Map<String, Object> arguments = new HashMap();
      arguments.put("user", user.getName());
      arguments.put("serverId", serverId);
      URL url = HttpAuthenticationService.concatenateURL(CHECK_URL, HttpAuthenticationService.buildQuery(arguments));

      try {
         String response = this.getAuthenticationService().performGetRequest(url);
         return response.equals("YES")?user:null;
      } catch (IOException var6) {
         throw new AuthenticationUnavailableException(var6);
      }
   }

   public Map getTextures(GameProfile profile, boolean requireSecure) {
      return new HashMap();
   }

   public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
      return profile;
   }

   public LegacyAuthenticationService getAuthenticationService() {
      return (LegacyAuthenticationService)super.getAuthenticationService();
   }
}
