package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.RenderElement;

public class Padding extends RenderElement {
   private int width;
   private int height;

   public Padding(int width, int height) {
      this.width = width;
      this.height = height;
      if(this.width == 0) {
         this.width = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth();
      }

   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public void render() {
   }

   public void update(int mouseX, int mouseY) {
   }
}
