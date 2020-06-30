package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockSilverfish extends Block {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockSilverfish.EnumType.class);
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType;

   public BlockSilverfish() {
      super(Material.clay);
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSilverfish.EnumType.STONE));
      this.setHardness(0.0F);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public int quantityDropped(Random random) {
      return 0;
   }

   public static boolean canContainSilverfish(IBlockState blockState) {
      Block block = blockState.getBlock();
      return blockState == Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE) || block == Blocks.cobblestone || block == Blocks.stonebrick;
   }

   protected ItemStack createStackedBlock(IBlockState state) {
      switch($SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType()[((BlockSilverfish.EnumType)state.getValue(VARIANT)).ordinal()]) {
      case 2:
         return new ItemStack(Blocks.cobblestone);
      case 3:
         return new ItemStack(Blocks.stonebrick);
      case 4:
         return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.MOSSY.getMetadata());
      case 5:
         return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CRACKED.getMetadata());
      case 6:
         return new ItemStack(Blocks.stonebrick, 1, BlockStoneBrick.EnumType.CHISELED.getMetadata());
      default:
         return new ItemStack(Blocks.stone);
      }
   }

   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if(!worldIn.isRemote && worldIn.getGameRules().getBoolean("doTileDrops")) {
         EntitySilverfish entitysilverfish = new EntitySilverfish(worldIn);
         entitysilverfish.setLocationAndAngles((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
         worldIn.spawnEntityInWorld(entitysilverfish);
         entitysilverfish.spawnExplosionParticle();
      }

   }

   public int getDamageValue(World worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      return iblockstate.getBlock().getMetaFromState(iblockstate);
   }

   public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
      BlockSilverfish.EnumType[] var7;
      for(BlockSilverfish.EnumType blocksilverfish$enumtype : var7 = BlockSilverfish.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, blocksilverfish$enumtype.getMetadata()));
      }

   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(VARIANT, BlockSilverfish.EnumType.byMetadata(meta));
   }

   public int getMetaFromState(IBlockState state) {
      return ((BlockSilverfish.EnumType)state.getValue(VARIANT)).getMetadata();
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{VARIANT});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType;
      if($SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockSilverfish.EnumType.values().length];

         try {
            var0[BlockSilverfish.EnumType.CHISELED_STONEBRICK.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[BlockSilverfish.EnumType.COBBLESTONE.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[BlockSilverfish.EnumType.CRACKED_STONEBRICK.ordinal()] = 5;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[BlockSilverfish.EnumType.MOSSY_STONEBRICK.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BlockSilverfish.EnumType.STONE.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockSilverfish.EnumType.STONEBRICK.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockSilverfish$EnumType = var0;
         return var0;
      }
   }

   public static enum EnumType implements IStringSerializable {
      STONE(0, "stone") {
         public IBlockState getModelBlock() {
            return Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, BlockStone.EnumType.STONE);
         }
      },
      COBBLESTONE(1, "cobblestone", "cobble") {
         public IBlockState getModelBlock() {
            return Blocks.cobblestone.getDefaultState();
         }
      },
      STONEBRICK(2, "stone_brick", "brick") {
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.DEFAULT);
         }
      },
      MOSSY_STONEBRICK(3, "mossy_brick", "mossybrick") {
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.MOSSY);
         }
      },
      CRACKED_STONEBRICK(4, "cracked_brick", "crackedbrick") {
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED);
         }
      },
      CHISELED_STONEBRICK(5, "chiseled_brick", "chiseledbrick") {
         public IBlockState getModelBlock() {
            return Blocks.stonebrick.getDefaultState().withProperty(BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CHISELED);
         }
      };

      private static final BlockSilverfish.EnumType[] META_LOOKUP = new BlockSilverfish.EnumType[values().length];
      private final int meta;
      private final String name;
      private final String unlocalizedName;

      static {
         BlockSilverfish.EnumType[] var3;
         for(BlockSilverfish.EnumType blocksilverfish$enumtype : var3 = values()) {
            META_LOOKUP[blocksilverfish$enumtype.getMetadata()] = blocksilverfish$enumtype;
         }

      }

      private EnumType(int meta, String name) {
         this(meta, name, name);
      }

      private EnumType(int meta, String name, String unlocalizedName) {
         this.meta = meta;
         this.name = name;
         this.unlocalizedName = unlocalizedName;
      }

      public int getMetadata() {
         return this.meta;
      }

      public String toString() {
         return this.name;
      }

      public static BlockSilverfish.EnumType byMetadata(int meta) {
         if(meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      public String getName() {
         return this.name;
      }

      public String getUnlocalizedName() {
         return this.unlocalizedName;
      }

      public abstract IBlockState getModelBlock();

      public static BlockSilverfish.EnumType forModelBlock(IBlockState model) {
         BlockSilverfish.EnumType[] var4;
         for(BlockSilverfish.EnumType blocksilverfish$enumtype : var4 = values()) {
            if(model == blocksilverfish$enumtype.getModelBlock()) {
               return blocksilverfish$enumtype;
            }
         }

         return STONE;
      }

      // $FF: synthetic method
      EnumType(int var3, String var4, BlockSilverfish.EnumType var5) {
         this(var3, var4);
      }

      // $FF: synthetic method
      EnumType(int var3, String var4, String var5, BlockSilverfish.EnumType var6) {
         this(var3, var4, var5);
      }
   }
}
