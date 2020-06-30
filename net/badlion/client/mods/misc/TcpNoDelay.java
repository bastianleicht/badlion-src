package net.badlion.client.mods.misc;

import net.badlion.client.mods.Mod;
import net.badlion.client.util.ImageDimension;

public class TcpNoDelay extends Mod {
   public TcpNoDelay() {
      super("TcpNoDelay", true);
   }

   public void init() {
      this.setFontOffset(0.042D);
      this.iconDimension = new ImageDimension(92, 90);
      this.offsetX = -1;
      super.init();
   }
}
