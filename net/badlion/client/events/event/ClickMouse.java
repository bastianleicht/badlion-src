package net.badlion.client.events.event;

import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;

public class ClickMouse extends Event {
   private final int clickType;

   public ClickMouse(int clickType) {
      super(EventType.CLICK_MOUSE);
      this.clickType = clickType;
   }

   public int getClickType() {
      return this.clickType;
   }
}
