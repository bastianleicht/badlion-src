package com.mojang.authlib.yggdrasil;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.HttpMinecraftSessionService;
import com.mojang.authlib.minecraft.InsecureTextureException;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.request.JoinMinecraftServerRequest;
import com.mojang.authlib.yggdrasil.response.HasJoinedMinecraftServerResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftProfilePropertiesResponse;
import com.mojang.authlib.yggdrasil.response.MinecraftTexturesPayload;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YggdrasilMinecraftSessionService extends HttpMinecraftSessionService {
   private static final String[] WHITELISTED_DOMAINS = new String[]{".minecraft.net", ".mojang.com"};
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String BASE_URL = "https://sessionserver.mojang.com/session/minecraft/";
   private static final URL JOIN_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/join");
   private static final URL CHECK_URL = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/hasJoined");
   private final PublicKey publicKey;
   private final Gson gson = (new GsonBuilder()).registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();
   private final LoadingCache insecureProfiles = CacheBuilder.newBuilder().expireAfterWrite(6L, TimeUnit.HOURS).build(new CacheLoader() {
      public GameProfile load(GameProfile key) throws Exception {
         return YggdrasilMinecraftSessionService.this.fillGameProfile(key, false);
      }
   });

   protected YggdrasilMinecraftSessionService(YggdrasilAuthenticationService authenticationService) {
      super(authenticationService);

      try {
         X509EncodedKeySpec spec = new X509EncodedKeySpec(IOUtils.toByteArray(YggdrasilMinecraftSessionService.class.getResourceAsStream("/yggdrasil_session_pubkey.der")));
         KeyFactory keyFactory = KeyFactory.getInstance("RSA");
         this.publicKey = keyFactory.generatePublic(spec);
      } catch (Exception var4) {
         throw new Error("Missing/invalid yggdrasil public key!");
      }
   }

   public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
      JoinMinecraftServerRequest request = new JoinMinecraftServerRequest();
      request.accessToken = authenticationToken;
      request.selectedProfile = profile.getId();
      request.serverId = serverId;
      this.getAuthenticationService().makeRequest(JOIN_URL, request, Response.class);
   }

   public GameProfile hasJoinedServer(GameProfile user, String serverId) throws AuthenticationUnavailableException {
      Map<String, Object> arguments = new HashMap();
      arguments.put("username", user.getName());
      arguments.put("serverId", serverId);
      URL url = HttpAuthenticationService.concatenateURL(CHECK_URL, HttpAuthenticationService.buildQuery(arguments));

      try {
         HasJoinedMinecraftServerResponse response = (HasJoinedMinecraftServerResponse)this.getAuthenticationService().makeRequest(url, (Object)null, HasJoinedMinecraftServerResponse.class);
         if(response != null && response.getId() != null) {
            GameProfile result = new GameProfile(response.getId(), user.getName());
            if(response.getProperties() != null) {
               result.getProperties().putAll(response.getProperties());
            }

            return result;
         } else {
            return null;
         }
      } catch (AuthenticationUnavailableException var7) {
         throw var7;
      } catch (AuthenticationException var8) {
         return null;
      }
   }

   public Map getTextures(GameProfile profile, boolean requireSecure) {
      Property textureProperty = (Property)Iterables.getFirst(profile.getProperties().get("textures"), (Object)null);
      if(textureProperty == null) {
         return new HashMap();
      } else {
         if(requireSecure) {
            if(!textureProperty.hasSignature()) {
               LOGGER.error("Signature is missing from textures payload");
               throw new InsecureTextureException("Signature is missing from textures payload");
            }

            if(!textureProperty.isSignatureValid(this.publicKey)) {
               LOGGER.error("Textures payload has been tampered with (signature invalid)");
               throw new InsecureTextureException("Textures payload has been tampered with (signature invalid)");
            }
         }

         MinecraftTexturesPayload result;
         try {
            String json = new String(Base64.decodeBase64(textureProperty.getValue()), Charsets.UTF_8);
            result = (MinecraftTexturesPayload)this.gson.fromJson(json, MinecraftTexturesPayload.class);
         } catch (JsonParseException var7) {
            LOGGER.error((String)"Could not decode textures payload", (Throwable)var7);
            return new HashMap();
         }

         if(result.getTextures() == null) {
            return new HashMap();
         } else {
            for(Entry<MinecraftProfileTexture.Type, MinecraftProfileTexture> entry : result.getTextures().entrySet()) {
               if(!isWhitelistedDomain(((MinecraftProfileTexture)entry.getValue()).getUrl())) {
                  LOGGER.error("Textures payload has been tampered with (non-whitelisted domain)");
                  return new HashMap();
               }
            }

            return result.getTextures();
         }
      }
   }

   public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
      return profile.getId() == null?profile:(!requireSecure?(GameProfile)this.insecureProfiles.getUnchecked(profile):this.fillGameProfile(profile, true));
   }

   protected GameProfile fillGameProfile(GameProfile profile, boolean requireSecure) {
      try {
         URL url = HttpAuthenticationService.constantURL("https://sessionserver.mojang.com/session/minecraft/profile/" + UUIDTypeAdapter.fromUUID(profile.getId()));
         url = HttpAuthenticationService.concatenateURL(url, "unsigned=" + !requireSecure);
         MinecraftProfilePropertiesResponse response = (MinecraftProfilePropertiesResponse)this.getAuthenticationService().makeRequest(url, (Object)null, MinecraftProfilePropertiesResponse.class);
         if(response == null) {
            LOGGER.debug("Couldn\'t fetch profile properties for " + profile + " as the profile does not exist");
            return profile;
         } else {
            GameProfile result = new GameProfile(response.getId(), response.getName());
            result.getProperties().putAll(response.getProperties());
            profile.getProperties().putAll(response.getProperties());
            LOGGER.debug("Successfully fetched profile properties for " + profile);
            return result;
         }
      } catch (AuthenticationException var6) {
         LOGGER.warn((String)("Couldn\'t look up profile properties for " + profile), (Throwable)var6);
         return profile;
      }
   }

   public YggdrasilAuthenticationService getAuthenticationService() {
      return (YggdrasilAuthenticationService)super.getAuthenticationService();
   }

   private static boolean isWhitelistedDomain(String url) {
      URI uri = null;

      try {
         uri = new URI(url);
      } catch (URISyntaxException var4) {
         throw new IllegalArgumentException("Invalid URL \'" + url + "\'");
      }

      String domain = uri.getHost();

      for(int i = 0; i < WHITELISTED_DOMAINS.length; ++i) {
         if(domain.endsWith(WHITELISTED_DOMAINS[i])) {
            return true;
         }
      }

      return false;
   }
}
