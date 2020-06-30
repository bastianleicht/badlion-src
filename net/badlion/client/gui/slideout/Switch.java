package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;

public class Switch extends RenderElement {
   private boolean enabled;
   private double percentMoved;

   public Switch(boolean defaultEnabled) {
      this.enabled = defaultEnabled;
   }

   public void render() {
   }

   public void update(int mouseX, int mouseY) {
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }
}
