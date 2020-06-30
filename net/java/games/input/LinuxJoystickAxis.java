package net.java.games.input;

import java.io.IOException;
import net.java.games.input.AbstractComponent;
import net.java.games.input.Component;

class LinuxJoystickAxis extends AbstractComponent {
   private float value;
   private boolean analog;

   public LinuxJoystickAxis(Component.Identifier.Axis axis_id) {
      this(axis_id, true);
   }

   public LinuxJoystickAxis(Component.Identifier.Axis axis_id, boolean analog) {
      super(axis_id.getName(), axis_id);
      this.analog = analog;
   }

   public final boolean isRelative() {
      return false;
   }

   public final boolean isAnalog() {
      return this.analog;
   }

   final void setValue(float value) {
      this.value = value;
      this.resetHasPolled();
   }

   protected final float poll() throws IOException {
      return this.value;
   }
}
