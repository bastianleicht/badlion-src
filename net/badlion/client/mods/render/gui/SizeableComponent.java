package net.badlion.client.mods.render.gui;

import net.badlion.client.mods.render.gui.Component;

public class SizeableComponent extends Component {
   protected int sizeX;
   protected int sizeY;

   public SizeableComponent(String name, int sizeX, int sizeY) {
      super(name, 0, 0);
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public int getSizeX() {
      return this.sizeX;
   }

   public int getSizeY() {
      return this.sizeY;
   }

   public void setSize(int sizeX, int sizeY) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public void setSizeX(int sizeX) {
      this.setSize(sizeX, this.sizeY);
   }

   public void setSizeY(int sizeY) {
      this.setSize(this.sizeX, sizeY);
   }
}
