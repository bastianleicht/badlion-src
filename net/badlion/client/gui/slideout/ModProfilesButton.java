package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.mainmenu.GuiModProfiles;
import net.badlion.client.gui.mainmenu.GuiTopBar;
import net.badlion.client.gui.slideout.RenderElement;

public class ModProfilesButton extends RenderElement {
   private final transient int buttonHeight = 25;
   private transient GuiTopBar guiTopBar = new GuiTopBar();
   private transient GuiModProfiles guiModProfiles = new GuiModProfiles();

   public void init() {
      super.init();
      this.guiModProfiles.calculateCenteredBoxOffsets();
   }

   public void render() {
      GuiTopBar guitopbar = this.guiTopBar;
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      int k = 25 + this.getX();
      this.getClass();
      guitopbar.renderModProfileBox(i, j, k, 50, 25, this.guiModProfiles.isBoxOpen());
      if(this.guiModProfiles.isBoxOpen()) {
         this.guiModProfiles.render(Wrapper.getInstance().getMouseX(), Wrapper.getInstance().getMouseY());
      }

   }

   public void update(int mX, int mY) {
   }

   public void keyTyped(char character, int keyCode) {
      if(this.guiModProfiles.isBoxOpen()) {
         this.guiModProfiles.keyTyped(character, keyCode);
      }

   }

   public boolean onClick(int mouseButton) {
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      GuiTopBar guitopbar = this.guiTopBar;
      this.getClass();
      if(guitopbar.isMouseOverModProfilesBox(i, j, 50, 50, 25)) {
         this.guiModProfiles.setBoxOpen(!this.guiModProfiles.isBoxOpen());
         return true;
      } else {
         return this.guiModProfiles.isBoxOpen()?this.guiModProfiles.onClick(i, j, mouseButton):false;
      }
   }

   public int getWidth() {
      return Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() - 20;
   }

   public int getHeight() {
      this.getClass();
      return 25;
   }

   public boolean isBoxOpen() {
      return this.guiModProfiles.isBoxOpen();
   }

   public void setBoxOpen(boolean boxOpen) {
      this.guiModProfiles.setBoxOpen(boxOpen);
   }

   public int getZLevel() {
      return 1000;
   }
}
