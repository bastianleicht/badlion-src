package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.dto.ServerActivity;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.ArrayList;
import java.util.List;

public class ServerActivityList {
   public long periodInMillis;
   public List serverActivities = new ArrayList();

   public static ServerActivityList parse(String json) {
      ServerActivityList activityList = new ServerActivityList();
      JsonParser parser = new JsonParser();

      try {
         JsonElement jsonElement = parser.parse(json);
         JsonObject object = jsonElement.getAsJsonObject();
         activityList.periodInMillis = JsonUtils.getLongOr("periodInMillis", object, -1L);
         JsonElement activityArray = object.get("playerActivityDto");
         if(activityArray != null && activityArray.isJsonArray()) {
            for(JsonElement element : activityArray.getAsJsonArray()) {
               ServerActivity sa = ServerActivity.parse(element.getAsJsonObject());
               activityList.serverActivities.add(sa);
            }
         }
      } catch (Exception var10) {
         ;
      }

      return activityList;
   }
}
