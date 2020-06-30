package net.badlion.client.util;

import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class ColorUtil {
   public static int getIntFromColor(Color color) {
      try {
         return (int)Long.parseLong(String.format("%02x%02x%02x%02x", new Object[]{Integer.valueOf(color.getAlpha()), Integer.valueOf(color.getRed()), Integer.valueOf(color.getGreen()), Integer.valueOf(color.getBlue())}), 16);
      } catch (NullPointerException var2) {
         return -1;
      }
   }

   public static int getIntFromColor(int alpha, int red, int green, int blue) {
      alpha = alpha << 24 & -16777216;
      red = red << 16 & 16711680;
      green = green << 8 & '\uff00';
      blue = blue & 255;
      return alpha | red | green | blue;
   }

   public static java.awt.Color getColorFromString(String string) {
      long i = Long.valueOf(string, 16).longValue();
      float f = (float)(i >> 16 & 255L) / 255.0F;
      float f1 = (float)(i >> 8 & 255L) / 255.0F;
      float f2 = (float)(i & 255L) / 255.0F;
      float f3 = (float)(i >> 24 & 255L) / 255.0F;
      return new java.awt.Color(f, f1, f2, f3);
   }

   public static void bindColor(Color color) {
      GlStateManager.color((float)color.getRed() / 255.0F, (float)color.getGreen() / 255.0F, (float)color.getBlue() / 255.0F, (float)color.getAlpha() / 255.0F);
   }

   public static void bindHexColorRGBA(int color) {
      float f = (float)(color >> 16 & 255) / 255.0F;
      float f1 = (float)(color >> 8 & 255) / 255.0F;
      float f2 = (float)(color & 255) / 255.0F;
      float f3 = (float)(color >> 24 & 255) / 255.0F;
      f3 = (float)((double)f3 * 0.9D);
      GL11.glColor4f(f, f1, f2, f3);
   }

   public static int getRed(int hexColor) {
      return hexColor >> 16 & 255;
   }

   public static int getGreen(int hexColor) {
      return hexColor >> 8 & 255;
   }

   public static int getBlue(int hexColor) {
      return hexColor & 255;
   }

   public static int getAlpha(int hexColor) {
      return hexColor >> 24 & 255;
   }

   public static java.awt.Color hsvToRgb(float hue, float saturation, float value) {
      int i = (int)(hue * 6.0F);
      float f = hue * 6.0F - (float)i;
      float f1 = value * (1.0F - saturation);
      float f2 = value * (1.0F - f * saturation);
      float f3 = value * (1.0F - (1.0F - f) * saturation);
      switch(i) {
      case 0:
         return new java.awt.Color(value, f3, f1);
      case 1:
         return new java.awt.Color(f2, value, f1);
      case 2:
         return new java.awt.Color(f1, value, f3);
      case 3:
         return new java.awt.Color(f1, f2, value);
      case 4:
         return new java.awt.Color(f3, f1, value);
      case 5:
         return new java.awt.Color(value, f1, f2);
      default:
         throw new RuntimeException("Something went wrong when converting from HSV to RGB. Input was " + hue + ", " + saturation + ", " + value);
      }
   }
}
