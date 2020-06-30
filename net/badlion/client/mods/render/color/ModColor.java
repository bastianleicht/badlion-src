package net.badlion.client.mods.render.color;

import net.badlion.client.Wrapper;
import net.badlion.client.util.ColorUtil;
import org.lwjgl.util.Color;

public class ModColor {
   private boolean enabled = true;
   private Color color;
   private int breathingSpeed = 1;
   private Color breathingColorStart;
   private Color breathingColorEnd;
   private transient int colorInt = -1;
   private transient int breathingTick = 0;
   private transient boolean breathingIncrease = true;
   private ModColor.DynamicColorMode mode = ModColor.DynamicColorMode.STATIC;
   private boolean up = true;
   int ticks = 0;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode;

   public ModColor.DynamicColorMode getMode() {
      return this.mode;
   }

   public Color getColor() {
      if(!this.enabled) {
         Color color = new Color(this.color);
         color.setAlpha((int)5);
         return color;
      } else {
         return this.color;
      }
   }

   public ModColor(int color) {
      this.color = new Color(ColorUtil.getRed(color), ColorUtil.getGreen(color), ColorUtil.getBlue(color), ColorUtil.getAlpha(color));
      this.colorInt = color;
   }

   public void init() {
      this.colorInt = ColorUtil.getIntFromColor(this.color);
   }

   public ModColor(Color start, Color end) {
      this.mode = ModColor.DynamicColorMode.BREATHING;
      this.breathingColorStart = new Color(start);
      this.breathingColorEnd = new Color(end);
      this.color = new Color(start);
   }

   public void tickColor() {
      switch($SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode()[this.mode.ordinal()]) {
      case 1:
         this.tickBreathingColor();
         break;
      case 2:
         this.tickRainbowColor();
      }

   }

   /** @deprecated */
   @Deprecated
   public int getColorInt() {
      return !this.enabled?ColorUtil.getIntFromColor(this.getColor()):(this.colorInt != -1?this.colorInt:(this.colorInt = ColorUtil.getIntFromColor(this.color)));
   }

   public void setColor(int color) {
      this.color = new Color(ColorUtil.getRed(color), ColorUtil.getGreen(color), ColorUtil.getBlue(color), ColorUtil.getAlpha(color));
      this.colorInt = color;
   }

   public void setMode(ModColor.DynamicColorMode mode) {
      this.mode = mode;
   }

   private void tickBreathingColor() {
      int i = 2;
      int j = this.color.getAlpha() + (this.up?i:-i);
      if(j > 255) {
         this.up = false;
         j -= i;
      } else if(j < 28) {
         j += i;
         this.up = true;
      }

      this.color.setAlpha(j);
      this.colorInt = ColorUtil.getIntFromColor(this.color);
   }

   private void tickRainbowColor() {
      int i = this.color.getAlpha();
      this.color.setColor(Wrapper.getInstance().getColorHandler().getColor());
      this.color.setAlpha(i);
      this.colorInt = ColorUtil.getIntFromColor(this.color);
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode;
      if($SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode != null) {
         return var10000;
      } else {
         int[] var0 = new int[ModColor.DynamicColorMode.values().length];

         try {
            var0[ModColor.DynamicColorMode.BREATHING.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[ModColor.DynamicColorMode.RAINBOW.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ModColor.DynamicColorMode.STATIC.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$color$ModColor$DynamicColorMode = var0;
         return var0;
      }
   }

   public static enum DynamicColorMode {
      BREATHING,
      RAINBOW,
      STATIC;
   }
}
