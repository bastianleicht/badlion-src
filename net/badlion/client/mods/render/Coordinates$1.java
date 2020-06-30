package net.badlion.client.mods.render;

import net.badlion.client.mods.render.ModOrientation;
import net.badlion.client.mods.render.ShowDirection;

// $FF: synthetic class
class Coordinates$1 {
   // $FF: synthetic field
   static final int[] $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction;
   // $FF: synthetic field
   static final int[] $SwitchMap$net$badlion$client$mods$render$ModOrientation = new int[ModOrientation.values().length];

   static {
      try {
         $SwitchMap$net$badlion$client$mods$render$ModOrientation[ModOrientation.VERTICAL.ordinal()] = 1;
      } catch (NoSuchFieldError var10) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ModOrientation[ModOrientation.HORIZONTAL.ordinal()] = 2;
      } catch (NoSuchFieldError var9) {
         ;
      }

      $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction = new int[ShowDirection.Direction.values().length];

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.N.ordinal()] = 1;
      } catch (NoSuchFieldError var8) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.NE.ordinal()] = 2;
      } catch (NoSuchFieldError var7) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.E.ordinal()] = 3;
      } catch (NoSuchFieldError var6) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.SE.ordinal()] = 4;
      } catch (NoSuchFieldError var5) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.S.ordinal()] = 5;
      } catch (NoSuchFieldError var4) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.SW.ordinal()] = 6;
      } catch (NoSuchFieldError var3) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.W.ordinal()] = 7;
      } catch (NoSuchFieldError var2) {
         ;
      }

      try {
         $SwitchMap$net$badlion$client$mods$render$ShowDirection$Direction[ShowDirection.Direction.NW.ordinal()] = 8;
      } catch (NoSuchFieldError var1) {
         ;
      }

   }
}
