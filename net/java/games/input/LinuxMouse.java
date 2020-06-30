package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.LinuxControllers;
import net.java.games.input.LinuxEventDevice;
import net.java.games.input.Mouse;
import net.java.games.input.Rumbler;

final class LinuxMouse extends Mouse {
   private final Controller.PortType port;
   private final LinuxEventDevice device;

   protected LinuxMouse(LinuxEventDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) throws IOException {
      super(device.getName(), components, children, rumblers);
      this.device = device;
      this.port = device.getPortType();
   }

   public final Controller.PortType getPortType() {
      return this.port;
   }

   public final void pollDevice() throws IOException {
      this.device.pollKeyStates();
   }

   protected final boolean getNextDeviceEvent(Event event) throws IOException {
      return LinuxControllers.getNextDeviceEvent(event, this.device);
   }
}
