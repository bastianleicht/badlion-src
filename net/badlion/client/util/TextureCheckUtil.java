package net.badlion.client.util;

import net.minecraft.client.Minecraft;

public class TextureCheckUtil {
   public static String TEXTURE_NAME = "";
   private static final String[] xrayChecks = new String[]{"wool_colored_black", "quartz_ore", "red_sandstone_normal", "wool_colored_magenta", "tnt_side", "door_birch_lower", "coal_ore", "enchanting_table_top", "stone_diorite", "jukebox_side", "stone_diorite_smooth", "stonebrick_cracked", "repeater_off", "quartz_block_chiseled_top", "stone", "lava_flow", "cobblestone_mossy", "quartz_block_side", "dragon_egg", "sandstone_bottom", "sandstone_carved", "red_sandstone_smooth", "daylight_detector_inverted_top", "stonebrick", "prismarine_rough", "pumpkin_face_on", "diamond_ore", "brewing_stand_base", "log_acacia", "noteblock", "nether_brick", "piston_inner", "command_block", "daylight_detector_top", "stone_andesite_smooth", "obsidian", "log_oak", "wool_colored_purple", "quartz_block_lines", "anvil_base", "hardened_clay_stained_blue", "iron_block", "stonebrick_carved", "itemframe_background", "tnt_top", "hardened_clay_stained_silver", "hardened_clay_stained_white", "mycelium_top", "wool_colored_orange", "stone_granite", "stonebrick_mossy", "wool_colored_blue", "beacon", "hay_block_side", "gold_block", "comparator_on", "bedrock", "hardened_clay_stained_green", "red_sandstone_top", "door_iron_lower", "log_jungle", "wool_colored_white", "log_jungle_top", "melon_top", "lapis_ore", "dispenser_front_vertical", "dropper_front_horizontal", "sandstone_top", "cobblestone", "log_birch_top", "coal_block", "lava_still", "grass_side", "stone_andesite", "redstone_lamp_on", "mycelium_side", "mushroom_block_skin_brown", "furnace_front_on", "log_oak_top", "wool_colored_lime", "planks_oak", "log_acacia_top", "hardened_clay_stained_yellow", "sponge_wet", "hardened_clay", "clay", "dropper_front_vertical", "pumpkin_side", "iron_ore", "log_spruce", "door_birch_upper", "pumpkin_top", "log_spruce_top", "door_spruce_lower", "grass_top", "snow", "hay_block_top", "grass_side_snowed", "emerald_ore", "wool_colored_cyan", "stone_slab_side", "hardened_clay_stained_light_blue", "comparator_off", "ice_packed", "dirt", "door_dark_oak_upper", "quartz_block_top", "stone_granite_smooth", "gold_ore", "dirt_podzol_top", "hardened_clay_stained_lime", "piston_top_normal", "wool_colored_brown", "furnace_side", "prismarine_bricks", "jukebox_top", "crafting_table_top", "pumpkin_face_off", "quartz_block_chiseled", "redstone_ore", "farmland_wet", "glowstone", "wool_colored_silver", "redstone_lamp_off", "log_big_oak", "wool_colored_light_blue", "planks_big_oak", "furnace_top", "mushroom_block_skin_red", "bookshelf", "hardened_clay_stained_cyan", "piston_bottom", "soul_sand", "planks_acacia", "mushroom_block_skin_stem", "red_sand", "hopper_inside", "repeater_on", "crafting_table_front", "hardened_clay_stained_gray", "netherrack", "end_stone", "hardened_clay_stained_magenta", "sponge", "sand", "quartz_block_lines_top", "gravel", "wool_colored_gray", "hardened_clay_stained_brown", "quartz_block_bottom", "dirt_podzol_side", "sandstone_smooth", "red_sandstone_bottom", "red_sandstone_carved", "crafting_table_side", "wool_colored_red", "piston_top_sticky", "log_birch", "bed_feet_top", "hardened_clay_stained_black", "cauldron_inner", "planks_jungle", "wool_colored_pink", "tnt_bottom", "melon_side", "furnace_front_off", "door_wood_lower", "hardened_clay_stained_purple", "hopper_outside", "planks_birch", "stone_slab_top", "log_big_oak_top", "diamond_block", "emerald_block", "sandstone_normal", "mushroom_block_inside", "prismarine_dark", "coarse_dirt", "door_spruce_upper", "wool_colored_yellow", "door_dark_oak_lower", "sea_lantern", "hardened_clay_stained_pink", "wool_colored_green", "lapis_block", "hardened_clay_stained_orange", "redstone_block", "planks_spruce", "bed_head_top", "piston_side", "dispenser_front_horizontal", "farmland_dry", "daylight_detector_side", "hardened_clay_stained_red"};

   public static int[][] checkTexture(int[][] texture) {
      if(TEXTURE_NAME.equals("double_plant_grass_top")) {
         return texture;
      } else {
         if(TEXTURE_NAME.startsWith("leaves_") && !Minecraft.getMinecraft().gameSettings.fancyGraphics) {
            int i = 0;
            int j = 0;

            for(int[] aint : texture) {
               if(aint != null) {
                  for(int k : aint) {
                     k = k & 16777215;
                     texture[i][j] = -16777216 | k;
                     ++j;
                  }

                  j = 0;
                  ++i;
               }
            }
         }

         for(String s : xrayChecks) {
            if((TEXTURE_NAME + "/").contains(s + "/")) {
               int i1 = 0;
               int j1 = 0;

               for(int[] aint1 : texture) {
                  if(aint1 != null) {
                     for(int l : aint1) {
                        l = l & 16777215;
                        texture[i1][j1] = -16777216 | l;
                        ++j1;
                     }

                     j1 = 0;
                     ++i1;
                  }
               }
            }
         }

         return texture;
      }
   }
}
