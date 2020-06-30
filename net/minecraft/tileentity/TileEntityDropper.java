package net.minecraft.tileentity;

import net.minecraft.tileentity.TileEntityDispenser;

public class TileEntityDropper extends TileEntityDispenser {
   public String getName() {
      return this.hasCustomName()?this.customName:"container.dropper";
   }

   public String getGuiID() {
      return "minecraft:dropper";
   }
}
