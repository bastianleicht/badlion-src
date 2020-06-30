package net.java.games.input;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Rumbler;

public abstract class AbstractController implements Controller {
   static final int EVENT_QUEUE_DEPTH = 32;
   private static final Event event = new Event();
   private final String name;
   private final Component[] components;
   private final Controller[] children;
   private final Rumbler[] rumblers;
   private final Map id_to_components = new HashMap();
   private EventQueue event_queue = new EventQueue(32);

   protected AbstractController(String name, Component[] components, Controller[] children, Rumbler[] rumblers) {
      this.name = name;
      this.components = components;
      this.children = children;
      this.rumblers = rumblers;

      for(int i = components.length - 1; i >= 0; --i) {
         this.id_to_components.put(components[i].getIdentifier(), components[i]);
      }

   }

   public final Controller[] getControllers() {
      return this.children;
   }

   public final Component[] getComponents() {
      return this.components;
   }

   public final Component getComponent(Component.Identifier id) {
      return (Component)this.id_to_components.get(id);
   }

   public final Rumbler[] getRumblers() {
      return this.rumblers;
   }

   public Controller.PortType getPortType() {
      return Controller.PortType.UNKNOWN;
   }

   public int getPortNumber() {
      return 0;
   }

   public final String getName() {
      return this.name;
   }

   public String toString() {
      return this.name;
   }

   public Controller.Type getType() {
      return Controller.Type.UNKNOWN;
   }

   public final void setEventQueueSize(int size) {
      try {
         this.setDeviceEventQueueSize(size);
         this.event_queue = new EventQueue(size);
      } catch (IOException var3) {
         ControllerEnvironment.logln("Failed to create new event queue of size " + size + ": " + var3);
      }

   }

   protected void setDeviceEventQueueSize(int size) throws IOException {
   }

   public final EventQueue getEventQueue() {
      return this.event_queue;
   }

   protected abstract boolean getNextDeviceEvent(Event var1) throws IOException;

   protected void pollDevice() throws IOException {
   }

   public synchronized boolean poll() {
      Component[] components = this.getComponents();

      try {
         this.pollDevice();

         for(int i = 0; i < components.length; ++i) {
            AbstractComponent component = (AbstractComponent)components[i];
            if(component.isRelative()) {
               component.setPollData(0.0F);
            } else {
               component.resetHasPolled();
            }
         }

         while(true) {
            while(true) {
               if(!this.getNextDeviceEvent(event)) {
                  return true;
               }

               AbstractComponent component = (AbstractComponent)event.getComponent();
               float value = event.getValue();
               if(component.isRelative()) {
                  if(value != 0.0F) {
                     component.setPollData(component.getPollData() + value);
                     break;
                  }
               } else if(value != component.getEventValue()) {
                  component.setEventValue(value);
                  break;
               }
            }

            if(!this.event_queue.isFull()) {
               this.event_queue.add(event);
            }
         }
      } catch (IOException var4) {
         ControllerEnvironment.logln("Failed to poll device: " + var4.getMessage());
         return false;
      }
   }
}
