package joptsimple;

import java.util.Collection;
import joptsimple.OptionException;

class OptionArgumentConversionException extends OptionException {
   private static final long serialVersionUID = -1L;
   private final String argument;

   OptionArgumentConversionException(Collection options, String argument, Throwable cause) {
      super(options, cause);
      this.argument = argument;
   }

   public String getMessage() {
      return "Cannot parse argument \'" + this.argument + "\' of option " + this.multipleOptionMessage();
   }
}
