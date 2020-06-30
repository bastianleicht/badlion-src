package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.Response;
import java.util.UUID;

public class MinecraftProfilePropertiesResponse extends Response {
   private UUID id;
   private String name;
   private PropertyMap properties;

   public UUID getId() {
      return this.id;
   }

   public String getName() {
      return this.name;
   }

   public PropertyMap getProperties() {
      return this.properties;
   }
}
