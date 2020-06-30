package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.GUIKeyPress;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiScreenSlideout extends GuiScreen {
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   protected void actionPerformed(GuiButton button) {
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      Wrapper.getInstance().getActiveModProfile().passEvent(new GUIClickMouse(mouseButton));
   }

   protected void keyTyped(char typedChar, int keyCode) {
      if(Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().isOpen()) {
         Wrapper.getInstance().getActiveModProfile().passEvent(new GUIKeyPress(typedChar, keyCode));
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
