package com.mojang.authlib.yggdrasil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.exceptions.UserMigratedException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.authlib.yggdrasil.response.Response;
import com.mojang.util.UUIDTypeAdapter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.net.URL;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

public class YggdrasilAuthenticationService extends HttpAuthenticationService {
   private final String clientToken;
   private final Gson gson;

   public YggdrasilAuthenticationService(Proxy proxy, String clientToken) {
      super(proxy);
      this.clientToken = clientToken;
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(GameProfile.class, new YggdrasilAuthenticationService.GameProfileSerializer());
      builder.registerTypeAdapter(PropertyMap.class, new PropertyMap.Serializer());
      builder.registerTypeAdapter(UUID.class, new UUIDTypeAdapter());
      builder.registerTypeAdapter(ProfileSearchResultsResponse.class, new ProfileSearchResultsResponse.Serializer());
      this.gson = builder.create();
   }

   public UserAuthentication createUserAuthentication(Agent agent) {
      return new YggdrasilUserAuthentication(this, agent);
   }

   public MinecraftSessionService createMinecraftSessionService() {
      return new YggdrasilMinecraftSessionService(this);
   }

   public GameProfileRepository createProfileRepository() {
      return new YggdrasilGameProfileRepository(this);
   }

   protected Response makeRequest(URL url, Object input, Class classOfT) throws AuthenticationException {
      try {
         String jsonResult = input == null?this.performGetRequest(url):this.performPostRequest(url, this.gson.toJson(input), "application/json");
         T result = (Response)this.gson.fromJson(jsonResult, classOfT);
         if(result == null) {
            return null;
         } else if(StringUtils.isNotBlank(result.getError())) {
            if("UserMigratedException".equals(result.getCause())) {
               throw new UserMigratedException(result.getErrorMessage());
            } else if(result.getError().equals("ForbiddenOperationException")) {
               throw new InvalidCredentialsException(result.getErrorMessage());
            } else {
               throw new AuthenticationException(result.getErrorMessage());
            }
         } else {
            return result;
         }
      } catch (IOException var6) {
         throw new AuthenticationUnavailableException("Cannot contact authentication server", var6);
      } catch (IllegalStateException var7) {
         throw new AuthenticationUnavailableException("Cannot contact authentication server", var7);
      } catch (JsonParseException var8) {
         throw new AuthenticationUnavailableException("Cannot contact authentication server", var8);
      }
   }

   public String getClientToken() {
      return this.clientToken;
   }

   private static class GameProfileSerializer implements JsonSerializer, JsonDeserializer {
      private GameProfileSerializer() {
      }

      public GameProfile deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
         JsonObject object = (JsonObject)json;
         UUID id = object.has("id")?(UUID)context.deserialize(object.get("id"), UUID.class):null;
         String name = object.has("name")?object.getAsJsonPrimitive("name").getAsString():null;
         return new GameProfile(id, name);
      }

      public JsonElement serialize(GameProfile src, Type typeOfSrc, JsonSerializationContext context) {
         JsonObject result = new JsonObject();
         if(src.getId() != null) {
            result.add("id", context.serialize(src.getId()));
         }

         if(src.getName() != null) {
            result.addProperty("name", src.getName());
         }

         return result;
      }
   }
}
