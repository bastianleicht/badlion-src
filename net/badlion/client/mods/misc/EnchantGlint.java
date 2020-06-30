package net.badlion.client.mods.misc;

import net.badlion.client.events.Event;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.mods.Mod;
import net.badlion.client.util.ImageDimension;

public class EnchantGlint extends Mod {
   private boolean legacy;

   public EnchantGlint() {
      super("EnchantGlint", false);
   }

   public void init() {
      this.setDisplayName("Enchant Glint");
      this.setFontOffset(0.023D);
      this.iconDimension = new ImageDimension(104, 91);
      this.offsetX = -1;
      super.init();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.legacy = false;
      }

      super.onEvent(e);
   }

   public void setLegacy(boolean legacy) {
      this.legacy = legacy;
   }

   public boolean isLegacy() {
      return this.legacy;
   }
}
