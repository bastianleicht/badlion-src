package net.java.games.input;

import java.io.IOException;
import net.java.games.input.Event;
import net.java.games.input.LinuxAbsInfo;
import net.java.games.input.LinuxAxisDescriptor;
import net.java.games.input.LinuxComponent;
import net.java.games.input.LinuxEvent;
import net.java.games.input.LinuxEventComponent;
import net.java.games.input.LinuxEventDevice;

final class LinuxControllers {
   private static final LinuxEvent linux_event = new LinuxEvent();
   private static final LinuxAbsInfo abs_info = new LinuxAbsInfo();

   public static final synchronized boolean getNextDeviceEvent(Event event, LinuxEventDevice device) throws IOException {
      while(true) {
         if(device.getNextEvent(linux_event)) {
            LinuxAxisDescriptor descriptor = linux_event.getDescriptor();
            LinuxComponent component = device.mapDescriptor(descriptor);
            if(component == null) {
               continue;
            }

            float value = component.convertValue((float)linux_event.getValue(), descriptor);
            event.set(component, value, linux_event.getNanos());
            return true;
         }

         return false;
      }
   }

   public static final synchronized float poll(LinuxEventComponent event_component) throws IOException {
      int native_type = event_component.getDescriptor().getType();
      switch(native_type) {
      case 1:
         int native_code = event_component.getDescriptor().getCode();
         float state = event_component.getDevice().isKeySet(native_code)?1.0F:0.0F;
         return state;
      case 3:
         event_component.getAbsInfo(abs_info);
         return (float)abs_info.getValue();
      default:
         throw new RuntimeException("Unkown native_type: " + native_type);
      }
   }
}
