package net.minecraft.util;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Map;
import java.util.UUID;

public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;

   public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
      this.username = usernameIn;
      this.playerID = playerIDIn;
      this.token = tokenIn;
      this.sessionType = Session.Type.setSessionType(sessionTypeIn);
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public UUID getPlayerUUID() {
      return UUID.fromString(this.playerID.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
   }

   public String getUsername() {
      return this.username;
   }

   public String getToken() {
      return this.token;
   }

   public GameProfile getProfile() {
      try {
         UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
         return new GameProfile(uuid, this.getUsername());
      } catch (IllegalArgumentException var2) {
         return new GameProfile((UUID)null, this.getUsername());
      }
   }

   public Session.Type getSessionType() {
      return this.sessionType;
   }

   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map SESSION_TYPES = Maps.newHashMap();
      private final String sessionType;

      static {
         Session.Type[] var3;
         for(Session.Type session$type : var3 = values()) {
            SESSION_TYPES.put(session$type.sessionType, session$type);
         }

      }

      private Type(String sessionTypeIn) {
         this.sessionType = sessionTypeIn;
      }

      public static Session.Type setSessionType(String sessionTypeIn) {
         return (Session.Type)SESSION_TYPES.get(sessionTypeIn.toLowerCase());
      }
   }
}
