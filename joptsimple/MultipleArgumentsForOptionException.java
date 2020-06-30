package joptsimple;

import java.util.Collection;
import joptsimple.OptionException;

class MultipleArgumentsForOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   MultipleArgumentsForOptionException(Collection options) {
      super(options);
   }

   public String getMessage() {
      return "Found multiple arguments for option " + this.multipleOptionMessage() + ", but you asked for only one";
   }
}
