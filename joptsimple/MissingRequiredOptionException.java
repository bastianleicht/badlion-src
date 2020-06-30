package joptsimple;

import java.util.Collection;
import joptsimple.OptionException;

class MissingRequiredOptionException extends OptionException {
   private static final long serialVersionUID = -1L;

   protected MissingRequiredOptionException(Collection options) {
      super(options);
   }

   public String getMessage() {
      return "Missing required option(s) " + this.multipleOptionMessage();
   }
}
