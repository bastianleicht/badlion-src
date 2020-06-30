package net.badlion.client.events.event;

import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.minecraft.client.gui.GuiIngame;

public class RenderGame extends Event {
   private GuiIngame gameRenderer;

   public RenderGame(GuiIngame gameRenderer) {
      super(EventType.RENDER_GAME);
      this.gameRenderer = gameRenderer;
   }

   public GuiIngame getGameRenderer() {
      return this.gameRenderer;
   }
}
