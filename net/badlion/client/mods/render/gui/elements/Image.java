package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.SizeableComponent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Image extends SizeableComponent {
   private ResourceLocation location;

   public Image(String name, String location, int sizeX, int sizeY) {
      super(name, sizeX, sizeY);
      if(location != null) {
         this.location = new ResourceLocation(location);
      }

   }

   public Image(String name, ResourceLocation location, int sizeX, int sizeY) {
      super(name, sizeX, sizeY);
      this.location = location;
   }

   public void setLocation(ResourceLocation location) {
      this.location = location;
   }

   public void setLocation(String location) {
      this.location = new ResourceLocation(location);
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public void render(GuiIngame gameRenderer, int x0, int y0) {
      if(this.isVisible()) {
         Minecraft minecraft = Minecraft.getMinecraft();
         if(this.location != null) {
            minecraft.getTextureManager().bindTexture(this.location);
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            Gui.drawScaledCustomSizeModalRect(x0, y0, (float)this.sizeX, (float)this.sizeY, this.sizeX, this.sizeY, this.sizeX, this.sizeY, (float)this.sizeX, (float)this.sizeY);
         }
      }

   }
}
