package com.mojang.realmsclient.dto;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.realmsclient.dto.ValueObject;
import com.mojang.realmsclient.util.JsonUtils;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Backup extends ValueObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public String backupId;
   public Date lastModifiedDate;
   public long size;
   private boolean uploadedVersion = false;
   public Map metadata = new HashMap();
   public Map changeList = new HashMap();

   public static Backup parse(JsonElement node) {
      JsonObject object = node.getAsJsonObject();
      Backup backup = new Backup();

      try {
         backup.backupId = JsonUtils.getStringOr("backupId", object, "");
         backup.lastModifiedDate = JsonUtils.getDateOr("lastModifiedDate", object);
         backup.size = JsonUtils.getLongOr("size", object, 0L);
         if(object.has("metadata")) {
            JsonObject metadataObject = object.getAsJsonObject("metadata");

            for(Entry<String, JsonElement> elem : metadataObject.entrySet()) {
               if(!((JsonElement)elem.getValue()).isJsonNull()) {
                  backup.metadata.put(format((String)elem.getKey()), ((JsonElement)elem.getValue()).getAsString());
               }
            }
         }
      } catch (Exception var7) {
         LOGGER.error("Could not parse Backup: " + var7.getMessage());
      }

      return backup;
   }

   private static String format(String key) {
      String[] worlds = key.split("_");
      StringBuilder sb = new StringBuilder();

      for(String world : worlds) {
         if(world != null && world.length() >= 1) {
            if(world.equals("of")) {
               sb.append(world).append(" ");
            } else {
               char firstCharacter = Character.toUpperCase(world.charAt(0));
               sb.append(firstCharacter).append(world.substring(1, world.length())).append(" ");
            }
         }
      }

      return sb.toString();
   }

   public boolean isUploadedVersion() {
      return this.uploadedVersion;
   }

   public void setUploadedVersion(boolean uploadedVersion) {
      this.uploadedVersion = uploadedVersion;
   }
}
