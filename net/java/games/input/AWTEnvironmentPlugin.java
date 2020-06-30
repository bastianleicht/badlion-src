package net.java.games.input;

import net.java.games.input.AWTKeyboard;
import net.java.games.input.AWTMouse;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.util.plugins.Plugin;

public class AWTEnvironmentPlugin extends ControllerEnvironment implements Plugin {
   private final Controller[] controllers = new Controller[]{new AWTKeyboard(), new AWTMouse()};

   public Controller[] getControllers() {
      return this.controllers;
   }

   public boolean isSupported() {
      return true;
   }
}
