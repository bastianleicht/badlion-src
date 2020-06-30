package com.mojang.realmsclient.dto;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.ValueObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsServerList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List servers;

   public static RealmsServerList parse(String json) {
      RealmsServerList list = new RealmsServerList();
      list.servers = new ArrayList();

      try {
         JsonParser parser = new JsonParser();
         JsonObject object = parser.parse(json).getAsJsonObject();
         if(object.get("servers").isJsonArray()) {
            JsonArray jsonArray = object.get("servers").getAsJsonArray();
            Iterator<JsonElement> it = jsonArray.iterator();

            while(it.hasNext()) {
               list.servers.add(RealmsServer.parse(((JsonElement)it.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var6) {
         LOGGER.error("Could not parse McoServerList: " + var6.getMessage());
      }

      return list;
   }
}
