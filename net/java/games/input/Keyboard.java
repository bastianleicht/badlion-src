package net.java.games.input;

import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Rumbler;

public abstract class Keyboard extends AbstractController {
   protected Keyboard(String name, Component[] keys, Controller[] children, Rumbler[] rumblers) {
      super(name, keys, children, rumblers);
   }

   public Controller.Type getType() {
      return Controller.Type.KEYBOARD;
   }

   public final boolean isKeyDown(Component.Identifier.Key key_id) {
      Component key = this.getComponent(key_id);
      return key == null?false:key.getPollData() != 0.0F;
   }
}
