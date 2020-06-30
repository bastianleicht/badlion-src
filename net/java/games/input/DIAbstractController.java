package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractController;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.DIControllers;
import net.java.games.input.Event;
import net.java.games.input.IDirectInputDevice;
import net.java.games.input.Rumbler;

final class DIAbstractController extends AbstractController {
   private final IDirectInputDevice device;
   private final Controller.Type type;

   protected DIAbstractController(IDirectInputDevice device, Component[] components, Controller[] children, Rumbler[] rumblers, Controller.Type type) {
      super(device.getProductName(), components, children, rumblers);
      this.device = device;
      this.type = type;
   }

   public final void pollDevice() throws IOException {
      this.device.pollAll();
   }

   protected final boolean getNextDeviceEvent(Event event) throws IOException {
      return DIControllers.getNextDeviceEvent(event, this.device);
   }

   protected final void setDeviceEventQueueSize(int size) throws IOException {
      this.device.setBufferSize(size);
   }

   public final Controller.Type getType() {
      return this.type;
   }
}
