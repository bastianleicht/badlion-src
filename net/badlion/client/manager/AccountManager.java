package net.badlion.client.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import net.badlion.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;

public class AccountManager {
   public static final ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
   private Map sessionMap = new HashMap();
   private List sortedUsernames = new ArrayList();
   private Map gameProfileCache = new HashMap();
   private Map cachedSkinResources = new HashMap();
   private static final ExecutorService asyncTaskThreadPool = new ThreadPoolExecutor(0, 2, 1L, TimeUnit.MINUTES, new LinkedBlockingQueue());

   public void addSession(Session session) {
      LogManager.getLogger().info("Adding session: " + session.getUsername());
      this.sessionMap.put(session.getUsername(), session);
      this.sortedUsernames.add(session.getUsername());
      final UUID uuid = session.getPlayerUUID();
      if(!this.gameProfileCache.containsKey(uuid)) {
         LogManager.getLogger().info("Skin Lookup: " + uuid.toString());
         GameProfile gameprofile = new GameProfile(uuid, session.getUsername());
         this.gameProfileCache.put(uuid, gameprofile);
         Minecraft.getMinecraft().getSkinManager().loadProfileTextures(gameprofile, new SkinManager.SkinAvailableCallback() {
            public void skinAvailable(MinecraftProfileTexture.Type p_180521_1_, ResourceLocation location, MinecraftProfileTexture profileTexture) {
               if(p_180521_1_.equals(MinecraftProfileTexture.Type.SKIN)) {
                  LogManager.getLogger().info("Skin Lookup Finished: " + uuid.toString());
                  AccountManager.this.cachedSkinResources.put(uuid, location);
               }

            }
         }, false, true);
      }

   }

   public Session getSession(String username) {
      return this.sessionMap.containsKey(username)?(Session)this.sessionMap.get(username):null;
   }

   public void reloadSessions() {
      String s = Wrapper.getInstance().getAvailableProfiles(Minecraft.getMinecraft().mcDataDir.getAbsolutePath());
      LogManager.getLogger().info("Reload Session Response: " + s);
      AccountManager.Profiles accountmanager$profiles = (AccountManager.Profiles)Wrapper.getInstance().getGson().fromJson(s, AccountManager.Profiles.class);
      LogManager.getLogger().info("Updating sessions mappings...");
      this.sessionMap.clear();
      this.sortedUsernames.clear();

      for(AccountManager.Profile accountmanager$profile : accountmanager$profiles.getProfiles()) {
         this.addSession(accountmanager$profile.toSession());
      }

      Collections.sort(this.sortedUsernames, new Comparator() {
         public int compare(String s1, String s2) {
            return s1.compareToIgnoreCase(s2);
         }
      });
      LogManager.getLogger().info("Reload sessions done!");
   }

   public boolean loginProfile(String username, String password) {
      AccountManager.Profile accountmanager$profile = (AccountManager.Profile)Wrapper.getInstance().getGson().fromJson(Wrapper.getInstance().loginProfile(Minecraft.getMinecraft().mcDataDir.getAbsolutePath(), username, password), AccountManager.Profile.class);
      if(accountmanager$profile.error != null && !accountmanager$profile.error.isEmpty()) {
         LogManager.getLogger().info("Error logging into account... ERROR: " + accountmanager$profile.error + " CODE: " + accountmanager$profile.code);
         return false;
      } else {
         this.addSession(accountmanager$profile.toSession());
         return true;
      }
   }

   public boolean switchToAccount(String username) {
      if(Minecraft.getMinecraft().getSession().getUsername().equals(username)) {
         return false;
      } else {
         LogManager.getLogger().info("Switching to account: " + username);
         if(this.sessionMap.containsKey(username)) {
            Session session = (Session)this.sessionMap.get(username);
            Minecraft.getMinecraft().setSession((Session)this.sessionMap.get(username));
            LogManager.getLogger().info("Switched to account: " + username);
            return true;
         } else {
            return false;
         }
      }
   }

   public Map getSessionMap() {
      return this.sessionMap;
   }

   public List getSortedUsernames() {
      return this.sortedUsernames;
   }

   public Map getCachedSkinResources() {
      return this.cachedSkinResources;
   }

   private class Profile {
      private String error;
      private int code;
      private String userId;
      private String uuid;
      private String displayName;
      private String email;
      private String accessToken;

      public Session toSession() {
         return new Session(this.displayName, this.uuid, this.accessToken, this.isMojangAccount()?"MOJANG":"LEGACY");
      }

      public boolean isMojangAccount() {
         return this.email != null && this.email.contains("@");
      }

      public String getDisplayName() {
         return this.displayName;
      }

      public void setAccessToken(String accessToken) {
         this.accessToken = accessToken;
      }

      public UUID getUuid() {
         return UUID.fromString(this.uuid.replaceFirst("([0-9a-fA-F]{8})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]{4})([0-9a-fA-F]+)", "$1-$2-$3-$4-$5"));
      }
   }

   private class Profiles {
      private List profiles;
      private String clientToken;

      public List getProfiles() {
         return this.profiles;
      }
   }
}
