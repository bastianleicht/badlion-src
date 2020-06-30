package net.java.games.input;

import net.java.games.input.ControllerEvent;

public interface ControllerListener {
   void controllerRemoved(ControllerEvent var1);

   void controllerAdded(ControllerEvent var1);
}
