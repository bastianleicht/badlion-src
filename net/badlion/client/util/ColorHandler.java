package net.badlion.client.util;

import org.lwjgl.util.Color;

public class ColorHandler {
   private Color color = new Color(255, 255, 255, 255);
   private int rainbowSpeed = 1;

   public Color getColor() {
      return this.color;
   }

   public int getRainbowSpeed() {
      return this.rainbowSpeed;
   }

   public void tickRainbow() {
      if(this.color.getRed() == 255) {
         if(this.color.getBlue() == 0) {
            if(this.color.getGreen() == 255) {
               this.color.setRed(this.color.getRed() - 1);
               return;
            }

            this.color.setGreen(this.color.getGreen() + 1);
            return;
         }

         if(this.color.getGreen() == 0) {
            if(this.color.getBlue() == 0) {
               this.color.setGreen(this.color.getGreen() + 1);
               return;
            }

            this.color.setBlue(this.color.getBlue() - 1);
            return;
         }
      }

      if(this.color.getGreen() == 255) {
         if(this.color.getRed() == 0) {
            if(this.color.getBlue() == 255) {
               this.color.setGreen(this.color.getGreen() - 1);
               return;
            }

            this.color.setBlue(this.color.getBlue() + 1);
            return;
         }

         if(this.color.getBlue() == 0) {
            if(this.color.getRed() == 0) {
               this.color.setBlue(this.color.getBlue() + 1);
               return;
            }

            this.color.setRed(this.color.getRed() - 1);
            return;
         }
      }

      if(this.color.getBlue() == 255) {
         if(this.color.getRed() == 0) {
            if(this.color.getGreen() == 0) {
               this.color.setRed(this.color.getRed() + 1);
               return;
            }

            this.color.setGreen(this.color.getGreen() - 1);
            return;
         }

         if(this.color.getGreen() == 0) {
            this.color.setRed(this.color.getRed() + 1);
            return;
         }
      }

      this.color.setRed((int)255);
      this.color.setGreen((int)0);
      this.color.setBlue((int)0);
   }
}
