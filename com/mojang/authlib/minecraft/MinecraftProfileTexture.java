package com.mojang.authlib.minecraft;

import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MinecraftProfileTexture {
   private final String url;
   private final Map metadata;

   public MinecraftProfileTexture(String url, Map metadata) {
      this.url = url;
      this.metadata = metadata;
   }

   public String getUrl() {
      return this.url;
   }

   @Nullable
   public String getMetadata(String key) {
      return this.metadata == null?null:(String)this.metadata.get(key);
   }

   public String getHash() {
      return FilenameUtils.getBaseName(this.url);
   }

   public String toString() {
      return (new ToStringBuilder(this)).append("url", (Object)this.url).append("hash", (Object)this.getHash()).toString();
   }

   public static enum Type {
      SKIN,
      CAPE;
   }
}
