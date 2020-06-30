package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTool;

public class ItemSpade extends ItemTool {
   private static final Set EFFECTIVE_ON = Sets.newHashSet((Object[])(new Block[]{Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand}));

   public ItemSpade(Item.ToolMaterial material) {
      super(1.0F, material, EFFECTIVE_ON);
   }

   public boolean canHarvestBlock(Block blockIn) {
      return blockIn == Blocks.snow_layer?true:blockIn == Blocks.snow;
   }
}
