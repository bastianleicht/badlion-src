package net.minecraft.world;

import net.minecraft.world.WorldProvider;

public class WorldProviderSurface extends WorldProvider {
   public String getDimensionName() {
      return "Overworld";
   }

   public String getInternalNameSuffix() {
      return "";
   }
}
