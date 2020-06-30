package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;
import net.java.games.input.DIControllers;
import net.java.games.input.DIDeviceObject;

final class DIComponent extends AbstractComponent {
   private final DIDeviceObject object;

   public DIComponent(Component.Identifier identifier, DIDeviceObject object) {
      super(object.getName(), identifier);
      this.object = object;
   }

   public final boolean isRelative() {
      return this.object.isRelative();
   }

   public final boolean isAnalog() {
      return this.object.isAnalog();
   }

   public final float getDeadZone() {
      return this.object.getDeadzone();
   }

   public final DIDeviceObject getDeviceObject() {
      return this.object;
   }

   protected final float poll() throws IOException {
      return DIControllers.poll(this, this.object);
   }
}
