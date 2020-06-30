package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;

public class Panel extends RenderElement {
   private int posX;
   private int posY;
   private int sizeX;
   private int sizeY;

   public Panel(int posX, int posY, int sizeX, int sizeY) {
      this.posX = posX;
      this.posY = posY;
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public void update(int mouseX, int mouseY) {
   }

   public void render() {
   }

   public void setPosX(int posX) {
      this.posX = posX;
   }

   public void setPosY(int posY) {
      this.posY = posY;
   }

   public int getPosX() {
      return this.posX;
   }

   public int getPosY() {
      return this.posY;
   }

   public void setSizeX(int sizeX) {
      this.sizeX = sizeX;
   }

   public void setSizeY(int sizeY) {
      this.sizeY = sizeY;
   }

   public int getSizeX() {
      return this.sizeX;
   }

   public int getSizeY() {
      return this.sizeY;
   }
}
