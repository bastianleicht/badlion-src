package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.dto.WorldTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldTemplateList extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public List templates;

   public static WorldTemplateList parse(String json) {
      WorldTemplateList list = new WorldTemplateList();
      list.templates = new ArrayList();

      try {
         JsonParser parser = new JsonParser();
         JsonObject object = parser.parse(json).getAsJsonObject();
         if(object.get("templates").isJsonArray()) {
            Iterator<JsonElement> it = object.get("templates").getAsJsonArray().iterator();

            while(it.hasNext()) {
               list.templates.add(WorldTemplate.parse(((JsonElement)it.next()).getAsJsonObject()));
            }
         }
      } catch (Exception var5) {
         LOGGER.error("Could not parse WorldTemplateList: " + var5.getMessage());
      }

      return list;
   }
}
