package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ImageButton extends RenderElement {
   private ResourceLocation image;
   private double scale;
   private transient boolean enabled = true;
   private int imgWidth;
   private int imgHeight;
   private boolean hover;
   private boolean selected;

   public ImageButton(ResourceLocation image, int imgWidth, int imgHeight, double scale) {
      this.image = image;
      this.scale = scale;
      this.imgWidth = imgWidth;
      this.imgHeight = imgHeight;
   }

   public int getImgWidth() {
      return this.imgWidth;
   }

   public int getImgHeight() {
      return this.imgHeight;
   }

   public ResourceLocation getImage() {
      return this.image;
   }

   public double getScale() {
      return this.scale;
   }

   public void init() {
      super.init();
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void render() {
      if(this.enabled) {
         if(this.hover) {
            GL11.glColor4f(0.5F, 1.0F, 1.0F, 1.0F);
         } else {
            GL11.glColor4f(0.3F, 1.0F, 1.0F, 1.0F);
         }
      } else {
         GL11.glColor4f(0.9F, 0.9F, 0.9F, 1.0F);
      }

      SlideoutGUI.renderImage(this.image, this.getX(), this.getY(), this.getImgWidth(), this.getImgHeight(), this.scale);
   }

   public void update(int mx, int my) {
      this.hover = false;
      if(mx > this.getX() && mx < this.getX() + this.getWidth() && my > this.getY() && my < this.getY() + this.getHeight()) {
         this.hover = true;
      }

      if(Mouse.isCreated() && !Mouse.isButtonDown(0)) {
         this.selected = false;
      }

   }

   public boolean onClick(int mouseButton) {
      if(this.hover && mouseButton == 0) {
         this.selected = true;
         return true;
      } else {
         return false;
      }
   }

   public int getWidth() {
      return (int)((double)this.getImgWidth() * this.scale);
   }

   public int getHeight() {
      return (int)((double)this.getImgHeight() * this.scale);
   }
}
