package net.minecraft.block;

import java.util.List;
import net.minecraft.block.BlockBasePressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockPressurePlate extends BlockBasePressurePlate {
   public static final PropertyBool POWERED = PropertyBool.create("powered");
   private final BlockPressurePlate.Sensitivity sensitivity;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity;

   protected BlockPressurePlate(Material materialIn, BlockPressurePlate.Sensitivity sensitivityIn) {
      super(materialIn);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)));
      this.sensitivity = sensitivityIn;
   }

   protected int getRedstoneStrength(IBlockState state) {
      return ((Boolean)state.getValue(POWERED)).booleanValue()?15:0;
   }

   protected IBlockState setRedstoneStrength(IBlockState state, int strength) {
      return state.withProperty(POWERED, Boolean.valueOf(strength > 0));
   }

   protected int computeRedstoneStrength(World worldIn, BlockPos pos) {
      AxisAlignedBB axisalignedbb = this.getSensitiveAABB(pos);
      List<? extends Entity> list;
      switch($SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity()[this.sensitivity.ordinal()]) {
      case 1:
         list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, axisalignedbb);
         break;
      case 2:
         list = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
         break;
      default:
         return 0;
      }

      if(!list.isEmpty()) {
         for(Entity entity : list) {
            if(!entity.doesEntityNotTriggerPressurePlate()) {
               return 15;
            }
         }
      }

      return 0;
   }

   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWERED, Boolean.valueOf(meta == 1));
   }

   public int getMetaFromState(IBlockState state) {
      return ((Boolean)state.getValue(POWERED)).booleanValue()?1:0;
   }

   protected BlockState createBlockState() {
      return new BlockState(this, new IProperty[]{POWERED});
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity;
      if($SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity != null) {
         return var10000;
      } else {
         int[] var0 = new int[BlockPressurePlate.Sensitivity.values().length];

         try {
            var0[BlockPressurePlate.Sensitivity.EVERYTHING.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[BlockPressurePlate.Sensitivity.MOBS.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$block$BlockPressurePlate$Sensitivity = var0;
         return var0;
      }
   }

   public static enum Sensitivity {
      EVERYTHING,
      MOBS;
   }
}
