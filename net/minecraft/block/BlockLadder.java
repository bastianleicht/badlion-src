package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
   public static final PropertyDirection FACING = PropertyDirection.create("facing", (Predicate)EnumFacing.Plane.HORIZONTAL);
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   protected BlockLadder() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      if(iblockstate.getBlock() == this) {
         float f = 0.125F;
         switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[((EnumFacing)iblockstate.getValue(FACING)).ordinal()]) {
         case 3:
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            break;
         case 4:
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            break;
         case 5:
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;
         case 6:
         default:
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
         }
      }

   }

   public boolean isOpaqueCube() {
      return false;
   }

   public boolean isFullCube() {
      return false;
   }

   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.west()).getBlock().isNormalCube()?true:(worldIn.getBlockState(pos.east()).getBlock().isNormalCube()?true:(worldIn.getBlockState(pos.north()).getBlock().isNormalCube()?true:worldIn.getBlockState(pos.south()).getBlock().isNormalCube()));
   }

   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if(facing.getAxis().isHorizontal() && this.canBlockStay(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      } else {
         for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if(this.canBlockStay(worldIn, pos, enumfacing)) {
               return this.getDefaultState().withProperty(FACING, enumfacing);
            }
         }

         return this.getDefaultState();
      }
   }

   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing enumfacing = (EnumFacing)state.getValue(FACING);
      if(!this.canBlockStay(worldIn, pos, enumfacing)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
   }

   protected boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
      return worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock().isNormalCube();
   }

   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   public IBlockState getStateFromMeta(int meta) {
      EnumFacing enumfacing = EnumFacing.getFront(meta);
      if(enumfacing.getAxis() == EnumFacing.Axis.Y) {
         enumfacing = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, enumfacing);
   }

   public int getMetaFromState(IBlockState state) {
      return ((EnumFacing)state.getValue(FACING)).getIndex();
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{FACING});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing;
      if($SWITCH_TABLE$net$minecraft$util$EnumFacing != null) {
         return var10000;
      } else {
         int[] var0 = new int[EnumFacing.values().length];

         try {
            var0[EnumFacing.DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[EnumFacing.EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[EnumFacing.NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[EnumFacing.SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[EnumFacing.UP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[EnumFacing.WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$util$EnumFacing = var0;
         return var0;
      }
   }
}
