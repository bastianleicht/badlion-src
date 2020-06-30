package net.java.games.input;

import net.java.games.input.Component;
import net.java.games.input.Event;
import net.java.games.input.WinTabComponent;
import net.java.games.input.WinTabContext;
import net.java.games.input.WinTabPacket;

public class WinTabButtonComponent extends WinTabComponent {
   private int index;

   protected WinTabButtonComponent(WinTabContext context, int parentDevice, String name, Component.Identifier id, int index) {
      super(context, parentDevice, name, id);
      this.index = index;
   }

   public Event processPacket(WinTabPacket packet) {
      Event newEvent = null;
      float newValue = (packet.PK_BUTTONS & (int)Math.pow(2.0D, (double)this.index)) > 0?1.0F:0.0F;
      if(newValue != this.getPollData()) {
         this.lastKnownValue = newValue;
         newEvent = new Event();
         newEvent.set(this, newValue, packet.PK_TIME * 1000L);
         return newEvent;
      } else {
         return newEvent;
      }
   }
}
