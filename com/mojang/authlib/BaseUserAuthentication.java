package com.mojang.authlib;

import com.mojang.authlib.AuthenticationService;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.UserType;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.util.UUIDTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseUserAuthentication implements UserAuthentication {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final String STORAGE_KEY_PROFILE_NAME = "displayName";
   protected static final String STORAGE_KEY_PROFILE_ID = "uuid";
   protected static final String STORAGE_KEY_PROFILE_PROPERTIES = "profileProperties";
   protected static final String STORAGE_KEY_USER_NAME = "username";
   protected static final String STORAGE_KEY_USER_ID = "userid";
   protected static final String STORAGE_KEY_USER_PROPERTIES = "userProperties";
   private final AuthenticationService authenticationService;
   private final PropertyMap userProperties = new PropertyMap();
   private String userid;
   private String username;
   private String password;
   private GameProfile selectedProfile;
   private UserType userType;

   protected BaseUserAuthentication(AuthenticationService authenticationService) {
      Validate.notNull(authenticationService);
      this.authenticationService = authenticationService;
   }

   public boolean canLogIn() {
      return !this.canPlayOnline() && StringUtils.isNotBlank(this.getUsername()) && StringUtils.isNotBlank(this.getPassword());
   }

   public void logOut() {
      this.password = null;
      this.userid = null;
      this.setSelectedProfile((GameProfile)null);
      this.getModifiableUserProperties().clear();
      this.setUserType((UserType)null);
   }

   public boolean isLoggedIn() {
      return this.getSelectedProfile() != null;
   }

   public void setUsername(String username) {
      if(this.isLoggedIn() && this.canPlayOnline()) {
         throw new IllegalStateException("Cannot change username whilst logged in & online");
      } else {
         this.username = username;
      }
   }

   public void setPassword(String password) {
      if(this.isLoggedIn() && this.canPlayOnline() && StringUtils.isNotBlank(password)) {
         throw new IllegalStateException("Cannot set password whilst logged in & online");
      } else {
         this.password = password;
      }
   }

   protected String getUsername() {
      return this.username;
   }

   protected String getPassword() {
      return this.password;
   }

   public void loadFromStorage(Map credentials) {
      this.logOut();
      this.setUsername(String.valueOf(credentials.get("username")));
      if(credentials.containsKey("userid")) {
         this.userid = String.valueOf(credentials.get("userid"));
      } else {
         this.userid = this.username;
      }

      if(credentials.containsKey("userProperties")) {
         try {
            for(Map<String, String> propertyMap : (List)credentials.get("userProperties")) {
               String name = (String)propertyMap.get("name");
               String value = (String)propertyMap.get("value");
               String signature = (String)propertyMap.get("signature");
               if(signature == null) {
                  this.getModifiableUserProperties().put(name, new Property(name, value));
               } else {
                  this.getModifiableUserProperties().put(name, new Property(name, value, signature));
               }
            }
         } catch (Throwable var10) {
            LOGGER.warn("Couldn\'t deserialize user properties", var10);
         }
      }

      if(credentials.containsKey("displayName") && credentials.containsKey("uuid")) {
         GameProfile profile = new GameProfile(UUIDTypeAdapter.fromString(String.valueOf(credentials.get("uuid"))), String.valueOf(credentials.get("displayName")));
         if(credentials.containsKey("profileProperties")) {
            try {
               for(Map<String, String> propertyMap : (List)credentials.get("profileProperties")) {
                  String name = (String)propertyMap.get("name");
                  String value = (String)propertyMap.get("value");
                  String signature = (String)propertyMap.get("signature");
                  if(signature == null) {
                     profile.getProperties().put(name, new Property(name, value));
                  } else {
                     profile.getProperties().put(name, new Property(name, value, signature));
                  }
               }
            } catch (Throwable var9) {
               LOGGER.warn("Couldn\'t deserialize profile properties", var9);
            }
         }

         this.setSelectedProfile(profile);
      }

   }

   public Map saveForStorage() {
      Map<String, Object> result = new HashMap();
      if(this.getUsername() != null) {
         result.put("username", this.getUsername());
      }

      if(this.getUserID() != null) {
         result.put("userid", this.getUserID());
      } else if(this.getUsername() != null) {
         result.put("username", this.getUsername());
      }

      if(!this.getUserProperties().isEmpty()) {
         List<Map<String, String>> properties = new ArrayList();

         for(Property userProperty : this.getUserProperties().values()) {
            Map<String, String> property = new HashMap();
            property.put("name", userProperty.getName());
            property.put("value", userProperty.getValue());
            property.put("signature", userProperty.getSignature());
            properties.add(property);
         }

         result.put("userProperties", properties);
      }

      GameProfile selectedProfile = this.getSelectedProfile();
      if(selectedProfile != null) {
         result.put("displayName", selectedProfile.getName());
         result.put("uuid", selectedProfile.getId());
         List<Map<String, String>> properties = new ArrayList();

         for(Property profileProperty : selectedProfile.getProperties().values()) {
            Map<String, String> property = new HashMap();
            property.put("name", profileProperty.getName());
            property.put("value", profileProperty.getValue());
            property.put("signature", profileProperty.getSignature());
            properties.add(property);
         }

         if(!properties.isEmpty()) {
            result.put("profileProperties", properties);
         }
      }

      return result;
   }

   protected void setSelectedProfile(GameProfile selectedProfile) {
      this.selectedProfile = selectedProfile;
   }

   public GameProfile getSelectedProfile() {
      return this.selectedProfile;
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      result.append(this.getClass().getSimpleName());
      result.append("{");
      if(this.isLoggedIn()) {
         result.append("Logged in as ");
         result.append(this.getUsername());
         if(this.getSelectedProfile() != null) {
            result.append(" / ");
            result.append(this.getSelectedProfile());
            result.append(" - ");
            if(this.canPlayOnline()) {
               result.append("Online");
            } else {
               result.append("Offline");
            }
         }
      } else {
         result.append("Not logged in");
      }

      result.append("}");
      return result.toString();
   }

   public AuthenticationService getAuthenticationService() {
      return this.authenticationService;
   }

   public String getUserID() {
      return this.userid;
   }

   public PropertyMap getUserProperties() {
      if(this.isLoggedIn()) {
         PropertyMap result = new PropertyMap();
         result.putAll(this.getModifiableUserProperties());
         return result;
      } else {
         return new PropertyMap();
      }
   }

   protected PropertyMap getModifiableUserProperties() {
      return this.userProperties;
   }

   public UserType getUserType() {
      return this.isLoggedIn()?(this.userType == null?UserType.LEGACY:this.userType):null;
   }

   protected void setUserType(UserType userType) {
      this.userType = userType;
   }

   protected void setUserid(String userid) {
      this.userid = userid;
   }
}
