package net.minecraft.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBook extends Item {
   public boolean isItemTool(ItemStack stack) {
      return stack.stackSize == 1;
   }

   public int getItemEnchantability() {
      return 1;
   }
}
