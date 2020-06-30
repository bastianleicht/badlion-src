package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen extends RealmsScreen {
   protected RealmsScreen parent;
   protected String title1;
   private String title2;
   protected String yesButton;
   protected String noButton;
   protected int id;
   private int delayTicker;

   public RealmsConfirmScreen(RealmsScreen parent, String title1, String title2, int id) {
      this.parent = parent;
      this.title1 = title1;
      this.title2 = title2;
      this.id = id;
      this.yesButton = getLocalizedString("gui.yes");
      this.noButton = getLocalizedString("gui.no");
   }

   public RealmsConfirmScreen(RealmsScreen parent, String title1, String title2, String yesButton, String noButton, int id) {
      this.parent = parent;
      this.title1 = title1;
      this.title2 = title2;
      this.yesButton = yesButton;
      this.noButton = noButton;
      this.id = id;
   }

   public void init() {
      this.buttonsAdd(newButton(0, this.width() / 2 - 105, RealmsConstants.row(9), 100, 20, this.yesButton));
      this.buttonsAdd(newButton(1, this.width() / 2 + 5, RealmsConstants.row(9), 100, 20, this.noButton));
   }

   public void buttonClicked(RealmsButton button) {
      this.parent.confirmResult(button.id() == 0, this.id);
   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString(this.title1, this.width() / 2, RealmsConstants.row(3), 16777215);
      this.drawCenteredString(this.title2, this.width() / 2, RealmsConstants.row(5), 16777215);
      super.render(xm, ym, a);
   }

   public void setDelay(int delay) {
      this.delayTicker = delay;

      for(RealmsButton button : this.buttons()) {
         button.active(false);
      }

   }

   public void tick() {
      super.tick();
      if(--this.delayTicker == 0) {
         for(RealmsButton button : this.buttons()) {
            button.active(true);
         }
      }

   }
}
