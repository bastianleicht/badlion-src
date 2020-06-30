package net.badlion.client.events.event;

import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;

public class GUIKeyPress extends Event {
   private int keyId;
   private char character;

   public GUIKeyPress(char character, int keyId) {
      super(EventType.GUI_KEY_PRESS);
      this.keyId = keyId;
      this.character = character;
   }

   public char getCharacter() {
      return this.character;
   }

   public int getKeyID() {
      return this.keyId;
   }
}
