package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Image extends RenderElement {
   private ResourceLocation location;
   private int imgWidth;
   private int imgHeight;
   private double scale;
   private double r = 1.0D;
   private double g = 1.0D;
   private double b = 1.0D;

   public Image(String imageLocation, int imgWidth, int imgHeight, double scale) {
      this.location = new ResourceLocation(imageLocation);
      this.imgWidth = imgWidth;
      this.imgHeight = imgHeight;
      this.scale = scale;
   }

   public Image(ResourceLocation imageLocation, int imgWidth, int imgHeight, double scale) {
      this.location = imageLocation;
      this.imgWidth = imgWidth;
      this.imgHeight = imgHeight;
      this.scale = scale;
   }

   public void setColorOffset(double r, double g, double b) {
      this.r = r;
      this.g = g;
      this.b = b;
   }

   public int getWidth() {
      return this.imgWidth;
   }

   public int getHeight() {
      return this.imgHeight;
   }

   public double getScale() {
      return this.scale;
   }

   public ResourceLocation getImage() {
      return this.location;
   }

   public void render() {
      GL11.glColor4d(this.r, this.g, this.b, 1.0D);
      SlideoutGUI.renderImage(this.location, this.getX(), this.getY(), this.getWidth(), this.getHeight(), this.scale);
   }

   public void update(int mouseX, int mouseY) {
   }
}
