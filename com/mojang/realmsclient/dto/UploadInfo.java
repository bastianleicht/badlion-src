package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UploadInfo {
   private static final Logger LOGGER = LogManager.getLogger();
   private boolean worldClosed;
   private String token = "";
   private String uploadEndpoint = "";

   public static UploadInfo parse(String json) {
      UploadInfo uploadInfo = new UploadInfo();

      try {
         JsonParser parser = new JsonParser();
         JsonObject jsonObject = parser.parse(json).getAsJsonObject();
         uploadInfo.worldClosed = JsonUtils.getBooleanOr("worldClosed", jsonObject, false);
         uploadInfo.token = JsonUtils.getStringOr("token", jsonObject, (String)null);
         uploadInfo.uploadEndpoint = JsonUtils.getStringOr("uploadEndpoint", jsonObject, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse UploadInfo: " + var4.getMessage());
      }

      return uploadInfo;
   }

   public String getToken() {
      return this.token;
   }

   public String getUploadEndpoint() {
      return this.uploadEndpoint;
   }

   public boolean isWorldClosed() {
      return this.worldClosed;
   }

   public void setToken(String token) {
      this.token = token;
   }
}
