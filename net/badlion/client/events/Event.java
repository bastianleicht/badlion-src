package net.badlion.client.events;

import net.badlion.client.events.EventType;

public class Event {
   private final EventType eventType;
   private boolean cancelled;

   public Event(EventType eventType) {
      this.eventType = eventType;
   }

   public EventType getEventType() {
      return this.eventType;
   }

   public boolean isCancelled() {
      return this.cancelled;
   }

   public void setCancelled(boolean b) {
      this.cancelled = true;
   }
}
