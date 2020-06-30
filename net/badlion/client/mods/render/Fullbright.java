package net.badlion.client.mods.render;

import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.mods.Mod;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.Gui;

public class Fullbright extends Mod {
   private boolean needsUpdate = false;
   private int toggleTime = 0;

   public Fullbright() {
      super("Fullbright", false);
      this.iconDimension = new ImageDimension(71, 81);
   }

   public void init() {
      this.offsetX = 1;
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.setFontOffset(0.089D);
      super.init();
   }

   public void toggle() {
      this.needsUpdate = true;
      this.toggleTime = 10;
      super.toggle();
   }

   public void onEvent(Event event) {
      if(event instanceof RenderGame && this.toggleTime > 0) {
         int i = -14144717;
         if(this.toggleTime == 3) {
            i = -1104663757;
         } else if(this.toggleTime == 2) {
            i = -2144851149;
         } else if(this.toggleTime == 1) {
            i = 1076374323;
         }

         Gui.drawRect(0, 0, this.gameInstance.displayWidth, this.gameInstance.displayHeight, i);
      }

      if(event instanceof MotionUpdate) {
         if(this.toggleTime > 0) {
            --this.toggleTime;
         }

         if(this.needsUpdate) {
            this.needsUpdate = false;
            this.gameInstance.renderGlobal.loadRenderers();
         }
      }

      super.onEvent(event);
   }
}
