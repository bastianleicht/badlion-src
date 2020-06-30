package net.minecraft.block;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public abstract class BlockLog extends BlockRotatedPillar {
   public static final PropertyEnum LOG_AXIS = PropertyEnum.create("axis", BlockLog.EnumAxis.class);

   public BlockLog() {
      super(Material.wood);
      this.setCreativeTab(CreativeTabs.tabBlock);
      this.setHardness(2.0F);
      this.setStepSound(soundTypeWood);
   }

   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      int i = 4;
      int j = i + 1;
      if(worldIn.isAreaLoaded(pos.add(-j, -j, -j), pos.add(j, j, j))) {
         for(BlockPos blockpos : BlockPos.getAllInBox(pos.add(-i, -i, -i), pos.add(i, i, i))) {
            IBlockState iblockstate = worldIn.getBlockState(blockpos);
            if(iblockstate.getBlock().getMaterial() == Material.leaves && !((Boolean)iblockstate.getValue(BlockLeaves.CHECK_DECAY)).booleanValue()) {
               worldIn.setBlockState(blockpos, iblockstate.withProperty(BlockLeaves.CHECK_DECAY, Boolean.valueOf(true)), 4);
            }
         }
      }

   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(LOG_AXIS, BlockLog.EnumAxis.fromFacingAxis(facing.getAxis()));
   }

   public static enum EnumAxis implements IStringSerializable {
      X("x"),
      Y("y"),
      Z("z"),
      NONE("none");

      private final String name;
      // $FF: synthetic field
      private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis;

      private EnumAxis(String name) {
         this.name = name;
      }

      public String toString() {
         return this.name;
      }

      public static BlockLog.EnumAxis fromFacingAxis(EnumFacing.Axis axis) {
         switch($SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis()[axis.ordinal()]) {
         case 1:
            return X;
         case 2:
            return Y;
         case 3:
            return Z;
         default:
            return NONE;
         }
      }

      public String getName() {
         return this.name;
      }

      // $FF: synthetic method
      static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis() {
         int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis;
         if($SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis != null) {
            return var10000;
         } else {
            int[] var0 = new int[EnumFacing.Axis.values().length];

            try {
               var0[EnumFacing.Axis.X.ordinal()] = 1;
            } catch (NoSuchFieldError var3) {
               ;
            }

            try {
               var0[EnumFacing.Axis.Y.ordinal()] = 2;
            } catch (NoSuchFieldError var2) {
               ;
            }

            try {
               var0[EnumFacing.Axis.Z.ordinal()] = 3;
            } catch (NoSuchFieldError var1) {
               ;
            }

            $SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis = var0;
            return var0;
         }
      }
   }
}
