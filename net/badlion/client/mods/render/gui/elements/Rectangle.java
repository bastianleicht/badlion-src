package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.SizeableComponent;
import net.minecraft.client.gui.GuiIngame;

public class Rectangle extends SizeableComponent {
   private int color;

   public Rectangle(String name, int sizeX, int sizeY, int color) {
      super(name, sizeX, sizeY);
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public void render(GuiIngame gameRenderer, int x0, int y0) {
      if(this.isVisible()) {
         GuiIngame.drawRect(x0, y0, x0 + this.sizeX, y0 + this.sizeY, this.color);
      }

   }
}
