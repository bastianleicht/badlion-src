package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigTree;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenSavannaTree;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BlockSapling extends BlockBush implements IGrowable {
   public static final PropertyEnum TYPE = PropertyEnum.create("type", BlockPlanks.EnumType.class);
   public static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 1);
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType;

   protected BlockSapling() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(TYPE, BlockPlanks.EnumType.OAK).withProperty(STAGE, Integer.valueOf(0)));
      float f = 0.4F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public String getLocalizedName() {
      return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + BlockPlanks.EnumType.OAK.getUnlocalizedName() + ".name");
   }

   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if(!worldIn.isRemote) {
         super.updateTick(worldIn, pos, state, rand);
         if(worldIn.getLightFromNeighbors(pos.up()) >= 9 && rand.nextInt(7) == 0) {
            this.grow(worldIn, pos, state, rand);
         }
      }

   }

   public void grow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if(((Integer)state.getValue(STAGE)).intValue() == 0) {
         worldIn.setBlockState(pos, state.cycleProperty(STAGE), 4);
      } else {
         this.generateTree(worldIn, pos, state, rand);
      }

   }

   public void generateTree(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      WorldGenerator worldgenerator = (WorldGenerator)(rand.nextInt(10) == 0?new WorldGenBigTree(true):new WorldGenTrees(true));
      int i = 0;
      int j = 0;
      boolean flag = false;
      switch($SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType()[((BlockPlanks.EnumType)state.getValue(TYPE)).ordinal()]) {
      case 1:
      default:
         break;
      case 2:
         label108:
         for(i = 0; i >= -1; --i) {
            for(j = 0; j >= -1; --j) {
               if(this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.SPRUCE)) {
                  worldgenerator = new WorldGenMegaPineTree(false, rand.nextBoolean());
                  flag = true;
                  break label108;
               }
            }
         }

         if(!flag) {
            j = 0;
            i = 0;
            worldgenerator = new WorldGenTaiga2(true);
         }
         break;
      case 3:
         worldgenerator = new WorldGenForest(true, false);
         break;
      case 4:
         IBlockState iblockstate = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
         IBlockState iblockstate1 = Blocks.leaves.getDefaultState().withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE).withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(false));

         label263:
         for(i = 0; i >= -1; --i) {
            for(j = 0; j >= -1; --j) {
               if(this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.JUNGLE)) {
                  worldgenerator = new WorldGenMegaJungle(true, 10, 20, iblockstate, iblockstate1);
                  flag = true;
                  break label263;
               }
            }
         }

         if(!flag) {
            j = 0;
            i = 0;
            worldgenerator = new WorldGenTrees(true, 4 + rand.nextInt(7), iblockstate, iblockstate1, false);
         }
         break;
      case 5:
         worldgenerator = new WorldGenSavannaTree(true);
         break;
      case 6:
         label384:
         for(i = 0; i >= -1; --i) {
            for(j = 0; j >= -1; --j) {
               if(this.func_181624_a(worldIn, pos, i, j, BlockPlanks.EnumType.DARK_OAK)) {
                  worldgenerator = new WorldGenCanopyTree(true);
                  flag = true;
                  break label384;
               }
            }
         }

         if(!flag) {
            return;
         }
      }

      IBlockState iblockstate2 = Blocks.air.getDefaultState();
      if(flag) {
         worldIn.setBlockState(pos.add(i, 0, j), iblockstate2, 4);
         worldIn.setBlockState(pos.add(i + 1, 0, j), iblockstate2, 4);
         worldIn.setBlockState(pos.add(i, 0, j + 1), iblockstate2, 4);
         worldIn.setBlockState(pos.add(i + 1, 0, j + 1), iblockstate2, 4);
      } else {
         worldIn.setBlockState(pos, iblockstate2, 4);
      }

      if(!worldgenerator.generate(worldIn, rand, pos.add(i, 0, j))) {
         if(flag) {
            worldIn.setBlockState(pos.add(i, 0, j), state, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j), state, 4);
            worldIn.setBlockState(pos.add(i, 0, j + 1), state, 4);
            worldIn.setBlockState(pos.add(i + 1, 0, j + 1), state, 4);
         } else {
            worldIn.setBlockState(pos, state, 4);
         }
      }

   }

   private boolean func_181624_a(World p_181624_1_, BlockPos p_181624_2_, int p_181624_3_, int p_181624_4_, BlockPlanks.EnumType p_181624_5_) {
      return this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_, 0, p_181624_4_ + 1), p_181624_5_) && this.isTypeAt(p_181624_1_, p_181624_2_.add(p_181624_3_ + 1, 0, p_181624_4_ + 1), p_181624_5_);
   }

   public boolean isTypeAt(World worldIn, BlockPos pos, BlockPlanks.EnumType type) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      return iblockstate.getBlock() == this && iblockstate.getValue(TYPE) == type;
   }

   public int damageDropped(IBlockState state) {
      return ((BlockPlanks.EnumType)state.getValue(TYPE)).getMetadata();
   }

   public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
      BlockPlanks.EnumType[] var7;
      for(BlockPlanks.EnumType blockplanks$enumtype : var7 = BlockPlanks.EnumType.values()) {
         list.add(new ItemStack(itemIn, 1, blockplanks$enumtype.getMetadata()));
      }

   }

   public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return (double)worldIn.rand.nextFloat() < 0.45D;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      this.grow(worldIn, pos, state, rand);
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(TYPE, BlockPlanks.EnumType.byMetadata(meta & 7)).withProperty(STAGE, Integer.valueOf((meta & 8) >> 3));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockPlanks.EnumType)state.getValue(TYPE)).getMetadata();
      i = i | ((Integer)state.getValue(STAGE)).intValue() << 3;
      return i;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{TYPE, STAGE});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType;
      if($SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockPlanks.EnumType.values().length];

         try {
            var0[BlockPlanks.EnumType.ACACIA.ordinal()] = 5;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[BlockPlanks.EnumType.BIRCH.ordinal()] = 3;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[BlockPlanks.EnumType.DARK_OAK.ordinal()] = 6;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[BlockPlanks.EnumType.JUNGLE.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BlockPlanks.EnumType.OAK.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockPlanks.EnumType.SPRUCE.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType = var0;
         return var0;
      }
   }
}
