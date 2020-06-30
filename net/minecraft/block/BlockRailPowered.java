package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
   public static final PropertyEnum SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate() {
      public boolean apply(BlockRailBase.EnumRailDirection p_apply_1_) {
         return p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST;
      }
   });
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection;

   protected BlockRailPowered() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, Boolean.valueOf(false)));
   }

   protected boolean func_176566_a(World worldIn, BlockPos pos, IBlockState state, boolean p_176566_4_, int p_176566_5_) {
      if(p_176566_5_ >= 8) {
         return false;
      } else {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         boolean flag = true;
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockRailBase.EnumRailDirection)state.getValue(SHAPE);
         switch($SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection()[blockrailbase$enumraildirection.ordinal()]) {
         case 1:
            if(p_176566_4_) {
               ++k;
            } else {
               --k;
            }
            break;
         case 2:
            if(p_176566_4_) {
               --i;
            } else {
               ++i;
            }
            break;
         case 3:
            if(p_176566_4_) {
               --i;
            } else {
               ++i;
               ++j;
               flag = false;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case 4:
            if(p_176566_4_) {
               --i;
               ++j;
               flag = false;
            } else {
               ++i;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case 5:
            if(p_176566_4_) {
               ++k;
            } else {
               --k;
               ++j;
               flag = false;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case 6:
            if(p_176566_4_) {
               ++k;
               ++j;
               flag = false;
            } else {
               --k;
            }

            blockrailbase$enumraildirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         return this.func_176567_a(worldIn, new BlockPos(i, j, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection)?true:flag && this.func_176567_a(worldIn, new BlockPos(i, j - 1, k), p_176566_4_, p_176566_5_, blockrailbase$enumraildirection);
      }
   }

   protected boolean func_176567_a(World worldIn, BlockPos p_176567_2_, boolean p_176567_3_, int distance, BlockRailBase.EnumRailDirection p_176567_5_) {
      IBlockState iblockstate = worldIn.getBlockState(p_176567_2_);
      if(iblockstate.getBlock() != this) {
         return false;
      } else {
         BlockRailBase.EnumRailDirection blockrailbase$enumraildirection = (BlockRailBase.EnumRailDirection)iblockstate.getValue(SHAPE);
         return p_176567_5_ != BlockRailBase.EnumRailDirection.EAST_WEST || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_NORTH && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH?(p_176567_5_ != BlockRailBase.EnumRailDirection.NORTH_SOUTH || blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.EAST_WEST && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_EAST && blockrailbase$enumraildirection != BlockRailBase.EnumRailDirection.ASCENDING_WEST?(((Boolean)iblockstate.getValue(POWERED)).booleanValue()?(worldIn.isBlockPowered(p_176567_2_)?true:this.func_176566_a(worldIn, p_176567_2_, iblockstate, p_176567_3_, distance + 1)):false):false):false;
      }
   }

   protected void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean flag = ((Boolean)state.getValue(POWERED)).booleanValue();
      boolean flag1 = worldIn.isBlockPowered(pos) || this.func_176566_a(worldIn, pos, state, true, 0) || this.func_176566_a(worldIn, pos, state, false, 0);
      if(flag1 != flag) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag1)), 3);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         if(((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }
      }

   }

   public IProperty getShapeProperty() {
      return SHAPE;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockRailBase.EnumRailDirection)state.getValue(SHAPE)).getMetadata();
      if(((Boolean)state.getValue(POWERED)).booleanValue()) {
         i |= 8;
      }

      return i;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{SHAPE, POWERED});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection;
      if($SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockRailBase.EnumRailDirection.values().length];

         try {
            var0[BlockRailBase.EnumRailDirection.ASCENDING_EAST.ordinal()] = 3;
         } catch (NoSuchFieldError var10) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.ASCENDING_NORTH.ordinal()] = 5;
         } catch (NoSuchFieldError var9) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.ASCENDING_SOUTH.ordinal()] = 6;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.ASCENDING_WEST.ordinal()] = 4;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.EAST_WEST.ordinal()] = 2;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.NORTH_EAST.ordinal()] = 10;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.NORTH_SOUTH.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.NORTH_WEST.ordinal()] = 9;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.SOUTH_EAST.ordinal()] = 7;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockRailBase.EnumRailDirection.SOUTH_WEST.ordinal()] = 8;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockRailBase$EnumRailDirection = var0;
         return var0;
      }
   }
}
