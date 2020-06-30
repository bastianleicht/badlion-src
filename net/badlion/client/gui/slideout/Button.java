package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;

public class Button extends RenderElement {
   private String text;

   public Button(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void update(int mouseX, int mouseY) {
   }

   public void render() {
   }
}
