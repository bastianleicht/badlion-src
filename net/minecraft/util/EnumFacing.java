package net.minecraft.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3i;

public enum EnumFacing implements IStringSerializable {
   DOWN(0, 1, -1, "down", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Y, new Vec3i(0, -1, 0)),
   UP(1, 0, -1, "up", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Y, new Vec3i(0, 1, 0)),
   NORTH(2, 3, 2, "north", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, -1)),
   SOUTH(3, 2, 0, "south", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.Z, new Vec3i(0, 0, 1)),
   WEST(4, 5, 1, "west", EnumFacing.AxisDirection.NEGATIVE, EnumFacing.Axis.X, new Vec3i(-1, 0, 0)),
   EAST(5, 4, 3, "east", EnumFacing.AxisDirection.POSITIVE, EnumFacing.Axis.X, new Vec3i(1, 0, 0));

   private final int index;
   private final int opposite;
   private final int horizontalIndex;
   private final String name;
   private final EnumFacing.Axis axis;
   private final EnumFacing.AxisDirection axisDirection;
   private final Vec3i directionVec;
   private static final EnumFacing[] VALUES = new EnumFacing[6];
   private static final EnumFacing[] HORIZONTALS = new EnumFacing[4];
   private static final Map NAME_LOOKUP = Maps.newHashMap();
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing;

   static {
      EnumFacing[] var3;
      for(EnumFacing enumfacing : var3 = values()) {
         VALUES[enumfacing.index] = enumfacing;
         if(enumfacing.getAxis().isHorizontal()) {
            HORIZONTALS[enumfacing.horizontalIndex] = enumfacing;
         }

         NAME_LOOKUP.put(enumfacing.getName2().toLowerCase(), enumfacing);
      }

   }

   private EnumFacing(int indexIn, int oppositeIn, int horizontalIndexIn, String nameIn, EnumFacing.AxisDirection axisDirectionIn, EnumFacing.Axis axisIn, Vec3i directionVecIn) {
      this.index = indexIn;
      this.horizontalIndex = horizontalIndexIn;
      this.opposite = oppositeIn;
      this.name = nameIn;
      this.axis = axisIn;
      this.axisDirection = axisDirectionIn;
      this.directionVec = directionVecIn;
   }

   public int getIndex() {
      return this.index;
   }

   public int getHorizontalIndex() {
      return this.horizontalIndex;
   }

   public EnumFacing.AxisDirection getAxisDirection() {
      return this.axisDirection;
   }

   public EnumFacing getOpposite() {
      return getFront(this.opposite);
   }

   public EnumFacing rotateAround(EnumFacing.Axis axis) {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing$Axis()[axis.ordinal()]) {
      case 1:
         if(this != WEST && this != EAST) {
            return this.rotateX();
         }

         return this;
      case 2:
         if(this != UP && this != DOWN) {
            return this.rotateY();
         }

         return this;
      case 3:
         if(this != NORTH && this != SOUTH) {
            return this.rotateZ();
         }

         return this;
      default:
         throw new IllegalStateException("Unable to get CW facing for axis " + axis);
      }
   }

   public EnumFacing rotateY() {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[this.ordinal()]) {
      case 3:
         return EAST;
      case 4:
         return WEST;
      case 5:
         return NORTH;
      case 6:
         return SOUTH;
      default:
         throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
      }
   }

   private EnumFacing rotateX() {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[this.ordinal()]) {
      case 1:
         return SOUTH;
      case 2:
         return NORTH;
      case 3:
         return DOWN;
      case 4:
         return UP;
      case 5:
      case 6:
      default:
         throw new IllegalStateException("Unable to get X-rotated facing of " + this);
      }
   }

   private EnumFacing rotateZ() {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[this.ordinal()]) {
      case 1:
         return WEST;
      case 2:
         return EAST;
      case 3:
      case 4:
      default:
         throw new IllegalStateException("Unable to get Z-rotated facing of " + this);
      case 5:
         return UP;
      case 6:
         return DOWN;
      }
   }

   public EnumFacing rotateYCCW() {
      switch($SWITCH_TABLE$net$minecraft$util$EnumFacing()[this.ordinal()]) {
      case 3:
         return WEST;
      case 4:
         return EAST;
      case 5:
         return SOUTH;
      case 6:
         return NORTH;
      default:
         throw new IllegalStateException("Unable to get CCW facing of " + this);
      }
   }

   public int getFrontOffsetX() {
      return this.axis == EnumFacing.Axis.X?this.axisDirection.getOffset():0;
   }

   public int getFrontOffsetY() {
      return this.axis == EnumFacing.Axis.Y?this.axisDirection.getOffset():0;
   }

   public int getFrontOffsetZ() {
      return this.axis == EnumFacing.Axis.Z?this.axisDirection.getOffset():0;
   }

   public String getName2() {
      return this.name;
   }

   public EnumFacing.Axis getAxis() {
      return this.axis;
   }

   public static EnumFacing byName(String name) {
      return name == null?null:(EnumFacing)NAME_LOOKUP.get(name.toLowerCase());
   }

   public static EnumFacing getFront(int index) {
      return VALUES[MathHelper.abs_int(index % VALUES.length)];
   }

   public static EnumFacing getHorizontal(int p_176731_0_) {
      return HORIZONTALS[MathHelper.abs_int(p_176731_0_ % HORIZONTALS.length)];
   }

   public static EnumFacing fromAngle(double angle) {
      return getHorizontal(MathHelper.floor_double(angle / 90.0D + 0.5D) & 3);
   }

   public static EnumFacing random(Random rand) {
      return values()[rand.nextInt(values().length)];
   }

   public static EnumFacing getFacingFromVector(float p_176737_0_, float p_176737_1_, float p_176737_2_) {
      EnumFacing enumfacing = NORTH;
      float f = Float.MIN_VALUE;

      EnumFacing[] var8;
      for(EnumFacing enumfacing1 : var8 = values()) {
         float f1 = p_176737_0_ * (float)enumfacing1.directionVec.getX() + p_176737_1_ * (float)enumfacing1.directionVec.getY() + p_176737_2_ * (float)enumfacing1.directionVec.getZ();
         if(f1 > f) {
            f = f1;
            enumfacing = enumfacing1;
         }
      }

      return enumfacing;
   }

   public String toString() {
      return this.name;
   }

   public String getName() {
      return this.name;
   }

   public static EnumFacing func_181076_a(EnumFacing.AxisDirection p_181076_0_, EnumFacing.Axis p_181076_1_) {
      EnumFacing[] var5;
      for(EnumFacing enumfacing : var5 = values()) {
         if(enumfacing.getAxisDirection() == p_181076_0_ && enumfacing.getAxis() == p_181076_1_) {
            return enumfacing;
         }
      }

      throw new IllegalArgumentException("No such direction: " + p_181076_0_ + " " + p_181076_1_);
   }

   public Vec3i getDirectionVec() {
      return this.directionVec;
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

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing;
      if($SWITCH_TABLE$net$minecraft$util$EnumFacing != null) {
         return var10000;
      } else {
         int[] var0 = new int[values().length];

         try {
            var0[DOWN.ordinal()] = 1;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[EAST.ordinal()] = 6;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[NORTH.ordinal()] = 3;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[SOUTH.ordinal()] = 4;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[UP.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[WEST.ordinal()] = 5;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$util$EnumFacing = var0;
         return var0;
      }
   }

   public static enum Axis implements Predicate, IStringSerializable {
      X("x", EnumFacing.Plane.HORIZONTAL),
      Y("y", EnumFacing.Plane.VERTICAL),
      Z("z", EnumFacing.Plane.HORIZONTAL);

      private static final Map NAME_LOOKUP = Maps.newHashMap();
      private final String name;
      private final EnumFacing.Plane plane;

      static {
         EnumFacing.Axis[] var3;
         for(EnumFacing.Axis enumfacing$axis : var3 = values()) {
            NAME_LOOKUP.put(enumfacing$axis.getName2().toLowerCase(), enumfacing$axis);
         }

      }

      private Axis(String name, EnumFacing.Plane plane) {
         this.name = name;
         this.plane = plane;
      }

      public static EnumFacing.Axis byName(String name) {
         return name == null?null:(EnumFacing.Axis)NAME_LOOKUP.get(name.toLowerCase());
      }

      public String getName2() {
         return this.name;
      }

      public boolean isVertical() {
         return this.plane == EnumFacing.Plane.VERTICAL;
      }

      public boolean isHorizontal() {
         return this.plane == EnumFacing.Plane.HORIZONTAL;
      }

      public String toString() {
         return this.name;
      }

      public boolean apply(EnumFacing p_apply_1_) {
         return p_apply_1_ != null && p_apply_1_.getAxis() == this;
      }

      public EnumFacing.Plane getPlane() {
         return this.plane;
      }

      public String getName() {
         return this.name;
      }
   }

   public static enum AxisDirection {
      POSITIVE(1, "Towards positive"),
      NEGATIVE(-1, "Towards negative");

      private final int offset;
      private final String description;

      private AxisDirection(int offset, String description) {
         this.offset = offset;
         this.description = description;
      }

      public int getOffset() {
         return this.offset;
      }

      public String toString() {
         return this.description;
      }
   }

   public static enum Plane implements Predicate, Iterable {
      HORIZONTAL,
      VERTICAL;

      // $FF: synthetic field
      private static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane;

      public EnumFacing[] facings() {
         switch($SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane()[this.ordinal()]) {
         case 1:
            return new EnumFacing[]{EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
         case 2:
            return new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN};
         default:
            throw new Error("Someone\'s been tampering with the universe!");
         }
      }

      public EnumFacing random(Random rand) {
         EnumFacing[] aenumfacing = this.facings();
         return aenumfacing[rand.nextInt(aenumfacing.length)];
      }

      public boolean apply(EnumFacing p_apply_1_) {
         return p_apply_1_ != null && p_apply_1_.getAxis().getPlane() == this;
      }

      public Iterator iterator() {
         return Iterators.forArray(this.facings());
      }

      // $FF: synthetic method
      static int[] $SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane() {
         int[] var10000 = $SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane;
         if($SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane != null) {
            return var10000;
         } else {
            int[] var0 = new int[values().length];

            try {
               var0[HORIZONTAL.ordinal()] = 1;
            } catch (NoSuchFieldError var2) {
               ;
            }

            try {
               var0[VERTICAL.ordinal()] = 2;
            } catch (NoSuchFieldError var1) {
               ;
            }

            $SWITCH_TABLE$net$minecraft$util$EnumFacing$Plane = var0;
            return var0;
         }
      }
   }
}
