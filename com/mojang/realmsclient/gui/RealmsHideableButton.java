package com.mojang.realmsclient.gui;

import net.minecraft.realms.RealmsButton;

public class RealmsHideableButton extends RealmsButton {
   boolean visible = true;

   public RealmsHideableButton(int id, int x, int y, int width, int height, String msg) {
      super(id, x, y, width, height, msg);
   }

   public void render(int xm, int ym) {
      if(this.visible) {
         super.render(xm, ym);
      }
   }

   public void clicked(int mx, int my) {
      if(this.visible) {
         super.clicked(mx, my);
      }
   }

   public void released(int mx, int my) {
      if(this.visible) {
         super.released(mx, my);
      }
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean getVisible() {
      return this.visible;
   }
}
