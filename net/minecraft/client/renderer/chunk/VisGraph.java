package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Queue;
import java.util.Set;
import net.minecraft.client.renderer.chunk.SetVisibility;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph {
   private static final int field_178616_a = (int)Math.pow(16.0D, 0.0D);
   private static final int field_178614_b = (int)Math.pow(16.0D, 1.0D);
   private static final int field_178615_c = (int)Math.pow(16.0D, 2.0D);
   private final BitSet field_178612_d = new BitSet(4096);
   private static final int[] field_178613_e = new int[1352];
   private int field_178611_f = 4096;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   static {
      int i = 0;
      int j = 15;
      int k = 0;

      for(int l = 0; l < 16; ++l) {
         for(int i1 = 0; i1 < 16; ++i1) {
            for(int j1 = 0; j1 < 16; ++j1) {
               if(l == 0 || l == 15 || i1 == 0 || i1 == 15 || j1 == 0 || j1 == 15) {
                  field_178613_e[k++] = getIndex(l, i1, j1);
               }
            }
         }
      }

   }

   public void func_178606_a(BlockPos pos) {
      this.field_178612_d.set(getIndex(pos), true);
      --this.field_178611_f;
   }

   private static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   private static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility setvisibility = new SetVisibility();
      if(4096 - this.field_178611_f < 256) {
         setvisibility.setAllVisible(true);
      } else if(this.field_178611_f == 0) {
         setvisibility.setAllVisible(false);
      } else {
         for(int i : field_178613_e) {
            if(!this.field_178612_d.get(i)) {
               setvisibility.setManyVisible(this.func_178604_a(i));
            }
         }
      }

      return setvisibility;
   }

   public Set func_178609_b(BlockPos pos) {
      return this.func_178604_a(getIndex(pos));
   }

   private Set func_178604_a(int p_178604_1_) {
      Set<EnumFacing> set = EnumSet.noneOf(EnumFacing.class);
      Queue<Integer> queue = Lists.newLinkedList();
      queue.add(IntegerCache.func_181756_a(p_178604_1_));
      this.field_178612_d.set(p_178604_1_, true);

      while(!((Queue)queue).isEmpty()) {
         int i = ((Integer)queue.poll()).intValue();
         this.func_178610_a(i, set);

         EnumFacing[] var8;
         for(EnumFacing enumfacing : var8 = EnumFacing.values()) {
            int j = this.func_178603_a(i, enumfacing);
            if(j >= 0 && !this.field_178612_d.get(j)) {
               this.field_178612_d.set(j, true);
               queue.add(IntegerCache.func_181756_a(j));
            }
         }
      }

      return set;
   }

   private void func_178610_a(int p_178610_1_, Set p_178610_2_) {
      int i = p_178610_1_ >> 0 & 15;
      if(i == 0) {
         p_178610_2_.add(EnumFacing.WEST);
      } else if(i == 15) {
         p_178610_2_.add(EnumFacing.EAST);
      }

      int j = p_178610_1_ >> 8 & 15;
      if(j == 0) {
         p_178610_2_.add(EnumFacing.DOWN);
      } else if(j == 15) {
         p_178610_2_.add(EnumFacing.UP);
      }

      int k = p_178610_1_ >> 4 & 15;
      if(k == 0) {
         p_178610_2_.add(EnumFacing.NORTH);
      } else if(k == 15) {
         p_178610_2_.add(EnumFacing.SOUTH);
      }

   }

   private int func_178603_a(int p_178603_1_, EnumFacing p_178603_2_) {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[p_178603_2_.ordinal()]) {
      case 1:
         if((p_178603_1_ >> 8 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - field_178615_c;
      case 2:
         if((p_178603_1_ >> 8 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + field_178615_c;
      case 3:
         if((p_178603_1_ >> 4 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - field_178614_b;
      case 4:
         if((p_178603_1_ >> 4 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + field_178614_b;
      case 5:
         if((p_178603_1_ >> 0 & 15) == 0) {
            return -1;
         }

         return p_178603_1_ - field_178616_a;
      case 6:
         if((p_178603_1_ >> 0 & 15) == 15) {
            return -1;
         }

         return p_178603_1_ + field_178616_a;
      default:
         return -1;
      }
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
