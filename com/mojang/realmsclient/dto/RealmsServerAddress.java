package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerAddress extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String address;

   public static RealmsServerAddress parse(String json) {
      JsonParser parser = new JsonParser();
      RealmsServerAddress serverAddress = new RealmsServerAddress();

      try {
         JsonObject object = parser.parse(json).getAsJsonObject();
         serverAddress.address = JsonUtils.getStringOr("address", object, (String)null);
      } catch (Exception var4) {
         LOGGER.error("Could not parse McoServerAddress: " + var4.getMessage());
      }

      return serverAddress;
   }
}
