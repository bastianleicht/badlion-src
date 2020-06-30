package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.HashSet;
import java.util.Set;

public class Ops {
   public Set ops = new HashSet();

   public static Ops parse(String json) {
      Ops ops = new Ops();
      JsonParser parser = new JsonParser();

      try {
         JsonElement jsonElement = parser.parse(json);
         JsonObject jsonObject = jsonElement.getAsJsonObject();
         JsonElement opsArray = jsonObject.get("ops");
         if(opsArray.isJsonArray()) {
            for(JsonElement jsonElement1 : opsArray.getAsJsonArray()) {
               ops.ops.add(jsonElement1.getAsString());
            }
         }
      } catch (Exception var8) {
         ;
      }

      return ops;
   }
}
