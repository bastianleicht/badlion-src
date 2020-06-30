package net.badlion.client.gui.slideout.elements;

import net.badlion.client.gui.slideout.RenderElement;

public class ToggleButton extends RenderElement {
   protected String text;

   public ToggleButton(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }
}
