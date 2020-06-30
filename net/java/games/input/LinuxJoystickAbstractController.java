package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.LinuxJoystickDevice;
import net.java.games.input.Rumbler;

final class LinuxJoystickAbstractController extends AbstractController {
   private final LinuxJoystickDevice device;

   protected LinuxJoystickAbstractController(LinuxJoystickDevice device, Component[] components, Controller[] children, Rumbler[] rumblers) {
      super(device.getName(), components, children, rumblers);
      this.device = device;
   }

   protected final void setDeviceEventQueueSize(int size) throws IOException {
      this.device.setBufferSize(size);
   }

   public final void pollDevice() throws IOException {
      this.device.poll();
   }

   protected final boolean getNextDeviceEvent(Event event) throws IOException {
      return this.device.getNextEvent(event);
   }

   public Controller.Type getType() {
      return Controller.Type.STICK;
   }
}
