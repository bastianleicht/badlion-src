package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockHugeMushroom.EnumType.class);
   private final Block smallBlock;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType;

   public BlockHugeMushroom(Material p_i46392_1_, MapColor p_i46392_2_, Block p_i46392_3_) {
      super(p_i46392_1_, p_i46392_2_);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
      this.smallBlock = p_i46392_3_;
   }

   public int quantityDropped(Random random) {
      return Math.max(0, random.nextInt(10) - 7);
   }

   public MapColor getMapColor(IBlockState state) {
      switch($SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType()[((BlockHugeMushroom.EnumType)state.getValue(VARIANT)).ordinal()]) {
      case 10:
         return MapColor.sandColor;
      case 11:
         return MapColor.sandColor;
      case 12:
      default:
         return super.getMapColor(state);
      case 13:
         return MapColor.clothColor;
      }
   }

   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(this.smallBlock);
   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState();
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockHugeMushroom.EnumType.byMetadata(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockHugeMushroom.EnumType)state.getValue(VARIANT)).getMetadata();
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{VARIANT});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType;
      if($SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockHugeMushroom.EnumType.values().length];

         try {
            var0[BlockHugeMushroom.EnumType.ALL_INSIDE.ordinal()] = 11;
         } catch (NoSuchFieldError var13) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.ALL_OUTSIDE.ordinal()] = 12;
         } catch (NoSuchFieldError var12) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.ALL_STEM.ordinal()] = 13;
         } catch (NoSuchFieldError var11) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.CENTER.ordinal()] = 5;
         } catch (NoSuchFieldError var10) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var9) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.NORTH.ordinal()] = 2;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.NORTH_EAST.ordinal()] = 3;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.NORTH_WEST.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.SOUTH.ordinal()] = 8;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.SOUTH_EAST.ordinal()] = 9;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.SOUTH_WEST.ordinal()] = 7;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.STEM.ordinal()] = 10;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockHugeMushroom.EnumType.WEST.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockHugeMushroom$EnumType = var0;
         return var0;
      }
   }

   public static enum EnumType implements IStringSerializable {
      NORTH_WEST(1, "north_west"),
      NORTH(2, "north"),
      NORTH_EAST(3, "north_east"),
      WEST(4, "west"),
      CENTER(5, "center"),
      EAST(6, "east"),
      SOUTH_WEST(7, "south_west"),
      SOUTH(8, "south"),
      SOUTH_EAST(9, "south_east"),
      STEM(10, "stem"),
      ALL_INSIDE(0, "all_inside"),
      ALL_OUTSIDE(14, "all_outside"),
      ALL_STEM(15, "all_stem");

      private static final BlockHugeMushroom.EnumType[] META_LOOKUP = new BlockHugeMushroom.EnumType[16];
      private final int meta;
      private final String name;

      static {
         BlockHugeMushroom.EnumType[] var3;
         for(BlockHugeMushroom.EnumType blockhugemushroom$enumtype : var3 = values()) {
            META_LOOKUP[blockhugemushroom$enumtype.getMetadata()] = blockhugemushroom$enumtype;
         }

      }

      private EnumType(int meta, String name) {
         this.meta = meta;
         this.name = name;
      }

      public int getMetadata() {
         return this.meta;
      }

      public String toString() {
         return this.name;
      }

      public static BlockHugeMushroom.EnumType byMetadata(int meta) {
         if(meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         BlockHugeMushroom.EnumType blockhugemushroom$enumtype = META_LOOKUP[meta];
         return blockhugemushroom$enumtype == null?META_LOOKUP[0]:blockhugemushroom$enumtype;
      }

      public String getName() {
         return this.name;
      }
   }
}
