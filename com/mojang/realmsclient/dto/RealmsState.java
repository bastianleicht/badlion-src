package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsState {
   private static final Logger LOGGER = LogManager.getLogger();
   private String statusMessage;
   private String buyLink;

   public static RealmsState parse(String json) {
      RealmsState realmsState = new RealmsState();

      try {
         JsonParser parser = new JsonParser();
         JsonObject jsonObject = parser.parse(json).getAsJsonObject();
         realmsState.statusMessage = JsonUtils.getStringOr("statusMessage", jsonObject, (String)null);
         realmsState.buyLink = JsonUtils.getStringOr("buyLink", jsonObject, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse RealmsState: " + var4.getMessage());
      }

      return realmsState;
   }

   public String getStatusMessage() {
      return this.statusMessage;
   }

   public String getBuyLink() {
      return this.buyLink;
   }
}
