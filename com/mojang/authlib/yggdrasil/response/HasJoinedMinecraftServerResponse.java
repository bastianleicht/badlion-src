package com.mojang.authlib.yggdrasil.response;

import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.yggdrasil.response.Response;
import java.util.UUID;

public class HasJoinedMinecraftServerResponse extends Response {
   private UUID id;
   private PropertyMap properties;

   public UUID getId() {
      return this.id;
   }

   public PropertyMap getProperties() {
      return this.properties;
   }
}
