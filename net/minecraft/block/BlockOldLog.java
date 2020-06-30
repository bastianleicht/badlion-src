package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlockOldLog extends BlockLog {
   public static final PropertyEnum VARIANT = PropertyEnum.create("variant", BlockPlanks.EnumType.class, new Predicate() {
      public boolean apply(BlockPlanks.EnumType p_apply_1_) {
         return p_apply_1_.getMetadata() < 4;
      }
   });
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis;

   public BlockOldLog() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockPlanks.EnumType.OAK).withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
   }

   public MapColor getMapColor(IBlockState state) {
      BlockPlanks.EnumType blockplanks$enumtype = (BlockPlanks.EnumType)state.getValue(VARIANT);
      switch($SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis()[((BlockLog.EnumAxis)state.getValue(LOG_AXIS)).ordinal()]) {
      case 1:
      case 3:
      case 4:
      default:
         switch($SWITCH_TABLE$net$minecraft$block$BlockPlanks$EnumType()[blockplanks$enumtype.ordinal()]) {
         case 1:
         default:
            return BlockPlanks.EnumType.SPRUCE.func_181070_c();
         case 2:
            return BlockPlanks.EnumType.DARK_OAK.func_181070_c();
         case 3:
            return MapColor.quartzColor;
         case 4:
            return BlockPlanks.EnumType.SPRUCE.func_181070_c();
         }
      case 2:
         return blockplanks$enumtype.func_181070_c();
      }
   }

   public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.OAK.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.SPRUCE.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.BIRCH.getMetadata()));
      list.add(new ItemStack(itemIn, 1, BlockPlanks.EnumType.JUNGLE.getMetadata()));
   }

   public IBlockState getStateFromMeta(int meta) {
      IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockPlanks.EnumType.byMetadata((meta & 3) % 4));
      switch(meta & 12) {
      case 0:
         iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Y);
         break;
      case 4:
         iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.X);
         break;
      case 8:
         iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.Z);
         break;
      default:
         iblockstate = iblockstate.withProperty(LOG_AXIS, BlockLog.EnumAxis.NONE);
      }

      return iblockstate;
   }

   public int getMetaFromState(IBlockState state) {
      int i = 0;
      i = i | ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata();
      switch($SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis()[((BlockLog.EnumAxis)state.getValue(LOG_AXIS)).ordinal()]) {
      case 1:
         i |= 4;
      case 2:
      default:
         break;
      case 3:
         i |= 8;
         break;
      case 4:
         i |= 12;
      }

      return i;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{VARIANT, LOG_AXIS});
   }

   protected ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata());
   }

   public int damageDropped(IBlockState state) {
      return ((BlockPlanks.EnumType)state.getValue(VARIANT)).getMetadata();
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

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis;
      if($SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockLog.EnumAxis.values().length];

         try {
            var0[BlockLog.EnumAxis.NONE.ordinal()] = 4;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[BlockLog.EnumAxis.X.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[BlockLog.EnumAxis.Y.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockLog.EnumAxis.Z.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockLog$EnumAxis = var0;
         return var0;
      }
   }
}
