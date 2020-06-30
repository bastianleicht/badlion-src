package net.badlion.client.events.event;

import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;

public class SneakEvent extends Event {
   private boolean sneaking;

   public SneakEvent(boolean sneaking) {
      super(EventType.SNEAK_EVENT);
      this.sneaking = sneaking;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }

   public void setSneaking(boolean sneaking) {
      this.sneaking = sneaking;
   }
}
