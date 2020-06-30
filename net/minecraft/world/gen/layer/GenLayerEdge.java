package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;

public class GenLayerEdge extends GenLayer {
   private final GenLayerEdge.Mode field_151627_c;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode;

   public GenLayerEdge(long p_i45474_1_, GenLayer p_i45474_3_, GenLayerEdge.Mode p_i45474_4_) {
      super(p_i45474_1_);
      this.parent = p_i45474_3_;
      this.field_151627_c = p_i45474_4_;
   }

   public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
      switch($SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode()[this.field_151627_c.ordinal()]) {
      case 1:
      default:
         return this.getIntsCoolWarm(areaX, areaY, areaWidth, areaHeight);
      case 2:
         return this.getIntsHeatIce(areaX, areaY, areaWidth, areaHeight);
      case 3:
         return this.getIntsSpecial(areaX, areaY, areaWidth, areaHeight);
      }
   }

   private int[] getIntsCoolWarm(int p_151626_1_, int p_151626_2_, int p_151626_3_, int p_151626_4_) {
      int i = p_151626_1_ - 1;
      int j = p_151626_2_ - 1;
      int k = 1 + p_151626_3_ + 1;
      int l = 1 + p_151626_4_ + 1;
      int[] aint = this.parent.getInts(i, j, k, l);
      int[] aint1 = IntCache.getIntCache(p_151626_3_ * p_151626_4_);

      for(int i1 = 0; i1 < p_151626_4_; ++i1) {
         for(int j1 = 0; j1 < p_151626_3_; ++j1) {
            this.initChunkSeed((long)(j1 + p_151626_1_), (long)(i1 + p_151626_2_));
            int k1 = aint[j1 + 1 + (i1 + 1) * k];
            if(k1 == 1) {
               int l1 = aint[j1 + 1 + (i1 + 1 - 1) * k];
               int i2 = aint[j1 + 1 + 1 + (i1 + 1) * k];
               int j2 = aint[j1 + 1 - 1 + (i1 + 1) * k];
               int k2 = aint[j1 + 1 + (i1 + 1 + 1) * k];
               boolean flag = l1 == 3 || i2 == 3 || j2 == 3 || k2 == 3;
               boolean flag1 = l1 == 4 || i2 == 4 || j2 == 4 || k2 == 4;
               if(flag || flag1) {
                  k1 = 2;
               }
            }

            aint1[j1 + i1 * p_151626_3_] = k1;
         }
      }

      return aint1;
   }

   private int[] getIntsHeatIce(int p_151624_1_, int p_151624_2_, int p_151624_3_, int p_151624_4_) {
      int i = p_151624_1_ - 1;
      int j = p_151624_2_ - 1;
      int k = 1 + p_151624_3_ + 1;
      int l = 1 + p_151624_4_ + 1;
      int[] aint = this.parent.getInts(i, j, k, l);
      int[] aint1 = IntCache.getIntCache(p_151624_3_ * p_151624_4_);

      for(int i1 = 0; i1 < p_151624_4_; ++i1) {
         for(int j1 = 0; j1 < p_151624_3_; ++j1) {
            int k1 = aint[j1 + 1 + (i1 + 1) * k];
            if(k1 == 4) {
               int l1 = aint[j1 + 1 + (i1 + 1 - 1) * k];
               int i2 = aint[j1 + 1 + 1 + (i1 + 1) * k];
               int j2 = aint[j1 + 1 - 1 + (i1 + 1) * k];
               int k2 = aint[j1 + 1 + (i1 + 1 + 1) * k];
               boolean flag = l1 == 2 || i2 == 2 || j2 == 2 || k2 == 2;
               boolean flag1 = l1 == 1 || i2 == 1 || j2 == 1 || k2 == 1;
               if(flag1 || flag) {
                  k1 = 3;
               }
            }

            aint1[j1 + i1 * p_151624_3_] = k1;
         }
      }

      return aint1;
   }

   private int[] getIntsSpecial(int p_151625_1_, int p_151625_2_, int p_151625_3_, int p_151625_4_) {
      int[] aint = this.parent.getInts(p_151625_1_, p_151625_2_, p_151625_3_, p_151625_4_);
      int[] aint1 = IntCache.getIntCache(p_151625_3_ * p_151625_4_);

      for(int i = 0; i < p_151625_4_; ++i) {
         for(int j = 0; j < p_151625_3_; ++j) {
            this.initChunkSeed((long)(j + p_151625_1_), (long)(i + p_151625_2_));
            int k = aint[j + i * p_151625_3_];
            if(k != 0 && this.nextInt(13) == 0) {
               k |= 1 + this.nextInt(15) << 8 & 3840;
            }

            aint1[j + i * p_151625_3_] = k;
         }
      }

      return aint1;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode;
      if($SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode != null) {
         return var10000;
      } else {
         int[] var0 = new int[GenLayerEdge.Mode.values().length];

         try {
            var0[GenLayerEdge.Mode.COOL_WARM.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[GenLayerEdge.Mode.HEAT_ICE.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[GenLayerEdge.Mode.SPECIAL.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$world$gen$layer$GenLayerEdge$Mode = var0;
         return var0;
      }
   }

   public static enum Mode {
      COOL_WARM,
      HEAT_ICE,
      SPECIAL;
   }
}
